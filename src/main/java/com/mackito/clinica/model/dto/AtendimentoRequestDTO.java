package com.mackito.clinica.model.dto;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.time.LocalTime;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;

public class AtendimentoRequestDTO {
    @NotNull(message = "O paciente é obrigatório")
    @Positive(message = "O identificador do paciente deve ser positivo")
    private Long idPaciente;
    @NotNull(message = "O médico é obrigatório")
    @Positive(message = "O identificador do médico deve ser positivo")
    private Long idMedico;
    @NotNull(message = "A data do atendimento é obrigatória")
    @FutureOrPresent(message = "A data do atendimento não pode estar no passado")
    private LocalDate dataAtendimento;
    @NotNull(message = "A hora inicial é obrigatória")
    private LocalTime horaInicial;
    @NotNull(message = "A duração é obrigatória")
    @Min(value = 15, message = "A duração mínima é de 15 minutos")
    @Max(value = 240, message = "A duração máxima é de 240 minutos")
    private Integer duracaoMinutos;
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

    public LocalDate getDataAtendimento() {
        return dataAtendimento;
    }

    public void setDataAtendimento(LocalDate dataAtendimento) {
        this.dataAtendimento = dataAtendimento;
    }
    public LocalTime getHoraInicial() { return horaInicial; }
    public void setHoraInicial(LocalTime horaInicial) { this.horaInicial = horaInicial; }
    public Integer getDuracaoMinutos() { return duracaoMinutos; }
    public void setDuracaoMinutos(Integer duracaoMinutos) { this.duracaoMinutos = duracaoMinutos; }

    public String getSala() {
        return sala;
    }

    public void setSala(String sala) {
        this.sala = sala;
    }
}
