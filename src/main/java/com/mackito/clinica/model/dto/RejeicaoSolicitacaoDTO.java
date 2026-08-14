package com.mackito.clinica.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RejeicaoSolicitacaoDTO(
        @NotBlank(message = "O motivo da rejeição é obrigatório")
        @Size(max = 500, message = "O motivo deve possuir no máximo 500 caracteres")
        String motivo) {
}
