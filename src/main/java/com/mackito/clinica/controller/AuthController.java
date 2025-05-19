package com.mackito.clinica.controller;

import com.mackito.clinica.model.Usuario;
import com.mackito.clinica.model.dto.AutenticacaoDTO;
import com.mackito.clinica.model.dto.TokenDTO;
import com.mackito.clinica.service.TokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private TokenService tokenService;

    @PostMapping("/login")
    public ResponseEntity<?> autenticar(@RequestBody AutenticacaoDTO dto) {
        try {
            UsernamePasswordAuthenticationToken dadosLogin =
                    new UsernamePasswordAuthenticationToken(dto.getEmail(), dto.getSenha());

            Authentication authentication = authenticationManager.authenticate(dadosLogin);

            Usuario usuario = (Usuario) authentication.getPrincipal();
            String token = tokenService.gerarToken(usuario);

            return ResponseEntity.ok(new TokenDTO(token));
        } catch (AuthenticationException e) {
            return ResponseEntity.status(401).body("Email ou senha inválidos");
        }
    }
}
