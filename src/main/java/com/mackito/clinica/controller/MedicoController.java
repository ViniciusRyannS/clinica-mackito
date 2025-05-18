package com.mackito.clinica.controller;

import com.mackito.clinica.model.Medico;
import com.mackito.clinica.service.MedicoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/medicos")
public class MedicoController {

    @Autowired
    private MedicoService medicoService;

    @GetMapping
    public List<Medico> listarTodos() {
        return medicoService.listarTodos();
    }

    @GetMapping("/{id}")
    public Optional<Medico> buscarPorId(@PathVariable Long id) {
        return medicoService.buscarPorId(id);
    }

    @PostMapping
    public Medico criarMedico(@RequestBody @Valid Medico medico) {
        return medicoService.salvar(medico);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Medico> atualizarMedico(@PathVariable Long id, @RequestBody @Valid Medico medico) {
        Medico atualizado = medicoService.atualizarMedico(id, medico);
        return ResponseEntity.ok(atualizado);
    }

    @DeleteMapping("/{id}")
    public void deletarMedico(@PathVariable Long id) {
        medicoService.deletar(id);
    }
}
