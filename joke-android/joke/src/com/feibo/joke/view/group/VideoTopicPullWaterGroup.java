package com.feibo.joke.view.group;

import android.content.Context;
import android.os.Bundle;
import android.view.View;

import com.feibo.joke.app.UmengConstant;
import com.feibo.joke.model.VideoTopicItem;
import com.feibo.joke.view.module.home.VideoTopicFragment2;
import com.feibo.joke.view.module.video.VideoDetailFragment;
import com.feibo.joke.view.util.LaunchUtil;
import com.feibo.joke.view.widget.waterpull.lib.internal.PLA_AdapterView;
import com.feibo.joke.view.widget.waterpull.lib.internal.PLA_AdapterView.OnItemClickListener;
import com.umeng.analytics.MobclickAgent;

import fbcore.log.LogUtil;
import fbcore.widget.BaseSingleTypeAdapter;

public class VideoTopicPullWaterGroup extends AbsPullWaterGroup<VideoTopicItem> implements OnItemClickListener{

    public VideoTopicPullWaterGroup(Context context) {
        super(context);
        init();
    }

    private void init() {
        setOnItemClickListener(this);
    }

    @Override
    public void setListAdapter(BaseSingleTypeAdapter<VideoTopicItem> adapter) {
        super.setListAdapter(adapter);
    }

    @Override
    public void onItemClick(PLA_AdapterView<?> parent, View view, int position, long id) {
        VideoTopicItem item = getListAdapter().getItem(position);
        if(item.isVideo()) {
            Bundle bundle= VideoDetailFragment.buildBundle(item.getId(), position);
            LogUtil.i("onItemClick=======BasePullWater", "position="+(position)+";videoPosition="+0);
            LaunchUtil.launchSubActivity(getContext(), VideoDetailFragment.class, bundle);

            MobclickAgent.onEvent(getContext(), UmengConstant.HANDPICK_VIDEO);
        } else {
            Bundle b = VideoTopicFragment2.buildBundle(item.topic.id, item.topic.oriImage, item.topic.title);
            LaunchUtil.launchSubActivity(getContext(), VideoTopicFragment2.class, b);

            MobclickAgent.onEvent(getContext(), UmengConstant.HANDPICK_TOPIC);
        }
    }

}
