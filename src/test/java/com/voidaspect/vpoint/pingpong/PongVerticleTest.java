package com.voidaspect.vpoint.pingpong;

import io.vertx.junit5.Checkpoint;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import io.vertx.reactivex.core.Vertx;
import io.vertx.reactivex.ext.web.client.WebClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(VertxExtension.class)
class PongVerticleTest {

  @BeforeEach
  void deploy_verticle(Vertx vertx, VertxTestContext testContext) {
    vertx.rxDeployVerticle(new PongVerticle())
      .subscribe(id -> testContext.completeNow(), testContext::failNow);
  }

  @Test
  @DisplayName("Should start a Web Server on port 8080")
  void test_ping_pong(Vertx vertx, VertxTestContext testContext) {

    Checkpoint checkpoint = testContext.checkpoint(10);

    var request = WebClient
      .create(vertx)
      .get(8080, "localhost", "/");

    request
      .rxSend()
      .repeat(10)
      .subscribe(
        response -> testContext.verify(() -> {
          assertEquals("Pong!", response.bodyAsString());
          assertEquals(200, response.statusCode());
          checkpoint.flag();
        }),
        testContext::failNow);
  }

}
