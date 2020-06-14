package com.example.demo.resource;

import io.restassured.RestAssured;
import io.restassured.module.jsv.JsonSchemaValidator;
import org.junit.Before;
import org.junit.Test;

import static io.restassured.RestAssured.given;
import static org.springframework.http.HttpStatus.OK;

//@TestPropertySource("classpath:application-test.properties")
public class RA_Schema extends GlobalTestConfig {

    @Before
    public void setUp() {
        RestAssured.baseURI = "http://restapi.wcaquino.me:80";
    }

    @Test
    public void checaJsonSchemaFormatosJsonTest() {
        //------- ACrescentar dependencia JSON-SCHEMA-VALIDATOR[REST ASSURE], no POM
        given()


                .when()
                .get("users")

                .then()

                .statusCode(OK.value())

                .body(JsonSchemaValidator.matchesJsonSchemaInClasspath("jsonValidacaoUsers.json"))
        ;

    }
}
