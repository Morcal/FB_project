
package com.feibo.joke.view.module.mine;

import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

import com.feibo.joke.manager.list.UsersFunnyMasterManager;
import com.feibo.joke.model.FunnyMaster;
import com.feibo.joke.view.adapter.FunnyMasterListAdapter;
import com.feibo.joke.view.adapter.OnKeyAttentionAdapter;
import com.feibo.joke.view.group.BasePullListGroup;
import com.feibo.joke.view.group.GroupConfig;
import com.feibo.joke.view.module.mine.detail.UserDetailFragment2;
import com.feibo.joke.view.util.LaunchUtil;
import com.feibo.joke.view.util.MessageHintManager;

/**
 * 达人推荐
 *
 * @author ml_bright
 * @version 2015-4-3 下午2:00:15
 */
public class FriendFindFunnyFragment extends BaseAttentionAllFragment {

    private BasePullListGroup<FunnyMaster> group;
    
    public FriendFindFunnyFragment() {
        super(false);
    }
    
    @Override
    public void onResume() {
        MessageHintManager.setFriendMasterHint(getActivity(), 0);
        super.onResume();
    }
    
    @Override
    public FunnyMasterListAdapter getGroupAdapter() {
        return new FunnyMasterListAdapter(getActivity());
    }

    @Override
    public UsersFunnyMasterManager getGroupManager() {
        return new UsersFunnyMasterManager();
    }
    
    @Override
    public void initListener(@SuppressWarnings("rawtypes") final OnKeyAttentionAdapter adapter) {
        group.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(adapter instanceof FunnyMasterListAdapter) {
                    FunnyMaster fm = ((FunnyMasterListAdapter)adapter).getItem(position);
//                    LaunchUtil.launchSubActivity(getActivity(), UserDetailFragment.class, UserDetailFragment.buildBundle(false, fm.id));
                    LaunchUtil.launchSubActivity(getActivity(), UserDetailFragment2.class, UserDetailFragment2.buildBundle(false, fm.id));
                }
            }
        });
    }

    @Override
    public BasePullListGroup<FunnyMaster> getGroup() {
        group = new BasePullListGroup<FunnyMaster>(getActivity(), true, false);  
        return group;
    }

    @Override
    public GroupConfig getGroupConfig() {
        return GroupConfig.create(GroupConfig.GROUP_USER_FUNNY);
    }

    @Override
    public void loginResult(boolean result, int operationCode) {
        
    }

}
