package com.mackito.clinica.repository;

import com.mackito.clinica.model.Atendimento;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.time.LocalDate;

public interface AtendimentoRepository extends JpaRepository<Atendimento, Long> {
    List<Atendimento> findByPacienteId(Long id);
    List<Atendimento> findByMedicoId(Long id);
    List<Atendimento> findByMedicoIdAndDataAtendimento(Long id, LocalDate data);
    List<Atendimento> findBySalaAndDataAtendimento(String sala, LocalDate data);
    
}
