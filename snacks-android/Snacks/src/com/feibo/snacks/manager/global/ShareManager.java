package com.feibo.snacks.manager.global;

import com.feibo.snacks.app.Constant;

import fbcore.log.LogUtil;

/**
 * Created by lidiqing on 15-9-1.
 */
public class ShareManager {
    private static final String TAG = ShareManager.class.getSimpleName();

    /**
     * 分享的网络页面
     *
     * @param id
     * @return
     */
    public static String buildSnacksWebUrl(int id) {
        String url = Constant.WEB_SNACKS + id;
        LogUtil.i(TAG, "buildSnacksWebUrl :  " + url);
        return url;
    }

    /**
     * 分享的专题的网络页面
     *
     * @param id
     * @return
     */
    public static String buildSnacksSubjectWebUrl(int id) {
        String url = Constant.WEB_SUBJECT_SNACKS + id;
        LogUtil.i(TAG, "buildSnacksSubjectWebUrl :  " + url);
        return url;
    }
}
