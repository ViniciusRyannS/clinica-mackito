package com.mackito.clinica.controller;

import com.mackito.clinica.model.dto.AtendimentoDTO;
import com.mackito.clinica.model.dto.AtendimentoRequestDTO;
import com.mackito.clinica.service.AtendimentoService;
import jakarta.validation.Valid;
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
    public ResponseEntity<AtendimentoDTO> marcarConsulta(@RequestBody @Valid AtendimentoRequestDTO dto) {
        AtendimentoDTO atendimento = atendimentoService.salvar(dto);
        return ResponseEntity.ok(atendimento);
    }

    @GetMapping
    public ResponseEntity<List<AtendimentoDTO>> listarConsultas() {
        return ResponseEntity.ok(atendimentoService.listarTodos());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> cancelarConsulta(@PathVariable Long id) {
        atendimentoService.cancelarAtendimento(id);
        return ResponseEntity.noContent().build();
    }
}
