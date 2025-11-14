package cl.huertohogar.backend.security;

import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import cl.huertohogar.backend.model.Usuario;

/**
 * Esta clase envuelve tu objeto Usuario (de la BD)
 * y lo transforma en un formato que Spring Security entiende.
 * Spring usa este objeto para saber:
 *  - correo (username)
 *  - contraseña encriptada
 *  - roles (autoridades)
 */
public class UsuarioDetails implements UserDetails {

    private final Usuario usuario; // entidad original

    public UsuarioDetails(Usuario usuario) {
        this.usuario = usuario;
    }

    /**
     * Convierte el rol del usuario (ADMIN/USER)
     * en un formato que Spring Security usa:
     *  ROLE_ADMIN o ROLE_USER
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(
                new SimpleGrantedAuthority("ROLE_" + usuario.getRol().name())
        );
    }

    /**
     * Devuelve la contraseña encriptada guardada en la BD.
     */
    @Override
    public String getPassword() {
        return usuario.getClaveHash();
    }

    /**
     * Nuestro "username" es el correo.
     */
    @Override
    public String getUsername() {
        return usuario.getCorreo();
    }

    // Los siguientes "true" permiten que la cuenta esté activa siempre.
    @Override public boolean isAccountNonExpired() { return true; }
    @Override public boolean isAccountNonLocked() { return true; }
    @Override public boolean isCredentialsNonExpired() { return true; }
    @Override public boolean isEnabled() { return true; }

    // Método opcional: obtiene el nombre del usuario
    public String getNombre() {
        return usuario.getNombre();
    }
}
