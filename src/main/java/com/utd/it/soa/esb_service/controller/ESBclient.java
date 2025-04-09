package com.utd.it.soa.esb_service.controller;

import com.utd.it.soa.esb_service.model.Client;
import com.utd.it.soa.esb_service.utils.AuthClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.http.HttpHeaders;

@RestController
@RequestMapping("/api/v1/esb")
public class ESBclient {

    @Autowired
    private WebClient.Builder webClientBuilder;

    private final AuthClient authClient = new AuthClient();

    // Crear cliente (POST)
    @PostMapping("/create/client")
    public ResponseEntity<String> createClient(@RequestHeader("Authorization") String token,
            @RequestBody Client client) {
        if (authClient.validateToken(token)) {
            String response = webClientBuilder.build().post()
                    .uri("https://ec-clients-production.up.railway.app/api/clients/create")
                    .header("Authorization", token)
                    .bodyValue(client)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.status(401).body("Token inválido o expirado.");
        }
    }

    // Conseguir todos los clientes (GET)
    @GetMapping("/client/all")
    public ResponseEntity<String> getAllClients(@RequestHeader(HttpHeaders.AUTHORIZATION) String token) {
        // Validar el token
        if (!authClient.validateToken(token)) {
            return ResponseEntity.status(400)
                    .body("Token inválido o expirado");
        }

        // Realizar la petición GET con el token
        String response = webClientBuilder.build().get()
                .uri("https://ec-clients-production.up.railway.app/api/clients/all")
                .header(HttpHeaders.AUTHORIZATION, token)
                .retrieve()
                .bodyToMono(String.class)
                .block();

        return ResponseEntity.ok(response);
    }

    // Actualizar cliente (PUT)
    @PutMapping("/clients/update/{id}")
    public ResponseEntity<String> updateClient(@RequestHeader("Authorization") String token, @PathVariable String id,
            @RequestBody Client client) {
        if (authClient.validateToken(token)) {
            String response = webClientBuilder.build().put()
                    .uri("https://ec-clients-production.up.railway.app/api/clients/update/" + id)
                    .header("Authorization", token)
                    .bodyValue(client)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.status(401).body("Token inválido o expirado.");
        }
    }

    // Eliminar cliente (PATCH)
    @PatchMapping("/client/delete/{id}")
    public ResponseEntity<String> deleteClient(@PathVariable String id,
            @RequestHeader(HttpHeaders.AUTHORIZATION) String token) {

        // Validar el token
        if (!authClient.validateToken(token)) {
            return ResponseEntity.status(400)
                    .body("Token inválido o expirado");
        }

        // Realizar la petición PATCH con el token
        String response = webClientBuilder.build().patch()
                .uri("https://ec-clients-production.up.railway.app/api/clients/remove/" + id)
                .header(HttpHeaders.AUTHORIZATION, token)
                .header("Content-Type", "application/json")
                .retrieve()
                .bodyToMono(String.class)
                .block();

        return ResponseEntity.ok(response);
    }
}