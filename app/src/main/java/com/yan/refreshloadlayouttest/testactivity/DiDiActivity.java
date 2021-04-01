package com.yan.refreshloadlayouttest.testactivity;

import android.os.Bundle;
import android.view.ViewGroup;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.yan.pullrefreshlayout.PullRefreshLayout;
import com.yan.refreshloadlayouttest.R;
import com.yan.refreshloadlayouttest.widget.DiDiHeader;

public class DiDiActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_didi);
        final PullRefreshLayout prl = (PullRefreshLayout) findViewById(R.id.prl);
        prl.setHeaderView(new DiDiHeader(getApplicationContext(), prl));
        prl.setOnRefreshListener(new PullRefreshLayout.OnRefreshListenerAdapter() {
            @Override
            public void onRefresh() {
                prl.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        prl.refreshComplete();
                    }
                }, 3000);
            }
        });

        RecyclerView rvData = (RecyclerView) findViewById(R.id.rv_data);
        rvData.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        rvData.setAdapter(new RecyclerView.Adapter() {
            public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                return new RecyclerView.ViewHolder(getLayoutInflater().inflate(R.layout.didi_item, parent, false)) {
                };
            }

            public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            }

            public int getItemCount() {
                return 2;
            }
        });
    }
}
