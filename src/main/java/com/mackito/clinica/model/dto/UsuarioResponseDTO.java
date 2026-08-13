package com.mackito.clinica.model.dto;

import com.mackito.clinica.model.PerfilUsuario;
import com.mackito.clinica.model.Usuario;

public record UsuarioResponseDTO(Long id, String email, PerfilUsuario perfil) {

    public UsuarioResponseDTO(Usuario usuario) {
        this(usuario.getId(), usuario.getEmail(), usuario.getPerfil());
    }
}
