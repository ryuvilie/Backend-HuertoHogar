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
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtFilter;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
            // 🌎 CORS habilitado
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))

            // ❌ CSRF deshabilitado (JWT no lo necesita)
            .csrf(csrf -> csrf.disable())

            // 📌 API Stateless (sin sesiones)
            .sessionManagement(session ->
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )

            // 🔐 Reglas de seguridad
            .authorizeHttpRequests(auth -> auth

                // 🔓 OPTIONS siempre permitidos
                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                // 🔓 Endpoints públicos
                .requestMatchers("/auth/**").permitAll()
                .requestMatchers("/doc/**", "/v3/**", "/", "/swagger-ui/**", "/swagger-ui.html", "/v3/api-docs/**").permitAll()

                // 🛒 Carrito (público)
                .requestMatchers("/api/carrito/**").permitAll()

                // 📦 Productos: GET y stock/price PATCH públicos
                .requestMatchers(HttpMethod.GET, "/api/productos/**").permitAll()
                .requestMatchers(HttpMethod.PATCH, "/api/productos/*/stock").permitAll()
                .requestMatchers(HttpMethod.PATCH, "/api/productos/*/precio").permitAll()

                // 📦 Productos: POST/PUT/DELETE → ADMIN
                .requestMatchers(HttpMethod.POST, "/api/productos/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.PUT, "/api/productos/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/api/productos/**").hasRole("ADMIN")

                // 🧾 Ventas: público GET/POST
                .requestMatchers(HttpMethod.GET, "/api/ventas/**").permitAll()
                .requestMatchers(HttpMethod.POST, "/api/ventas/**").permitAll()

                // 👤 Usuarios solo ADMIN
                .requestMatchers("/api/usuarios/**").hasRole("ADMIN")

                // 🔐 Cualquier otra ruta requiere JWT
                .anyRequest().authenticated()
            )

            // 🔥 Filtro JWT antes del filtro por defecto
            .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    // 🌎 Configuración CORS global
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();

        config.setAllowedOrigins(List.of("*"));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(false);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }

    // 🔑 Password encoder (BCrypt)
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // 🔐 AuthenticationManager
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config)
            throws Exception {
        return config.getAuthenticationManager();
    }
}
