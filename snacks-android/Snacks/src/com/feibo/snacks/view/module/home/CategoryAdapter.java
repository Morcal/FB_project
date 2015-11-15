package com.feibo.snacks.view.module.home;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.feibo.snacks.R;
import com.feibo.snacks.model.bean.Classify;
import com.feibo.snacks.view.util.UIUtil;

import fbcore.widget.BaseSingleTypeAdapter;

@SuppressLint("InflateParams")
public class CategoryAdapter extends BaseSingleTypeAdapter<Classify> {

    public CategoryAdapter(Context context) {
        super(context);
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
    	ViewHolder holder = null;
    	if(view == null) {
    		holder = new ViewHolder();
    		view = LayoutInflater.from(mContext).inflate(R.layout.item_home_category, null);
    		holder.icon = (ImageView) view.findViewById(R.id.item_home_category_icon);
    		holder.title = (TextView) view.findViewById(R.id.item_home_category_title);
            holder.desc = (TextView) view.findViewById(R.id.item_home_category_desc);
    		view.setTag(holder);
    	} else {
    		holder = (ViewHolder) view.getTag();
    	}
    	
        Classify classify = getItem(position);
        if (classify != null) {
            UIUtil.setDefaultHomeCategoryImage(classify.img.imgUrl, holder.icon);
            holder.title.setText(classify.title);
            holder.desc.setText(classify.desc);
        }
        return view;
    }

    private static class ViewHolder {
    	private ImageView icon;
    	private TextView title;
        private TextView desc;
    }
}
