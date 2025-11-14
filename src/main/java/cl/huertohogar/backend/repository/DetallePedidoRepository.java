package cl.huertohogar.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import cl.huertohogar.backend.model.DetallePedido;

public interface DetallePedidoRepository extends JpaRepository<DetallePedido, Long> {
}

