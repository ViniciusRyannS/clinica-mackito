package com.mackito.clinica.model.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public class AtendimentoRequestDTO {

    @NotNull(message = "O ID do paciente é obrigatório")
    private Long idPaciente;

    @NotNull(message = "O ID do médico é obrigatório")
    private Long idMedico;

    @NotNull(message = "A data do atendimento é obrigatória")
    @Future(message = "A data do atendimento deve ser futura")
    private LocalDate dataAtendimento;

    @NotNull(message = "A sala é obrigatória")
    @Size(min = 1, max = 50, message = "A sala deve ter entre 1 e 50 caracteres")
    private String sala;

    public Long getIdPaciente() {
        return idPaciente;
    }

    public void setIdPaciente(Long idPaciente) {
        this.idPaciente = idPaciente;
    }

    public Long getIdMedico() {
        return idMedico;
    }

    public void setIdMedico(Long idMedico) {
        this.idMedico = idMedico;
    }

    public LocalDate getDataAtendimento() {
        return dataAtendimento;
    }

    public void setDataAtendimento(LocalDate dataAtendimento) {
        this.dataAtendimento = dataAtendimento;
    }

    public String getSala() {
        return sala;
    }

    public void setSala(String sala) {
        this.sala = sala;
    }
}
