package cl.huertohogar.backend.config;

import cl.huertohogar.backend.security.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.*;

import java.util.List;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtFilter;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .csrf(csrf -> csrf.disable())
            .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

            .authorizeHttpRequests(auth -> auth

                // 🔓 PUBLICO
                .requestMatchers("/auth/**").permitAll()
                .requestMatchers(
                        "/swagger-ui.html",
                        "/swagger-ui/**",
                        "/v3/api-docs/**",
                        "/api-docs/**"
                ).permitAll()

                // 🔓 PRODUCTOS
                .requestMatchers(HttpMethod.GET, "/api/productos/**").permitAll()

                // ⭐ COMENTARIOS SOLO CLIENTE
                .requestMatchers(HttpMethod.POST, "/api/productos/*/comentario")
                    .hasRole("CLIENTE")

                // 🔒 PRODUCTOS ADMIN
                .requestMatchers(HttpMethod.POST, "/api/productos/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.PUT, "/api/productos/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/api/productos/**").hasRole("ADMIN")

                // 🛒 CARRITO + CHECKOUT (CREA VENTA)
                // 👉 cualquier usuario puede finalizar compra
                .requestMatchers("/api/carrito/**").permitAll()

                // 🧾 VENTAS
                // Crear venta: viene del carrito → permitido
                .requestMatchers(HttpMethod.POST, "/api/ventas/**").permitAll()

                // Ver ventas: permitir acceso público (temporal para frontend dev)
                .requestMatchers(HttpMethod.GET, "/api/ventas/**").permitAll()

                // Gestionar ventas (editar/eliminar): VENDEDOR o ADMIN
                .requestMatchers(HttpMethod.PUT, "/api/ventas/**").hasAnyRole("ADMIN", "VENDEDOR")

                // Eliminar ventas: solo ADMIN
                .requestMatchers(HttpMethod.DELETE, "/api/ventas/**").hasRole("ADMIN")

                // 👤 USUARIOS
                .requestMatchers("/api/usuarios/**").hasRole("ADMIN")

                // 🔐 RESTO
                .anyRequest().authenticated()
            )

            .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(List.of("*"));
        config.setAllowedMethods(List.of("GET","POST","PUT","DELETE","PATCH","OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(false);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config)
            throws Exception {
        return config.getAuthenticationManager();
    }
}
