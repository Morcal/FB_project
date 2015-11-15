package com.feibo.joke.view.module.mine;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

import com.feibo.joke.R;
import com.feibo.joke.app.DataChangeEventCode;
import com.feibo.joke.manager.list.MessagesSystemManager;
import com.feibo.joke.manager.list.MessagesUserManager;
import com.feibo.joke.model.Message;
import com.feibo.joke.utils.SPHelper;
import com.feibo.joke.view.BaseTitleFragment;
import com.feibo.joke.view.adapter.BasePageAdapter;
import com.feibo.joke.view.adapter.MessageSystemAdapter;
import com.feibo.joke.view.adapter.MessageUserAdapter;
import com.feibo.joke.view.group.AbsLoadingGroup;
import com.feibo.joke.view.group.BasePullListGroup;
import com.feibo.joke.view.group.GroupConfig;
import com.feibo.joke.view.module.mine.detail.UserDetailFragment2;
import com.feibo.joke.view.module.video.VideoDetailFragment;
import com.feibo.joke.view.util.LaunchUtil;
import com.feibo.joke.view.util.MessageHintManager;
import com.feibo.joke.view.widget.MessageButton;
import com.feibo.joke.view.widget.MessageButton.ISelectListener;

public class MessageFragment extends BaseTitleFragment {

    private ViewPager viewPager;
    private MessageButton messageButton;
    private BasePageAdapter pagerAdapter;
    private AbsLoadingGroup[] groups;
    
    private MessageUserAdapter userAdapter;
    private MessageSystemAdapter systemAdapter;
    
    private MessagesUserManager userManager;
    private MessagesSystemManager systemManager;
    
    @Override
    public View containChildView() {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_message, null);
        initWidget(view);
        initAdapter();
        initListener();

        viewPager.setCurrentItem(0);
        messageButton.select(0);
        
        return view;
    }

    @Override
    public int setTitleLayoutId() {
        return R.layout.titlebar_message;
    }

    @Override
    public void onReleaseView() {
        userManager.onDestroy();
        systemManager.onDestroy();
        
        for(int i=0; i<groups.length; i++){
            groups[i].onDestroyView();
        }
    }
    
    @Override
    public void onDestroy() {
        if(userManager != null && userManager.getDatas() != null && userManager.getDatas().size() > 0) {
            SPHelper.setReadPositionInUserMessage(getActivity(), userManager.getDatas().get(0).id);
        }
        super.onDestroy();
    }

    private void initListener() {
        viewPager.setOnPageChangeListener(new OnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                if(position == 0) {
                    MessageHintManager.setMessageMessageHint(getActivity(), 0);
                } else {
                    MessageHintManager.setMessageNoticeHint(getActivity(), 0);
                }
                messageButton.select(position);
            }

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageScrollStateChanged(int statu) {

            }
        });
        
        messageButton.setSelectListener(new ISelectListener() {
            @Override
            public void select(int type) {
                if(type == 0) {
                    MessageHintManager.setMessageMessageHint(getActivity(), 0);
                } else {
                    MessageHintManager.setMessageNoticeHint(getActivity(), 0);
                }
                viewPager.setCurrentItem(type);
            }
        });
        ((MessageListGroup)groups[0]).setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                view.postDelayed(new Runnable() {
                    
                    @Override
                    public void run() {
                        userAdapter.setRead(position);
                    }
                }, 200);
                
                Message message = userAdapter.getItem(position);
                switch (message.type) {
                case Message.TYPE_ATTENTION:
                    if(message.user != null) {
//                        LaunchUtil.launchSubActivity(getActivity(), UserDetailFragment.class, UserDetailFragment.buildBundle(false, message.user.id));
                        LaunchUtil.launchSubActivity(getActivity(), UserDetailFragment2.class, UserDetailFragment2.buildBundle(false, message.user.id));
                    }
                    break;
                case Message.TYPE_COMMENT:
                case Message.TYPE_LIKE:
                    if(message.video != null) {
                        LaunchUtil.launchSubActivity(getActivity(), VideoDetailFragment.class, VideoDetailFragment.buildBundle(message.video.id, position));
                    }
                    break;
                }
            }
        });
        ((MessageListGroup)groups[1]).setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                
            }
        });
        
    }

    private void initWidget(View view) {
        messageButton = (MessageButton) getTitleBar().title;
        viewPager = (ViewPager) view.findViewById(R.id.pager);
    }

    private void initAdapter() {
        groups = new AbsLoadingGroup[2];
        groups[0] = new MessageListGroup(getActivity());
        groups[0].setGroupConfig(GroupConfig.create(GroupConfig.GROUP_MESSAGE_USER));
        groups[1] = new MessageListGroup(getActivity());
        groups[1].setGroupConfig(GroupConfig.create(GroupConfig.GROUP_MESSAGE_SYSTEM));
        
        userManager = new MessagesUserManager();
        systemManager = new MessagesSystemManager();
        
        userAdapter = new MessageUserAdapter(getActivity());
        systemAdapter = new MessageSystemAdapter(getActivity());
        
        ((MessageListGroup)groups[0]).setListManager(userManager);
        ((MessageListGroup)groups[0]).setListAdapter(userAdapter);
        ((MessageListGroup)groups[1]).setListManager(systemManager);
        ((MessageListGroup)groups[1]).setListAdapter(systemAdapter);
        
        pagerAdapter = new BasePageAdapter();
        pagerAdapter.setGroups(groups);
        viewPager.setAdapter(pagerAdapter);
        messageButton.select(0);
        MessageHintManager.setMessageMessageHint(getActivity(), 0);
    }

    public class MessageListGroup extends BasePullListGroup<Message>{
        public MessageListGroup(Context context) {
            super(context);
        }
    }
    
    @Override
    public void setTitlebar() {
        
    }
    
    @Override
    public void onResume() {
        super.onResume();
        initRedhint();
    }

    private void initRedhint() {
        MessageHintManager.initMessage(getActivity(), messageButton);
    }
    
    @Override
    public void onDataChange(int code) {
        if(code == DataChangeEventCode.CODE_EVENT_BUS_REDHINT) {
            initRedhint();
        }
    }
    
}