package com.aisecream.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class BaixaEstoqueForm {

    @NotNull(message = "Selecione a loja.")
    private Integer lojaId;

    @NotNull(message = "Selecione o lote.")
    private Integer loteId;

    @NotNull
    @Min(value = 1, message = "Quantidade deve ser pelo menos 1.")
    private Integer quantidade;

    @Size(max = 2000)
    private String observacao;
}
