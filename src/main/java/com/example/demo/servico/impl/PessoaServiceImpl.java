package com.example.demo.servico.impl;

import com.example.demo.modelo.Pessoa;
import com.example.demo.repo.PessoaRepo;
import com.example.demo.servico.PessoaService;
import com.example.demo.servico.exceptions.PessoaComCpfDuplicado;
import com.example.demo.servico.exceptions.PessoaComTelDuplicado;

public class PessoaServiceImpl implements PessoaService {

    private final PessoaRepo repo;

    public PessoaServiceImpl(PessoaRepo repo) {
        this.repo = repo;
    }

    @Override
    public Pessoa save(Pessoa pessoa) throws PessoaComCpfDuplicado, PessoaComTelDuplicado {
        if(repo.findByCpf(pessoa.getCpf()).isPresent()) throw new PessoaComCpfDuplicado();
        final String ddd = pessoa.getTelefones().get(0).getDdd();
        final String numero = pessoa.getTelefones().get(0).getNumero();
        if(repo.findByTel(ddd, numero).isPresent()) throw new PessoaComTelDuplicado();
       return repo.save(pessoa);
    }
}
