package com.yan.refreshloadlayouttest.widget;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;

import com.yan.pullrefreshlayout.PullRefreshLayout;

public class MaterialHeader extends NestedFrameLayout implements PullRefreshLayout.OnPullListener {

    private MaterialProgressDrawable mDrawable;
    private float mScale = 1f;
    private float multiple;
    private PullRefreshLayout refreshLayout;

    private final ValueAnimator scaleAnimation = ValueAnimator.ofFloat(1, 0);

    private final ValueAnimator.AnimatorUpdateListener updateListener = new ValueAnimator.AnimatorUpdateListener() {
        @Override
        public void onAnimationUpdate(ValueAnimator animation) {
            mScale = (float) animation.getAnimatedValue();
            mDrawable.setAlpha((int) (255 * mScale));
            invalidate();
        }
    };

    public MaterialHeader(Context context, PullRefreshLayout refreshLayout, float multiple) {
        super(context);
        this.multiple = multiple;
        this.refreshLayout = refreshLayout;
        initView();
    }

    private void initView() {
        mDrawable = new MaterialProgressDrawable(getContext(), this);
        mDrawable.setBackgroundColor(Color.WHITE);
        mDrawable.setCallback(this);

        scaleAnimation.setDuration(180);
        scaleAnimation.addUpdateListener(updateListener);
    }

    @Override
    public void invalidateDrawable(Drawable dr) {
        if (dr == mDrawable) {
            invalidate();
        } else {
            super.invalidateDrawable(dr);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int height = mDrawable.getIntrinsicHeight() + getPaddingTop() + getPaddingBottom();
        heightMeasureSpec = MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        final int size = mDrawable.getIntrinsicHeight();
        mDrawable.setBounds(0, 0, size, size);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        final int saveCount = canvas.save();
        Rect rect = mDrawable.getBounds();
        int l = getPaddingLeft() + (getMeasuredWidth() - mDrawable.getIntrinsicWidth()) / 2;
        canvas.translate(l, getPaddingTop());
        canvas.scale(mScale, mScale, rect.exactCenterX(), rect.exactCenterY());
        mDrawable.draw(canvas);
        canvas.restoreToCount(saveCount);
    }

    @Override
    public void onPullChange(float percent) {
        if (refreshLayout.isHoldingTrigger()) return;
        percent = Math.abs(percent / multiple);

        mDrawable.setAlpha((int) (percent * 255));
        mDrawable.showArrow(true);
        float strokeStart = ((percent) * .8f);
        mDrawable.setStartEndTrim(0f, Math.min(0.8f, strokeStart));
        mDrawable.setArrowScale(Math.min(1f, percent));

        // magic
        float rotation = (-0.25f + .4f * percent + percent * 2) * .5f;
        mDrawable.setProgressRotation(rotation);
        invalidate();
    }

    @Override
    public void onPullHoldTrigger() {

    }

    @Override
    public void onPullHoldUnTrigger() {

    }

    @Override
    public void onPullHolding() {
        mDrawable.setAlpha(255);
        mDrawable.start();
    }

    @Override
    public void onPullFinish(boolean flag) {
        mDrawable.stop();
        refreshLayout.setMoveWithHeader(false);
        refreshLayout.setMoveWithFooter(false);
        scaleAnimation.start();
    }

    @Override
    public void onPullReset() {
        mDrawable.stop();
        mScale = 1f;
        scaleAnimation.cancel();

        refreshLayout.setMoveWithHeader(true);
        refreshLayout.setMoveWithFooter(true);

        // 使得回弹复原
        refreshLayout.cancelAllAnimation();
        refreshLayout.moveChildren(0);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        mDrawable.stop();
        clearAnimation();
        scaleAnimation.cancel();
    }

}