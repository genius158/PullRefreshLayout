package com.yan.refreshloadlayouttest.widget;

import android.content.Context;
import androidx.core.widget.NestedScrollView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;

/**
 * Created by Administrator on 2017/10/3 0003.
 */

public class PNestedScrollView extends NestedScrollView {
    public PNestedScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        Log.e("dispatchTouchEvent", ev.getActionMasked() + "  " + MotionEvent.ACTION_DOWN + "   " + MotionEvent.ACTION_MOVE + "  " + MotionEvent.ACTION_UP + "  " + MotionEvent.ACTION_CANCEL);
        return super.dispatchTouchEvent(ev);
    }
}
