package com.mackito.clinica.controller;

import com.mackito.clinica.model.dto.AtendimentoDTO;
import com.mackito.clinica.model.dto.AtendimentoRequestDTO;
import com.mackito.clinica.service.AtendimentoService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/atendimentos")
public class AtendimentoController {

    @Autowired
    private AtendimentoService atendimentoService;

    @PostMapping
    public ResponseEntity<AtendimentoDTO> criar(@RequestBody AtendimentoRequestDTO dto) {
        var atendimento = atendimentoService.salvar(dto);
        return ResponseEntity.ok(atendimento);
    }

    @GetMapping
    public ResponseEntity<List<AtendimentoDTO>> listarTodos() {
        return ResponseEntity.ok(atendimentoService.listarTodos());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> cancelar(@PathVariable Long id) {
        atendimentoService.cancelarAtendimento(id);
        return ResponseEntity.noContent().build();
    }

    // ENDPOINT: Listar atendimentos por paciente
    @GetMapping("/paciente/{id}")
    public ResponseEntity<List<AtendimentoDTO>> listarPorPaciente(@PathVariable Long id) {
        var lista = atendimentoService.listarPorPaciente(id);
        return ResponseEntity.ok(lista);
    }

    @GetMapping("/medico/{id}")
public ResponseEntity<List<AtendimentoDTO>> listarPorMedico(@PathVariable Long id) {
    var lista = atendimentoService.listarPorMedico(id);
    return ResponseEntity.ok(lista);
}

}
