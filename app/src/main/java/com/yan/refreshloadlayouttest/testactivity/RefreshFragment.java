package com.yan.refreshloadlayouttest.testactivity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.core.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import com.yan.pullrefreshlayout.PullRefreshLayout;
import com.yan.refreshloadlayouttest.R;
import com.yan.refreshloadlayouttest.widget.PhoenixHeader;
import com.yan.refreshloadlayouttest.widget.fungame.FunGameBattleCityHeader;
import com.yan.refreshloadlayouttest.widget.fungame.FunGameHitBlockHeader;
import java.lang.ref.WeakReference;

import static android.content.ContentValues.TAG;

/**
 * A simple {@link Fragment} subclass.
 */
public class RefreshFragment extends Fragment {
  private PullRefreshLayout refreshLayout;
  private View root;

  public static RefreshFragment getInstance(int index) {
    RefreshFragment refreshFragment = new RefreshFragment();
    Bundle args = new Bundle();
    args.putInt("index", index);
    refreshFragment.setArguments(args);
    return refreshFragment;
  }

  public RefreshFragment() {
    // Required empty public constructor
  }

  @Override public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    if (root == null) {
      root = inflater.inflate(R.layout.fragment_refresh, container, false);
      init();
    }
    return root;
  }

  private void init() {
    refreshLayout = root.findViewById(R.id.refreshLayout);
    initHeader(refreshLayout);
    refreshLayout.setOnRefreshListener(new PullRefreshLayout.OnRefreshListenerAdapter() {
      @Override public void onRefresh() {
        Log.e(TAG, "refreshLayout onRefresh: ");
        refreshLayout.postDelayed(new Runnable() {
          @Override public void run() {
            if (refreshLayout != null) {
              refreshLayout.refreshComplete();
            }
          }
        }, 3000);
      }
    });
    setImages();
  }

  private void initHeader(PullRefreshLayout refreshLayout) {
    switch (getArguments().getInt("index")) {
      case 1:
        refreshLayout.setHeaderView(new PhoenixHeader(getContext(), refreshLayout));
        break;
      case 2:
        refreshLayout.setHeaderView(new FunGameHitBlockHeader(getContext(), refreshLayout));
        break;
      case 3:
        refreshLayout.setHeaderView(new FunGameBattleCityHeader(getContext(), refreshLayout));
        break;
    }
  }

  private void setImages() {
    ((ImageView) root.findViewById(R.id.iv1)).setImageDrawable(
        ContextCompat.getDrawable(getActivity().getApplicationContext(), R.drawable.img1));
    ((ImageView) root.findViewById(R.id.iv2)).setImageDrawable(
        ContextCompat.getDrawable(getContext().getApplicationContext(), R.drawable.img2));
    ((ImageView) root.findViewById(R.id.iv3)).setImageDrawable(
        ContextCompat.getDrawable(getContext().getApplicationContext(), R.drawable.img3));
    ((ImageView) root.findViewById(R.id.iv4)).setImageDrawable(
        ContextCompat.getDrawable(getContext().getApplicationContext(), R.drawable.img4));
    ((ImageView) root.findViewById(R.id.iv5)).setImageDrawable(
        ContextCompat.getDrawable(getContext().getApplicationContext(), R.drawable.img5));
    ((ImageView) root.findViewById(R.id.iv6)).setImageDrawable(
        ContextCompat.getDrawable(getContext().getApplicationContext(), R.drawable.img6));
    ((ImageView) root.findViewById(R.id.iv7)).setImageDrawable(
        ContextCompat.getDrawable(getContext().getApplicationContext(), R.drawable.loading_bg));
  }

  private void onLazyLoad() {
    refreshLayout.postDelayed(new Runnable() {
      @Override public void run() {
        if (refreshLayout != null) {
          refreshLayout.autoRefresh();
        }
      }
    }, 300);
  }

  /**
   * Lazy load
   */
  private boolean isLazyLoad = false;
  private boolean isActivityCreate = false;

  @Override public void setUserVisibleHint(boolean isVisibleToUser) {
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
    private WeakReference<RefreshFragment> reference;

    private LazyHandler(RefreshFragment refreshFragment) {
      reference = new WeakReference<>(refreshFragment);
    }

    @Override public void handleMessage(Message msg) {
      RefreshFragment rf = reference.get();
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

  @Override public void onActivityCreated(@Nullable Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);
    if (savedInstanceState != null) {
      isLazyLoad = savedInstanceState.getBoolean("isLazyLoad");
    }
    isActivityCreate = true;
  }

  @Override public void onSaveInstanceState(Bundle outState) {
    super.onSaveInstanceState(outState);
    outState.putBoolean("isLazyLoad", isLazyLoad);
  }

  @Override public void onDestroy() {
    super.onDestroy();
    refreshLayout = null;
    root = null;
  }
}
