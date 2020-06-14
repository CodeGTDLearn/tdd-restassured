package com.example.demo.resource;

import com.example.demo.modelo.Pessoa;
import com.example.demo.modelo.Telefone;
import com.example.demo.servico.PessoaServiceInt;
import io.restassured.module.mockmvc.RestAssuredMockMvc;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Arrays;

import static com.example.demo.databuildersObMother.ObjectMotherPessoa.pessoaComCpfeTel;
import static io.restassured.http.ContentType.JSON;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.http.HttpStatus.OK;

@RunWith(MockitoJUnitRunner.class) // <<< RestAssuremockmvcTest + Mockito
public class RA_MockmvcUnit extends GlobalTestConfig {

    @Mock
    private PessoaServiceInt service;

    @InjectMocks
    private PessoaResource resource;

    @Before
    public void setUp() {
        RestAssuredMockMvc.standaloneSetup(resource);
    }

    @Test
    public void findByTelephoneTest() {
        Pessoa person = pessoaComCpfeTel().gerar();
        Telefone tel = person.getTelefones().get(0);

        final String nome = person.getNome();
        final String ddd = person.getTelefones().get(0).getDdd();
        final String numeroTel = person.getTelefones().get(0).getNumero();

        Mockito.when(service.findAllMockmvc()).thenReturn(Arrays.asList(person));

        RestAssuredMockMvc.given()
                .when()
                .get("/pessoas")
                .then()
                .log().body().and()
                .statusCode(OK.value())
                .contentType(JSON)
                .body("nome", hasItem(nome),
                        "telefones[0].ddd", hasItem(ddd));
    }
}
