package com.yan.refreshloadlayouttest.widget;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.content.Context;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.core.widget.NestedScrollView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;
import com.wang.avi.AVLoadingIndicatorView;
import com.yan.pullrefreshlayout.PullRefreshLayout;
import com.yan.pullrefreshlayout.ShowGravity;
import com.yan.pullrefreshlayout.ViscousInterpolator;
import com.yan.refreshloadlayouttest.R;

/**
 * Created by yan on 2017/10/11.
 * 自动加载（加载完成footer不隐藏，可跟随滑动）
 */

public class ClassicHoldLoadView extends FrameLayout implements PullRefreshLayout.OnPullListener {
    private TextView tv;
    private AVLoadingIndicatorView loadingView;
    private PullRefreshLayout refreshLayout;
    private ObjectAnimator objectAnimator;

    public ClassicHoldLoadView(@NonNull Context context, final PullRefreshLayout refreshLayout) {
        super(context);
        this.refreshLayout = refreshLayout;
        setLayoutParams(new ViewGroup.LayoutParams(-1, -1));
        this.refreshLayout.setFooterFront(true);
        this.refreshLayout.setLoadMoreEnable(true);
        this.refreshLayout.setAutoLoadingEnable(true);
        this.refreshLayout.setFooterShowGravity(ShowGravity.PLACEHOLDER);
        setBackgroundColor(ContextCompat.getColor(getContext(), R.color.colorWhite));
        initView();
        scrollInit();
    }

    private void scrollInit() {
        post(new Runnable() {
            @Override
            public void run() {
                final View target = refreshLayout.getTargetView();
                refreshLayout.setLoadTriggerDistance(tv.getHeight());
                target.setOverScrollMode(OVER_SCROLL_NEVER);
                ((ViewGroup) target).setClipToPadding(false);

                if (target instanceof NestedScrollView) {
                    ((NestedScrollView) target).setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
                        @Override
                        public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                            if (refreshLayout.isRefreshing()) {
                                return;
                            }
                            setTargetTranslationY();
                        }
                    });
                } else if (target instanceof RecyclerView) {
                    ((RecyclerView) target).addOnScrollListener(new RecyclerView.OnScrollListener() {
                        @Override
                        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                            if (refreshLayout.isRefreshing()) {
                                return;
                            }
                            setTargetTranslationY();
                        }
                    });
                }

                setTargetTranslationY();
            }
        });
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

    // 动画初始化
    private void animationInit() {
        if (objectAnimator != null) return;

        objectAnimator = ObjectAnimator.ofFloat(this, "translationY", 0, 0);
        objectAnimator.setDuration(300);
        objectAnimator.setInterpolator(new ViscousInterpolator(8));

        objectAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                refreshLayout.loadMoreComplete();
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
                // 再设置内容移动到0的位置
                refreshLayout.moveChildren(0);
                refreshLayout.getTargetView().scrollBy(0, -moveDistance);

                // 调用自定义footer动画
                animationInit();

                objectAnimator.setFloatValues(getHeight() + moveDistance, getHeight());
                objectAnimator.start();
            }
        }, 150);

    }

    public void loadFinish(boolean isHold) {
        if (refreshLayout.isLoadMoreEnable()) {
            refreshLayout.setLoadMoreEnable(false);
            refreshLayout.setAutoLoadingEnable(false);
            tv.setText("no more data");
            loadingView.setVisibility(GONE);
            if (isHold) {
                ViewGroup target = refreshLayout.getTargetView();
                target.setPadding(0, 0, 0, tv.getHeight());//方便起见，这里就不考虑设置target已设置的情况

                if (refreshLayout.getMoveDistance() < 0) {
                    int offsetY = Math.min(tv.getHeight(), -refreshLayout.getMoveDistance());
                    target.scrollBy(0, offsetY);
                    refreshLayout.moveChildren(refreshLayout.getMoveDistance() + offsetY);
                    setTargetTranslationY();
                }
            }
            refreshLayout.loadMoreComplete();
        }
    }

    public void holdReset() {
        ViewGroup target = refreshLayout.getTargetView();
        target.setPadding(0, 0, 0, 0);
        refreshLayout.setLoadMoreEnable(true);
        refreshLayout.setAutoLoadingEnable(true);
    }

    private void setTargetTranslationY() {
        View target = refreshLayout.getTargetView();
        if (target instanceof NestedScrollView) {
            setTranslationY(Math.max(getHeight(), ((NestedScrollView) target).getChildAt(0).getHeight()) - target.getScrollY() + refreshLayout.getMoveDistance());
        } else if (refreshLayout.getTargetView() instanceof RecyclerView) {
            RecyclerView rv = (RecyclerView) target;
            RecyclerView.LayoutManager layoutManager = rv.getLayoutManager();
            RecyclerView.ViewHolder viewHolder = rv.findViewHolderForAdapterPosition(layoutManager.getItemCount() - 1);
            float offset = 0;
            if (viewHolder != null) {
                offset = refreshLayout.isTargetScrollDownAble() || refreshLayout.isTargetScrollUpAble() ?
                        viewHolder.itemView.getBottom() - refreshLayout.getTargetView().getHeight() : 0;
            }
            setTranslationY(getHeight() + refreshLayout.getMoveDistance() + offset);
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

    @Override
    public void onPullChange(float percent) {
        Log.e("onFooterPullChange", "setTargetTranslationY: " + percent);
        onPullHolding();

        setTargetTranslationY();

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
        postDelayed(new Runnable() {
            @Override
            public void run() {
                if (!refreshLayout.isTargetScrollDownAble() && !refreshLayout.isTargetScrollUpAble()) {
                    if (!refreshLayout.isRefreshing()) {
                        refreshLayout.autoLoading();
                    }
                }
            }
        }, 250);
    }

}
