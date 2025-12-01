package cl.huertohogar.backend.dto;

import cl.huertohogar.backend.model.Venta;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class VentaDTO {

    private Long id;
    private LocalDate fecha;
    private Double total;
    private Long idUsuario;
    private List<DetalleDTO> detalles;

    public VentaDTO(Venta v) {
        this.id = v.getId_venta();
        this.fecha = v.getFecha();
        this.total = v.getTotal();
        this.idUsuario = v.getUsuario() != null ? v.getUsuario().getId_usuario() : null;
        this.detalles = v.getDetalles()
                .stream()
                .map(DetalleDTO::new)
                .toList();
    }
}
