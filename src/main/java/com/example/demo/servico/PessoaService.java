package com.example.demo.servico;

import com.example.demo.modelo.Pessoa;
import com.example.demo.servico.exceptions.PessoaComCpfDuplicado;

public interface PessoaService {
    Pessoa save(Pessoa pessoa) throws PessoaComCpfDuplicado;
}
