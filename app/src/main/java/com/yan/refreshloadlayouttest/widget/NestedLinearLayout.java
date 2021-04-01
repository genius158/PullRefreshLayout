package com.yan.refreshloadlayouttest.widget;

import android.content.Context;
import androidx.core.view.MotionEventCompat;
import androidx.core.view.NestedScrollingChild;
import androidx.core.view.NestedScrollingChildHelper;
import androidx.core.view.NestedScrollingParent;
import androidx.core.view.NestedScrollingParentHelper;
import androidx.core.view.ViewCompat;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewParent;
import android.widget.LinearLayout;

/**
 * Created by yan on 2017/7/19.
 */

public class NestedLinearLayout extends LinearLayout implements NestedScrollingChild, NestedScrollingParent {
    private static final String TAG = "NestedFrameLayout";
    private static final int INVALID_POINTER = -1;

    private final NestedScrollingChildHelper mChildHelper;
    private final NestedScrollingParentHelper mParentHelper;
    private final int[] mScrollConsumed = new int[2];
    private final int[] mScrollOffset = new int[2];

    private final float mTouchSlop;
    private final float mMaximumVelocity;
    private final float mMinimumVelocity;

    private int mLastMotionY;
    private int mActivePointerId;

    private VelocityTracker mVelocityTracker;

    private boolean mIsBeingDragged;

    public NestedLinearLayout(Context context) {
        this(context, null, -1);
    }

    public NestedLinearLayout(Context context, AttributeSet attrs) {
        this(context, attrs, -1);
    }

    public NestedLinearLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setWillNotDraw(false);
        mChildHelper = new NestedScrollingChildHelper(this);
        mParentHelper = new NestedScrollingParentHelper(this);
        setNestedScrollingEnabled(true);
        mTouchSlop = ViewConfiguration.get(getContext()).getScaledTouchSlop();
        mMaximumVelocity = ViewConfiguration.get(getContext()).getScaledMaximumFlingVelocity();
        mMinimumVelocity = ViewConfiguration.get(getContext()).getScaledMinimumFlingVelocity();
    }

    protected boolean nestedTouchEvent(MotionEvent ev) {
        MotionEvent evto = MotionEvent.obtain(ev);
        initVelocityTrackerIfNotExists();
        final int actionMasked = evto.getActionMasked();

        switch (actionMasked) {
            case MotionEvent.ACTION_DOWN: {
                mVelocityTracker.addMovement(ev);

                mLastMotionY = (int) evto.getY();
                mActivePointerId = evto.getPointerId(0);
                startNestedScroll(ViewCompat.SCROLL_AXIS_VERTICAL);
                break;
            }
            case MotionEvent.ACTION_MOVE:
                final int activePointerIndex = evto.findPointerIndex(mActivePointerId);
                if (activePointerIndex == -1) {
                    break;
                }

                final int y = (int) evto.getY(activePointerIndex);
                int deltaY = mLastMotionY - y;

                if (dispatchNestedPreScroll(0, deltaY, mScrollConsumed, mScrollOffset)) {
                    deltaY -= mScrollConsumed[1];
                    evto.offsetLocation(0, mScrollOffset[1]);
                }
                if (!mIsBeingDragged && Math.abs(mLastMotionY - y) > mTouchSlop) {
                    final ViewParent parent = getParent();
                    if (parent != null) {
                        parent.requestDisallowInterceptTouchEvent(true);
                    }
                    mIsBeingDragged = true;
                    if (deltaY > 0) {
                        deltaY -= mTouchSlop;
                    } else {
                        deltaY += mTouchSlop;
                    }
                }

                if (mIsBeingDragged) {
                    mVelocityTracker.addMovement(ev);

                    mLastMotionY = y - mScrollOffset[1];
                    if (dispatchNestedScroll(0, 0, 0, deltaY, mScrollOffset)) {
                        mLastMotionY -= mScrollOffset[1];
                        evto.offsetLocation(0, mScrollOffset[1]);
                    }
                }
                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                if (mIsBeingDragged) {
                    mVelocityTracker.computeCurrentVelocity(1000, mMaximumVelocity);
                    int initialVelocity = (int) mVelocityTracker.getYVelocity(mActivePointerId);

                    if ((Math.abs(initialVelocity) > mMinimumVelocity)) {
                        flingWithNestedDispatch(-initialVelocity);
                    }
                }
                mActivePointerId = INVALID_POINTER;
                endDrag();
                break;
            case MotionEvent.ACTION_POINTER_DOWN: {
                final int index = evto.getActionIndex();
                mLastMotionY = (int) evto.getY(index);
                mActivePointerId = evto.getPointerId(index);
                break;
            }
            case MotionEvent.ACTION_POINTER_UP:
                onSecondaryPointerUp(evto);
                mLastMotionY = (int) evto.getY(evto.findPointerIndex(mActivePointerId));
                break;
        }

        evto.recycle();
        return true;
    }

    protected boolean superTouchEvent(MotionEvent ev) {
        return super.onTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        return nestedTouchEvent(ev);
    }

    private void onSecondaryPointerUp(MotionEvent ev) {
        final int actionIndex = ev.getActionIndex();
        if (ev.getPointerId(actionIndex) == mActivePointerId) {
            final int newPointerIndex = actionIndex == 0 ? 1 : 0;
            mLastMotionY = (int) ev.getY(newPointerIndex);
            mActivePointerId = ev.getPointerId(newPointerIndex);

            if (mVelocityTracker != null) {
                mVelocityTracker.clear();
            }
        }
    }

    private void endDrag() {
        mIsBeingDragged = false;
        recycleVelocityTracker();
        stopNestedScroll();
    }

    private void flingWithNestedDispatch(int velocityY) {
        if (!dispatchNestedPreFling(0, velocityY)) {
            dispatchNestedFling(0, velocityY, true);
        }
    }

    private void initVelocityTrackerIfNotExists() {
        if (mVelocityTracker == null) {
            mVelocityTracker = VelocityTracker.obtain();
        }
    }

    private void recycleVelocityTracker() {
        if (mVelocityTracker != null) {
            mVelocityTracker.recycle();
            mVelocityTracker = null;
        }
    }

    @Override
    public void requestDisallowInterceptTouchEvent(boolean disallowIntercept) {
        if (disallowIntercept) {
            recycleVelocityTracker();
        }
        super.requestDisallowInterceptTouchEvent(disallowIntercept);
    }

    // NestedScrollingChild

    @Override
    public void setNestedScrollingEnabled(boolean enabled) {
        mChildHelper.setNestedScrollingEnabled(enabled);
    }

    @Override
    public boolean isNestedScrollingEnabled() {
        return mChildHelper.isNestedScrollingEnabled();
    }

    @Override
    public boolean startNestedScroll(int axes) {
        return mChildHelper.startNestedScroll(axes);
    }

    @Override
    public void stopNestedScroll() {
        mChildHelper.stopNestedScroll();
    }

    @Override
    public boolean hasNestedScrollingParent() {
        return mChildHelper.hasNestedScrollingParent();
    }

    @Override
    public boolean dispatchNestedScroll(int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed, int[] offsetInWindow) {
        return mChildHelper.dispatchNestedScroll(dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed, offsetInWindow);
    }

    @Override
    public boolean dispatchNestedPreScroll(int dx, int dy, int[] consumed, int[] offsetInWindow) {
        return mChildHelper.dispatchNestedPreScroll(dx, dy, consumed, offsetInWindow);
    }

    @Override
    public boolean dispatchNestedFling(float velocityX, float velocityY, boolean consumed) {
        return mChildHelper.dispatchNestedFling(velocityX, velocityY, consumed);
    }

    @Override
    public boolean dispatchNestedPreFling(float velocityX, float velocityY) {
        return mChildHelper.dispatchNestedPreFling(velocityX, velocityY);
    }

    // NestedScrollingParent

    @Override
    public boolean onStartNestedScroll(View child, View target, int nestedScrollAxes) {
        return (nestedScrollAxes & ViewCompat.SCROLL_AXIS_VERTICAL) != 0;
    }

    @Override
    public void onNestedScrollAccepted(View child, View target, int nestedScrollAxes) {
        mParentHelper.onNestedScrollAccepted(child, target, nestedScrollAxes);
        startNestedScroll(ViewCompat.SCROLL_AXIS_VERTICAL);
    }

    @Override
    public void onStopNestedScroll(View target) {
        mParentHelper.onStopNestedScroll(target);
        stopNestedScroll();
    }

    @Override
    public void onNestedScroll(View target, int dxConsumed, int dyConsumed, int dxUnconsumed,
                               int dyUnconsumed) {
        dispatchNestedScroll(dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed, null);
    }

    @Override
    public void onNestedPreScroll(View target, int dx, int dy, int[] consumed) {
        dispatchNestedPreScroll(dx, dy, consumed, null);
    }

    @Override
    public boolean onNestedFling(View target, float velocityX, float velocityY, boolean consumed) {
        return dispatchNestedFling(velocityX, velocityY, consumed);
    }

    @Override
    public boolean onNestedPreFling(View target, float velocityX, float velocityY) {
        return dispatchNestedPreFling(velocityX, velocityY);
    }

    @Override
    public int getNestedScrollAxes() {
        return mParentHelper.getNestedScrollAxes();
    }

}