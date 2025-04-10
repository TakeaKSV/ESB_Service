package utd.ti.esb_service.utils;

import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Value;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.security.Keys;
import javax.crypto.SecretKey;

@Component
public class Auth {

    private final Logger logger = LoggerFactory.getLogger(Auth.class);
    
    @Value("${jwt.secret:c9PcgRFL2S8n0NYQp6MZUbbxRgTRHJxjYnvux54VrnA=}")
    private String jwtSecret;
    
    private SecretKey getSecretKey() {
        // Usar directamente la clave sin intentar decodificarla como Base64
        return Keys.hmacShaKeyFor(jwtSecret.getBytes(java.nio.charset.StandardCharsets.UTF_8));
    }
    
    /**
     * Validar un token JWT
     * @param token El token a validar (incluyendo el prefijo "Bearer " si es aplicable)
     * @return true si el token es válido, false en caso contrario
     */
    public boolean validateToken(String token) {
        if (token == null || token.isEmpty()) {
            logger.warn("Token vacío o nulo");
            return false;
        }
        
        // Eliminar el prefijo "Bearer " si está presente
        String jwtToken = token;
        if (token.startsWith("Bearer ")) {
            jwtToken = token.substring(7);
        }
        
        // Verificar formato básico del token (debe tener 2 puntos)
        if (jwtToken.chars().filter(ch -> ch == '.').count() != 2) {
            logger.error("Formato de token inválido: debe contener exactamente 2 caracteres '.'");
            return false;
        }
        
        try {
            // Obtener una clave segura para validar el token
            SecretKey key = getSecretKey();
            
            // Usar la clave secreta para validar el token
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(jwtToken)
                    .getBody();
                    
            // Verificar si el token ha expirado
            if (claims.getExpiration() != null && claims.getExpiration().before(new java.util.Date())) {
                logger.warn("Token expirado");
                return false;
            }
            
            logger.info("Token válido para usuario ID: " + claims.get("id"));
            return true;
        } catch (JwtException e) {
            logger.error("Error validando token: " + e.getMessage());
            return false;
        } catch (Exception e) {
            logger.error("Error inesperado validando token: " + e.getMessage());
            return false;
        }
    }
}