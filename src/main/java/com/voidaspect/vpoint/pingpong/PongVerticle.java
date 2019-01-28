package com.voidaspect.vpoint.pingpong;


import com.voidaspect.vpoint.common.ContentType;
import io.reactivex.Completable;
import io.vertx.core.http.HttpHeaders;
import io.vertx.reactivex.config.ConfigRetriever;
import io.vertx.reactivex.core.AbstractVerticle;
import io.vertx.reactivex.core.http.HttpServerRequest;

public class PongVerticle extends AbstractVerticle {

  @Override
  public Completable rxStart() {
    return ConfigRetriever.create(vertx)
      .rxGetConfig()
      .map(cfg -> cfg.getJsonObject("pong"))
      .flatMap(cfg -> vertx.createHttpServer()
        .requestHandler(this::handlePing)
        .rxListen(cfg.getInteger("port")))
      .ignoreElement();
  }

  private void handlePing(HttpServerRequest rec) {
    rec.response()
      .putHeader(HttpHeaders.CONTENT_TYPE, ContentType.TEXT_PLAIN.getDisplayName())
      .end("Pong!");
  }

}
