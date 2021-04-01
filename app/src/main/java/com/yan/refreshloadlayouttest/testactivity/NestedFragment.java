package com.yan.refreshloadlayouttest.testactivity;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.yan.pullrefreshlayout.PullRefreshLayout;
import com.yan.refreshloadlayouttest.R;
import com.yan.refreshloadlayouttest.widget.ClassicHoldLoadView;
import com.yan.refreshloadlayouttest.widget.PlaceHolderHeader;

import java.util.ArrayList;
import java.util.List;

public class NestedFragment extends Fragment {
    private static final String TAG = "NestedActivity";
    private List<SimpleItem> datas;
    private PullRefreshLayout refreshLayout;
    private SimpleAdapter adapter;
    private View vState;
    private RecyclerView recyclerView;
    private ClassicHoldLoadView classicLoadView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_nested, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initData();
        initRefreshLayout(view);
        initRecyclerView(view);
        vState = view.findViewById(R.id.no_data);

        refreshLayout.postDelayed(new Runnable() {
            @Override
            public void run() {
                refreshLayout.autoRefresh();
            }
        }, 150);
        refreshLayout.setHeaderView(new PlaceHolderHeader(getContext()));
    }


    private void initRecyclerView(View container) {
        recyclerView = (RecyclerView) container.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new SimpleAdapter(getContext(), datas);
        recyclerView.setAdapter(adapter);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
    }

    private void initRefreshLayout(View container) {
        refreshLayout = (PullRefreshLayout) container.findViewById(R.id.refreshLayout);
        refreshLayout.setLoadTriggerDistance((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 60, getResources().getDisplayMetrics()));
        refreshLayout.setFooterView(classicLoadView = new ClassicHoldLoadView(getContext(), refreshLayout));
        refreshLayout.setOnRefreshListener(new PullRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshLayout.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        //if (vState.getVisibility() == View.VISIBLE) {
                        //    vState.setVisibility(View.GONE);
                        //    recyclerView.setVisibility(View.VISIBLE);
                        //    refreshLayout.setTargetView(recyclerView);
                        //    refreshLayout.setFooterView(classicLoadView);
                        //    refreshLayout.setLoadMoreEnable(true);
                        //    refreshLayout.setAutoLoadingEnable(true);
                        //
                        //    classicLoadView.holdReset();
                        //
                        //} else {
                        //    refreshLayout.setTargetView(vState);
                        //    vState.setVisibility(View.VISIBLE);
                        //    recyclerView.setVisibility(View.GONE);
                        //    refreshLayout.cancelTouchEvent();
                        //    refreshLayout.setFooterView(null);
                        //    refreshLayout.setLoadMoreEnable(false);
                        //    refreshLayout.setAutoLoadingEnable(false);
                        //}
                        refreshLayout.refreshComplete();
                    }
                }, 3000);
            }

            @Override
            public void onLoading() {
                refreshLayout.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (datas.size() > 10) {
                            classicLoadView.loadFinish(true);
                            return;
                        }
                        datas.add(new SimpleItem(R.drawable.img4, "夏目友人帐"));
                        adapter.notifyItemInserted(datas.size());
                        classicLoadView.startBackAnimation();
                    }
                }, 2000);
            }
        });
    }

    protected void initData() {
        datas = new ArrayList<>();

        datas.add(new SimpleItem(R.drawable.img1, "夏目友人帐"));
        datas.add(new SimpleItem(R.drawable.img2, "夏目友人帐"));
        datas.add(new SimpleItem(R.drawable.img3, "夏目友人帐"));
        datas.add(new SimpleItem(R.drawable.img4, "夏目友人帐"));
        datas.add(new SimpleItem(R.drawable.img5, "夏目友人帐"));
        datas.add(new SimpleItem(R.drawable.img6, "夏目友人帐"));
    }
}
