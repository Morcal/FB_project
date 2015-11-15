package com.feibo.joke.view.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import fbcore.widget.BaseSingleTypeAdapter;

import com.feibo.joke.R;
import com.feibo.joke.model.Feedback;
import com.feibo.joke.utils.UIUtil;
import com.feibo.joke.view.widget.VImageView;

/**
 * 意见反馈适配器 com.feibo.joke.view.group.setting.FeedbackAdapter
 * 
 * @author LinMW<br/>
 *         Creat at2015-4-14 下午1:44:16
 */
public class FeedbackAdapter extends BaseSingleTypeAdapter<Feedback> {

    private final int TYPE_SYSTEM = 0;
    private final int TYPE_USER = 1;

    public FeedbackAdapter(Context context) {
        super(context);
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public int getItemViewType(int position) {
        return getItem(position).type == Feedback.TYPE_SYSTEM ? TYPE_SYSTEM : TYPE_USER;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        int type = getItemViewType(position);
        int layout = type == TYPE_SYSTEM ? R.layout.item_feedback_system : R.layout.item_feedback_user;
        return getItem(convertView, position, layout, type);
    }

    public View getItem(View convertView, int position, int itemLayout, int type) {
        ItemHolder view = null;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(itemLayout, null);
            view = new ItemHolder();
            view.head = convertView.findViewById(R.id.iv_head);
            view.content = (TextView) convertView.findViewById(R.id.tv_say);
            convertView.setTag(view);
        } else {
            view = (ItemHolder) convertView.getTag();
        }
        Feedback fb = getItem(position);
        
        if (type == TYPE_USER) {
            if(fb.author == null) {
                ((VImageView)view.head).getImageView().setImageResource(R.drawable.default_avatar);
                ((VImageView)view.head).showSensation(false);
            } else {
                UIUtil.setVAvatar(fb.author.avatar, fb.author.isSensation(), ((VImageView)view.head));
            }
        }
        view.content.setText(fb.content);
        return convertView;
    }

    public static class ItemHolder {
        View head;
        TextView content;
    }

}
