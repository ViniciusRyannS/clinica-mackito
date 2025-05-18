package com.mackito.clinica.model.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public class AtendimentoRequestDTO {

    @NotNull(message = "O ID do paciente é obrigatório.")
    private Long idPaciente;

    @NotNull(message = "O ID do médico é obrigatório.")
    private Long idMedico;

    @NotNull(message = "A data do atendimento é obrigatória.")
    @Future(message = "A data do atendimento deve ser no futuro.")
    private LocalDate dataAtendimento;

    private String sala; // Opcional

    // Getters e setters
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
