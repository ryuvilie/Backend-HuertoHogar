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

/**
 * Configuración principal de la seguridad del proyecto.
 * Aquí definimos:
 *  - qué rutas son públicas
 *  - qué rutas requieren autenticación
 *  - qué rutas son solo para ADMIN
 *  - el uso de JWT en lugar de sesiones
 *  - el filtro JwtAuthenticationFilter
 */
@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    // Inyectamos el filtro JWT creado previamente
    private final JwtAuthenticationFilter jwtFilter;

    /**
     * Bean principal de configuración de seguridad.
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
            // 🔐 1) Desactivamos CSRF porque JWT no usa formularios
            .csrf(csrf -> csrf.disable())

            // 🧱 2) Declaramos que NO usaremos sesiones
            //      porque trabajamos con JWT (stateless)
            .sessionManagement(sm ->
                sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )

            // 🚦 3) Configuración de rutas (quién puede acceder a qué)
            .authorizeHttpRequests(auth -> auth

                // --- ENDPOINTS PÚBLICOS ---
                .requestMatchers(
                    "/auth/**",               // login y registro
                    "/swagger-ui.html",       // swagger
                    "/swagger-ui/**",
                    "/api-docs/**"
                ).permitAll()

                // --- ENDPOINTS SOLO ADMIN ---
                // Ejemplo: borrar productos requiere rol ADMIN
                .requestMatchers("DELETE", "/api/productos/**").hasRole("ADMIN")

                // --- EL RESTO REQUIERE ESTAR LOGEADO ---
                .anyRequest().authenticated()
            )

            // 🧩 4) Registramos nuestro filtro JWT
            .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        // Construimos la configuración final
        return http.build();
    }

    /**
     * Bean para encriptar contraseñas.
     * BCrypt es el algoritmo estándar en seguridad moderna.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * AuthenticationManager es necesario para que Spring Security
     * gestione el login (comparar contraseñas, etc.)
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config)
            throws Exception {
        return config.getAuthenticationManager();
    }
}
