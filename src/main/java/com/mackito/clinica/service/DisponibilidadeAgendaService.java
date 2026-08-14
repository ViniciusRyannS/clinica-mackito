package com.mackito.clinica.service;

import com.mackito.clinica.exception.ConflitoDadosException;
import com.mackito.clinica.model.Atendimento;
import com.mackito.clinica.repository.AtendimentoRepository;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Service
public class DisponibilidadeAgendaService {
    private final AtendimentoRepository atendimentoRepository;

    public DisponibilidadeAgendaService(AtendimentoRepository atendimentoRepository) {
        this.atendimentoRepository = atendimentoRepository;
    }

    public void validar(Long idMedico, LocalDate data, LocalTime inicio, int duracaoMinutos, String sala) {
        LocalTime fim = inicio.plusMinutes(duracaoMinutos);
        if (!fim.isAfter(inicio)) {
            throw new IllegalArgumentException("O atendimento deve terminar no mesmo dia");
        }
        if (possuiSobreposicao(atendimentoRepository.findByMedicoIdAndDataAtendimento(idMedico, data), inicio, fim)) {
            throw new ConflitoDadosException("O médico já possui atendimento nesse intervalo");
        }
        if (possuiSobreposicao(atendimentoRepository.findBySalaAndDataAtendimento(sala, data), inicio, fim)) {
            throw new ConflitoDadosException("A sala já está ocupada nesse intervalo");
        }
    }

    private boolean possuiSobreposicao(List<Atendimento> existentes, LocalTime inicio, LocalTime fim) {
        return existentes.stream().anyMatch(atendimento -> {
            LocalTime inicioExistente = atendimento.getHoraInicial();
            LocalTime fimExistente = inicioExistente.plusMinutes(atendimento.getDuracaoMinutos());
            return inicio.isBefore(fimExistente) && fim.isAfter(inicioExistente);
        });
    }
}
