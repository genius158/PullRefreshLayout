package com.yan.refreshloadlayouttest.widget;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.Intent;
import android.os.CountDownTimer;
import androidx.core.content.ContextCompat;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.yan.pullrefreshlayout.PullRefreshLayout;
import com.yan.pullrefreshlayout.ViscousInterpolator;
import com.yan.refreshloadlayouttest.OptionActivity;
import com.yan.refreshloadlayouttest.R;

/**
 * Created by yan on 2017/9/16.
 */

public class TwoRefreshHeader extends HeaderOrFooter implements View.OnClickListener {
  private final String TWO_REFRESH_TEXT = "二级刷新";
  private final int REFRESH_FIRST_DURING = 180;
  private final int TWO_REFRESH_DURING = 400;

  private int twoRefreshDistance;
  private int firstRefreshTriggerDistance;

  private PullRefreshLayout pullRefreshLayout;
  private TextView tvTitle;

  private boolean isTwoRefresh;

  private ObjectAnimator alphaInAnimation;
  private ObjectAnimator alphaOutAnimation;
  private ValueAnimator translateYAnimation;

  private CountDownTimer countDownTimer = new CountDownTimer(3100, 1000) {
    @Override public void onTick(long millisUntilFinished) {
      tvTitle.setText("倒计时" + (int) ((millisUntilFinished) / 1000) + "秒进入新的世界");
    }

    @Override public void onFinish() {
      tvTitle.setText("倒计时0秒进入新的世界");
      getContext().startActivity(new Intent(getContext(), OptionActivity.class).addFlags(
          Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK));
    }
  };

  public boolean isTwoRefresh() {
    return isTwoRefresh;
  }

  public TwoRefreshHeader(Context context, final PullRefreshLayout pullRefreshLayout) {
    super(context);
    this.pullRefreshLayout = pullRefreshLayout;
    pullRefreshLayout.setOnTouchListener(new OnTouchListener() {
      @Override public boolean onTouch(View v, MotionEvent ev) {
        if (!isTwoRefresh) {
          return false;
        }

        switch (ev.getActionMasked()) {
          case MotionEvent.ACTION_MOVE:
            if (translateYAnimation.isRunning()) {
              translateYAnimation.cancel();
            }
            break;
          case MotionEvent.ACTION_UP:
          case MotionEvent.ACTION_CANCEL:
            if (pullRefreshLayout.getMoveDistance() <= getHeight() - firstRefreshTriggerDistance) {
              pullRefreshLayout.refreshComplete();
            } else if (pullRefreshLayout.getMoveDistance() < getHeight()) {
              translateYAnimation.setFloatValues(pullRefreshLayout.getMoveDistance(), getHeight());
              translateYAnimation.start();
            }
            break;
        }
        return false;
      }
    });
    twoRefreshInit();
  }

  private void twoRefreshInit() {
    setLayoutParams(new ViewGroup.LayoutParams(-1, -1));

    firstRefreshTriggerDistance =
        TwoRefreshHeader.this.pullRefreshLayout.getRefreshTriggerDistance();
    twoRefreshDistance = firstRefreshTriggerDistance * 2;

    FrameLayout.LayoutParams layoutParams = (LayoutParams) rlContainer.getLayoutParams();
    layoutParams.gravity = Gravity.BOTTOM;
    rlContainer.setLayoutParams(layoutParams);

    tvTitle = new TextView(getContext());
    tvTitle.setTextSize(20);
    tvTitle.setGravity(Gravity.CENTER);
    tvTitle.setTextColor(ContextCompat.getColor(getContext(), R.color.colorWhite));
    tvTitle.setText("倒计时3秒进入新的世界");
    FrameLayout.LayoutParams ivLp = new LayoutParams(-1, -1);
    ivLp.gravity = Gravity.CENTER;
    tvTitle.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.colorPrimary));
    tvTitle.setAlpha(0F);
    addView(tvTitle, ivLp);

    animationInit();

    tvTitle.setOnClickListener(this);
  }

  private void animationInit() {
    alphaInAnimation = ObjectAnimator.ofFloat(tvTitle, "alpha", 0, 1);
    alphaInAnimation.setDuration(TWO_REFRESH_DURING);
    alphaOutAnimation = ObjectAnimator.ofFloat(tvTitle, "alpha", 1, 0);
    alphaOutAnimation.setDuration(TWO_REFRESH_DURING);

    translateYAnimation = ValueAnimator.ofFloat(0, 0);
    translateYAnimation.setDuration(TWO_REFRESH_DURING);
    translateYAnimation.setInterpolator(new ViscousInterpolator());
    translateYAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
      @Override public void onAnimationUpdate(ValueAnimator animation) {
        float m = (float) animation.getAnimatedValue();
        pullRefreshLayout.moveChildren((int) m);
      }
    });
  }

  @Override public void onPullChange(float percent) {
    super.onPullChange(percent);
    if (!pullRefreshLayout.isHoldingTrigger()) {
      if (pullRefreshLayout.getMoveDistance() >= twoRefreshDistance) {
        if (!tv.getText().toString().equals(TWO_REFRESH_TEXT)) {
          tv.setText(TWO_REFRESH_TEXT);
        }
      } else if (tv.getText().toString().equals(TWO_REFRESH_TEXT)) {
        tv.setText("release loading");
      }
    } else if (pullRefreshLayout.getMoveDistance() >= getHeight()) {
      pullRefreshLayout.setDispatchPullTouchAble(true);
      isTwoRefresh = true;
    }
  }

  @Override public void onPullHolding() {
    super.onPullHolding();
    if (pullRefreshLayout.getMoveDistance() >= twoRefreshDistance) {
      pullRefreshLayout.setPullDownMaxDistance(getHeight() * 2);
      pullRefreshLayout.setRefreshTriggerDistance(getHeight());
      pullRefreshLayout.setRefreshAnimationDuring(TWO_REFRESH_DURING);
      pullRefreshLayout.setDispatchPullTouchAble(false);
      alphaInAnimation.start();

      tvTitle.setText("倒计时3秒进入新的世界");
      countDownTimer.start();
      pullRefreshLayout.setTwinkEnable(false);
    }
  }

  @Override public void onPullFinish(boolean flag) {
    super.onPullFinish(flag);
    if (isTwoRefresh) {
      isTwoRefresh = false;
      countDownTimer.cancel();
      pullRefreshLayout.setTwinkEnable(true);
      if (tvTitle.getAlpha() > 0) {
        alphaOutAnimation.start();
      }

      pullRefreshLayout.setPullDownMaxDistance(getHeight());
      pullRefreshLayout.setRefreshTriggerDistance(firstRefreshTriggerDistance);
      pullRefreshLayout.setRefreshAnimationDuring(REFRESH_FIRST_DURING);
      pullRefreshLayout.setDispatchPullTouchAble(true);
    }
  }

  @Override protected void onDetachedFromWindow() {
    super.onDetachedFromWindow();
    countDownTimer.cancel();

    if (alphaInAnimation.isRunning()) {
      alphaInAnimation.cancel();
    }
    if (alphaOutAnimation.isRunning()) {
      alphaOutAnimation.cancel();
    }
    if (translateYAnimation.isRunning()) {
      translateYAnimation.cancel();
    }
  }

  @Override public void onClick(View v) {
    if (!pullRefreshLayout.isLayoutDragMoved()) {
      Toast.makeText(getContext().getApplicationContext(), "new world", Toast.LENGTH_SHORT).show();
    }
  }
}