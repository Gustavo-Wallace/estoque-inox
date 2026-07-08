package br.com.estoqueinox.service;

import java.util.List;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.estoqueinox.dto.UsuarioForm;
import br.com.estoqueinox.model.PerfilUsuario;
import br.com.estoqueinox.model.Usuario;
import br.com.estoqueinox.repository.UsuarioRepository;
import jakarta.persistence.EntityNotFoundException;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public UsuarioService(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public List<Usuario> listarTodos() {
        return usuarioRepository.findAllByOrderByNomeAsc();
    }

    public Usuario buscarPorId(Long id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Usuario nao encontrado."));
    }

    public String buscarNomePorUsername(String username) {
        return usuarioRepository.findByUsername(username)
                .map(Usuario::getNome)
                .orElse(username);
    }

    public void validarFormulario(UsuarioForm form, Long usuarioId) {
        String username = normalizar(form.getUsername());
        if (username == null) {
            return;
        }

        boolean usernameEmUso = usuarioId == null
                ? usuarioRepository.existsByUsername(username)
                : usuarioRepository.existsByUsernameAndIdNot(username, usuarioId);

        if (usernameEmUso) {
            throw new IllegalArgumentException("Ja existe um usuario com este username.");
        }

        if (usuarioId == null && (form.getSenha() == null || form.getSenha().isBlank())) {
            throw new IllegalArgumentException("Informe a senha do usuario.");
        }
    }

    @Transactional
    public Usuario criar(UsuarioForm form) {
        validarFormulario(form, null);
        Usuario usuario = new Usuario();
        aplicarFormulario(usuario, form);
        usuario.setSenha(passwordEncoder.encode(form.getSenha()));
        return usuarioRepository.save(usuario);
    }

    @Transactional
    public Usuario atualizar(Long id, UsuarioForm form, String usernameLogado) {
        validarFormulario(form, id);
        Usuario usuario = buscarPorId(id);
        validarAlteracaoCritica(usuario, form, usernameLogado);
        aplicarFormulario(usuario, form);
        return usuario;
    }

    @Transactional
    public void alterarSenha(Long id, String novaSenha) {
        if (novaSenha == null || novaSenha.isBlank()) {
            throw new IllegalArgumentException("Informe a nova senha.");
        }

        Usuario usuario = buscarPorId(id);
        usuario.setSenha(passwordEncoder.encode(novaSenha));
    }

    @Transactional
    public void alternarStatus(Long id, String usernameLogado) {
        Usuario usuario = buscarPorId(id);
        boolean novoStatus = !Boolean.TRUE.equals(usuario.getAtivo());

        if (!novoStatus) {
            if (usuario.getUsername().equals(usernameLogado)) {
                throw new IllegalArgumentException("Voce nao pode desativar o proprio usuario logado.");
            }
            validarUltimoAdminAtivo(usuario);
        }

        usuario.setAtivo(novoStatus);
    }

    private void aplicarFormulario(Usuario usuario, UsuarioForm form) {
        usuario.setNome(form.getNome().trim());
        usuario.setUsername(form.getUsername().trim());
        usuario.setPerfil(form.getPerfil());
        usuario.setAtivo(Boolean.TRUE.equals(form.getAtivo()));
    }

    private void validarAlteracaoCritica(Usuario usuario, UsuarioForm form, String usernameLogado) {
        boolean vaiFicarInativo = !Boolean.TRUE.equals(form.getAtivo());
        boolean vaiDeixarDeSerAdmin = form.getPerfil() != PerfilUsuario.ADMIN;

        if (usuario.getUsername().equals(usernameLogado) && (vaiFicarInativo || vaiDeixarDeSerAdmin)) {
            throw new IllegalArgumentException("Voce nao pode remover seu proprio acesso administrativo.");
        }

        if (Boolean.TRUE.equals(usuario.getAtivo())
                && usuario.getPerfil() == PerfilUsuario.ADMIN
                && (vaiFicarInativo || vaiDeixarDeSerAdmin)) {
            validarUltimoAdminAtivo(usuario);
        }
    }

    private void validarUltimoAdminAtivo(Usuario usuario) {
        if (usuario.getPerfil() == PerfilUsuario.ADMIN
                && Boolean.TRUE.equals(usuario.getAtivo())
                && usuarioRepository.countByPerfilAndAtivoTrue(PerfilUsuario.ADMIN) <= 1) {
            throw new IllegalArgumentException("Nao e possivel desativar ou remover o ultimo ADMIN ativo.");
        }
    }

    private String normalizar(String valor) {
        if (valor == null || valor.isBlank()) {
            return null;
        }
        return valor.trim();
    }
}
