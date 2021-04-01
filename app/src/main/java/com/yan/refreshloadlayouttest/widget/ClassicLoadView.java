package com.yan.refreshloadlayouttest.widget;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Color;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.wang.avi.AVLoadingIndicatorView;
import com.yan.pullrefreshlayout.PullRefreshLayout;
import com.yan.pullrefreshlayout.ShowGravity;
import com.yan.pullrefreshlayout.ViscousInterpolator;
import com.yan.refreshloadlayouttest.R;

/**
 * Created by yan on 2017/8/14.
 * <p>
 * 使用这个footer却确认调用以下两个方法
 * refreshLayout.setLoadTriggerDistance(120); 主动设置加载更多的触发距离
 * 设置 footerShowState（默认 为STATE_FOLLOW） 为 STATE_FOLLOW
 * <p>
 * 当然这里ClassicLoadView只是一种自动加载更多的实现思路，你也可以按照smartRefreshLayout保持默认执行，同时改变target的滑动位置来实现
 */

public class ClassicLoadView extends FrameLayout implements PullRefreshLayout.OnPullListener {
    private TextView tv;
    private AVLoadingIndicatorView loadingView;
    private PullRefreshLayout refreshLayout;
    private ObjectAnimator objectAnimator;

    public ClassicLoadView(@NonNull Context context, final PullRefreshLayout refreshLayout) {
        super(context);
        this.refreshLayout = refreshLayout;
        this.refreshLayout.setFooterFront(true);
        this.refreshLayout.setFooterShowGravity(ShowGravity.FOLLOW);
        // 设置 布局 为 match_parent
        setLayoutParams(new ViewGroup.LayoutParams(-1, -1));
        setBackgroundColor(Color.WHITE);
        initView();
    }

    // 动画初始化
    private void animationInit() {
        if (objectAnimator != null) return;

        objectAnimator = ObjectAnimator.ofFloat(this, "y", 0, 0);
        objectAnimator.setDuration(300);
        objectAnimator.setInterpolator(new ViscousInterpolator(8));

        objectAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                refreshLayout.loadMoreComplete();
                refreshLayout.setMoveWithFooter(true);
                refreshLayout.setDispatchTouchAble(true);
                refreshLayout.cancelTouchEvent();
                loadingView.smoothToHide();
            }
        });
    }

    // 自定义回复动画
    public void startBackAnimation() {
        // 记录refreshLayout移动距离
        final int moveDistance = refreshLayout.getMoveDistance();
        if (moveDistance >= 0) {// moveDistance大于等于0时不主动处理
            refreshLayout.loadMoreComplete();
            refreshLayout.setDispatchTouchAble(true);
            loadingView.smoothToHide();
            return;
        }

        // 阻止refreshLayout的事件分发
        refreshLayout.setDispatchTouchAble(false);
        postDelayed(new Runnable() {
            @Override
            public void run() {
                // 设置事件为ACTION_CANCEL
                refreshLayout.cancelTouchEvent();
                // 先设置footer不跟随移动
                refreshLayout.setMoveWithFooter(false);
                // 再设置内容移动到0的位置
                refreshLayout.moveChildren(0);
                refreshLayout.getTargetView().scrollBy(0, -moveDistance);

                // 调用自定义footer动画
                animationInit();
                objectAnimator.setFloatValues(getY(), getY() - moveDistance);
                objectAnimator.start();
            }
        }, 150);
    }

    public void loadFinish() {
        if (refreshLayout.isLoadMoreEnable()) {
            refreshLayout.setLoadMoreEnable(false);
            refreshLayout.setAutoLoadingEnable(false);
            refreshLayout.loadMoreComplete();
            tv.setText("no more data");
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        loadingView.smoothToHide();
        loadingView.clearAnimation();

        if (objectAnimator != null) {
            objectAnimator.cancel();
            objectAnimator = null;
        }
    }

    private void initView() {
        LayoutInflater.from(getContext()).inflate(R.layout.refresh_view, this, true);
        tv = (TextView) findViewById(R.id.title);
        tv.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext().getApplicationContext(), "you just touched me", Toast.LENGTH_SHORT).show();
            }
        });

        tv.setOnLongClickListener(new View.OnLongClickListener() {
            @Override public boolean onLongClick(View v) {
                Toast.makeText(v.getContext(), "setOnLongClickListener   -- " + v, Toast.LENGTH_LONG)
                    .show();
                return false;
            }
        });
        loadingView = (AVLoadingIndicatorView) findViewById(R.id.loading_view);

        loadingView.setIndicator("LineScaleIndicator");
        loadingView.setIndicatorColor(ContextCompat.getColor(getContext(), R.color.colorPrimary));
        refreshLayout.setAnimationMainInterpolator(new ViscousInterpolator());
        refreshLayout.setAnimationOverScrollInterpolator(new LinearInterpolator());
    }


    @Override
    public void onPullChange(float percent) {
        onPullHolding();

        // 判断是否处在 拖拽的状态
        if (refreshLayout.isDragDown() || refreshLayout.isDragUp() || !refreshLayout.isLoadMoreEnable()) {
            return;
        }

        if (!refreshLayout.isHoldingTrigger() && (percent < 0)) {
            refreshLayout.autoLoading();
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
        if (loadingView.getVisibility() != VISIBLE && refreshLayout.isLoadMoreEnable()) {
            loadingView.smoothToShow();
            tv.setText("loading...");
        }
    }

    @Override
    public void onPullFinish(boolean flag) {
        if (refreshLayout.isLoadMoreEnable()) {
            tv.setText("loading finish");
        }
        loadingView.smoothToHide();
    }

    @Override
    public void onPullReset() {
        /*
         * 内容没有铺满时继续执行自动加载
         */
        if (!refreshLayout.isTargetScrollDownAble() && !refreshLayout.isTargetScrollUpAble()) {
            postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (!refreshLayout.isRefreshing()) {
                        refreshLayout.autoLoading();
                    }
                }
            }, 250);
        }
    }
}
