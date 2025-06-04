package com.mackito.clinica.controller;

import com.mackito.clinica.model.Usuario;
import com.mackito.clinica.model.dto.AutenticacaoDTO;
import com.mackito.clinica.model.dto.TokenDTO;
import com.mackito.clinica.repository.UsuarioRepository;
import com.mackito.clinica.service.TokenService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private TokenService tokenService;

    @PostMapping("/cadastrar")
public ResponseEntity<Usuario> cadastrarUsuario(@RequestBody @Valid Usuario usuario) {
    usuario.setSenha(passwordEncoder.encode(usuario.getSenha()));
    return ResponseEntity.ok(usuarioRepository.save(usuario));
}

@Autowired
private UsuarioRepository usuarioRepository;

@Autowired
private PasswordEncoder passwordEncoder;



   @PostMapping("/login")
public ResponseEntity<?> autenticar(@RequestBody @Valid AutenticacaoDTO dados) {
    var usuarioOptional = usuarioRepository.findByEmail(dados.getEmail());

    if (usuarioOptional.isEmpty()) {
        return ResponseEntity.status(403).body("Usuário não encontrado");
    }

    Usuario usuario = usuarioOptional.get();

    if (!passwordEncoder.matches(dados.getSenha(), usuario.getSenha())) {
        return ResponseEntity.status(403).body("Senha inválida");
    }

    var tokenAutenticacao = new UsernamePasswordAuthenticationToken(dados.getEmail(), dados.getSenha());
    Authentication authentication = authenticationManager.authenticate(tokenAutenticacao);

    String jwt = tokenService.gerarToken(usuario);
    return ResponseEntity.ok(new TokenDTO(jwt));
}
    
    

}
