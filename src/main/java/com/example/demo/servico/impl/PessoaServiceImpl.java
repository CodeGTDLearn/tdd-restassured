package com.example.demo.servico.impl;

import com.example.demo.modelo.Pessoa;
import com.example.demo.modelo.Telefone;
import com.example.demo.repo.PessoaRepoInt;
import com.example.demo.repo.filtro.FiltroPessoaCascade;
import com.example.demo.servico.PessoaServiceInt;
import com.example.demo.servico.exceptions.CpfDuplicadoException;
import com.example.demo.servico.exceptions.TelDuplicadoException;
import com.example.demo.servico.exceptions.TelephoneNotFoundException;
import com.example.demo.servico.utils.CreateMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PessoaServiceImpl implements PessoaServiceInt {

    @Autowired
    private PessoaRepoInt repo;

    @Autowired
    public PessoaServiceImpl(PessoaRepoInt repo) {
        this.repo = repo;
    }

    @Override
    public Pessoa save(Pessoa pessoa) throws TelDuplicadoException, CpfDuplicadoException {

        if (repo.findByCpf(pessoa.getCpf()).isPresent())
            throw new CpfDuplicadoException(CreateMessage.builder()
                    .text1(pessoa.getCpf())
                    .build()
                    .cpfDuplicity());

        final String ddd = pessoa.getTelefones().get(0).getDdd();
        final String numero = pessoa.getTelefones().get(0).getNumero();

        if (repo.findByTelDdd(ddd, numero).isPresent())
            throw new TelDuplicadoException(CreateMessage.builder()
                    .text1(ddd)
                    .text2(numero)
                    .build()
                    .telDuplicity());

        return repo.save(pessoa);
    }

    @Override
    public boolean delete(Pessoa pessoa) {
        repo.delete(pessoa);
        return repo.findByCpf(pessoa.getCpf()).isPresent();

    }

    @Override
    public Pessoa findByTelephone(Telefone telefone) throws TelephoneNotFoundException {
        return repo.findByTelDdd(telefone.getDdd(), telefone.getNumero()).orElseThrow(
                () -> new TelephoneNotFoundException("Telefone: " +
                        telefone.getDdd() +
                        " - " +
                        telefone.getNumero() +
                        " nao encontrado!"));
    }

    @Override
    public List<Pessoa> findByMultiFilterCascade(FiltroPessoaCascade filter) {
        return repo.findByMultiFilterCascade(
                filter.getNome(),
                filter.getCpf(),
                filter.getDdd(),
                filter.getPhone());
    }

    @Override
    public List<Pessoa> findAllMockmvc() {
        return repo.findAll();
    }

    @Override
    public Optional<Pessoa> findByTelDdd(String ddd, String numero) {
        return repo.findByTelDdd(ddd, numero);
    }

}
