package com.mackito.clinica.service;

import com.mackito.clinica.model.Paciente;
import com.mackito.clinica.repository.PacienteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PacienteService {

    @Autowired
    private PacienteRepository pacienteRepository;

    public List<Paciente> listarTodos() {
        return pacienteRepository.findAll();
    }

    public Optional<Paciente> buscarPorId(Long id) {
        return pacienteRepository.findById(id);
    }

    public Paciente salvar(Paciente paciente) {
        return pacienteRepository.save(paciente);
    }

    public void deletar(Long id) {
        pacienteRepository.deleteById(id);
    }

    public Paciente atualizarPaciente(Long id, Paciente pacienteAtualizado) {
    Paciente pacienteExistente = pacienteRepository.findById(id)
        .orElseThrow(() -> new RuntimeException("Paciente não encontrado com o ID: " + id));

    pacienteExistente.setNome(pacienteAtualizado.getNome());
    pacienteExistente.setCpf(pacienteAtualizado.getCpf());
    pacienteExistente.setEmail(pacienteAtualizado.getEmail());
    pacienteExistente.setTelefone(pacienteAtualizado.getTelefone());

    return pacienteRepository.save(pacienteExistente);
}

}
