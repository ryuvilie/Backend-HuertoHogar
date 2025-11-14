package cl.huertohogar.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * DTO que representa la respuesta enviada al cliente
 * luego de un login o registro exitoso.
 * Contiene el token JWT generado, y datos útiles del usuario.
 */
@Getter
@Setter
@AllArgsConstructor
public class AuthResponse {

    // Token JWT que el frontend debe guardar
    private String token;

    // Nombre del usuario (para mostrar en la interfaz)
    private String nombre;

    // Rol del usuario (ADMIN / USER)
    private String rol;
}

