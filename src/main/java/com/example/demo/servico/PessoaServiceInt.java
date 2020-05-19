package com.example.demo.servico;

import com.example.demo.modelo.Pessoa;
import com.example.demo.modelo.Telefone;
import com.example.demo.repo.filtro.FiltroPessoaCascade;
import com.example.demo.servico.exceptions.CpfDuplicadoException;
import com.example.demo.servico.exceptions.TelDuplicadoException;
import com.example.demo.servico.exceptions.TelephoneNotFoundException;

import java.util.List;

public interface PessoaServiceInt {
    Pessoa save(Pessoa pessoa) throws TelDuplicadoException, CpfDuplicadoException;

    Pessoa findByTelephone(Telefone telefone) throws TelephoneNotFoundException;

    List<Pessoa> findByMultiFilterCascade(FiltroPessoaCascade filter);
}
