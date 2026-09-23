package com.rckrm.backenddisenobd.seguridad;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.List;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final TokenService tokenService;

    public AuthController(AuthenticationManager authenticationManager, TokenService tokenService) {
        this.authenticationManager = authenticationManager;
        this.tokenService = tokenService;
    }

    @PostMapping("/login")
    public LoginResponse login(@Valid @RequestBody LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                UsernamePasswordAuthenticationToken.unauthenticated(request.usuario(), request.clave()));
        TokenService.TokenEmitido emitido = tokenService.emitir(authentication);
        return new LoginResponse(emitido.token(), "Bearer", emitido.expiraEn(), request.usuario(), emitido.roles());
    }

    @GetMapping("/me")
    public SesionResponse sesion(Authentication authentication) {
        return new SesionResponse(authentication.getName(), authentication.getAuthorities().stream()
                .map(Object::toString).toList());
    }

    public record LoginRequest(@NotBlank String usuario, @NotBlank String clave) {
    }

    public record LoginResponse(String token, String tipo, Instant expiraEn, String usuario, List<String> roles) {
    }

    public record SesionResponse(String usuario, List<String> roles) {
    }
}
