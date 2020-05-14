package com.example.demo.servico;

import com.example.demo.modelo.Pessoa;
import com.example.demo.servico.exceptions.PessoaComCpfDuplicado;
import com.example.demo.servico.exceptions.PessoaComTelDuplicado;

public interface PessoaService {
    Pessoa save(Pessoa pessoa) throws PessoaComCpfDuplicado, PessoaComTelDuplicado;
}
