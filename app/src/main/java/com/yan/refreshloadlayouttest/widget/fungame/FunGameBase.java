package com.yan.refreshloadlayouttest.widget.fungame;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import androidx.core.view.ViewCompat;
import android.view.View;

import com.yan.pullrefreshlayout.PullRefreshLayout;
import com.yan.pullrefreshlayout.ShowGravity;
import com.yan.refreshloadlayouttest.widget.NestedFrameLayout;


/**
 * 游戏 header
 * Created by SCWANG on 2017/6/17.
 * from https://github.com/scwang90/SmartRefreshLayout
 */

public class FunGameBase extends NestedFrameLayout implements PullRefreshLayout.OnPullListener {

    //<editor-fold desc="Field">
    protected int mHeaderHeight;
    protected int mScreenHeightPixels;
    protected boolean mManualOperation;
    protected PullRefreshLayout refreshLayout;
    private boolean isGameViewReady;
    //</editor-fold>

    //<editor-fold desc="View">
    public FunGameBase(Context context, PullRefreshLayout refreshLayout) {
        super(context);
        this.refreshLayout = refreshLayout;
        initView(context);
    }

    private void initView(Context context) {
        mScreenHeightPixels = context.getResources().getDisplayMetrics().heightPixels;
        mHeaderHeight = (int) (mScreenHeightPixels * 0.16f);
        refreshLayout.setRefreshTriggerDistance(mHeaderHeight);
        refreshLayout.setHeaderShowGravity(ShowGravity.FOLLOW);
        refreshLayout.setHeaderFront(true);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawColor(Color.parseColor("#fefefe"));
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
    }

    protected void onManualOperationMove(float percent) {

    }

    @Override
    public void onPullChange(float percent) {
        if (mManualOperation && !refreshLayout.isDragUp() && !refreshLayout.isDragDown() && refreshLayout.isHoldingFinishTrigger() && percent <= 1) {
            onPullFinish(true);
            return;
        }

        if (mManualOperation) {
            if (!isGameViewReady && percent == 1) {
                refreshLayout.setDispatchTouchAble(true);
                refreshLayout.cancelTouchEvent();
                refreshLayout.setMoveWithHeader(false);
                isGameViewReady = true;
            }
            setPRLDispatchChildrenEventAble(refreshLayout.isTargetScrollUpAble());

            if (isGameViewReady && refreshLayout.getMoveDistance() < refreshLayout.getRefreshTriggerDistance() / 3) {
                refreshLayout.moveChildren(refreshLayout.getRefreshTriggerDistance() / 3);//保证onPullChange() 会持续触发
            }
            onManualOperationMove(1 + (percent - 1) * 0.8F);
        }

        if (refreshLayout.isHoldingFinishTrigger() && refreshLayout.getMoveDistance() == 0) {
            setPRLDispatchChildrenEventAble(true);
        }
    }

    private void setPRLDispatchChildrenEventAble(boolean isDispatch) {
        View target = refreshLayout.getTargetView();
        boolean isTargetNestedAble = ViewCompat.isNestedScrollingEnabled(target);
        if (!isTargetNestedAble) {
            refreshLayout.setDispatchChildrenEventAble(isDispatch);
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
        mManualOperation = true;
        refreshLayout.setDragDampingRatio(1f);
        refreshLayout.cancelTouchEvent();
        refreshLayout.setDispatchTouchAble(false);
    }

    @Override
    public void onPullFinish(boolean flag) {
        if (refreshLayout.isDragDown() || refreshLayout.isDragUp()) {
            refreshLayout.cancelAllAnimation();
        } else {
            mManualOperation = false;
            isGameViewReady = false;
            refreshLayout.setDragDampingRatio(0.6F);
            refreshLayout.setMoveWithHeader(true);
            refreshLayout.setDispatchTouchAble(true);
        }
    }

    @Override
    public void onPullReset() {
    }
}
