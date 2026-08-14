ALTER TABLE solicitacoes_agendamento ADD COLUMN hora_preferida TIME NULL;
UPDATE solicitacoes_agendamento SET hora_preferida = '08:00:00' WHERE hora_preferida IS NULL;
ALTER TABLE solicitacoes_agendamento MODIFY COLUMN hora_preferida TIME NOT NULL;

ALTER TABLE atendimentos ADD COLUMN hora_inicial TIME NULL;
ALTER TABLE atendimentos ADD COLUMN duracao_minutos INT NULL;
UPDATE atendimentos SET hora_inicial = '08:00:00', duracao_minutos = 30
WHERE hora_inicial IS NULL OR duracao_minutos IS NULL;
ALTER TABLE atendimentos MODIFY COLUMN hora_inicial TIME NOT NULL;
ALTER TABLE atendimentos MODIFY COLUMN duracao_minutos INT NOT NULL;
