package com.feibo.snacks.app;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.util.DisplayMetrics;

import com.feibo.snacks.manager.global.AddressManager;
import com.feibo.snacks.manager.global.UserManager;
import com.feibo.snacks.model.dao.cache.DataDiskProvider;
import com.feibo.snacks.util.DeviceUidGenerator;
import com.feibo.social.base.Config;
import com.feibo.social.base.Platform;

import fbcore.security.Md5;
import fbcore.utils.Utils;

public class AppContext {

    public static final String TAG = AppContext.class.getSimpleName();

    private final static String CHANNEL_KEY = "UMENG_CHANNEL";


    /** 服务器地址 */
    public static String SERVER_HOST;
    /** 应用版本号 */
    public static String APP_VERSION_CODE;
    /** 应用版本名 */
    public static String APP_VERSION_NAME;

    /**获取渠道名*/
    public static String CHANNEL_NAME;

    private static Context context;
    private static AppContext appContext;

    public static String MODEL;
    public static String OS_TYPE = "3";
    public static String DEVICE_ID;

    public static int SCREEN_WIDTH;
    public static int SCREEN_HEIGHT;

    static {
        SERVER_HOST = Constant.USE_REAL_SEVER ? Constant.REAL_SERVER : Constant.TEST_SERVER;
    }

    public synchronized static AppContext getInstance() {
        if (appContext == null) {
            appContext = new AppContext();
        }
        return appContext;
    }

    private AppContext() {
    }

    public static Context getContext() {
        return context;
    }

    public synchronized static void init(Context context) {
        AppContext.context = context;

        initCache(context);
        initAppParams(context);
        initUser(context);
        initSocial();
        initSelectAddress();
        initScreenSize(context);
    }

    private static void initCache(Context context){
        DataDiskProvider.init(context);
    }

    // 初始化社会化分享配置
    private static void initSocial() {
        //初始化配置..buildScope("snsapi_userinfo")
        Config.config(
                Platform.SINA,
                new Config().buildAppKey("1727949816").buildRedirectUrl("http://xd.feibo.com/index.html")
                        .buildScope("all"));

        Config.config(Platform.QQ, new Config().buildAppId("1103595250").buildScope("all"));

        Config.config(Platform.WEIXIN,
                new Config().buildAppId("wxca0480439ed2296d").buildAppSecret("dee9362d6b58a9329fe6979d138f730b")
                        .buildScope("snsapi_userinfo"));
    }

    /** 初始化应用变量 */
    private static void initAppParams(Context context) {
        MODEL = android.os.Build.MODEL;
        MODEL = MODEL.replace(" ", "");
        APP_VERSION_CODE = String.valueOf(Utils.getAppVersionCode(context));
        APP_VERSION_NAME = Utils.getAppVersionName(context);

        CHANNEL_NAME = getChannelName(context);

        try {
            DEVICE_ID = Md5.digest32(DeviceUidGenerator.generate(context));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 初始化用户信息
    private static void initUser(Context context) {
        UserManager.getInstance().initUser(context);
    }

    private static void initSelectAddress() {
        AddressManager.getInstance().loadNewAddress();
    }

    private static String getChannelName(Context ctx) {
    	 if (ctx == null) {
             return null;
         }
         String resultData = null;
         try {
             PackageManager packageManager = ctx.getPackageManager();
             if (packageManager != null) {
                 ApplicationInfo applicationInfo = packageManager.getApplicationInfo(ctx.getPackageName(), PackageManager.GET_META_DATA);
                 if (applicationInfo != null) {
                     if (applicationInfo.metaData != null) {
                         resultData = applicationInfo.metaData.getString(CHANNEL_KEY);
                     }
                 }
             }
         } catch (PackageManager.NameNotFoundException e) {
             e.printStackTrace();
         }
         return resultData;
	}

    public static void initScreenSize(Context context) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        SCREEN_WIDTH = displayMetrics.widthPixels;
        SCREEN_HEIGHT = displayMetrics.heightPixels;
    }

    public static boolean isWifiActive() {
        return Utils.isWiFiActive(context);
    }

    public static boolean isNetworkAvailable() {
        return Utils.isNetworkAvailable(context);
    }
}
