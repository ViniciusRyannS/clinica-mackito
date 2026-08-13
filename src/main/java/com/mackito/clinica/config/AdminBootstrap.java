package com.mackito.clinica.config;

import com.mackito.clinica.model.PerfilUsuario;
import com.mackito.clinica.model.Usuario;
import com.mackito.clinica.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class AdminBootstrap implements ApplicationRunner {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final String email;
    private final String senha;

    public AdminBootstrap(
            UsuarioRepository usuarioRepository,
            PasswordEncoder passwordEncoder,
            @Value("${app.bootstrap.admin.email:}") String email,
            @Value("${app.bootstrap.admin.password:}") String senha) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.email = email;
        this.senha = senha;
    }

    @Override
    public void run(ApplicationArguments args) {
        boolean emailInformado = email != null && !email.isBlank();
        boolean senhaInformada = senha != null && !senha.isBlank();

        if (!emailInformado && !senhaInformada) {
            return;
        }
        if (!emailInformado || !senhaInformada) {
            throw new IllegalStateException(
                    "ADMIN_INITIAL_EMAIL e ADMIN_INITIAL_PASSWORD devem ser informados juntos");
        }
        if (senha.length() < 12) {
            throw new IllegalStateException("A senha inicial do administrador deve ter pelo menos 12 caracteres");
        }
        if (usuarioRepository.count() > 0) {
            return;
        }

        Usuario admin = new Usuario(email, passwordEncoder.encode(senha), PerfilUsuario.ADMIN);
        usuarioRepository.save(admin);
    }
}
