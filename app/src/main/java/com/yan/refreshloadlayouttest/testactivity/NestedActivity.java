package com.yan.refreshloadlayouttest.testactivity;

import android.os.Bundle;
import androidx.annotation.Nullable;

import com.yan.refreshloadlayouttest.R;

import me.imid.swipebacklayout.lib.app.SwipeBackActivity;

public class NestedActivity extends SwipeBackActivity {
//    private static final String TAG = "NestedActivity";
//    private List<SimpleItem> datas;
//    private PullRefreshLayout refreshLayout;
//    private SimpleAdapter adapter;
//    private View vState;
//    private RecyclerView recyclerView;
//    private ClassicHoldLoadView classicLoadView;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_nested);
//        initData();
//        initRefreshLayout();
//        initRecyclerView();
//        vState = findViewById(R.id.no_data);
//
//        refreshLayout.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                refreshLayout.autoRefresh();
//            }
//        }, 150);
//    }
//
//    private void initRecyclerView() {
//        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
//        recyclerView.setLayoutManager(new LinearLayoutManager(this));
//        adapter = new SimpleAdapter(this, datas);
//        recyclerView.setAdapter(adapter);
//        recyclerView.setItemAnimator(new DefaultItemAnimator());
//    }
//
//    private void initRefreshLayout() {
//        refreshLayout = (PullRefreshLayout) findViewById(R.id.refreshLayout);
//        refreshLayout.setLoadTriggerDistance((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 60, getResources().getDisplayMetrics()));
//        refreshLayout.setFooterView(classicLoadView = new ClassicHoldLoadView(getApplicationContext(), refreshLayout));
//        refreshLayout.setOnRefreshListener(new PullRefreshLayout.OnRefreshListener() {
//            @Override
//            public void onRefresh() {
//                refreshLayout.postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                        if (vState.getVisibility() == View.VISIBLE) {
//                            vState.setVisibility(View.GONE);
//                            recyclerView.setVisibility(View.VISIBLE);
//                            refreshLayout.setTargetView(recyclerView);
//                            refreshLayout.setFooterView(classicLoadView);
//                            refreshLayout.setLoadMoreEnable(true);
//                            refreshLayout.setAutoLoadingEnable(true);
//
//                            classicLoadView.holdReset();
//
//                        } else {
//                            refreshLayout.setTargetView(vState);
//                            vState.setVisibility(View.VISIBLE);
//                            recyclerView.setVisibility(View.GONE);
//                            refreshLayout.cancelTouchEvent();
//                            refreshLayout.setFooterView(null);
//                            refreshLayout.setLoadMoreEnable(false);
//                            refreshLayout.setAutoLoadingEnable(false);
//                        }
//                        refreshLayout.refreshComplete();
//                    }
//                }, 3000);
//            }
//
//            @Override
//            public void onLoading() {
//                refreshLayout.postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                        if (datas.size() > 10) {
//                            classicLoadView.loadFinish(true);
//                            return;
//                        }
//                        datas.add(new SimpleItem(R.drawable.img4, "夏目友人帐"));
//                        adapter.notifyItemInserted(datas.size());
//                        classicLoadView.startBackAnimation();
//                    }
//                }, 1000);
//            }
//        });
//    }
//
//    protected void initData() {
//        datas = new ArrayList<>();
//
//        datas.add(new SimpleItem(R.drawable.img1, "夏目友人帐"));
//        datas.add(new SimpleItem(R.drawable.img2, "夏目友人帐"));
//        datas.add(new SimpleItem(R.drawable.img3, "夏目友人帐"));
//        datas.add(new SimpleItem(R.drawable.img4, "夏目友人帐"));
//        datas.add(new SimpleItem(R.drawable.img5, "夏目友人帐"));
//        datas.add(new SimpleItem(R.drawable.img6, "夏目友人帐"));
//    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_frg);
        getSupportFragmentManager().beginTransaction()
                .add(R.id.cl,new NestedFragment(), "Nested").commit();
    }
}
