package cl.huertohogar.backend.model;

import jakarta.persistence.*;
import lombok.*;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
@Entity @Table(name = "usuario")
public class Usuario {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id_usuario;

    @Column(nullable = false, unique = true)
    private String correo;

    @Column(nullable = false)
    private String claveHash;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Rol rol;

    @Column(nullable = false)
    private String nombre;

    // 🔥 ELIMINACIÓN LÓGICA
    @Column(nullable = false)
    private boolean enabled = true;
}
