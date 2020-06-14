package com.example.demo.resource;

import io.restassured.http.Header;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.lessThanOrEqualTo;
import static org.springframework.http.HttpStatus.*;

public class RA_DownUpload extends GlobalTestConfig {

    @Test
    public void sendFileFailTest() {
        given()

                .when()
                .post("/upload")
                .then()

                .statusCode(NOT_FOUND.value())
                .body("error" ,is("Arquivo não enviado"))
        ;
    }

    @Test
    public void sendFileOKTest() {
        given()
                .multiPart(
                        "arquivo" ,
                        new File("src/test/resources/file.pdf"))
                .header(new Header("content-type", "multipart/form-data"))
                .when()
                .post("/upload") //GET UPLOAD FILE
                .then()

                .statusCode(OK.value())
                .log().all()
                .body("name" ,is("file.pdf"))
        ;
    }

    @Test
    public void noFileAbove1MegaTest() {
        given()
                .multiPart("arquivo" ,new File("src/test/resources/6m.zip"))
                .header("Content-Type", "multipart/json")
                .when()
                .post("/upload")
                .then()
                .statusCode(PAYLOAD_TOO_LARGE.value())
        ;
    }

    @Test
    public void uploadTimeTest() {

        given()
                .multiPart("arquivo" ,new File("src/test/resources/6m.zip"))
                .header("Content-Type", "multipart/json")
                .when()
                .post("/upload")
                .then()

                .statusCode(PAYLOAD_TOO_LARGE.value())
                //medira a demora decorrent do tamanho do arquivo
                .time(lessThanOrEqualTo(10000L))
        ;
    }

    @Test
    public void downloadTest() throws IOException {
        final byte[] imageVAR = given()

                .when()
                .get("/download") //GET DOWNLOAD FILE
                .then()
                .statusCode(OK.value())
                .extract().asByteArray();

        File imagemDownload = new File("src/test/resources/file.pdf");
        OutputStream out = new FileOutputStream(imagemDownload);
        out.write(imageVAR);
        out.close();
        System.out.println(imagemDownload.length());
        Assert.assertThat(imagemDownload.length() ,lessThanOrEqualTo(100000L));
    }
}
