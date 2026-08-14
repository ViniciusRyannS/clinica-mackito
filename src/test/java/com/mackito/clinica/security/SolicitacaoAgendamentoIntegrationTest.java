package com.mackito.clinica.security;

import com.mackito.clinica.model.*;
import com.mackito.clinica.repository.*;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.*;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
class SolicitacaoAgendamentoIntegrationTest {
    @DynamicPropertySource
    static void propriedadesDeTeste(DynamicPropertyRegistry registry) {
        registry.add("app.jwt.secret", () -> "segredo-exclusivo-do-teste-com-tamanho-adequado-123456");
    }

    @Autowired private MockMvc mockMvc;
    @Autowired private SolicitacaoAgendamentoRepository solicitacaoRepository;
    @Autowired private AtendimentoRepository atendimentoRepository;
    @Autowired private UsuarioRepository usuarioRepository;
    @Autowired private PacienteRepository pacienteRepository;
    @Autowired private MedicoRepository medicoRepository;

    @BeforeEach
    void limparDados() {
        solicitacaoRepository.deleteAll();
        atendimentoRepository.deleteAll();
        usuarioRepository.deleteAll();
        pacienteRepository.deleteAll();
        medicoRepository.deleteAll();
    }

    @Test
    @WithMockUser(username = "paciente@example.com", roles = "PACIENTE")
    void pacienteSolicitaSemEscolherOutroPaciente() throws Exception {
        Medico medico = prepararPacienteEMedico();

        mockMvc.perform(post("/me/solicitacoes-agendamento")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"idMedico":%d,"dataPreferida":"2099-09-10","horaPreferida":"09:00","observacao":"Prefiro pela manhã","idPaciente":999}
                                """.formatted(medico.getId())))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nomePaciente").value("Paciente Exemplo"))
                .andExpect(jsonPath("$.status").value("PENDENTE"))
                .andExpect(jsonPath("$.idAtendimento").isEmpty());
    }

    @Test
    @WithMockUser(username = "recepcao@example.com", roles = "RECEPCAO")
    void recepcaoConfirmaECriaAtendimento() throws Exception {
        SolicitacaoAgendamento solicitacao = prepararSolicitacao();
        usuarioRepository.save(new Usuario("recepcao@example.com", "hash-seguro", PerfilUsuario.RECEPCAO));

        mockMvc.perform(patch("/solicitacoes-agendamento/{id}/confirmar", solicitacao.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"horaInicial\":\"09:30\",\"duracaoMinutos\":30,\"sala\":\"Sala 2\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("CONFIRMADA"))
                .andExpect(jsonPath("$.idAtendimento").isNumber());

        Assertions.assertEquals(1, atendimentoRepository.count());
    }

    @Test
    @WithMockUser(username = "recepcao@example.com", roles = "RECEPCAO")
    void solicitacaoNaoPodeSerProcessadaDuasVezes() throws Exception {
        SolicitacaoAgendamento solicitacao = prepararSolicitacao();
        Usuario recepcao = usuarioRepository.save(
                new Usuario("recepcao@example.com", "hash-seguro", PerfilUsuario.RECEPCAO));
        solicitacao.rejeitar(recepcao, "Data indisponível");
        solicitacaoRepository.save(solicitacao);

        mockMvc.perform(patch("/solicitacoes-agendamento/{id}/confirmar", solicitacao.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"horaInicial\":\"09:30\",\"duracaoMinutos\":30,\"sala\":\"Sala 2\"}"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.mensagem").value("A solicitação já foi processada"));
    }

    @Test
    @WithMockUser(username = "medico@example.com", roles = "MEDICO")
    void medicoNaoPodeProcessarSolicitacao() throws Exception {
        mockMvc.perform(patch("/solicitacoes-agendamento/1/rejeitar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"motivo\":\"Indisponível\"}"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "recepcao@example.com", roles = "RECEPCAO")
    void deveBloquearSobreposicaoNaAgendaDoMedico() throws Exception {
        SolicitacaoAgendamento solicitacao = prepararSolicitacao();
        Usuario recepcao = usuarioRepository.save(
                new Usuario("recepcao@example.com", "hash-seguro", PerfilUsuario.RECEPCAO));
        atendimentoRepository.save(new Atendimento(solicitacao.getPaciente(), solicitacao.getMedico(), recepcao,
                solicitacao.getDataPreferida(), java.time.LocalTime.of(9, 0), 60, "Sala 1"));

        mockMvc.perform(patch("/solicitacoes-agendamento/{id}/confirmar", solicitacao.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"horaInicial\":\"09:30\",\"duracaoMinutos\":30,\"sala\":\"Sala 2\"}"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.mensagem").value("O médico já possui atendimento nesse intervalo"));
    }

    @Test
    @WithMockUser(username = "recepcao@example.com", roles = "RECEPCAO")
    void deveBloquearSalaSobrepostaMasPermitirHorarioConsecutivo() throws Exception {
        SolicitacaoAgendamento solicitacao = prepararSolicitacao();
        Usuario recepcao = usuarioRepository.save(
                new Usuario("recepcao@example.com", "hash-seguro", PerfilUsuario.RECEPCAO));
        Medico outroMedico = medicoRepository.save(new Medico("Dr. Outro", "CRM-OUTRO", "Cardiologia"));
        atendimentoRepository.save(new Atendimento(solicitacao.getPaciente(), outroMedico, recepcao,
                solicitacao.getDataPreferida(), java.time.LocalTime.of(9, 0), 30, "Sala 1"));

        mockMvc.perform(patch("/solicitacoes-agendamento/{id}/confirmar", solicitacao.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"horaInicial\":\"09:15\",\"duracaoMinutos\":30,\"sala\":\"Sala 1\"}"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.mensagem").value("A sala já está ocupada nesse intervalo"));

        mockMvc.perform(patch("/solicitacoes-agendamento/{id}/confirmar", solicitacao.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"horaInicial\":\"09:30\",\"duracaoMinutos\":30,\"sala\":\"Sala 1\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("CONFIRMADA"));
    }

    @Test
    @WithMockUser(username = "recepcao@example.com", roles = "RECEPCAO")
    void atendimentoNaoPodeTerminarNoDiaSeguinte() throws Exception {
        SolicitacaoAgendamento solicitacao = prepararSolicitacao();
        usuarioRepository.save(new Usuario("recepcao@example.com", "hash-seguro", PerfilUsuario.RECEPCAO));

        mockMvc.perform(patch("/solicitacoes-agendamento/{id}/confirmar", solicitacao.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"horaInicial\":\"23:50\",\"duracaoMinutos\":30,\"sala\":\"Sala 1\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.mensagem").value("O atendimento deve terminar no mesmo dia"));
    }

    private Medico prepararPacienteEMedico() {
        Paciente paciente = pacienteRepository.save(new Paciente(
                "Paciente Exemplo", "12345678901", "paciente@example.com", "11999999999"));
        Usuario usuario = new Usuario("paciente@example.com", "hash-seguro", PerfilUsuario.PACIENTE);
        usuario.setPaciente(paciente);
        usuarioRepository.save(usuario);
        return medicoRepository.save(new Medico("Dra. Exemplo", "CRM-TESTE", "Clínica Geral"));
    }

    private SolicitacaoAgendamento prepararSolicitacao() {
        Medico medico = prepararPacienteEMedico();
        Paciente paciente = pacienteRepository.findAll().get(0);
        return solicitacaoRepository.save(new SolicitacaoAgendamento(
                paciente, medico, java.time.LocalDate.of(2099, 9, 10), java.time.LocalTime.of(9, 0),
                "Preferência pela manhã"));
    }
}
