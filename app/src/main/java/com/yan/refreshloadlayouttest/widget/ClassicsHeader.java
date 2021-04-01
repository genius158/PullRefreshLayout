package com.yan.refreshloadlayouttest.widget;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import androidx.annotation.DrawableRes;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.core.content.ContextCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.yan.pullrefreshlayout.PullRefreshLayout;
import com.yan.refreshloadlayouttest.R;
import com.yan.refreshloadlayouttest.widget.pathview.PathsDrawable;

import static com.yan.pullrefreshlayout.PRLCommonUtils.dipToPx;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

/**
 * 经典下拉头部
 * Created by SCWANG on 2017/5/28.
 * from https://github.com/scwang90/SmartRefreshLayout
 */
public class ClassicsHeader extends NestedRelativeLayout implements PullRefreshLayout.OnPullListener {

    public static String REFRESH_HEADER_PULLDOWN = "下拉可以刷新";
    public static String REFRESH_HEADER_REFRESHING = "正在刷新...";
    public static String REFRESH_HEADER_RELEASE = "释放立即刷新";
    public static String REFRESH_HEADER_FINISH = "刷新完成";
    public static String REFRESH_HEADER_FAILED = "刷新失败";

    private String KEY_LAST_UPDATE_TIME = "LAST_UPDATE_TIME";

    private Date mLastTime;
    protected TextView mHeaderText;
    protected TextView mLastUpdateText;
    protected ImageView mArrowView;
    protected ImageView mProgressView;
    protected PathsDrawable mArrowDrawable;
    protected ProgressDrawable mProgressDrawable;
    private DateFormat mFormat = new SimpleDateFormat("上次更新 M-d HH:mm", Locale.CHINA);
    private SharedPreferences mShared;

    public ClassicsHeader(Context context) {
        super(context);
        this.initView(context, null);
    }

    public ClassicsHeader(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.initView(context, attrs);
        mHeaderText.setTextColor(ContextCompat.getColor(getContext(), R.color.colorFF666666));
        mArrowDrawable.parserColors(0xff666666);
        mLastUpdateText.setTextColor(ContextCompat.getColor(getContext(), R.color.colorFF999999));
        mProgressDrawable.setColor(ContextCompat.getColor(getContext(), R.color.colorFF666666));
    }

    protected void initView(Context context, AttributeSet attrs) {
        setMinimumHeight(dipToPx(getContext(), 80));

        LinearLayout layout = new LinearLayout(context);
        layout.setId(android.R.id.widget_frame);
        layout.setGravity(Gravity.CENTER_HORIZONTAL);
        layout.setOrientation(LinearLayout.VERTICAL);
        mHeaderText = new TextView(context);
        mHeaderText.setText(REFRESH_HEADER_PULLDOWN);
        mHeaderText.setTextColor(ContextCompat.getColor(getContext(), R.color.colorWhite));
        mHeaderText.setTextSize(16);

        mLastUpdateText = new TextView(context);
        mLastUpdateText.setTextColor(ContextCompat.getColor(getContext(), R.color.color9EFFFFFF));
        mLastUpdateText.setTextSize(12);
        LinearLayout.LayoutParams lpHeaderText = new LinearLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT);
        layout.addView(mHeaderText, lpHeaderText);
        LinearLayout.LayoutParams lpUpdateText = new LinearLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT);
        layout.addView(mLastUpdateText, lpUpdateText);

        LayoutParams lpHeaderLayout = new LayoutParams(WRAP_CONTENT, WRAP_CONTENT);
        lpHeaderLayout.addRule(CENTER_IN_PARENT);
        addView(layout, lpHeaderLayout);

        mProgressView = new ImageView(context);
        mProgressView.animate().setInterpolator(new LinearInterpolator());
        LayoutParams lpProgress = new LayoutParams(dipToPx(getContext(), 20), dipToPx(getContext(), 20));
        lpProgress.rightMargin = dipToPx(getContext(), 20);
        lpProgress.addRule(CENTER_VERTICAL);
        lpProgress.addRule(LEFT_OF, android.R.id.widget_frame);
        addView(mProgressView, lpProgress);

        mArrowView = new ImageView(context);
        addView(mArrowView, lpProgress);

        if (isInEditMode()) {
            mArrowView.setVisibility(GONE);
            mHeaderText.setText(REFRESH_HEADER_REFRESHING);
        } else {
            mProgressView.setVisibility(GONE);
        }

        mArrowDrawable = new PathsDrawable();
        mArrowDrawable.parserColors(0xfff1f1f1);
        mArrowDrawable.parserPaths("M20,12l-1.41,-1.41L13,16.17V4h-2v12.17l-5.58,-5.59L4,12l8,8 8,-8z");
        mArrowView.setImageDrawable(mArrowDrawable);


        mProgressDrawable = new ProgressDrawable();
        mProgressDrawable.setColor(ContextCompat.getColor(getContext(), R.color.colorWhite));
        mProgressView.setImageDrawable(mProgressDrawable);


        try {//try 不能删除-否则会出现兼容性问题
            if (context instanceof FragmentActivity) {
                FragmentManager manager = ((FragmentActivity) context).getSupportFragmentManager();
                if (manager != null) {
                    List<Fragment> fragments = manager.getFragments();
                    if (fragments != null && fragments.size() > 0) {
                        setLastUpdateTime(new Date());
                        return;
                    }
                }
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }

        KEY_LAST_UPDATE_TIME += context.getClass().getName();
        mShared = context.getSharedPreferences("ClassicsHeader", Context.MODE_PRIVATE);
        setLastUpdateTime(new Date(mShared.getLong(KEY_LAST_UPDATE_TIME, System.currentTimeMillis())));
    }

    //<editor-fold desc="API">
    public ClassicsHeader setProgressBitmap(Bitmap bitmap) {
        mProgressDrawable = null;
        mProgressView.setImageBitmap(bitmap);
        return this;
    }

    public ClassicsHeader setProgressDrawable(Drawable drawable) {
        mProgressDrawable = null;
        mProgressView.setImageDrawable(drawable);
        return this;
    }

    public ClassicsHeader setProgressResource(@DrawableRes int resId) {
        mProgressDrawable = null;
        mProgressView.setImageResource(resId);
        return this;
    }

    public ClassicsHeader setArrowBitmap(Bitmap bitmap) {
        mArrowDrawable = null;
        mArrowView.setImageBitmap(bitmap);
        return this;
    }

    public ClassicsHeader setArrowDrawable(Drawable drawable) {
        mArrowDrawable = null;
        mArrowView.setImageDrawable(drawable);
        return this;
    }

    public ClassicsHeader setArrowResource(@DrawableRes int resId) {
        mArrowDrawable = null;
        mArrowView.setImageResource(resId);
        return this;
    }

    public ClassicsHeader setLastUpdateTime(Date time) {
        mLastTime = time;
        mLastUpdateText.setText(mFormat.format(time));
        if (mShared != null && !isInEditMode()) {
            mShared.edit().putLong(KEY_LAST_UPDATE_TIME, time.getTime()).apply();
        }
        return this;
    }

    public ClassicsHeader setTimeFormat(DateFormat format) {
        mFormat = format;
        mLastUpdateText.setText(mFormat.format(mLastTime));
        return this;
    }

    @Override
    public void onPullChange(float percent) {

    }

    @Override
    public void onPullHoldTrigger() {
        mHeaderText.setText(REFRESH_HEADER_RELEASE);
        mArrowView.animate().rotation(180);
    }

    @Override
    public void onPullHoldUnTrigger() {
        mHeaderText.setText(REFRESH_HEADER_PULLDOWN);
        mArrowView.setVisibility(VISIBLE);
        mProgressView.setVisibility(GONE);
        mArrowView.animate().rotation(0);
    }

    @Override
    public void onPullHolding() {
        Log.e("onPullHolding", "onPullHolding: ");
        mHeaderText.setText(REFRESH_HEADER_REFRESHING);
        mProgressView.setVisibility(VISIBLE);
        mArrowView.setVisibility(GONE);

        if (mProgressDrawable != null) {
            mProgressDrawable.start();
        } else {
            mProgressView.animate().rotation(36000).setDuration(100000);
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (mProgressDrawable != null) {
            mProgressDrawable.stop();
        }
    }

    public void setRefreshError() {
        mHeaderText.setText(REFRESH_HEADER_FAILED);
    }

    @Override
    public void onPullFinish(boolean flag) {
        Log.e("onPullFinish", "onPullFinish: ");
        if (mProgressDrawable != null) {
            mProgressDrawable.stop();
        } else {
            mProgressView.animate().rotation(0).setDuration(300);
        }
        if (!mHeaderText.getText().toString().equals(REFRESH_HEADER_FAILED)) {
            mHeaderText.setText(REFRESH_HEADER_FINISH);
        }
        mProgressView.setVisibility(GONE);
        setLastUpdateTime(new Date());
    }

    @Override
    public void onPullReset() {
        Log.e("onPullReset", "onPullReset: ");
        onPullHoldUnTrigger();
    }
}
