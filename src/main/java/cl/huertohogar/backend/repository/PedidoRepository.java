package cl.huertohogar.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import cl.huertohogar.backend.model.Pedido;

public interface PedidoRepository extends JpaRepository<Pedido, Long> {
}
