package com.mackito.clinica.controller;

import com.mackito.clinica.model.dto.*;
import com.mackito.clinica.service.SolicitacaoAgendamentoService;
import jakarta.validation.Valid;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
public class SolicitacaoAgendamentoController {
    private final SolicitacaoAgendamentoService service;

    public SolicitacaoAgendamentoController(SolicitacaoAgendamentoService service) {
        this.service = service;
    }

    @PostMapping("/me/solicitacoes-agendamento")
    public ResponseEntity<SolicitacaoAgendamentoDTO> criar(@Valid @RequestBody SolicitacaoAgendamentoRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.criar(dto));
    }

    @GetMapping("/me/solicitacoes-agendamento")
    public List<SolicitacaoAgendamentoDTO> listarProprias() { return service.listarProprias(); }

    @GetMapping("/solicitacoes-agendamento")
    public List<SolicitacaoAgendamentoDTO> listarTodas() { return service.listarTodas(); }

    @PatchMapping("/solicitacoes-agendamento/{id}/confirmar")
    public SolicitacaoAgendamentoDTO confirmar(@PathVariable Long id,
                                                @Valid @RequestBody ConfirmacaoSolicitacaoDTO dto) {
        return service.confirmar(id, dto);
    }

    @PatchMapping("/solicitacoes-agendamento/{id}/rejeitar")
    public SolicitacaoAgendamentoDTO rejeitar(@PathVariable Long id,
                                               @Valid @RequestBody RejeicaoSolicitacaoDTO dto) {
        return service.rejeitar(id, dto);
    }
}
