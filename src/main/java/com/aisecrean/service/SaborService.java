package com.aisecrean.service;

import com.aisecrean.model.Sabor;
import com.aisecrean.repository.SaborRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SaborService {

    private final SaborRepository saborRepository;

    public SaborService(SaborRepository saborRepository) {
        this.saborRepository = saborRepository;
    }

    public List<Sabor> listarTodos() {
        return saborRepository.findAllByOrderByNomeAsc();
    }

    public List<Sabor> listarAtivos() {
        return saborRepository.findByAtivoTrueOrderByNomeAsc();
    }

    public Sabor buscarPorId(Integer id) {
        return saborRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Sabor não encontrado: " + id));
    }

    @Transactional
    public Sabor salvar(Sabor sabor) {
        if (saborRepository.existsByNomeIgnoreCase(sabor.getNome())) {
            throw new IllegalArgumentException("Já existe um sabor com o nome: " + sabor.getNome());
        }
        return saborRepository.save(sabor);
    }

    @Transactional
    public Sabor atualizar(Integer id, Sabor dadosAtualizados) {
        Sabor sabor = buscarPorId(id);
        sabor.setNome(dadosAtualizados.getNome());
        sabor.setDescricao(dadosAtualizados.getDescricao());
        sabor.setAtivo(dadosAtualizados.getAtivo());
        return saborRepository.save(sabor);
    }

    @Transactional
    public void inativar(Integer id) {
        Sabor sabor = buscarPorId(id);
        sabor.setAtivo(false);
        saborRepository.save(sabor);
    }
}
