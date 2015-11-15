package com.feibo.joke.app;

public class Constant {

	public static final boolean DEBUG = true;
	public static final boolean TRACE = true;

	public static final boolean USE_REAL_SEVER = true;

	//屏蔽用户频繁点击的最短时间间隔 ms
	public static long CLICK_INTERVAL = 600;

	/** 用户协议地址 */
	public static final String URL_USER_PROTOCOL = "http://v.lengxiaohua.cn/statics/xieyi.html";

	/** 服务器地址 */
	public static final String REAL_SERVER = "http://api.v.lengxiaohua.cn:8080";
//	public static final String TEST_SERVER = "http://192.168.45.4:8086/api.php";
    public static final String TEST_SERVER = "http://test.v.lengxiaohua.cn/api.php";
	//http://wangjunxiong.lengxiaohua/api.php?  http://192.168.45.4:8086/api.php  http://test.v.lengxiaohua.cn

	public static final String PATH_AREA = "area.plist";

}
