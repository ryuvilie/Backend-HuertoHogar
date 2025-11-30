package cl.huertohogar.backend.dto;

import lombok.Data;

@Data
public class DetalleVentaRequest {
    private Long id_producto;
    private Integer cantidad;
    private Double subtotal;
}
