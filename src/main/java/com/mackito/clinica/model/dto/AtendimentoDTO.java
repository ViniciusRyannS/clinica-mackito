package com.mackito.clinica.model.dto;

import java.time.LocalDate;

public class AtendimentoDTO {

    private Long id;
    private Long idPaciente;
    private String nomePaciente;
    private Long idMedico;
    private String nomeMedico;
    private LocalDate dataAtendimento;
    private String sala;

    public AtendimentoDTO(Long id, Long idPaciente, String nomePaciente, Long idMedico, String nomeMedico, LocalDate dataAtendimento, String sala) {
        this.id = id;
        this.idPaciente = idPaciente;
        this.nomePaciente = nomePaciente;
        this.idMedico = idMedico;
        this.nomeMedico = nomeMedico;
        this.dataAtendimento = dataAtendimento;
        this.sala = sala;
    }

    

    // Getters
    public Long getId() {
        return id;
    }

    public Long getIdPaciente() {
        return idPaciente;
    }

    public String getNomePaciente() {
        return nomePaciente;
    }

    public Long getIdMedico() {
        return idMedico;
    }

    public String getNomeMedico() {
        return nomeMedico;
    }

    public LocalDate getDataAtendimento() {
        return dataAtendimento;
    }

    public String getSala() {
        return sala;
    }

    // Setters
    public void setId(Long id) {
        this.id = id;
    }

    public void setIdPaciente(Long idPaciente) {
        this.idPaciente = idPaciente;
    }

    public void setNomePaciente(String nomePaciente) {
        this.nomePaciente = nomePaciente;
    }

    public void setIdMedico(Long idMedico) {
        this.idMedico = idMedico;
    }

    public void setNomeMedico(String nomeMedico) {
        this.nomeMedico = nomeMedico;
    }

    public void setDataAtendimento(LocalDate dataAtendimento) {
        this.dataAtendimento = dataAtendimento;
    }

    public void setSala(String sala) {
        this.sala = sala;
    }
}
