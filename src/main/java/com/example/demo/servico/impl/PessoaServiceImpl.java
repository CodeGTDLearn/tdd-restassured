package com.example.demo.servico.impl;

import com.example.demo.modelo.Pessoa;
import com.example.demo.modelo.Telefone;
import com.example.demo.repo.PessoaRepoInt;
import com.example.demo.servico.PessoaServiceInt;
import com.example.demo.servico.exceptions.PessoaComCpfDuplicado;
import com.example.demo.servico.exceptions.PessoaComTelDuplicado;
import com.example.demo.servico.exceptions.TelephoneNotFound;

public class PessoaServiceImpl implements PessoaServiceInt {

    private final PessoaRepoInt repo;

    public PessoaServiceImpl(PessoaRepoInt repo) {
        this.repo = repo;
    }

    @Override
    public Pessoa save(Pessoa pessoa) throws PessoaComCpfDuplicado, PessoaComTelDuplicado {
        if (repo.findByCpf(pessoa.getCpf()).isPresent()) throw new PessoaComCpfDuplicado();
        final String ddd = pessoa.getTelefones().get(0).getDdd();
        final String numero = pessoa.getTelefones().get(0).getNumero();
        if (repo.findByTel(ddd, numero).isPresent()) throw new PessoaComTelDuplicado();
        return repo.save(pessoa);
    }

    @Override
    public Pessoa findByTelephone(Telefone telefone) throws TelephoneNotFound {
        return repo.findByTel(telefone.getDdd(), telefone.getNumero()).orElseThrow(() -> new TelephoneNotFound());
    }
}
