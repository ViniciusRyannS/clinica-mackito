package com.mackito.clinica.security;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.security.test.context.support.WithMockUser;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
class SecurityConfigIntegrationTest {

    @DynamicPropertySource
    static void propriedadesDeTeste(DynamicPropertyRegistry registry) {
        registry.add("app.jwt.secret", () -> "segredo-exclusivo-do-teste-com-tamanho-adequado-123456");
    }

    @Autowired
    private MockMvc mockMvc;

    @Test
    void deveBloquearListagemDePacientesSemAutenticacao() throws Exception {
        mockMvc.perform(get("/pacientes"))
                .andExpect(status().isForbidden());
    }

    @Test
    void deveBloquearListagemDeAtendimentosSemAutenticacao() throws Exception {
        mockMvc.perform(get("/atendimentos"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void devePermitirPacientesParaAdmin() throws Exception {
        mockMvc.perform(get("/pacientes"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "RECEPCAO")
    void devePermitirAtendimentosParaRecepcao() throws Exception {
        mockMvc.perform(get("/atendimentos"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "MEDICO")
    void deveNegarListaCompletaDePacientesParaMedico() throws Exception {
        mockMvc.perform(get("/pacientes"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "PACIENTE")
    void deveNegarListaCompletaDeAtendimentosParaPaciente() throws Exception {
        mockMvc.perform(get("/atendimentos"))
                .andExpect(status().isForbidden());
    }
}
