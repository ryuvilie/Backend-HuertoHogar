package cl.huertohogar.backend.dto;

import cl.huertohogar.backend.model.Venta;
import lombok.Data;

@Data
public class VentaDTO {

    private Long id_venta;
    private Double total;
    private String fecha;

    // Eliminamos los datos del usuario, ya que ya no se incluyen en la venta

    public VentaDTO(Venta venta) {
        this.id_venta = venta.getId_venta();
        this.total = venta.getTotal();
        this.fecha = venta.getFecha().toString();
    }
}
