package com.mackito.clinica.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "solicitacoes_agendamento")
public class SolicitacaoAgendamento {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_paciente", nullable = false)
    private Paciente paciente;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_medico", nullable = false)
    private Medico medico;

    @Column(name = "data_preferida", nullable = false)
    private LocalDate dataPreferida;

    @Column(length = 500)
    private String observacao;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private StatusSolicitacao status = StatusSolicitacao.PENDENTE;

    @Column(name = "motivo_rejeicao", length = 500)
    private String motivoRejeicao;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_responsavel")
    private Usuario responsavel;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_atendimento", unique = true)
    private Atendimento atendimento;

    @Column(name = "criada_em", nullable = false)
    private LocalDateTime criadaEm;

    @Column(name = "decidida_em")
    private LocalDateTime decididaEm;

    public SolicitacaoAgendamento() {}

    public SolicitacaoAgendamento(Paciente paciente, Medico medico, LocalDate dataPreferida, String observacao) {
        this.paciente = paciente;
        this.medico = medico;
        this.dataPreferida = dataPreferida;
        this.observacao = observacao;
        this.criadaEm = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public Paciente getPaciente() { return paciente; }
    public Medico getMedico() { return medico; }
    public LocalDate getDataPreferida() { return dataPreferida; }
    public String getObservacao() { return observacao; }
    public StatusSolicitacao getStatus() { return status; }
    public String getMotivoRejeicao() { return motivoRejeicao; }
    public Usuario getResponsavel() { return responsavel; }
    public Atendimento getAtendimento() { return atendimento; }
    public LocalDateTime getCriadaEm() { return criadaEm; }
    public LocalDateTime getDecididaEm() { return decididaEm; }

    public void confirmar(Usuario responsavel, Atendimento atendimento) {
        this.status = StatusSolicitacao.CONFIRMADA;
        this.responsavel = responsavel;
        this.atendimento = atendimento;
        this.decididaEm = LocalDateTime.now();
    }

    public void rejeitar(Usuario responsavel, String motivo) {
        this.status = StatusSolicitacao.REJEITADA;
        this.responsavel = responsavel;
        this.motivoRejeicao = motivo;
        this.decididaEm = LocalDateTime.now();
    }
}
