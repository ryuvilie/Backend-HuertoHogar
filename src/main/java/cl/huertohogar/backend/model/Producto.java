package cl.huertohogar.backend.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "producto")
public class Producto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id_producto;

    @Column(nullable = false)
    private String nombre;

    @Column(nullable = false)
    private Double precio;

    @Column(length = 255)
    private String descripcion;

    @Column(nullable = false)
    private Integer stock;

    private String imageUrl;

    private String categoria;
}
