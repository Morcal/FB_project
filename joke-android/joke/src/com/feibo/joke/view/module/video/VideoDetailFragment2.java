package com.feibo.joke.view.module.video;

import android.os.Bundle;
import android.view.View;

import com.feibo.joke.app.BundleUtil;
import com.feibo.joke.view.module.mine.BaseLoginFragment;

/**
 * Created by lidiqing on 15-10-28.
 */
@Deprecated
public class VideoDetailFragment2 extends BaseLoginFragment {

    public final static String EXTRAS_KEY = "vedio_detail_show";


    public static final Bundle buildBundle(long videoId, int originAdapterId) {
        return buildBundle(videoId, originAdapterId, 0);
    }

    public static final Bundle buildBundle(long videoId, int originAdapterId, int discoveryVideoPosition) {
        Bundle bundle = new Bundle();
        bundle.putLong(VideoDetailFragment2.EXTRAS_KEY, videoId);
        bundle.putInt(BundleUtil.KEY_ADAPTER_POSITION, originAdapterId);
        bundle.putInt(BundleUtil.KEY_DSICOVERY_VIDEO_POSITION, discoveryVideoPosition);
        return bundle;
    }



    @Override
    public void loginResult(boolean result, int operationCode) {

    }

    @Override
    public View containChildView() {
        return null;
    }

    @Override
    public int setTitleLayoutId() {
        return 0;
    }

    @Override
    public void setTitlebar() {

    }



    // ----------------废弃或者无用的方法---------------------
    @Override
    public void onReleaseView() {

    }
}
