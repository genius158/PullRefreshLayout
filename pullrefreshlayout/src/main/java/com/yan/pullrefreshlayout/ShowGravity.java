package com.yan.pullrefreshlayout;

import android.view.ViewGroup;
import androidx.annotation.IntDef;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * refresh show helper
 *
 * @author yanxianwei
 * @date 2017/7/7
 */
public final class ShowGravity {

  /**
   * @ShowState
   */
  @IntDef({
      FOLLOW, FOLLOW_PLACEHOLDER, FOLLOW_CENTER, PLACEHOLDER, PLACEHOLDER_FOLLOW,
      PLACEHOLDER_CENTER, CENTER, CENTER_FOLLOW
  }) @Retention(RetentionPolicy.SOURCE) public @interface ShowState {
  }

  public static final int FOLLOW = 0;
  public static final int FOLLOW_PLACEHOLDER = 1;
  public static final int FOLLOW_CENTER = 2;
  public static final int PLACEHOLDER = 3;
  public static final int PLACEHOLDER_FOLLOW = 4;
  public static final int PLACEHOLDER_CENTER = 5;
  public static final int CENTER = 6;
  public static final int CENTER_FOLLOW = 7;

  /**
   * show gravity
   * - use by pullRefreshLayout to set show gravity
   */
  int headerShowGravity = FOLLOW;
  int footerShowGravity = FOLLOW;

  private PullRefreshLayout prl;

  ShowGravity(PullRefreshLayout pullRefreshLayout) {
    this.prl = pullRefreshLayout;
  }

  void dellHeaderMoving(int moveDistance) {
    if (prl.headerView != null && moveDistance >= 0) {
      switch (headerShowGravity) {
        case FOLLOW:
          prl.headerView.setTranslationY(moveDistance);
          break;
        case FOLLOW_PLACEHOLDER:
          prl.headerView.setTranslationY(moveDistance <= prl.refreshTriggerDistance ? moveDistance
              : prl.refreshTriggerDistance);
          break;
        case FOLLOW_CENTER:
          prl.headerView.setTranslationY(moveDistance <= prl.refreshTriggerDistance ? moveDistance
              : prl.refreshTriggerDistance + (moveDistance - prl.refreshTriggerDistance) / 2);
          break;
        case PLACEHOLDER_CENTER:
          prl.headerView.setTranslationY(moveDistance <= prl.refreshTriggerDistance ? 0
              : (moveDistance - prl.refreshTriggerDistance) / 2);
          break;
        case PLACEHOLDER_FOLLOW:
          prl.headerView.setTranslationY(moveDistance <= prl.refreshTriggerDistance ? 0
              : moveDistance - prl.refreshTriggerDistance);
          break;
        case CENTER:
          prl.headerView.setTranslationY(moveDistance / 2);
          break;
        case CENTER_FOLLOW:
          prl.headerView.setTranslationY(
              moveDistance <= prl.refreshTriggerDistance ? moveDistance / 2
                  : moveDistance - prl.refreshTriggerDistance / 2);
          break;
        default:
      }
    }
  }

  void dellFooterMoving(int moveDistance) {
    if (prl.footerView != null && moveDistance <= 0) {
      switch (footerShowGravity) {
        case FOLLOW:
          prl.footerView.setTranslationY(moveDistance);
          break;
        case FOLLOW_PLACEHOLDER:
          prl.footerView.setTranslationY(
              moveDistance >= -prl.loadTriggerDistance ? moveDistance : -prl.loadTriggerDistance);
          break;
        case FOLLOW_CENTER:
          prl.footerView.setTranslationY(
              moveDistance <= -prl.loadTriggerDistance ? -prl.loadTriggerDistance
                  + (prl.loadTriggerDistance + moveDistance) / 2 : moveDistance);
          break;
        case PLACEHOLDER_CENTER:
          prl.footerView.setTranslationY(
              moveDistance <= -prl.loadTriggerDistance ? (moveDistance + prl.loadTriggerDistance)
                  / 2 : 0);
          break;
        case PLACEHOLDER_FOLLOW:
          prl.footerView.setTranslationY(
              moveDistance <= -prl.loadTriggerDistance ? moveDistance + prl.loadTriggerDistance
                  : 0);
          break;
        case CENTER:
          prl.footerView.setTranslationY(moveDistance / 2);
          break;
        case CENTER_FOLLOW:
          prl.footerView.setTranslationY(
              moveDistance <= -prl.loadTriggerDistance ? moveDistance + prl.loadTriggerDistance / 2
                  : moveDistance / 2);
          break;
        default:
      }
    }
  }

  void layout(int left, int top, int right, int bottom) {
    if (prl.headerView != null) {
      int paddingLeft = prl.getPaddingLeft();
      int paddingTop = prl.getPaddingTop();
      ViewGroup.MarginLayoutParams lp =
          (ViewGroup.MarginLayoutParams) prl.headerView.getLayoutParams();
      switch (headerShowGravity) {
        case FOLLOW:
        case FOLLOW_PLACEHOLDER:
        case FOLLOW_CENTER:
          prl.headerView.layout(paddingLeft + lp.leftMargin,
              top + lp.topMargin + paddingTop - prl.headerView.getMeasuredHeight(),
              paddingLeft + lp.leftMargin + prl.headerView.getMeasuredWidth(),
              top + lp.topMargin + paddingTop);
          break;
        case PLACEHOLDER:
        case PLACEHOLDER_CENTER:
        case PLACEHOLDER_FOLLOW:
          prl.headerView.layout(paddingLeft + lp.leftMargin, top + paddingTop + lp.topMargin,
              paddingLeft + lp.leftMargin + prl.headerView.getMeasuredWidth(),
              top + paddingTop + lp.topMargin + prl.headerView.getMeasuredHeight());
          break;
        case CENTER:
        case CENTER_FOLLOW:
          prl.headerView.layout(paddingLeft + lp.leftMargin,
              top + paddingTop - prl.headerView.getMeasuredHeight() / 2,
              paddingLeft + lp.leftMargin + prl.headerView.getMeasuredWidth(),
              top + paddingTop + prl.headerView.getMeasuredHeight() / 2);
          break;
        default:
      }
    }
    if (prl.footerView != null) {
      int paddingLeft = prl.getPaddingLeft();
      int paddingBottom = prl.getPaddingBottom();
      ViewGroup.MarginLayoutParams lp =
          (ViewGroup.MarginLayoutParams) prl.footerView.getLayoutParams();
      switch (footerShowGravity) {
        case FOLLOW:
        case FOLLOW_PLACEHOLDER:
        case FOLLOW_CENTER:
          prl.footerView.layout(lp.leftMargin + paddingLeft, bottom - lp.topMargin - paddingBottom,
              lp.leftMargin + paddingLeft + prl.footerView.getMeasuredWidth(),
              bottom - lp.topMargin - paddingBottom + prl.footerView.getMeasuredHeight());
          break;
        case PLACEHOLDER:
        case PLACEHOLDER_CENTER:
        case PLACEHOLDER_FOLLOW:
          prl.footerView.layout(lp.leftMargin + paddingLeft,
              bottom - lp.bottomMargin - paddingBottom - prl.footerView.getMeasuredHeight(),
              lp.leftMargin + paddingLeft + prl.footerView.getMeasuredWidth(),
              bottom - lp.bottomMargin - paddingBottom);
          break;
        case CENTER:
        case CENTER_FOLLOW:
          prl.footerView.layout(lp.leftMargin + paddingLeft,
              bottom - paddingBottom - prl.footerView.getMeasuredHeight() / 2,
              lp.leftMargin + paddingLeft + prl.footerView.getMeasuredWidth(),
              bottom - paddingBottom + prl.footerView.getMeasuredHeight() / 2);
          break;
        default:
      }
    }
  }
}
