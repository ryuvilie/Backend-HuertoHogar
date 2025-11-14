package cl.huertohogar.backend.service;

import org.springframework.security.authentication.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

import cl.huertohogar.backend.dto.*;
import cl.huertohogar.backend.model.*;
import cl.huertohogar.backend.repository.UsuarioRepository;
import cl.huertohogar.backend.security.JwtService;

/**
 * Servicio que contiene TODA la lógica de autenticación:
 * - Login
 * - Registro
 * - Encriptación de clave
 * - Generación de token JWT
 */
@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authManager; // valida login
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder encoder;           // encripta claves
    private final JwtService jwt;                   // genera tokens

    /**
     * LOGIN:
     * 1. Recibe correo + clave
     * 2. Spring Security valida las credenciales
     * 3. Genera un JWT si todo está OK
     * 4. Devuelve token + nombre + rol
     */
    public AuthResponse login(AuthRequest req) {

        // Valida usuario y contraseña usando Spring Security
        authManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        req.getCorreo(),
                        req.getClave()
                )
        );

        // Si pasa la autenticación, buscamos el usuario real
        Usuario u = usuarioRepository.findByCorreo(req.getCorreo())
                .orElseThrow();

        // Generamos el token JWT con correo y rol
        String token = jwt.generateToken(u.getCorreo(), u.getRol().name());

        // Devolvemos la respuesta estándar
        return new AuthResponse(token, u.getNombre(), u.getRol().name());
    }

    /**
     * REGISTRO:
     * 1. Verifica si el correo ya existe
     * 2. Cifra la clave
     * 3. Crea usuario con rol (si no se envía, por defecto USER)
     * 4. Genera token
     * 5. Devuelve token + nombre + rol
     */
    public AuthResponse register(RegisterRequest req) {

        if (usuarioRepository.existsByCorreo(req.getCorreo())) {
            throw new IllegalArgumentException("Correo ya registrado");
        }

        // Si no viene rol, lo dejamos como USER
        Rol rol = (req.getRol() == null) ? Rol.USER : req.getRol();

        Usuario nuevo = Usuario.builder()
                .nombre(req.getNombre())
                .correo(req.getCorreo())
                .claveHash(encoder.encode(req.getClave())) // ciframos clave
                .rol(rol)
                .build();

        usuarioRepository.save(nuevo);

        String token = jwt.generateToken(nuevo.getCorreo(), nuevo.getRol().name());

        return new AuthResponse(token, nuevo.getNombre(), nuevo.getRol().name());
    }
}
