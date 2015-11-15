package com.feibo.joke.view.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.feibo.joke.R;

import java.util.List;

import fbcore.widget.BaseSingleTypeAdapter;

/**
 * Created by Administrator on 2015/11/6.
 */
public class SearchListAdapter extends BaseSingleTypeAdapter<String> {

    public SearchListAdapter(Context context) {
        super(context);
    }

    @Override
    public View getView(int i, View convertView, ViewGroup viewGroup) {
       ViewHolder viewHolder = null;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_list_search,null);
            viewHolder=new ViewHolder();
            viewHolder.textView = (TextView) convertView.findViewById(R.id.search_text);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.textView.setText(getItem(i));
        return convertView;
    }
    public static class ViewHolder{
        private TextView textView;
    }
}
