package com.yan.refreshloadlayouttest.widget;

import android.animation.ValueAnimator;
import android.content.Context;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import com.yan.pullrefreshlayout.PullRefreshLayout;
import com.yan.pullrefreshlayout.ViscousInterpolator;
import com.yan.refreshloadlayouttest.App;

import static com.yan.pullrefreshlayout.PRLCommonUtils.dipToPx;

/**
 * Created by yan on 2017/9/16.
 */

public class SlidingDownHeader extends NestedFrameLayout
    implements PullRefreshLayout.OnPullListener {
  private static final String TAG = "SlidingDownHeader";
  private PullRefreshLayout pullRefreshLayout;
  private View targetView;

  private final int SLIDING_OFFSET = dipToPx(App.getAppContext(), 60);
  private final int SLIDING_DURING = 400;

  private boolean isSlidingDown;

  private ValueAnimator translateYAnimation;

  public void setTargetView(View target) {
    this.targetView = target;
    addView(targetView);
  }

  public SlidingDownHeader(Context context, final PullRefreshLayout pullRefreshLayout) {
    super(context);
    this.pullRefreshLayout = pullRefreshLayout;
    pullRefreshLayout.setOnTouchListener(new OnTouchListener() {
      @Override public boolean onTouch(View v, MotionEvent ev) {
        if (!isSlidingDown) {
          return false;
        }

        switch (ev.getActionMasked()) {
          case MotionEvent.ACTION_MOVE:
            Log.e(TAG,
                "dispatchTouchEvent: ACTION_MOVEACTION_MOVEACTION_MOVEACTION_MOVEACTION_MOVE");
            if (translateYAnimation.isRunning()) {
              translateYAnimation.cancel();
            }

            if (pullRefreshLayout.isLayoutDragMoved()) {
              ev.setAction(MotionEvent.ACTION_CANCEL);
            } else if (pullRefreshLayout.isDragHorizontal()) {
              pullRefreshLayout.requestPullDisallowInterceptTouchEvent(true);
            }
            break;
          case MotionEvent.ACTION_UP:
          case MotionEvent.ACTION_CANCEL:
            Log.e(TAG, "dispatchTouchEvent: ACTION_CANCELACTION_CANCELACTION_CANCELACTION_CANCEL");
            if (isSlidingDown) {
              if (pullRefreshLayout.getMoveDistance() <= getHeight() - SLIDING_OFFSET) {
                pullRefreshLayout.refreshComplete();
              } else if (pullRefreshLayout.getMoveDistance() < getHeight()) {
                translateYAnimation.setFloatValues(pullRefreshLayout.getMoveDistance(),
                    getHeight());
                translateYAnimation.start();
              }
            }
            break;
        }
        return false;
      }
    });
    setLayoutParams(new ViewGroup.LayoutParams(-1, -1));
    post(new Runnable() {
      @Override public void run() {
        slidingDownInit();
      }
    });
  }

  private void slidingDownInit() {
    pullRefreshLayout.setRefreshTriggerDistance(SLIDING_OFFSET);
    pullRefreshLayout.setPullDownMaxDistance(getHeight() * 2);
    pullRefreshLayout.setRefreshAnimationDuring(SLIDING_DURING);

    translateYAnimation = ValueAnimator.ofFloat(0, 0);
    translateYAnimation.setDuration(SLIDING_DURING);
    translateYAnimation.setInterpolator(new ViscousInterpolator());
    translateYAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
      @Override public void onAnimationUpdate(ValueAnimator animation) {
        float m = (float) animation.getAnimatedValue();
        pullRefreshLayout.moveChildren((int) m);
      }
    });
  }

  @Override public void onPullChange(float percent) {
    if (!isSlidingDown
        && pullRefreshLayout.isHoldingTrigger()
        && !pullRefreshLayout.isHoldingFinishTrigger()
        && pullRefreshLayout.getMoveDistance() >= getHeight()) {
      isSlidingDown = true;
      pullRefreshLayout.setDispatchPullTouchAble(true);
    }
  }

  @Override public void onPullHoldTrigger() {

  }

  @Override public void onPullHoldUnTrigger() {

  }

  @Override public void onPullHolding() {
    if (pullRefreshLayout.getMoveDistance() != getHeight()) {
      pullRefreshLayout.setRefreshTriggerDistance(getHeight());
      pullRefreshLayout.setDispatchPullTouchAble(false);
      pullRefreshLayout.setTwinkEnable(false);
    }
  }

  @Override public void onPullFinish(boolean flag) {
    isSlidingDown = false;
    pullRefreshLayout.setTwinkEnable(true);
  }

  @Override public void onPullReset() {
    pullRefreshLayout.setDispatchPullTouchAble(true);
    pullRefreshLayout.setRefreshTriggerDistance(SLIDING_OFFSET);
  }

  @Override protected void onDetachedFromWindow() {
    super.onDetachedFromWindow();
    if (translateYAnimation.isRunning()) {
      translateYAnimation.cancel();
    }
  }
}