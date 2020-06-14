package com.example.demo.resource;

import com.example.demo.modelo.Pessoa;
import com.example.demo.repo.PessoaRepoInt;
import com.example.demo.servico.PessoaServiceInt;
import com.example.demo.servico.utils.CreateMessage;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Optional;

import static com.example.demo.databuildersObMother.ObjectMotherPessoa.pessoaComCpfTelAddress;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;
import static org.springframework.http.HttpStatus.*;

public class RA_CRUD extends GlobalTestConfig {

    @Autowired
    private PessoaRepoInt repo;

    @Autowired
    private PessoaServiceInt service;

    private Pessoa pessoaSource1;

    @Before
    public void setUp() {
        RestAssured.port = super.port;
        Optional<Pessoa> optional1 = repo.findFirstByOrderByNomeAsc();
        pessoaSource1 = optional1.get();
    }

    @Test
    public void get_body() {
        List<Pessoa> retornoPessoas = service.findAllMockmvc();

        given()
                .request()
                .header("Accept" ,ContentType.ANY)
                .header("Content-type" ,ContentType.JSON)
                .body(retornoPessoas)

                .when()
                .get("/pessoas")

                .then()
                .log().headers().and()
                .log().body().and()
                .contentType(ContentType.JSON)
                .statusCode(OK.value())
                .assertThat()

                .body("size()" ,is(retornoPessoas.size()));
    }

    @Test
    public void get_params() {

        final String nome = pessoaSource1.getNome();
        final String cpf = pessoaSource1.getCpf();
        final int codigo = pessoaSource1.getCodigo().intValue();

        final String ddd = pessoaSource1.getTelefones().get(0).getDdd();
        final String numeroTel = pessoaSource1.getTelefones().get(0).getNumero();
        final int codigoTel = pessoaSource1.getTelefones().get(0).getCodigo().intValue();

        final String logradouro = pessoaSource1.getEnderecos().get(0).getLogradouro();
        final int numeroRes = pessoaSource1.getEnderecos().get(0).getNumero();


        given()
                .contentType(ContentType.JSON)   //CONTENT-TYPE: TIPO DE DADO DA REQUISICAO

                .when()
                .get("/pessoas/{ddd}/{numero}" ,ddd ,numeroTel)

                .then()
                .log().headers().and()
                .log().body().and()
                .contentType(ContentType.JSON)
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
    public void get_header() {
        RestAssured.baseURI = "http://restapi.wcaquino.me:80";
        RestAssured.basePath = "/v2";

        given()

                .accept(ContentType.JSON) //ACCEPT: CHECA REQUEST (HEADER)

                .when()
                .get("/users")

                .then()

                .statusCode(OK.value())
                .contentType(ContentType.JSON)  //checando o HEADER
        ;
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
                .contentType(ContentType.JSON)
                .statusCode(CREATED.value())

                .header("Location" ,
                        equalTo(
                                "http://localhost:" + RestAssured.port + "/pessoas/" +
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
    public void put() {

        final String codigo = pessoaSource1.getCodigo().toString();
        final String nome = pessoaSource1.getNome();

        //PUT ALTERATION
        final String cpf = "11122233345";
        pessoaSource1.setCpf(cpf);

        final String ddd = pessoaSource1.getTelefones().get(0).getDdd();
        final String numero = pessoaSource1.getTelefones().get(0).getNumero();

        final String logradouro = pessoaSource1.getEnderecos().get(0).getLogradouro();
        final String bairro = pessoaSource1.getEnderecos().get(0).getBairro();


        given()

                .request()
                .header("Accept" ,ContentType.ANY)
                .header("Content-type" ,ContentType.JSON)
                .body(pessoaSource1)

                .when()
                .put("/pessoas/{codigo}" ,codigo)

                .then()
                .log().headers().and()
                .log().body().and()
                .statusCode(OK.value())
                .contentType(ContentType.JSON)

                .body("codigo" ,isA(Integer.class))
                .body("nome" ,equalTo(nome))
                .body("cpf" ,equalTo(cpf))
                .body("telefones.ddd" ,hasItem(ddd))
                .body("telefones.numero" ,hasItem(numero))
                .body("enderecos.logradouro" ,hasItem(logradouro))
        ;
    }

    @Test
    public void delete() {
        RestAssured.baseURI = "http://restapi.wcaquino.me:80";

        given()

                .when()
                .delete("/users/1")
                .then()

                .statusCode(NO_CONTENT.value());
    }

    @Test
    public void exception() {
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

}
