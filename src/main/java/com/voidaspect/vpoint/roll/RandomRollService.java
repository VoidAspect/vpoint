package com.voidaspect.vpoint.roll;

import java.util.Random;

public final class RandomRollService implements RollService {

  @Override
  public int[] roll(int n, int d) {
    return new Random()
      .ints(n, 1, d + 1)
      .toArray();
  }
}
