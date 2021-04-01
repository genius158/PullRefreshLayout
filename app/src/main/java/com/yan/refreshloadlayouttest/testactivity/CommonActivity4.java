package com.yan.refreshloadlayouttest.testactivity;

import android.os.Bundle;
import android.widget.ListView;

import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.yan.pullrefreshlayout.PullRefreshLayout;
import com.yan.refreshloadlayouttest.R;

import java.util.ArrayList;
import java.util.List;

public class CommonActivity4 extends BaseActivity {
    private List<SimpleItem> datas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.common_activity4);
        initData();
        initListView();
        initRecyclerView();
        initRefreshLayout();
    }

    private void initListView() {
        ListView listView = (ListView) findViewById(R.id.lv_data);
        listView.setAdapter(new SimpleListAdapter(this, datas));
    }

    private void initRefreshLayout() {
        ((PullRefreshLayout) findViewById(R.id.refreshLayout1)).setRefreshEnable(false);
        ((PullRefreshLayout) findViewById(R.id.refreshLayout2)).setRefreshEnable(false);
    }

    private void initRecyclerView() {
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.rv_data);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        SimpleAdapter adapter = new SimpleAdapter(this, datas);

        recyclerView.setAdapter(adapter);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
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