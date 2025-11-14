package cl.huertohogar.backend.model;

import java.time.LocalDate;
import java.util.List;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "pedido")
public class Pedido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id_pedido;

    @ManyToOne
    @JoinColumn(name = "id_cliente", nullable = false)
    private Cliente cliente;

    @Column(nullable = false)
    private LocalDate fecha;

    @Column(nullable = false)
    private Double total;

    // Relación con DetallePedido (1 pedido tiene muchos detalles)
    @OneToMany(mappedBy = "pedido", cascade = CascadeType.ALL)
    private List<DetallePedido> detalles;
}
