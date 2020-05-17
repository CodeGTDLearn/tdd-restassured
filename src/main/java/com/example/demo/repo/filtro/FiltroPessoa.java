package com.example.demo.repo.filtro;

import com.example.demo.modelo.Pessoa;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class FiltroPessoa {

    private Pessoa pessoa;

    private String nome;
    private String cpf;
    private String ddd;
    private String phone;
}
