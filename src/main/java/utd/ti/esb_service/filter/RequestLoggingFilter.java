package utd.ti.esb_service.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.lang.NonNull;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Component
public class RequestLoggingFilter extends OncePerRequestFilter {

    private final Logger logger = LoggerFactory.getLogger(RequestLoggingFilter.class);

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request, 
            @NonNull HttpServletResponse response, 
            @NonNull FilterChain filterChain)
            throws ServletException, IOException {
        
        Instant start = Instant.now();
        String requestUri = request.getRequestURI();
        
        try {
            filterChain.doFilter(request, response);
        } finally {
            Instant end = Instant.now();
            long timeElapsed = ChronoUnit.MILLIS.between(start, end);
            int status = response.getStatus();
            
            logger.info(String.format(
                "Request completed: [%s] %s - %d (%d ms)",
                request.getMethod(), requestUri, status, timeElapsed
            ));
        }
    }
}