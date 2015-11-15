package com.feibo.joke.view.adapter;

import android.app.Activity;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

import fbcore.widget.BaseSingleTypeAdapter;

import com.feibo.joke.R;
import com.feibo.joke.model.Video;
import com.feibo.joke.utils.StringUtil;
import com.feibo.joke.utils.UIUtil;
import com.feibo.joke.view.widget.VImageView;

public class VideoListAdapter extends BaseSingleTypeAdapter<Video> {

    public static final int TYPE_DEFAULT = 0;
    public static final int TYPE_MINE = 1;
    
    private int type;
    private int width;
    
    public VideoListAdapter(Activity context) {
        this(context, TYPE_DEFAULT);
    }

    public VideoListAdapter(Activity context, int type) {
        super(context);
        this.type = type;
        init(context);
    }
    
    private void init(Activity context) {
        DisplayMetrics dm = new DisplayMetrics();
        context.getWindowManager().getDefaultDisplay().getMetrics(dm);
        width = dm.widthPixels / 2 - 30;
    }

    public static class ViewHodler {
        public ImageView img;
        public VImageView author;
        public TextView title;
        public TextView like;
        public TextView play;
        public View parent;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHodler holder = null;
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(mContext);
            convertView = inflater.inflate(R.layout.item_video, null);
            holder = new ViewHodler();
            holder.parent = convertView.findViewById(R.id.video_layout);
            holder.author = (VImageView) convertView.findViewById(R.id.author_img);
            holder.img = (ImageView) convertView.findViewById(R.id.video_img);
            holder.like = (TextView) convertView.findViewById(R.id.like_count);
            holder.play = (TextView) convertView.findViewById(R.id.play_count);
            holder.title = (TextView) convertView.findViewById(R.id.video_title);
            if(type == TYPE_MINE) {
                holder.author.setVisibility(View.GONE);
            }
            convertView.setTag(holder);
        } else {
            holder = (ViewHodler) convertView.getTag();
        }
            
        final Video video = getItem(position);
        
        holder.play.setText(video.playCount+" 播放");
        holder.like.setText(video.beLikeCount+"");

        LayoutParams lp = (LayoutParams) holder.img.getLayoutParams();
        float height = (float)(width * video.thumbnail.height) / (float)video.thumbnail.width;
        lp.height = (int) height;
        holder.img.setLayoutParams(lp);
        
//        holder.img.setImageResource(R.drawable.default_video);
        UIUtil.setImage(video.thumbnail.url, holder.img, R.drawable.default_video, R.drawable.default_video);

        holder.title.setText(StringUtil.isEmpty(video.desc) ? video.author.nickname : video.desc);
        
        if(type != TYPE_MINE) {
            UIUtil.setVAvatar(video.author.avatar, video.author.isSensation(), holder.author);
        }
        
        return convertView;
    }
    
}
