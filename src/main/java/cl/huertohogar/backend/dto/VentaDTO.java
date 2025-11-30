package cl.huertohogar.backend.dto;

import cl.huertohogar.backend.model.Venta;
import lombok.Data;

@Data
public class VentaDTO {

    private Long id_venta;
    private Double total;
    private String fecha;

    // 🔥 Datos del usuario asociado a la venta (si existe)
    private Long usuarioId;
    private String usuarioNombre;
    private String usuarioCorreo;

    public VentaDTO(Venta venta) {
        this.id_venta = venta.getId_venta();
        this.total = venta.getTotal();
        this.fecha = venta.getFecha().toString();

        if (venta.getUsuario() != null) {
            this.usuarioId = venta.getUsuario().getId_usuario();
            this.usuarioNombre = venta.getUsuario().getNombre();
            this.usuarioCorreo = venta.getUsuario().getCorreo();
        }
    }
}
