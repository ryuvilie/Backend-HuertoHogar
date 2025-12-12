package cl.huertohogar.backend.service;

import org.springframework.security.authentication.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

import cl.huertohogar.backend.dto.*;
import cl.huertohogar.backend.model.*;
import cl.huertohogar.backend.repository.UsuarioRepository;
import cl.huertohogar.backend.security.JwtService;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authManager;
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder encoder;
    private final JwtService jwt;

    // 🔐 LOGIN
    public AuthResponse login(AuthRequest req) {

        authManager.authenticate(
            new UsernamePasswordAuthenticationToken(
                req.getCorreo(),
                req.getClave()
            )
        );

        Usuario u = usuarioRepository.findByCorreo(req.getCorreo())
                .orElseThrow();

        String token = jwt.generateToken(
                u.getCorreo(),
                u.getRol().name()
        );

        return new AuthResponse(
                token,
                u.getNombre(),
                u.getRol().name()
        );
    }

    // 📝 REGISTER
    public AuthResponse register(RegisterRequest req) {

        if (usuarioRepository.existsByCorreo(req.getCorreo())) {
            throw new IllegalArgumentException("Correo ya registrado");
        }

        // 👉 Si no viene rol, se usa USER (como antes)
        Rol rol = (req.getRol() == null) ? Rol.USER : req.getRol();

        Usuario nuevo = Usuario.builder()
                .nombre(req.getNombre())
                .correo(req.getCorreo())
                .claveHash(encoder.encode(req.getClave()))
                .rol(rol)
                .build();

        usuarioRepository.save(nuevo);

        String token = jwt.generateToken(
                nuevo.getCorreo(),
                nuevo.getRol().name()
        );

        return new AuthResponse(
                token,
                nuevo.getNombre(),
                nuevo.getRol().name()
        );
    }
}
