package com.example.demo.repo;

import com.example.demo.modelo.Pessoa;

import java.util.Optional;

public interface PessoaRepo {
    Pessoa save(Pessoa pessoa);

    Optional<Pessoa> findByCpf(String cpf);
}
