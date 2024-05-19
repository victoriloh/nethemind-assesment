package com.nethermind.api;

// src/test/java/com/nethermind/api/JsonRpcVerificationTest.java

import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.notNullValue;

public class JsonRpcVerificationTest {

    @Test
    public void testChainHead() {
        RestAssured.baseURI = "http://localhost:8545";

        // Get chain head
        String chainHead = given()
                .header("Content-Type", "application/json")
                .body("{\"jsonrpc\":\"2.0\",\"method\":\"eth_blockNumber\",\"params\":[],\"id\":1}")
                .when()
                .post()
                .then()
                .statusCode(200)
                .body("result", notNullValue())
                .extract()
                .path("result");

        // Get block by number
        Response response = given()
                .header("Content-Type", "application/json")
                .body("{\"jsonrpc\":\"2.0\",\"method\":\"eth_getBlockByNumber\",\"params\":[\"" + chainHead + "\", true],\"id\":1}")
                .when()
                .post()
                .then()
                .statusCode(200)
                .extract()
                .response();

        Assert.assertNotNull(response.jsonPath().get("result"));
        Assert.assertNull(response.jsonPath().get("error"));
    }
}
