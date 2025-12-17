package com.example.miapp.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Data;

@Data
@Document(collection = "ordenes")
public class Orden {

    @Id
    private String id; // ID de la orden generada en tu sistema

    private String usuarioId; // ID del Usuario que realizó la compra

    private List<CarritoItem> items; // Lista de productos y cantidades compradas (usando tu modelo existente)
    
    private double total; // Monto total del pedido
    
    // Estado de la orden en tu tienda (ej: CREADA, PAGADA, ENVIADA, CANCELADA)
    private String estadoOrden; 
    
    // Referencias de Mercado Pago para trazabilidad
    private String mercadoPagoPreferenceId; // ID de Preferencia que retorna Mercado Pago al crear el pago
    private String mercadoPagoPaymentId;    // ID de Pago que retorna Mercado Pago en el Webhook
    private String mercadoPagoStatus;       // Estado del pago según Mercado Pago (approved, pending, rejected)
    
    private LocalDateTime fechaCreacion;

    // Constructor vacío (necesario para Spring Data)
    public Orden() {
        this.fechaCreacion = LocalDateTime.now();
    }

    // Constructor con campos esenciales (ajusta según tus necesidades)
    public Orden(String usuarioId, List<CarritoItem> items, double total) {
        this.usuarioId = usuarioId;
        this.items = items;
        this.total = total;
        this.estadoOrden = "CREADA"; // Estado inicial
        this.fechaCreacion = LocalDateTime.now();
    }

    // Getters y Setters (Necesitas implementarlos)
    // ...
    // Ejemplo de Getter y Setter para estadoOrden:
    public String getEstadoOrden() {
        return estadoOrden;
    }

    public void setEstadoOrden(String estadoOrden) {
        this.estadoOrden = estadoOrden;
    }
    
    // (Asegúrate de generar o escribir todos los getters y setters necesarios)
    // ...
}