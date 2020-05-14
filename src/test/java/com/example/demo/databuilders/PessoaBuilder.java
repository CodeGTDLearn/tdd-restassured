package com.example.demo.databuilders;

import com.example.demo.modelo.Pessoa;
import com.example.demo.modelo.Telefone;
import com.github.javafaker.Faker;
import lombok.Builder;
import lombok.Builder.*;
import lombok.Getter;

import java.util.Arrays;
import java.util.Locale;


@Builder
@Getter
public class PessoaBuilder {
    private Pessoa pessoa;
    private static Telefone tel;

    private static Faker faker = new Faker(new Locale("en-BR"));
    private static String FAKER_REGEX_CPF = "[0-9]{3}\\.[0-9]{3}\\.[0-9]{3}-[0-9]{2}";

    @Default
    private static String nome = faker.name().fullName();

    @Default
    private static String cpf = faker.regexify(FAKER_REGEX_CPF).trim();

    @Default
    private static String ddd = faker.phoneNumber().extension();
    @Default
    private static String telNumero = faker.phoneNumber().phoneNumber();

    public static PessoaBuilder pessoaComCpf() {
        Pessoa pessoa = new Pessoa();
        pessoa.setNome(nome);
        pessoa.setCpf(cpf);
        return PessoaBuilder.builder().pessoa(pessoa).build();
    }

    public static PessoaBuilder pessoaComCpfDiverso() {
        Pessoa pessoa = new Pessoa();
        pessoa.setNome(nome);
        pessoa.setCpf(Faker.instance().regexify(FAKER_REGEX_CPF).trim());
        return PessoaBuilder.builder().pessoa(pessoa).build();
    }

    public static PessoaBuilder pessoaComCpfeTel() {
        Pessoa pessoa = new Pessoa();
        pessoa.setNome(nome);
        pessoa.setCpf(cpf);

        tel = new Telefone();
        tel.setDdd(ddd);
        tel.setNumero(telNumero);
        pessoa.setNome(nome);
        pessoa.setTelefones(Arrays.asList(tel));
        return PessoaBuilder.builder().pessoa(pessoa).build();
    }

    public Pessoa gerar() {
        return this.pessoa;
    }

    public Telefone retornaTel() {
        return this.tel;
    }
}