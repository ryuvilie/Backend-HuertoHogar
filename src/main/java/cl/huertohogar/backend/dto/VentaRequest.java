package cl.huertohogar.backend.dto;

import lombok.Data;
import java.util.List;

@Data
public class VentaRequest {

    private Long idUsuario; // puede ser null
    private List<ItemCarrito> items;

    @Data
    public static class ItemCarrito {
        private Long idProducto;
        private Integer cantidad;
    }
}
