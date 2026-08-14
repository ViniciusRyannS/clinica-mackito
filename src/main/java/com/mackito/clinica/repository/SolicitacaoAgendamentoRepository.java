package com.mackito.clinica.repository;

import com.mackito.clinica.model.SolicitacaoAgendamento;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface SolicitacaoAgendamentoRepository extends JpaRepository<SolicitacaoAgendamento, Long> {
    List<SolicitacaoAgendamento> findByPacienteIdOrderByCriadaEmDesc(Long idPaciente);
    List<SolicitacaoAgendamento> findAllByOrderByCriadaEmDesc();
}
