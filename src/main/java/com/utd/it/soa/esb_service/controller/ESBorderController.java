package com.utd.it.soa.esb_service.controller;

import com.utd.it.soa.esb_service.model.Order;
import com.utd.it.soa.esb_service.utils.AuthOrder;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;

@RestController
@RequestMapping("/api/v1/esb")
public class ESBorderController {

    private final WebClient webClient = WebClient.create("https://ec-orders-production.up.railway.app");
    private final AuthOrder auth = new AuthOrder();

    @PostMapping("/orders/create")
    public ResponseEntity<String> createOrder(@RequestBody Order order,
            @RequestHeader(HttpHeaders.AUTHORIZATION) String token) {
        if (!auth.validateToken(token)) {
            return ResponseEntity.status(401).body("Token inválido o expirado");
        }

        String response = webClient.post()
                .uri("/api/orders/create")
                .header(HttpHeaders.CONTENT_TYPE, "application/json")
                .bodyValue(order)
                .retrieve()
                .bodyToMono(String.class)
                .block();

        return ResponseEntity.ok(response);
    }

    @GetMapping("/orders/all")
    public ResponseEntity<String> getAllOrders(@RequestHeader(HttpHeaders.AUTHORIZATION) String token) {
        if (!auth.validateToken(token)) {
            return ResponseEntity.status(401).body("Token inválido o expirado");
        }

        String response = webClient.get()
                .uri("/api/orders/all")
                .retrieve()
                .bodyToMono(String.class)
                .block();

        return ResponseEntity.ok(response);
    }

    @PutMapping("/orders/update/{id}")
    public ResponseEntity<String> updateOrder(
            @PathVariable String id,
            @RequestBody Order order,
            @RequestHeader(HttpHeaders.AUTHORIZATION) String token) {

        if (!auth.validateToken(token)) {
            return ResponseEntity.status(401).body("Token inválido o expirado");
        }

        // Construir un objeto JSON solo con los campos permitidos
        Map<String, Object> allowedFields = new HashMap<>();
        if (order.getPaymentStatus() != null) {
            allowedFields.put("paymentStatus", order.getPaymentStatus());
        }
        if (order.getPaymentMethod() != null) {
            allowedFields.put("paymentMethod", order.getPaymentMethod());
        }
        if (order.getDeliveryDate() != null) {
            allowedFields.put("deliveryDate", order.getDeliveryDate().toString()); // Convertir LocalDate a String
        }

        if (allowedFields.isEmpty()) {
            return ResponseEntity.badRequest().body("No se enviaron campos válidos para actualizar");
        }

        System.out.println("Datos enviados a Orders Service: " + allowedFields); // Debugging

        String response = webClient.put()
                .uri("/api/orders/update/" + id)
                .bodyValue(allowedFields) // Enviar solo los campos permitidos
                .retrieve()
                .bodyToMono(String.class)
                .block();

        return ResponseEntity.ok(response);
    }

    @PatchMapping("/orders/remove/{id}")
    public ResponseEntity<String> deleteOrder(@PathVariable String id,
            @RequestHeader(HttpHeaders.AUTHORIZATION) String token) {
        if (!auth.validateToken(token)) {
            return ResponseEntity.status(401).body("Token inválido o expirado");
        }

        String response = webClient.patch()
                .uri("/api/orders/remove/" + id)
                .retrieve()
                .bodyToMono(String.class)
                .block();

        return ResponseEntity.ok(response);
    }
}