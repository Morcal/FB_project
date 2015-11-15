package com.feibo.snacks.view.module.home.category;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.feibo.snacks.R;
import com.feibo.snacks.model.bean.SubClassify;

import fbcore.widget.BaseSingleTypeAdapter;

public class CategoryAdapter extends BaseSingleTypeAdapter<SubClassify> {

    private int pos;

    public CategoryAdapter(Context context) {
        super(context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_list_category, null);
            ViewHolder holder = new ViewHolder();
            holder.view = convertView.findViewById(R.id.title_back);
            holder.title = (TextView) convertView.findViewById(R.id.item_list_category_title);
            convertView.setTag(holder);
        }
        ViewHolder holder = (ViewHolder) convertView.getTag();
        holder.title.setText(getItem(position).title);
        if (position == pos) {
            holder.title.setTextColor(mContext.getResources().getColor(R.color.white));
            holder.view.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.bg_item_list_category_click));
        } else {
            holder.title.setTextColor(mContext.getResources().getColor(R.color.c8));
            holder.view.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.bg_item_list_catrgory_nor));
        }
        return convertView;
    }

    public void setSelectPos(int pos) {
        this.pos = pos;
        notifyDataSetChanged();
    }

    public class ViewHolder {
        TextView title;
        View view;
    }
}
