package com.aisecream.controller;

import com.aisecream.dto.LoginRequest;
import com.aisecream.dto.LoginResponse;
import com.aisecream.model.Usuario;
import com.aisecream.security.JwtAuthFilter;
import com.aisecream.security.JwtService;
import com.aisecream.service.UsuarioService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Duration;
import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UsuarioService usuarioService;
    private final JwtService jwtService;
    private final long expirationMs;

    public AuthController(
            AuthenticationManager authenticationManager,
            UsuarioService usuarioService,
            JwtService jwtService,
            @Value("${jwt.expiration-ms}") long expirationMs
    ) {
        this.authenticationManager = authenticationManager;
        this.usuarioService = usuarioService;
        this.jwtService = jwtService;
        this.expirationMs = expirationMs;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request, HttpServletResponse response) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.email(), request.senha())
            );
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "E-mail ou senha inválidos."));
        } catch (AuthenticationException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "Não foi possível autenticar."));
        }
        Usuario usuario = usuarioService.buscarPorEmail(request.email());
        String token = jwtService.generateToken(usuario);
        ResponseCookie cookie = ResponseCookie.from(JwtAuthFilter.COOKIE_ACCESS_TOKEN, token)
                .httpOnly(true)
                .path("/")
                .maxAge(Duration.ofMillis(expirationMs))
                .sameSite("Lax")
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
        long expiresInSeconds = Math.max(1L, expirationMs / 1000);
        return ResponseEntity.ok(new LoginResponse(token, "Bearer", expiresInSeconds, usuario.getPerfil().name()));
    }
}
