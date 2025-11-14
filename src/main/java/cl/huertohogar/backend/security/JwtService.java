package cl.huertohogar.backend.security;

import java.util.Date;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.*;  // librería JWT

/**
 * Servicio que genera, valida y analiza los tokens JWT.
 * Se usa tanto en el login/registro como en el filtro de seguridad.
 */
@Service
public class JwtService {

    // Se obtiene desde application.properties
    @Value("${app.jwt.secret}")
    private String secret;

    // Tiempo de expiración en milisegundos (1 hora por defecto)
    @Value("${app.jwt.expirationMs}")
    private long expirationMs;

    /**
     * Genera un token JWT nuevo usando:
     * - correo del usuario como "subject"
     * - rol del usuario como "claim"
     */
    public String generateToken(String username, String role) {

        // Fecha de creación del token
        Date now = new Date();

        // Fecha de expiración (now + expirationMs)
        Date exp = new Date(now.getTime() + expirationMs);

        // Construcción del token JWT
        return Jwts.builder()
            .setSubject(username)      // identifica al usuario
            .claim("role", role)       // guardamos el rol dentro del token
            .setIssuedAt(now)          // fecha de emisión
            .setExpiration(exp)        // fecha de expiración
            .signWith(
                io.jsonwebtoken.security.Keys.hmacShaKeyFor(secret.getBytes())
            )                          // firma del token con clave secreta
            .compact();                // convierte en string final
    }

    /**
     * Extrae el "username" (correo) desde el token.
     */
    public String extractUsername(String token) {
        return parse(token).getBody().getSubject();
    }

    /**
     * Extrae el rol desde el claim "role" del token.
     */
    public String extractRole(String token) {
        Object v = parse(token).getBody().get("role");
        return v == null ? null : v.toString();
    }

    /**
     * Verifica si el token es válido:
     * - firma correcta
     * - no expirado
     * - formato correcto
     */
    public boolean isValid(String token) {
        try {
            parse(token);  // si falla, lanza excepción
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    /**
     * Método privado que decodifica y valida el token usando la clave secreta.
     */
    private Jws<Claims> parse(String token) {
        return Jwts.parserBuilder()
            .setSigningKey(io.jsonwebtoken.security.Keys.hmacShaKeyFor(secret.getBytes()))
            .build()
            .parseClaimsJws(token);  // valida firma + contenido
    }
}
