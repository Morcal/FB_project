package com.feibo.snacks.view.module.person.setting;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.feibo.snacks.R;
import com.feibo.snacks.model.bean.Feedback;
import com.feibo.snacks.view.util.UIUtil;

import fbcore.widget.BaseSingleTypeAdapter;

public class FeedbackAdapeter extends BaseSingleTypeAdapter<Feedback> {

    private static final int OFFICIAL = 0;
    private static final int USER = 1;

    public FeedbackAdapeter(Context context) {
        super(context);
    }

    @Override
    public int getItemViewType(int position) {
        return getItem(position).isOfficial() ? OFFICIAL : USER;
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            if (getItemViewType(position) == OFFICIAL) {
                convertView = LayoutInflater.from(mContext).inflate(R.layout.item_feedback_official,parent,false);
            } else {
                convertView = LayoutInflater.from(mContext).inflate(R.layout.item_feedback_user, parent,false);
            }
            holder = new ViewHolder();
            holder.avatar = (ImageView) convertView.findViewById(R.id.item_feedback_icon);
//            holder.avatar.setImageResource(R.drawable.icon_feedback_logo);
            holder.content = (TextView) convertView.findViewById(R.id.item_feedback_content);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Feedback feedback = getItem(position);
        if (feedback != null) {
            holder.content.setText(feedback.content);
            holder.content.setTextIsSelectable(true);
            if (!feedback.isOfficial()) {
                UIUtil.setDefaultFeedbackImage(feedback.icon() , holder.avatar);
            }
        }
        return convertView;
    }

    private class ViewHolder {
        ImageView avatar;
        TextView content;
    }
}
