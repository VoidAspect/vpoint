package com.voidaspect.vpoint.roll;

public final class RollResponse {

  private final String roll;

  private final int[] results;

  public RollResponse(String roll, int[] results) {
    this.roll = roll;
    this.results = results;
  }

  public String getRoll() {
    return roll;
  }

  public int[] getResults() {
    return results;
  }

}
