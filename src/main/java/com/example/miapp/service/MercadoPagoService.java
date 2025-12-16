package com.example.miapp.service;

import com.mercadopago.MercadoPagoConfig;
import com.mercadopago.client.preference.PreferenceClient;
import com.mercadopago.client.preference.PreferenceBackUrlsRequest;
import com.mercadopago.client.preference.PreferenceItemRequest;
import com.mercadopago.client.preference.PreferenceRequest;
import com.mercadopago.exceptions.MPApiException;
import com.mercadopago.exceptions.MPException;
import com.mercadopago.resources.preference.Preference;
import com.example.miapp.model.Producto;
import com.example.miapp.model.CarritoItem;
import com.example.miapp.repository.ProductoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import com.example.miapp.model.Orden;
import com.example.miapp.repository.OrdenRepository;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class MercadoPagoService {

    @Value("${mercadopago.access.token}")
    private String accessToken;

    @Value("${mercadopago.back.url.success}")
    private String successUrl;

    @Value("${mercadopago.back.url.pending}")
    private String pendingUrl;

    @Value("${mercadopago.back.url.failure}")
    private String failureUrl;

    @Autowired
    private ProductoRepository productoRepository; // Asumo que tienes un repositorio para buscar productos

    @Autowired
    private OrdenRepository ordenRepository;

    public void processPaymentNotification(String paymentId) throws MPException, MPApiException {
        // 1. Configurar credenciales 
        MercadoPagoConfig.setAccessToken(accessToken);
        
        // 2. Obtener el cliente de Pago y buscar el ID
        PaymentClient client = new PaymentClient();
        Payment payment = client.get(Long.parseLong(paymentId));

        // 3. Obtener el estado y la referencia de la orden
        String mpStatus = payment.getStatus();
        String orderId = payment.getExternalReference(); // Esto es el ID de tu orden de MongoDB
        
        // 4. Buscar la Orden en tu BD
        Orden orden = ordenRepository.findById(orderId).orElse(null);

        if (orden == null) {
            System.err.println("Error: Orden de ComiCommerce no encontrada con ID: " + orderId);
            return; 
        }

        // 5. Actualizar la Orden
        orden.setMercadoPagoPaymentId(paymentId);
        orden.setMercadoPagoStatus(mpStatus);

        if ("approved".equals(mpStatus)) {
            orden.setEstadoOrden("PAGADA");
            System.out.println("Orden PAGADA. ID: " + orderId);
            // LÓGICA ADICIONAL: Descontar stock (si lo implementas)
            // LÓGICA ADICIONAL: Enviar correo de confirmación
        } else if ("pending".equals(mpStatus)) {
            orden.setEstadoOrden("PENDIENTE_PAGO");
            System.out.println("Orden PENDIENTE. ID: " + orderId);
        } else if ("rejected".equals(mpStatus)) {
            orden.setEstadoOrden("PAGO_RECHAZADO");
            System.out.println("Orden RECHAZADA. ID: " + orderId);
        } else {
             orden.setEstadoOrden("MP_STATUS_" + mpStatus.toUpperCase());
        }

        ordenRepository.save(orden); // Guardar la orden actualizada
    }

    public String createPreference(List<CarritoItem> carritoItems, String userId) throws MPException, MPApiException {
        // 1. Configurar credenciales al inicio de la operación
        MercadoPagoConfig.setAccessToken(accessToken);

        // 2. Mapear CarritoItem a PreferenceItemRequest
        List<PreferenceItemRequest> items = new ArrayList<>();
        
        for (CarritoItem item : carritoItems) {
            // Buscamos el producto en la base de datos para obtener el nombre y precio
            Producto producto = productoRepository.findById(item.getProductoId()).orElse(null);

            if (producto == null) {
                // Manejar el caso donde el producto no existe (puedes lanzar una excepción)
                throw new MPException("Product not found: " + item.getProductoId());
            }
            
            // NOTA: El campo 'precio' es String en tu modelo ProductMongo. 
            // Debe ser convertido a BigDecimal para el SDK de MP.
            BigDecimal unitPrice = new BigDecimal(producto.getPrecio());

            PreferenceItemRequest itemRequest = PreferenceItemRequest.builder()
                .title(producto.getNombre())
                .quantity(item.getCantidad())
                .unitPrice(unitPrice)
                // Asume la moneda (ej: CLP para Chile, ARS para Argentina)
                .currencyId("CLP") 
                .build();
            
            items.add(itemRequest);
        }

        // 3. Configurar URLs de retorno
        PreferenceBackUrlsRequest backUrls = PreferenceBackUrlsRequest.builder()
            .success(successUrl)
            .pending(pendingUrl)
            .failure(failureUrl)
            .build();
        
        // 4. Construir la solicitud de preferencia
        PreferenceRequest preferenceRequest = PreferenceRequest.builder()
            .items(itemsRequest) // Usamos los items calculados
            .backUrls(backUrls)
            .autoReturn("approved")
            // CRUCIAL: Usar el ID de la orden de tu BD como referencia externa
            .externalReference(orderId) 
            .build();
        
        // 5. Crear la preferencia
        PreferenceClient client = new PreferenceClient();
        Preference preference = client.create(preferenceRequest);

        ordenGuardada.setMercadoPagoPreferenceId(preference.getId());
        ordenRepository.save(ordenGuardada);    
        
        // Retornar el URL de redirección (init_point)
        return preference.getInitPoint();

        double total = 0.0;
        List<PreferenceItemRequest> itemsRequest = new ArrayList<>();

        for (CarritoItem item : carritoItems) {
            Producto producto = productoRepository.findById(item.getProductoId()).orElseThrow(
                () -> new MPException("Product not found: " + item.getProductoId())
            );
            
            BigDecimal unitPrice = new BigDecimal(producto.getPrecio());
            double itemTotal = unitPrice.doubleValue() * item.getCantidad();
            total += itemTotal;
            
            // Creación del item de MP (tu lógica existente)
            PreferenceItemRequest itemRequest = PreferenceItemRequest.builder()
                .title(producto.getNombre())
                .quantity(item.getCantidad())
                .unitPrice(unitPrice)
                .currencyId("CLP") 
                .build();
            itemsRequest.add(itemRequest);
        }
        
        // Crear la orden en tu BD
        Orden nuevaOrden = new Orden(userId, carritoItems, total);
        Orden ordenGuardada = ordenRepository.save(nuevaOrden);
        
        String orderId = ordenGuardada.getId();
    }
}