package com.feibo.joke.video;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.feibo.joke.R;
import com.feibo.joke.app.DataChangeEventCode;
import com.feibo.joke.utils.StringUtil;
import com.feibo.joke.video.manager.VideoDraftManager.Draft;
import com.feibo.joke.view.group.GroupConfig;
import com.feibo.joke.view.group.impl.VideoShareGroup;
import com.feibo.joke.view.group.impl.VideoShareGroup.IVideoShareListener;
import com.feibo.joke.view.module.mine.BaseLoginFragment;

public class VideoShareFragment extends BaseLoginFragment {

    private static final String KEY_DRAFT = "key_draft"; 
    
    private VideoShareGroup group;

    public static Bundle buildBuilder(Draft draft) {
        Bundle bundle = new Bundle();
        bundle.putSerializable(KEY_DRAFT, draft);
        return bundle;
    }

    private Draft getDraftFromBundle() {
        Bundle bundle = getArguments();
        Draft draft = (Draft) bundle.getSerializable(KEY_DRAFT);
        if(draft == null || StringUtil.isEmpty(draft.jsonPath)) {
            return null;
        }
        return draft;
    }

    @Override
    public View containChildView() {
        group = new VideoShareGroup(getActivity(), getDraftFromBundle());
        initListener();
        group.onCreateView();
        group.setGroupConfig(GroupConfig.create(GroupConfig.GROUP_VEDIO_SHARE));
        return group.getRoot();
    }

    private void initListener() {
        group.setVideoShareListener(new IVideoShareListener() {
            
            @Override
            public void setTitlebarVisible(int visible) {
                if(getTitleBar() != null && getTitleBar().rightPart != null) {
                    getTitleBar().rightPart.setVisibility(visible);
                }
            }
            
            @Override
            public void onLogin() {
                loginClick();
            }

            @Override
            public void onPublish() {
                setChangeTypeAndFinish(DataChangeEventCode.CHANGE_TYPE_VIDEO_PRODUCE_SUCESS);
            }
        });
    }

    @Override
    public int setTitleLayoutId() {
        return R.layout.base_titlebar_video;
    }

    @Override
    public void setTitlebar() {
        ((TextView) getTitleBar().title).setText("分享");
        getTitleBar().tvHeadRight.setText("存草稿");
        getTitleBar().ivHeadRight.setVisibility(View.INVISIBLE);
        getTitleBar().tvHeadRight.setVisibility(View.VISIBLE);
        getTitleBar().ivHeadLeft.setVisibility(View.VISIBLE);
        getTitleBar().tvHeadLeft.setVisibility(View.INVISIBLE);

        getTitleBar().rightPart.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                group.handleSave();
            }
        });
    }

    @Override
    public void onReleaseView() {
        group.onDestroyView();
    }


    @Override
    public void loginResult(boolean result, int operationCode) {
        if(result && group != null) {
            group.handleSubmit();
        }
    }

    @Override
    public void onDataChange(int code) {
        switch (code) {
        case DataChangeEventCode.CHANGE_TYPE_VIDEO_COVER_SUCCESS:
            getTitleBar().rightPart.setVisibility(View.VISIBLE);
            break;
        default:
            break;
        }
        if(group != null) {
            group.setCoverImage();
        }
    }

}