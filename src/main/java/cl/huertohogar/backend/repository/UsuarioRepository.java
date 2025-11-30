package cl.huertohogar.backend.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import cl.huertohogar.backend.model.Usuario;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    Optional<Usuario> findByCorreo(String correo); // 🔥 NECESARIO
    boolean existsByCorreo(String correo);
}
