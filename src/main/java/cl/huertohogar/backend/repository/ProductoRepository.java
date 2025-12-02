package cl.huertohogar.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import cl.huertohogar.backend.model.Producto;
import java.util.List;

public interface ProductoRepository extends JpaRepository<Producto, Long> {

    // ✔ SOLO productos activos
    List<Producto> findByActivoTrue();
}
