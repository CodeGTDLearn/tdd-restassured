package com.example.demo.databuilders;

import com.example.demo.modelo.Pessoa;
import com.github.javafaker.Faker;
import lombok.Builder;
import lombok.Builder.*;
import lombok.Getter;

@Builder
@Getter
public class PessoaBuilder {

    private Pessoa pessoa;   //variavel de instancia do DataBuilder
    private static String REGEX_CPF = "[0-9]{3}.[0-9]{3}.[0-9]{3}-[0-9]{2}";

    @Default
    private static String nome = Faker.instance().name().fullName();

    @Default
    private static String cpf = Faker.instance().regexify(REGEX_CPF).trim();

    public static PessoaBuilder pessoaComCpf() {

        Pessoa pessoa = new Pessoa();
        pessoa.setNome(nome);
        pessoa.setCpf(cpf);

        return PessoaBuilder.builder().pessoa(pessoa).build();
    }

    public Pessoa gerar() {
        return this.pessoa;
    }
}
