package com.voidaspect.vpoint;

import com.voidaspect.vpoint.pingpong.PongVerticle;
import com.voidaspect.vpoint.roll.RollVerticle;
import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.vertx.reactivex.core.AbstractVerticle;

public class MainVerticle extends AbstractVerticle {

  @Override
  public Completable rxStart() {
    return Flowable.fromArray(new PongVerticle(), new RollVerticle())
      .flatMapSingle(vertx::rxDeployVerticle)
      .ignoreElements();

  }

}
