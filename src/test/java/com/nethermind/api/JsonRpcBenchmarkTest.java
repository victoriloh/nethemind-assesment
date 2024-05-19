package com.nethermind.api;

import io.restassured.RestAssured;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static io.restassured.RestAssured.given;

public class JsonRpcBenchmarkTest {

    private static final String BLOCK_NUMBER = "0x10"; // Replace with the block number you want to test

    @BeforeClass
    public void setup() {
        RestAssured.baseURI = "http://localhost:8545";
    }

    @Test
    public void benchmarkGetBlockByNumber_1000Requests() {
        long startTime = System.currentTimeMillis();
        for (int i = 0; i < 1000; i++) {
            given()
                    .header("Content-Type", "application/json")
                    .body("{\"jsonrpc\":\"2.0\",\"method\":\"eth_getBlockByNumber\",\"params\":[\"" + BLOCK_NUMBER + "\", true],\"id\":1}")
                    .when()
                    .post()
                    .then()
                    .statusCode(200);
        }
        long endTime = System.currentTimeMillis();
        System.out.println("Time for 1000 requests: " + (endTime - startTime) + " ms");
    }

    @Test
    public void benchmarkGetBlockByNumber_10000Requests() {
        long startTime = System.currentTimeMillis();
        for (int i = 0; i < 10000; i++) {
            given()
                    .header("Content-Type", "application/json")
                    .body("{\"jsonrpc\":\"2.0\",\"method\":\"eth_getBlockByNumber\",\"params\":[\"" + BLOCK_NUMBER + "\", true],\"id\":1}")
                    .when()
                    .post()
                    .then()
                    .statusCode(200);
        }
        long endTime = System.currentTimeMillis();
        System.out.println("Time for 10000 requests: " + (endTime - startTime) + " ms");
    }
}
