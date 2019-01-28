package com.voidaspect.vpoint.roll;

import com.voidaspect.vpoint.common.VPointException;
import io.vertx.core.json.JsonObject;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import io.vertx.reactivex.core.Vertx;
import io.vertx.reactivex.ext.web.client.HttpRequest;
import io.vertx.reactivex.ext.web.client.WebClient;
import io.vertx.reactivex.ext.web.codec.BodyCodec;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;


@ExtendWith(VertxExtension.class)
class RollVerticleTest {

  @BeforeEach
  void deploy_verticle(Vertx vertx, VertxTestContext testContext) {
    vertx.rxDeployVerticle(new RollVerticle())
      .subscribe(id -> testContext.completeNow(), testContext::failNow);
  }

  @Test
  @DisplayName("GET /roll should roll dice")
  void test_roll_dice(Vertx vertx, VertxTestContext testContext) {

    var requestBase = WebClient
      .create(vertx)
      .get(8081, "localhost", "/roll")
      .as(BodyCodec.jsonObject());

    var request_2d10 = requestBase.copy()
      .addQueryParam("n", "2")
      .addQueryParam("d", "10");

    assertRollRequest(testContext, "2d10", 2, 10, request_2d10);

    var request_3d10 = requestBase.copy()
      .addQueryParam("n", "3")
      .addQueryParam("d", "10");

    assertRollRequest(testContext, "3d10", 3, 10, request_3d10);

    var request_d100 = requestBase.copy()
      .addQueryParam("d", "100");

    assertRollRequest(testContext, "1d100", 1, 100, request_d100);

    var request_50d6 = requestBase.copy()
      .addQueryParam("n", "50")
      .addQueryParam("d", "6");

    assertRollRequest(testContext, "50d6", 50, 6, request_50d6);
  }

  @Test
  @DisplayName("when 'd' not set, GET /roll should reply with 404")
  void test_roll_dice_no_sides(Vertx vertx, VertxTestContext testContext) {
    var request = WebClient
      .create(vertx)
      .get(8081, "localhost", "/roll")
      .as(BodyCodec.jsonObject());

    request.rxSend()
      .subscribe(
        response -> testContext.verify(() -> {
          var error = response.body();
          assertEquals(400, response.statusCode());
          assertEquals(VPointException.class.getName() + " - Incorrect number of sides on a die!", error.getString("message"));
          assertEquals("/roll", error.getString("source"));
          testContext.completeNow();
        }),
        testContext::failNow
      );
  }

  private static void assertRollRequest(
    VertxTestContext testContext,
    String expectedRoll,
    int expectedResultSize,
    int expectedResultHeight,
    HttpRequest<JsonObject> request) {

    request
      .rxSend()
      .subscribe(
        response -> testContext.verify(() -> {
          var body = response.body();
          var results = body
            .getJsonArray("results").stream()
            .map(o -> (int) o)
            .collect(Collectors.toUnmodifiableList());

          assertEquals(200, response.statusCode());
          assertEquals(expectedRoll, body.getString("roll"));
          assertThat(results)
            .hasSize(expectedResultSize)
            .allSatisfy(i -> assertThat(i).isBetween(1, expectedResultHeight));

          testContext.completeNow();
        }),
        testContext::failNow);
  }

}
