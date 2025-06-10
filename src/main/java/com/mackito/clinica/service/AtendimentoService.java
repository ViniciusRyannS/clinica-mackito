package com.mackito.clinica.service;

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

import java.util.List;
import java.util.Optional;
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

    public AtendimentoDTO salvar(AtendimentoRequestDTO dto) {
        Optional<Medico> medicoOpt = medicoRepository.findById(dto.getIdMedico());
        Optional<Paciente> pacienteOpt = pacienteRepository.findById(dto.getIdPaciente());
        Optional<Usuario> usuarioOpt = usuarioRepository.findById(dto.getIdUsuario());

        if (medicoOpt.isEmpty() || pacienteOpt.isEmpty() || usuarioOpt.isEmpty()) {
            throw new RuntimeException("Médico, Paciente ou Usuário não encontrado");
        }

        Atendimento atendimento = new Atendimento();
        atendimento.setMedico(medicoOpt.get());
        atendimento.setPaciente(pacienteOpt.get());
        atendimento.setUsuario(usuarioOpt.get());
        atendimento.setDataAtendimento(dto.getDataAtendimento());
        atendimento.setSala(dto.getSala());

        Atendimento salvo = atendimentoRepository.save(atendimento);

        return new AtendimentoDTO(
            salvo.getId(),
            salvo.getPaciente().getId(),
            salvo.getPaciente().getNome(),
            salvo.getMedico().getId(),
            salvo.getMedico().getNome(),
            salvo.getDataAtendimento(),
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
                a.getSala()
            )).collect(Collectors.toList());
    }

    public void cancelarAtendimento(Long id) {
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
            a.getSala()
        )).collect(Collectors.toList());
}

}
