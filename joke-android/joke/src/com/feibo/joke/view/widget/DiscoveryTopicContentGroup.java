package com.feibo.joke.view.widget;

import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.feibo.joke.R;
import com.feibo.joke.model.Video;
import com.feibo.joke.utils.UIUtil;

public class DiscoveryTopicContentGroup extends LinearLayout {

    private View layoutMore;
    private ViewHolder[] items;
    
    private boolean multipleLine;
    
    private View[] itemLayouts;
    
    private List<Video> videos;
    
    private IItemClickListener listener;
    
    public DiscoveryTopicContentGroup(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs, 0);
    }

    @SuppressLint("NewApi")
    public DiscoveryTopicContentGroup(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs, defStyle);
    }

    private void init(Context context, AttributeSet attrs, int defStyle) {
        TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.DiscoveryTopicContentGroup, defStyle, 0);;
        boolean singleLine = a.getBoolean(R.styleable.DiscoveryTopicContentGroup_singleLine, true);
        multipleLine = !singleLine;
        a.recycle();
        
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View root = inflater.inflate(R.layout.item_discovery_content, null);
        initWidget(root);
        initListener();
        attachViewToParent(root, 0, new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
    }

    private void initListener() {
        for(int i=0; i<itemLayouts.length; i++) {
            View v = itemLayouts[i];
            final int p = i;
            v.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(listener != null && videos != null) {
                        listener.onClickItem(p, videos.get(p).id);
                    }
                }
            });
        }
    }
    
    public void setOnItemClickListener(IItemClickListener listener) {
        this.listener = listener;
    }

    private void initWidget(View root) {
        itemLayouts = new View[multipleLine ? 4 : 2];
        items = new ViewHolder[multipleLine ? 4 : 2];

        itemLayouts[0] = root.findViewById(R.id.item_topic1);
        itemLayouts[1] = root.findViewById(R.id.item_topic2);
        items[0] = getViewHodler(itemLayouts[0]);
        items[1] = getViewHodler(itemLayouts[1]);
        
        if(multipleLine) {
            itemLayouts[2] = root.findViewById(R.id.item_topic3);
            itemLayouts[3] = root.findViewById(R.id.item_topic4);
            items[2] = getViewHodler(itemLayouts[2]);
            items[3] = getViewHodler(itemLayouts[3]);
        }
        layoutMore = root.findViewById(R.id.layout_more);
        layoutMore.setVisibility(multipleLine ? View.VISIBLE : View.GONE);
        
    }
    
    private ViewHolder getViewHodler(View root) {
        ViewHolder h = new ViewHolder();
        h.img = (ImageView) root.findViewById(R.id.img);
        h.like = (TextView) root.findViewById(R.id.like_count);
        h.play = (TextView) root.findViewById(R.id.play_count);
        h.avatar = (VImageView) root.findViewById(R.id.author_img);
        h.title = (TextView) root.findViewById(R.id.video_title);
        return h;
    }
    
    private void setVideoView(ViewHolder view, Video video) {
        UIUtil.setImage(video.thumbnail.url, view.img, R.drawable.default_video, R.drawable.default_video);
        UIUtil.setVAvatar(video.author.avatar, video.author.isSensation(), view.avatar);
        
        view.like.setText(String.valueOf(video.beLikeCount));
        view.play.setText(String.valueOf(video.playCount) + " 播放");
        view.title.setText(video.desc == null ? "NULL" : video.desc);
    }

    public void setView(List<Video> videos) {
        if(videos == null) {
            return;
        }
        this.videos = videos;
        for(int i=0; i<itemLayouts.length; i++) {
            if(i < videos.size()) {
                setVideoView(items[i], videos.get(i));
            } else {
                itemLayouts[i].setVisibility(View.INVISIBLE);
            }
        }
    }
    
    public interface IItemClickListener {
        public void onClickItem(int position, long videoId);
    }
    
    public static class ViewHolder {
        public ImageView img;
        public TextView like;
        public TextView play;
        public VImageView avatar;
        public TextView title;
    }

}
