package com.mackito.clinica.controller;

import com.mackito.clinica.model.Paciente;
import com.mackito.clinica.model.dto.PacienteDTO;
import com.mackito.clinica.service.PacienteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/pacientes")
public class PacienteController {

    @Autowired
    private PacienteService pacienteService;

    @GetMapping
    public List<Paciente> listarTodos() {
        return pacienteService.listarTodos();
    }

    @GetMapping("/{id}")
public ResponseEntity<PacienteDTO> buscarPorId(@PathVariable Long id) {
    return pacienteService.buscarPorId(id)
        .map(p -> new PacienteDTO(p.getId(), p.getNome(), p.getCpf(), p.getEmail(), p.getTelefone()))
        .map(ResponseEntity::ok)
        .orElse(ResponseEntity.notFound().build());
}


    @PostMapping
    public Paciente criarPaciente(@RequestBody @Valid Paciente paciente) {
        return pacienteService.salvar(paciente);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Paciente> atualizarPaciente(@PathVariable Long id, @RequestBody @Valid Paciente paciente) {
        Paciente atualizado = pacienteService.atualizarPaciente(id, paciente);
        return ResponseEntity.ok(atualizado);
    }

    @DeleteMapping("/{id}")
    public void deletarPaciente(@PathVariable Long id) {
        pacienteService.deletar(id);
    }

    
}