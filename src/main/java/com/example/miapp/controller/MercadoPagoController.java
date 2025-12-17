package com.example.miapp.controller;

import com.example.miapp.model.CarritoItem;
import com.example.miapp.service.MercadoPagoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/mercadopago")
@CrossOrigin(origins = "http://localhost:3000")
public class MercadoPagoController {

    @Autowired
    private MercadoPagoService mercadoPagoService;

    public static class PreferenceResponse {
        public String init_point;

        public PreferenceResponse(String init_point) {
            this.init_point = init_point;
        }
    }

    @PostMapping("/create-preference")
    public ResponseEntity<?> createPreference(@RequestBody List<CarritoItem> carritoItems) {

        String userId = "usuario_test_001";

        try {
            String initPoint = mercadoPagoService.createPreference(carritoItems, userId);
            return ResponseEntity.ok(new PreferenceResponse(initPoint));

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity
                    .status(500)
                    .body(Map.of("error", "Error al crear la preferencia de pago"));
        }
    }
}