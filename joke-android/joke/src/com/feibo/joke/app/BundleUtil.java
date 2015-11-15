package com.feibo.joke.app;

import android.os.Bundle;

import com.feibo.joke.model.Video;

public class BundleUtil {

    /** 用户ID */
    public static final String KEY_USERID = "user_id";
    
    /** 视频bundle */
    public static final String KEY_VIDEO = "video";

    /** 列表页position */
    public static final String KEY_ADAPTER_POSITION = "adapter_position";

    /** 发现模块中的视频position */
    public static final String KEY_DSICOVERY_VIDEO_POSITION = "discovery_video_position";

    /** 视频详情页喜欢标记 */
    public static final String KEY_LIKE_FLAG = "like_flag";
    
    /** 关注数变化 */
    public static final String KEY_ATTENTION_COUNT_CHANGE = "attention_count_change";

    /** 视频详情页返回bundle设置 */
    public static Bundle buildVideoDetailBundle(Bundle bundle, boolean isDelete, int adapterPosition,
            int discoveryVideoPosition, int likeFlag, int attionChangeFlag, Video mVideo) {
        bundle = bundle == null ? new Bundle() : bundle;
        bundle.putSerializable(KEY_VIDEO, isDelete ? null : mVideo);
        bundle.putInt(KEY_ADAPTER_POSITION, adapterPosition);
        bundle.putInt(KEY_DSICOVERY_VIDEO_POSITION, discoveryVideoPosition);
        bundle.putInt(KEY_LIKE_FLAG, likeFlag);
        bundle.putInt(KEY_ATTENTION_COUNT_CHANGE, attionChangeFlag);
        return bundle;
    }
    
    /** 关注数量变化bundle */
    public static Bundle buildAttentionCountChangeBundle(Bundle bundle, int attionChangeFlag) {
        if(bundle == null) {
            bundle = new Bundle();
        }
        bundle.putInt(KEY_ATTENTION_COUNT_CHANGE, attionChangeFlag);
        return bundle;
    }
    
    /** 用户列表bundle */
    public static Bundle buildVideoFavoriteBundle(int likeCountChange, int attentionCountChange) {
        Bundle bundle = new Bundle();
        bundle.putInt(KEY_LIKE_FLAG, likeCountChange);
        bundle = buildAttentionCountChangeBundle(bundle, attentionCountChange);
        return bundle;
    }
    
    /** 用户列表bundle */
    public static Bundle buildAttentionInUserDetailBundle(long userId, int attentionCountChange) {
        Bundle bundle = new Bundle();
        bundle.putLong(KEY_USERID, userId);
        bundle = buildAttentionCountChangeBundle(bundle, attentionCountChange);
        return bundle;
    }
    
}
