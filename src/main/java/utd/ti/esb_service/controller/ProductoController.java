package utd.ti.esb_service.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;

@RestController
@RequestMapping("/api/esb2/productos-esb")
public class ProductoController extends BaseController {
    
    private final WebClient productosWebClient;
    
    public ProductoController(WebClient.Builder webClientBuilder,
                            @Value("https://producto-production.up.railway.app") String productosServiceUrl) {
        this.productosWebClient = webClientBuilder.baseUrl(productosServiceUrl).build();
    }
    
    @GetMapping
    public ResponseEntity<?> getAllProductos(@RequestHeader(HttpHeaders.AUTHORIZATION) String token) {
        try {
            String response = productosWebClient.get()
                    .uri("/productos")
                    .header(HttpHeaders.AUTHORIZATION, token)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return handleWebClientException(e, "obtener todos los productos");
        }
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<?> getProductoById(@PathVariable Long id,
    @RequestHeader(HttpHeaders.AUTHORIZATION) String token) {
        try {
            String response = productosWebClient.get()
                    .uri("/productos/" + id)
                    .header(HttpHeaders.AUTHORIZATION, token)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return handleWebClientException(e, "obtener producto id=" + id);
        }
    }
    
    @PostMapping
    public ResponseEntity<?> createProducto(@RequestBody Object producto,
            @RequestHeader(HttpHeaders.AUTHORIZATION) String token) {
        try {
            String response = productosWebClient.post()
                    .uri("/productos")
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                    .header(HttpHeaders.AUTHORIZATION, token)
                    .bodyValue(producto)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();
                    
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return handleWebClientException(e, "crear producto");
        }
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<?> updateProducto(@PathVariable Long id,
        @RequestBody Object producto,
        @RequestHeader(HttpHeaders.AUTHORIZATION) String token) {
        try {
            String response = productosWebClient.put()
                    .uri("/productos/" + id)
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                    .header(HttpHeaders.AUTHORIZATION, token)
                    .bodyValue(producto)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();
                    
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return handleWebClientException(e, "actualizar producto id=" + id);
        }
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteProducto(@PathVariable Long id,
    @RequestHeader(HttpHeaders.AUTHORIZATION) String token) {
        try {
            String response = productosWebClient.delete()
                    .uri("/productos/" + id)
                    .header(HttpHeaders.AUTHORIZATION, token)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();
                    
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return handleWebClientException(e, "eliminar producto id=" + id);
        }
    }
}