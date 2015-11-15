package com.feibo.joke.dao;

import android.annotation.SuppressLint;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import fbcore.security.Md5;
import fbcore.task.toolbox.GetStringTask;
import fbcore.utils.Strings;

import com.feibo.joke.app.AppContext;
import com.feibo.joke.manager.work.UserManager;
import com.feibo.joke.model.DeviceInfo;
import com.feibo.joke.model.Response;
import com.feibo.joke.utils.SPHelper;
import com.feibo.joke.utils.TimeUtil;

public class UrlBuilder {

    public static final String TEST_PUBLIC_URL = "http://192.168.45.3:8086/api.php?cid=94&uid=27&tms=20150418163838&sig=4f67216fc1a780ba&wssig=9d4762dec885d311&os_type=3&version=1";

    public static StringBuilder getPublicParamUrl() {
        if(!isDeviceVerified()){
            verifyDevice();
        }
        return buildPrefix();
    }

    public static void refreshCid() {
        SPHelper.initAuthDeviceId(AppContext.getContext());
        verifyDevice();
    }

    public static boolean isRefreshCid() {
        return SPHelper.isRefreshCid(AppContext.getContext());
    }

    private static StringBuilder buildPrefix() {
        String key = SPHelper.getAuthKey(AppContext.getContext());
        long cidInt = SPHelper.getAuthDeviceId(AppContext.getContext());

        // 这两个来自设备
        String cid = (cidInt == 0) ? "10002" : (String.valueOf(cidInt));

        // 未注册设备时默认第一次用这个Key
        key = key == null ? "uYz1ZS6AXNQGNlV8" : key;

        String tms = TimeUtil.generateTimestamp();
        String sig = getMd5Backward16(key + tms);

        // 这两个来自用户
        // String uid = "0";
        String uid = UserManager.getInstance().getUser().id + "";
        String wssig = getMd5Backward16(SPHelper.getAuthWSKey(AppContext.getContext()) + tms);
        StringBuilder sb = new StringBuilder(AppContext.SERVER_HOST);
        sb.append("?cid=").append(cid).append("&uid=").append(uid).append("&tms=").append(tms).append("&sig=")
                .append(sig).append("&wssig=").append(wssig).append("&os_type=").append(AppContext.OS_TYPE)
                .append("&version=").append(AppContext.APP_VERSION_CODE)
                .append("&channel=").append(AppContext.UMENG_CHANNEL);
        return sb;
    }

    private static boolean isDeviceVerified() {
        long cid = SPHelper.getAuthDeviceId(AppContext.getContext());
        return cid != 0;
    }

    private static void verifyDevice() {
        String url = new StringBuilder(buildPrefix().append("&srv=1002").append("&device_id=")
                .append(AppContext.DEVICE_ID).append("&os_version=").append(AppContext.OS_VERSION).append("&model=")
                .append(AppContext.MODEL)).toString();

        String result = new GetStringTask(url).execute();
        TypeToken<Response<DeviceInfo>> token = new TypeToken<Response<DeviceInfo>>(){};
        Response<DeviceInfo> deviceEntity = new Gson().fromJson(result, token.getType());

        if (deviceEntity != null && deviceEntity.rsCode == ReturnCode.RS_SUCCESS) {
            SPHelper.setAuthDeviceInfo(AppContext.getContext(), deviceEntity.data.cid, deviceEntity.data.key);
        }
    }

    /**
     * 返回md5值的后16位.
     *
     * @param src
     * @return
     */
    @SuppressLint("DefaultLocale")
    private static String getMd5Backward16(String src) {
        String md5 = Md5.digest32(src);
        if (!Strings.isMeaningful(md5)) {
            return null;
        }
        return md5.toLowerCase().substring(16);
    }


    public static String buildJokeWebUrl(int i) {
        return "http://www.feibo.com";
    }
}
