package com.example.demo.resource;

import io.restassured.http.ContentType;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;

public class RA_Authentic_jwt extends GlobalTestConfig {

    private String url1, url2, url3, url4, url5;

    private Object tokenJWTExtraido;

    @Before
    public void setUpLocal() {
        url1 = super.API_auth_no_aut;
        url2 = super.API_openweathermap;
        url3 = super.API_wcaquino_basic;
        url4 = super.API_wcaquino_basic2;
        url5 = super.API_barrigarest_signin;

        //CAPTURANDO TOKEN JWT
        //LOGIN + SENHA CADASTRADAS NA API
        Map<String, String> logarNaApi = new HashMap<String, String>();
        logarNaApi.put("email" ,"paulo@paulo123456");
        logarNaApi.put("senha" ,"123456");

        this.tokenJWTExtraido = given()

                .body(logarNaApi)
                .contentType(ContentType.JSON)

                .when()
                //.post("http://barrigarest.wcaquino.me/signin")//POST
                .post(url5)//POST

                .then()

                .statusCode(OK.value())
                .extract().path("token");
    }

    @Test
    public void API_Sem_Necessidade_de_Autentic() {
        given()

                .when()
                .get(url1)

                .then()

                .statusCode(OK.value())

                .body("name" ,is("Luke Skywalker"))
        ;
    }

    @Test
    public void API_plainText_Autentic() {

        //Simples Key fornecida pela API
        String plainTextKey = "2838b8ef48b0fb18e1eed8336a07efa6";

        given()

                .queryParam("q" ,"Fortaleza,BR")
                .queryParam("appid" ,plainTextKey)
                .queryParam("units" ,"metric")

                .when()
                //.get("https://api.openweathermap.org/data/2.5/weather")
                .get(url2)

                .then()

                .statusCode(OK.value())

                .body("name" ,is("Fortaleza"))
                .body("coord.lon" ,is(-38.52f))
                .body("main.temp" ,greaterThan(25f))
        ;
    }

    @Test
    public void API_basic_Key_ERROR_Autentic() {

        given()


                .when()
//                .get("http://restapi.wcaquino.me/basicauth")
                .get(url3)

                .then()

                .statusCode(UNAUTHORIZED.value())
        ;
    }

    @Test
    public void API_basic_Key_Autentic_v1() {

        //Simples Key fornecida pela API
        String entraLoginSenhaURL = "admin:senha@";

        given()


                .when()
                .get("http://" + entraLoginSenhaURL + "restapi.wcaquino.me/basicauth")

                .then()

                .statusCode(OK.value())
                .body("status" ,is("logado"))
        ;
    }

    @Test
    public void API_basic_Key_Autentic_v2() {

        given()

                .auth().basic("admin" ,"senha")

                .when()
                .get(url3)

                .then()

                .statusCode(OK.value())
                .body("status" ,is("logado"))
        ;
    }

    @Test
    public void API_basic_Key_Autentic_Preemptive_Challenge() {

        given()

                .auth().preemptive()
                .basic("admin" ,"senha")

                .when()
                .get(url4)

                .then()

                .statusCode(OK.value())
                .body("status" ,is("logado"))
        ;
    }

    @Test
    public void API_JWT_HEADER_Autentic() {

        given()

                //CHAVE DE INSERCAO DO JWT NO HEADER
                //--PREFIXO ANTIGO: JWT (a URL aqui testada, usa o sistema antigo)
                .header("Authorization" ,"JWT " + this.tokenJWTExtraido)

                //--PREFIXO ATUAL: bearer
                // .header("Authorization" ,"JWT " + this.tokenJWTExtraido)

                .when()
                .get("http://barrigarest.wcaquino.me/contas")//GET

                .then()

                .statusCode(OK.value())

                //CONTA CADASTRADA NA API
                .body("nome" ,hasItem("conta do paulo"))
        ;
    }

    @Test
    public void API_JWT_HEADER_Autentic_FAIL() {
        given()
                .when()
                .get("http://barrigarest.wcaquino.me/contas")//GET

                .then()

                .statusCode(UNAUTHORIZED.value())
        ;
    }

    //    @Test
//    public void test_APIWithOAuth2Authentication_ShouldBeGivenAccess() {
//
//        given().
//                auth().
//                oauth2(YOUR_AUTHENTICATION_TOKEN_GOES_HERE).
//                when().
//                get("http://path.to/oath2/secured/api").
//                then().
//                assertThat().
//                statusCode(200);
//    }
}
