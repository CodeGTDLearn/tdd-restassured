package com.example.demo.resource;

import com.example.demo.modelo.Pessoa;
import com.example.demo.repo.PessoaRepoInt;
import com.example.demo.servico.PessoaServiceInt;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

import static com.example.demo.databuilders.ObjectMotherPessoa.pessoaComCpfTelAddress;
import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertEquals;
import static org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment;

@SpringBootTest(webEnvironment = WebEnvironment.DEFINED_PORT)
@Sql(value = "/preload-ScriptHSQL-dbTest.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(value = "/clean-ScriptHSQL-dbTest.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
@DirtiesContext
@RunWith(SpringRunner.class)
@AutoConfigureTestDatabase(replace = Replace.NONE)
@TestPropertySource("classpath:application-test.properties")
public class ResourceRestAssureTricksTest {

    @Before
    public void setUp() {
        RestAssured.baseURI = "https://api.github.com";
        RestAssured.port = 443;
    }

    @AfterClass
    public static void afterClass() throws Exception {
        RestAssured.reset();
    }

    @Test
    public void whenMeasureResponseTime_thenOK() {

        Response response = RestAssured.get("/users/eugenp");

        long timeInMS = response.time();
        long timeInS = response.timeIn(TimeUnit.SECONDS);

        //org.junit.Assert.assertEquals;
        assertEquals(timeInS, timeInMS / 1000);
    }

    @Test
    public void whenValidateResponseTime_thenSuccess() {

        //io.restassured.RestAssured.when;
        when().get("/users/eugenp").then().time(lessThan(5000L));

        when().get("/users/eugenp").then().time(lessThan(5L), TimeUnit.SECONDS);

    }

    @Test
    public void whenLogResponseIfErrorOccurred_thenSuccess() {

        when().get("/users/eugenp")
                .then().log().ifError();
        when().get("/users/eugenp")
                .then().log().ifStatusCodeIsEqualTo(500);
        when().get("/users/eugenp")
                .then().log().ifStatusCodeMatches(greaterThan(200));
    }

    @Test
    public void test_APIWithOAuth2Authentication_ShouldBeGivenAccess() {

        given().
                auth().
                oauth2(YOUR_AUTHENTICATION_TOKEN_GOES_HERE).
                when().
                get("http://path.to/oath2/secured/api").
                then().
                assertThat().
                statusCode(200);
    }

    @Test
    public void whenLogRequest_thenOK() {
        given().log().all()
                .when().get("/users/eugenp")
                .then().statusCode(200);
    }

    @Test
    public void whenLogResponse_thenOK() {
        when().get("/repos/eugenp/tutorials")
                .then().log().body().statusCode(200);
    }

    @Test
    public void whenLogOnlyIfValidationFailed_thenSuccess() {
        when().get("/users/eugenp")
                .then().log().ifValidationFails().statusCode(200);

        given().log().ifValidationFails()
                .when().get("/users/eugenp")
                .then().statusCode(200);
    }

    @Test
    public void test_ScenarioRetrieveFirstCircuitFor2017SeasonAndGetCountry_ShouldBeAustralia() {

        // First, retrieve the circuit ID for the first circuit of the 2017 season
        String circuitId = given().
                when().
                get("http://ergast.com/api/f1/2017/circuits.json").
                then().
                extract().
                path("MRData.CircuitTable.Circuits.circuitId[0]");

        // Then, retrieve the information known for that circuit and verify it is located in Australia
        given().
                pathParam("circuitId",circuitId).
                when().
                get("http://ergast.com/api/f1/circuits/{circuitId}.json").
                then().
                assertThat().
                body("MRData.CircuitTable.Circuits.Location[0].country",equalTo("Australia"));
    }

    @Test
    public void testAuthMd5() {
        given().
                param("text", "test").
                when().
                get("http://md5.jsontest.com").
                then().
                body("md5",equalTo("098f6bcd4621d373cade4e832627b4f6")).
                and().
                body("original", equalTo("incorrect"));
    }

    @Test
    public void test_APIWithBasicAuthentication_ShouldBeGivenAccess() {

        given().
                auth().
                preemptive().
                basic("username", "password").
                when().
                get("http://path.to/basic/secured/api").
                then().
                assertThat().
                statusCode(200);
    }

}
