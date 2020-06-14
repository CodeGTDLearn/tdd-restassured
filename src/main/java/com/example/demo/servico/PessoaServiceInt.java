package com.example.demo.servico;

import com.example.demo.modelo.Pessoa;
import com.example.demo.modelo.Telefone;
import com.example.demo.repo.filtro.FiltroPessoaCascade;
import com.example.demo.servico.exceptions.CpfDuplicadoException;
import com.example.demo.servico.exceptions.PessoaNaoEncontradaException;
import com.example.demo.servico.exceptions.TelDuplicadoException;
import com.example.demo.servico.exceptions.TelephoneNotFoundException;

import java.util.List;
import java.util.Optional;

public interface PessoaServiceInt {
    Pessoa save(Pessoa pessoa) throws TelDuplicadoException, CpfDuplicadoException;

    Pessoa put(Pessoa pessoa, String codigo) throws PessoaNaoEncontradaException;

    Pessoa findByTelephone(Telefone telefone) throws TelephoneNotFoundException;

    List<Pessoa> findByMultiFilterCascade(FiltroPessoaCascade filter);

    List<Pessoa> findAllMockmvc();

    Optional<Pessoa> findByTelDdd(String ddd, String numero);

    boolean delete(Pessoa pessoa);
}
