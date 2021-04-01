package com.yan.refreshloadlayouttest.testactivity;


import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.yan.pullrefreshlayout.PullRefreshLayout;
import com.yan.refreshloadlayouttest.R;
import com.yan.refreshloadlayouttest.widget.DropboxHeader;

import java.util.ArrayList;
import java.util.List;

public class ListViewFragment extends Fragment {
    private static final String TAG = "CommonActivity3";

    protected PullRefreshLayout refreshLayout;
    protected ListView listView;
    private List<SimpleItem> datas = new ArrayList<>();

    private View status;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.common_activity3, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        status = view.findViewById(R.id.status);
        initRefreshLayout(view);
        initListView(view);
        refreshLayout.postDelayed(new Runnable() {
            @Override
            public void run() {
                refreshLayout.autoRefresh();
            }
        }, 150);
    }

    private void initListView(View container) {
        listView = (ListView) container.findViewById(R.id.lv_data);
        listView.setAdapter(new SimpleListAdapter(getContext(), datas));
        listView.setEmptyView(status);
    }

    protected void initData() {
        if (!datas.isEmpty()) {
            datas.clear();
            return;
        }
        datas.add(new SimpleItem(R.drawable.img1, "夏目友人帐"));
        datas.add(new SimpleItem(R.drawable.img2, "夏目友人帐"));
        datas.add(new SimpleItem(R.drawable.img3, "夏目友人帐"));
        datas.add(new SimpleItem(R.drawable.img4, "夏目友人帐"));
        datas.add(new SimpleItem(R.drawable.img5, "夏目友人帐"));
        datas.add(new SimpleItem(R.drawable.img6, "夏目友人帐"));
    }

    private void initRefreshLayout(View container) {
        refreshLayout = (PullRefreshLayout) container.findViewById(R.id.refreshLayout);
        refreshLayout.setHeaderView(new DropboxHeader(getContext(), refreshLayout));
        refreshLayout.setLoadMoreEnable(false);
        refreshLayout.setOnRefreshListener(new PullRefreshLayout.OnRefreshListenerAdapter() {
            @Override
            public void onRefresh() {
                Log.e(TAG, "refreshLayout onRefresh: ");
                refreshLayout.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        initData();
                        ((SimpleListAdapter) listView.getAdapter()).notifyDataSetChanged();
                        refreshLayout.refreshComplete();
                    }
                }, 3000);
            }
        });
    }
}
