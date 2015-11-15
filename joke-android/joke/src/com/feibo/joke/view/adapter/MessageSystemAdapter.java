package com.feibo.joke.view.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import fbcore.widget.BaseSingleTypeAdapter;

import com.feibo.joke.R;
import com.feibo.joke.model.Message;
import com.feibo.joke.utils.TimeUtil;

public class MessageSystemAdapter extends BaseSingleTypeAdapter<Message>{

    public MessageSystemAdapter(Context context) {
        super(context);
    }

    public static class Holder {
        protected ImageView avatar;
        protected TextView content;
        protected TextView time;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Holder h = null;
        if(convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(mContext);
            convertView = inflater.inflate(R.layout.item_notice, null);
            h = new Holder();
            h.avatar = (ImageView) convertView.findViewById(R.id.item_avatar);
            h.content = (TextView) convertView.findViewById(R.id.item_content);
            h.time = (TextView) convertView.findViewById(R.id.item_time);
            convertView.setTag(h);
        } else {
            h = (Holder) convertView.getTag();
        }

        Message m = getItem(position);
        h.content.setText(m.content);
        h.time.setText(TimeUtil.transformTime(m.publishTime));
        return convertView;
    }

}
