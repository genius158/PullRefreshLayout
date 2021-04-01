package com.yan.refreshloadlayouttest.widget;

import android.animation.ValueAnimator;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.Drawable;
import androidx.annotation.NonNull;
import android.view.animation.LinearInterpolator;

/**
 * 旋转动画
 * Created by SCWANG on 2017/6/16.
 * from https://github.com/scwang90/SmartRefreshLayout
 */

public class ProgressDrawable extends Drawable implements Animatable {

    private int mProgressDegree = 0;
    private ValueAnimator mValueAnimator;
    private Path mPath = new Path();
    private Paint mPaint = new Paint();

    public ProgressDrawable() {
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setAntiAlias(true);
        mPaint.setColor(0xffaaaaaa);
        setupAnimators();
    }

    public void setColor(int color) {
        mPaint.setColor(color);
    }

    //<editor-fold desc="Drawable">
    @Override
    public void draw(@NonNull Canvas canvas) {
        Rect bounds = getBounds();
        int width = bounds.width();
        int height = bounds.height();
        canvas.save();
        canvas.rotate(mProgressDegree, (width) / 2, (height) / 2);
        final int r = Math.max(1, width / 20);
        for (int i = 0; i < 12; i++) {
            mPath.reset();
            mPath.addCircle(width - r, height / 2, r, Path.Direction.CW);
            mPath.addRect(width - 5 * r, height / 2 - r, width - r, height / 2 + r, Path.Direction.CW);
            mPath.addCircle(width - 5 * r, height / 2, r, Path.Direction.CW);
            mPaint.setAlpha((i+5) * 0x11);
            canvas.rotate(30, (width) / 2, (height) / 2);
            canvas.drawPath(mPath, mPaint);
        }
        canvas.restore();
    }

    @Override
    public void setAlpha(int alpha) {
        mPaint.setAlpha(alpha);
    }

    @Override
    public void setColorFilter(ColorFilter cf) {
        mPaint.setColorFilter(cf);
    }

    @Override
    public int getOpacity() {
        return PixelFormat.TRANSLUCENT;
    }
    //</editor-fold>

    private void setupAnimators() {
        mValueAnimator = ValueAnimator.ofInt(30, 3600);
        mValueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int value = (int) animation.getAnimatedValue();
                mProgressDegree = 30 * (value / 30);
                invalidateSelf();
            }
        });
        mValueAnimator.setDuration(10000);
        mValueAnimator.setInterpolator(new LinearInterpolator());
        mValueAnimator.setRepeatCount(ValueAnimator.INFINITE);
        mValueAnimator.setRepeatMode(ValueAnimator.RESTART);
    }

    @Override
    public void start() {
        if (!mValueAnimator.isRunning()) {
            mValueAnimator.start();
        }
    }

    @Override
    public void stop() {
        if (mValueAnimator.isRunning()) {
            mValueAnimator.cancel();
        }
    }

    @Override
    public boolean isRunning() {
        return mValueAnimator.isRunning();
    }

    public int width() {
        return getBounds().width();
    }

    public int height() {
        return getBounds().height();
    }
}
