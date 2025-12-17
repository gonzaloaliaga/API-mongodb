package com.example.miapp.service;

import com.example.miapp.model.Producto;
import com.example.miapp.model.CarritoItem;
import com.example.miapp.repository.ProductoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import com.example.miapp.model.Orden;
import com.example.miapp.repository.OrdenRepository;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.*;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;

import java.math.BigDecimal;
import java.net.http.HttpHeaders;
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
    private ProductoRepository productoRepository;

    @Autowired
    private OrdenRepository ordenRepository;

    private final RestTemplate restTemplate = new RestTemplate();

    public String createPreference(List<CarritoItem> carritoItems, String userId) {

        double total = 0.0;
        List<Map<String, Object>> itemsMp = new ArrayList<>();

        for (CarritoItem item : carritoItems) {
            Producto producto = productoRepository.findById(item.getProductoId())
                    .orElseThrow(() -> new RuntimeException("Producto no encontrado"));

            double precio = producto.getPrecio();
            total += precio * item.getCantidad();

            itemsMp.add(Map.of(
                "title", producto.getNombre(),
                "quantity", item.getCantidad(),
                "unit_price", precio,
                "currency_id", "CLP"
            ));
        }

        // 1️⃣ Crear la orden ANTES del pago
        Orden orden = new Orden(userId, carritoItems, total);
        orden = ordenRepository.save(orden);

        // 2️⃣ Crear preferencia en MP
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(accessToken);

        Map<String, Object> body = Map.of(
            "items", itemsMp,
            "external_reference", orden.getId(),
            "back_urls", Map.of(
                "success", successUrl,
                "pending", pendingUrl,
                "failure", failureUrl
            ),
            "auto_return", "approved"
        );

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);

        ResponseEntity<Map> response = restTemplate.postForEntity(
                "https://api.mercadopago.com/checkout/preferences",
                request,
                Map.class
        );

        Map responseBody = response.getBody();
        String preferenceId = (String) responseBody.get("id");
        String initPoint = (String) responseBody.get("init_point");

        // 3️⃣ Guardar referencia MP
        orden.setMercadoPagoPreferenceId(preferenceId);
        ordenRepository.save(orden);

        return initPoint;
    }
}
