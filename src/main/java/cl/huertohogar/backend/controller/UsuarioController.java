package cl.huertohogar.backend.controller;

import cl.huertohogar.backend.dto.UsuarioResponse;
import cl.huertohogar.backend.service.UsuarioService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/usuarios")
@CrossOrigin(origins = "*")
public class UsuarioController {

    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    // GET /api/usuarios  (solo ADMIN)
    @GetMapping
    public ResponseEntity<List<UsuarioResponse>> listarUsuarios() {
        return ResponseEntity.ok(usuarioService.listarTodos());
    }

    // PUT /api/usuarios/{id}/rol?rol=ADMIN
    @PutMapping("/{id}/rol")
    public ResponseEntity<UsuarioResponse> cambiarRol(
            @PathVariable Long id,
            @RequestParam String rol
    ) {
        return ResponseEntity.ok(usuarioService.cambiarRol(id, rol));
    }

    // DELETE /api/usuarios/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarUsuario(@PathVariable Long id) {
        usuarioService.eliminarUsuario(id);
        return ResponseEntity.noContent().build(); // 204 sin cuerpo
    }
        @GetMapping("/perfil")
        public ResponseEntity<UsuarioResponse> perfil() {
            return ResponseEntity.ok(usuarioService.obtenerPerfilActual());
        }

        @PutMapping("/perfil")
        public ResponseEntity<UsuarioResponse> actualizarPerfil(@RequestBody UsuarioResponse datos) {
            return ResponseEntity.ok(usuarioService.actualizarPerfilActual(datos));
        }



}
