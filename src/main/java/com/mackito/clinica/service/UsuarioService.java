package com.mackito.clinica.service;

import com.mackito.clinica.exception.ConflitoDadosException;
import com.mackito.clinica.exception.RecursoNaoEncontradoException;
import com.mackito.clinica.model.Medico;
import com.mackito.clinica.model.PerfilUsuario;
import com.mackito.clinica.model.Usuario;
import com.mackito.clinica.model.dto.CadastroUsuarioInternoDTO;
import com.mackito.clinica.repository.MedicoRepository;
import com.mackito.clinica.repository.UsuarioRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final MedicoRepository medicoRepository;
    private final PasswordEncoder passwordEncoder;

    public UsuarioService(
            UsuarioRepository usuarioRepository,
            MedicoRepository medicoRepository,
            PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.medicoRepository = medicoRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public Usuario criarContaInterna(CadastroUsuarioInternoDTO dados) {
        if (dados.getPerfil() == PerfilUsuario.PACIENTE) {
            throw new IllegalArgumentException("Contas de paciente devem usar o cadastro público");
        }
        if (usuarioRepository.existsByEmail(dados.getEmail())) {
            throw new ConflitoDadosException("Já existe uma conta cadastrada com este e-mail");
        }

        Usuario usuario = new Usuario(
                dados.getEmail(),
                passwordEncoder.encode(dados.getSenha()),
                dados.getPerfil());

        if (dados.getPerfil() == PerfilUsuario.MEDICO) {
            if (dados.getIdMedico() == null) {
                throw new IllegalArgumentException("O vínculo com o médico é obrigatório para este perfil");
            }
            Medico medico = medicoRepository.findById(dados.getIdMedico())
                    .orElseThrow(() -> new RecursoNaoEncontradoException(
                            "Médico não encontrado com o ID: " + dados.getIdMedico()));
            if (usuarioRepository.existsByMedicoId(medico.getId())) {
                throw new ConflitoDadosException("Este médico já possui uma conta vinculada");
            }
            usuario.setMedico(medico);
        } else if (dados.getIdMedico() != null) {
            throw new IllegalArgumentException("O vínculo com médico só pode ser usado no perfil MEDICO");
        }

        return usuarioRepository.save(usuario);
    }
}
