package com.aisecream.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
public class LoteProducaoForm {

    @NotNull(message = "Selecione o sabor.")
    private Integer saborId;

    @NotNull
    @Min(value = 1, message = "Quantidade deve ser pelo menos 1.")
    private Integer quantidadeProduzida;

    @NotNull(message = "Informe a data de produção.")
    private LocalDate dataProducao;
}
