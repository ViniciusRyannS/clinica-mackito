package com.mackito.clinica.config;

import com.mackito.clinica.model.PerfilUsuario;
import com.mackito.clinica.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ActiveProfiles("test")
@SpringBootTest
class AdminBootstrapTest {

    @DynamicPropertySource
    static void propriedadesDeTeste(DynamicPropertyRegistry registry) {
        registry.add("app.jwt.secret", () -> "segredo-exclusivo-do-teste-com-tamanho-adequado-123456");
    }

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void limparUsuarios() {
        usuarioRepository.deleteAll();
    }

    @Test
    void deveCriarPrimeiroAdminSomenteComConfiguracaoExplicita() throws Exception {
        AdminBootstrap bootstrap = new AdminBootstrap(
                usuarioRepository,
                passwordEncoder,
                "admin@example.com",
                "senha-inicial-segura");

        bootstrap.run(null);

        var admin = usuarioRepository.findByEmail("admin@example.com").orElseThrow();
        assertEquals(PerfilUsuario.ADMIN, admin.getPerfil());
        assertTrue(passwordEncoder.matches("senha-inicial-segura", admin.getSenha()));
    }

    @Test
    void naoDeveCriarAdminQuandoJaExisteUsuario() throws Exception {
        AdminBootstrap primeiro = new AdminBootstrap(
                usuarioRepository,
                passwordEncoder,
                "admin@example.com",
                "senha-inicial-segura");
        primeiro.run(null);

        AdminBootstrap segundo = new AdminBootstrap(
                usuarioRepository,
                passwordEncoder,
                "outro-admin@example.com",
                "outra-senha-segura");
        segundo.run(null);

        assertEquals(1, usuarioRepository.count());
    }
}
