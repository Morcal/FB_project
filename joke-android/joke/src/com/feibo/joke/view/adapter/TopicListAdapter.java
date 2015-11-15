package com.feibo.joke.view.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import fbcore.widget.BaseSingleTypeAdapter;

import com.feibo.joke.R;
import com.feibo.joke.model.Topic;
import com.feibo.joke.utils.UIUtil;

public class TopicListAdapter extends BaseSingleTypeAdapter<Topic> {

    public TopicListAdapter(Context context) {
        super(context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        
        ViewHolder holder = null;
        if(convertView == null) { 
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_topic, null);
            holder = new ViewHolder();
            holder.img = (ImageView) convertView.findViewById(R.id.img);
            holder.title = (TextView) convertView.findViewById(R.id.title);
            holder.playCount = (TextView) convertView.findViewById(R.id.play_count);
            holder.worksCount = (TextView) convertView.findViewById(R.id.works);
            convertView.setTag(holder);
        } else { 
            holder = (ViewHolder) convertView.getTag();
        }
        Topic topic = getItem(position);
        UIUtil.setImage(topic.thumbnail.url, holder.img, R.drawable.default_video, R.drawable.default_video);
        holder.playCount.setText(String.valueOf(topic.playCount));
        holder.title.setText(topic.title);
        holder.worksCount.setText("作品数:" + String.valueOf(topic.worksCount));
        return convertView;
    }
    
    public void setRead(int position) {
        Topic t = getItem(position);
        t.playCount++;
    }
    
    public static class ViewHolder {
        public ImageView img;
        public TextView title;
        public TextView worksCount;
        public TextView playCount;
    }

}
