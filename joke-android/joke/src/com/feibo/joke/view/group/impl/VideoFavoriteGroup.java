package com.feibo.joke.view.group.impl;

import android.content.Context;
import android.os.Bundle;

import com.feibo.joke.app.BundleUtil;
import com.feibo.joke.app.DataChangeEventCode;
import com.feibo.joke.view.group.BasePullWaterGroup;

public class VideoFavoriteGroup extends BasePullWaterGroup {

    private int likeCount;
    private int attionChangeFlag;
    
    public VideoFavoriteGroup(Context context) {
        super(context);
    }

    @Override
    public void onDataChange(int code) {
        if(code == DataChangeEventCode.CHANGE_TYPE_VIDEO_DETAIL_CHANGE) {
            Bundle bundle = getFinishBundle();
            if (bundle == null) {
                return;
            }
            int likeFlag = bundle.getInt(BundleUtil.KEY_LIKE_FLAG);
            attionChangeFlag += bundle.getInt(BundleUtil.KEY_ATTENTION_COUNT_CHANGE);
            if(likeFlag < 0) {
                //删除bundle
                bundle.putSerializable(BundleUtil.KEY_VIDEO, null);
                
                //为退出这个界面的时候刷新下个界面准备
                likeCount += likeFlag;
            }
            
            super.onDataChange(code);
        }
        
        
        if(likeCount != 0 || attionChangeFlag != 0) {
            Bundle bundleLike = BundleUtil.buildVideoFavoriteBundle(likeCount, attionChangeFlag);
            setFinishBundle(bundleLike);
            setChangeType(DataChangeEventCode.CHANGE_TYPE_LIKE_COUNT_CHANGE);
        }
    }
    
}
