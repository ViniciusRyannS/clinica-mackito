package com.mackito.clinica.security;

import com.mackito.clinica.model.Atendimento;
import com.mackito.clinica.model.Medico;
import com.mackito.clinica.model.Paciente;
import com.mackito.clinica.model.PerfilUsuario;
import com.mackito.clinica.model.Usuario;
import com.mackito.clinica.repository.AtendimentoRepository;
import com.mackito.clinica.repository.MedicoRepository;
import com.mackito.clinica.repository.PacienteRepository;
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

import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
class MeuPerfilIntegrationTest {

    @DynamicPropertySource
    static void propriedadesDeTeste(DynamicPropertyRegistry registry) {
        registry.add("app.jwt.secret", () -> "segredo-exclusivo-do-teste-com-tamanho-adequado-123456");
    }

    @Autowired private MockMvc mockMvc;
    @Autowired private AtendimentoRepository atendimentoRepository;
    @Autowired private UsuarioRepository usuarioRepository;
    @Autowired private PacienteRepository pacienteRepository;
    @Autowired private MedicoRepository medicoRepository;

    @BeforeEach
    void limparDados() {
        atendimentoRepository.deleteAll();
        usuarioRepository.deleteAll();
        pacienteRepository.deleteAll();
        medicoRepository.deleteAll();
    }

    @Test
    @WithMockUser(username = "paciente@example.com", roles = "PACIENTE")
    void pacienteCriaPerfilComEmailDaContaAutenticada() throws Exception {
        usuarioRepository.save(new Usuario("paciente@example.com", "hash-seguro", PerfilUsuario.PACIENTE));

        mockMvc.perform(post("/me/paciente")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "nome": "Paciente Exemplo",
                                  "cpf": "12345678901",
                                  "telefone": "11999999999",
                                  "email": "outra-pessoa@example.com"
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.email").value("paciente@example.com"))
                .andExpect(jsonPath("$.cpf").value("12345678901"));
    }

    @Test
    @WithMockUser(username = "paciente@example.com", roles = "PACIENTE")
    void pacienteNaoPodeCriarSegundoPerfil() throws Exception {
        Paciente paciente = pacienteRepository.save(
                new Paciente("Paciente Exemplo", "12345678901", "paciente@example.com", "11999999999"));
        Usuario usuario = new Usuario("paciente@example.com", "hash-seguro", PerfilUsuario.PACIENTE);
        usuario.setPaciente(paciente);
        usuarioRepository.save(usuario);

        mockMvc.perform(post("/me/paciente")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"nome":"Outro Nome","cpf":"10987654321","telefone":"11888888888"}
                                """))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.mensagem").value("A conta já possui um perfil de paciente"));
    }

    @Test
    @WithMockUser(username = "paciente@example.com", roles = "PACIENTE")
    void pacienteVeSomenteOsPropriosAtendimentos() throws Exception {
        Usuario recepcao = usuarioRepository.save(
                new Usuario("recepcao@example.com", "hash-seguro", PerfilUsuario.RECEPCAO));
        Paciente proprio = pacienteRepository.save(
                new Paciente("Paciente Próprio", "12345678901", "paciente@example.com", "11999999999"));
        Paciente outro = pacienteRepository.save(
                new Paciente("Outro Paciente", "10987654321", "outro@example.com", "11888888888"));
        Medico medico = medicoRepository.save(new Medico("Dra. Exemplo", "CRM-TESTE", "Clínica Geral"));
        Usuario usuario = new Usuario("paciente@example.com", "hash-seguro", PerfilUsuario.PACIENTE);
        usuario.setPaciente(proprio);
        usuarioRepository.save(usuario);
        atendimentoRepository.save(new Atendimento(proprio, medico, recepcao, LocalDate.of(2026, 9, 10), "A1"));
        atendimentoRepository.save(new Atendimento(outro, medico, recepcao, LocalDate.of(2026, 9, 11), "B2"));

        mockMvc.perform(get("/me/atendimentos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].nomePaciente").value("Paciente Próprio"));
    }

    @Test
    @WithMockUser(username = "medico@example.com", roles = "MEDICO")
    void medicoVeSomenteOsPropriosAtendimentos() throws Exception {
        Usuario recepcao = usuarioRepository.save(
                new Usuario("recepcao@example.com", "hash-seguro", PerfilUsuario.RECEPCAO));
        Paciente paciente = pacienteRepository.save(
                new Paciente("Paciente Exemplo", "12345678901", "paciente@example.com", "11999999999"));
        Medico proprio = medicoRepository.save(new Medico("Dra. Própria", "CRM-0001", "Clínica Geral"));
        Medico outro = medicoRepository.save(new Medico("Dr. Outro", "CRM-0002", "Cardiologia"));
        Usuario usuario = new Usuario("medico@example.com", "hash-seguro", PerfilUsuario.MEDICO);
        usuario.setMedico(proprio);
        usuarioRepository.save(usuario);
        atendimentoRepository.save(new Atendimento(paciente, proprio, recepcao, LocalDate.of(2026, 9, 10), "A1"));
        atendimentoRepository.save(new Atendimento(paciente, outro, recepcao, LocalDate.of(2026, 9, 11), "B2"));

        mockMvc.perform(get("/me/atendimentos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].nomeMedico").value("Dra. Própria"));
    }

    @Test
    @WithMockUser(username = "medico@example.com", roles = "MEDICO")
    void medicoNaoPodeCriarPerfilDePaciente() throws Exception {
        mockMvc.perform(post("/me/paciente")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"nome":"Paciente Exemplo","cpf":"12345678901","telefone":"11999999999"}
                                """))
                .andExpect(status().isForbidden());
    }
}
