package com.mackito.clinica.service;

import com.mackito.clinica.model.dto.AtendimentoDTO;
import com.mackito.clinica.model.dto.AtendimentoRequestDTO;
import com.mackito.clinica.model.Atendimento;
import com.mackito.clinica.model.Medico;
import com.mackito.clinica.model.Paciente;
import com.mackito.clinica.repository.AtendimentoRepository;
import com.mackito.clinica.repository.MedicoRepository;
import com.mackito.clinica.repository.PacienteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class AtendimentoService {

    @Autowired
    private AtendimentoRepository atendimentoRepository;

    @Autowired
    private MedicoRepository medicoRepository;

    @Autowired
    private PacienteRepository pacienteRepository;

    public AtendimentoDTO salvar(AtendimentoRequestDTO dto) {
        Optional<Medico> medicoOpt = medicoRepository.findById(dto.getIdMedico());
        Optional<Paciente> pacienteOpt = pacienteRepository.findById(dto.getIdPaciente());

        if (medicoOpt.isEmpty() || pacienteOpt.isEmpty()) {
            throw new RuntimeException("Médico ou Paciente não encontrado");

        }

        Atendimento atendimento = new Atendimento();
        atendimento.setMedico(medicoOpt.get());
        atendimento.setPaciente(pacienteOpt.get());
        atendimento.setDataAtendimento(dto.getDataAtendimento());
        atendimento.setSala(dto.getSala());

        Atendimento salvo = atendimentoRepository.save(atendimento);

        return new AtendimentoDTO(
                salvo.getId(),
                salvo.getPaciente().getId(),
                salvo.getMedico().getId(),
                salvo.getDataAtendimento(),
                salvo.getSala()
        );
    }

    public List<AtendimentoDTO> listarTodos() {
        return atendimentoRepository.findAll().stream().map(a ->
                new AtendimentoDTO(
                        a.getId(),
                        a.getPaciente().getId(),
                        a.getMedico().getId(),
                        a.getDataAtendimento(),
                        a.getSala()
                )).collect(Collectors.toList());
    }

    public void cancelarAtendimento(Long id) {
        atendimentoRepository.deleteById(id);
    }
}
