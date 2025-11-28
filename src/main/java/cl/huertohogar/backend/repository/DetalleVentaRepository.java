package cl.huertohogar.backend.repository;

import cl.huertohogar.backend.model.DetalleVenta;
import cl.huertohogar.backend.model.Venta;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DetalleVentaRepository extends JpaRepository<DetalleVenta, Long> {

    // 🔹 Para obtener los detalles de una venta específica
    List<DetalleVenta> findByVenta(Venta venta);
}
