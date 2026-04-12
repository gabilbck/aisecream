package com.aisecream.service;

import com.aisecream.dto.LoteProducaoForm;
import com.aisecream.model.LoteProducao;
import com.aisecream.model.Sabor;
import com.aisecream.model.Usuario;
import com.aisecream.repository.LoteProducaoRepository;
import com.aisecream.repository.SaborRepository;
import jakarta.transaction.Transactional;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LoteService {

    private final LoteProducaoRepository loteProducaoRepository;
    private final SaborRepository saborRepository;
    private final UsuarioService usuarioService;

    public LoteService(
            LoteProducaoRepository loteProducaoRepository,
            SaborRepository saborRepository,
            UsuarioService usuarioService
    ) {
        this.loteProducaoRepository = loteProducaoRepository;
        this.saborRepository = saborRepository;
        this.usuarioService = usuarioService;
    }

    public List<LoteProducao> listarTodos() {
        return loteProducaoRepository.findAllByOrderByDataProducaoDescIdDesc();
    }

    public List<LoteProducao> listarComEstoqueDisponivel() {
        return loteProducaoRepository.findByQuantidadeDisponivelGreaterThanOrderByDataProducaoDescIdDesc(0);
    }

    public LoteProducao buscarPorId(Integer id) {
        return loteProducaoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Lote não encontrado: " + id));
    }

    @Transactional
    public LoteProducao criar(LoteProducaoForm form) {
        Sabor sabor = saborRepository.findById(form.getSaborId())
                .orElseThrow(() -> new IllegalArgumentException("Sabor não encontrado."));
        if (!sabor.isAtivo()) {
            throw new IllegalArgumentException("Sabor inativo não pode ser usado em novo lote.");
        }
        Usuario criador = usuarioAutenticado();
        LoteProducao lote = new LoteProducao();
        lote.setSabor(sabor);
        lote.setQuantidadeProduzida(form.getQuantidadeProduzida());
        lote.setQuantidadeDisponivel(form.getQuantidadeProduzida());
        lote.setDataProducao(form.getDataProducao());
        lote.setCriadoPor(criador);
        return loteProducaoRepository.save(lote);
    }

    private Usuario usuarioAutenticado() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            throw new IllegalStateException("Usuário não autenticado.");
        }
        return usuarioService.buscarPorEmail(auth.getName());
    }
}
