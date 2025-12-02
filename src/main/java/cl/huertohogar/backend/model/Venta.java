package cl.huertohogar.backend.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;

@Data
@Entity
@Table(name = "venta")
public class Venta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id_venta;

    @Column(nullable = false)
    private LocalDate fecha;

    @Column(nullable = false)
    private Double total;
}
