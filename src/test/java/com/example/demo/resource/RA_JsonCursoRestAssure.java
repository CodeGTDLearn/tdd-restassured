package com.example.demo.resource;

import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;
import org.junit.Test;

import java.util.Arrays;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;
import static org.springframework.http.HttpStatus.OK;

public class RA_JsonCursoRestAssure extends GlobalTestConfig {

    @Test
    public void basePathTest() {

        RestAssured.basePath = "/v2";
        ContentType CONTENT_TYPE_TEST = ContentType.JSON;

        given()
                .when()

                //erroConnectionTimedOut ERRO: inserir a porta
                .get("/users")
                .then()
                .statusCode(OK.value())
                .contentType(CONTENT_TYPE_TEST)
        ;
    }

    @Test
    public void listaSubList_CustomReqAndRespTest() {

        RequestSpecBuilder reqBuilder = new RequestSpecBuilder();
        final RequestSpecification requestEspecificado =
                reqBuilder
                        .log(LogDetail.COOKIES)
                        .log(LogDetail.URI)
                        .log(LogDetail.HEADERS)
                        .addHeader("bear" ,"Chave de Acesso")
                        .addCookie("TestCookie" ,"resultado")
                        .log(LogDetail.BODY)
                        .build();

        ResponseSpecBuilder respBuilder = new ResponseSpecBuilder();
        final ResponseSpecification responseEsperado =
                respBuilder
                        .expectStatusCode(OK.value())
                        .build();

        given()
                .when()
                .spec(requestEspecificado)

                .get("/users")

                .then()
                .spec(responseEsperado)

//                .statusCode(OK.value())


                .body("$" ,hasSize(3))

                //Checa LISTA na Raiz(comum no HATEOAS)
                .body("name" ,hasItems("João da Silva" ,"Maria Joaquina" ,"Ana Júlia"))
                .body("age[0]" ,is(30))

                //Checa SUB-LISTA da Lista Raiz
                //  Ordenacao necessaria
                .body("filhos.name" ,hasItems(Arrays.asList("Zezinho" ,"Luizinho")))

                //  Letra "F" ao final de numeros fracionados OBRIGATORIA - cASO CONTRARIO erro DE <*****>
                .body("salary" ,contains(1234.5678f ,2500 ,null)) // DEVE TER PADRAO FLOAT - f AO FINAL
        ;
    }

    @Test
    public void advancedCheckCommandsPart01() {
        given()
                .when()
                .get("/users")

                .then()
                .statusCode(OK.value())

                // Checa TOTAL DE ITEMS da lista - verificando UMA-A-UM
                .body("$" ,hasSize(3))

                //METODOS AVANCADOS*****
                // Checa, dentro da LISTA, ALL com IDADE igual ou maior que 25
                .body("age.findAll{ it <= 25 }.size()" ,is(2))
                .body("age.findAll{ it <= 25 && it > 20 }.size()" ,is(1))

                // Checa nome, da pessoa rastreada por idade
                .body("findAll{ it.age <= 25 && it.age > 20 }[0].name" ,is("Maria Joaquina"))

                // Checa ITEM ESPECIFICO
                .body("find{ it.age <= 25 }.name" ,is("Maria Joaquina"))
        ;
    }

    @Test
    public void advancedCheckCommandsPart02() {
        given()
                .when()
                .get("/users")

                .then()
                .statusCode(OK.value())

                // Checa NOMES
                //   com "N"
                .body("findAll{ it.name.contains('n') }.name" ,hasItems("Maria Joaquina" ,"Ana Júlia"))
                //   com 10 Caracteres
                .body("findAll{ it.name.length() > 10 }.name" ,hasItems("Maria Joaquina" ,"João da Silva"))
                //   comecam com MARIA - transformando o resultado em MAIUSCULO
                .body("name.findAll{ it.startsWith('Maria') }.collect{ it.toUpperCase() }" ,hasItems("MARIA JOAQUINA"))
                //   comecam com MARIA - transformando o resultado em MAIUSCULO + TOTAL DOS VALORES ENCONTRADOS
                .body("name.findAll{ it.startsWith('Maria') }.collect{ it.toUpperCase() }.toArray()" ,
                        allOf(arrayContaining("MARIA JOAQUINA") ,arrayWithSize(1)))
        ;
    }

    @Test
    public void advancedCheckCommandsPart03() {
        given()
                .when()
                .get("/users")

                .then()
                .statusCode(OK.value())

                //   calculando SOBRE OS VALORES ENCONTRADOS
                .body("age.collect{it * 2}" ,hasItems(60 ,50 ,40))
                .body("id.max()" ,is(3))
                .body("salary.min()" ,is(1234.5678f)) // PADRAO FLOAT - 'f' AO FINAL
                .body("salary.findAll{ it != null }.sum()" ,
                        is(closeTo(3734.5678f ,0.001))) // PADRAO FLOAT - 'f' AO FINAL
                .body("salary.findAll{ it != null }.sum()" ,
                        allOf(greaterThan(3000d) ,lessThan(5000d))) // PADRAO DECIMAL - 'D' AO FINAL
        ;
    }
}
