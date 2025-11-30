package cl.huertohogar.backend.dto;

import lombok.Data;
import java.util.List;

@Data
public class VentaRequest {
    private Double total;
    private List<DetalleVentaRequest> detalles;
}
