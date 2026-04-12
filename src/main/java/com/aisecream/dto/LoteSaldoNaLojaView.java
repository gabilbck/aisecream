package com.aisecream.dto;

import com.aisecream.model.LoteProducao;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class LoteSaldoNaLojaView {

    private final LoteProducao lote;
    private final int saldoDisponivel;
}
