package com.mackito.clinica.model.dto;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public class AtendimentoRequestDTO {
    @NotNull(message = "O paciente é obrigatório")
    @Positive(message = "O identificador do paciente deve ser positivo")
    private Long idPaciente;
    @NotNull(message = "O médico é obrigatório")
    @Positive(message = "O identificador do médico deve ser positivo")
    private Long idMedico;
    @NotNull(message = "O usuário é obrigatório")
    @Positive(message = "O identificador do usuário deve ser positivo")
    private Long idUsuario;
    @NotNull(message = "A data do atendimento é obrigatória")
    @FutureOrPresent(message = "A data do atendimento não pode estar no passado")
    private LocalDate dataAtendimento;
    @NotBlank(message = "A sala é obrigatória")
    @Size(max = 50, message = "A sala deve possuir no máximo 50 caracteres")
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

    public Long getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(Long idUsuario) {
        this.idUsuario = idUsuario;
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
