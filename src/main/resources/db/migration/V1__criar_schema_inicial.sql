CREATE TABLE paciente (
    id BIGINT NOT NULL AUTO_INCREMENT,
    nome VARCHAR(100) NOT NULL,
    cpf VARCHAR(11) NOT NULL,
    email VARCHAR(255) NOT NULL,
    telefone VARCHAR(20) NOT NULL,
    CONSTRAINT pk_paciente PRIMARY KEY (id),
    CONSTRAINT uk_paciente_cpf UNIQUE (cpf),
    CONSTRAINT uk_paciente_email UNIQUE (email)
);

CREATE TABLE medico (
    id BIGINT NOT NULL AUTO_INCREMENT,
    nome VARCHAR(100) NOT NULL,
    crm VARCHAR(20) NOT NULL,
    especialidade VARCHAR(50) NOT NULL,
    CONSTRAINT pk_medico PRIMARY KEY (id),
    CONSTRAINT uk_medico_crm UNIQUE (crm)
);

CREATE TABLE usuarios (
    id BIGINT NOT NULL AUTO_INCREMENT,
    email VARCHAR(255) NOT NULL,
    senha VARCHAR(72) NOT NULL,
    perfil VARCHAR(20) NOT NULL,
    id_paciente BIGINT NULL,
    id_medico BIGINT NULL,
    CONSTRAINT pk_usuarios PRIMARY KEY (id),
    CONSTRAINT uk_usuarios_email UNIQUE (email),
    CONSTRAINT uk_usuarios_paciente UNIQUE (id_paciente),
    CONSTRAINT uk_usuarios_medico UNIQUE (id_medico),
    CONSTRAINT fk_usuarios_paciente FOREIGN KEY (id_paciente) REFERENCES paciente (id),
    CONSTRAINT fk_usuarios_medico FOREIGN KEY (id_medico) REFERENCES medico (id)
);

CREATE TABLE atendimentos (
    id BIGINT NOT NULL AUTO_INCREMENT,
    id_paciente BIGINT NOT NULL,
    id_medico BIGINT NOT NULL,
    id_usuario BIGINT NOT NULL,
    data_atendimento DATE NOT NULL,
    sala VARCHAR(50) NOT NULL,
    CONSTRAINT pk_atendimentos PRIMARY KEY (id),
    CONSTRAINT fk_atendimentos_paciente FOREIGN KEY (id_paciente) REFERENCES paciente (id),
    CONSTRAINT fk_atendimentos_medico FOREIGN KEY (id_medico) REFERENCES medico (id),
    CONSTRAINT fk_atendimentos_usuario FOREIGN KEY (id_usuario) REFERENCES usuarios (id)
);
