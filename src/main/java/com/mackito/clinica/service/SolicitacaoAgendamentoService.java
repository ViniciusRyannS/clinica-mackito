package com.mackito.clinica.service;

import com.mackito.clinica.exception.ConflitoDadosException;
import com.mackito.clinica.exception.RecursoNaoEncontradoException;
import com.mackito.clinica.model.*;
import com.mackito.clinica.model.dto.*;
import com.mackito.clinica.repository.*;
import com.mackito.clinica.security.SecurityUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class SolicitacaoAgendamentoService {
    private final SolicitacaoAgendamentoRepository solicitacaoRepository;
    private final UsuarioRepository usuarioRepository;
    private final MedicoRepository medicoRepository;
    private final AtendimentoRepository atendimentoRepository;
    private final DisponibilidadeAgendaService disponibilidadeAgendaService;

    public SolicitacaoAgendamentoService(SolicitacaoAgendamentoRepository solicitacaoRepository,
                                         UsuarioRepository usuarioRepository,
                                         MedicoRepository medicoRepository,
                                         AtendimentoRepository atendimentoRepository,
                                         DisponibilidadeAgendaService disponibilidadeAgendaService) {
        this.solicitacaoRepository = solicitacaoRepository;
        this.usuarioRepository = usuarioRepository;
        this.medicoRepository = medicoRepository;
        this.atendimentoRepository = atendimentoRepository;
        this.disponibilidadeAgendaService = disponibilidadeAgendaService;
    }

    @Transactional
    public SolicitacaoAgendamentoDTO criar(SolicitacaoAgendamentoRequestDTO dto) {
        Usuario usuario = usuarioAutenticado();
        if (usuario.getPaciente() == null) {
            throw new RecursoNaoEncontradoException("Complete o perfil de paciente antes de solicitar um agendamento");
        }
        Medico medico = medicoRepository.findById(dto.idMedico())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Médico não encontrado com o ID: " + dto.idMedico()));
        return paraDTO(solicitacaoRepository.save(new SolicitacaoAgendamento(
                usuario.getPaciente(), medico, dto.dataPreferida(), dto.horaPreferida(), normalizar(dto.observacao()))));
    }

    @Transactional(readOnly = true)
    public List<SolicitacaoAgendamentoDTO> listarProprias() {
        Usuario usuario = usuarioAutenticado();
        if (usuario.getPaciente() == null) {
            throw new RecursoNaoEncontradoException("A conta ainda não possui um perfil de paciente");
        }
        return solicitacaoRepository.findByPacienteIdOrderByCriadaEmDesc(usuario.getPaciente().getId())
                .stream().map(this::paraDTO).toList();
    }

    @Transactional(readOnly = true)
    public List<SolicitacaoAgendamentoDTO> listarTodas() {
        return solicitacaoRepository.findAllByOrderByCriadaEmDesc().stream().map(this::paraDTO).toList();
    }

    @Transactional
    public SolicitacaoAgendamentoDTO confirmar(Long id, ConfirmacaoSolicitacaoDTO dto) {
        SolicitacaoAgendamento solicitacao = buscarPendente(id);
        Usuario responsavel = usuarioAutenticado();
        disponibilidadeAgendaService.validar(solicitacao.getMedico().getId(), solicitacao.getDataPreferida(),
                dto.horaInicial(), dto.duracaoMinutos(), dto.sala());
        Atendimento atendimento = atendimentoRepository.save(new Atendimento(
                solicitacao.getPaciente(), solicitacao.getMedico(), responsavel,
                solicitacao.getDataPreferida(), dto.horaInicial(), dto.duracaoMinutos(), dto.sala()));
        solicitacao.confirmar(responsavel, atendimento);
        return paraDTO(solicitacaoRepository.save(solicitacao));
    }

    @Transactional
    public SolicitacaoAgendamentoDTO rejeitar(Long id, RejeicaoSolicitacaoDTO dto) {
        SolicitacaoAgendamento solicitacao = buscarPendente(id);
        solicitacao.rejeitar(usuarioAutenticado(), dto.motivo());
        return paraDTO(solicitacaoRepository.save(solicitacao));
    }

    private SolicitacaoAgendamento buscarPendente(Long id) {
        SolicitacaoAgendamento solicitacao = solicitacaoRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Solicitação não encontrada com o ID: " + id));
        if (solicitacao.getStatus() != StatusSolicitacao.PENDENTE) {
            throw new ConflitoDadosException("A solicitação já foi processada");
        }
        return solicitacao;
    }

    private Usuario usuarioAutenticado() {
        return usuarioRepository.findByEmail(SecurityUtil.getEmailUsuarioLogado())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Usuário autenticado não encontrado"));
    }

    private String normalizar(String valor) {
        return valor == null || valor.isBlank() ? null : valor.trim();
    }

    private SolicitacaoAgendamentoDTO paraDTO(SolicitacaoAgendamento s) {
        return new SolicitacaoAgendamentoDTO(s.getId(), s.getPaciente().getId(), s.getPaciente().getNome(),
                s.getMedico().getId(), s.getMedico().getNome(), s.getDataPreferida(), s.getHoraPreferida(), s.getObservacao(),
                s.getStatus(), s.getMotivoRejeicao(), s.getAtendimento() == null ? null : s.getAtendimento().getId(),
                s.getCriadaEm(), s.getDecididaEm());
    }
}
