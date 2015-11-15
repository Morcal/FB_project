package com.feibo.joke.view.module.mine.detail;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

import com.feibo.joke.R;
import com.feibo.joke.app.BundleUtil;
import com.feibo.joke.app.DataChangeEventCode;
import com.feibo.joke.manager.AbsListManager;
import com.feibo.joke.manager.work.UserManager;
import com.feibo.joke.model.User;
import com.feibo.joke.view.BaseTitleFragment;
import com.feibo.joke.view.adapter.UserListAdapter;
import com.feibo.joke.view.group.BasePullListGroup;
import com.feibo.joke.view.group.GroupConfig;
import com.feibo.joke.view.util.LaunchUtil;
import com.feibo.joke.view.widget.FocusStateView.OnStatuChangeListener;

public abstract class BaseUserListFragment extends BaseTitleFragment implements OnStatuChangeListener{

    private static final String PARAM_USER_ID = "user_id";

    private int attionChangeFlag;
    
    private BasePullListGroup<User> group;
    private AbsListManager<User> manager;
    private UserListAdapter adapter;

    public static Bundle buildBundle(long userid) {
        Bundle bundle = new Bundle();
        bundle.putLong(PARAM_USER_ID, userid);
        return bundle;
    }
    
    @Override
    public View containChildView() {
        Bundle bundle = getActivity().getIntent().getExtras();
        long userId = bundle.getLong(PARAM_USER_ID, 0);
        
        adapter = new UserListAdapter(getActivity());
        adapter.setOnStatuChangelistener(this);
        
        manager = getGroupManager(userId);
        
        group = new BasePullListGroup<User>(getActivity());
        group.setListAdapter(adapter);
        group.setListManager(manager);
        group.setGroupConfig(getGroupConfig());

        initListener();
        
        group.onCreateView();
        return group.getRoot();
    }    
    
    public void initListener() {
        group.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                User user = adapter.getItem(position);
                boolean isFromMe = UserManager.getInstance().isFromMe((int)user.id);
//                LaunchUtil.launchSubActivity(getActivity(), UserDetailFragment.class,
//                        UserDetailFragment.buildBundle(isFromMe, user.id));
                LaunchUtil.launchSubActivity(getActivity(), UserDetailFragment2.class,
                        UserDetailFragment2.buildBundle(isFromMe, user.id));
            }
        });
    }
    
    @Override
    public void onStatuChange(boolean isAttention) {
        if(getActivity() == null) {
            return;
        }
        
        attionChangeFlag = isAttention ? (attionChangeFlag + 1) : (attionChangeFlag - 1); 
        
        Bundle bundle = BundleUtil.buildAttentionCountChangeBundle(getFinishBundle(), attionChangeFlag);
        setFinishBundle(bundle);
        setChangeType(DataChangeEventCode.CHANGE_TYPE_ATTENTION_COUNT_CHANGE);
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
    
    public abstract AbsListManager<User> getGroupManager(long userId);
    public abstract GroupConfig getGroupConfig();
    
}
