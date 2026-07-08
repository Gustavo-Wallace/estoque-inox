package br.com.estoqueinox.dto;

import br.com.estoqueinox.model.PerfilUsuario;
import br.com.estoqueinox.model.Usuario;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class UsuarioForm {

    @NotBlank(message = "Informe o nome.")
    private String nome;

    @NotBlank(message = "Informe o username.")
    @Size(max = 60, message = "O username deve ter no maximo 60 caracteres.")
    private String username;

    private String senha;

    @NotNull(message = "Selecione o perfil.")
    private PerfilUsuario perfil;

    private Boolean ativo = true;

    public static UsuarioForm from(Usuario usuario) {
        UsuarioForm form = new UsuarioForm();
        form.setNome(usuario.getNome());
        form.setUsername(usuario.getUsername());
        form.setPerfil(usuario.getPerfil());
        form.setAtivo(usuario.getAtivo());
        return form;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public PerfilUsuario getPerfil() {
        return perfil;
    }

    public void setPerfil(PerfilUsuario perfil) {
        this.perfil = perfil;
    }

    public Boolean getAtivo() {
        return ativo;
    }

    public void setAtivo(Boolean ativo) {
        this.ativo = ativo;
    }
}
