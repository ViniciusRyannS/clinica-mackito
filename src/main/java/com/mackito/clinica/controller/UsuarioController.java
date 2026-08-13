package com.mackito.clinica.controller;

import com.mackito.clinica.model.Usuario;
import com.mackito.clinica.model.dto.CadastroUsuarioInternoDTO;
import com.mackito.clinica.model.dto.UsuarioResponseDTO;
import com.mackito.clinica.service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/usuarios")
public class UsuarioController {

    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @PostMapping
    public ResponseEntity<UsuarioResponseDTO> criarContaInterna(
            @RequestBody @Valid CadastroUsuarioInternoDTO dados) {
        Usuario usuario = usuarioService.criarContaInterna(dados);
        return ResponseEntity.status(HttpStatus.CREATED).body(new UsuarioResponseDTO(usuario));
    }
}
