package com.feibo.joke.view.group.impl;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

import com.feibo.joke.R;
import com.feibo.joke.app.BundleUtil;
import com.feibo.joke.app.DataChangeEventCode;
import com.feibo.joke.app.UmengConstant;
import com.feibo.joke.model.DiscoveryTopicItem;
import com.feibo.joke.model.Video;
import com.feibo.joke.utils.SPHelper;
import com.feibo.joke.view.group.BasePullListGroup;
import com.feibo.joke.view.module.home.SearchFragment;
import com.feibo.joke.view.module.home.TopicsFragment;
import com.feibo.joke.view.module.home.VideoFreshFragment;
import com.feibo.joke.view.util.LaunchUtil;
import com.feibo.joke.view.util.ToastUtil;
import com.umeng.analytics.MobclickAgent;

/**
 * 发现组件
 * 
 * @author ml_bright
 * @version 2015-4-2 下午2:10:00
 */
public class DiscoveryListGroup extends BasePullListGroup<DiscoveryTopicItem> {

    private boolean isShowRecommend;

    private View recommendBtn;
    private Button newVideoBtn;
    private Button searchBtn;

    public DiscoveryListGroup(Context context) {
        super(context, true, false);
    }

    @Override
    public View initHeaderView() {
        View headView = LayoutInflater.from(getContext()).inflate(R.layout.discovery_head, null);
        newVideoBtn = (Button) headView.findViewById(R.id.discoivery_new_video);
        searchBtn = (Button) headView.findViewById(R.id.search_edit);
        return headView;
    }

    @Override
    public View initFooterView() {
        isShowRecommend = SPHelper.isShowTopic(getContext());
        View footView = null;
        if (isShowRecommend) {
            footView = LayoutInflater.from(getContext()).inflate(R.layout.discovery_footer, null);
            recommendBtn = footView.findViewById(R.id.discoivery_recommend_btn);
        } else {
            footView = new View(getContext());
        }
        return footView;
    }

    @Override
    public void onDataChange(int code) {
        super.onDataChange(code);
        switch (code) {
        case DataChangeEventCode.CHANGE_TYPE_VIDEO_DETAIL_CHANGE:
            Bundle bundle = getFinishBundle();
            if (bundle == null) {
                return;
            }

            Video video = (Video) bundle.getSerializable(BundleUtil.KEY_VIDEO);
            int adapterId = bundle.getInt(BundleUtil.KEY_ADAPTER_POSITION);
            int discoveryVideoPosition = bundle.getInt(BundleUtil.KEY_DSICOVERY_VIDEO_POSITION);

            if (adapterId == -1 || discoveryVideoPosition == -1) {
                return;
            }

            DiscoveryTopicItem item = getListManager().getDatas().get(adapterId);
            item.videos.remove(discoveryVideoPosition);
            if (video != null) {
                item.videos.add(discoveryVideoPosition, video);
            }

            localRefreshData(adapterId, item);
            break;
        }
    }

    @Override
    public void initListener() {
        super.initListener();
        if (isShowRecommend) {
            recommendBtn.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    LaunchUtil.launchSubActivity(getContext(), TopicsFragment.class, null);

                    MobclickAgent.onEvent(getContext(), UmengConstant.DISCOVERY_TOPIC_RECOMMEND);
                }
            });
        }
        newVideoBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                LaunchUtil.launchSubActivity(getContext(), VideoFreshFragment.class, null);

                MobclickAgent.onEvent(getContext(), UmengConstant.DISCOVERY_NEW_VIDEO);
            }
        });

        searchBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                LaunchUtil.launchSubActivity(getContext(), SearchFragment.class, null);
                MobclickAgent.onEvent(getContext(), UmengConstant.DISCOVERY_SEARCH);
            }
        });
    }

}
