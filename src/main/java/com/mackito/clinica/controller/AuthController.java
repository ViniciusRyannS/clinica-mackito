package com.mackito.clinica.controller;

import com.mackito.clinica.model.Usuario;
import com.mackito.clinica.model.dto.AutenticacaoDTO;
import com.mackito.clinica.model.dto.TokenDTO;
import com.mackito.clinica.service.TokenService;

import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private TokenService tokenService;

    @PostMapping("/login")
    public ResponseEntity<TokenDTO> login(@RequestBody @Valid AutenticacaoDTO dados) {
        UsernamePasswordAuthenticationToken authToken =
                new UsernamePasswordAuthenticationToken(dados.getEmail(), dados.getSenha());

        Authentication authentication = authenticationManager.authenticate(authToken);

        String token = tokenService.gerarToken((Usuario) authentication.getPrincipal());

        return ResponseEntity.ok(new TokenDTO(token));
    }
}
