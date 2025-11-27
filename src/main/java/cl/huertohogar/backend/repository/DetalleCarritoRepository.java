package cl.huertohogar.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import cl.huertohogar.backend.model.DetalleCarrito;

public interface DetalleCarritoRepository extends JpaRepository<DetalleCarrito, Long> {

}
