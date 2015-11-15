package com.feibo.joke.view.module.home;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.feibo.joke.R;
import com.feibo.joke.manager.list.VideosTopicManager;
import com.feibo.joke.view.BaseTitleFragment;
import com.feibo.joke.view.adapter.VideoListAdapter;
import com.feibo.joke.view.group.BasePullWaterGroup;
import com.feibo.joke.view.group.GroupConfig;

@Deprecated
public class VideoTopicFragment extends BaseTitleFragment{
    
    private static final String BUNDLE_KEY_TOPIC_ID = "topic_id";
    private static final String BUNDLE_KEY_TOPIC_NAME = "topic_name";

    private VideoListAdapter adapter;
    private BasePullWaterGroup group;
    private VideosTopicManager manager;

    private long topicId;
    private String topicName;
    
    public static Bundle buildBundle(long topicId, String topicName) {
        Bundle args = new Bundle();
        args.putLong(BUNDLE_KEY_TOPIC_ID, topicId);
        args.putString(BUNDLE_KEY_TOPIC_NAME, topicName);
        return args;
    }
    
    @SuppressLint("NewApi")
    @Override
    public View containChildView() {
        Bundle bundle = getActivity().getIntent().getExtras();
        topicId = bundle.getLong(BUNDLE_KEY_TOPIC_ID, 0);
        topicName = bundle.getString(BUNDLE_KEY_TOPIC_NAME, "更多话题视频列表");
        
        adapter = new VideoListAdapter(getActivity());
        manager = new VideosTopicManager(topicId);
        group = new BasePullWaterGroup(getActivity());
        group.setListAdapter(adapter);
        group.setListManager(manager);
        group.setGroupConfig(GroupConfig.create(GroupConfig.GROUP_TOPIC_VIDEOS));
        group.onCreateView();
        return group.getRoot();
    }
    
    @Override
    public void onDataChange(int code) {
        group.onDataChange(code);
    }
    
    @Override
    public void setTitlebar() {
        getTitleBar().rightPart.setVisibility(View.GONE);
        ((TextView)getTitleBar().title).setText(topicName);
    }

    @Override
    public int setTitleLayoutId() {
        return R.layout.base_titlebar;
    }

    @Override
    public void onReleaseView() {
        manager.onDestroy();
        group.onDestroyView();
    }
    
}
