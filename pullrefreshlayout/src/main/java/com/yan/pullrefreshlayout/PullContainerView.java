package com.yan.pullrefreshlayout;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

/**
 * Created by yan on 2017/4/11.
 */
class PullContainerView extends FrameLayout implements PullRefreshLayout.OnPullListener {
    private static final String TAG = "PullView";
    private View pullView;

    public PullContainerView(Context context) {
        super(context);
    }

    public PullContainerView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public PullContainerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void onPullChange(float percent) {
        Log.e(TAG, "onPullChange: " + percent);
    }

    @Override
    public void onPullReset() {
        if (pullView != null && pullView instanceof PullRefreshLayout.OnPullListener) {
            ((PullRefreshLayout.OnPullListener) pullView).onPullReset();
        }
    }

    @Override
    public void onPullHoldTrigger() {
        Log.e(TAG, "onPullHoldTrigger: ");
        if (pullView != null && pullView instanceof PullRefreshLayout.OnPullListener) {
            ((PullRefreshLayout.OnPullListener) pullView).onPullHoldTrigger();
        }
    }

    @Override
    public void onPullHoldUnTrigger() {
        Log.e(TAG, "onPullHoldUnTrigger: ");
        if (pullView != null && pullView instanceof PullRefreshLayout.OnPullListener) {
            ((PullRefreshLayout.OnPullListener) pullView).onPullHoldUnTrigger();
        }
    }

    @Override
    public void onPullHolding() {
        Log.e(TAG, "onPullHolding: ");
        if (pullView != null && pullView instanceof PullRefreshLayout.OnPullListener) {
            ((PullRefreshLayout.OnPullListener) pullView).onPullHolding();
        }
    }

    @Override
    public void onPullFinish() {
        Log.e(TAG, "onPullFinish: ");
        if (pullView != null && pullView instanceof PullRefreshLayout.OnPullListener) {
            ((PullRefreshLayout.OnPullListener) pullView).onPullFinish();
        }
    }

    public void setPullView(View pullView, int gravity) {
        this.pullView = pullView;
        addView(pullView, getDefaultLayoutParams(gravity));
    }

    private FrameLayout.LayoutParams getDefaultLayoutParams(int gravity) {
        FrameLayout.LayoutParams layoutParams =
                new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        layoutParams.gravity = gravity;
        return layoutParams;
    }
}
