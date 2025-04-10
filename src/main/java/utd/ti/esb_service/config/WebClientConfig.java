package utd.ti.esb_service.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;

import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import reactor.netty.http.client.HttpClient;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

@Configuration
public class WebClientConfig {
    
    @Bean
    public WebClient.Builder webClientBuilder() {
        // Configuración del cliente HTTP con timeouts
        HttpClient httpClient = HttpClient.create()
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 10000) // 10 segundos de timeout de conexión
                .responseTimeout(Duration.ofMillis(15000)) // 15 segundos de timeout para la respuesta
                .doOnConnected(conn -> 
                    conn.addHandlerLast(new ReadTimeoutHandler(20, TimeUnit.SECONDS)) // 20 segundos de timeout de lectura
                        .addHandlerLast(new WriteTimeoutHandler(20, TimeUnit.SECONDS))); // 20 segundos de timeout de escritura
        
        // Aumentamos el tamaño del buffer para manejar respuestas grandes
        final int size = 16 * 1024 * 1024; // 16MB
        final ExchangeStrategies strategies = ExchangeStrategies.builder()
                .codecs(codecs -> codecs.defaultCodecs().maxInMemorySize(size))
                .build();
        
        return WebClient.builder()
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .exchangeStrategies(strategies);
    }
}