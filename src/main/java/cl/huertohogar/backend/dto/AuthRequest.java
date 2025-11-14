package cl.huertohogar.backend.dto;

import lombok.Getter;
import lombok.Setter;

/**
 * DTO que representa los datos enviados al hacer login.
 * No es una entidad (no se guarda en BD), solo se usa
 * para transportar información entre cliente y servidor.
 */
@Getter
@Setter
public class AuthRequest {

    // Correo del usuario (sirve como "username")
    private String correo;

    // Contraseña en texto plano enviada desde el cliente
    // (se compara con la versión cifrada en la BD)
    private String clave;
}
