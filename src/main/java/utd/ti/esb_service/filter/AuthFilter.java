package utd.ti.esb_service.filter;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import org.springframework.lang.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import utd.ti.esb_service.utils.Auth;

@Component
@Order(1)
public class AuthFilter extends OncePerRequestFilter {

    private final Auth auth;
    private final ObjectMapper objectMapper;
    private final Logger logger = LoggerFactory.getLogger(AuthFilter.class);
    
    // Rutas que no requieren autenticación
    private final List<String> publicPaths = Arrays.asList("/api/esb2/health", "/api/esb2/login");
    
    @Autowired
    public AuthFilter(Auth auth, ObjectMapper objectMapper) {
        this.auth = auth;
        this.objectMapper = objectMapper;
    }

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request, 
            @NonNull HttpServletResponse response, 
            @NonNull FilterChain filterChain)
            throws ServletException, IOException {
        
        String path = request.getRequestURI();
        
        // Verificar si la ruta es pública
        if (publicPaths.stream().anyMatch(path::equals)) {
            filterChain.doFilter(request, response);
            return;
        }
        
        String token = request.getHeader(HttpHeaders.AUTHORIZATION);
        logger.debug("Validando token: {}", token != null ? "token presente" : "token ausente");
        
        // Si la solicitud no tiene token o el token es inválido
        if (token == null || !auth.validateToken(token)) {
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            
            String errorJson = objectMapper.writeValueAsString(
                Map.of("error", "Token inválido o expirado")
            );
            
            response.getWriter().write(errorJson);
            logger.warn("Token inválido o expirado en la solicitud a: {}", request.getRequestURI());
            return;
        }
        
        logger.debug("Token válido para la solicitud a: {}", request.getRequestURI());
        filterChain.doFilter(request, response);
    }
}