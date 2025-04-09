package com.utd.it.soa.esb_service.controller;

import com.utd.it.soa.esb_service.model.Product;
import com.utd.it.soa.esb_service.utils.AuthProduct;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;

@RestController
@RequestMapping("/api/v1/esb")
public class ESBproductController {

    private final WebClient webClient = WebClient.create("https://ec-product-production.up.railway.app");
    private final AuthProduct auth = new AuthProduct();

    @PostMapping("/product/create")
    public ResponseEntity<String> createProduct(@RequestBody Product product,
            @RequestHeader(HttpHeaders.AUTHORIZATION) String token) {
        if (!auth.validateToken(token)) {
            return ResponseEntity.status(401).body("Token inválido o expirado");
        }

        String response = webClient.post()
                .uri("/api/product/create")
                .header(HttpHeaders.CONTENT_TYPE, "application/json")
                .bodyValue(product)
                .retrieve()
                .bodyToMono(String.class)
                .block();

        return ResponseEntity.ok(response);
    }

    @GetMapping("/product/all")
    public ResponseEntity<String> getAllProducts(@RequestHeader(HttpHeaders.AUTHORIZATION) String token) {
        if (!auth.validateToken(token)) {
            return ResponseEntity.status(401).body("Token inválido o expirado");
        }

        String response = webClient.get()
                .uri("/api/product/all")
                .retrieve()
                .bodyToMono(String.class)
                .block();

        return ResponseEntity.ok(response);
    }

    @PutMapping("/product/update/{id}")
    public ResponseEntity<String> updateProduct(
            @PathVariable String id,
            @RequestBody Product product,
            @RequestHeader(HttpHeaders.AUTHORIZATION) String token) {

        if (!auth.validateToken(token)) {
            return ResponseEntity.status(401).body("Token inválido o expirado");
        }

        // Filtrar solo los campos permitidos para actualización
        Map<String, Object> allowedFields = new HashMap<>();
        if (product.getPrice() != null)
            allowedFields.put("price", product.getPrice());
        if (product.getStock() != null)
            allowedFields.put("stock", product.getStock());
        if (product.getSku() != null)
            allowedFields.put("sku", product.getSku().toUpperCase()); // Convertir a mayúsculas
        if (product.getColor() != null)
            allowedFields.put("color", product.getColor());

        if (allowedFields.isEmpty()) {
            return ResponseEntity.badRequest().body("No se enviaron campos válidos para actualizar");
        }

        System.out.println("Datos enviados a Product Service: " + allowedFields); // Debugging

        String response = webClient.put()
                .uri("/api/product/update/" + id)
                .bodyValue(allowedFields) // Enviar solo los campos permitidos
                .retrieve()
                .bodyToMono(String.class)
                .block();

        return ResponseEntity.ok(response);
    }

    @PatchMapping("/product/remove/{id}")
    public ResponseEntity<String> deleteProduct(@PathVariable String id,
            @RequestHeader(HttpHeaders.AUTHORIZATION) String token) {
        if (!auth.validateToken(token)) {
            return ResponseEntity.status(401).body("Token inválido o expirado");
        }

        String response = webClient.patch()
                .uri("/api/product/remove/" + id)
                .retrieve()
                .bodyToMono(String.class)
                .block();

        return ResponseEntity.ok(response);
    }
}