package com.utd.it.soa.esb_service.controller;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.http.MediaType;

import com.utd.it.soa.esb_service.model.User;
import com.utd.it.soa.esb_service.utils.Auth;

@RestController
@RequestMapping("/api/v1/esb")
public class ESBController {

        // Correcta inicialización de WebClient
        private final WebClient webClient = WebClient.create();
        private final Auth auth = new Auth();

        @PostMapping("/create")
        public ResponseEntity<String> createUser(@RequestBody User user,
                        @RequestHeader(HttpHeaders.AUTHORIZATION) String token) {
                System.out.println("Request Body: " + user);
                System.out.println("Token recibido: " + token);

                // Validar el token
                if (!auth.validateToken(token)) {
                        return ResponseEntity.status(400)
                                        .body("Token inválido o expirado");
                }

                // Enviar la petición correctamente con Content-Type JSON
                String response = webClient.post()
                                .uri("http://users-ecommerce:5000/api/users/create")
                                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                                .bodyValue(user)
                                .retrieve()
                                .bodyToMono(String.class)
                                .block();

                return ResponseEntity.ok(response);
        }

        // Conseguir todos los usuarios (GET)
        @GetMapping("/user/all")
        public ResponseEntity<String> getAllUsers(@RequestHeader(HttpHeaders.AUTHORIZATION) String token) {
                // Validar el token
                if (!auth.validateToken(token)) {
                        return ResponseEntity.status(400)
                                        .body("Token inválido o expirado");
                }

                // Realizar la petición GET con el token
                String response = webClient.get()
                                .uri("http://users-ecommerce:5000/api/users/all")
                                .header(HttpHeaders.AUTHORIZATION, token)
                                .retrieve()
                                .bodyToMono(String.class)
                                .block();

                return ResponseEntity.ok(response);
        }

        // Actualizar usuario (PUT)
        @PutMapping("/user/update/{id}")
        public ResponseEntity<String> updateUser(@PathVariable String id,
                        @RequestBody User user,
                        @RequestHeader(HttpHeaders.AUTHORIZATION) String token) {

                // Validar el token
                if (!auth.validateToken(token)) {
                        return ResponseEntity.status(400)
                                        .body("Token inválido o expirado");
                }

                // Realizar la petición PUT con el token
                String response = webClient.put()
                                .uri("http://users-ecommerce:5000/api/users/update/" + id)
                                .header(HttpHeaders.AUTHORIZATION, token)
                                .bodyValue(user)
                                .retrieve()
                                .bodyToMono(String.class)
                                .block();

                return ResponseEntity.ok(response);
        }

        // Eliminar usuario (PATCH)
        @PatchMapping("/user/delete/{id}")
        public ResponseEntity<String> deleteUser(@PathVariable String id,
                        @RequestHeader(HttpHeaders.AUTHORIZATION) String token) {

                // Validar el token
                if (!auth.validateToken(token)) {
                        return ResponseEntity.status(400)
                                        .body("Token inválido o expirado");
                }

                // Realizar la petición PATCH con el token
                String response = webClient.patch()
                                .uri("http://users-ecommerce:5000/api/users/remove/" + id) // localhost:4000
                                .header(HttpHeaders.AUTHORIZATION, token)
                                .retrieve()
                                .bodyToMono(String.class)
                                .block();

                return ResponseEntity.ok(response);
        }
}