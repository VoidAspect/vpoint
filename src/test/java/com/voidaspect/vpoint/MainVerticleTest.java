package com.voidaspect.vpoint;

import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import io.vertx.reactivex.core.Vertx;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(VertxExtension.class)
class MainVerticleTest {

  @Test
  @DisplayName("should deploy two verticles")
  void deploy_verticle(Vertx vertx, VertxTestContext testContext) {
    vertx.rxDeployVerticle(new MainVerticle())
      .subscribe(id -> {
        assertThat(vertx.deploymentIDs())
          .hasSize(3)
          .contains(id);
        testContext.completeNow();
      }, testContext::failNow);
  }


}
