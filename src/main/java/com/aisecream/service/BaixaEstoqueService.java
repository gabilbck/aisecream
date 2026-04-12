package com.aisecream.service;

import com.aisecream.dto.BaixaEstoqueForm;
import com.aisecream.dto.LoteSaldoNaLojaView;
import com.aisecream.model.BaixaEstoque;
import com.aisecream.model.Loja;
import com.aisecream.model.LoteProducao;
import com.aisecream.model.Usuario;
import com.aisecream.model.enums.StatusDistribuicao;
import com.aisecream.repository.BaixaEstoqueRepository;
import com.aisecream.repository.DistribuicaoRepository;
import com.aisecream.repository.LojaRepository;
import com.aisecream.repository.LoteProducaoRepository;
import jakarta.transaction.Transactional;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
public class BaixaEstoqueService {

    private final BaixaEstoqueRepository baixaEstoqueRepository;
    private final DistribuicaoRepository distribuicaoRepository;
    private final LojaRepository lojaRepository;
    private final LoteProducaoRepository loteProducaoRepository;
    private final UsuarioService usuarioService;

    public BaixaEstoqueService(
            BaixaEstoqueRepository baixaEstoqueRepository,
            DistribuicaoRepository distribuicaoRepository,
            LojaRepository lojaRepository,
            LoteProducaoRepository loteProducaoRepository,
            UsuarioService usuarioService
    ) {
        this.baixaEstoqueRepository = baixaEstoqueRepository;
        this.distribuicaoRepository = distribuicaoRepository;
        this.lojaRepository = lojaRepository;
        this.loteProducaoRepository = loteProducaoRepository;
        this.usuarioService = usuarioService;
    }

    public List<BaixaEstoque> listar(Integer lojaIdFiltro) {
        if (lojaIdFiltro == null) {
            return baixaEstoqueRepository.findAllByOrderByCriadoEmDescIdDesc();
        }
        return baixaEstoqueRepository.findByLoja_IdOrderByCriadoEmDescIdDesc(lojaIdFiltro);
    }

    public int calcularSaldoDisponivelNaLoja(Integer lojaId, Integer loteId) {
        long recebido = distribuicaoRepository.sumQuantidadeByLojaAndLoteAndStatus(
                lojaId, loteId, StatusDistribuicao.ATIVA
        );
        long baixado = baixaEstoqueRepository.sumQuantidadeByLojaAndLote(lojaId, loteId);
        return (int) (recebido - baixado);
    }

    public List<LoteSaldoNaLojaView> listarLotesComSaldoNaLoja(Integer lojaId) {
        List<Integer> loteIds = distribuicaoRepository.findDistinctLoteIdsByLojaIdAndStatus(
                lojaId, StatusDistribuicao.ATIVA
        );
        List<LoteSaldoNaLojaView> resultado = new ArrayList<>();
        for (Integer loteId : loteIds) {
            int saldo = calcularSaldoDisponivelNaLoja(lojaId, loteId);
            if (saldo <= 0) {
                continue;
            }
            loteProducaoRepository.findById(loteId).ifPresent(lote -> resultado.add(new LoteSaldoNaLojaView(lote, saldo)));
        }
        resultado.sort(Comparator.comparing(v -> v.getLote().getSabor().getNome(), String.CASE_INSENSITIVE_ORDER));
        return resultado;
    }

    @Transactional
    public BaixaEstoque registrar(BaixaEstoqueForm form) {
        Loja loja = lojaRepository.findById(form.getLojaId())
                .orElseThrow(() -> new IllegalArgumentException("Loja não encontrada."));
        if (!loja.isAtivo()) {
            throw new IllegalArgumentException("Não é possível registrar baixa em loja inativa.");
        }
        LoteProducao lote = loteProducaoRepository.findById(form.getLoteId())
                .orElseThrow(() -> new IllegalArgumentException("Lote não encontrado."));
        int saldo = calcularSaldoDisponivelNaLoja(form.getLojaId(), form.getLoteId());
        if (form.getQuantidade() > saldo) {
            throw new IllegalArgumentException(
                    "Quantidade maior que o saldo disponível nesta loja para este lote (" + saldo + " un.)."
            );
        }
        Usuario criador = usuarioAutenticado();
        BaixaEstoque baixa = new BaixaEstoque();
        baixa.setLoja(loja);
        baixa.setLote(lote);
        baixa.setQuantidade(form.getQuantidade());
        baixa.setObservacao(normalizarObservacao(form.getObservacao()));
        baixa.setCriadoPor(criador);
        return baixaEstoqueRepository.save(baixa);
    }

    private static String normalizarObservacao(String observacao) {
        if (observacao == null || observacao.isBlank()) {
            return null;
        }
        return observacao.trim();
    }

    private Usuario usuarioAutenticado() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            throw new IllegalStateException("Usuário não autenticado.");
        }
        return usuarioService.buscarPorEmail(auth.getName());
    }
}
