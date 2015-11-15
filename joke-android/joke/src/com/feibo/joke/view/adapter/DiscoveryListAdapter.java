package com.feibo.joke.view.adapter;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import fbcore.log.LogUtil;
import fbcore.widget.BaseSingleTypeAdapter;

import com.feibo.joke.R;
import com.feibo.joke.app.UmengConstant;
import com.feibo.joke.model.DiscoveryTopicItem;
import com.feibo.joke.utils.StringUtil;
import com.feibo.joke.utils.UIUtil;
import com.feibo.joke.view.module.home.VideoTopicFragment2;
import com.feibo.joke.view.module.video.VideoDetailFragment;
import com.feibo.joke.view.util.LaunchUtil;
import com.feibo.joke.view.widget.DiscoveryTopicContentGroup;
import com.feibo.joke.view.widget.DiscoveryTopicContentGroup.IItemClickListener;
import com.umeng.analytics.MobclickAgent;

public class DiscoveryListAdapter extends BaseSingleTypeAdapter<DiscoveryTopicItem> {

    //这里一定要注意使用getItemViewType 要从0开始计数
    private final int TYPE_ONE_LINE = 0;
    private final int TYPE_TWO_LINE = 1;

    public DiscoveryListAdapter(Context context) {
        super(context);
    }

    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public int getItemViewType(int position) {
        DiscoveryTopicItem item = getItem(position);
        if (item.videos != null && item.videos.size() > 2) {
            return TYPE_TWO_LINE;
        }
        return TYPE_ONE_LINE;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHodler holder;
        final DiscoveryTopicItem item = getItem(position);
        if (convertView == null) {
            int type = getItemViewType(position);
            if (type == TYPE_TWO_LINE) {
                convertView = LayoutInflater.from(mContext).inflate(R.layout.item_discovery_multiple, null);
            } else {
                convertView = LayoutInflater.from(mContext).inflate(R.layout.item_discovery_single, null);
            }
            holder = new ViewHodler();
            holder.head = convertView.findViewById(R.id.topic_title);
            holder.topicImg = (ImageView) convertView.findViewById(R.id.titleImg);
            holder.topicTitle = (TextView) convertView.findViewById(R.id.title);
            holder.group = (DiscoveryTopicContentGroup) convertView.findViewById(R.id.content);
            convertView.setTag(holder);
        } else {
            holder = (ViewHodler) convertView.getTag();
        }
        if (StringUtil.isEmpty(item.topic.thumbnail.url)) {
            holder.topicImg.setImageResource(R.drawable.default_avatar);
        } else {
            UIUtil.setImage(item.topic.thumbnail.url, holder.topicImg, R.drawable.default_avatar, R.drawable.default_avatar);
        }
        holder.topicTitle.setText(item.topic.title);
        if (item.videos == null || item.videos.size() == 0) {
            holder.group.setVisibility(View.GONE);
        } else {
            holder.group.setVisibility(View.VISIBLE);
            holder.group.setView(item.videos);
        }

        holder.group.setOnItemClickListener(new IItemClickListener() {
            @Override
            public void onClickItem(int videoPosition, long videoId) {
                Bundle bundle = VideoDetailFragment.buildBundle(videoId, position, videoPosition);
                LogUtil.i("onClickItem=======DiscoveryListAdapter", "position=" + position + ";videoPosition=" + videoPosition);
                LaunchUtil.launchSubActivity(mContext, VideoDetailFragment.class, bundle);


                MobclickAgent.onEvent(mContext, UmengConstant.DISCOVERY_VIDEO);
            }
        });
        holder.head.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
//                Bundle b = VideoTopicFragment.buildBundle(item.topic.id, item.topic.title);
//                LaunchUtil.launchSubActivity(mContext, VideoTopicFragment.class, b);

                Bundle b = VideoTopicFragment2.buildBundle(item.topic.id, item.topic.oriImage, item.topic.title);
                LaunchUtil.launchSubActivity(mContext, VideoTopicFragment2.class, b);
                MobclickAgent.onEvent(mContext, UmengConstant.DISCOVERY_TOPIC);
            }
        });
        return convertView;
    }

    public static class ViewHodler {
        public View head;
        public ImageView topicImg;
        public TextView topicTitle;
        public DiscoveryTopicContentGroup group;
    }


}
