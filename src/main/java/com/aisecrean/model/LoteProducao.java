package com.aisecrean.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "lote_producao")
@Getter
@Setter
@NoArgsConstructor
public class LoteProducao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotNull
    @ManyToOne(optional = false)
    @JoinColumn(name = "sabor_id", nullable = false)
    private Sabor sabor;

    @Min(1)
    @Column(name = "quantidade_produzida", nullable = false)
    private Integer quantidadeProduzida;

    @Min(0)
    @Column(name = "quantidade_disponivel", nullable = false)
    private Integer quantidadeDisponivel;

    @NotNull
    @Column(name = "data_producao", nullable = false)
    private LocalDate dataProducao;

    @Column(name = "criado_em", nullable = false, updatable = false)
    private LocalDateTime criadoEm = LocalDateTime.now();

    @NotNull
    @ManyToOne(optional = false)
    @JoinColumn(name = "criado_por", nullable = false)
    private Usuario criadoPor;

    public void decrementarDisponivel(Integer qtd) {
        if (qtd > this.quantidadeDisponivel) {
            throw new IllegalArgumentException(
                "Quantidade solicitada (" + qtd + ") maior que o disponível (" + this.quantidadeDisponivel + ")."
            );
        }
        this.quantidadeDisponivel -= qtd;
    }

    public void incrementarDisponivel(Integer qtd) {
        this.quantidadeDisponivel += qtd;
    }
}
