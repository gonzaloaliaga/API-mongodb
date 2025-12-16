package com.example.miapp.controller;

import com.example.miapp.model.CarritoItem; // Usamos la interfaz de item de carrito
import com.example.miapp.service.MercadoPagoService;
import com.mercadopago.exceptions.MPApiException;
import com.mercadopago.exceptions.MPException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/mercadopago")
@CrossOrigin(origins = "http://localhost:3000") // Asegúrate de que tu frontend pueda acceder
public class MercadoPagoController {

    @Autowired
    private MercadoPagoService mercadoPagoService;

    // Estructura de respuesta simple
    public static class PreferenceResponse {
        public String init_point;

        public PreferenceResponse(String init_point) {
            this.init_point = init_point;
        }
    }

    // Endpoint para generar la preferencia de pago
    @PostMapping("/create-preference")
    public ResponseEntity<?> createPreference(@RequestBody List<CarritoItem> carritoItems) {
        // NOTA: Asumo que tienes alguna forma de obtener el ID del usuario loggeado.
        // Aquí lo haré de forma simple para el ejemplo.
        String userId = "usuario_test_001"; 
        
        try {
            String initPoint = mercadoPagoService.createPreference(carritoItems, userId);
            
            // Retorna el link de inicialización que el frontend usará para redirigir
            return ResponseEntity.ok(new PreferenceResponse(initPoint));
            
        } catch (MPException e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(Map.of("error", "Error interno de Mercado Pago: " + e.getMessage()));
        } catch (MPApiException e) {
            e.printStackTrace();
            return ResponseEntity.status(400).body(Map.of("error", "Error de API de Mercado Pago: " + e.getApiResponse().getContent()));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(Map.of("error", "Error al procesar la solicitud: " + e.getMessage()));
        }
    }
}