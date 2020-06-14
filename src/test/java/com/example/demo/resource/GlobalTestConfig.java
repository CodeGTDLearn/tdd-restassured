package com.example.demo.resource;

import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.http.ContentType;
import org.hamcrest.Matchers;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;

import static org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment;

@Sql(value = "/data-mass-load.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(value = "/data-mass-clean.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
@DirtiesContext
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.DEFINED_PORT)
@AutoConfigureTestDatabase(replace = Replace.NONE)
@TestPropertySource("classpath:config-test.properties")
@TestPropertySource("classpath:application-test.properties")
@Ignore
public class GlobalTestConfig {

    @Value("${baseURI}")
    String baseUri;

//    @Value("${basePATH}")
//    String basePath;

    @Value("${API_auth_no_aut}")
    String API_auth_no_aut;

    @Value("${API_openweathermap}")
    String API_openweathermap;

    @Value("${API_wcaquino_basic}")
    String API_wcaquino_basic;

    @Value("${API_wcaquino_basic2}")
    String API_wcaquino_basic2;

    @Value("${API_barrigarest_signin}")
    String API_barrigarest_signin;

    @Value("#{${MAX_TIMEOUT}}")
    Long MAX_TIMEOUT;

    ContentType API_CONTENT_TYPE = ContentType.JSON;

    @LocalServerPort
    int port;

    @Before
    public void setUp() {

        RestAssured.baseURI = baseUri;
        RestAssured.port = port;
//        RestAssured.basePath = basePath;

        //substitue os ".log().And()." em todos os REstAssureTestes
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();

        //DEFINING GLOBAL-SPECIFICATIONS TO REQUESTS
        //DEFINE CONFIG-GLOBAL PARA OS REQUESTS DOS TESTES
        //RequestSpecBuilder reqBuilder = new RequestSpecBuilder();
        //SET CONTENT-TYPE FOR ALL TESTS
        //reqBuilder.setContentType(API_CONTENT_TYPE);
        //SET TOKEN JWT FOR ALL TESTS
        //reqBuilder.addHeader("Authorization", "bearer " + TOKEN)
        //RestAssured.requestSpecification = reqBuilder.build();
        RestAssured.requestSpecification = new RequestSpecBuilder()
                .setContentType(API_CONTENT_TYPE)
                .build();

        //DEFINING GLOBAL-SPECIFICATIONS TO RESPONSES
        //ResponseSpecBuilder respBuilder = new ResponseSpecBuilder();
        //respBuilder.expectResponseTime(Matchers.lessThanOrEqualTo(MAX_TIMEOUT));
        //RestAssured.responseSpecification = respBuilder.build();
        RestAssured.responseSpecification = new ResponseSpecBuilder()
                .expectResponseTime(Matchers.lessThanOrEqualTo(MAX_TIMEOUT))
                .build();
    }

    @After
    public void tearDown() {

        //DELETE AO TOKEN AFTER ALL TESTS
//        FilterableRequestSpecification req = (FilterableRequestSpecification) RestAssured.requestSpecification;
//        req.removeHeader("Autorization");

        RestAssured.reset();
    }
}
