package com.example.demo.servico.impl;

import com.example.demo.modelo.Pessoa;
import com.example.demo.repo.PessoaRepo;
import com.example.demo.servico.PessoaService;

public class PessoaServiceImpl implements PessoaService {

    private final PessoaRepo repo;

    public PessoaServiceImpl(PessoaRepo repo) {
        this.repo = repo;
    }

    @Override
    public Pessoa save(Pessoa pessoa) {
       return repo.save(pessoa);
    }
}
