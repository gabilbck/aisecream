package com.aisecream.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "baixa_estoque")
@Getter
@Setter
@NoArgsConstructor
public class BaixaEstoque {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotNull
    @ManyToOne(optional = false)
    @JoinColumn(name = "loja_id", nullable = false)
    private Loja loja;

    @NotNull
    @ManyToOne(optional = false)
    @JoinColumn(name = "lote_id", nullable = false)
    private LoteProducao lote;

    @Min(1)
    @Column(nullable = false)
    private Integer quantidade;

    @Column(columnDefinition = "TEXT")
    private String observacao;

    @Column(name = "criado_em", nullable = false, updatable = false)
    private LocalDateTime criadoEm = LocalDateTime.now();

    @NotNull
    @ManyToOne(optional = false)
    @JoinColumn(name = "criado_por", nullable = false)
    private Usuario criadoPor;
}
