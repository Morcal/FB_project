package com.feibo.snacks.model.dao.cache;

/**
     * @author canyang 一个场景一个类型
     * 这部分可以考虑用静态变量替换枚举优化
     */

    public interface BaseDataType {

    // 优惠券相关
    public enum DiscouponDataType implements BaseDataType {
        SPECIAL_OFFER,DETAIL,USING_RULE,RECEIVE_DISCOUPON,MY_COUPON,USE_COUPON,VALID_COUPON,INVALID_COUPON;

    }

    // 首页相关
    public enum HomeDataType implements BaseDataType {
        HOME_ABOVE, NEW_PRODUCTS, PROMOTION_DETAIL,BRAND_GROUP_DETAIL,BANNER_GOODS_LIST;

    }

    // 品类
    public enum CategoryDataType implements BaseDataType {
        CATEGORY_SELECT, CATEGORY_LIST;
    }

    // 搜索
    public enum SearchDataType implements BaseDataType {
        HOT_SEARCH, SEARCH_GUIDE, SEARCH
    }

    // 专题
    public enum SubjectDataType implements BaseDataType {
        SUBJECT, SUBJECT_GOODS_LIST, BANNER, SUBJECT_NEW,SUBJECT_DETAIL;
    }

    // 今日特卖
    public enum TodaySpecialDataType implements BaseDataType {
        SPECIAL_GOODS_LIST;
    }

    // 分类
    public enum ClassifyDataType implements BaseDataType {
        CLASSIFY_INFO, CATE_GOODS_LIST, BRAND_GOODS_LIST;
    }

    // 商品详情
    public enum GoodsDetailDataType implements BaseDataType {
        GOODS_DETAIL, BUYER_COMMENT,
    }

    // 应用
    public enum AppDataType implements BaseDataType {
        AUTHDEVICE, LAUNCH, UPDATE, REGISTER_IMEI, CONTACT, RED_POINT;
    }

    // 个人
    public enum PersonDataType implements BaseDataType {
        COLLECT, FEEDBACK, REGISTER, COLLECT_SUBJECT, IMG_CODE, ABOUT, AGREEMENT, INVISITED_SEND_GIFT,POST_AUTH_URL
    }

    // 购物车
    public enum ShoppingCartDataType implements BaseDataType {
        SHOPPING_CART, SHOPING_CART_NO_AVAILABLE
    }

    // 地址
    public enum AddressDataType implements BaseDataType {
        ADDRESS_LIST, ADDRESS;
        }

    //订单
    public enum OrdersDataType implements BaseDataType {
        ALL_ORDERS, WAIT_PAY_ORDERS, WAIT_SENG_ORDERS, WAIT_RECEIPT_ORDERS, WAIT_COMMENT, CONFIRM_ORDERS, LOGISTICS_DETAIL,SUPPLIER_GOODS_LIST
    }

    public enum OrdersDetailDataType implements  BaseDataType {
        ORDERS_DETAIL, ORDERS_COMMENT,
    }

    public enum PaymentDataType implements  BaseDataType {
        PAYMENT_DATA_TYPE, PAY_ORDER_TYPE, TO_BE_PAID_TYPE, PAY_RESULT_TYPE;
    }
}
