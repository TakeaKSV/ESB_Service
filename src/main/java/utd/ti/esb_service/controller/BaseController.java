package utd.ti.esb_service.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.Map;

public abstract class BaseController {
    protected final Logger logger = LoggerFactory.getLogger(this.getClass());

    protected ResponseEntity<?> handleWebClientException(Exception e, String operacion) {
        logger.error("Error al " + operacion + ": " + e.getMessage(), e);
        
        if (e instanceof WebClientResponseException) {
            WebClientResponseException wcre = (WebClientResponseException) e;
            HttpStatusCode statusCode = wcre.getStatusCode();
            
            if (statusCode.equals(HttpStatus.NOT_FOUND)) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("error", "El recurso solicitado no existe"));
            } else if (statusCode.equals(HttpStatus.UNAUTHORIZED)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("error", "No autorizado para acceder a este recurso"));
            } else if (statusCode.equals(HttpStatus.FORBIDDEN)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("error", "Acceso prohibido a este recurso"));
            } else if (statusCode.equals(HttpStatus.BAD_REQUEST)) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Map.of("error", "Solicitud incorrecta: " + wcre.getResponseBodyAsString()));
            }
            
            return ResponseEntity.status(statusCode)
                    .body(Map.of("error", "Error de servicio: " + wcre.getResponseBodyAsString()));
        }
        
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Error al " + operacion + ": " + e.getMessage()));
    }
}