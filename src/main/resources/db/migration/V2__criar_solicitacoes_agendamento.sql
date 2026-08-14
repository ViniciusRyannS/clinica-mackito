CREATE TABLE solicitacoes_agendamento (
    id BIGINT NOT NULL AUTO_INCREMENT,
    id_paciente BIGINT NOT NULL,
    id_medico BIGINT NOT NULL,
    data_preferida DATE NOT NULL,
    observacao VARCHAR(500) NULL,
    status VARCHAR(20) NOT NULL,
    motivo_rejeicao VARCHAR(500) NULL,
    id_responsavel BIGINT NULL,
    id_atendimento BIGINT NULL,
    criada_em DATETIME NOT NULL,
    decidida_em DATETIME NULL,
    CONSTRAINT pk_solicitacoes_agendamento PRIMARY KEY (id),
    CONSTRAINT uk_solicitacoes_atendimento UNIQUE (id_atendimento),
    CONSTRAINT fk_solicitacoes_paciente FOREIGN KEY (id_paciente) REFERENCES paciente (id),
    CONSTRAINT fk_solicitacoes_medico FOREIGN KEY (id_medico) REFERENCES medico (id),
    CONSTRAINT fk_solicitacoes_responsavel FOREIGN KEY (id_responsavel) REFERENCES usuarios (id),
    CONSTRAINT fk_solicitacoes_atendimento FOREIGN KEY (id_atendimento) REFERENCES atendimentos (id)
);
