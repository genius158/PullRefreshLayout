package com.yan.refreshloadlayouttest.testactivity;

import android.os.Bundle;
import androidx.core.content.ContextCompat;
import androidx.core.widget.NestedScrollView;
import androidx.appcompat.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;

import com.yan.pullrefreshlayout.PullRefreshLayout;
import com.yan.refreshloadlayouttest.R;
import com.yan.refreshloadlayouttest.widget.ClassicsHeader;

/**
 * 微博列表
 * code modify from SmartRefreshLayout
 */
public class ScrollingActivity2 extends BaseActivity {

    private int mOffset = 0;
    private int mScrollY = 0;

    private int count = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scrolling2);

        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        final View parallax = findViewById(R.id.parallax);
        final View buttonBar = findViewById(R.id.buttonBarLayout);
        final NestedScrollView scrollView = (NestedScrollView) findViewById(R.id.scrollView);
        final PullRefreshLayout refreshLayout = (PullRefreshLayout) findViewById(R.id.refreshLayout);
        refreshLayout.setHeaderView(new ClassicsHeader(this));
        refreshLayout.setOnRefreshListener(new PullRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshLayout.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (count++ % 3 == 0) {
                            ClassicsHeader classicsHeader = refreshLayout.getHeaderView();
                            classicsHeader.setRefreshError();
                        }

                        refreshLayout.refreshComplete();
                    }
                }, 2000);
            }

            @Override
            public void onLoading() {

            }
        });
        scrollView.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            private int lastScrollY = 0;
            private int h = 340;
            private int color = ContextCompat.getColor(getApplicationContext(), R.color.colorPrimary) & 0x00ffffff;

            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                if (lastScrollY < h) {
                    scrollY = Math.min(h, scrollY);
                    mScrollY = scrollY > h ? h : scrollY;
                    buttonBar.setAlpha(1f * mScrollY / h);
                    toolbar.setBackgroundColor(((255 * mScrollY / h) << 24) | color);
                    parallax.setTranslationY(mOffset - mScrollY);
                }
                lastScrollY = scrollY;
            }
        });
        buttonBar.setAlpha(0);
        toolbar.setBackgroundColor(0);
        setImages();

        refreshLayout.postDelayed(new Runnable() {
            @Override
            public void run() {
                refreshLayout.autoRefresh();
            }
        }, 150);
    }

    private void setImages() {
        ((ImageView) findViewById(R.id.iv1)).setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.img1));
        ((ImageView) findViewById(R.id.iv2)).setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.img2));
        ((ImageView) findViewById(R.id.iv3)).setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.img3));
        ((ImageView) findViewById(R.id.iv4)).setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.img4));
        ((ImageView) findViewById(R.id.iv5)).setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.img5));
        ((ImageView) findViewById(R.id.iv6)).setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.img6));
        ((ImageView) findViewById(R.id.iv7)).setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.loading_bg));
    }

}
