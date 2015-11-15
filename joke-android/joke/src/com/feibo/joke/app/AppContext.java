package com.feibo.joke.app;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import fbcore.log.LogUtil;
import fbcore.security.Md5;
import fbcore.utils.Utils;

import com.feibo.ffmpeg.FfmpegUtils;
import com.feibo.joke.manager.SocialManager;
import com.feibo.joke.manager.work.UserManager;
import com.feibo.joke.model.User;
import com.feibo.joke.utils.DeviceUidGenerator;
import com.feibo.joke.utils.SPHelper;
import com.feibo.joke.view.util.ToastUtil;

public class AppContext {

    public static final String TAG = AppContext.class.getSimpleName();

    /** 服务器地址 */
    public static String SERVER_HOST;
    /** 应用版本号 */
    public static String APP_VERSION_CODE;
    /** 应用版本名 */
    public static String APP_VERSION_NAME;

    public static String UMENG_CHANNEL;

    private static Context context;
    private static AppContext appContext;

    public static String MODEL;
    public static String OS_VERSION;
    public static String OS_TYPE = "3";
    // 设备唯一ID，由硬件参数获取。用于向服务端获取cid(服务端发回的唯一ID)
    public static String DEVICE_ID;

    static {
        SERVER_HOST = Constant.USE_REAL_SEVER ? Constant.REAL_SERVER : Constant.TEST_SERVER;
    }

    private AppContext() {
    }

    public static Context getContext() {
        return context;
    }

    public synchronized static void init(Context context) {
        AppContext.context = context;
        initAppParames(context);
        initUser(context);
        // 安装ffmpeg命令行工具
        FfmpegUtils.installFfmpeg(context);
        DirContext.getInstance().initCacheDir(context);
        SocialManager.getInstance(context).initSocial();
        context.registerReceiver(new NetworkReceiver(), new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
    }

    private static void initUser(Context context) {
        User user = UserManager.getInstance().getUser();
        SPHelper.fillUser(context, user);
    }

    /** 初始化应用变量 */
    private static void initAppParames(Context context) {
        APP_VERSION_CODE = String.valueOf(Utils.getAppVersionCode(context));
        APP_VERSION_NAME = Utils.getAppVersionName(context);

        MODEL = android.os.Build.MODEL;
        MODEL = MODEL.replace(" ", "");

        OS_VERSION = android.os.Build.VERSION.RELEASE;
        try {
            DEVICE_ID = Md5.digest32(DeviceUidGenerator.generate(context));
        } catch (Exception e) {
            e.printStackTrace();
        }
         ApplicationInfo info;
        try {
            info = AppContext.context.getPackageManager()
                    .getApplicationInfo(AppContext.context.getPackageName(),
                    PackageManager.GET_META_DATA);
            UMENG_CHANNEL =info.metaData.getString("UMENG_CHANNEL");
            LogUtil.i(TAG, "channles : " + UMENG_CHANNEL);
        } catch (NameNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public synchronized static AppContext getInstance() {
        if (appContext == null) {
            appContext = new AppContext();
        }
        return appContext;
    }

    public static boolean isWifiActive() {
        return NetworkReceiver.isWifiActive;
    }

    /**
     * 解决hasAvailableNetwork的bug
     */
    public static boolean isNetworkAvailable() {
        return Utils.isNetworkAvailable(context);
    }

    public static boolean hasAvailableNetwork() {
        // if (!NetworkReceiver.hasAvailableNetwork) {
        // Toast.makeText(context, "当前无网络，请检查网络状态",
        // Toast.LENGTH_SHORT).show();
        // }
        return NetworkReceiver.hasAvailableNetwork;
    }

    private static String getCurProcessName(Context context) {
        int pid = android.os.Process.myPid();
        ActivityManager mActivityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningAppProcessInfo appProcess : mActivityManager.getRunningAppProcesses()) {
            if (appProcess.pid == pid) {
                return appProcess.processName;
            }
        }
        return null;
    }

    private static class NetworkReceiver extends BroadcastReceiver {
        private static boolean isWifiActive = false;
        private static boolean hasAvailableNetwork = false;
//        private static long lastTime = -1;

        @Override
        public void onReceive(Context context, Intent intent) {
            LogUtil.i("NetworkReceiver onReceive", "out");
            if (!"com.feibo.joke".equals(getCurProcessName(getContext()))) {//存在不同进程，同时接收广播，造成多次Toast，所以需要判断
                LogUtil.i("NetworkReceiver onReceive", "in");
                return;
            }
            onReceiveDo(intent);
        }

        private synchronized void onReceiveDo(Intent intent) {
//            long now = System.currentTimeMillis();
//            if (now - lastTime < 50) {
//                return;
//            }
//
//            lastTime = now;

            String action = intent.getAction();
            if (ConnectivityManager.CONNECTIVITY_ACTION.equals(action)) {
                updateNetwork();
            }
        }

        private void updateNetwork() {
            ConnectivityManager connectivity = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            if (connectivity != null) {
                NetworkInfo mNetworkInfo = connectivity.getActiveNetworkInfo();
                if (mNetworkInfo != null && mNetworkInfo.isConnected()) {
                    hasAvailableNetwork=mNetworkInfo.isAvailable();
                    String type = mNetworkInfo.getTypeName();
                    LogUtil.i("NetworkReceiver type", "type="+type);
                    if(type.equalsIgnoreCase("WIFI")){
//                        ToastUtil.showSimpleToast("正在使用WIFI网络");
                        isWifiActive=true;
                    }else if(type.equalsIgnoreCase("MOBILE")){
                        ToastUtil.showSimpleToast("正在使用移动蜂窝网络");
                        isWifiActive=false;
                    }else{
                        isWifiActive=false;
                    }
                }else{
                    ToastUtil.showSimpleToast("网络已断开");
                    hasAvailableNetwork=false;
                    isWifiActive=false;
                }
            }
        }
    }
}
