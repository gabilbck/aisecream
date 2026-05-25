package com.aisecream.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "loja")
@Getter
@Setter
@NoArgsConstructor
public class Loja {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotBlank
    @Size(max = 100)
    @Column(nullable = false, length = 100)
    private String nome;

    @NotBlank
    @Pattern(regexp = "^\\d{5}-?\\d{3}$", message = "CEP inválido. Use o formato 00000-000.")
    @Column(nullable = false, length = 9)
    private String cep;

    @NotBlank
    @Pattern(regexp = "^[A-Za-z]{2}$", message = "Informe a UF com 2 letras.")
    @Column(nullable = false, length = 2)
    private String estado;

    @NotBlank
    @Size(max = 100)
    @Column(nullable = false, length = 100)
    private String cidade;

    @NotBlank
    @Size(max = 150)
    @Column(nullable = false, length = 150)
    private String logradouro;

    @NotBlank
    @Size(max = 20)
    @Column(nullable = false, length = 20)
    private String numero;

    @Size(max = 100)
    @Column(length = 100)
    private String complemento;

    @Size(max = 20, message = "Telefone deve ter no máximo 20 caracteres.")
    @Column(length = 20)
    private String telefone;

    @Column(nullable = false)
    private Boolean ativo = true;

    @Column(name = "criado_em", nullable = false, updatable = false)
    private LocalDateTime criadoEm = LocalDateTime.now();

    public boolean isAtivo() {
        return Boolean.TRUE.equals(this.ativo);
    }

    public String getEnderecoFormatado() {
        StringBuilder sb = new StringBuilder();
        sb.append(logradouro).append(", ").append(numero);
        if (complemento != null && !complemento.isBlank()) {
            sb.append(" - ").append(complemento);
        }
        sb.append(" — ").append(cidade).append("/").append(estado);
        sb.append(" — CEP ").append(cep);
        return sb.toString();
    }
}
