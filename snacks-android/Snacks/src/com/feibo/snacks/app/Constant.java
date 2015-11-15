package com.feibo.snacks.app;

public class Constant {

    public static final boolean useTestData = false;
    public static final boolean DEBUG = false;
    public static final boolean TRACE = false;

    public static final boolean USE_REAL_SEVER = true;

    public static final String TAOKE_PID = "mm_100156872_0_0";

    /**
     * 服务器地址
     */
    public static final String REAL_SERVER = "http://api.ds.lingshi.cccwei.com/";
//    public static final String TEST_SERVER = "http://192.168.45.4:8092/api.php";//线上暂时修复的
   //public static final String TEST_SERVER = "http://zengnanlin.lingshi/api.php";// 测试环境.3
//    public static final String TEST_SERVER = "http://zengnanlin.lingshi:804/api.php";
//    public static final String TEST_SERVER = "http://192.168.45.4:8091/api.php";//测试
    public static final String TEST_SERVER = "http://testapi.ds.lingshi.cccwei.com/api.php";

    public static final String WEB_SNACKS = "http://ds.lingshi.cccwei.com/";
    public static final String WEB_SUBJECT_SNACKS = "http://ds.lingshi.cccwei.com/subject/";

    public static final String WEB_TAOBAO_ORDER_URL = "http://h5.m.taobao.com/awp/mtb/mtb.htm#!/awp/mtb/olist.htm?sta=4";

    //记录 详情页、 主题、 专题的访问量
    public static final int GOODS_DETAIL = 1;
    public static final int TOPIC = 2;
    public static final int SUBJECT_TYPE = 3;
    public static final int SUBJECT_TOPIC = 4;
    public static final int HOME_CLASSIFY = 5;
    public static final int CLASSIFY_CATEGORY = 6;
    public static final int CLASSIFY_BRAND = 7;

    //详情页来源
    public static final int TOPIC_LIST = 1;//首页banner点进去的活动列表商品
    public static final int DAY_DISCOUNT = 2;//首页每日一折，现改成今日特卖
    public static final int TASTY = 3;//首页好吃到爆
    public static final int SIX_CATE_OF_GOODS_LIST = 4;//首页6个分类的品类的商品列表
    public static final int SUBJECT_LIST = 7;//旧的专题列表
    public static final int GUESS_LIKE = 8;//详情页的猜你喜欢
    public static final int MY_COLLECT = 9;//收藏页
    public static final int SEARCH_RESULT = 10;//搜索结果页
    public static final int NOTIFICATION = 11;//推送通知
    public static final int HOME_BANNER = 13; //首页banner
    public static final int SUBJECT_BANNER = 14; //专题页banner
    public static final int TODYAY_SPECIAL_SELLING = 15; //今日特卖
    public static final int BRAND_GROUP_DETAIL = 16; //品牌团详情页
    public static final int HOME_SPECIAL = 17; //首页四个广告位
    public static final int SUBJECT_H5_LIST = 18;//H5专题列表
    public static final int WEB_ACTIVITY = 19;//WEB抽奖页

    //行为统计
    public static final int COLLECT = 1;
    public static final int SHARE = 2;
    public static final int TAOBAO_ORDER = 3;
    public static final int IMMEDIATE_BUY = 4;

    //banner或者广告位的跳转情况
    public static final int SUBJECT_OF_DETAIL = 1;
    public static final int SUBJECT_OF_LIST = 2;
    public static final int GOODS_OF_DETAIL = 3;
    public static final int GOODS_OF_LIST = 4;
    public static final int WEB_LINK = 5;
    public static final int COUPON_DETAIL = 6;
    public static final int COUPON_LIST = 7;
    public static final int COUPON_LIST_DETAIL = 8;//领取优惠券集合
    public static final int SHARE_GIFTS = 9;//领取优惠券集合
}
