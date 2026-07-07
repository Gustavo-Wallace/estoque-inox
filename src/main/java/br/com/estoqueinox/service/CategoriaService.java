package br.com.estoqueinox.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.estoqueinox.dto.CategoriaForm;
import br.com.estoqueinox.model.Categoria;
import br.com.estoqueinox.repository.CategoriaRepository;
import jakarta.persistence.EntityNotFoundException;

@Service
public class CategoriaService {

    private final CategoriaRepository categoriaRepository;

    public CategoriaService(CategoriaRepository categoriaRepository) {
        this.categoriaRepository = categoriaRepository;
    }

    public List<Categoria> listarTodas() {
        return categoriaRepository.findAllByOrderByNomeAsc();
    }

    public List<Categoria> listarAtivas() {
        return categoriaRepository.findByAtivaTrueOrderByNomeAsc();
    }

    public Categoria buscarPorId(Long id) {
        return categoriaRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Categoria nao encontrada."));
    }

    @Transactional
    public Categoria salvar(CategoriaForm form) {
        Categoria categoria = new Categoria();
        aplicarFormulario(categoria, form);
        return categoriaRepository.save(categoria);
    }

    @Transactional
    public Categoria atualizar(Long id, CategoriaForm form) {
        Categoria categoria = buscarPorId(id);
        aplicarFormulario(categoria, form);
        return categoria;
    }

    @Transactional
    public void alternarStatus(Long id) {
        Categoria categoria = buscarPorId(id);
        categoria.setAtiva(!Boolean.TRUE.equals(categoria.getAtiva()));
    }

    private void aplicarFormulario(Categoria categoria, CategoriaForm form) {
        categoria.setNome(form.getNome().trim());
        categoria.setAtiva(Boolean.TRUE.equals(form.getAtiva()));
    }
}
