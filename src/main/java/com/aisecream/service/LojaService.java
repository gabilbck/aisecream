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

    public List<Loja> listarAtivas() {
        return lojaRepository.findByAtivoTrueOrderByNomeAsc();
    }

    public Loja buscarPorId(Integer id) {
        return lojaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Loja não encontrada: " + id));
    }

    @Transactional
    public Loja salvar(Loja loja) {
        normalizar(loja);
        return lojaRepository.save(loja);
    }

    @Transactional
    public Loja atualizar(Integer id, Loja dados) {
        Loja loja = buscarPorId(id);
        normalizar(dados);
        loja.setNome(dados.getNome());
        loja.setCep(dados.getCep());
        loja.setEstado(dados.getEstado());
        loja.setCidade(dados.getCidade());
        loja.setLogradouro(dados.getLogradouro());
        loja.setNumero(dados.getNumero());
        loja.setComplemento(dados.getComplemento());
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

    private static void normalizar(Loja loja) {
        normalizarTelefoneVazio(loja);
        normalizarEndereco(loja);
    }

    private static void normalizarEndereco(Loja loja) {
        loja.setCep(formatarCep(loja.getCep()));
        loja.setEstado(trimToUpper(loja.getEstado()));
        loja.setCidade(trim(loja.getCidade()));
        loja.setLogradouro(trim(loja.getLogradouro()));
        loja.setNumero(trim(loja.getNumero()));
        if (loja.getComplemento() != null && loja.getComplemento().isBlank()) {
            loja.setComplemento(null);
        } else if (loja.getComplemento() != null) {
            loja.setComplemento(loja.getComplemento().trim());
        }
    }

    private static String formatarCep(String cep) {
        if (cep == null) {
            return null;
        }
        String digits = cep.replaceAll("\\D", "");
        if (digits.length() != 8) {
            return cep.trim();
        }
        return digits.substring(0, 5) + "-" + digits.substring(5);
    }

    private static String trim(String value) {
        return value == null ? null : value.trim();
    }

    private static String trimToUpper(String value) {
        return value == null ? null : value.trim().toUpperCase();
    }

    private static void normalizarTelefoneVazio(Loja loja) {
        if (loja.getTelefone() != null && loja.getTelefone().isBlank()) {
            loja.setTelefone(null);
        }
    }
}
