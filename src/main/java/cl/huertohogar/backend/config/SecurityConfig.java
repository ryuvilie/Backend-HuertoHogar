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

/**
 * Configuración principal de seguridad del proyecto.
 */
@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    // Filtro JWT creado previamente (se ejecuta en cada request)
    private final JwtAuthenticationFilter jwtFilter;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
            // 1️⃣ CORS para permitir app móvil / frontend
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))

            // 2️⃣ CSRF desactivado (API stateless con JWT)
            .csrf(csrf -> csrf.disable())

            // 3️⃣ Sin sesiones, todo por JWT
            .sessionManagement(sm ->
                sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )

            // 4️⃣ Reglas de acceso
            .authorizeHttpRequests(auth -> auth

                // --- RUTAS PÚBLICAS (SIN TOKEN) ---
                .requestMatchers(
                    "/auth/**",          // login y registro
                    "/swagger-ui.html",
                    "/swagger-ui/**",
                    "/v3/api-docs/**",
                    "/api-docs/**"
                ).permitAll()

                // --- PRODUCTOS ---
                // GET productos: público (para catálogo)
                .requestMatchers(HttpMethod.GET, "/api/productos/**").permitAll()

                // POST/PUT/DELETE productos: solo ADMIN
                .requestMatchers(HttpMethod.POST,   "/api/productos/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.PUT,    "/api/productos/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/api/productos/**").hasRole("ADMIN")
                .requestMatchers("/api/usuarios/**").hasRole("ADMIN")

                // --- CUALQUIER OTRA RUTA REQUIERE TOKEN ---
                .anyRequest().authenticated()
            )

            // 5️⃣ Filtro JWT antes del filtro de usuario/clave
            .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    /**
     * Configuración CORS.
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();

        // Permitir cualquier origen (puerto de emulador, frontend, etc.)
        config.setAllowedOrigins(List.of("*"));

        // Métodos HTTP permitidos
        config.setAllowedMethods(List.of("GET","POST","PUT","DELETE","OPTIONS"));

        // Headers permitidos
        config.setAllowedHeaders(List.of("*"));

        // Como usamos JWT en header, no necesitamos credenciales de cookies
        config.setAllowCredentials(false);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }

    /**
     * BCrypt como encriptador de contraseñas.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * AuthenticationManager que usará Spring Security internamente.
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config)
            throws Exception {
        return config.getAuthenticationManager();
    }
}
