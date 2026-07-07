package br.com.estoqueinox.dto;

import br.com.estoqueinox.model.Categoria;
import jakarta.validation.constraints.NotBlank;

public class CategoriaForm {

    @NotBlank(message = "Informe o nome da categoria.")
    private String nome;

    private Boolean ativa = true;

    public static CategoriaForm from(Categoria categoria) {
        CategoriaForm form = new CategoriaForm();
        form.setNome(categoria.getNome());
        form.setAtiva(categoria.getAtiva());
        return form;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public Boolean getAtiva() {
        return ativa;
    }

    public void setAtiva(Boolean ativa) {
        this.ativa = ativa;
    }
}
