package cl.huertohogar.backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;

/**
 * Configuración para habilitar el botón "Authorize" en Swagger,
 * permitiendo enviar JWT Bearer Tokens.
 */
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {

        // Definición del esquema de seguridad (JWT)
        SecurityScheme securityScheme = new SecurityScheme()
                .type(SecurityScheme.Type.HTTP)
                .scheme("bearer")
                .bearerFormat("JWT")
                .in(SecurityScheme.In.HEADER)
                .name("Authorization");

        return new OpenAPI()
                .info(new Info()
                        .title("HuertoHogar API")
                        .version("1.0")
                        .description("Documentación de API para HuertoHogar")
                        .contact(new Contact()
                                .name("Moira")
                                .email("admin@huertohogar.cl")
                        ))
                .components(new Components().addSecuritySchemes("bearerAuth", securityScheme))
                // 🔥 Esto activa el candado
                .addSecurityItem(new SecurityRequirement().addList("bearerAuth"));
    }
}

