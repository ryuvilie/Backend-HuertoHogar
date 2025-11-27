package cl.huertohogar.backend.dto;

import cl.huertohogar.backend.model.Producto;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class DetalleCarritoDTO {
    private Long idItem;
    private Integer cantidad;
    private Double subtotal;
    private Producto producto;
}
