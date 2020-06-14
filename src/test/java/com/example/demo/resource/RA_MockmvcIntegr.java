package com.example.demo.resource;

import com.example.demo.servico.PessoaServiceInt;
import io.restassured.module.mockmvc.RestAssuredMockMvc;
import lombok.var;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.context.WebApplicationContext;

import static io.restassured.http.ContentType.JSON;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.http.HttpStatus.OK;

//@Sql(value = "/data-mass-load.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
//@Sql(value = "/data-mass-clean.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
//@DirtiesContext
//@RunWith(SpringRunner.class)
//@AutoConfigureTestDatabase(replace= AutoConfigureTestDatabase.Replace.NONE)
//@TestPropertySource("classpath:application-test.properties")
//@SpringBootTest(webEnvironment = RANDOM_PORT)// <<< RestAssuremockmvcTest + Spring
public class RA_MockmvcIntegr extends GlobalTestConfig {

    @Autowired
    private PessoaServiceInt service;

    //MOCKANDO CONTEXT WEB, OU SEJA, AO INVES DE FAZER 'REQUESTS' PARA WEB
    //EU MOCKO O CONTEXT WEB, ATRAVES DO webApplicationContext
    //E FACO OS MEUS TESTES NEXTE 'CONTEXTO WEB MOCKADO'
    @Autowired
    private WebApplicationContext webApplicationContext;

    @Before
    public void initialiseRestAssuredMockMvcWebApplicationContext() {
        RestAssuredMockMvc.webAppContextSetup(webApplicationContext);
    }

    @Test
    public void findByTelephoneTest() {

        var list = service.findAllMockmvc();

        final String nome = list.get(0).getNome();
        final String ddd = list.get(0).getTelefones().get(0).getDdd();

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
