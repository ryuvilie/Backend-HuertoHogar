package cl.huertohogar.backend.repository;

import cl.huertohogar.backend.model.Carrito;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CarritoRepository extends JpaRepository<Carrito, Long> {
}
