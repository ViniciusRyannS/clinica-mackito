package com.mackito.clinica.service;

import com.mackito.clinica.exception.RecursoNaoEncontradoException;
import com.mackito.clinica.exception.ConflitoDadosException;
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
        validarUnicidadeAoCriar(paciente);
        return pacienteRepository.save(paciente);
    }

    public void deletar(Long id) {
        if (!pacienteRepository.existsById(id)) {
            throw new RecursoNaoEncontradoException("Paciente não encontrado com o ID: " + id);
        }
        pacienteRepository.deleteById(id);
    }

    public Paciente atualizarPaciente(Long id, Paciente pacienteAtualizado) {
    Paciente pacienteExistente = pacienteRepository.findById(id)
        .orElseThrow(() -> new RecursoNaoEncontradoException("Paciente não encontrado com o ID: " + id));

    if (pacienteRepository.existsByCpfAndIdNot(pacienteAtualizado.getCpf(), id)) {
        throw new ConflitoDadosException("Já existe um paciente cadastrado com este CPF");
    }
    if (pacienteRepository.existsByEmailAndIdNot(pacienteAtualizado.getEmail(), id)) {
        throw new ConflitoDadosException("Já existe um paciente cadastrado com este e-mail");
    }

    pacienteExistente.setNome(pacienteAtualizado.getNome());
    pacienteExistente.setCpf(pacienteAtualizado.getCpf());
    pacienteExistente.setEmail(pacienteAtualizado.getEmail());
    pacienteExistente.setTelefone(pacienteAtualizado.getTelefone());

    return pacienteRepository.save(pacienteExistente);
}

    private void validarUnicidadeAoCriar(Paciente paciente) {
        if (pacienteRepository.existsByCpf(paciente.getCpf())) {
            throw new ConflitoDadosException("Já existe um paciente cadastrado com este CPF");
        }
        if (pacienteRepository.existsByEmail(paciente.getEmail())) {
            throw new ConflitoDadosException("Já existe um paciente cadastrado com este e-mail");
        }
    }

}
