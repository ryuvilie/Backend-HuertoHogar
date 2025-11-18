package cl.huertohogar.backend.config;

import cl.huertohogar.backend.security.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.cors.CorsConfigurationSource;

import java.util.List;

/**
 * Configuración principal de seguridad del proyecto.
 *
 * Aquí definimos:
 *  - Qué rutas son públicas (permitAll)
 *  - Qué rutas requieren autenticación
 *  - Qué rutas solo pueden ser accedidas por ADMIN
 *  - Uso de JWT en vez de sesiones
 *  - Filtro personalizado JwtAuthenticationFilter
 *  - CORS para permitir consumir la API desde apps móviles y frontends
 */
@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    // Filtro JWT creado previamente (se ejecuta en cada request)
    private final JwtAuthenticationFilter jwtFilter;

    /**
     * Bean principal de la configuración de seguridad.
     * Aquí se arma toda la lógica de Spring Security.
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
            /* 
             * 1️⃣ Habilitar CORS
             * Necesario para que la API pueda ser consumida por:
             * - Aplicación móvil
             * - Frontend (React, Angular, etc.)
             * Permite solicitudes desde otros orígenes (dominios o puertos).
             */
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))

            /*
             * 2️⃣ Desactivar CSRF
             * Se desactiva porque:
             * - No usamos formularios HTML
             * - Usamos JWT (stateless), no cookies de sesión
             */
            .csrf(csrf -> csrf.disable())

            /*
             * 3️⃣ Sesión deshabilitada
             * Política STATELESS significa que el backend:
             * - NO crea sesiones
             * - NO guarda estado
             * Cada request debe incluir su token (JWT)
             */
            .sessionManagement(sm ->
                sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )

            /*
             * 4️⃣ REGLAS DE ACCESO (quién puede entrar a qué)
             */
            .authorizeHttpRequests(auth -> auth

                // --- RUTAS PÚBLICAS ---
                // Cualquiera puede acceder SIN token
                .requestMatchers(
                    "/auth/**",          // login y registro
                    "/swagger-ui.html", // Swagger
                    "/swagger-ui/**",
                    "/api-docs/**"
                ).permitAll()

                // --- RUTAS EXCLUSIVAS PARA ADMIN ---
                // Solo un usuario con rol ADMIN puede:
                .requestMatchers("POST",   "/api/productos/**").hasRole("ADMIN")
                .requestMatchers("PUT",    "/api/productos/**").hasRole("ADMIN")
                .requestMatchers("DELETE", "/api/productos/**").hasRole("ADMIN")

                // --- TODAS LAS DEMÁS RUTAS REQUIEREN TOKEN ---
                .anyRequest().authenticated()
            )

            /*
             * 5️⃣ Registro del filtro JWT
             * Este filtro se ejecuta ANTES que UsernamePasswordAuthenticationFilter.
             * Se encarga de:
             * - Leer el token
             * - Validarlo
             * - Cargar usuario + rol en el contexto de seguridad
             */
            .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    /**
     * Bean para configurar CORS.
     * Define qué métodos y orígenes están permitidos para consumir la API.
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();

        // Permitir ANY ORIGIN (app móvil, frontend, etc.)
        config.setAllowedOrigins(List.of("*"));

        // Métodos HTTP permitidos
        config.setAllowedMethods(List.of("GET","POST","PUT","DELETE","OPTIONS"));

        // Permitir cualquier Header (Authorization, Content-Type, etc.)
        config.setAllowedHeaders(List.of("*"));

        config.setAllowCredentials(false);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }

    /**
     * Bean para encriptar contraseñas.
     * BCrypt es el estándar moderno.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Bean AuthenticationManager
     * Spring Security lo usa internamente para verificar usuario/clave.
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config)
            throws Exception {
        return config.getAuthenticationManager();
    }
}
