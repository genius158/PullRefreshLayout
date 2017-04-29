package com.yan.pullrefreshlayout;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.widget.FrameLayout;

/**
 * Created by yan on 2017/4/11.
 */
public abstract class AbsPullView extends FrameLayout implements PullRefreshLayout.OnPullListener {

    public AbsPullView(@NonNull Context context) {
        super(context);
        addView(LayoutInflater.from(getContext()).inflate(contentView(), this, false));
        initView();
    }

    protected void initView() {
    }

    protected abstract int contentView();

}
