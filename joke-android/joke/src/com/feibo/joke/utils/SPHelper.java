package com.feibo.joke.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.feibo.joke.app.AppContext;
import com.feibo.joke.model.Image;
import com.feibo.joke.model.Push;
import com.feibo.joke.model.SplashImage;
import com.feibo.joke.model.User;
import com.feibo.joke.model.UserDetail;
import com.google.gson.Gson;

public class SPHelper {

    public enum Pref {
        APP("app"), USER("user");

        private String ns;

        private Pref(String ns) {
            this.ns = ns;
        }

        public String getNameSpace() {
            return this.ns;
        }
    }

    public static final SharedPreferences getPref(Context context, Pref pref) {
        return context.getSharedPreferences(pref.getNameSpace(), Context.MODE_PRIVATE);
    }

    public static void setAuthDeviceInfo(Context context, long deviceId, String key) {
        SharedPreferences sp = getPref(context, Pref.APP);
        sp.edit().putLong("auth_device_id", deviceId).putString("auth_key", key).commit();
    }

    public static void initAuthDeviceId(Context context) {
        SharedPreferences sp = getPref(context, Pref.APP);
        sp.edit().putLong("auth_device_id", 0).putString("auth_key", null).commit();
    }

    public static final String getAuthUid(Context context) {
        SharedPreferences sp = getPref(context, Pref.APP);
        return sp.getString("auth_uid", "0");
    }

    public static final String getAuthWSKey(Context context) {
        SharedPreferences sp = getPref(context, Pref.APP);
        return sp.getString("auth_wskey", "0");
    }

    public static final long getAuthDeviceId(Context context) {
        SharedPreferences sp = getPref(context, Pref.APP);
        return sp.getLong("auth_device_id", 0);
    }

    public static final String getAuthKey(Context context) {
        SharedPreferences sp = getPref(context, Pref.APP);
        return sp.getString("auth_key", null);
    }

    public static final boolean isRefreshCid(Context context) {
        SharedPreferences sp = getPref(context, Pref.APP);
        return sp.getBoolean("refresh_cid_at_v4.0.2", false);
    }

    public static final void refreshCid(Context context) {
        SharedPreferences sp = getPref(context, Pref.APP);
        sp.edit().putBoolean("refresh_cid_at_v4.0.2", true).commit();
    }

    /**
     * @param context
     * @return 最新启动图的开始时间
     */
    public static final long getLaunchStartTime(Context context) {
        SharedPreferences sp = getPref(context, Pref.APP);
        return sp.getLong("launch_start_time", 0);
    }

    /**
     * @param context
     * @return 最新启动图的结束时间
     */
    public static final long getLaunchEndTime(Context context) {
        SharedPreferences sp = getPref(context, Pref.APP);
        return sp.getLong("launch_end_time", 0);
    }

    /**
     * @param context
     * @return 最新启动图的图片地址
     */
    public static final String getLaunchPath(Context context) {
        SharedPreferences sp = getPref(context, Pref.APP);
        return sp.getString("launch_path", null);
    }

    /**
     * 保存搜索历史
     */
    public static final void setSearchHistory(Context context, String historyWord) {
        SharedPreferences sp = context.getSharedPreferences("search_history", Context.MODE_PRIVATE);
        sp.edit().putString("history", historyWord).commit();
    }

    /**
     * 获取搜索历史
     */
    public static final String getSearchHistory(Context context) {
        SharedPreferences sp = context.getSharedPreferences("search_history", Context.MODE_PRIVATE);
        return sp.getString("history", "No History Search Word");
    }

    public static void clear(Context context) {
        SharedPreferences sp = context.getSharedPreferences("search_history", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.clear();
        editor.commit();
    }

    /**
     * 第三方授权/登录成功时，保存用户信息.
     *
     * @param context
     * @param user 用户
     */
    public static void saveUser(Context context, User user) {
        SharedPreferences sp = getPref(context, Pref.USER);
        sp.edit().putLong("user_id", user.id)
                .putString("user_nickname", user.nickname)
                .putString("user_avatar", user.avatar)
                .putString("share_url", user.detail.shareUrl)
                .putInt("platform", user.platform)
                .putString("download_url", user.detail.downloadUrl)
                .putInt("is_sensation", user.isSensation)
                .putInt("user_sex", user.detail.gender)
                .putString("user_province", user.detail.province)
                .putString("user_city", user.detail.city)
                .putString("user_birth", user.detail.birth)
                .putString("user_signature", user.detail.signature)
                .putInt("user_be_like_count", user.detail.beLikeCount).commit();
    }

    /** 保存绑定关系 */
    public static void saveBanding(Context context, int bandingRelationship) {
        SharedPreferences sp = getPref(context, Pref.USER);
        sp.edit().putInt("band_relationship", bandingRelationship).commit();
    }

    /*** 获取是否绑定平台关系  */
    public static int getBanding(Context context) {
        SharedPreferences sp = getPref(context, Pref.USER);
        return sp.getInt("band_relationship", 0);
    }

    /** 取消绑定 */
    public static void clearBinding(Context context) {
        SharedPreferences sp = getPref(context, Pref.USER);
        sp.edit().putInt("band_relationship", 0).commit();
    }

    /**
     * 应用启动时初始化用户数据.
     *
     * @param context
     * @param user
     */
    public static void fillUser(Context context, User user) {
        SharedPreferences sp = getPref(context, Pref.USER);
        user.id = sp.getLong("user_id", 0);
        user.nickname = sp.getString("user_nickname", "");
        user.avatar = sp.getString("user_avatar", "");
        user.platform = sp.getInt("platform", 0);
        user.detail = new UserDetail();
        user.detail.gender = sp.getInt("user_sex", UserDetail.UN_KNOW);
        user.detail.province = sp.getString("user_province", "");
        user.detail.birth = sp.getString("user_birth", "");
        user.detail.city = sp.getString("user_city", "");
        user.detail.shareUrl = sp.getString("share_url", "");
        user.detail.signature = sp.getString("user_signature", "");
        user.detail.beLikeCount = sp.getInt("user_be_like_count", 0);
        user.detail.downloadUrl = sp.getString("download_url", "");
        user.isSensation = sp.getInt("is_sensation", 0);
    }

    /**
     * 第三方全部解除授权/登出时，清空用户信息.
     *
     * @param context
     */
    public static final void clearUser(Context context) {
        clearBinding(context);
        SharedPreferences sp = getPref(context, Pref.USER);
        sp.edit().remove("user_nickname").remove("user_id").remove("user_avatar").remove("platform")
                .remove("share_url").remove("is_sensation").remove("user_be_like_count").remove("download_url").commit();
    }

    /** 版本更新提醒 */
    public static void setRemindeVersion(Context context, boolean reminde) {
        SharedPreferences sp = getPref(context, Pref.USER);
        if (reminde) {
            sp.edit().putLong("remide_time", 0).commit();
        } else {
            sp.edit().putLong("remide_time", System.currentTimeMillis()).commit();
        }
        sp.edit().putBoolean("remide_version", reminde).commit();
    }

    /** 是否提示自动更新 */
    public static boolean getCanRemideVersion(Context context) {
        SharedPreferences sp = getPref(context, Pref.USER);
        boolean b = sp.getBoolean("remide_version", true);
        if (!b) {
            long old = sp.getLong("remide_time", System.currentTimeMillis() - 5000);
            boolean over = TimeUtil.overOneDay(old, System.currentTimeMillis());
            if (over) {
                setRemindeVersion(context, true);
            }
            return over;
        }
        return true;
    }

    public static void setauthUser(Context context, String uid, String wsKey) {
        SharedPreferences sp = getPref(context, Pref.APP);
        sp.edit().putString("auth_uid", uid).putString("auth_wskey", wsKey).commit();
    }

    public static void setImei(Context context, int imei_code) {
        SharedPreferences sp = getPref(context, Pref.APP);
        sp.edit().putInt("imei_code", imei_code).commit();
    }

    public static int getImei(Context context) {
        SharedPreferences sp = getPref(context, Pref.APP);
        return sp.getInt("imei_code", 0);
    }

    public static void saveChangeAvater(String path) {
        SharedPreferences sp = getPref(AppContext.getContext(), Pref.USER);
        sp.edit().putString("user_avatar", path).putInt("change_type", 1).commit();
    }

    public static int getChangeType(Context context) {
        SharedPreferences sp = getPref(context, Pref.USER);
        return sp.getInt("change_type", 0);
    }

    public static void setChangeType() {
        SharedPreferences sp = getPref(AppContext.getContext(), Pref.USER);
        sp.edit().putInt("change_type", 0).commit();
    }

    public static String getChangeAvatarPath(Context context) {
        SharedPreferences sp = getPref(context, Pref.USER);
        return sp.getString("user_avatar", null);
    }

    public static void changeNickname(String nick) {
        SharedPreferences sp = getPref(AppContext.getContext(), Pref.USER);
        sp.edit().putString("user_nickname", nick).commit();
    }

    public static boolean isCollect(Context context, int goodsId) {
        SharedPreferences sp = getPref(context, Pref.USER);
        return sp.getBoolean(goodsId + "", false);
    }

    public static boolean addCollect(Context context, int goodsId) {
        SharedPreferences sp = getPref(context, Pref.USER);
        return sp.edit().putBoolean(goodsId + "", true).commit();
    }

    public static void removeCollect(Context context, int goodsId) {
        SharedPreferences sp = getPref(context, Pref.USER);
        sp.edit().putBoolean(goodsId + "", false).commit();
    }

    public static boolean needGuide(Context context) {
        SharedPreferences sp = getPref(context, Pref.APP);
        return sp.getBoolean("needGuide", true);
    }

    public static void cancelGuide(Context context) {
        SharedPreferences sp = getPref(context, Pref.APP);
        sp.edit().putBoolean("needGuide", false).commit();
    }

    public static boolean showSplashLaunch(Context context) {
        SharedPreferences sp = getPref(context, Pref.APP);
        return sp.getBoolean("showSplashLaunch", true);
    }

    public static void cancelSplashLaunch(Context context) {
        SharedPreferences sp = getPref(context, Pref.APP);
        sp.edit().putBoolean("showSplashLaunch", false).commit();
    }

    public static void saveGetuiClientID(Context context, String clientId) {
        SharedPreferences sp = getPref(context, Pref.APP);
        sp.edit().putString("getui_client_id", clientId).commit();
    }

    public static String getGetuiClientID(Context context) {
        SharedPreferences sp = getPref(context, Pref.APP);
        return sp.getString("getui_client_id", null);
    }

    /** 设置消息通知开关 */
    public static void setPushNotice(Context context, int pushType, boolean open) {
        SharedPreferences sp = getPref(context, Pref.APP);
        if (pushType == Push.NOTICE_NEW_MESSGAE) {
            sp.edit().putBoolean("push_new_message", open).commit();
        } else if (pushType == Push.NOTICE_FANS) {
            sp.edit().putBoolean("push_fans", open).commit();
        } else if (pushType == Push.NOTICE_LIKE) {
            sp.edit().putBoolean("push_like", open).commit();
        } else if (pushType == Push.NOTICE_NEW_SYSTEM_MESSAGE) {
            sp.edit().putBoolean("push_new_notice", open).commit();
        }
    }

    /** 获取相应消息通知开关的状态 */
    public static boolean getPushNotice(Context context, int pushType) {
        SharedPreferences sp = getPref(context, Pref.APP);

        boolean open = true;
        if (pushType == Push.NOTICE_NEW_MESSGAE) {
            open = sp.getBoolean("push_new_message", true);
        } else if (pushType == Push.NOTICE_FANS) {
            open = sp.getBoolean("push_fans", true);
        } else if (pushType == Push.NOTICE_LIKE) {
            open = sp.getBoolean("push_like", true);
        } else if (pushType == Push.NOTICE_NEW_SYSTEM_MESSAGE) {
            open = sp.getBoolean("push_new_notice", true);
        }
        return open;
    }

    /** 设置在wifi下自动播放视频 */
    public static void setPlayVideoOnWifi(Context context, boolean open) {
        SharedPreferences sp = getPref(context, Pref.APP);
        sp.edit().putBoolean("play_on_wifi", open).commit();
    }

    /** 获取在wifi下自动播放视频 */
    public static boolean getPlayVideoOnWifi(Context context) {
        SharedPreferences sp = getPref(context, Pref.APP);
        return sp.getBoolean("play_on_wifi", true);
    }

    /** 获取在wifi下自动播放视频 */
    public static boolean getPlayVideoOnWifi() {
        SharedPreferences sp = getPref(AppContext.getContext(), Pref.APP);
        return sp.getBoolean("play_on_wifi", true);
    }

    /** 设置保存拍摄的视频到本地 */
    public static void setSaveVideoLocal(Context context, boolean save) {
        SharedPreferences sp = getPref(context, Pref.APP);
        sp.edit().putBoolean("save_video_local", save).commit();
    }

    /** 获取保存拍摄的视频到本地 */
    public static boolean getSaveVideoLocal(Context context) {
        SharedPreferences sp = getPref(context, Pref.APP);
        return sp.getBoolean("save_video_local", true);
    }

    /** 设置红点数据保存 */
    public static void setMessageHint(Context context, String spName, int hintNum) {
        SharedPreferences sp = getPref(context, Pref.APP);
        if(hintNum < 0) {
            hintNum = 0;
        }
        sp.edit().putInt(spName, hintNum).commit();
    }

    /** 获取红点数据保存 */
    public static int getMessageHint(Context context, String spName) {
        SharedPreferences sp = getPref(context, Pref.APP);
        return sp.getInt(spName, 0);
    }

    /** 设置用户消息上次已读的最后一项ID */
    public static void setReadPositionInUserMessage(Context context, long lastId) {
        SharedPreferences sp = getPref(context, Pref.APP);
        sp.edit().putLong("read_message_user_position", lastId).commit();
    }

    /** 设置获取消息上次已读的最后一项ID */
    public static long getReadPositionInUserMessage(Context context) {
        SharedPreferences sp = getPref(context, Pref.APP);
        return sp.getLong("read_message_user_position", 0);
    }

    /** 设置启动图配置 */
    public static void setLaunchImage(Context context, SplashImage image) {
        SharedPreferences sp = getPref(context, Pref.APP);
        if(image != null && image.image != null && !StringUtil.isEmpty(image.image.url)) {
            sp.edit().putString("launch_url_bg", image.image.url).commit();
            sp.edit().putString("launch_url_path", image.imagePath).commit();
            sp.edit().putLong("launch_start_time", image.startTime).commit();
            sp.edit().putLong("launch_end_time", image.endTime).commit();
        } else {
            sp.edit().putString("launch_url_bg", null).commit();
            sp.edit().putString("launch_url_path", null).commit();
            sp.edit().putLong("launch_start_time", 0).commit();
            sp.edit().putLong("launch_end_time", 0).commit();
        }
    }

    /** 获取启动图配置 */
    public static SplashImage getLaunchImage(Context context) {
        SharedPreferences sp = getPref(context, Pref.APP);

        SplashImage image = null;
        String url = sp.getString("launch_url_bg", null);
        if(url != null) {
            image = new SplashImage();
            Image img = new Image();
            img.url = url;
            image.image = img;
            image.imagePath = sp.getString("launch_url_path", null);
            image.endTime = sp.getLong("launch_end_time", 0);
            image.startTime = sp.getLong("launch_start_time", 0);
        }
        return image;
    }

    /** 设置是否隐藏话题大保健功能 */
    public static void setIsShowTopic(Context context, boolean isHind) {
        SharedPreferences sp = getPref(context, Pref.APP);
        sp.edit().putBoolean("is_show_topic", isHind).commit();
    }

    /** 设置是否隐藏话题大保健功能 */
    public static boolean isShowTopic(Context context) {
        SharedPreferences sp = getPref(context, Pref.APP);
        return sp.getBoolean("is_show_topic", true);
    }

    /** 设置最后一次弹窗的ID */
    public static void setPopupId(Context context, long id) {
        SharedPreferences sp = getPref(context, Pref.APP);
        sp.edit().putLong("popup_id", id).commit();
    }

    /** 获取上一次弹窗的ID */
    public static long getPopupId(Context context) {
        SharedPreferences sp = getPref(context, Pref.APP);
        return sp.getLong("popup_id", 0);
    }

    // 设置地址文件路径
    public static void setAddressFilePath(String path) {
        SharedPreferences sp = getPref(AppContext.getContext(), Pref.USER);
        sp.edit().putString("SelectAddress", path).commit();
    }

    // 读取地址文件路径
    public static String getAddressFilePath() {
        SharedPreferences sp = getPref(AppContext.getContext(), Pref.USER);
        return sp.getString("SelectAddress", "");
    }
}
