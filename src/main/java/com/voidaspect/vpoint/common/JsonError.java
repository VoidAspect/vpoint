package com.voidaspect.vpoint.common;

public final class JsonError {

  private final String source;

  private final String message;

  public JsonError(String source, String message) {
    this.source = source;
    this.message = message;
  }

  public String getSource() {
    return source;
  }

  public String getMessage() {
    return message;
  }

}
