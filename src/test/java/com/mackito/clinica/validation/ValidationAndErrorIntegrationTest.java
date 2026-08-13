package com.mackito.clinica.validation;

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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
class ValidationAndErrorIntegrationTest {

    @DynamicPropertySource
    static void propriedadesDeTeste(DynamicPropertyRegistry registry) {
        registry.add("app.jwt.secret", () -> "segredo-exclusivo-do-teste-com-tamanho-adequado-123456");
    }

    @Autowired
    private MockMvc mockMvc;

    @Test
    void deveRetornarCamposInvalidosNoCadastroDeUsuario() throws Exception {
        mockMvc.perform(post("/auth/cadastrar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "email": "email-invalido",
                                  "senha": "123"
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.erro").value("Bad Request"))
                .andExpect(jsonPath("$.mensagem").value("Campos inválidos"))
                .andExpect(jsonPath("$.path").value("/auth/cadastrar"))
                .andExpect(jsonPath("$.campos.email").value("O e-mail deve possuir um formato válido"))
                .andExpect(jsonPath("$.campos.senha").value("A senha deve ter entre 8 e 72 caracteres"));
    }

    @Test
    @WithMockUser(username = "recepcao@example.com")
    void deveRetornarCamposInvalidosAoCriarPaciente() throws Exception {
        mockMvc.perform(post("/pacientes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "nome": "A",
                                  "cpf": "123.456",
                                  "email": "email-invalido",
                                  "telefone": "123"
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.campos.nome").exists())
                .andExpect(jsonPath("$.campos.cpf").value("O CPF deve conter exatamente 11 dígitos"))
                .andExpect(jsonPath("$.campos.email").value("O email deve possuir um formato válido"))
                .andExpect(jsonPath("$.campos.telefone").exists());
    }

    @Test
    @WithMockUser(username = "recepcao@example.com")
    void deveRetornarCamposInvalidosAoCriarAtendimento() throws Exception {
        mockMvc.perform(post("/atendimentos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "idPaciente": 0,
                                  "idMedico": null,
                                  "idUsuario": -1,
                                  "dataAtendimento": "2020-01-01",
                                  "sala": ""
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.campos.idPaciente").exists())
                .andExpect(jsonPath("$.campos.idMedico").value("O médico é obrigatório"))
                .andExpect(jsonPath("$.campos.idUsuario").exists())
                .andExpect(jsonPath("$.campos.dataAtendimento").exists())
                .andExpect(jsonPath("$.campos.sala").value("A sala é obrigatória"));
    }

    @Test
    @WithMockUser(username = "recepcao@example.com")
    void deveRetornarNaoEncontradoAoAtualizarPacienteInexistente() throws Exception {
        mockMvc.perform(put("/pacientes/999999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "nome": "Paciente Exemplo",
                                  "cpf": "12345678901",
                                  "email": "paciente@example.com",
                                  "telefone": "11999999999"
                                }
                                """))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.erro").value("Not Found"))
                .andExpect(jsonPath("$.mensagem").value("Paciente não encontrado com o ID: 999999"))
                .andExpect(jsonPath("$.path").value("/pacientes/999999"))
                .andExpect(jsonPath("$.campos").doesNotExist());
    }
}
