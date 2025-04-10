package utd.ti.esb_service.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;

import utd.ti.esb_service.model.User;

@RestController
@RequestMapping("/api/esb2/users")
public class UserController extends BaseController {
    
    private final WebClient usersWebClient;
    
    public UserController(WebClient.Builder webClientBuilder,
                        @Value("https://usuario-production-cf6f.up.railway.app") String usersServiceUrl) {
        this.usersWebClient = webClientBuilder.baseUrl(usersServiceUrl).build();
    }
    
    @PostMapping
    public ResponseEntity<?> createUser(@RequestBody User user,
                        @RequestHeader(HttpHeaders.AUTHORIZATION) String token) {
        try {
            System.out.println("Sending user data: " + user);
            String response = usersWebClient.post()
                    .uri("/app/users/create")
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                    .header(HttpHeaders.AUTHORIZATION, token)
                    .bodyValue(user)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return handleWebClientException(e, "crear usuario");
        }
    }
    
    @GetMapping
    public ResponseEntity<?> getAllUsers(@RequestHeader(HttpHeaders.AUTHORIZATION) String token) {
        try {
            String response = usersWebClient.get()
                    .uri("/app/users/all")
                    .header(HttpHeaders.AUTHORIZATION, token)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return handleWebClientException(e, "obtener todos los usuarios");
        }
    }
    
    @PatchMapping("/{id}")
    public ResponseEntity<?> updateUser(@PathVariable Long id,
                @RequestBody User user,
                @RequestHeader(HttpHeaders.AUTHORIZATION) String token) {
        try {
            user.setId(id); // Asegurar que el ID en el cuerpo coincida con el de la URL
            String response = usersWebClient.patch()
                    .uri("/app/users/update/" + id)
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                    .header(HttpHeaders.AUTHORIZATION, token)
                    .bodyValue(user)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return handleWebClientException(e, "actualizar usuario id=" + id);
        }
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id,
    @RequestHeader(HttpHeaders.AUTHORIZATION) String token) {
        try {
            String response = usersWebClient.patch()
                    .uri("/app/users/delete/" + id)
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                    .header(HttpHeaders.AUTHORIZATION, token)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return handleWebClientException(e, "eliminar usuario id=" + id);
        }
    }
}