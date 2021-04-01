package com.yan.refreshloadlayouttest.testactivity;

import android.os.Bundle;
import androidx.core.content.ContextCompat;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;
import com.yan.pullrefreshlayout.PullRefreshLayout;
import com.yan.pullrefreshlayout.ShowGravity;
import com.yan.refreshloadlayouttest.R;
import com.yan.refreshloadlayouttest.widget.SlidingDownHeader;

import java.util.ArrayList;
import java.util.List;

public class SlidingDownActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sliding);
        initRefreshLayout();
    }

    protected void initRefreshLayout() {
        PullRefreshLayout refreshLayout = (PullRefreshLayout) findViewById(R.id.parl);
        refreshLayout.setRefreshShowGravity(ShowGravity.CENTER, ShowGravity.FOLLOW);

        SlidingDownHeader slidingDownHeader = new SlidingDownHeader(getBaseContext(), refreshLayout);
        ViewPager vp = (ViewPager) getLayoutInflater().inflate(R.layout.scroll_vp_layout, slidingDownHeader, false);
        initVp(vp);

        slidingDownHeader.setTargetView(vp);
        refreshLayout.setHeaderView(slidingDownHeader);
    }

    private void initVp(ViewPager vp) {
        final List<TextView> tvs = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            TextView textView = new TextView(getBaseContext());
            textView.setText("How to code Horizontal SlidingDownPanelLayout - Tip" + i);
            textView.setTextColor(ContextCompat.getColor(getApplication(), R.color.colorWhite));
            textView.setLayoutParams(new ViewGroup.LayoutParams(-1, -1));
            textView.setGravity(Gravity.CENTER);
            textView.setTextSize(20);
            tvs.add(textView);
        }
        vp.setAdapter(new PagerAdapter() {
            @Override
            public int getCount() {
                return tvs.size();
            }

            @Override
            public Object instantiateItem(ViewGroup container, int position) {
                container.addView(tvs.get(position));
                return tvs.get(position);
            }

            @Override
            public void destroyItem(ViewGroup container, int position, Object object) {
                container.removeView((View) object);
            }

            @Override
            public boolean isViewFromObject(View view, Object object) {
                return view == object;
            }
        });
    }
}
