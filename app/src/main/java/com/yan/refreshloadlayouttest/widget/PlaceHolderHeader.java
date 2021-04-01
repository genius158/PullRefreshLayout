package com.yan.refreshloadlayouttest.widget;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.ImageView;

import com.wang.avi.AVLoadingIndicatorView;
import com.yan.pullrefreshlayout.PullRefreshLayout;
import com.yan.refreshloadlayouttest.R;

/**
 * Created by Administrator on 2017/7/16 0016.
 */

public class PlaceHolderHeader extends NestedFrameLayout implements PullRefreshLayout.OnPullListener {
    public PlaceHolderHeader(@NonNull Context context) {
        super(context);
        LayoutInflater.from(context).inflate(R.layout.refresh_view_big, this, true);
        ((ImageView) findViewById(R.id.iv_bg)).setImageDrawable(ContextCompat
                .getDrawable(getContext().getApplicationContext(),R.drawable.loading_bg));
    }

    @Override
    public void onPullChange(float percent) {
        if (percent > 1.2) {
            findViewById(R.id.iv_bg).setScaleY(1 + (percent - 1.2f) * 0.2f);
        } else {
            findViewById(R.id.iv_bg).setScaleY(1f);
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
        ((AVLoadingIndicatorView) findViewById(R.id.loading_view)).smoothToShow();
    }

    @Override
    public void onPullFinish(boolean flag) {
        Log.e("onPullFinish", "onPullFinish: "  );
        ((AVLoadingIndicatorView) findViewById(R.id.loading_view)).smoothToHide();

    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        AVLoadingIndicatorView avLoadingIndicatorView = ((AVLoadingIndicatorView) findViewById(R.id.loading_view));
        if (avLoadingIndicatorView.getVisibility() == VISIBLE) {
            avLoadingIndicatorView.smoothToHide();
        }
    }

    @Override
    public void onPullReset() {
        Log.e("onPullReset", "onPullFinish: "  );

        AVLoadingIndicatorView avLoadingIndicatorView = ((AVLoadingIndicatorView) findViewById(R.id.loading_view));
        if (avLoadingIndicatorView.getVisibility() == VISIBLE) {
            avLoadingIndicatorView.smoothToHide();
        }
    }
}
