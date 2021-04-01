package com.yan.refreshloadlayouttest.widget;

import android.content.Context;

import com.yan.pullrefreshlayout.PullRefreshLayout;

/**
 * Created by yan on 2017/7/4.
 */

public class HeaderWithAutoLoading extends HeaderOrFooter {
    PullRefreshLayout refreshLayout;

    public HeaderWithAutoLoading(Context context, String animationName, PullRefreshLayout refreshLayout) {
        super(context, animationName);
        this.refreshLayout = refreshLayout;
    }

    @Override
    public void onPullReset() {
        super.onPullReset();
        PullRefreshLayout.OnPullListener onPullListener = refreshLayout.getFooterView();
        onPullListener.onPullReset();
    }
}