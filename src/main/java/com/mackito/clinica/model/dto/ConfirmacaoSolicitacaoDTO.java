package com.mackito.clinica.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ConfirmacaoSolicitacaoDTO(
        @NotBlank(message = "A sala é obrigatória")
        @Size(max = 50, message = "A sala deve possuir no máximo 50 caracteres")
        String sala) {
}
