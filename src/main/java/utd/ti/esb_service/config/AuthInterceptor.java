package utd.ti.esb_service.config;

import java.util.Map;

import org.slf4j.Logger;
import org.springframework.lang.NonNull;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import utd.ti.esb_service.utils.Auth;

@Component
public class AuthInterceptor implements HandlerInterceptor {
    
    private final Auth auth;
    private final ObjectMapper objectMapper;
    private final Logger logger = LoggerFactory.getLogger(AuthInterceptor.class);
    
    @Autowired
    public AuthInterceptor(Auth auth, ObjectMapper objectMapper) {
        this.auth = auth;
        this.objectMapper = objectMapper;
    }
    
    @Override
    public boolean preHandle(@ NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull Object handler) throws Exception {
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
            return false;
        }
        
        logger.debug("Token válido para la solicitud a: {}", request.getRequestURI());
        return true;
    }
}