package cl.huertohogar.backend.security;

import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;

import cl.huertohogar.backend.repository.UsuarioRepository;

@Service
@RequiredArgsConstructor
public class UsuarioDetailsService implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;

    @Override
    public UserDetails loadUserByUsername(String correo) throws UsernameNotFoundException {
        return usuarioRepository.findByCorreo(correo)
                .map(UsuarioDetails::new)
                .orElseThrow(() ->
                        new UsernameNotFoundException("Usuario no encontrado: " + correo)
                );
    }
}
