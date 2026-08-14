package com.mackito.clinica.model.dto;

import com.mackito.clinica.model.StatusSolicitacao;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record SolicitacaoAgendamentoDTO(
        Long id,
        Long idPaciente,
        String nomePaciente,
        Long idMedico,
        String nomeMedico,
        LocalDate dataPreferida,
        String observacao,
        StatusSolicitacao status,
        String motivoRejeicao,
        Long idAtendimento,
        LocalDateTime criadaEm,
        LocalDateTime decididaEm) {
}
