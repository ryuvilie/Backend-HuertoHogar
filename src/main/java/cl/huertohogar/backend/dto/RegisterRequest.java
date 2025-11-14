package cl.huertohogar.backend.dto;

import cl.huertohogar.backend.model.Rol;
import lombok.Getter;
import lombok.Setter;

/**
 * DTO que representa los datos enviados al registrar un usuario.
 * El backend luego los transforma en una entidad Usuario.
 */
@Getter
@Setter
public class RegisterRequest {

    // Nombre del usuario (se muestra luego en la interfaz)
    private String nombre;

    // Correo electrónico (único por usuario)
    private String correo;

    // Contraseña en texto plano (se cifrará antes de guardar)
    private String clave;

    // Rol del usuario: ADMIN o USER (si no se envía, se asume USER)
    private Rol rol;
}
