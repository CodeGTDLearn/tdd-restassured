package com.example.demo.servico;

import com.example.demo.modelo.Pessoa;
import com.example.demo.modelo.Telefone;
import com.example.demo.servico.exceptions.PessoaComCpfDuplicado;
import com.example.demo.servico.exceptions.PessoaComTelDuplicado;
import com.example.demo.servico.exceptions.TelephoneNotFound;

public interface PessoaServiceInt {
    Pessoa save(Pessoa pessoa) throws PessoaComCpfDuplicado, PessoaComTelDuplicado;

    Pessoa findByTelephone(Telefone telefone) throws TelephoneNotFound;
}
