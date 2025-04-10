package utd.ti.esb_service.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
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
import org.springframework.web.reactive.function.client.WebClientResponseException;

import utd.ti.esb_service.utils.Auth;

@RestController
@RequestMapping("/api/esb2")
public class ESBController extends BaseController {
    private final Auth auth;
    private final WebClient usersWebClient; // Add this line
    private final WebClient clientesWebClient;
    private final WebClient emailWebClient;
    private final WebClient productosWebClient;
    private final WebClient ordenesWebClient;

    public ESBController(Auth auth, WebClient.Builder webClientBuilder,
        @Value("https://usuario-production-cf6f.up.railway.app") String usersServiceUrl,
        @Value("https://clientes-production-45be.up.railway.app") String clientesServiceUrl,
        @Value("https://notificaciones-production.up.railway.app") String emailServiceUrl,
        @Value("https://producto-production.up.railway.app") String productosServiceUrl,
        @Value("https://ordenes-production-14e0.up.railway.app") String ordenesServiceUrl) {
        this.auth = auth;
        this.usersWebClient = webClientBuilder.baseUrl(usersServiceUrl).build(); // Add this line
        this.clientesWebClient = webClientBuilder.baseUrl(clientesServiceUrl).build();
        this.emailWebClient = webClientBuilder.baseUrl(emailServiceUrl).build();
        this.productosWebClient = webClientBuilder.baseUrl(productosServiceUrl).build();
        this.ordenesWebClient = webClientBuilder.baseUrl(ordenesServiceUrl).build();
    }

    // ENDPOINTS PARA EL SERVICIO DE CLIENTES
    
    @GetMapping("/clientes")
    public ResponseEntity<?> getAllClientes(@RequestHeader(HttpHeaders.AUTHORIZATION) String token) {
        System.out.println("Token recibido en getAllClientes: " + token);

        // Validar el token
        if (!auth.validateToken(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Token inválido o expirado"));
        }

        try {
            String response = clientesWebClient.get()
                    .uri("/clientes")
                    .header(HttpHeaders.AUTHORIZATION, token)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return handleWebClientException(e, "obtener todos los clientes");
        }
    }

    @PostMapping("/promote")
    public ResponseEntity<?> promoteUser(
            @RequestBody Map<String, String> requestBody,
            @RequestHeader(HttpHeaders.AUTHORIZATION) String token) {
        
        // Validar el token
        if (!auth.validateToken(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Token inválido o expirado"));
        }
    
        try {
            // Reenviar la solicitud al servicio de usuarios
            String response = usersWebClient.post()
                    .uri("/app/users/promote")
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                    .header(HttpHeaders.AUTHORIZATION, token)
                    .bodyValue(requestBody)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();
    
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return handleWebClientException(e, "promover usuario");
        }
    }

    @GetMapping("/clientes/{id}")
    public ResponseEntity<?> getClienteById(
            @PathVariable Long id,
            @RequestHeader(HttpHeaders.AUTHORIZATION) String token) {
        
        // Validar el token
        if (!auth.validateToken(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Token inválido"));
        }

        try {
            String response = clientesWebClient.get()
                    .uri("/clientes/" + id)
                    .header(HttpHeaders.AUTHORIZATION, token)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return handleWebClientException(e, "obtener cliente id=" + id);
        }
    }

    @PostMapping("/clientes")
    public ResponseEntity<?> createCliente(
            @RequestBody Object cliente,
            @RequestHeader(HttpHeaders.AUTHORIZATION) String token) {
        
        // Validar el token
        if (!auth.validateToken(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Token inválido o expirado"));
        }

        try {
            System.out.println("Enviando datos de cliente para crear: " + cliente);
            String response = clientesWebClient.post()
                    .uri("/clientes")
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                    .header(HttpHeaders.AUTHORIZATION, token)
                    .bodyValue(cliente)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();
                    
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return handleWebClientException(e, "crear cliente");
        }
    }

    @PutMapping("/clientes/{id}")
    public ResponseEntity<?> updateCliente(
            @PathVariable Long id,
            @RequestBody Object cliente,
            @RequestHeader(HttpHeaders.AUTHORIZATION) String token) {
        
        // Validar el token
        if (!auth.validateToken(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Token inválido o expirado"));
        }

        try {
            System.out.println("Enviando datos de cliente para actualizar: " + cliente);
            String response = clientesWebClient.put()
                    .uri("/clientes/" + id)
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                    .header(HttpHeaders.AUTHORIZATION, token)
                    .bodyValue(cliente)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();
                    
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return handleWebClientException(e, "actualizar cliente id=" + id);
        }
    }

    @DeleteMapping("/clientes/{id}")
    public ResponseEntity<?> deleteCliente(
            @PathVariable Long id,
            @RequestHeader(HttpHeaders.AUTHORIZATION) String token) {
        
        // Validar el token
        if (!auth.validateToken(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Token inválido o expirado"));
        }

        try {
            System.out.println("Enviando solicitud para eliminar cliente con ID: " + id);
            String response = clientesWebClient.delete()
                    .uri("/clientes/" + id)
                    .header(HttpHeaders.AUTHORIZATION, token)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();
                    
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return handleWebClientException(e, "eliminar cliente id=" + id);
        }
    }

    @PostMapping("/clientes/{id}/enviar-email")
    public ResponseEntity<?> enviarInformacionClienteEmail(
            @PathVariable Long id,
            @RequestBody Map<String, String> body,
            @RequestHeader(HttpHeaders.AUTHORIZATION) String token) {
        
        // Validar el token
        if (!auth.validateToken(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Token inválido o expirado"));
        }

        // Validar que se proporcionó el correo electrónico
        String email = body.get("email");
        if (email == null || email.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", "El campo [email] es requerido"));
        }

        try {
            // Obtener información del cliente por ID
            String clienteResponse;
            try {
                clienteResponse = clientesWebClient.get()
                        .uri("/clientes/" + id)
                        .header(HttpHeaders.AUTHORIZATION, token)
                        .retrieve()
                        .bodyToMono(String.class)
                        .block();
            } catch (WebClientResponseException e) {
                if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
                    return ResponseEntity.status(HttpStatus.NOT_FOUND)
                            .body(Map.of("error", "El recurso solicitado no existe"));
                }
                throw e;
            }

            // Preparar datos para el servicio de email
            Map<String, Object> emailData = new HashMap<>();
            emailData.put("to", email);
            emailData.put("subject", "Información del Cliente #" + id);
            emailData.put("message", clienteResponse);
            emailData.put("text", "Detalles del cliente #" + id + ": " + clienteResponse);
            emailData.put("name", "Estimado Usuario");

            // Enviar email con la información del cliente
            String emailResponse = emailWebClient.post()
                    .uri("/api/email/send")
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                    .bodyValue(emailData)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            return ResponseEntity.ok(Map.of(
                "message", "Información del cliente enviada por correo electrónico",
                "emailResponse", emailResponse
            ));
        } catch (Exception e) {
            return handleWebClientException(e, "enviar información cliente id=" + id);
        }
    }

    // ENDPOINTS PARA EL SERVICIO DE PRODUCTOS
    
    @GetMapping("/productos")
    public ResponseEntity<?> getAllProductos(@RequestHeader(HttpHeaders.AUTHORIZATION) String token) {
        // Validar el token
        if (!auth.validateToken(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Token inválido o expirado"));
        }

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

    @GetMapping("/productos/{id}")
    public ResponseEntity<?> getProductoById(
            @PathVariable Long id,
            @RequestHeader(HttpHeaders.AUTHORIZATION) String token) {
        
        // Validar el token
        if (!auth.validateToken(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Token inválido o expirado"));
        }

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

    @PostMapping("/productos")
    public ResponseEntity<?> createProducto(
            @RequestBody Object producto,
            @RequestHeader(HttpHeaders.AUTHORIZATION) String token) {
        
        // Validar el token
        if (!auth.validateToken(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Token inválido o expirado"));
        }

        try {
            System.out.println("Enviando datos de producto para crear: " + producto);
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

    @PutMapping("/productos/{id}")
    public ResponseEntity<?> updateProducto(
            @PathVariable Long id,
            @RequestBody Object producto,
            @RequestHeader(HttpHeaders.AUTHORIZATION) String token) {
        
        // Validar el token
        if (!auth.validateToken(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Token inválido o expirado"));
        }

        try {
            System.out.println("Enviando datos de producto para actualizar: " + producto);
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

    @DeleteMapping("/productos/{id}")
    public ResponseEntity<?> deleteProducto(
            @PathVariable Long id,
            @RequestHeader(HttpHeaders.AUTHORIZATION) String token) {
        
        // Validar el token
        if (!auth.validateToken(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Token inválido o expirado"));
        }

        try {
            System.out.println("Enviando solicitud para eliminar producto con ID: " + id);
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
    
    // ENDPOINTS PARA CATEGORÍAS
    
        // Crear una nueva categoría
        @PostMapping("/categorias")
        public ResponseEntity<?> createCategoria(
                @RequestBody Object categoria,
                @RequestHeader(HttpHeaders.AUTHORIZATION) String token) {
            
            // Validar el token
            if (!auth.validateToken(token)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("error", "Token inválido o expirado"));
            }
    
            try {
                logger.info("Enviando datos de categoría para crear: {}", categoria);
                String response = productosWebClient.post()
                        .uri("/categorias")
                        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .header(HttpHeaders.AUTHORIZATION, token)
                        .bodyValue(categoria)
                        .retrieve()
                        .bodyToMono(String.class)
                        .block();
                        
                return ResponseEntity.ok(response);
            } catch (Exception e) {
                return handleWebClientException(e, "crear categoría");
            }
        }
    
        // Obtener una categoría por ID
        @GetMapping("/categorias/{id}")
        public ResponseEntity<?> getCategoriaPorId(
                @PathVariable Long id,
                @RequestHeader(HttpHeaders.AUTHORIZATION) String token) {
            
            // Validar el token
            if (!auth.validateToken(token)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("error", "Token inválido o expirado"));
            }
    
            try {
                String response = productosWebClient.get()
                        .uri("/categorias/" + id)
                        .header(HttpHeaders.AUTHORIZATION, token)
                        .retrieve()
                        .bodyToMono(String.class)
                        .block();
    
                return ResponseEntity.ok(response);
            } catch (Exception e) {
                return handleWebClientException(e, "obtener categoría id=" + id);
            }
        }
    
        // Obtener todas las categorías
        @GetMapping("/categorias")
        public ResponseEntity<?> getAllCategorias(@RequestHeader(HttpHeaders.AUTHORIZATION) String token) {
            // Validar el token
            if (!auth.validateToken(token)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("error", "Token inválido o expirado"));
            }
        
            try {
                String response = productosWebClient.get()
                        .uri("/categorias")
                        .header(HttpHeaders.AUTHORIZATION, token)
                        .retrieve()
                        .bodyToMono(String.class)
                        .block();
        
                return ResponseEntity.ok(response);
            } catch (Exception e) {
                return handleWebClientException(e, "obtener todas las categorías");
            }
        }
    
        // Actualizar una categoría existente
        @PutMapping("/categorias/{id}")
        public ResponseEntity<?> updateCategoria(
                @PathVariable Long id,
                @RequestBody Object categoria,
                @RequestHeader(HttpHeaders.AUTHORIZATION) String token) {
            
            // Validar el token
            if (!auth.validateToken(token)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("error", "Token inválido o expirado"));
            }
    
            try {
                logger.info("Enviando datos para actualizar categoría ID: {}", id);
                String response = productosWebClient.put()
                        .uri("/categorias/" + id)
                        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .header(HttpHeaders.AUTHORIZATION, token)
                        .bodyValue(categoria)
                        .retrieve()
                        .bodyToMono(String.class)
                        .block();
                        
                return ResponseEntity.ok(response);
            } catch (Exception e) {
                return handleWebClientException(e, "actualizar categoría id=" + id);
            }
        }
    
        // Eliminar una categoría
        @DeleteMapping("/categorias/{id}")
        public ResponseEntity<?> deleteCategoria(
                @PathVariable Long id,
                @RequestHeader(HttpHeaders.AUTHORIZATION) String token) {
            
            // Validar el token
            if (!auth.validateToken(token)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("error", "Token inválido o expirado"));
            }
    
            try {
                logger.info("Enviando solicitud para eliminar categoría con ID: {}", id);
                String response = productosWebClient.delete()
                        .uri("/categorias/" + id)
                        .header(HttpHeaders.AUTHORIZATION, token)
                        .retrieve()
                        .bodyToMono(String.class)
                        .block();
                        
                return ResponseEntity.ok(response);
            } catch (Exception e) {
                return handleWebClientException(e, "eliminar categoría id=" + id);
            }
        }

    // ENDPOINTS PARA EL SERVICIO DE CARRITO
    
    @GetMapping("/carrito")
    public ResponseEntity<?> getCarrito(@RequestHeader(HttpHeaders.AUTHORIZATION) String token) {
        // Validar el token
        if (!auth.validateToken(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Token inválido o expirado"));
        }

        try {
            String response = ordenesWebClient.get()
                    .uri("/carrito")
                    .header(HttpHeaders.AUTHORIZATION, token)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return handleWebClientException(e, "obtener carrito");
        }
    }

    @PostMapping("/carrito/agregar")
    public ResponseEntity<?> agregarAlCarrito(
            @RequestBody Object item,
            @RequestHeader(HttpHeaders.AUTHORIZATION) String token) {
        
        // Validar el token
        if (!auth.validateToken(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Token inválido o expirado"));
        }

        try {
            String response = ordenesWebClient.post()
                    .uri("/carrito/agregar")
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                    .header(HttpHeaders.AUTHORIZATION, token)
                    .bodyValue(item)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();
                    
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return handleWebClientException(e, "agregar al carrito");
        }
    }

    @PutMapping("/carrito/item/{itemId}")
    public ResponseEntity<?> actualizarItemCarrito(
            @PathVariable Long itemId,
            @RequestBody Object datos,
            @RequestHeader(HttpHeaders.AUTHORIZATION) String token) {
        
        // Validar el token
        if (!auth.validateToken(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Token inválido o expirado"));
        }

        try {
            String response = ordenesWebClient.put()
                    .uri("/carrito/item/" + itemId)
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                    .header(HttpHeaders.AUTHORIZATION, token)
                    .bodyValue(datos)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();
                    
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return handleWebClientException(e, "actualizar item del carrito id=" + itemId);
        }
    }

    @DeleteMapping("/carrito/item/{itemId}")
    public ResponseEntity<?> eliminarItemCarrito(
            @PathVariable Long itemId,
            @RequestHeader(HttpHeaders.AUTHORIZATION) String token) {
        
        // Validar el token
        if (!auth.validateToken(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Token inválido o expirado"));
        }

        try {
            String response = ordenesWebClient.delete()
                    .uri("/carrito/item/" + itemId)
                    .header(HttpHeaders.AUTHORIZATION, token)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();
                    
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return handleWebClientException(e, "eliminar item del carrito id=" + itemId);
        }
    }

    @DeleteMapping("/carrito/vaciar")
    public ResponseEntity<?> vaciarCarrito(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String token) {
        
        // Validar el token
        if (!auth.validateToken(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Token inválido o expirado"));
        }

        try {
            String response = ordenesWebClient.delete()
                    .uri("/carrito/vaciar")
                    .header(HttpHeaders.AUTHORIZATION, token)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();
                    
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return handleWebClientException(e, "vaciar carrito");
        }
    }

    // ENDPOINTS PARA EL SERVICIO DE ÓRDENES
    
    @PostMapping("/ordenes")
    public ResponseEntity<?> crearOrden(
            @RequestBody Object orden,
            @RequestHeader(HttpHeaders.AUTHORIZATION) String token) {
        
        // Validar el token
        if (!auth.validateToken(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Token inválido o expirado"));
        }

        try {
            String response = ordenesWebClient.post()
                    .uri("/ordenes")
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                    .header(HttpHeaders.AUTHORIZATION, token)
                    .bodyValue(orden)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();
                    
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return handleWebClientException(e, "crear orden");
        }
    }

    @GetMapping("/ordenes")
    public ResponseEntity<?> getOrdenesUsuario(@RequestHeader(HttpHeaders.AUTHORIZATION) String token) {
        // Validar el token
        if (!auth.validateToken(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Token inválido o expirado"));
        }

        try {
            String response = ordenesWebClient.get()
                    .uri("/ordenes")
                    .header(HttpHeaders.AUTHORIZATION, token)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return handleWebClientException(e, "obtener órdenes del usuario");
        }
    }

    @GetMapping("/ordenes/{id}")
    public ResponseEntity<?> getOrdenPorId(
            @PathVariable Long id,
            @RequestHeader(HttpHeaders.AUTHORIZATION) String token) {
        
        // Validar el token
        if (!auth.validateToken(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Token inválido o expirado"));
        }

        try {
            String response = ordenesWebClient.get()
                    .uri("/ordenes/" + id)
                    .header(HttpHeaders.AUTHORIZATION, token)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return handleWebClientException(e, "obtener orden id=" + id);
        }
    }

    @PutMapping("/ordenes/{id}/estado")
    public ResponseEntity<?> actualizarEstadoOrden(
            @PathVariable Long id,
            @RequestBody Object datos,
            @RequestHeader(HttpHeaders.AUTHORIZATION) String token) {
        
        // Validar el token
        if (!auth.validateToken(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Token inválido o expirado"));
        }

        try {
            String response = ordenesWebClient.put()
                    .uri("/ordenes/" + id + "/estado")
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                    .header(HttpHeaders.AUTHORIZATION, token)
                    .bodyValue(datos)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();
                    
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return handleWebClientException(e, "actualizar estado de la orden id=" + id);
        }
    }

    @GetMapping("/ordenes/admin")
    public ResponseEntity<?> getAllOrdenes(@RequestHeader(HttpHeaders.AUTHORIZATION) String token) {
        // Validar el token
        if (!auth.validateToken(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Token inválido o expirado"));
        }

        try {
            String response = ordenesWebClient.get()
                    .uri("/ordenes/admin")
                    .header(HttpHeaders.AUTHORIZATION, token)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return handleWebClientException(e, "obtener todas las órdenes");
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> credentials) {
        try {
            // Forward login request to the users service
            String response = usersWebClient.post()
                    .uri("/app/users/login")
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(credentials)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();
                    
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return handleWebClientException(e, "login");
        }
    }
}




