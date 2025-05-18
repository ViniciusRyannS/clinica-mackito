package com.mackito.clinica.service;

import com.mackito.clinica.model.Medico;
import com.mackito.clinica.repository.MedicoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class MedicoService {

    @Autowired
    private MedicoRepository repository;

    public List<Medico> listarTodos() {
        return repository.findAll();
    }

    public Optional<Medico> buscarPorId(Long id) {
        return repository.findById(id);
    }

    public Medico salvar(Medico medico) {
        return repository.save(medico);
    }

    public Medico atualizarMedico(Long id, Medico novoMedico) {
        Medico medico = repository.findById(id).orElseThrow();
        medico.setNome(novoMedico.getNome());
        medico.setCrm(novoMedico.getCrm());
        medico.setEspecialidade(novoMedico.getEspecialidade());
        return repository.save(medico);
    }

    public void deletar(Long id) {
        repository.deleteById(id);
    }
}
