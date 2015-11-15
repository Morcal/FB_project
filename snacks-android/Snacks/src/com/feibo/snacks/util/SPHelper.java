package com.feibo.snacks.util;

import android.content.Context;
import android.content.SharedPreferences;

import com.feibo.snacks.R;
import com.feibo.snacks.app.AppContext;
import com.feibo.snacks.model.bean.User;

public class SPHelper {

    public enum Pref {
        APP("app"), USER("user"), SEARCH_HISTORY("search_history");

        private String ns;

        private Pref(String ns) {
            this.ns = ns;
        }

        public String getNameSpace() {
            return this.ns;
        }
    }

    private static final Context CONTEXT = AppContext.getContext();

    private static final SharedPreferences getPref(Pref pref) {
        return CONTEXT.getSharedPreferences(pref.getNameSpace(), Context.MODE_PRIVATE);
    }

    public static void setAuthDeviceInfo(int deviceId, String key) {
        SharedPreferences sp = getPref(Pref.APP);
        sp.edit().putInt("auth_device_id", deviceId).putString("auth_key", key).commit();
    }

    public static final String getAuthUid() {
        SharedPreferences sp = getPref(Pref.APP);
        return sp.getString("auth_uid", "0");
    }

    public static final String getAuthWSKey() {
        SharedPreferences sp = getPref(Pref.APP);
        return sp.getString("auth_wskey", "0");
    }

    public static final int getAuthDeviceId() {
        SharedPreferences sp = getPref(Pref.APP);
        return sp.getInt("auth_device_id", 0);
    }

    public static final String getAuthKey() {
        SharedPreferences sp = getPref(Pref.APP);
        return sp.getString("auth_key", null);
    }

    /**
     * @return 最新启动图的开始时间
     */
    public static final long getLaunchStartTime() {
        SharedPreferences sp = getPref(Pref.APP);
        return sp.getLong("launch_start_time", 0);
    }

    /**
     * @return 最新启动图的结束时间
     */
    public static final long getLaunchEndTime() {
        SharedPreferences sp = getPref(Pref.APP);
        return sp.getLong("launch_end_time", 0);
    }

//    /**
//     * @param context
//     * @return 获得当前的提示文案
//     */
//    public static final int getCurPromp(Context context) {
//        SharedPreferences sp = getPref(context, Pref.APP);
//        return sp.getInt("curPrompt", 0);
//    }
//
//    /**
//     * 设置当前提示文案的标号
//     * 
//     * @param context
//     * @param curPrompt
//     */
//    public static final void setCurPrompt(Context context, int curPrompt) {
//        SharedPreferences sp = getPref(context, Pref.APP);
//        sp.edit().putInt("curPrompt", curPrompt).commit();
//    }

    /**
     * @return 最新启动图的图片地址
     */
    public static final String getLaunchPath() {
        SharedPreferences sp = getPref(Pref.APP);
        return sp.getString("launch_path", null);
    }

    /**
     * 设置启动图的开始时间
     *
     * @param startTime
     */
    public static final void setLaunchStartTime(long startTime) {
        SharedPreferences sp = getPref(Pref.APP);
        sp.edit().putLong("launch_start_time", startTime).commit();
    }

    /**
     * 设置启动图的结束时间
     *
     * @param endTime
     */
    public static final void setLaunchEndTime(long endTime) {
        SharedPreferences sp = getPref(Pref.APP);
        sp.edit().putLong("launch_end_time", endTime).commit();
    }

    public static final void setLaunchPath(String path) {
        SharedPreferences sp = getPref(Pref.APP);
        sp.edit().putString("launch_path", path).commit();
    }

    /**
     * 保存搜索历史
     */
    public static final void setSearchHistory(String historyWord) {
        SharedPreferences sp = getPref(Pref.SEARCH_HISTORY);
        sp.edit().putString("history", historyWord).commit();
    }

    /**
     * 获取搜索历史
     */
    public static final String getSearchHistory() {
        SharedPreferences sp = getPref(Pref.SEARCH_HISTORY);
        return sp.getString("history", "No History Search Word");
    }

    public static void clear() {
        SharedPreferences sp = getPref(Pref.SEARCH_HISTORY);
        SharedPreferences.Editor editor = sp.edit();
        editor.clear();
        editor.commit();
    }

    /**
     * 第三方授权/登录成功时，保存用户信息.
     *
     * @param user 用户
     */
    public static void saveUser(User user) {
        SharedPreferences sp = getPref(Pref.USER);
        sp.edit().putString("user_id", user.getUid()).putString("user_mobi", user.getMobiNum()).putString("user_nickname", user.getNickname())
                .putString("user_avatar", user.getAvatar()).putInt("platform", user.getPlatform()).commit();
    }

    public static String getUserName() {
        SharedPreferences sp = getPref(Pref.USER);
        return sp.getString("user_nickname", "default");
    }

    /**
     * 应用启动时初始化用户数据.
     *
     * @param user
     */
    public static void fillUser(User user) {
        SharedPreferences sp = getPref(Pref.USER);
        user.setUid(sp.getString("user_id", User.DEFAULT_USER_ID));
        user.setNickname(sp.getString("user_nickname", CONTEXT.getString(R.string.default_nickname)));
        user.setAvatar(sp.getString("user_avatar", ""));
        user.setPlatform(sp.getInt("platform", User.PLATFORM_TYPE_DEFAULT));
        user.setMobiNum(sp.getString("user_mobi", ""));
    }

    /**
     * 第三方全部解除授权/登出时，清空用户信息.
     */
    public static final void clearUser() {
        SharedPreferences sp = getPref(Pref.USER);
        sp.edit().remove("user_nickname").remove("user_id").remove("user_avatar").remove("platform").remove("user_mobi").commit();
    }

    public static void setauthUser(String uid, String wsKey) {
        SharedPreferences sp = getPref(Pref.APP);
        sp.edit().putString("auth_uid", uid).putString("auth_wskey", wsKey).commit();
    }

    public static void changeNickname(String nick) {
        SharedPreferences sp = getPref(Pref.USER);
        sp.edit().putString("user_nickname", nick).commit();
    }

    public static void changeAvatar(String avatar) {
        SharedPreferences sp = getPref(Pref.USER);
        sp.edit().putString("user_avatar", avatar).apply();
    }

    public static boolean isCollect(int goodsId) {
        SharedPreferences sp = getPref(Pref.USER);
        return sp.getBoolean(goodsId + "CollectGoods", false);
    }

    public static boolean isCollectSubject(int subjectId) {
        SharedPreferences sp = getPref(Pref.USER);
        return sp.getBoolean(subjectId + "subjectId", false);
    }

    public static boolean addCollect(int goodsId) {
        SharedPreferences sp = getPref(Pref.USER);
        return sp.edit().putBoolean(goodsId + "CollectGoods", true).commit();
    }

    public static void addShareSubject(int subjectId, int number) {
        SharedPreferences sp = getPref(Pref.USER);
        sp.edit().putInt(subjectId + "ShareSubject", number).commit();
    }

    public static int getShareSubjectNumber(int subjectId) {
        SharedPreferences sp = getPref(Pref.USER);
        return sp.getInt(subjectId + "ShareSubject", 0);
    }

    public static boolean addCollectSubject(int subjectsId) {
        SharedPreferences sp = getPref(Pref.USER);
        return sp.edit().putBoolean(subjectsId + "subjectId", true).commit();
    }

    public static void removeCollect(int goodsId) {
        SharedPreferences sp = getPref(Pref.USER);
        sp.edit().putBoolean(goodsId + "CollectGoods", false).commit();
    }

    public static void removeCollectSubject(int subjectsId) {
        SharedPreferences sp = getPref(Pref.USER);
        sp.edit().putBoolean(subjectsId + "subjectId", false).commit();
    }

    // 设置地址文件路径
    public static void setAddressFilePath(String path) {
        SharedPreferences sp = getPref(Pref.USER);
        sp.edit().putString("SelectAddress", path).commit();
    }

    // 读取地址文件路径
    public static String getAddressFilePath() {
        SharedPreferences sp = getPref(Pref.USER);
        return sp.getString("SelectAddress", "");
    }

    // 设置地址文件版本号
    public static void setAddressFileVersion(int version) {
        SharedPreferences sp = getPref(Pref.USER);
        sp.edit().putInt("SelectAddressVersion", version).commit();
    }

    // 获取地址文件版本号
    public static int getAddressFileVersion() {
        SharedPreferences sp = getPref(Pref.USER);
        return sp.getInt("SelectAddressVersion", 0);
    }

    public static boolean needGuide() {
        SharedPreferences sp = getPref(Pref.APP);
        return sp.getBoolean("needGuide", true);
    }

    public static void cancelGuide() {
        SharedPreferences sp = getPref(Pref.APP);
        sp.edit().putBoolean("needGuide", false).commit();
    }

    public static long getOrdersRemindTime(String ordersId) {
        SharedPreferences sp = getPref(Pref.APP);
        return sp.getLong(ordersId, -1);
    }

    public static void setOrdersRemindTime(String ordersId, long time) {
        SharedPreferences sp = getPref(Pref.APP);
        sp.edit().putLong(ordersId, time).commit();
    }
}
