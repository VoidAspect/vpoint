package com.voidaspect.vpoint.common;

public enum ContentType {
  TEXT_PLAIN("text/plain"),
  APPLICATION_JSON_UTF8("application/json;charset=utf8");

  private final String displayName;

  ContentType(String displayName) {
    this.displayName = displayName;
  }

  public String getDisplayName() {
    return displayName;
  }
}
