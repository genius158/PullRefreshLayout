package com.yan.refreshloadlayouttest.testactivity;

import android.content.Context;
import androidx.core.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.yan.refreshloadlayouttest.R;

import java.util.List;

/**
 * Created by yan on 2017/8/10.
 */

public class SimpleListAdapter extends BaseAdapter {
    private Context context;
    private List<SimpleItem> simpleItems;

    public SimpleListAdapter(Context context, List<SimpleItem> simpleItems) {
        this.context = context.getApplicationContext();
        this.simpleItems = simpleItems;
    }

    @Override
    public int getCount() {
        return simpleItems.size();
    }


    @Override
    public Object getItem(int position) {
        return simpleItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        SimpleViewHolder viewHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.simple_item, parent, false);
            convertView.setTag(viewHolder = new SimpleViewHolder(convertView));
        } else {
            viewHolder = (SimpleViewHolder) convertView.getTag();
        }
        viewHolder.tv.setText(simpleItems.get(position).title);
        viewHolder.iv.setImageDrawable(ContextCompat.getDrawable(context, simpleItems.get(position).resId));
        return convertView;
    }
}
