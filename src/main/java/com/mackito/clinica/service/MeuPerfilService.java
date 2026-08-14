package com.mackito.clinica.service;

import com.mackito.clinica.exception.ConflitoDadosException;
import com.mackito.clinica.exception.RecursoNaoEncontradoException;
import com.mackito.clinica.model.Atendimento;
import com.mackito.clinica.model.Paciente;
import com.mackito.clinica.model.PerfilUsuario;
import com.mackito.clinica.model.Usuario;
import com.mackito.clinica.model.dto.AtendimentoDTO;
import com.mackito.clinica.model.dto.MedicoDTO;
import com.mackito.clinica.model.dto.MeuPacienteRequestDTO;
import com.mackito.clinica.model.dto.PacienteDTO;
import com.mackito.clinica.repository.AtendimentoRepository;
import com.mackito.clinica.repository.PacienteRepository;
import com.mackito.clinica.repository.UsuarioRepository;
import com.mackito.clinica.security.SecurityUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class MeuPerfilService {

    private final UsuarioRepository usuarioRepository;
    private final PacienteRepository pacienteRepository;
    private final AtendimentoRepository atendimentoRepository;

    public MeuPerfilService(UsuarioRepository usuarioRepository,
                            PacienteRepository pacienteRepository,
                            AtendimentoRepository atendimentoRepository) {
        this.usuarioRepository = usuarioRepository;
        this.pacienteRepository = pacienteRepository;
        this.atendimentoRepository = atendimentoRepository;
    }

    @Transactional
    public PacienteDTO criarPaciente(MeuPacienteRequestDTO dto) {
        Usuario usuario = usuarioAutenticado();
        if (usuario.getPaciente() != null) {
            throw new ConflitoDadosException("A conta já possui um perfil de paciente");
        }
        if (pacienteRepository.existsByCpf(dto.cpf())) {
            throw new ConflitoDadosException("CPF já cadastrado");
        }
        if (pacienteRepository.existsByEmail(usuario.getEmail())) {
            throw new ConflitoDadosException("E-mail já cadastrado como paciente");
        }

        Paciente paciente = pacienteRepository.save(
                new Paciente(dto.nome(), dto.cpf(), usuario.getEmail(), dto.telefone()));
        usuario.setPaciente(paciente);
        usuarioRepository.save(usuario);
        return paraPacienteDTO(paciente);
    }

    @Transactional(readOnly = true)
    public PacienteDTO obterPaciente() {
        Paciente paciente = usuarioAutenticado().getPaciente();
        if (paciente == null) {
            throw new RecursoNaoEncontradoException("A conta ainda não possui um perfil de paciente");
        }
        return paraPacienteDTO(paciente);
    }

    @Transactional(readOnly = true)
    public MedicoDTO obterMedico() {
        var medico = usuarioAutenticado().getMedico();
        if (medico == null) {
            throw new RecursoNaoEncontradoException("A conta ainda não está vinculada a um médico");
        }
        return new MedicoDTO(medico);
    }

    @Transactional(readOnly = true)
    public List<AtendimentoDTO> listarAtendimentos() {
        Usuario usuario = usuarioAutenticado();
        List<Atendimento> atendimentos;

        if (usuario.getPerfil() == PerfilUsuario.PACIENTE) {
            if (usuario.getPaciente() == null) {
                throw new RecursoNaoEncontradoException("A conta ainda não possui um perfil de paciente");
            }
            atendimentos = atendimentoRepository.findByPacienteId(usuario.getPaciente().getId());
        } else if (usuario.getPerfil() == PerfilUsuario.MEDICO) {
            if (usuario.getMedico() == null) {
                throw new RecursoNaoEncontradoException("A conta ainda não está vinculada a um médico");
            }
            atendimentos = atendimentoRepository.findByMedicoId(usuario.getMedico().getId());
        } else {
            throw new IllegalArgumentException("O perfil autenticado não possui atendimentos próprios");
        }

        return atendimentos.stream().map(this::paraAtendimentoDTO).toList();
    }

    private Usuario usuarioAutenticado() {
        return usuarioRepository.findByEmail(SecurityUtil.getEmailUsuarioLogado())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Usuário autenticado não encontrado"));
    }

    private PacienteDTO paraPacienteDTO(Paciente paciente) {
        return new PacienteDTO(paciente.getId(), paciente.getNome(), paciente.getCpf(),
                paciente.getEmail(), paciente.getTelefone());
    }

    private AtendimentoDTO paraAtendimentoDTO(Atendimento atendimento) {
        return new AtendimentoDTO(atendimento.getId(), atendimento.getPaciente().getId(),
                atendimento.getPaciente().getNome(), atendimento.getMedico().getId(),
                atendimento.getMedico().getNome(), atendimento.getDataAtendimento(), atendimento.getHoraInicial(),
                atendimento.getDuracaoMinutos(), atendimento.getSala());
    }
}
