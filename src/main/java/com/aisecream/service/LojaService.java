package com.aisecream.service;

import com.aisecream.model.Loja;
import com.aisecream.repository.LojaRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LojaService {

    private final LojaRepository lojaRepository;

    public LojaService(LojaRepository lojaRepository) {
        this.lojaRepository = lojaRepository;
    }

    public List<Loja> listarTodos() {
        return lojaRepository.findAllByOrderByNomeAsc();
    }

    public Loja buscarPorId(Integer id) {
        return lojaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Loja não encontrada: " + id));
    }

    @Transactional
    public Loja salvar(Loja loja) {
        normalizarTelefoneVazio(loja);
        return lojaRepository.save(loja);
    }

    @Transactional
    public Loja atualizar(Integer id, Loja dados) {
        Loja loja = buscarPorId(id);
        normalizarTelefoneVazio(dados);
        loja.setNome(dados.getNome());
        loja.setEndereco(dados.getEndereco());
        loja.setTelefone(dados.getTelefone());
        loja.setAtivo(dados.getAtivo());
        return lojaRepository.save(loja);
    }

    @Transactional
    public void inativar(Integer id) {
        Loja loja = buscarPorId(id);
        loja.setAtivo(false);
        lojaRepository.save(loja);
    }

    private static void normalizarTelefoneVazio(Loja loja) {
        if (loja.getTelefone() != null && loja.getTelefone().isBlank()) {
            loja.setTelefone(null);
        }
    }
}
