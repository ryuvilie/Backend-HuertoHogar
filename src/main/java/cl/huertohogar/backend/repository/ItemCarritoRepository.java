package cl.huertohogar.backend.repository;

import cl.huertohogar.backend.model.Carrito;
import cl.huertohogar.backend.model.ItemCarrito;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ItemCarritoRepository extends JpaRepository<ItemCarrito, Long> {

    List<ItemCarrito> findByCarrito(Carrito carrito);

    void deleteAllByCarrito(Carrito carrito);
}
