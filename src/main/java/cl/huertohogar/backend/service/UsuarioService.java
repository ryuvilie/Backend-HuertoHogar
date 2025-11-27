package cl.huertohogar.backend.service;

import cl.huertohogar.backend.dto.UsuarioResponse;
import cl.huertohogar.backend.model.Rol;
import cl.huertohogar.backend.model.Usuario;
import cl.huertohogar.backend.repository.UsuarioRepository;
import org.springframework.http.HttpStatus;
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

    // 🔹 Listar todos los usuarios
    public List<UsuarioResponse> listarTodos() {
        List<Usuario> usuarios = usuarioRepository.findAll();

        return usuarios.stream()
                .map(u -> new UsuarioResponse(
                        u.getId_usuario(),     // ✔ getter real
                        u.getNombre(),
                        u.getCorreo(),
                        u.getRol().name()
                ))
                .collect(Collectors.toList());
    }

    // 🔹 Cambiar rol de un usuario
    public UsuarioResponse cambiarRol(Long idUsuario, String nuevoRol) {

        Usuario usuario = usuarioRepository.findById(idUsuario)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Usuario no encontrado"
                ));

        Rol rolEnum;
        try {
            rolEnum = Rol.valueOf(nuevoRol.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Rol inválido. Usa USER o ADMIN"
            );
        }

        usuario.setRol(rolEnum);
        usuarioRepository.save(usuario);

        return new UsuarioResponse(
                usuario.getId_usuario(),
                usuario.getNombre(),
                usuario.getCorreo(),
                usuario.getRol().name()
        );
    }

    public void eliminarUsuario(Long idUsuario) {
        if (!usuarioRepository.existsById(idUsuario)) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Usuario no encontrado"
            );
        }   

    usuarioRepository.deleteById(idUsuario);
    }

}
