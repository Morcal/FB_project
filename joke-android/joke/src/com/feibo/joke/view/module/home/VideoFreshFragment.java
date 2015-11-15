package com.feibo.joke.view.module.home;

import android.view.View;
import android.widget.TextView;

import com.feibo.joke.R;
import com.feibo.joke.app.UmengConstant;
import com.feibo.joke.manager.list.VideosFreshManager;
import com.feibo.joke.view.BaseTitleFragment;
import com.feibo.joke.view.adapter.VideoListAdapter;
import com.feibo.joke.view.group.GroupConfig;
import com.feibo.joke.view.group.BasePullWaterGroup;

/**
 * 最新视频模块
 * @author ml_bright
 * @version 2015-4-2  下午5:34:46
 */
public class VideoFreshFragment extends BaseTitleFragment{

    private VideoListAdapter adapter;
    private BasePullWaterGroup group;
    private VideosFreshManager manager;
    
    @Override
    public View containChildView() {
        adapter = new VideoListAdapter(getActivity());
        manager = new VideosFreshManager();
        group = new BasePullWaterGroup(getActivity());
        group.setUmengVideoClickName(UmengConstant.NEW_VIDEO_PLAY);
        group.setFooterLoadMoreOverText(getString(R.string.load_more_over_text_news_videos));
        group.setListAdapter(adapter);
        group.setListManager(manager);
        group.setGroupConfig(GroupConfig.create(GroupConfig.GROUP_VIDEO_FRESH));
        group.onCreateView();
        return group.getRoot();
    }
    
    @Override
    public void setTitlebar() {
        getTitleBar().rightPart.setVisibility(View.GONE);
        ((TextView)getTitleBar().title).setText("看点新片");
    }

    @Override
    public int setTitleLayoutId() {
        return R.layout.base_titlebar;
    }
    
    @Override
    public void onDataChange(int code) {
        group.onDataChange(code);
    }

    @Override
    public void onReleaseView() {
        manager.onDestroy();
        group.onDestroyView();
    }
    
}
