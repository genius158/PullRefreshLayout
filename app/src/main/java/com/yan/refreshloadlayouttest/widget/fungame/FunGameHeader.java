package com.yan.refreshloadlayouttest.widget.fungame;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Color;
import androidx.core.content.ContextCompat;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.yan.pullrefreshlayout.PullRefreshLayout;
import com.yan.refreshloadlayouttest.R;

import static com.yan.pullrefreshlayout.PRLCommonUtils.dipToPx;
import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;

/**
 * 游戏 header
 * Created by SCWANG on 2017/6/17.
 * from https://github.com/scwang90/SmartRefreshLayout
 */

public class FunGameHeader extends FunGameBase {

    //<editor-fold desc="Field">
    /**
     * 分割线默认宽度大小
     */
    protected float DIVIDING_LINE_SIZE = 1.f;

    private RelativeLayout curtainReLayout, maskReLayout;

    private TextView topMaskView, bottomMaskView;

    private int halfHitBlockHeight;

    private boolean isStart = false;

    private String topMaskViewText = "下拉即将展开";//"Pull To Break Out!";
    private String bottomMaskViewText = "拖动控制游戏";//"Scrooll to move handle";

    private int topMaskTextSize;

    private int bottomMaskTextSize;

    //</editor-fold>

    //<editor-fold desc="View">
    public FunGameHeader(Context context, PullRefreshLayout pullRefreshLayout) {
        super(context, pullRefreshLayout);
        this.initView(context, null);

    }

    private void initView(Context context, AttributeSet attrs) {

        topMaskTextSize = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 16, getResources().getDisplayMetrics());
        bottomMaskTextSize = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 16, getResources().getDisplayMetrics());

        curtainReLayout = new RelativeLayout(context);
        maskReLayout = new RelativeLayout(context);
        maskReLayout.setBackgroundColor(Color.parseColor("#3A3A3A"));

        topMaskView = createMaskTextView(context, topMaskViewText, topMaskTextSize, Gravity.BOTTOM);
        bottomMaskView = createMaskTextView(context, bottomMaskViewText, bottomMaskTextSize, Gravity.TOP);

        DIVIDING_LINE_SIZE = Math.max(1, dipToPx(getContext(), 0.5f));
        post(new Runnable() {
            @Override
            public void run() {
                coverMaskView();
            }
        });
    }

    private TextView createMaskTextView(Context context, String text, int textSize, int gravity) {
        TextView maskTextView = new TextView(context);
        maskTextView.setTextColor(ContextCompat.getColor(context, R.color.colorPrimaryDark));
        maskTextView.setBackgroundColor(ContextCompat.getColor(context, R.color.colorF0F9FF));
        maskTextView.setGravity(gravity | Gravity.CENTER_HORIZONTAL);
        maskTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
        maskTextView.setText(text);
        return maskTextView;
    }

    private void coverMaskView() {
        if (getChildCount() < 2 && !isInEditMode()) {
            LayoutParams maskLp = new LayoutParams(MATCH_PARENT, mHeaderHeight);
//            maskLp.topMargin = (int) FunGameView.DIVIDING_LINE_SIZE;
//            maskLp.bottomMargin = (int) FunGameView.DIVIDING_LINE_SIZE;

            addView(maskReLayout, maskLp);
            addView(curtainReLayout, maskLp);

            halfHitBlockHeight = (int) ((mHeaderHeight/* - 2 * DIVIDING_LINE_SIZE*/) * .5f);
            RelativeLayout.LayoutParams topRelayLayoutParams = new RelativeLayout.LayoutParams(MATCH_PARENT, halfHitBlockHeight);
            RelativeLayout.LayoutParams bottomRelayLayoutParams = new RelativeLayout.LayoutParams(MATCH_PARENT, halfHitBlockHeight);
            bottomRelayLayoutParams.topMargin = mHeaderHeight - halfHitBlockHeight;
            curtainReLayout.addView(topMaskView, topRelayLayoutParams);
            curtainReLayout.addView(bottomMaskView, bottomRelayLayoutParams);
        }
    }

    private void doStart(long delay) {
        ObjectAnimator topMaskAnimator = ObjectAnimator.ofFloat(topMaskView, "translationY", topMaskView.getTranslationY(), -halfHitBlockHeight);
        ObjectAnimator bottomMaskAnimator = ObjectAnimator.ofFloat(bottomMaskView, "translationY", bottomMaskView.getTranslationY(), halfHitBlockHeight);
        ObjectAnimator maskShadowAnimator = ObjectAnimator.ofFloat(maskReLayout, "alpha", maskReLayout.getAlpha(), 0);

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.play(topMaskAnimator).with(bottomMaskAnimator).with(maskShadowAnimator);
        animatorSet.setDuration(800);
        animatorSet.setStartDelay(delay);
        animatorSet.start();

        animatorSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                topMaskView.setVisibility(View.GONE);
                bottomMaskView.setVisibility(View.GONE);
                maskReLayout.setVisibility(View.GONE);
                onGameStart();
            }
        });
    }

    protected void onGameStart() {

    }

    @Override
    public void onPullHolding() {
        super.onPullHolding();
        postStart();
    }

    public void postStart() {
        if (!isStart) {
            doStart(200);
            isStart = true;
        }
    }

    public void postEnd() {
        isStart = false;

        topMaskView.setTranslationY(topMaskView.getTranslationY() + halfHitBlockHeight);
        bottomMaskView.setTranslationY(bottomMaskView.getTranslationY() - halfHitBlockHeight);
        maskReLayout.setAlpha(1.f);

        topMaskView.setVisibility(View.VISIBLE);
        bottomMaskView.setVisibility(View.VISIBLE);
        maskReLayout.setVisibility(View.VISIBLE);
    }

    public void setTopMaskViewText(String topMaskViewText) {
        this.topMaskViewText = topMaskViewText;
        topMaskView.setText(topMaskViewText);
    }

    public void setBottomMaskViewText(String bottomMaskViewText) {
        this.bottomMaskViewText = bottomMaskViewText;
        bottomMaskView.setText(bottomMaskViewText);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
    }

    @Override
    public void onPullFinish(boolean flag) {
        super.onPullFinish(flag);
        if (!mManualOperation) {
            postEnd();
        }
    }

    @Override
    public void onPullReset() {
        super.onPullReset();
    }

    //</editor-fold>
}
