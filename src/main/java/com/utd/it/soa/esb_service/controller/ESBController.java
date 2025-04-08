package com.utd.it.soa.esb_service.controller;

import java.util.Map;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
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
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.http.MediaType;

import com.utd.it.soa.esb_service.model.User;
import com.utd.it.soa.esb_service.utils.Auth;

import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/v1/esb")
public class ESBController {

        // Correcta inicialización de WebClient
        private final WebClient webClient = WebClient
                        .create("http://ec-users-production.up.railway.app:5000/api/users");
        private final Auth auth = new Auth();

        @PostMapping("/create")
        public ResponseEntity<String> createUser(@RequestBody User user) {
                System.out.println("Request Body: " + user);

                // Enviar la petición correctamente con Content-Type JSON
                String response = webClient.post()
                                .uri("/create")
                                /* .uri("http://users-ecommerce:5000/api/users/create") */
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
                                .uri("/all")
                                /* .uri("http://users-ecommerce:5000/api/users/all") */
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
                                .uri("/update/" + id)
                                /* .uri("http://users-ecommerce:5000/api/users/update/" + id) */
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
                                .uri("/remove/" + id)
                                /* .uri("http://users-ecommerce:5000/api/users/remove/" + id) */
                                .header(HttpHeaders.AUTHORIZATION, token)
                                .retrieve()
                                .bodyToMono(String.class)
                                .block();

                return ResponseEntity.ok(response);
        }

        @PostMapping(value = "/user/login", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
        public Mono<ResponseEntity<String>> loginUser(@RequestBody User user) {

                System.out.println("Enviando solicitud a Node.js con usuario: " + user.getUsername());

                return webClient.post()
                                .uri("/login")
                                /* .uri("http://users-ecommerce:5000/api/users/login") */
                                .contentType(MediaType.APPLICATION_JSON)
                                .bodyValue(user)
                                .retrieve()
                                .bodyToMono(String.class)
                                .map(response -> {
                                        System.out.println("Respuesta del servicio Node.js: " + response);
                                        HttpHeaders headers = new HttpHeaders();
                                        headers.setContentType(MediaType.APPLICATION_JSON);
                                        return ResponseEntity.ok().headers(headers).body(response);
                                })
                                .onErrorResume(WebClientResponseException.class,
                                                e -> Mono.just(ResponseEntity.status(e.getStatusCode())
                                                                .body(e.getResponseBodyAsString())))
                                .onErrorResume(e -> Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                                .body("Error interno del servidor")));
        }

        @PostMapping(value = "/users/newpassword/{token}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
        public Mono<ResponseEntity<String>> setNewPassword(
                        @PathVariable String token,
                        @RequestBody Map<String, String> requestBody) {

                System.out.println("Solicitud de cambio de contraseña con token: " + token);

                return webClient.post()
                                .uri("/newpassword/" + token)
                                /* .uri("http://users-ecommerce:5000/api/users/newpassword/" + token) */
                                .contentType(MediaType.APPLICATION_JSON)
                                .bodyValue(requestBody)
                                .retrieve()
                                .bodyToMono(String.class)
                                .map(response -> {
                                        System.out.println("Respuesta del servicio de usuarios: " + response);
                                        return ResponseEntity.ok()
                                                        .contentType(MediaType.APPLICATION_JSON)
                                                        .body(response);
                                })
                                .onErrorResume(WebClientResponseException.class, e -> {
                                        // Manejo específico para errores de token
                                        if (e.getStatusCode() == HttpStatus.BAD_REQUEST) {
                                                return Mono.just(ResponseEntity.badRequest()
                                                                .body("{\"message\":\"Token inválido o expirado\"}"));
                                        }
                                        return Mono.just(ResponseEntity.status(e.getStatusCode())
                                                        .body(e.getResponseBodyAsString()));
                                })
                                .onErrorResume(e -> Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                                .body("{\"message\":\"Error al actualizar la contraseña\"}")));
        }
}