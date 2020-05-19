package com.example.demo.repo.filtro;

import com.example.demo.modelo.Pessoa;
import com.example.demo.modelo.Telefone;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.Arrays;

import static lombok.Builder.Default;

//@AllArgsConstructor
@Builder
@Getter
public class FiltroPessoaCascade {
//    private Pessoa pessoa;

    @Default
    private String nome = "";

    @Default
    private String cpf = "";

    @Default
    private String ddd = "";

    @Default
    private String phone = "";

//    public FiltroPessoaCascade compor() {
//        Pessoa pessoa = new Pessoa();
//        pessoa.setNome( nome == null ? "" : this.nome);
//        pessoa.setCpf( nome == null ? "" : this.cpf);
//
//        Telefone tel = new Telefone();
//        tel.setDdd(ddd == null ? "" : this.ddd);
//        tel.setNumero(phone == null ? "" : this.phone);
//
//        pessoa.setTelefones(Arrays.asList(tel));
//
//        return FiltroPessoaCascade.builder().pessoa(pessoa).build();
//
//    }
//
//    public Pessoa gerar() {
//        return this.pessoa;
//    }

}
