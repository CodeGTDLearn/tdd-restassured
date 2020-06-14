package com.example.demo.resource;

import com.example.demo.modelo.Pessoa;
import com.example.demo.repo.PessoaRepoInt;
import com.example.demo.repo.filtro.FiltroPessoaCascade;
import com.example.demo.servico.PessoaServiceInt;
import com.example.demo.servico.utils.CreateMessage;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Optional;

import static com.example.demo.databuildersObMother.ObjectMotherPessoa.pessoaComCpf;
import static com.example.demo.databuildersObMother.ObjectMotherPessoa.pessoaComCpfTelAddress;
import static com.example.demo.repo.filtro.FiltroPessoaCascade.builder;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;
import static org.springframework.http.HttpStatus.*;

public class RA_Param extends GlobalTestConfig {

    @Autowired
    private PessoaRepoInt repo;

    @Autowired
    private PessoaServiceInt service;

    Pessoa pessoaSource1;

    @Override
    public void setUp() {
        RestAssured.port = super.port;
        Optional<Pessoa> optional1 = repo.findFirstByOrderByNomeAsc();
        this.pessoaSource1 = optional1.get();
    }

    @Test
    public void TelDuplicadoException() {

        Pessoa pessoa = pessoaComCpf().gerar();

        pessoa.setTelefones(pessoaSource1.getTelefones());

        final String ddd = pessoaSource1.getTelefones().get(0).getDdd();
        final String numero = pessoaSource1.getTelefones().get(0).getNumero();

        given()
                .request()
                .header("Accept" ,ContentType.ANY)
                .header("Content-type" ,ContentType.JSON)
                .body(pessoa)

                .when()
                .post("/pessoas")

                .then()
                .log().body()
                .and()
                .statusCode(BAD_REQUEST.value())

                .body(is(not(nullValue())))
                .body(containsString(ddd))
                .body("erro" ,equalTo(CreateMessage.builder()
                        .text1(ddd)
                        .text2(numero)
                        .build()
                        .telDuplicity()))
        ;
    }

    @Test
    public void findByMultiFilterCascade() {

        final FiltroPessoaCascade multiFilter = builder()
                .nome("a")
                .cpf("4")
                .build();

        List<Pessoa> listTestPessoa = service.findByMultiFilterCascade(multiFilter);

        final String nome1 = listTestPessoa.get(0).getNome();
        final String nome2 = listTestPessoa.get(1).getNome();

        final String ddd1 = listTestPessoa.get(0).getTelefones().get(0).getDdd();
        final String ddd2 = listTestPessoa.get(1).getTelefones().get(0).getDdd();

        final String end1 = listTestPessoa.get(0).getEnderecos().get(0).getLogradouro();
        final String end2 = listTestPessoa.get(1).getEnderecos().get(0).getLogradouro();

        given()
                .request()
                .header("Accept" ,ContentType.ANY)
                .header("Content-type" ,ContentType.JSON)
                .body(multiFilter)

                .when()
                .post("/pessoas/filtrar")

                .then()
                .log().body().and()
                .statusCode(OK.value())

                //containsInAnyOrder:
                //  deve receber TODOS ITEMS DA LISTA
                .body("nome" ,containsInAnyOrder(nome1 ,nome2))

                //containsInAnyOrder:
                //  p/ somente ALGUNS ITEMS usar 'hasItem' dentro do containsInAnyOrder
                .body("telefones.ddd" ,
                        containsInAnyOrder(hasItem(ddd1) ,hasItem(ddd2)))
                .body("telefones[0].ddd" ,hasItem(ddd1))
                .body("telefones[1].ddd" ,hasItem(ddd2))
                .body("enderecos.logradouro" ,
                        containsInAnyOrder(hasItem(end1) ,hasItem(end2)))
        ;
    }

    @Test
    public void findByDddAndTelPATHPARAM() {
//        init();
        final String nome = pessoaSource1.getNome();
        final String cpf = pessoaSource1.getCpf();
        final int codigo = pessoaSource1.getCodigo().intValue();

        final String ddd = pessoaSource1.getTelefones().get(0).getDdd();
        final String numeroTel = pessoaSource1.getTelefones().get(0).getNumero();
        final int codigoTel = pessoaSource1.getTelefones().get(0).getCodigo().intValue();

        final String logradouro = pessoaSource1.getEnderecos().get(0).getLogradouro();
        final int numeroRes = pessoaSource1.getEnderecos().get(0).getNumero();


        given()
                .pathParam("ddd" ,ddd)
                .pathParam("numero" ,numeroTel)

                .when()
                .get("/pessoas/{ddd}/{numero}")

                .then()
                .log().headers().and()
                .log().body().and()
                .statusCode(OK.value())

                //equalTo para o corpo do Json
                .body("codigo" ,equalTo(codigo))
                .body("nome" ,equalTo(nome))
                .body("nome" ,containsString(nome))
                .body("cpf" ,equalTo(cpf))

                //'hasItem' devera ser usado p/ os
                // SUB-OBJETOS(pois esses tem SquareBrackets
                // e isso faz o TESTE nao BATER) do json
                .body("enderecos.logradouro" ,hasItem(logradouro))
                .body("enderecos.numero" ,hasItem(numeroRes))

                .body("telefones.ddd" ,hasItem(ddd))
                .body("telefones.codigo" ,hasItem(codigoTel))
        ;
    }

    @Test
    public void findByDddAndTelPARAMinLINE() {

        final String nome = pessoaSource1.getNome();
        final String cpf = pessoaSource1.getCpf();
        final int codigo = pessoaSource1.getCodigo().intValue();

        final String ddd = pessoaSource1.getTelefones().get(0).getDdd();
        final String numeroTel = pessoaSource1.getTelefones().get(0).getNumero();
        final int codigoTel = pessoaSource1.getTelefones().get(0).getCodigo().intValue();

        final String logradouro = pessoaSource1.getEnderecos().get(0).getLogradouro();
        final int numeroRes = pessoaSource1.getEnderecos().get(0).getNumero();


        given()
//                .pathParam("ddd" ,ddd)
//                .pathParam("numero" ,numeroTel)

                .when()
                .get("/pessoas/{ddd}/{numero}" ,ddd ,numeroTel)

                .then()
                .log().headers().and()
                .log().body().and()
                .statusCode(OK.value())

                //equalTo para o corpo do Json
                .body("codigo" ,equalTo(codigo))
                .body("nome" ,equalTo(nome))
                .body("nome" ,containsString(nome))
                .body("cpf" ,equalTo(cpf))

                //'hasItem' devera ser usado p/ os
                // SUB-OBJETOS(pois esses tem SquareBrackets
                // e isso faz o TESTE nao BATER) do json
                .body("enderecos.logradouro" ,hasItem(logradouro))
                .body("enderecos.numero" ,hasItem(numeroRes))

                .body("telefones.ddd" ,hasItem(ddd))
                .body("telefones.codigo" ,hasItem(codigoTel))
        ;
    }

    @Test
    public void findByDddAndTelWithNoReturnException() {
        Optional<Pessoa> optional2 = repo.findFirstByOrderByCpfAsc();
        Pessoa pessoaSource2 = optional2.get();

        final String dddPessoa01 = pessoaSource1.getTelefones().get(0).getDdd();
        final String numTelPessoa02 = pessoaSource2.getTelefones().get(0).getNumero();

        given()
                .pathParam("ddd" ,dddPessoa01)
                .pathParam("numero" ,numTelPessoa02)

                .when()
                .get("/pessoas/{ddd}/{numero}")

                .then()
                .log().headers().and()
                .log().body().and()
                .statusCode(NOT_FOUND.value())

                .body("erro" ,is(
                        CreateMessage.builder()
                                .text1(dddPessoa01)
                                .text2(numTelPessoa02)
                                .build()
                                .telNotFound()))

                .body("erro" ,equalTo(
                        CreateMessage.builder()
                                .text1(dddPessoa01)
                                .text2(numTelPessoa02)
                                .build()
                                .telNotFound()));
    }

    @Test
    public void save() {
        Pessoa person = pessoaComCpfTelAddress().gerar();

        final String nome = person.getNome();
        final String cpf = person.getCpf();

        final String ddd = person.getTelefones().get(0).getDdd();
        final String numero = person.getTelefones().get(0).getNumero();

        final String logradouro = person.getEnderecos().get(0).getLogradouro();
        final String bairro = person.getEnderecos().get(0).getBairro();

        given()

                .request()
                .header("Accept" ,ContentType.ANY)
                .header("Content-type" ,ContentType.JSON)
                .body(person)

                .when()
                .post("/pessoas")

                .then()
                .log().headers().and()
                .log().body().and()
                .statusCode(CREATED.value())
                .contentType(ContentType.JSON)

                .header("Location" ,
                        equalTo(
                                "http://localhost:" + "8081" + "/pessoas/" +
                                        person.getTelefones().get(0).getDdd() + "/" +
                                        person.getTelefones().get(0).getNumero()))

                .body(
                        "nome" ,equalTo(nome))
                .body("cpf" ,equalTo(cpf))
                .body("codigo" ,isA(Integer.class))
                .body("telefones.ddd" ,hasItem(ddd))
                .body("telefones.numero" ,hasItem(numero))
                .body("enderecos.logradouro" ,hasItem(logradouro))
                .body("enderecos.bairro" ,hasItem(bairro))
        ;
    }

    @Test
    public void CpfDuplicadoException() {
        given()
                .request()
                .header("Accept" ,ContentType.ANY)
                .header("Content-type" ,ContentType.JSON)

                .body(pessoaSource1)

                .when()
                .post("/pessoas")

                .then()
                .log().body()
                .and()
                .statusCode(BAD_REQUEST.value())

                .body("erro" ,equalTo(CreateMessage.builder()
                        .text1(pessoaSource1.getCpf())
                        .build()
                        .cpfDuplicity()));
    }

    @Test
    public void findAllMockmvcTest() {
        List<Pessoa> retornoPessoas = service.findAllMockmvc();

        given()
                .request()
                .header("Accept" ,ContentType.ANY)
                .header("Content-type" ,ContentType.JSON)
                .body(retornoPessoas)

                .when()
                .get("/pessoas")

                .then()
                .log().body().and()
                .statusCode(OK.value())
                .assertThat()

                .body("size()" ,is(retornoPessoas.size()));
    }
}
