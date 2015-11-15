package com.feibo.snacks.view.module.goods.goodsdetail;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.feibo.snacks.R;
import com.feibo.snacks.model.bean.Comment;
import com.feibo.snacks.view.util.UIUtil;

import fbcore.widget.BaseSingleTypeAdapter;

@SuppressLint("InflateParams")
public class CommentAdapter extends BaseSingleTypeAdapter<Comment> {

    public CommentAdapter(Context context) {
        super(context);
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {
        ViewHolder holder = null;
        if(view == null) {
            view = LayoutInflater.from(mContext).inflate(R.layout.item_comment, null);
            holder = new ViewHolder();
            holder.icon = (ImageView) view.findViewById(R.id.item_comment_icon);
            holder.desc = (TextView) view.findViewById(R.id.expandable_text);
            holder.name = (TextView) view.findViewById(R.id.item_comment_name);
            view.setTag(holder);
        }
        holder = (ViewHolder) view.getTag();
        Comment item = getItem(position);
        if (item != null) {
            UIUtil.setDefaultCommentImage(item.avatar.imgUrl, holder.icon);
            holder.desc.setText(item.content);
            holder.name.setText(item.nickname);
        }
        return view;
    }

    public static class ViewHolder{
        public ImageView icon;
        public TextView name;
        public TextView desc;
    }
}
