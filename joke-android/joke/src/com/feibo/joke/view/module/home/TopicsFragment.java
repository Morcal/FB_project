package com.feibo.joke.view.module.home;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.TextView;

import com.feibo.joke.R;
import com.feibo.joke.app.UmengConstant;
import com.feibo.joke.manager.list.TopicsManager;
import com.feibo.joke.model.Topic;
import com.feibo.joke.view.BaseTitleFragment;
import com.feibo.joke.view.adapter.TopicListAdapter;
import com.feibo.joke.view.group.BasePullGridGroup;
import com.feibo.joke.view.group.GroupConfig;
import com.feibo.joke.view.util.LaunchUtil;
import com.umeng.analytics.MobclickAgent;

/**
 * 更多热门话题
 *
 * @author ml_bright
 * @version 2015-4-2  下午5:33:36
 */
public class TopicsFragment extends BaseTitleFragment {

    private static final String BUNDLE_KEY_TOPIC_ID = "topic_id";

    private BasePullGridGroup group;
    private TopicsManager topicsManager;
    private TopicListAdapter topicsAdapter;

    private long topicId;

    public static Bundle buildBundle(long topicId) {
        Bundle args = new Bundle();
        args.putLong(BUNDLE_KEY_TOPIC_ID, topicId);
        return args;
    }

    @Override
    public View containChildView() {
        Bundle bundle = getActivity().getIntent().getExtras();
        topicId = bundle.getLong(BUNDLE_KEY_TOPIC_ID, 0);

        topicsManager = new TopicsManager(topicId);
        topicsAdapter = new TopicListAdapter(getActivity());
        group = new BasePullGridGroup(getActivity());
        group.setListAdapter(topicsAdapter);
        group.setListManager(topicsManager);
        group.setGroupConfig(GroupConfig.create(GroupConfig.GROUP_TOPICS_HOT));
        group.onCreateView();
        initListener();
        return group.getRoot();
    }

    private void initListener() {
        group.setOnItemClickListerner(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                view.postDelayed(new Runnable() {

                    @Override
                    public void run() {
                        topicsAdapter.setRead(position);
                        topicsAdapter.notifyDataSetChanged();
                    }
                }, 200);

                Topic t = topicsAdapter.getItem(position);
//                Bundle bundle = VideoTopicFragment.buildBundle(t.id, t.title);
//                LaunchUtil.launchSubActivity(TopicsFragment.this.getActivity(), VideoTopicFragment.class, bundle);

                Bundle bundle = VideoTopicFragment2.buildBundle(t.id, t.oriImage, t.title);
                LaunchUtil.launchSubActivity(TopicsFragment.this.getActivity(), VideoTopicFragment2.class, bundle);

                if(topicId == 0) {
                    MobclickAgent.onEvent(TopicsFragment.this.getActivity(), UmengConstant.TOPIC_RECOMMEND_PLAY);
                }

            }
        });
    }

    @Override
    public void setTitlebar() {
        getTitleBar().rightPart.setVisibility(View.INVISIBLE);
        ((TextView) getTitleBar().title).setText(topicId == 0 ? "话题大宝荐" : "更多话题");
    }

    @Override
    public int setTitleLayoutId() {
        return R.layout.base_titlebar;
    }

    @Override
    public void onReleaseView() {
        topicsManager.onDestroy();
        group.onDestroyView();
    }

}
