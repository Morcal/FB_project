package com.feibo.joke.view.module.mine.detail;

import android.view.View;
import android.widget.TextView;

import com.feibo.joke.R;
import com.feibo.joke.manager.list.VideosFavoriteManager;
import com.feibo.joke.view.BaseTitleFragment;
import com.feibo.joke.view.adapter.VideoListAdapter;
import com.feibo.joke.view.group.GroupConfig;
import com.feibo.joke.view.group.impl.VideoFavoriteGroup;

public class VideoFavoriteFragment extends BaseTitleFragment{

    private VideosFavoriteManager manager;
    private VideoListAdapter adapter;
    private VideoFavoriteGroup group;
    
    @Override
    public View containChildView() {
        manager = new VideosFavoriteManager();
        adapter = new VideoListAdapter(getActivity());
        group = new VideoFavoriteGroup(getActivity());
        group.setListAdapter(adapter);
        group.setListManager(manager);
        group.setGroupConfig(GroupConfig.create(GroupConfig.GROUP_VIDEO_FAVORITE));
        group.onCreateView();
        return group.getRoot();
    }

    @Override
    public void onDataChange(int code) {
        if(group != null) {
            group.onDataChange(code);
        }
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
    
    @Override
    public void setTitlebar() {
        ((TextView)getTitleBar().title).setText("我爱过的");
        getTitleBar().rightPart.setVisibility(View.GONE);
    }

}
