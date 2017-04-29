package com.yan.pullrefreshlayout;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.os.Build;
import android.support.v4.view.NestedScrollingParent;
import android.support.v4.view.NestedScrollingParentHelper;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.FrameLayout;

/**
 * Created by yan on 2017/4/11.
 */
public class PullRefreshLayout extends FrameLayout implements NestedScrollingParent {

    private NestedScrollingParentHelper parentHelper;
    private OnRefreshListener onRefreshListener;

    private PullContainerView headerView;
    private PullContainerView footerView;

    private float moveDistance = 0;
    private View targetView;

    private static final int ACTION_PULL_REFRESH = 0;
    private static final int ACTION_LOAD_MORE = 1;

    private static final int PULL_VIEW_HEIGHT = 60;
    private static final int PULL_FLOW_HEIGHT = PULL_VIEW_HEIGHT * 2;

    // Enable PullRefresh and LoadMore
    private boolean pullRefreshEnable = true;
    private boolean pullLoadEnable = true;

    // Is Refreshing
    volatile private boolean refreshing = false;

    // pullStateControl
    private boolean pullStateControl = true;

    // RefreshView Height
    private float pullViewHeight = 0;

    // RefreshView Over Flow Height
    private float pullFlowHeight = 0;

    // Drag Action
    private int currentAction = -1;
    private boolean isConfirm = false;

    public PullRefreshLayout(Context context) {
        super(context);
        initAttrs(context);
    }

    public PullRefreshLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        initAttrs(context);
    }

    public PullRefreshLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAttrs(context);
    }

    private void initAttrs(Context context) {
        if (getChildCount() > 1) {
            throw new RuntimeException("PullRefreshLayout should not have more than one child");
        }

        parentHelper = new NestedScrollingParentHelper(this);

        headerView = new PullContainerView(getContext());
        footerView = new PullContainerView(getContext());

        if (pullViewHeight == 0) {
            pullViewHeight = dipToPx(context, PULL_VIEW_HEIGHT);
        }
        if (pullFlowHeight == 0) {
            pullFlowHeight = dipToPx(context, PULL_FLOW_HEIGHT);
        }
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        targetView = getChildAt(0);
        addView(headerView, new FrameLayout.LayoutParams(ViewGroup
                .LayoutParams.MATCH_PARENT, (int) pullViewHeight));
        addView(footerView, new FrameLayout.LayoutParams(ViewGroup
                .LayoutParams.MATCH_PARENT, (int) pullViewHeight));
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        headerView.layout(left, (int) (-pullViewHeight), right, 0);
        footerView.layout(left, bottom - top, right, (int) (bottom - top + pullViewHeight));
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if ((!pullRefreshEnable && !pullLoadEnable)) {
            return false;
        }

        return super.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean onStartNestedScroll(View child, View target, int nestedScrollAxes) {
        if (refreshing) {
            return false;
        }
        return true;
    }

    @Override
    public void onNestedScrollAccepted(View child, View target, int axes) {
        parentHelper.onNestedScrollAccepted(child, target, axes);
    }

    /**
     * Callback on TouchEvent.ACTION_CANCEL or TouchEvent.ACTION_UP
     * handler : refresh or loading
     *
     * @param child : child view of PullRefreshLayout,RecyclerView or Scroller
     */
    @Override
    public void onStopNestedScroll(View child) {
        parentHelper.onStopNestedScroll(child);
        handlerAction();
    }

    /**
     * With child view to processing move events
     *
     * @param target   the child view
     * @param dx       move x
     * @param dy       move y
     * @param consumed parent consumed move distance
     */
    @Override
    public void onNestedPreScroll(View target, int dx, int dy, int[] consumed) {

        if ((!pullRefreshEnable && !pullLoadEnable)) {
            return;
        }

        // Prevent Layout shake
        if (Math.abs(dy) > 200) {
            return;
        }

        if (!isConfirm) {
            if (dy < 0 && !canChildScrollUp()) {
                currentAction = ACTION_PULL_REFRESH;
                isConfirm = true;
            } else if (dy > 0 && !canChildScrollDown()) {
                currentAction = ACTION_LOAD_MORE;
                isConfirm = true;
            }
        }

        if (movePullContainerView(-dy)) {
            consumed[1] += dy;
        }
    }

    @Override
    public void onNestedScroll(View target, int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed) {

    }

    @Override
    public int getNestedScrollAxes() {
        return parentHelper.getNestedScrollAxes();
    }

    @Override
    public boolean onNestedPreFling(View target, float velocityX, float velocityY) {
        return false;
    }

    @Override
    public boolean onNestedFling(View target, float velocityX, float velocityY, boolean consumed) {
        return false;
    }

    /**
     * Adjust the refresh or loading view according to the size of the gesture
     *
     * @param distanceY move distance of Y
     */
    private boolean movePullContainerView(float distanceY) {

        if (refreshing) {
            return false;
        }

        if (!canChildScrollUp() && pullRefreshEnable && currentAction == ACTION_PULL_REFRESH) {
            // Pull Refresh
            moveDistance += distanceY;
            if (moveDistance < 0) {
                moveDistance = 0;
            }
            if (moveDistance > pullFlowHeight) {
                moveDistance = (int) pullFlowHeight;
            }

            if (moveDistance == 0) {
                isConfirm = false;
                currentAction = -1;
            }
            if (moveDistance >= pullViewHeight) {
                if (pullStateControl) {
                    pullStateControl = false;
                    headerView.onPullHoldTrigger();
                }
            } else {
                if (!pullStateControl) {
                    pullStateControl = true;
                    headerView.onPullHoldUnTrigger();
                }
            }
            headerView.onPullChange(moveDistance / pullViewHeight);
            moveView(moveDistance);
            return true;
        } else if (!canChildScrollDown() && pullLoadEnable && currentAction == ACTION_LOAD_MORE) {
            // Load more
            moveDistance -= distanceY;
            if (moveDistance < 0) {
                moveDistance = 0;
            }
            if (moveDistance > pullFlowHeight) {
                moveDistance = (int) pullFlowHeight;
            }
            if (moveDistance == 0) {
                isConfirm = false;
                currentAction = -1;
            }
            if (moveDistance >= pullViewHeight) {
                if (pullStateControl) {
                    pullStateControl = false;
                    footerView.onPullHoldTrigger();
                }
            } else {
                if (!pullStateControl) {
                    pullStateControl = true;
                    footerView.onPullHoldUnTrigger();
                }
            }
            footerView.onPullChange(moveDistance / pullViewHeight);
            moveView(-moveDistance);
            return true;
        }
        return false;
    }

    /**
     * Move children
     */
    private void moveView(float h) {
        headerView.setTranslationY(h);
        footerView.setTranslationY(h);
        targetView.setTranslationY(h);
    }

    /**
     * Decide on the action refresh or loadMore
     */
    private void handlerAction() {

        if (refreshing) {
            return;
        }
        isConfirm = false;

        if (pullRefreshEnable && currentAction == ACTION_PULL_REFRESH) {
            if (moveDistance >= pullViewHeight) {
                startRefresh((int) moveDistance);
                headerView.onPullHolding();
            } else if (moveDistance > 0) {
                resetHeaderView((int) moveDistance);
            } else {
                resetRefreshState();
            }
        }

        if (pullLoadEnable && currentAction == ACTION_LOAD_MORE) {
            if (moveDistance >= pullViewHeight) {
                startLoadMore((int) moveDistance);
                footerView.onPullHolding();
            } else if (moveDistance > 0) {
                resetFootView((int) moveDistance);
            } else {
                resetLoadMoreState();
            }
        }
    }

    /**
     * Start Refresh
     *
     * @param headerViewHeight
     */
    private void startRefresh(int headerViewHeight) {
        refreshing = true;
        ValueAnimator animator = ValueAnimator.ofFloat(headerViewHeight, pullViewHeight);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                moveDistance = (int) ((Float) animation.getAnimatedValue()).floatValue();
                moveView(moveDistance);
            }
        });
        animator.addListener(new RefreshAnimatorListener() {
            @Override
            public void onAnimationEnd(Animator animation) {
                if (onRefreshListener != null) {
                    onRefreshListener.onRefresh();
                }
            }
        });
        animator.setDuration(300);
        animator.start();
    }

    /**
     * Reset refresh state
     *
     * @param headerViewHeight
     */
    private void resetHeaderView(int headerViewHeight) {
        ValueAnimator animator = ValueAnimator.ofFloat(headerViewHeight, 0);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                moveDistance = (int) ((Float) animation.getAnimatedValue()).floatValue();
                moveView(moveDistance);
            }
        });
        animator.addListener(new RefreshAnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                headerView.onPullFinish();
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                resetRefreshState();
            }
        });
        animator.setDuration(300);
        animator.start();
    }

    private void resetRefreshState() {
        headerView.onPullReset();
        refreshing = false;
        moveDistance = 0;
        isConfirm = false;
        pullStateControl = true;
        currentAction = -1;
    }

    /**
     * Start loadMore
     *
     * @param loadMoreViewHeight
     */
    private void startLoadMore(int loadMoreViewHeight) {
        refreshing = true;
        ValueAnimator animator = ValueAnimator.ofFloat(loadMoreViewHeight, pullViewHeight);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                moveDistance = (int) ((Float) animation.getAnimatedValue()).floatValue();
                moveView(-moveDistance);
            }
        });
        animator.addListener(new RefreshAnimatorListener() {
            @Override
            public void onAnimationEnd(Animator animation) {
                if (onRefreshListener != null) {
                    onRefreshListener.onLoading();
                }
            }
        });
        animator.setDuration(300);
        animator.start();
    }

    /**
     * Reset loadMore state
     *
     * @param loadMoreViewHeight
     */
    private void resetFootView(int loadMoreViewHeight) {
        ValueAnimator animator = ValueAnimator.ofFloat(loadMoreViewHeight, 0);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                moveDistance = (int) ((Float) animation.getAnimatedValue()).floatValue();
                moveView(-moveDistance);
            }
        });
        animator.addListener(new RefreshAnimatorListener() {
            @Override
            public void onAnimationEnd(Animator animation) {
                resetLoadMoreState();
            }

            @Override
            public void onAnimationStart(Animator animation) {
                footerView.onPullFinish();
            }
        });
        animator.setDuration(300);
        animator.start();
    }

    private void resetLoadMoreState() {
        footerView.onPullReset();
        refreshing = false;
        moveDistance = 0;
        isConfirm = false;
        pullStateControl = true;
        currentAction = -1;
    }

    /**
     * Whether child view can scroll up
     *
     * @return
     */
    public boolean canChildScrollUp() {
        if (targetView == null) {
            return false;
        }
        if (Build.VERSION.SDK_INT < 14) {
            if (targetView instanceof AbsListView) {
                final AbsListView absListView = (AbsListView) targetView;
                return absListView.getChildCount() > 0
                        && (absListView.getFirstVisiblePosition() > 0 || absListView.getChildAt(0)
                        .getTop() < absListView.getPaddingTop());
            } else {
                return ViewCompat.canScrollVertically(targetView, -1) || targetView.getScrollY() > 0;
            }
        } else {
            return ViewCompat.canScrollVertically(targetView, -1);
        }
    }

    /**
     * Whether child view can scroll down
     *
     * @return
     */
    public boolean canChildScrollDown() {
        if (targetView == null) {
            return false;
        }
        if (Build.VERSION.SDK_INT < 14) {
            if (targetView instanceof AbsListView) {
                final AbsListView absListView = (AbsListView) targetView;
                if (absListView.getChildCount() > 0) {
                    int lastChildBottom = absListView.getChildAt(absListView.getChildCount() - 1)
                            .getBottom();
                    return absListView.getLastVisiblePosition() == absListView.getAdapter().getCount() - 1
                            && lastChildBottom <= absListView.getMeasuredHeight();
                } else {
                    return false;
                }

            } else {
                return ViewCompat.canScrollVertically(targetView, 1) || targetView.getScrollY() > 0;
            }
        } else {
            return ViewCompat.canScrollVertically(targetView, 1);
        }
    }

    public float dipToPx(Context context, float value) {
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, value, metrics);
    }

    public void setOnRefreshListener(OnRefreshListener onRefreshListener) {
        this.onRefreshListener = onRefreshListener;
    }

    /**
     * Callback on refresh finish
     */
    public void refreshComplete() {
        if (currentAction == ACTION_PULL_REFRESH) {
            resetHeaderView((int) moveDistance);
        }
    }

    /**
     * Callback on loadMore finish
     */
    public void loadMoreComplete() {
        if (currentAction == ACTION_LOAD_MORE) {
            resetFootView((int) moveDistance);
        }
    }

    public boolean isLoadMoreEnable() {
        return pullLoadEnable;
    }

    public void setLoadMoreEnable(boolean mPullLoadEnable) {
        this.pullLoadEnable = mPullLoadEnable;
    }

    public boolean isRefreshEnable() {
        return pullRefreshEnable;
    }

    public void setRefreshEnable(boolean mPullRefreshEnable) {
        this.pullRefreshEnable = mPullRefreshEnable;
    }

    public boolean isRefreshing() {
        return refreshing;
    }

    public void setHeaderView(View view) {
        headerView.setPullView(view, Gravity.CENTER);
    }

    public void setHeaderView(View view, int gravity) {
        headerView.setPullView(view, gravity);
    }

    public void setFooterView(View view) {
        footerView.setPullView(view, Gravity.TOP);
    }

    public void setFooterView(View view, int gravity) {
        footerView.setPullView(view, gravity);
    }

    public void setPullViewHeight(float pullViewHeight) {
        this.pullViewHeight = pullViewHeight;
    }

    public void setPullFlowHeight(float pullFlowHeight) {
        this.pullFlowHeight = pullFlowHeight;
    }

    public static interface OnPullListener {
        void onPullChange(float percent);

        void onPullReset();

        void onPullHoldTrigger();

        void onPullHoldUnTrigger();

        void onPullHolding();

        void onPullFinish();
    }

    public static abstract class OnRefreshListener {
        public abstract void onRefresh();

        public void onLoading() {
        }
    }

    private static class RefreshAnimatorListener implements Animator.AnimatorListener {

        @Override
        public void onAnimationStart(Animator animation) {
        }

        @Override
        public void onAnimationEnd(Animator animation) {
        }

        @Override
        public void onAnimationCancel(Animator animation) {
        }

        @Override
        public void onAnimationRepeat(Animator animation) {
        }
    }

}
