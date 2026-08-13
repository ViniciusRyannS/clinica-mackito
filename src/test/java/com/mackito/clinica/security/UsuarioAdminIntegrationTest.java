package com.mackito.clinica.security;

import com.mackito.clinica.model.Medico;
import com.mackito.clinica.repository.MedicoRepository;
import com.mackito.clinica.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
class UsuarioAdminIntegrationTest {

    @DynamicPropertySource
    static void propriedadesDeTeste(DynamicPropertyRegistry registry) {
        registry.add("app.jwt.secret", () -> "segredo-exclusivo-do-teste-com-tamanho-adequado-123456");
    }

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private MedicoRepository medicoRepository;

    @BeforeEach
    void limparDados() {
        usuarioRepository.deleteAll();
        medicoRepository.deleteAll();
    }

    @Test
    @WithMockUser(roles = "RECEPCAO")
    void recepcaoNaoPodeCriarContaInterna() throws Exception {
        mockMvc.perform(post("/usuarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(bodyRecepcao()))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void adminPodeCriarContaDeRecepcaoSemExporSenha() throws Exception {
        mockMvc.perform(post("/usuarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(bodyRecepcao()))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.email").value("recepcao@example.com"))
                .andExpect(jsonPath("$.perfil").value("RECEPCAO"))
                .andExpect(jsonPath("$.senha").doesNotExist());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void adminDeveVincularContaDeMedicoAoCadastro() throws Exception {
        Medico medico = medicoRepository.save(new Medico("Dra. Exemplo", "CRM-TESTE", "Clínica Geral"));

        mockMvc.perform(post("/usuarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "email": "medica@example.com",
                                  "senha": "senha-segura-123",
                                  "perfil": "MEDICO",
                                  "idMedico": %d
                                }
                                """.formatted(medico.getId())))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.perfil").value("MEDICO"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void endpointInternoNaoAceitaPerfilPaciente() throws Exception {
        mockMvc.perform(post("/usuarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "email": "paciente@example.com",
                                  "senha": "senha-segura-123",
                                  "perfil": "PACIENTE"
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.mensagem")
                        .value("Contas de paciente devem usar o cadastro público"));
    }

    private String bodyRecepcao() {
        return """
                {
                  "email": "recepcao@example.com",
                  "senha": "senha-segura-123",
                  "perfil": "RECEPCAO"
                }
                """;
    }
}
