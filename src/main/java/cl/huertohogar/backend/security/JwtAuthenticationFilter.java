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

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UsuarioDetailsService usuarioDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String path = request.getServletPath();

        // 🔓 Excluir rutas abiertas
        if (path.startsWith("/auth/")
            || path.startsWith("/api/productos")
            || path.startsWith("/api/carrito")
            || path.startsWith("/api/ventas")) 
        {
            filterChain.doFilter(request, response);
            return;
        }

        // 1) Leer Authorization
        final String authHeader = request.getHeader("Authorization");

        // 🔓 Si no viene header o viene malo → NO autenticar, pero dejar pasar
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        // 2) Extraer token
        String token = authHeader.substring(7);

        // 🔓 Si token es null, vacío o literalmente "null" → continuar sin bloquear
        if (token == null || token.isBlank() || token.equalsIgnoreCase("null")) {
            filterChain.doFilter(request, response);
            return;
        }

        // 3) Validar token
        if (jwtService.isValid(token)
                && SecurityContextHolder.getContext().getAuthentication() == null) {

            String username = jwtService.extractUsername(token);
            var userDetails = usuarioDetailsService.loadUserByUsername(username);

            UsernamePasswordAuthenticationToken authToken =
                    new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null,
                            userDetails.getAuthorities()
                    );

            authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authToken);
        }

        // 4) Continuar siempre
        filterChain.doFilter(request, response);
    }
}
