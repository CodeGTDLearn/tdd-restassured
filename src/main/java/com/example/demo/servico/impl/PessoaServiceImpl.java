package com.example.demo.servico.impl;

import com.example.demo.modelo.Pessoa;
import com.example.demo.repo.PessoaRepo;
import com.example.demo.servico.PessoaService;
import com.example.demo.servico.exceptions.PessoaComCpfDuplicado;

public class PessoaServiceImpl implements PessoaService {

    private final PessoaRepo repo;

    public PessoaServiceImpl(PessoaRepo repo) {
        this.repo = repo;
    }

    @Override
    public Pessoa save(Pessoa pessoa) throws PessoaComCpfDuplicado {
        if(repo.findByCpf(pessoa.getCpf()).isPresent()) throw new PessoaComCpfDuplicado();
       return repo.save(pessoa);
    }
}
