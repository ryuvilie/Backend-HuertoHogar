package cl.huertohogar.backend.service;

import cl.huertohogar.backend.dto.UsuarioResponse;
import cl.huertohogar.backend.model.Rol;
import cl.huertohogar.backend.model.Usuario;
import cl.huertohogar.backend.repository.UsuarioRepository;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
public class UsuarioServiceTest {

    @InjectMocks
    private UsuarioService usuarioService;

    @Mock
    private UsuarioRepository usuarioRepository;

    private Usuario buildUsuario(Long id, String nombre, String correo, Rol rol) {
        Usuario u = new Usuario();
        u.setId_usuario(id);
        u.setNombre(nombre);
        u.setCorreo(correo);
        u.setRol(rol);
        u.setClaveHash("hash");
        return u;
    }

    // 1) listarTodos should map entities to DTO correctly
    @Test
    void testListarTodosMapsToDto() {
        List<Usuario> usuarios = List.of(
                buildUsuario(1L, "Alice", "alice@example.com", Rol.USER),
                buildUsuario(2L, "Bob", "bob@example.com", Rol.ADMIN)
        );
        when(usuarioRepository.findAll()).thenReturn(usuarios);

        List<UsuarioResponse> result = usuarioService.listarTodos();

        assertEquals(2, result.size());
        assertEquals(1L, result.get(0).getId());
        assertEquals("Alice", result.get(0).getNombre());
        assertEquals("alice@example.com", result.get(0).getCorreo());
        assertEquals("USER", result.get(0).getRol());
        assertEquals("ADMIN", result.get(1).getRol());
        verify(usuarioRepository, times(1)).findAll();
        verifyNoMoreInteractions(usuarioRepository);
    }

    
    @Test
    void testListarTodosEmpty() {
        when(usuarioRepository.findAll()).thenReturn(List.of());

        List<UsuarioResponse> result = usuarioService.listarTodos();

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(usuarioRepository, times(1)).findAll();
        verifyNoMoreInteractions(usuarioRepository);
    }

    
    @Test
    void testCambiarRolSuccess() {
        Usuario usuario = buildUsuario(10L, "Carol", "carol@example.com", Rol.USER);
        when(usuarioRepository.findById(10L)).thenReturn(Optional.of(usuario));
        when(usuarioRepository.save(any(Usuario.class))).thenAnswer(inv -> inv.getArgument(0));

        UsuarioResponse response = usuarioService.cambiarRol(10L, "ADMIN");

        assertEquals(10L, response.getId());
        assertEquals("Carol", response.getNombre());
        assertEquals("carol@example.com", response.getCorreo());
        assertEquals("ADMIN", response.getRol());
        assertEquals(Rol.ADMIN, usuario.getRol());
        verify(usuarioRepository, times(1)).findById(10L);
        verify(usuarioRepository, times(1)).save(usuario);
        verifyNoMoreInteractions(usuarioRepository);
    }

   
    @Test
    void testCambiarRolUsuarioNoEncontrado() {
        when(usuarioRepository.findById(77L)).thenReturn(Optional.empty());

        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () ->
                usuarioService.cambiarRol(77L, "ADMIN")
        );
        assertEquals(404, ex.getStatusCode().value());
        assertTrue(ex.getReason().contains("Usuario no encontrado"));
        verify(usuarioRepository, times(1)).findById(77L);
        verifyNoMoreInteractions(usuarioRepository);
    }

  
    @Test
    void testCambiarRolRolInvalido() {
        when(usuarioRepository.findById(5L)).thenReturn(Optional.of(buildUsuario(5L, "Dave", "dave@example.com", Rol.USER)));

        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () ->
                usuarioService.cambiarRol(5L, "MANAGER")
        );
        assertEquals(400, ex.getStatusCode().value());
        assertTrue(ex.getReason().contains("Rol inválido"));
        verify(usuarioRepository, times(1)).findById(5L);
        verifyNoMoreInteractions(usuarioRepository);
    }

    
    @Test
    void testEliminarUsuarioSuccess() {
        when(usuarioRepository.existsById(3L)).thenReturn(true);

        usuarioService.eliminarUsuario(3L);

        verify(usuarioRepository, times(1)).existsById(3L);
        verify(usuarioRepository, times(1)).deleteById(3L);
        verifyNoMoreInteractions(usuarioRepository);
    }

    
    @Test
    void testEliminarUsuarioNotFound() {
        when(usuarioRepository.existsById(9L)).thenReturn(false);

        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () ->
                usuarioService.eliminarUsuario(9L)
        );
        assertEquals(404, ex.getStatusCode().value());
        assertTrue(ex.getReason().contains("Usuario no encontrado"));
        verify(usuarioRepository, times(1)).existsById(9L);
        verifyNoMoreInteractions(usuarioRepository);
    }
}
