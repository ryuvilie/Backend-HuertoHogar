package cl.huertohogar.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import cl.huertohogar.backend.model.Cliente;

public interface ClienteRepository extends JpaRepository<Cliente, Long> {
}
