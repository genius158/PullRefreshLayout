package com.yan.pullrefreshlayout;

import android.view.animation.Interpolator;

/**
 * @author yanxianwei
 */
public final class ViscousInterpolator implements Interpolator {
  /**
   * Controls the viscous fluid effect (how much of it).
   */
  private static final float VISCOUS_FLUID_SCALE = 4.5f;

  private final float VISCOUS_FLUID_NORMALIZE;
  private final float VISCOUS_FLUID_OFFSET;

  private float currentViscousScale;

  public ViscousInterpolator() {
    this(VISCOUS_FLUID_SCALE);
  }

  public ViscousInterpolator(float viscousScale) {
    currentViscousScale = viscousScale;
    // must be set to 1.0 (used in viscousFluid())
    VISCOUS_FLUID_NORMALIZE = 1.0f / viscousFluid(currentViscousScale, 1.0f);
    // account for very small floating-point error
    VISCOUS_FLUID_OFFSET = 1.0f - VISCOUS_FLUID_NORMALIZE * viscousFluid(currentViscousScale, 1.0f);
  }

  private float viscousFluid(float viscousScale, float x) {
    x *= viscousScale;
    if (x < 1.0f) {
      x -= (1.0f - (float) Math.exp(-x));
    } else {
      float start = 0.36787944117f;   // 1/e == exp(-1)
      x = 1.0f - (float) Math.exp(1.0f - x);
      x = start + x * (1.0f - start);
    }
    return x;
  }

  @Override
  public float getInterpolation(float input) {
    final float interpolated = VISCOUS_FLUID_NORMALIZE * viscousFluid(currentViscousScale, input);
    if (interpolated > 0) {
      return interpolated + VISCOUS_FLUID_OFFSET;
    }
    return interpolated;
  }
}