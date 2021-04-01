package com.yan.pullrefreshlayout;

import android.content.Context;
import androidx.core.widget.ListViewCompat;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;
import android.view.WindowManager;
import android.widget.ListView;

/**
 * @author yanxianwei
 * @date 2017/5/21
 */
public final class PRLCommonUtils {

  /**
   * code from SwipeRefreshLayout
   *
   * @return Whether it is possible for the child view of this layout to
   * scroll up. Override this if the child view is a custom view.
   */
  public static boolean canChildScrollUp(View targetView) {
    if (targetView == null) {
      return false;
    }
    if (targetView instanceof ListView) {
      return ListViewCompat.canScrollList((ListView) targetView, -1);
    }
    return targetView.canScrollVertically(-1);
  }

  /**
   * @return Whether it is possible for the child view of this layout to
   * scroll down. Override this if the child view is a custom view.
   */
  public static boolean canChildScrollDown(View targetView) {
    if (targetView == null) {
      return false;
    }
    if (targetView instanceof ListView) {
      return ListViewCompat.canScrollList((ListView) targetView, 1);
    }
    return targetView.canScrollVertically(1);
  }

  /**
   * common utils
   */
  static int getWindowHeight(Context context) {
    DisplayMetrics displayMetrics = new DisplayMetrics();
    WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
    if (windowManager != null) {
      windowManager.getDefaultDisplay().getMetrics(displayMetrics);
    }
    return displayMetrics.heightPixels;
  }

  public static int dipToPx(Context context, float value) {
    DisplayMetrics metrics = context.getResources().getDisplayMetrics();
    return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, value, metrics);
  }
}