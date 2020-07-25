package com.example.demo.resource;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.TimeUnit;

import static io.restassured.RestAssured.given;
import static io.restassured.RestAssured.when;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.lessThan;
import static org.junit.Assert.assertEquals;

public class RA_Request_Response extends GlobalTestConfig {

    @Before
    public void setUp() {
        RestAssured.baseURI = "https://api.github.com";
    }

    @Test
    public void whenMeasureResponseTime_thenOK() {
        Response response = RestAssured.get("/users/eugenp");

        long timeInMS = response.time();
        long timeInS = response.timeIn(TimeUnit.SECONDS);

        //org.junit.Assert.assertEquals;
        assertEquals(timeInS ,timeInMS / 1000);
    }

    @Test
    public void whenValidateResponseTime_thenSuccess() {

        //io.restassured.RestAssured.when;
        when().get("/users/eugenp").then().time(lessThan(5000L));

        when().get("/users/eugenp").then().time(lessThan(5L) ,TimeUnit.SECONDS);

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
    public void whenLogRequest_thenOK() {
        given()
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
}
