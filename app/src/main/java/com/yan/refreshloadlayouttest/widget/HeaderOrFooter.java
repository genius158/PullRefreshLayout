package com.yan.refreshloadlayouttest.widget;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.wang.avi.AVLoadingIndicatorView;
import com.yan.refreshloadlayouttest.R;

/**
 * Created by yan on 2017/7/4.
 */

public class HeaderOrFooter extends PullRefreshView {
    protected TextView tv;
    private AVLoadingIndicatorView loadingView;
    protected FrameLayout rlContainer;

    private boolean isStateFinish;
    private boolean isHolding;

    public HeaderOrFooter(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(getContext()).inflate(contentView(), this, true);
        initView();
        loadingView.setIndicator("LineScaleIndicator");
        loadingView.setIndicatorColor(ContextCompat.getColor(context, R.color.colorPrimary));
        tv.setTextColor(ContextCompat.getColor(context, R.color.colorPrimary));
    }

    public HeaderOrFooter(Context context) {
        this(context, "LineScaleIndicator", ContextCompat.getColor(context, R.color.colorPrimary), false);
    }

    public HeaderOrFooter(Context context, String animationName) {
        this(context, animationName, ContextCompat.getColor(context, R.color.colorPrimary), false);
    }

    public HeaderOrFooter(Context context, String animationName, int color) {
        this(context, animationName, color, true);
    }

    public HeaderOrFooter(Context context, String animationName, int color, boolean withBg) {
        super(context);
        loadingView.setIndicator(animationName);
        loadingView.setIndicatorColor(color);
        tv.setTextColor(color);
        if (withBg) {
            setBackgroundColor(ContextCompat.getColor(getContext(), R.color.colorPrimary));
        }
    }

    public void setTv(String title) {
       if (tv!=null) {
           tv.setText(title);
       }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        loadingView.smoothToHide();
        loadingView.clearAnimation();
    }

    @Override
    protected int contentView() {
        return R.layout.refresh_view;
    }

    @Override
    protected void initView() {
        rlContainer = (FrameLayout) findViewById(R.id.rl_container);
        tv = (TextView) findViewById(R.id.title);
        loadingView = (AVLoadingIndicatorView) findViewById(R.id.loading_view);
    }

    @Override
    public void onPullChange(float percent) {
        super.onPullChange(percent);
        if (isStateFinish || isHolding) return;
        percent = Math.abs(percent);
        if (percent > 0.2 && percent < 1) {
            if (loadingView.getVisibility() != VISIBLE) {
                loadingView.smoothToShow();
            }
            if (percent < 1) {
                loadingView.setScaleX(percent);
                loadingView.setScaleY(percent);
            }
        } else if (percent <= 0.2 && loadingView.getVisibility() == VISIBLE) {
            loadingView.smoothToHide();
        } else if (loadingView.getScaleX() != 1) {
            loadingView.setScaleX(1f);
            loadingView.setScaleY(1f);
        }
    }

    @Override
    public void onPullHoldTrigger() {
        super.onPullHoldTrigger();
        tv.setText("release loading");
    }

    @Override
    public void onPullHoldUnTrigger() {
        super.onPullHoldUnTrigger();
        tv.setText("drag");
    }

    @Override
    public void onPullHolding() {
        super.onPullHolding();
        isHolding = true;
        tv.setText("loading...");
    }

    @Override
    public void onPullFinish(boolean flag) {
        super.onPullFinish(flag);
        tv.setText("loading finish");
        isStateFinish = true;
        loadingView.smoothToHide();
    }

    @Override
    public void onPullReset() {
        super.onPullReset();
        tv.setText("drag");
        isStateFinish = false;
        isHolding = false;

    }
}