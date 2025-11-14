package cl.huertohogar.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import cl.huertohogar.backend.model.Producto;

public interface ProductoRepository extends JpaRepository<Producto, Long> {
}
