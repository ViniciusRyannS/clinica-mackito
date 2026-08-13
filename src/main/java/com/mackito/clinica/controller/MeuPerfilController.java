package com.mackito.clinica.controller;

import com.mackito.clinica.model.dto.AtendimentoDTO;
import com.mackito.clinica.model.dto.MedicoDTO;
import com.mackito.clinica.model.dto.MeuPacienteRequestDTO;
import com.mackito.clinica.model.dto.PacienteDTO;
import com.mackito.clinica.service.MeuPerfilService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/me")
public class MeuPerfilController {

    private final MeuPerfilService meuPerfilService;

    public MeuPerfilController(MeuPerfilService meuPerfilService) {
        this.meuPerfilService = meuPerfilService;
    }

    @PostMapping("/paciente")
    public ResponseEntity<PacienteDTO> criarPaciente(@Valid @RequestBody MeuPacienteRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(meuPerfilService.criarPaciente(dto));
    }

    @GetMapping("/paciente")
    public PacienteDTO obterPaciente() {
        return meuPerfilService.obterPaciente();
    }

    @GetMapping("/medico")
    public MedicoDTO obterMedico() {
        return meuPerfilService.obterMedico();
    }

    @GetMapping("/atendimentos")
    public List<AtendimentoDTO> listarAtendimentos() {
        return meuPerfilService.listarAtendimentos();
    }
}
