package com.mackito.clinica.model.dto;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;

public record SolicitacaoAgendamentoRequestDTO(
        @NotNull(message = "O médico é obrigatório")
        @Positive(message = "O identificador do médico deve ser positivo")
        Long idMedico,
        @NotNull(message = "A data preferida é obrigatória")
        @FutureOrPresent(message = "A data preferida não pode estar no passado")
        LocalDate dataPreferida,
        @Size(max = 500, message = "A observação deve possuir no máximo 500 caracteres")
        String observacao) {
}
