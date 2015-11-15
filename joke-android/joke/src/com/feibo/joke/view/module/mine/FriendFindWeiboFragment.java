package com.feibo.joke.view.module.mine;

import android.view.View;

import com.feibo.joke.manager.LoadListener;
import com.feibo.joke.manager.SocialManager;
import com.feibo.joke.manager.list.WeiboFriendsManager;
import com.feibo.joke.manager.work.UserManager;
import com.feibo.joke.model.LoginInfo;
import com.feibo.joke.utils.ShareUtil;
import com.feibo.joke.view.adapter.OnKeyAttentionAdapter;
import com.feibo.joke.view.adapter.WeiboFriendsAdapter;
import com.feibo.joke.view.adapter.WeiboFriendsAdapter.IOnWeiboItemClickListener;
import com.feibo.joke.view.group.GroupConfig;
import com.feibo.joke.view.group.impl.FriendFindWeiboListGroup;
import com.feibo.joke.view.group.impl.FriendFindWeiboListGroup.OnBandingListener;
import com.feibo.joke.view.module.mine.detail.UserDetailFragment2;
import com.feibo.joke.view.util.LaunchUtil;
import com.feibo.joke.view.util.MessageHintManager;
import com.feibo.joke.view.util.ToastUtil;
import com.feibo.social.base.Platform;

public class FriendFindWeiboFragment extends BaseAttentionAllFragment implements OnBandingListener{
    
    private FriendFindWeiboListGroup group;
    
    public FriendFindWeiboFragment() {
        super(true);
    }

    @Override
    public void onResume() {
        MessageHintManager.setFriendSinaHint(getActivity(), 0);
        super.onResume();
    }
    
    @SuppressWarnings("rawtypes") 
    public void initListener(OnKeyAttentionAdapter adapter) {
    	((WeiboFriendsAdapter)adapter).setOnInvitationListener(new IOnWeiboItemClickListener() {
			@Override
			public void invitation(String beInvitationName) {
				ShareUtil.invitationSina(getActivity(), UserManager.getInstance().getUser(), beInvitationName, new LoadListener() {
                    
                    @Override
                    public void onSuccess() {
                        
                    }
                    
                    @Override
                    public void onFail(int code) {
                        if(code == SocialManager.WEIBO_TOKEN_TIMEOUT) {
                            ToastUtil.showSimpleToast("微博授权已失效, 要重新绑定哦");
//                            resetWeiboToken(OPERATION_RESET_WEIBO_TOKEN);
                        }
                    }
                });
			}

            @Override
            public void onLaunchUserDetail(long userId) {
                boolean isFromMe = userId == UserManager.getInstance().getUser().id;
//                LaunchUtil.launchSubActivity(getActivity(), UserDetailFragment.class, UserDetailFragment.buildBundle(isFromMe, userId));
                LaunchUtil.launchSubActivity(getActivity(), UserDetailFragment2.class, UserDetailFragment2.buildBundle(isFromMe, userId));

            }
		});
    }
    
    @Override
    public void initPrepared() {
        //未绑定微博的情况下，一键关注不可见
        if(getListGroup().onPrepareView() != null) {
            getTitleBar().rightPart.setVisibility(View.INVISIBLE);
        }
    }
    
    @Override
    public WeiboFriendsAdapter getGroupAdapter() {
        return new WeiboFriendsAdapter(getActivity());
    }

    @Override
    public WeiboFriendsManager getGroupManager() {
        return new WeiboFriendsManager();
    }
    
    @Override
    public FriendFindWeiboListGroup getGroup() {
    	group = new FriendFindWeiboListGroup(getActivity());
    	group.onBandingWeiboListener(this);
        return group;
    }
    
    @Override
    public GroupConfig getGroupConfig() {
        return GroupConfig.create(GroupConfig.GROUP_USER_WEIBO_FRIENDS);
    }

	@Override
	public void onBanding(Platform platform) {
		bandPlatform(platform);
	}

    @Override
    public void onWeiboAuthTimeout() {
        if(getActivity() == null) {
            return;
        }
        UserManager.unBandingRelationship(getActivity(), LoginInfo.BANDING_SINA);
        ToastUtil.showSimpleToast("微博授权已失效, 要重新绑定哦");
        
        group.setRoot(group.onPrepareView());
        attachToRoot(group.getRoot());
    }

    @Override
    public void loginResult(boolean result, int operationCode) {
        if(result) {
            if(operationCode == OPERATION_RESET_WEIBO_TOKEN) {
                
            } else {
                getTitleBar().rightPart.setVisibility(View.VISIBLE);
                getListGroup().onResetView();
                attachToRoot(getListGroup().getRoot());
            }
        }
    }

}
