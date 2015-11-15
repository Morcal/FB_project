package com.feibo.snacks.model.bean;

import java.util.HashMap;
import java.util.Map;

import fbcore.log.LogUtil;

/**
 * 网络信息处理
 * @author fuJun
 *
 */
public class NetResult {
	private static final String TAG = NetResult.class.getSimpleName();

	public static final String NOT_NETWORK_STRING = "没有网络，请检查网络";
	public static final String NOT_DATA_STRING = "NO DATA";
	public static final String SUCCESS_CODE = "1000";
	private static final String INVALID_DEVICE_CODE = "-112";
	private static final String DEFAULT_CODE = "0000";
	private static final String NOT_NET_CODE = "-1";
	private static final String NOT_DATA_CODE = "1004";
	private static final String CONFIRM_ORDER_ERROR_CODE = "5010";
	private static final String OFF_SHELF_CODE = "5007";
	private static final String SHELL_EMPTY_CODE = "5008";
	public static final String OFF_SHELF = "该商品已下架";
	public static final String SELL_EMPTY = "商品已售空";
	public static final String HAS_COLLECTED_STRING = "已收藏";
	private static final String HAS_COLLECTED = "2019";
	private static final String SUBJECT_ID_ERROR_CODE = "4001";

	private static NetResult SUBJECT_ID_ERROR = new NetResult(SUBJECT_ID_ERROR_CODE, NOT_DATA_STRING);
	private static NetResult HAS_COLLECTED_SHELF = new NetResult(HAS_COLLECTED, HAS_COLLECTED_STRING);
	private static NetResult GOODS_OFF_SHELF = new NetResult(OFF_SHELF_CODE, OFF_SHELF);
	private static NetResult GOODS_SELL_EMPTY = new NetResult(SHELL_EMPTY_CODE, SELL_EMPTY);
	private static NetResult DEFAULT_ERR = new NetResult(DEFAULT_CODE, "未知错误请联系管理员");
	private static NetResult CONFIRM_ORDER_ERROR = new NetResult(CONFIRM_ORDER_ERROR_CODE, "您的订单内有商品信息失效，请重新购买");

	public static NetResult NOT_DATA = new NetResult(NOT_DATA_CODE, NOT_DATA_STRING);
	public static NetResult NOT_NET = new NetResult(NOT_NET_CODE, NOT_NETWORK_STRING);
	public static NetResult NOT_PARSE = new NetResult("-2", "数据解析错误");

	public static NetResult INVALID_DEVICE = new NetResult(INVALID_DEVICE_CODE, "设备初始化失败，请联系管理员");
//	public static NetResult NEED_OS_TYPE = new NetResult(NEED_OS_TYPE_CODE, "需要系统类型");
	private static Map<String, NetResult> map = new HashMap<String, NetResult>();

	static {
		map.put(SUBJECT_ID_ERROR_CODE, SUBJECT_ID_ERROR);
		map.put(OFF_SHELF_CODE, GOODS_OFF_SHELF);
		map.put(SHELL_EMPTY_CODE, GOODS_SELL_EMPTY);
	    map.put(NOT_DATA_CODE, NOT_DATA);
	    map.put(DEFAULT_CODE, DEFAULT_ERR);
	    map.put(INVALID_DEVICE_CODE, INVALID_DEVICE);
	    map.put(CONFIRM_ORDER_ERROR_CODE, CONFIRM_ORDER_ERROR);
		map.put(HAS_COLLECTED,HAS_COLLECTED_SHELF);
//	    map.put(NEED_OS_TYPE_CODE, NEED_OS_TYPE);
	}

	public String responseCode;
	public String responseMsg;

	private NetResult(String responseCode, String responseMsg) {
		this.responseCode = responseCode;
		this.responseMsg = responseMsg;
	}

    public static String showFilterMsg(String url, String code, String msg) {
        LogUtil.i(TAG, "URL : " + url + " code : " + code + " responseMsg : " + msg);
        if (map.containsKey(code)) {
            return map.get(code).responseMsg;
        }
//        return DEFAULT_ERR.responseMsg;
		return msg;
    }
}
