package com.mackito.clinica.model.dto;

import java.time.LocalDate;

public class AtendimentoDTO {

    private Long id;
    private Long idPaciente;
    private Long idMedico;
    private LocalDate dataAtendimento;
    private String sala;

    public AtendimentoDTO(Long id, Long idPaciente, Long idMedico, LocalDate dataAtendimento, String sala) {
        this.id = id;
        this.idPaciente = idPaciente;
        this.idMedico = idMedico;
        this.dataAtendimento = dataAtendimento;
        this.sala = sala;
    }

    // Getters e Setters
    public Long getId() {
        return id;
    }

    public Long getIdPaciente() {
        return idPaciente;
    }

    public Long getIdMedico() {
        return idMedico;
    }

    public LocalDate getDataAtendimento() {
        return dataAtendimento;
    }

    public String getSala() {
        return sala;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setIdPaciente(Long idPaciente) {
        this.idPaciente = idPaciente;
    }

    public void setIdMedico(Long idMedico) {
        this.idMedico = idMedico;
    }

    public void setDataAtendimento(LocalDate dataAtendimento) {
        this.dataAtendimento = dataAtendimento;
    }

    public void setSala(String sala) {
        this.sala = sala;
    }
}
