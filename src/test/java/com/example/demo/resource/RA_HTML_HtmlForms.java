package com.example.demo.resource;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.path.xml.XmlPath;
import org.apache.commons.lang3.StringUtils;
import org.junit.Before;
import org.junit.Test;

import static io.restassured.RestAssured.given;
import static io.restassured.path.xml.XmlPath.CompatibilityMode.HTML;
import static org.junit.Assert.assertEquals;
import static org.springframework.http.HttpStatus.OK;

public class RA_HTML_HtmlForms extends GlobalTestConfig {

    @Before
    public void setUp() {
        RestAssured.baseURI = "http://seubarriga.wcaquino.me";
    }

    @Test
    public void manipulando_HTML_Form() {

        //LOGIN + SENHA CADASTRADAS NA API
        String email = "paulo@paulo123456";
        String senha = "123456";

        given()


                //PAGINA HTML - CHECAR O CODIGO FONTE
                //a) checar os 'names'dos fields do FORM
                .formParam("email" ,email)
                .formParam("senha" ,senha)
                .contentType(ContentType.URLENC.withCharset("UTF-8"))

                //b) checar a ACTION + METHOD do FORM
                .when()
                .post("/logar")//GET

                .then()

        ;
    }

    @Test
    public void manipulando_COOKIES_HTML_FORM_TABLE() {

        //LOGIN + SENHA CADASTRADAS NA API
        String email = "paulo@paulo123456";
        String senha = "123456";

        final String cookieExtraido = given()


                //PAGINA HTML - CHECAR O CODIGO FONTE
                //a) checar os 'names'dos fields do FORM
                .formParam("email" ,email)
                .formParam("senha" ,senha)
                .contentType(ContentType.URLENC.withCharset("UTF-8"))

                //b) checar a ACTION + METHOD do FORM
                .when()
                .post("/logar")     //POST

                .then()

                .statusCode(OK.value())
                .extract().header("set-cookie");
        ;

        String cookieFracionado = StringUtils.substringBetween(cookieExtraido ,"connect.sid=" ,"; Path=/");
        System.out.println("COOKIE EXTRAIDO: " + cookieFracionado);

        //http://seubarriga.wcaquino.me/contas
        final String extrato = given()


                //RODAR O TESTE UM VEZ PARA CHECAR A CHAVE / COOKIES{connect.sid}
                .cookie("connect.sid" ,cookieFracionado)

                //PAGINA CONTAS - ACESSADA DEPOIS DO LOG
                .when()
                .get("/contas")     //GET

                .then()

                .statusCode(OK.value())
                .extract().body().asString();
        ;

        XmlPath htmlPath = new XmlPath(HTML ,extrato);
        assertEquals(htmlPath.getString("html.body.table.tbody.tr[0].td[0]") ,"conta do paulo");
    }
}
