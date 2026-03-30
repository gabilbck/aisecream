package com.aisecream.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UsuarioOperadorForm {

    private Integer id;

    @NotBlank(message = "Informe o nome.")
    private String nome;

    @NotBlank(message = "Informe o e-mail.")
    @Email(message = "Digite um e-mail válido.")
    private String email;

    /** Preenchido só no cadastro ou ao trocar senha na edição. */
    private String senha;

    private Boolean ativo = Boolean.TRUE;
}
