package com.aisecream.service;

import com.aisecream.dto.DistribuicaoForm;
import com.aisecream.model.Distribuicao;
import com.aisecream.model.Loja;
import com.aisecream.model.LoteProducao;
import com.aisecream.model.Usuario;
import com.aisecream.model.enums.StatusDistribuicao;
import com.aisecream.repository.DistribuicaoRepository;
import com.aisecream.repository.LojaRepository;
import com.aisecream.repository.LoteProducaoRepository;
import jakarta.transaction.Transactional;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DistribuicaoService {

    private final DistribuicaoRepository distribuicaoRepository;
    private final LoteProducaoRepository loteProducaoRepository;
    private final LojaRepository lojaRepository;
    private final UsuarioService usuarioService;

    public DistribuicaoService(
            DistribuicaoRepository distribuicaoRepository,
            LoteProducaoRepository loteProducaoRepository,
            LojaRepository lojaRepository,
            UsuarioService usuarioService
    ) {
        this.distribuicaoRepository = distribuicaoRepository;
        this.loteProducaoRepository = loteProducaoRepository;
        this.lojaRepository = lojaRepository;
        this.usuarioService = usuarioService;
    }

    public List<Distribuicao> listarTodos() {
        return distribuicaoRepository.findAllByOrderByDistribuidoEmDescIdDesc();
    }

    @Transactional
    public Distribuicao registrar(DistribuicaoForm form) {
        LoteProducao lote = loteProducaoRepository.findById(form.getLoteId())
                .orElseThrow(() -> new IllegalArgumentException("Lote não encontrado."));
        Loja loja = lojaRepository.findById(form.getLojaId())
                .orElseThrow(() -> new IllegalArgumentException("Loja não encontrada."));
        if (!loja.isAtivo()) {
            throw new IllegalArgumentException("Não é possível distribuir para loja inativa.");
        }
        int quantidadeLoteInicial = lote.getQuantidadeProduzida();
        lote.decrementarDisponivel(form.getQuantidade());
        int saldoCdApos = lote.getQuantidadeDisponivel();
        Usuario criador = usuarioAutenticado();
        Distribuicao d = new Distribuicao();
        d.setLote(lote);
        d.setLoja(loja);
        d.setQuantidade(form.getQuantidade());
        d.setQuantidadeLoteInicial(quantidadeLoteInicial);
        d.setSaldoDisponivelCdApos(saldoCdApos);
        d.setStatus(StatusDistribuicao.ATIVA);
        d.setCriadoPor(criador);
        return distribuicaoRepository.save(d);
    }

    @Transactional
    public void cancelar(Integer id) {
        Distribuicao d = distribuicaoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Distribuição não encontrada."));
        if (d.getStatus() != StatusDistribuicao.ATIVA) {
            throw new IllegalArgumentException("Somente distribuições ativas podem ser canceladas.");
        }
        d.getLote().incrementarDisponivel(d.getQuantidade());
        d.cancelar();
        distribuicaoRepository.save(d);
    }

    private Usuario usuarioAutenticado() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            throw new IllegalStateException("Usuário não autenticado.");
        }
        return usuarioService.buscarPorEmail(auth.getName());
    }
}
