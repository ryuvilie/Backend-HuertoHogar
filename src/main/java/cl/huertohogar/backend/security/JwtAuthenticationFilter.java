package cl.huertohogar.backend.security;

import java.io.IOException;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;

import org.springframework.web.filter.OncePerRequestFilter;

import lombok.RequiredArgsConstructor;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Filtro JWT que se ejecuta ANTES que cualquier endpoint.
 * Su función:
 * 1. Leer el token JWT desde el header Authorization.
 * 2. Validarlo.
 * 3. Extraer correo y rol.
 * 4. Autenticar al usuario dentro de Spring Security.
 */
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UsuarioDetailsService usuarioDetailsService;

    /**
     * Cada request del cliente pasa por este método.
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        // 1) Leer el header Authorization
        final String authHeader = request.getHeader("Authorization");

        // Si no existe o no empieza con "Bearer ", no hay token → continuar normal
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        // 2) Extraemos el token real (sin "Bearer ")
        String token = authHeader.substring(7);

        // 3) Validar token y evitar doble autenticación
        if (jwtService.isValid(token)
            && SecurityContextHolder.getContext().getAuthentication() == null) {

            // 4) Obtener el correo del usuario desde el token
            String username = jwtService.extractUsername(token);

            // 5) Cargar datos del usuario desde BD
            var userDetails = usuarioDetailsService.loadUserByUsername(username);

            // 6) Crear el objeto de autenticación para Spring Security
            UsernamePasswordAuthenticationToken authToken =
                    new UsernamePasswordAuthenticationToken(
                            userDetails,             // usuario
                            null,                    // no necesitamos contraseña
                            userDetails.getAuthorities() // roles (ADMIN/USER)
                    );

            // Asociamos metadata de la request (IP, headers, etc.)
            authToken.setDetails(
                    new WebAuthenticationDetailsSource().buildDetails(request)
            );

            // 7) Guardar autenticación en el contexto global
            SecurityContextHolder.getContext().setAuthentication(authToken);
        }

        // 8) Continuar la cadena de filtros
        filterChain.doFilter(request, response);
    }
}
