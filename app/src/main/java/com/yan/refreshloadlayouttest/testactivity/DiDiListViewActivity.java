package com.yan.refreshloadlayouttest.testactivity;

import android.os.Bundle;
import androidx.annotation.Nullable;

import com.yan.refreshloadlayouttest.R;

public class DiDiListViewActivity extends BaseActivity {
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_didi);
//        final PullRefreshLayout prl = (PullRefreshLayout) findViewById(R.id.prl);
//        prl.setHeaderView(new DiDiListHeader(getApplicationContext(), prl));
//        prl.setOnRefreshListener(new PullRefreshLayout.OnRefreshListenerAdapter() {
//            @Override
//            public void onRefresh() {
//                prl.postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                        prl.refreshComplete();
//                    }
//                }, 3000);
//            }
//        });
//
//        RecyclerView rvData = (RecyclerView) findViewById(R.id.rv_data);
//        rvData.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
//        rvData.setAdapter(new RecyclerView.Adapter() {
//            public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
//                return new RecyclerView.ViewHolder(getLayoutInflater().inflate(R.layout.didi_item, parent, false)) {
//                };
//            }
//
//            public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
//            }
//
//            public int getItemCount() {
//                return 2;
//            }
//        });
//    }


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_frg);
        getSupportFragmentManager().beginTransaction()
                .add(R.id.cl,new DiDiListViewFragment(), "DiDi").commit();
    }
}
