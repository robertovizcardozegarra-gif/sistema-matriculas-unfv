package com.rckrm.backenddisenobd.seguridad;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class AdminInitializer implements ApplicationRunner {

    private final RolRepository roles;
    private final UsuarioRepository usuarios;
    private final PasswordEncoder passwordEncoder;
    private final String username;
    private final String password;
    private final String email;

    public AdminInitializer(RolRepository roles, UsuarioRepository usuarios, PasswordEncoder passwordEncoder,
                            @Value("${app.admin.username}") String username,
                            @Value("${app.admin.password}") String password,
                            @Value("${app.admin.email}") String email) {
        this.roles = roles;
        this.usuarios = usuarios;
        this.passwordEncoder = passwordEncoder;
        this.username = username;
        this.password = password;
        this.email = email;
    }

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        if (password == null || password.isBlank() || usuarios.findByNombreUsuarioIgnoreCase(username).isPresent()) {
            return;
        }

        Rol admin = roles.findByNombreIgnoreCase("ADMIN")
                .orElseGet(() -> roles.save(new Rol("ADMIN", "Administrador del sistema")));
        Usuario usuario = new Usuario(username, passwordEncoder.encode(password), email, "Administrador", "UNFV");
        usuario.agregarRol(admin);
        usuarios.save(usuario);
    }
}
