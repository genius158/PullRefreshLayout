package com.yan.refreshloadlayouttest.widget;

import android.content.Context;
import androidx.core.content.ContextCompat;
import androidx.core.widget.NestedScrollView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ListView;

import androidx.recyclerview.widget.RecyclerView;
import com.yan.pullrefreshlayout.PullRefreshLayout;
import com.yan.pullrefreshlayout.ShowGravity;
import com.yan.refreshloadlayouttest.R;
import com.yan.refreshloadlayouttest.testactivity.SimpleItem;
import com.yan.refreshloadlayouttest.testactivity.SimpleListAdapter;

import java.util.ArrayList;

/**
 * Created by Administrator on 2017/10/1 0001.
 */

public class DiDiListHeader extends FrameLayout implements PullRefreshLayout.OnPullListener, NestedScrollView.OnScrollChangeListener {
    private PullRefreshLayout prl;
    private ClassicsHeader loadingView;
    private ListView fixedHeader;
    private ArrayList<SimpleItem> headerListData;
    private boolean isNeedDispatchTouchEvent;

    public DiDiListHeader(Context context, final PullRefreshLayout prl) {
        super(context);
        setLayoutParams(new ViewGroup.LayoutParams(-1, -1));
        this.prl = prl;
        prl.setHeaderFront(false);
        prl.setHeaderShowGravity(ShowGravity.PLACEHOLDER);
        scrollInit();
        LayoutInflater.from(context).inflate(R.layout.didi_list_header, this, true);

        listInt((ListView) findViewById(R.id.fixed_top));
        loadingView = findViewById(R.id.loading);

        postDelayed(new Runnable() {
            @Override
            public void run() {
                updateListData();
            }
        }, 2000);
    }

    private void updateListData() {
        headerListData.add(new SimpleItem(R.drawable.img2, " test test"));
        headerListData.add(new SimpleItem(R.drawable.img1, " test test"));
        SimpleListAdapter adapter = (SimpleListAdapter) fixedHeader.getAdapter();
        adapter.notifyDataSetChanged();

        // 需要listView加载完成，才能知道固定头部的高度
        postDelayed(new Runnable() {
            @Override
            public void run() {
                setFixedHeaderParam();
            }
        }, 150);
    }

    private void listInt(ListView fixedHeader) {
        this.fixedHeader = fixedHeader;
        headerListData = new ArrayList<>();

        fixedHeader.setAdapter(new SimpleListAdapter(getContext(), headerListData));
    }

    private void setFixedHeaderParam() {
        View target = prl.getTargetView();
        prl.setRefreshTriggerDistance(loadingView.getHeight());
        target.setPadding(target.getPaddingLeft()
                , fixedHeader.getHeight()
                , target.getPaddingRight()
                , target.getPaddingBottom());
        if (target instanceof RecyclerView) {
            ((RecyclerView) target).smoothScrollToPosition(0);
        }
    }

    private void scrollInit() {
        post(new Runnable() {
            @Override
            public void run() {
                View target = prl.getTargetView();
                setFixedHeaderParam();
                ((ViewGroup) target).setClipToPadding(false);
                target.setOverScrollMode(OVER_SCROLL_NEVER);
                target.setOnTouchListener(new OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        if (event.getActionMasked() == MotionEvent.ACTION_DOWN) {
                            isNeedDispatchTouchEvent = true;//这里重置是否需要分发事件的标志位
                        } else if (event.getActionMasked() == MotionEvent.ACTION_MOVE && prl.isDragVertical()) {
                            isNeedDispatchTouchEvent = false;//如果prl处在纵向拖动，则取消header的事件分发
                        }
                        if (isNeedDispatchTouchEvent) {
                            DiDiListHeader.this.dispatchTouchEvent(event);
                        }
                        return false;
                    }
                });
                if (target instanceof NestedScrollView) {
                    ((NestedScrollView) target).setOnScrollChangeListener(DiDiListHeader.this);
                } else if (target instanceof RecyclerView) {
                    ((RecyclerView) target).addOnScrollListener(getRvOnScrollListener());
                    ((RecyclerView) target).scrollToPosition(0);
                }
            }
        });
    }

    @Override
    public void onPullChange(float percent) {
        loadingView.setTranslationY(prl.getMoveDistance());
    }

    @Override
    public void onPullHoldTrigger() {
        loadingView.onPullHoldTrigger();
    }

    @Override
    public void onPullHoldUnTrigger() {
        loadingView.onPullHoldUnTrigger();
    }

    @Override
    public void onPullHolding() {
        loadingView.onPullHolding();
    }

    @Override
    public void onPullFinish(boolean flag) {
        loadingView.onPullFinish(flag);
    }

    @Override
    public void onPullReset() {
        loadingView.onPullReset();
    }

    private RecyclerView.OnScrollListener getRvOnScrollListener() {
        return new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                RecyclerView.ViewHolder viewHolder = recyclerView.findViewHolderForAdapterPosition(0);
                float offset = viewHolder != null && (prl.isTargetScrollDownAble() || prl.isTargetScrollUpAble()) ? viewHolder.itemView.getTop() : 0;
                getChildAt(0).setTranslationY(-fixedHeader.getHeight() + Math.max(0, offset));
            }
        };
    }

    @Override
    public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
        getChildAt(0).setTranslationY(-scrollY);//scrollTo() 有明显的延迟,改为移动子view
    }
}
