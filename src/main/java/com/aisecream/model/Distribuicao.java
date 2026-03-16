package com.aisecream.model;

import com.aisecream.model.enums.StatusDistribuicao;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "distribuicao")
@Getter
@Setter
@NoArgsConstructor
public class Distribuicao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotNull
    @ManyToOne(optional = false)
    @JoinColumn(name = "lote_id", nullable = false)
    private LoteProducao lote;

    @NotNull
    @ManyToOne(optional = false)
    @JoinColumn(name = "loja_id", nullable = false)
    private Loja loja;

    @Min(1)
    @Column(nullable = false)
    private Integer quantidade;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusDistribuicao status = StatusDistribuicao.ATIVA;

    @Column(name = "distribuido_em", nullable = false, updatable = false)
    private LocalDateTime distribuidoEm = LocalDateTime.now();

    @NotNull
    @ManyToOne(optional = false)
    @JoinColumn(name = "criado_por", nullable = false)
    private Usuario criadoPor;

    public void cancelar() {
        this.status = StatusDistribuicao.CANCELADA;
    }
}
