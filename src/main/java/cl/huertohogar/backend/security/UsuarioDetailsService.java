package cl.huertohogar.backend.security;

import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;

import cl.huertohogar.backend.repository.UsuarioRepository;

/**
 * Servicio que carga un usuario desde la base de datos
 * cuando Spring Security lo necesita.
 * Se usa en:
 *  - Login
 *  - Validación de token JWT
 */
@Service
@RequiredArgsConstructor
public class UsuarioDetailsService implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;

    /**
     * Busca al usuario por correo.
     * Si existe → lo envuelve en UsuarioDetails.
     * Si no existe → lanza error (Spring lo interpreta como login fallido).
     */
    @Override
    public UserDetails loadUserByUsername(String correo) throws UsernameNotFoundException {
        return usuarioRepository.findByCorreo(correo)
                .map(UsuarioDetails::new)
                .orElseThrow(() ->
                        new UsernameNotFoundException("Usuario no encontrado: " + correo)
                );
    }
}
