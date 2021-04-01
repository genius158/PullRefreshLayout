package com.yan.refreshloadlayouttest.testactivity;

import android.content.Context;
import androidx.core.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.Toast;
import androidx.recyclerview.widget.RecyclerView;
import com.yan.refreshloadlayouttest.R;

import java.util.List;

/**
 * Created by yan on 2017/8/10.
 */
public class SimpleAdapter extends RecyclerView.Adapter<SimpleViewHolder> {
  /**
   * Item 点击事件监听的回调
   */
  public interface OnItemClickListener {
    void onItemClick(View view, int position);

    void onItemLongClick(View view, int position);
  }

  private OnItemClickListener mOnItemClickListener;

  public void setOnItemClickLitener(OnItemClickListener mOnItemClickListener) {
    this.mOnItemClickListener = mOnItemClickListener;
  }

  private Context context;
  private List<SimpleItem> datas;

  public SimpleAdapter(Context context, List<SimpleItem> datas) {
    this.context = context.getApplicationContext();
    this.datas = datas;
  }

  @Override
  public SimpleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    SimpleViewHolder holder = new SimpleViewHolder(LayoutInflater.from(context)
        .inflate(R.layout.simple_item, parent, false));
    return holder;
  }

  @Override
  public void onBindViewHolder(final SimpleViewHolder holder, final int position) {
    holder.tv.setText(datas.get(position).title);
    holder.iv.setImageDrawable(ContextCompat.getDrawable(context, datas.get(position).resId));

    if (mOnItemClickListener != null) {
      holder.itemView.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
          int pos = holder.getLayoutPosition();
          mOnItemClickListener.onItemClick(holder.itemView, pos);
        }
      });

      holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
        @Override
        public boolean onLongClick(View v) {
          int pos = holder.getLayoutPosition();
          mOnItemClickListener.onItemLongClick(holder.itemView, pos);
          Toast.makeText(v.getContext(), "setOnLongClickListener   -- " + pos, Toast.LENGTH_LONG)
              .show();
          return false;
        }
      });
    }
  }

  @Override
  public int getItemCount() {
    return datas.size();
  }
}
