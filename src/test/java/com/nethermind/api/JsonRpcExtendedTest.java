// src/test/java/com/nethermind/api/JsonRpcExtendedTest.java
package com.nethermind.api;

import io.restassured.RestAssured;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.notNullValue;

public class JsonRpcExtendedTest {

    @BeforeClass
    public void setup() {
        RestAssured.baseURI = "http://localhost:8545";
    }

    @Test
    public void testGetBalance() {
        String address = "0x0000000000000000000000000000000000000000";

        String balance = given()
                .header("Content-Type", "application/json")
                .body("{\"jsonrpc\":\"2.0\",\"method\":\"eth_getBalance\",\"params\":[\"" + address + "\", \"latest\"],\"id\":1}")
                .when()
                .post()
                .then()
                .statusCode(200)
                .body("result", notNullValue())
                .extract()
                .path("result");

        Assert.assertNotNull(balance);
    }
}

