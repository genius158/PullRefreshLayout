package com.yan.refreshloadlayouttest.testactivity;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;
import com.yan.refreshloadlayouttest.R;

/**
 * Created by yan on 2017/8/10.
 */

public class SimpleViewHolder extends RecyclerView.ViewHolder {

  public   TextView tv;
    public  ImageView iv;

    public SimpleViewHolder(View view) {
        super(view);
        tv = (TextView) view.findViewById(R.id.id_num);
        iv = (ImageView) view.findViewById(R.id.iv);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(v.getContext().getApplicationContext(), "you just touched me", android.widget.Toast.LENGTH_SHORT).show();
            }
        });

        view.setOnLongClickListener(new View.OnLongClickListener() {
          @Override public boolean onLongClick(View v) {
            Toast.makeText(v.getContext(), "setOnLongClickListener   -- " + v, Toast.LENGTH_LONG)
                .show();
            return true;
          }
        });
    }
}