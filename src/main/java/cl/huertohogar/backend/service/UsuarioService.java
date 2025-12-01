package cl.huertohogar.backend.service;

import cl.huertohogar.backend.dto.UsuarioResponse;
import cl.huertohogar.backend.repository.UsuarioRepository;
import cl.huertohogar.backend.model.Usuario;
import cl.huertohogar.backend.model.Rol;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;

    public UsuarioService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    // ===========================================================
    // 🔹 LISTAR USUARIOS
    // ===========================================================

    public List<UsuarioResponse> listarTodos() {

        return usuarioRepository.findAll()
                .stream()
                .map(u -> new UsuarioResponse(
                        u.getId_usuario(),
                        u.getNombre(),
                        u.getCorreo(),
                        u.getRol().name()
                ))
                .collect(Collectors.toList());
    }

    // ===========================================================
    // 🔹 CAMBIAR ROL
    // ===========================================================

    public UsuarioResponse cambiarRol(Long idUsuario, String nuevoRol) {

        Usuario usuario = usuarioRepository.findById(idUsuario)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado"));

        Rol rolNuevo = Rol.valueOf(nuevoRol.toUpperCase());
        usuario.setRol(rolNuevo);

        usuarioRepository.save(usuario);

        return new UsuarioResponse(
                usuario.getId_usuario(),
                usuario.getNombre(),
                usuario.getCorreo(),
                usuario.getRol().name()
        );
    }

    // ===========================================================
    // 🔹 ELIMINAR (LÓGICO)
    // ===========================================================

    public void eliminarUsuario(Long idUsuario) {

        Usuario usuario = usuarioRepository.findById(idUsuario)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Usuario no encontrado"
                ));

        usuario.setEnabled(false); // eliminación lógica
        usuarioRepository.save(usuario);
    }

    // ===========================================================
    // 🔹 PERFIL (usuario autenticado)
    // ===========================================================

    public UsuarioResponse obtenerPerfilActual() {

        String correo = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();

        Usuario usuario = usuarioRepository.findByCorreo(correo)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Usuario no encontrado"));

        return new UsuarioResponse(
                usuario.getId_usuario(),
                usuario.getNombre(),
                usuario.getCorreo(),
                usuario.getRol().name()
        );
    }

    public UsuarioResponse actualizarPerfilActual(UsuarioResponse datos) {

        String correoActual = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();

        Usuario usuario = usuarioRepository.findByCorreo(correoActual)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Usuario no encontrado"));

        usuario.setNombre(datos.getNombre());
        usuario.setCorreo(datos.getCorreo());

        usuarioRepository.save(usuario);

        return new UsuarioResponse(
                usuario.getId_usuario(),
                usuario.getNombre(),
                usuario.getCorreo(),
                usuario.getRol().name()
        );
    }
}
