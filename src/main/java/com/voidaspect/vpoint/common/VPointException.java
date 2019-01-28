package com.voidaspect.vpoint.common;

public final class VPointException extends RuntimeException {

  private final int code;

  public VPointException(String message, int code) {
    super(message);
    this.code = code;
  }

  public int getCode() {
    return code;
  }
}
