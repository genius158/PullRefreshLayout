package com.yan.refreshloadlayouttest.testactivity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.core.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.yan.pullrefreshlayout.PullRefreshLayout;
import com.yan.pullrefreshlayout.ShowGravity;
import com.yan.refreshloadlayouttest.R;
import com.yan.refreshloadlayouttest.widget.MaterialHeader;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class SwipeFragment extends Fragment implements PullRefreshLayout.OnRefreshListener {
    private PullRefreshLayout refreshLayout;
    private View root;

    public static SwipeFragment getInstance(int index) {
        SwipeFragment refreshFragment = new SwipeFragment();
        Bundle args = new Bundle();
        args.putInt("index", index);
        refreshFragment.setArguments(args);
        return refreshFragment;
    }

    public SwipeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (root == null) {
            if (loadRoot(inflater, container)) {
                init();
            }
        }
        return root;
    }

    private boolean loadRoot(LayoutInflater inflater, ViewGroup containe) {
        switch (getArguments().getInt("index")) {
            case 1:
                root = inflater.inflate(R.layout.fragment_swipe1, containe, false);
                initRecyclerView();
                break;
            case 2:
                root = inflater.inflate(R.layout.activity_nested, containe, false);
                initRecyclerView();
                break;
            case 3:
                root = inflater.inflate(R.layout.common_refresh, containe, false);
                setImages();
                break;
            case 4:
                root = inflater.inflate(R.layout.memory_test, containe, false);
                setImages();
                return false;
        }
        return true;
    }

    private void init() {
        refreshLayout = (PullRefreshLayout) root.findViewById(R.id.refreshLayout);
        refreshLayout.setTwinkEnable(true);
        refreshLayout.setLoadMoreEnable(true);
        refreshLayout.setAutoLoadingEnable(false);

        refreshLayout.setRefreshTriggerDistance(300);
        refreshLayout.setLoadTriggerDistance(300);
        refreshLayout.setPullDownMaxDistance(500);
        refreshLayout.setPullUpMaxDistance(500);
        refreshLayout.setHeaderView(new MaterialHeader(getContext().getApplicationContext(), refreshLayout, 500F / 300));// 触发距离/拖动范围
        refreshLayout.setHeaderShowGravity(ShowGravity.FOLLOW);
        refreshLayout.setHeaderFront(true);
        refreshLayout.setFooterView(new MaterialHeader(getContext().getApplicationContext(), refreshLayout, 500F / 300));
        refreshLayout.setFooterShowGravity(ShowGravity.FOLLOW);
        refreshLayout.setFooterFront(true);
        refreshLayout.setMoveWithContent(false);
        refreshLayout.setOnRefreshListener(this);
    }

    private void initRecyclerView() {
        RecyclerView recyclerView = (RecyclerView) root.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        ArrayList<SimpleItem> datas = new ArrayList<>();
        datas.add(new SimpleItem(R.drawable.img1, "夏目友人帐"));
        datas.add(new SimpleItem(R.drawable.img2, "夏目友人帐"));
        datas.add(new SimpleItem(R.drawable.img3, "夏目友人帐"));
        datas.add(new SimpleItem(R.drawable.img4, "夏目友人帐"));
        datas.add(new SimpleItem(R.drawable.img5, "夏目友人帐"));
        datas.add(new SimpleItem(R.drawable.img6, "夏目友人帐"));
        SimpleAdapter adapter = new SimpleAdapter(getContext(), datas);
        recyclerView.setAdapter(adapter);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
    }

    private void setImages() {
        ((ImageView) root.findViewById(R.id.iv1)).setImageDrawable(ContextCompat
                .getDrawable(getActivity().getApplicationContext(), R.drawable.img1));
        ((ImageView) root.findViewById(R.id.iv2)).setImageDrawable(ContextCompat
                .getDrawable(getContext().getApplicationContext(), R.drawable.img2));
        ((ImageView) root.findViewById(R.id.iv3)).setImageDrawable(ContextCompat
                .getDrawable(getContext().getApplicationContext(), R.drawable.img3));
        ((ImageView) root.findViewById(R.id.iv4)).setImageDrawable(ContextCompat
                .getDrawable(getContext().getApplicationContext(), R.drawable.img4));
        ((ImageView) root.findViewById(R.id.iv5)).setImageDrawable(ContextCompat
                .getDrawable(getContext().getApplicationContext(), R.drawable.img5));
        ((ImageView) root.findViewById(R.id.iv6)).setImageDrawable(ContextCompat
                .getDrawable(getContext().getApplicationContext(), R.drawable.img6));
        ((ImageView) root.findViewById(R.id.iv7)).setImageDrawable(ContextCompat
                .getDrawable(getContext().getApplicationContext(), R.drawable.loading_bg));
    }

    private void onLazyLoad() {
        if (refreshLayout == null) return;

        refreshLayout.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (refreshLayout != null) {
                    refreshLayout.autoRefresh();
                }
            }
        }, 300);
    }

    @Override
    public void onRefresh() {
        refreshLayout.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (refreshLayout != null) {
                    refreshLayout.refreshComplete();
                }
            }
        }, 3000);
    }

    @Override
    public void onLoading() {
        refreshLayout.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (refreshLayout!=null) {
                    refreshLayout.loadMoreComplete();
                }
            }
        }, 3000);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        refreshLayout = null;
        root = null;
    }

    /**
     * Lazy load
     */
    private boolean isLazyLoad = false;
    private boolean isActivityCreate = false;

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            if (isActivityCreate) {
                if (!isLazyLoad) {
                    onLazyLoad();
                    isLazyLoad = true;
                }
            } else if (!isLazyLoad) {
                lazyHandler.sendEmptyMessage(1);
            }
        } else {
            lazyHandler.removeMessages(1);
        }
    }

    private LazyHandler lazyHandler = new LazyHandler(this);

    private static class LazyHandler extends Handler {
        private WeakReference<SwipeFragment> reference;

        private LazyHandler(SwipeFragment refreshFragment) {
            reference = new WeakReference<>(refreshFragment);
        }

        @Override
        public void handleMessage(Message msg) {
            SwipeFragment rf = reference.get();
            if (rf != null) {
                if (msg.what == 1) {
                    if (!rf.isActivityCreate) {
                        sendEmptyMessageDelayed(1, 10);
                        return;
                    }
                    rf.onLazyLoad();
                    rf.isLazyLoad = true;
                }
            }
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState != null) {
            isLazyLoad = savedInstanceState.getBoolean("isLazyLoad");
        }
        isActivityCreate = true;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean("isLazyLoad", isLazyLoad);
    }
}
