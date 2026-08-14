package com.mackito.clinica.service;

import com.mackito.clinica.exception.RecursoNaoEncontradoException;
import com.mackito.clinica.model.dto.AtendimentoDTO;
import com.mackito.clinica.model.dto.AtendimentoRequestDTO;
import com.mackito.clinica.model.Atendimento;
import com.mackito.clinica.model.Medico;
import com.mackito.clinica.model.Paciente;
import com.mackito.clinica.model.Usuario;
import com.mackito.clinica.repository.AtendimentoRepository;
import com.mackito.clinica.repository.MedicoRepository;
import com.mackito.clinica.repository.PacienteRepository;
import com.mackito.clinica.repository.UsuarioRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AtendimentoService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private AtendimentoRepository atendimentoRepository;

    @Autowired
    private MedicoRepository medicoRepository;

    @Autowired
    private PacienteRepository pacienteRepository;

    @Autowired
    private DisponibilidadeAgendaService disponibilidadeAgendaService;

    public AtendimentoDTO salvar(AtendimentoRequestDTO dto) {
        Medico medico = medicoRepository.findById(dto.getIdMedico())
                .orElseThrow(() -> new RecursoNaoEncontradoException(
                        "Médico não encontrado com o ID: " + dto.getIdMedico()));
        Paciente paciente = pacienteRepository.findById(dto.getIdPaciente())
                .orElseThrow(() -> new RecursoNaoEncontradoException(
                        "Paciente não encontrado com o ID: " + dto.getIdPaciente()));
        String emailUsuario = SecurityContextHolder.getContext().getAuthentication().getName();
        Usuario usuario = usuarioRepository.findByEmail(emailUsuario)
                .orElseThrow(() -> new RecursoNaoEncontradoException(
                        "Usuário autenticado não encontrado"));

        disponibilidadeAgendaService.validar(medico.getId(), dto.getDataAtendimento(),
                dto.getHoraInicial(), dto.getDuracaoMinutos(), dto.getSala());

        Atendimento atendimento = new Atendimento();
        atendimento.setMedico(medico);
        atendimento.setPaciente(paciente);
        atendimento.setUsuario(usuario);
        atendimento.setDataAtendimento(dto.getDataAtendimento());
        atendimento.setHoraInicial(dto.getHoraInicial());
        atendimento.setDuracaoMinutos(dto.getDuracaoMinutos());
        atendimento.setSala(dto.getSala());

        Atendimento salvo = atendimentoRepository.save(atendimento);

        return new AtendimentoDTO(
            salvo.getId(),
            salvo.getPaciente().getId(),
            salvo.getPaciente().getNome(),
            salvo.getMedico().getId(),
            salvo.getMedico().getNome(),
            salvo.getDataAtendimento(),
            salvo.getHoraInicial(),
            salvo.getDuracaoMinutos(),
            salvo.getSala()
        );
    }

    public List<AtendimentoDTO> listarTodos() {
        return atendimentoRepository.findAll().stream().map(a ->
            new AtendimentoDTO(
                a.getId(),
                a.getPaciente().getId(),
                a.getPaciente().getNome(),
                a.getMedico().getId(),
                a.getMedico().getNome(),
                a.getDataAtendimento(),
                a.getHoraInicial(),
                a.getDuracaoMinutos(),
                a.getSala()
            )).collect(Collectors.toList());
    }

    public void cancelarAtendimento(Long id) {
        if (!atendimentoRepository.existsById(id)) {
            throw new RecursoNaoEncontradoException("Atendimento não encontrado com o ID: " + id);
        }
        atendimentoRepository.deleteById(id);
    }

    // MÉTODO: listar atendimentos por paciente
    public List<AtendimentoDTO> listarPorPaciente(Long idPaciente) {
        return atendimentoRepository.findByPacienteId(idPaciente)
            .stream()
            .map(a -> new AtendimentoDTO(
                a.getId(),
                a.getPaciente().getId(),
                a.getPaciente().getNome(),
                a.getMedico().getId(),
                a.getMedico().getNome(),
                a.getDataAtendimento(),
                a.getHoraInicial(),
                a.getDuracaoMinutos(),
                a.getSala()
            )).collect(Collectors.toList());
    }

    public List<AtendimentoDTO> listarPorMedico(Long idMedico) {
    return atendimentoRepository.findByMedicoId(idMedico)
        .stream()
        .map(a -> new AtendimentoDTO(
            a.getId(),
            a.getPaciente().getId(),
            a.getPaciente().getNome(),
            a.getMedico().getId(),
            a.getMedico().getNome(),
            a.getDataAtendimento(),
            a.getHoraInicial(),
            a.getDuracaoMinutos(),
            a.getSala()
        )).collect(Collectors.toList());
}

}
