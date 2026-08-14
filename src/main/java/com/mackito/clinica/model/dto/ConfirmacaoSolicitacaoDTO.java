package com.mackito.clinica.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.time.LocalTime;

public record ConfirmacaoSolicitacaoDTO(
        @NotNull(message = "A hora inicial é obrigatória")
        LocalTime horaInicial,
        @NotNull(message = "A duração é obrigatória")
        @Min(value = 15, message = "A duração mínima é de 15 minutos")
        @Max(value = 240, message = "A duração máxima é de 240 minutos")
        Integer duracaoMinutos,
        @NotBlank(message = "A sala é obrigatória")
        @Size(max = 50, message = "A sala deve possuir no máximo 50 caracteres")
        String sala) {
}
