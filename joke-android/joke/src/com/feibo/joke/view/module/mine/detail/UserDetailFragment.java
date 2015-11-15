package com.feibo.joke.view.module.mine.detail;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.feibo.joke.R;
import com.feibo.joke.app.DataChangeEventCode;
import com.feibo.joke.manager.SocialManager;
import com.feibo.joke.manager.list.VideosPublishManager;
import com.feibo.joke.manager.work.UserManager;
import com.feibo.joke.view.adapter.VideoListAdapter;
import com.feibo.joke.view.group.impl.UserDetailGroup;
import com.feibo.joke.view.group.impl.UserDetailGroup.OnUserDetailGroupListener;
import com.feibo.joke.view.module.mine.BaseLoginFragment;
import com.feibo.joke.view.module.mine.edit.PersonEditFragment;
import com.feibo.joke.view.util.LaunchUtil;
import com.feibo.joke.view.widget.waterpull.CListView;

@Deprecated
public class UserDetailFragment extends BaseLoginFragment {

    private static final String BUNDLE_KEY_FROM = "from";
    private static final String BUNDLE_KEY_USERID = "userId";

    private TextView title;

    private boolean isFromMe;
    private long userId;

    private VideosPublishManager videosPublishManager;
    private VideoListAdapter videoListAdapter;
    private UserDetailGroup videoListGroup;

    public static Bundle buildBundle(boolean isFromMe, long id) {
        Bundle args = new Bundle();
        args.putBoolean(BUNDLE_KEY_FROM, isFromMe);
        args.putLong(BUNDLE_KEY_USERID, id);
        return args;
    }

    @Override
    public View containChildView() {
        Bundle bundle = getActivity().getIntent().getExtras();
        isFromMe = bundle.getBoolean(BUNDLE_KEY_FROM, true);
        userId = bundle.getLong(BUNDLE_KEY_USERID);

        videoListAdapter = new VideoListAdapter(getActivity(), isFromMe ? VideoListAdapter.TYPE_MINE
                : VideoListAdapter.TYPE_DEFAULT);
        videosPublishManager = new VideosPublishManager(userId);
        videoListGroup = new UserDetailGroup(getActivity(), isFromMe, userId);

        videoListGroup.setListAdapter(videoListAdapter);
        videoListGroup.setListManager(videosPublishManager);

        videoListGroup.onCreateView();
        View view = videoListGroup.getRoot();
        CListView cListview = videoListGroup.getListView();

        videoListGroup.initHeadView(cListview.getListHeaderView().getCustomView());
        
        videoListGroup.initHeaderListener();
        initListener();
        
        return view;
    }
    
    private void initListener() {
        videoListGroup.setOnUserDetailGroupListener(new OnUserDetailGroupListener() {
            
            @Override
            public void setFragmentTitle(String titleText) {
                if(title != null && titleText != null) {
                    title.setText(titleText);
                }
            }

            @Override
            public void onPreperadLogin() {
                loginClick(OPERATION_CODE_FOUCES);
            }

            @Override
            public void onShareFail(int code) {
                if(code == SocialManager.WEIBO_TOKEN_TIMEOUT) {
//                    resetWeiboToken(OPERATION_RESET_WEIBO_TOKEN);
                }
            }
        });
    }

    @Override
    public int setTitleLayoutId() {
        return R.layout.base_titlebar;
    }

    @Override
    public void onReleaseView() {
        videoListGroup.onDestroyView();
        videosPublishManager.onDestroy();
    }

    @Override
    public void setTitlebar() {
        TitleBar titleBar = getTitleBar();

        title = (TextView) titleBar.title;
        TextView tvHeadRight = titleBar.tvHeadRight;
        
        if(UserManager.getInstance().isFromMe(userId)) {
	        tvHeadRight.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					LaunchUtil.launchSubActivity(UserDetailFragment.this.getActivity(), PersonEditFragment.class, null);
				}
			});
	        tvHeadRight.setText("编辑");
        }
    }

    @Override
    public void onDataChange(int code) {
        switch (code) {
        case DataChangeEventCode.CHANGE_TYPE_VIDEO_DETAIL_CHANGE:
            videoListGroup.onDataChange(code);
            break;
        case DataChangeEventCode.CHANGE_TYPE_ATTENTION_COUNT_CHANGE:
        case DataChangeEventCode.CHANGE_TYPE_LIKE_COUNT_CHANGE:
            if(isFromMe) {
                videoListGroup.onDataChange(code);
            }
            break;
        case DataChangeEventCode.CHANGE_TYPE_VIDEO_PRODUCE_SUCESS:
            super.setChangeTypeAndFinish(code);
            break;
        case DataChangeEventCode.CHANGE_TYPE_MODIFY_USER:
        	title.setText(UserManager.getInstance().getUser().nickname);
        	videoListGroup.onDataChange(code);
        	break;
        }
    }

    @Override
    public void loginResult(boolean result, int operationCode) {
        if(operationCode == OPERATION_CODE_FOUCES && result) {
            
        }
    }
    
}
