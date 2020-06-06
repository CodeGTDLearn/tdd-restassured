package com.example.demo.resource;

import com.example.demo.modelo.Pessoa;
import com.example.demo.repo.PessoaRepoInt;
import com.example.demo.repo.filtro.FiltroPessoaCascade;
import com.example.demo.servico.PessoaServiceInt;
import com.example.demo.servico.utils.CreateMessage;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;
import java.util.Optional;

import static com.example.demo.databuilders.ObjectMotherPessoa.pessoaComCpf;
import static com.example.demo.databuilders.ObjectMotherPessoa.pessoaComCpfTelAddress;
import static com.example.demo.repo.filtro.FiltroPessoaCascade.*;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;
import static org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment;

@SpringBootTest(webEnvironment = WebEnvironment.DEFINED_PORT)
@Sql(value = "/preload-ScriptHSQL-dbTest.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(value = "/clean-ScriptHSQL-dbTest.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
@DirtiesContext
@RunWith(SpringRunner.class)
@AutoConfigureTestDatabase(replace = Replace.NONE)
@TestPropertySource("classpath:application-test.properties")
public class ResourceRestAssureTest {

    @Autowired
    private PessoaRepoInt repo;

    @Autowired
    private PessoaServiceInt service;

    private Pessoa pessoaSource1;
    private Pessoa pessoaSource2;

    @LocalServerPort
    protected int port;

    @Before
    public void setUp() throws Exception {
        RestAssured.port = port;
        Optional<Pessoa> optional1 = repo.findFirstByOrderByNomeAsc();
        pessoaSource1 = optional1.get();
    }

    @Test
    public void findByDddAndTel() {

        final String nome = pessoaSource1.getNome();
        final String cpf = pessoaSource1.getCpf();
        final int codigo = pessoaSource1.getCodigo().intValue();

        final String ddd = pessoaSource1.getTelefones().get(0).getDdd();
        final String numeroTel = pessoaSource1.getTelefones().get(0).getNumero();
        final int codigoTel = pessoaSource1.getTelefones().get(0).getCodigo().intValue();

        final String logradouro = pessoaSource1.getEnderecos().get(0).getLogradouro();
        final int numeroRes = pessoaSource1.getEnderecos().get(0).getNumero();


        given()
                .pathParam("ddd", ddd)
                .pathParam("numero", numeroTel)

                .get("/pessoas/{ddd}/{numero}")

                .then()
                .log().headers().and()
                .log().body().and()
                .statusCode(HttpStatus.OK.value())

                //equalTo para o corpo do Json
                .body("codigo", equalTo(codigo),
                        "nome", equalTo(nome),
                        "cpf", equalTo(cpf),

                        //'hasItem' devera ser usado p/ os
                        // SUB-OBJETOS(pois esses tem SquareBrackets
                        // e isso faz o TESTE nao BATER) do json
                        "enderecos.logradouro", hasItem(logradouro),
                        "enderecos.numero", hasItem(numeroRes),


                        "telefones.ddd", hasItem(ddd),
                        "telefones.codigo", hasItem(codigoTel)
                );
    }

    @Test
    public void findByDddAndTelWithNoReturnException() {
        Optional<Pessoa> optional2 = repo.findFirstByOrderByCpfAsc();
        pessoaSource2 = optional2.get();

        final String dddPessoa01 = pessoaSource1.getTelefones().get(0).getDdd();
        final String numTelPessoa02 = pessoaSource2.getTelefones().get(0).getNumero();

        given()
                .pathParam("ddd", dddPessoa01)
                .pathParam("numero", numTelPessoa02)

                .get("/pessoas/{ddd}/{numero}")

                .then()
                .log().headers().and()
                .log().body().and()
                .statusCode(HttpStatus.NOT_FOUND.value())

                .body("erro", equalTo(
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
                .header("Accept", ContentType.ANY)
                .header("Content-type", ContentType.JSON)

                .body(person)

                .when()
                .post("/pessoas")

                .then()
                .log().headers().and()
                .log().body().and()

                .statusCode(HttpStatus.CREATED.value())

                .header("Location",
                        equalTo(
                                "http://localhost:" + port + "/pessoas/" +
                                        person.getTelefones().get(0).getDdd() + "/" +
                                        person.getTelefones().get(0).getNumero()))

                .body(
                        "nome", equalTo(nome),
                        "cpf", equalTo(cpf),
                        "telefones.ddd", hasItem(ddd),
                        "telefones.numero", hasItem(numero),
                        "enderecos.logradouro", hasItem(logradouro),
                        "enderecos.bairro", hasItem(bairro));
    }

    @Test
    public void CpfDuplicadoException() {
        given()
                .request()
                .header("Accept", ContentType.ANY)
                .header("Content-type", ContentType.JSON)

                .body(pessoaSource1)

                .when()
                .post("/pessoas")

                .then()
                .log().body()
                .and()
                .statusCode(HttpStatus.BAD_REQUEST.value())

                .body("erro", equalTo(CreateMessage.builder()
                        .text1(pessoaSource1.getCpf())
                        .build()
                        .cpfDuplicity()));
    }

    @Test
    public void TelDuplicadoException() {
        Pessoa pessoa = pessoaComCpf().gerar();

        pessoa.setTelefones(pessoaSource1.getTelefones());

        final String ddd = pessoaSource1.getTelefones().get(0).getDdd();
        final String numero = pessoaSource1.getTelefones().get(0).getNumero();

        given()
                .request()
                .header("Accept", ContentType.ANY)
                .header("Content-type", ContentType.JSON)

                .body(pessoa)

                .when()
                .post("/pessoas")

                .then()
                .log().body()
                .and()
                .statusCode(HttpStatus.BAD_REQUEST.value())

                .body("erro", equalTo(CreateMessage.builder()
                        .text1(ddd)
                        .text2(numero)
                        .build()
                        .telDuplicity()));
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
                .header("Accept", ContentType.ANY)
                .header("Content-type", ContentType.JSON)

                .body(multiFilter)

                .when()
                .post("/pessoas/filtrar")

                .then()
                .log().body().and()
                .statusCode(HttpStatus.OK.value())

                .body("nome",
                        containsInAnyOrder(nome1, nome2),
                        "telefones.ddd", containsInAnyOrder(hasItem(ddd1), hasItem(ddd2)),
                        "enderecos.logradouro", containsInAnyOrder(hasItem(end1), hasItem(end2)));
    }

    @Test
    public void findAllMockmvcTest() {
        List<Pessoa> retornoPessoas = service.findAllMockmvc();

        given()
                .request()
                .header("Accept", ContentType.ANY)
                .header("Content-type", ContentType.JSON)

                .body(retornoPessoas)

                .when()
                .get("/pessoas")

                .then()
                .log().body().and()
                .statusCode(HttpStatus.OK.value())
                .assertThat()

                .body("size()", is(retornoPessoas.size()));
    }
}
