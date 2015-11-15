package com.feibo.joke.view.adapter;

import android.app.Activity;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.feibo.joke.R;
import com.feibo.joke.model.Topic;
import com.feibo.joke.model.Video;
import com.feibo.joke.model.VideoTopicItem;
import com.feibo.joke.utils.StringUtil;
import com.feibo.joke.utils.UIUtil;
import com.feibo.joke.view.widget.VImageView;

import fbcore.widget.BaseSingleTypeAdapter;

/**
 * Create by：ml_bright on 2015/10/21 15:51
 * Email: 2504509903@qq.com
 */
public class VideoTopicAdapter extends BaseSingleTypeAdapter<VideoTopicItem> {

    private static final int TYPE_VIDEO = 0;
    private static final int TYPE_TOPIC = 1;

    private int width;

    public VideoTopicAdapter(Activity context) {
        super(context);

        init(context);
    }

    private void init(Activity context) {
        DisplayMetrics dm = new DisplayMetrics();
        context.getWindowManager().getDefaultDisplay().getMetrics(dm);
        width = dm.widthPixels / 2 - 30;
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public int getItemViewType(int position) {
        int type;
        if(getItem(position).isVideo()) {
            type = TYPE_VIDEO;
        } else {
            type = TYPE_TOPIC;
        }
        return type;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        int type = getItemViewType(i);
        if(type == TYPE_VIDEO) {
            return getVideoView(i, view, viewGroup);
        } else if(type == TYPE_TOPIC) {
            return getTopicView(i, view, viewGroup);
        }
        return null;
    }

    private View getTopicView(int i, View view, ViewGroup viewGroup) {
        TopicHolder holder;
        if(view == null) {
            view = LayoutInflater.from(mContext).inflate(R.layout.item_video_topic, null);

            holder = new TopicHolder();
            holder.img = (ImageView) view.findViewById(R.id.image);

            view.setTag(holder);
        } else {
            holder = (TopicHolder) view.getTag();
        }

        Topic item = getItem(i).topic;
        UIUtil.setImage(item.adImage.url, holder.img, R.drawable.default_video, R.drawable.default_video);
        return view;
    }

    public static class TopicHolder {
        public ImageView img;
    }

    public static class ViewHolder {
        public ImageView img;
        public VImageView author;
        public TextView title;
        public TextView like;
        public TextView play;
        public View parent;
    }

    public View getVideoView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(mContext);
            convertView = inflater.inflate(R.layout.item_video, null);
            holder = new ViewHolder();
            holder.parent = convertView.findViewById(R.id.video_layout);
            holder.author = (VImageView) convertView.findViewById(R.id.author_img);
            holder.img = (ImageView) convertView.findViewById(R.id.video_img);
            holder.like = (TextView) convertView.findViewById(R.id.like_count);
            holder.play = (TextView) convertView.findViewById(R.id.play_count);
            holder.title = (TextView) convertView.findViewById(R.id.video_title);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        final Video video = getItem(position).video;

        holder.play.setText(video.playCount+" 播放");
        holder.like.setText(video.beLikeCount+"");

        LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) holder.img.getLayoutParams();
        float height = (float)(width * video.thumbnail.height) / (float)video.thumbnail.width;
        lp.height = (int) height;
        holder.img.setLayoutParams(lp);


        holder.title.setText(StringUtil.isEmpty(video.desc) ? video.author.nickname : video.desc);

        UIUtil.setVAvatar(video.author.avatar, video.author.isSensation(), holder.author);
//        holder.img.setImageResource(R.drawable.default_video);
        UIUtil.setImage(video.thumbnail.url, holder.img, R.drawable.default_video, R.drawable.default_video);

        return convertView;
    }


}
