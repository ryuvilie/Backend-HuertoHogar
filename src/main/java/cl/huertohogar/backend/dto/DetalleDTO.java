package cl.huertohogar.backend.dto;

import cl.huertohogar.backend.model.DetalleVenta;
import lombok.Data;

@Data
public class DetalleDTO {

    private Long idProducto;
    private String nombre;
    private Double precio;
    private Integer cantidad;
    private Double subtotal;

    public DetalleDTO(DetalleVenta d) {
        this.idProducto = d.getProducto().getId_producto();
        this.nombre = d.getProducto().getNombre();
        this.precio = d.getProducto().getPrecio();
        this.cantidad = d.getCantidad();
        this.subtotal = d.getSubtotal();
    }
}
