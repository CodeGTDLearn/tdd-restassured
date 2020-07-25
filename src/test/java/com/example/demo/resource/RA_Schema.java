package com.example.demo.resource;

import io.restassured.RestAssured;
import io.restassured.module.jsv.JsonSchemaValidator;
import org.junit.Before;
import org.junit.Test;

import static io.restassured.RestAssured.given;
import static org.springframework.http.HttpStatus.OK;

public class RA_Schema extends GlobalTestConfig {

    @Before
    public void setUp() {
        RestAssured.baseURI = "http://restapi.wcaquino.me:80";
    }

    @Test
    public void checaJsonSchemaFormatosJsonTest() {
        //1) Acrescentar JSON-SCHEMA-VALIDATOR[REST ASSURE], no POM
        //2) Montando o SCHEMA de validacao:
        //   a) Usar o website: 'https://JSONSCHEMA.NET/HOME'
        //   b) Rodar o controller/resource no postman/browser, 'copiar' o resultado em JSON obtido;
        //   c) 'Colar' o resultado(JSON) obtido acima, no https://JSONSCHEMA.NET/HOME;
        //   d) 'Copiar' o SCHEMA gerado em JSONSCHEMA.NET, num arquivo .JSON, na pasta resources
        //   e) no Body do RestAssure, inserir:
        //       - .body(JsonSchemaValidator.matchesJsonSchemaInClasspath("RA_Schema-de-validacao.json"))
        given()

                .when()
                .get("users")

                .then()
                .statusCode(OK.value())

                .body(JsonSchemaValidator.matchesJsonSchemaInClasspath("RA_Schema-de-validacao.json"))
        ;

    }
}
