package com.yan.refreshloadlayouttest.widget;

import android.content.Context;
import android.view.View;

import com.yan.pullrefreshlayout.PullRefreshLayout;

/**
 * Created by yan on 2017/9/16.
 * AutoTriggerHeader(自动触发刷新) 只有开启回弹才能使用
 * <p>
 * 由于自动加载使用的很多，所以本库自带实现了这个功能，调用pullRefreshLayout.setAutoLoadingEnable(true)（不开启回弹也可以触发）
 */

public class AutoTriggerHeader extends View implements PullRefreshLayout.OnPullListener {
    private PullRefreshLayout pullRefreshLayout;

    public AutoTriggerHeader(Context context, PullRefreshLayout pullRefreshLayout) {
        super(context);
        this.pullRefreshLayout = pullRefreshLayout;
        pullRefreshLayout.setRefreshTriggerDistance(1);
        pullRefreshLayout.setHeaderFront(false);
        setVisibility(GONE);
    }

    @Override
    public void onPullChange(float percent) {
        if (percent > 0) {//如果你不想要回弹可以这样模拟，否则直接去掉(同时最好重新设置setRefreshAnimationDuring、setResetAnimationDuring 保证回弹时间相同)
            pullRefreshLayout.moveChildren(1);
        }
    }

    @Override
    public void onPullHoldTrigger() {

    }

    @Override
    public void onPullHoldUnTrigger() {

    }

    @Override
    public void onPullHolding() {
    }

    @Override
    public void onPullFinish(boolean flag) {

    }

    @Override
    public void onPullReset() {

    }
}