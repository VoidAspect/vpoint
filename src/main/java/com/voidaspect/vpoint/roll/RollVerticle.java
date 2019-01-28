package com.voidaspect.vpoint.roll;

import com.voidaspect.vpoint.common.ContentType;
import com.voidaspect.vpoint.common.JsonError;
import com.voidaspect.vpoint.common.VPointException;
import com.voidaspect.vpoint.pingpong.PongVerticle;
import io.reactivex.Completable;
import io.vertx.core.http.HttpHeaders;
import io.vertx.core.json.Json;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.reactivex.config.ConfigRetriever;
import io.vertx.reactivex.core.AbstractVerticle;
import io.vertx.reactivex.ext.web.Router;
import io.vertx.reactivex.ext.web.RoutingContext;

import java.util.Optional;

public class RollVerticle extends AbstractVerticle {

  private static final Logger log = LoggerFactory.getLogger(PongVerticle.class);

  private final RollService rollService;

  public RollVerticle() {
    rollService = new RandomRollService();
  }

  @Override
  public Completable rxStart() {
    return ConfigRetriever.create(vertx)
      .rxGetConfig()
      .map(cfg -> cfg.getJsonObject("roll"))
      .flatMap(cfg -> vertx.createHttpServer()
        .requestHandler(router())
        .rxListen(cfg.getInteger("port")))
      .ignoreElement();
  }

  private Router router() {
    var router = Router.router(vertx);

    router.get("/roll")
      .handler(this::handleRoll)
      .failureHandler(this::handleFailure);

    return router;
  }

  private void handleRoll(RoutingContext rc) {
    var request = rc.request();

    var number = Optional.ofNullable(request.getParam("n"))
      .stream()
      .mapToInt(Integer::parseInt).findAny()
      .orElse(1);

    var sides = Optional.ofNullable(request.getParam("d"))
      .stream().mapToInt(Integer::parseInt)
      .filter(i -> i > 1).findAny()
      .orElseThrow(() -> new VPointException("Incorrect number of sides on a die!", 400));

    var rollValue = number + "d" + sides;

    log.info("ROLLING " + rollValue);

    var rolls = rollService.roll(number, sides);

    var responseBody = new RollResponse(rollValue, rolls);

    rc.response()
      .putHeader(HttpHeaders.CONTENT_TYPE, ContentType.APPLICATION_JSON_UTF8.getDisplayName())
      .end(Json.encode(responseBody));
  }

  private void handleFailure(RoutingContext rc) {
    var failure =  rc.failure();
    log.error("failed to perform a roll!", failure);
    final int code;
    if (failure instanceof VPointException) {
      code = ((VPointException) failure).getCode();
    } else {
      code = 500;
    }
    var message = failure.getClass().getName() + " - " + failure.getMessage();
    var jsonError = new JsonError(rc.currentRoute().getPath(), message);
    rc.response()
      .setStatusCode(code)
      .putHeader(HttpHeaders.CONTENT_TYPE, ContentType.APPLICATION_JSON_UTF8.getDisplayName())
      .end(Json.encode(jsonError));
  }

}
