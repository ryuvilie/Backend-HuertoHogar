package cl.huertohogar.backend.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import lombok.RequiredArgsConstructor;

import cl.huertohogar.backend.dto.*;
import cl.huertohogar.backend.service.AuthService;

/**
 * Controlador público para:
 *  - /auth/login
 *  - /auth/register
 *
 * No requiere autenticación porque el login sirve para obtener el token.
 */
@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    /**
     * LOGIN:
     * Recibe correo + clave → devuelve token, nombre y rol
     */
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody AuthRequest req) {
        return ResponseEntity.ok(authService.login(req));
    }

    /**
     * REGISTRO:
     * Recibe nombre + correo + clave + rol → crea usuario → devuelve token
     */
    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@RequestBody RegisterRequest req) {
        return ResponseEntity.ok(authService.register(req));
    }
}
