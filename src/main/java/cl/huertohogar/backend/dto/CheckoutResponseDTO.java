package cl.huertohogar.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CheckoutResponseDTO {
    private Long idVenta;
    private String mensaje;
}
