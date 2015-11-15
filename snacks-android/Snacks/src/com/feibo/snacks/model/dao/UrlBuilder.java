package com.feibo.snacks.model.dao;

import com.feibo.snacks.app.AppContext;
import com.feibo.snacks.manager.global.UserManager;
import com.feibo.snacks.model.bean.DeviceInfo;
import com.feibo.snacks.model.bean.NetResult;
import com.feibo.snacks.model.bean.Response;
import com.feibo.snacks.util.DeviceUidGenerator;
import com.feibo.snacks.util.SPHelper;
import com.feibo.snacks.util.TimeUtil;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import fbcore.security.Md5;
import fbcore.task.toolbox.GetStringTask;
import fbcore.utils.Strings;


public class UrlBuilder {

    private static final String DEFAULT_CID = "10002";
    private static final String DEFAULT_KEY = "uYz1ZS6AXNQGNlV8";

    private static StringBuilder buildPrefix() {
        String key = SPHelper.getAuthKey();
        long cidInt = SPHelper.getAuthDeviceId();

        // 这两个来自设备
        String cid = (cidInt == 0) ? DEFAULT_CID : (String.valueOf(cidInt));

        // 未注册设备时默认第一次用这个Key
        key = key == null ? DEFAULT_KEY : key;

        String tms = TimeUtil.generateTimestamp();
        String sig = getMd5Backward16(key + tms);

        // 这两个来自用户
        // String uid = "0";
        String uid = UserManager.getInstance().getUser().getUid() + "";
        String wssig = getMd5Backward16(SPHelper.getAuthWSKey() + tms);
        StringBuilder sb = new StringBuilder(AppContext.SERVER_HOST);
        sb.append("?cid=").append(cid).append("&uid=").append(uid).append("&tms=").append(tms).append("&sig=")
                .append(sig).append("&wssig=").append(wssig).append("&os_type=").append(AppContext.OS_TYPE)
                .append("&version=").append(AppContext.APP_VERSION_CODE).append("&channel_name=").append(AppContext.CHANNEL_NAME);
        return sb;
    }


    /**
     * 构造URL的公共部分
     * @return
     */
    public static StringBuilder getPublicParamUrl() {
        // 如果AuthDevice不合法，更新
        if(!isDeviceVerified()){
            try {
                verifyDevice();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return buildPrefix();
    }

    /**
     * 验证AuthDevice，不正确的话获取新的AuthDevice
     * 1002
     */
    private static void verifyDevice() throws Exception {
        String url = new StringBuilder(buildPrefix().append("&srv=1002").append("&imei=").append(DeviceUidGenerator.generate(AppContext.getContext())).append("&name=")
                .append(AppContext.MODEL)).toString();

        String result = new GetStringTask(url).execute();
        TypeToken<Response<DeviceInfo>> token = new TypeToken<Response<DeviceInfo>>(){};
        Response<DeviceInfo> deviceEntity = new Gson().fromJson(result, token.getType());

        if (deviceEntity != null && deviceEntity.code.equals(NetResult.SUCCESS_CODE)) {
            SPHelper.setAuthDeviceInfo(deviceEntity.data.cid, deviceEntity.data.key);
        }
    }

    private static boolean isDeviceVerified() {
        long cid = SPHelper.getAuthDeviceId();
        return cid != 0;
    }

    private static String getMd5Backward16(String src) {
        String md5 = Md5.digest32(src);
        if (!Strings.isMeaningful(md5)) {
            return null;
        }
        return md5.toLowerCase().substring(16);
    }
}
