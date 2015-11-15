package com.feibo.joke.view.module.home;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import fbcore.log.LogUtil;

import com.feibo.joke.R;
import com.feibo.joke.app.BundleUtil;
import com.feibo.joke.app.DataChangeEventCode;
import com.feibo.joke.manager.list.VideosEssenseManager;
import com.feibo.joke.model.Video;
import com.feibo.joke.model.VideoTopicItem;
import com.feibo.joke.view.BaseFragment;
import com.feibo.joke.view.adapter.VideoListAdapter;
import com.feibo.joke.view.adapter.VideoTopicAdapter;
import com.feibo.joke.view.group.BasePullWaterGroup;
import com.feibo.joke.view.group.GroupConfig;
import com.feibo.joke.view.group.VideoTopicPullWaterGroup;

public class EssenceFragment extends BaseFragment implements GroupOperator {
    private static final String TAG = EssenceFragment.class.getSimpleName();
    
    private VideoTopicPullWaterGroup listGroup;
    private VideosEssenseManager essenseManager;
    private VideoTopicAdapter essenseAdapter;
    
    public EssenceFragment() {
        
    }
    
    public static EssenceFragment newInstance() {
        return new EssenceFragment();
    }
    
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        essenseManager = new VideosEssenseManager();
        essenseAdapter = new VideoTopicAdapter(getActivity());
    }
    
    @Override
    public ViewGroup onCreateView(LayoutInflater inflater, Bundle savedInstanceState) {
        return (ViewGroup) inflater.inflate(R.layout.fragment_essence, null);
    }
    
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        LogUtil.i(TAG, "onViewCreated");
        String handpickLoadMoreOverText = getString(R.string.load_more_over_text_handpick);
        
        listGroup = new VideoTopicPullWaterGroup(getActivity());
        listGroup.setFooterLoadMoreOverText(handpickLoadMoreOverText);
        listGroup.setGroupConfig(GroupConfig.create(GroupConfig.GROUP_VIDEO_ESSENCE));

        listGroup.setListAdapter(essenseAdapter);
        listGroup.setListManager(essenseManager);

        listGroup.onCreateView();
        ViewGroup viewGroup = (ViewGroup) view;
        viewGroup.removeAllViews();
        viewGroup.addView(listGroup.getRoot());
    }

    @Override
    public void onScrollToTop(boolean refresh) {
        listGroup.onScollToTop(refresh);
    }

    @Override
    public void onDataChange(int code) {
        super.onDataChange(code);
        if(code == DataChangeEventCode.CHANGE_TYPE_VIDEO_DETAIL_CHANGE) {
            Bundle bundle = getFinishBundle();
            if (bundle == null) {
                return;
            }
            Video video = (Video) bundle.getSerializable(BundleUtil.KEY_VIDEO);
            int positionId = bundle.getInt(BundleUtil.KEY_ADAPTER_POSITION);
            if (positionId == -1) {
                return;
            }
            VideoTopicItem videoTopicItem = null;
            if(video != null) {
                videoTopicItem = new VideoTopicItem();
                videoTopicItem.type = VideoTopicItem.TYPE_VIDEO;
                videoTopicItem.video = video;
            }

            getFinishBundle().putSerializable(BundleUtil.KEY_VIDEO, videoTopicItem);
        }

        listGroup.onDataChange(code);
    }
}
