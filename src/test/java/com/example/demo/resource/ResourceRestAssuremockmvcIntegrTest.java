package com.example.demo.resource;

import com.example.demo.servico.PessoaServiceInt;
import io.restassured.module.mockmvc.RestAssuredMockMvc;
import lombok.var;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.context.WebApplicationContext;

import static io.restassured.http.ContentType.JSON;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.http.HttpStatus.OK;

@Sql(value = "/preload-ScriptHSQL-dbTest.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(value = "/clean-ScriptHSQL-dbTest.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
@DirtiesContext
@RunWith(SpringRunner.class)
@AutoConfigureTestDatabase(replace= AutoConfigureTestDatabase.Replace.NONE)
@TestPropertySource("classpath:application-test.properties")
@SpringBootTest(webEnvironment = RANDOM_PORT)// <<< RestAssuremockmvcTest + Spring
public class ResourceRestAssuremockmvcIntegrTest {

    @Autowired
    private PessoaServiceInt service;

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
