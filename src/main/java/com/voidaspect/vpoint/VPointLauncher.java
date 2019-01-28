package com.voidaspect.vpoint;


import io.vertx.core.VertxOptions;
import io.vertx.reactivex.core.Vertx;
import io.vertx.spi.cluster.hazelcast.HazelcastClusterManager;

public class VPointLauncher {

  public static void main(String[] args) {
    Vertx.rxClusteredVertx(new VertxOptions().setClusterManager(new HazelcastClusterManager()))
      .flatMap(vertx -> vertx.rxDeployVerticle(new MainVerticle()))
      .ignoreElement().blockingAwait();
  }
}
