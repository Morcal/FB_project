package com.feibo.snacks.model.dao;

import android.text.TextUtils;

import com.feibo.snacks.model.bean.AboutInfo;
import com.feibo.snacks.model.bean.Address;
import com.feibo.snacks.model.bean.BaseComment;
import com.feibo.snacks.model.bean.CartItem;
import com.feibo.snacks.model.bean.CartSuppliers;
import com.feibo.snacks.model.bean.DiscountCoupon;
import com.feibo.snacks.model.bean.EntityArray;
import com.feibo.snacks.model.bean.Feedback;
import com.feibo.snacks.model.bean.Goods;
import com.feibo.snacks.model.bean.GoodsDetail;
import com.feibo.snacks.model.bean.ImgCodeImage;
import com.feibo.snacks.model.bean.ItemOrder;
import com.feibo.snacks.model.bean.Note;
import com.feibo.snacks.model.bean.OrdersDetail;
import com.feibo.snacks.model.bean.PayResult;
import com.feibo.snacks.model.bean.RedPointInfo;
import com.feibo.snacks.model.bean.Response;
import com.feibo.snacks.model.bean.ServiceContact;
import com.feibo.snacks.model.bean.SignInfo;
import com.feibo.snacks.model.bean.Special;
import com.feibo.snacks.model.bean.SplashImage;
import com.feibo.snacks.model.bean.Status;
import com.feibo.snacks.model.bean.StatusBean;
import com.feibo.snacks.model.bean.SubClassify;
import com.feibo.snacks.model.bean.Subject;
import com.feibo.snacks.model.bean.UpdateInfo;
import com.feibo.snacks.model.bean.UrlBean;
import com.feibo.snacks.model.bean.User;
import com.feibo.snacks.model.bean.group.BrandDetail;
import com.feibo.snacks.model.bean.group.ExpressDetail;
import com.feibo.snacks.model.bean.group.HomePageHead;
import com.feibo.snacks.util.Util;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import fbcore.conn.http.HttpParams;
import fbcore.log.LogUtil;

/**
 * 从网络读取实体类，每个方法均为异步操作，并且可以选择是否从缓存读取
 * Created by lidiqing on 15-8-28.
 */
public class SnacksDao {

    private static final String TAG = SnacksDao.class.getSimpleName();

    /**
     * 检查更新 1001
     *
     * @param listener
     */
    public static void getUpdateInfo(DaoListener<UpdateInfo> listener) {
        String url = new StringBuilder().append("&srv=1001").toString();
        Dao.getEntity(url, new TypeToken<Response<UpdateInfo>>() {
        }, listener, false);
    }

    /**
     * 获取启动图配置 1004
     */
    public static void getSplashImage(String radio, DaoListener<SplashImage> listener) {
        String url = "&srv=1004&ratio=" + radio;
        Dao.getEntity(url, new TypeToken<Response<SplashImage>>() {
        }, listener, false);
    }

    /**
     * 提交推送码 1007
     */
    public static void pushCode(String id, DaoListener<Object> listener) {
        String url = new StringBuilder().append("&srv=1007").append("&code=").append(id).toString();
        Dao.getEntity(url, new TypeToken<Response<Object>>() {
        }, listener, false);
    }

    public static void controlShareHaveGift(DaoListener<Status> listener) {
        String url = new StringBuilder().append("&srv=1008").toString();
        Dao.getEntity(url, new TypeToken<Response<Status>>() {
        }, listener, false);
    }

    /**
     * 用户登录/注册（第三方平台） 1101
     *
     * @param pfType 0，默认；1，QQ；2，微信；3，微博
     * @param openId
     * @param nickName
     * @param icon
     * @param accessToken
     * @return 用户注册url
     */
    public static void register(int pfType, String openId, String nickName, String icon, String accessToken, DaoListener<User> listener) {
        if (!TextUtils.isEmpty(nickName)) {
            nickName = URLEncoder.encode(nickName);
        }
        String url = new StringBuilder().append("&srv=1101").append("&pf_type=").append(pfType).append("&openid=").append(openId).append("&nickname=")
                .append(nickName).append("&icon=").append(icon).append("&access_token=").append(accessToken).toString();
        Dao.getEntity(url, new TypeToken<Response<User>>() {
        }, listener, false);
    }

    /**
     * 用户反馈 1102
     *
     * @param listener
     */
    public static void getFeedback(DaoListener<EntityArray<Feedback>> listener) {
        String url = new StringBuilder().append("&srv=1102").append("&content=")
                .toString();
        Dao.getEntity(url, new TypeToken<Response<EntityArray<Feedback>>>() {
        }, listener, false);
    }

    /**
     * 用户反馈 1102
     *
     * @param content  上传反馈内容
     * @param listener
     */
    public static void addFeedback(String content, DaoListener<EntityArray<Feedback>> listener) {
        String url = new StringBuilder().append("&srv=1102").append("&content=").append(content)
                .toString();
        Dao.getEntity(url, new TypeToken<Response<EntityArray<Feedback>>>() {
        }, listener, false);
    }

    /**
     * 专题分享 1107
     *
     * @param id  上传反馈内容
     * @param listener
     */
    public static void addSubjectShare(long id,int type, DaoListener<Object> listener) {
        String url = new StringBuilder().append("&srv=1107").append("&fav_id=").append(id).append("&type=").append(type)
                .toString();
        Dao.getEntity(url, new TypeToken<Response<Object>>() {
        }, listener, false);
    }

    /**
     * 主题列表、专题列表和详情页访问量 1601
     *
     * @param listener
     */
    public static void statisticQuantity(long id, int type, int enterSource, DaoListener<Object> listener) {
        String url = new StringBuilder().append("&srv=1601").append("&id=").append(id).append("&type=").append(type).append("&enter_source=").append(enterSource).toString();
        Dao.getEntity(url, new TypeToken<Response<Object>>() {
        }, listener, false);
    }

    /**
     * 时长统计 1602
     *
     * @param listener
     */
    public static void statisticVisitTimeLength(int id, long stayTime, DaoListener<Object> listener) {
        String url = new StringBuilder().append("&srv=1602").append("&id=").append(id).append("&stay_time=").append(stayTime).toString();
        Dao.getEntity(url, new TypeToken<Response<Object>>() {
        }, listener, false);
    }

    /**
     * 详情页的行为统计 1603
     * @param id
     * @param type 1:收藏 2:分享 3:淘宝下单 4:立刻购买
     * @param aboutUrl
     * @param listener
     */
    public static void statisticBehaviour(long id, int type, String aboutUrl, DaoListener<Object> listener) {
        String url = new StringBuilder().append("&srv=1603").append("&id=").append(id).append("&type=").append(type).append("&url=").append(aboutUrl).toString();
        Dao.getEntity(url, new TypeToken<Response<Object>>() {
        }, listener, false);
    }

    /**
     * 获取服务信息 2001
     */
    public static void getServiceContact(DaoListener<ServiceContact> listener) {
        String url = "&srv=2001";
        Dao.getEntity(url, new TypeToken<Response<ServiceContact>>() {
        }, listener, false);
    }

    /**
     * 获取关于零食小喵信息 2002
     *
     * @param listener
     */
    public static void getAboutInfo(DaoListener<AboutInfo> listener) {
        String url = "&srv=2002";
        Dao.getEntity(url, new TypeToken<Response<AboutInfo>>() {
        }, listener, false);
    }

    /**
     * 创建手机登录 2102
     *
     * @param num      电话号码
     * @param pwd      密码
     * @param listener
     */
    public static void mobileLogin(String num, String pwd, DaoListener<User> listener) {
        String url = new StringBuilder().append("&srv=2102").append("&mobi_num=").append(num).append("&pwd=").append(pwd)
                .append("&opt=0").toString();
        LogUtil.i(TAG, "getMobiCommit : " + url);
        Dao.getEntity(url, new TypeToken<Response<User>>() {
        }, listener, false);
    }

    /**
     * 创建手机注册   2102
     *
     * @param num      电话号码
     * @param pwd      密码
     * @param code     验证码:短信验证码，注册和忘记密码必须
     * @param listener
     */
    public static void mobileRegister(String num, String pwd, String code,DaoListener<User> listener) {
        String url = new StringBuilder().append("&srv=2102").append("&mobi_num=").append(num).append("&pwd=").append(pwd).append("&msg_code=").append(code)
                .append("&opt=1").toString();
        LogUtil.i(TAG, "getMobiCommit : " + url);
        Dao.getEntity(url, new TypeToken<Response<User>>() {
        }, listener, false);
    }

    /**
     * 忘记密码  2102
     *
     * @param num      电话号码
     * @param pwd      密码
     * @param code     验证码:短信验证码，注册和忘记密码必须
     * @param listener
     */
    public static void forgetPassword(String num, String pwd, String code, DaoListener<User> listener) {
        String url = new StringBuilder().append("&srv=2102").append("&mobi_num=").append(num).append("&pwd=").append(pwd).append("&msg_code=").append(code)
                .append("&opt=2").toString();
        LogUtil.i(TAG, "getMobiCommit : " + url);
        Dao.getEntity(url, new TypeToken<Response<User>>() {
        }, listener, false);
    }

    /**
     * 绑定手机号    2102
     *
     * @param num      电话号码
     * @param pwd      密码
     * @param code     验证码:短信验证码，注册和忘记密码必须
     * @param listener
     */
    public static void bindMobile(String num, String pwd, String code,DaoListener<User> listener) {
        String url = new StringBuilder().append("&srv=2102").append("&mobi_num=").append(num).append("&pwd=").append(pwd).append("&msg_code=").append(code)
                .append("&opt=3").toString();
        LogUtil.i(TAG, "getMobiCommit : " + url);
        Dao.getEntity(url, new TypeToken<Response<User>>() {
        }, listener, false);
    }

    /**
     * 获取短信验证码 2103
     *
     * @param num      手机号
     * @param imgCode  图片验证码，可选，获取手机验证码时需要
     * @param listener
     */
    public static void getVarifyCodeSMS(String num, String imgCode, DaoListener<ImgCodeImage> listener) {
        String url = new StringBuilder().append("&srv=2103").append("&mobi_num=").append(num).append("&img_code=").append(imgCode).toString();

        Dao.getEntity(url, new TypeToken<Response<ImgCodeImage>>() {
        }, listener, false);
    }

    /**
     * 获取获图片验证码 2103
     *
     * @param listener
     */
    public static void getVarifyCodeImage(DaoListener<ImgCodeImage> listener) {
        String url = new StringBuilder().append("&srv=2103").toString();

        Dao.getEntity(url, new TypeToken<Response<ImgCodeImage>>() {
        }, listener, false);
    }

    /**
     * 获取首页信息(除今日上新列表)  2201
     *
     * @param listener
     */
    public static void getHomePageHead(DaoListener<HomePageHead> listener) {
        String url = new StringBuilder().append("&srv=2201").toString();
        Dao.getEntity(url, new TypeToken<Response<HomePageHead>>() {
        }, listener, false);
    }

    /**
     * 获取搜索  2204
     *
     * @param keyword
     * @param sinceId
     * @param listener
     */
    public static void getSearchResult(String keyword, long sinceId, int curPage, DaoListener<EntityArray<Goods>> listener) {
        String url = new StringBuilder().append("&srv=2204").append("&since_id=").append(sinceId).append("&pg_cur=")
                .append(curPage).append("&pg_size=20").append("&keyword=").append(URLEncoder.encode(keyword)).toString();
        LogUtil.i(TAG, "getSearchResult : " + url);
        Dao.getEntity(url, new TypeToken<Response<EntityArray<Goods>>>() {
        }, listener, false);
    }

    /**
     * 通过品牌id获取品牌团详情    2205
     *
     * @param brandId
     * @param sinceId
     * @return
     */
    public static void getBrandDetail(long brandId, long sinceId, int curPage, DaoListener<BrandDetail> listener) {
        String url = new StringBuilder().append("&srv=2205").append("&brand_id=").append(brandId).append("&pg_cur=")
                .append(curPage).append("&pg_size=20").append("&since_id=").append(sinceId).toString();
        LogUtil.i(TAG, "getBrandDetail : " + url);
        Dao.getEntity(url, new TypeToken<Response<BrandDetail>>() {
        }, listener, false);
    }

    /**
     * 获取首页今日上新 2206
     *
     * @param sinceId
     * @param listener
     */
    public static void getNewGoods(long sinceId, int curPage, DaoListener<EntityArray<Goods>> listener) {
        String url = new StringBuilder().append("&srv=2206").append("&since_id=").append(sinceId).append("&pg_cur=")
                .append(curPage).append("&pg_size=20").toString();
        Dao.getEntity(url, new TypeToken<Response<EntityArray<Goods>>>(){
        },listener,false);
    }

    /**
     * 获取特卖 2301
     *
     * @param sinceId
     * @param listener
     */
    public static void getDiscountList(long sinceId, int curPage, DaoListener<EntityArray<Goods>> listener) {
        String url = new StringBuilder().append("&srv=2301").append("&since_id=").append(sinceId).append("&pg_cur=")
                .append(curPage).append("&pg_size=20").toString();
        LogUtil.i(TAG, "getDiscountList : " + url);
        Dao.getEntity(url, new TypeToken<Response<EntityArray<Goods>>>() {
        }, listener, false);
    }

    /**
     * 获取专题页顶部大专题 2401
     *
     * @return
     */
    public static void getDiscoveryHead(DaoListener<EntityArray<Special>> listener) {
        String url = new StringBuilder().append("&srv=2401").toString();
        LogUtil.i(TAG, "getDiscoveryHead : " + url);
        Dao.getEntity(url, new TypeToken<Response<EntityArray<Special>>>() {
        }, listener, false);
    }

    /**
     * 通过分类id获取其子分类 2402
     *
     * @param classifyId
     * @return
     */
    public static void getClassifyList(long classifyId, DaoListener<EntityArray<SubClassify>> listener) {
        String url = new StringBuilder().append("&srv=2402").append("&classify_id=").append(classifyId).toString();
        LogUtil.i(TAG, "getClassifyList : " + url);
        Dao.getEntity(url, new TypeToken<Response<EntityArray<SubClassify>>>() {
        }, listener, false);
    }

    /**
     * 通过大专题id获取小专题列表 2403
     *
     * @param specialId
     * @param sinceId
     * @return
     */
    public static void getSubjectList(long specialId, long sinceId, int curPage, DaoListener<EntityArray<Subject>> listener) {
        String url = new StringBuilder().append("&srv=2403").append("&pg_cur=")
                .append(curPage).append("&pg_size=20").append("&special_id=").append(specialId).append("&since_id=").append(sinceId).toString();
        LogUtil.i(TAG, "getSubjectList : " + url);
        Dao.getEntity(url, new TypeToken<Response<EntityArray<Subject>>>() {
        }, listener, false);
    }

    /**
     * 获取专题页的专题列表  2405
     *
     * @param sinceId
     * @return
     */
    public static void getDiscoverySubjectList(long sinceId, int curPage, DaoListener<EntityArray<Subject>> listener) {
        String url = new StringBuilder().append("&srv=2405").append("&pg_cur=")
                .append(curPage).append("&pg_size=20").append("&since_id=").append(sinceId).toString();
        LogUtil.i(TAG, "getDiscoverySubjectList : " + url);
        Dao.getEntity(url, new TypeToken<Response<EntityArray<Subject>>>() {
        }, listener, false);
    }

    /**
     * 获取子分类的商品列表   2406
     *
     * @param subId    子分类id，为0时获取父分类所有商品列表
     * @param parentId 父分类id
     * @param sinceId
     * @return
     */
    public static void getSubClassifyGoodsList(long subId, long parentId, long sinceId, int curPage, DaoListener<EntityArray<Goods>> listener) {
        String url = new StringBuilder().append("&srv=2406").append("&pg_cur=")
                .append(curPage).append("&pg_size=20").append("&sub_id=").append(subId).append("&parent_id=").append(parentId).append("&since_id=")
                .append(sinceId).toString();
        LogUtil.i(TAG, "getSubClassifyGoodsList : " + url);
        Dao.getEntity(url, new TypeToken<Response<EntityArray<Goods>>>() {
        }, listener, false);
    }

    /**
     * 通过大专题id获取商品列表 2407
     *
     * @param specialId
     * @param sinceId
     * @param listener
     * @return
     */
    public static void getBannerGoodsList(long specialId, long sinceId, int curPage, DaoListener<EntityArray<Goods>> listener) {
        String url = new StringBuilder().append("&srv=2407").append("&pg_cur=")
                .append(curPage).append("&pg_size=20").append("&subject_id=").append(specialId).append("&since_id=").append(sinceId).toString();
        LogUtil.i(TAG, "getBannerGoodsList : " + url);
        Dao.getEntity(url, new TypeToken<Response<EntityArray<Goods>>>() {
        }, listener, false);
    }

    /**
     * 通过专题id获取专题详情 2408
     *
     * @param subjectId
     * @param listener
     * @return
     */
    public static void getSubjectH5Detail(long subjectId, DaoListener<Subject> listener) {
        String url = new StringBuilder().append("&srv=2408").append("&subject_id=").append(subjectId).toString();
        LogUtil.i(TAG, "getSubjectH5Detail : " + url);
        Dao.getEntity(url, new TypeToken<Response<Subject>>() {
        }, listener, false);
    }

    /**
     * 通过商品id获取普通商品更多详情 2505
     *
     * @param goodsId
     * @return
     */
    public static void getGoodsDetail(long goodsId, DaoListener<GoodsDetail> listener) {
        String url = new StringBuilder().append("&srv=2505").append("&goods_id=").append(goodsId).toString();
        LogUtil.i(TAG, "getGoodsDetail : " + url);
        Dao.getEntity(url, new TypeToken<Response<GoodsDetail>>() {
        }, listener, false);
    }

    /**
     * 加入购物车 2601
     *
     * @param goodsId
     * @param kindId
     * @param subkindId
     * @param num
     * @return
     */
    public static void add2Cart(long goodsId, long kindId, long subkindId, int num, DaoListener<Object> listener) {
        String url = new StringBuilder().append("&srv=2601").append("&goods_id=").append(goodsId).append("&kind_id=").append(kindId).append("&subkind_id=")
                .append(subkindId).append("&num=").append(num).toString();
        LogUtil.i(TAG, "getAdd2Cart : " + url);
        Dao.getEntity(url, new TypeToken<Response<Object>>() {
        }, listener, false);
    }

    /**
     * 修改购物车中单个商品数量 2602
     *
     * @param cartId
     * @param num
     * @return
     */
    public static void updateNum2Cart(long cartId, int num, DaoListener<Object> listener) {
        String url = new StringBuilder().append("&srv=2602").append("&item_id=").append(cartId).append("&num=").append(num).toString();
        LogUtil.i(TAG, "getChangeNum2Cart : " + url);
        Dao.getEntity(url, new TypeToken<Response<Object>>() {
        }, listener, false);
    }

    /**
     * 从购物车删除(单个或批量) 2603
     *
     * @param ids
     * @return
     */
    public static void delete2Cart(List<Long> ids, DaoListener<Object> listener) {
        String url = new StringBuilder().append("&srv=2603").append("&ids=").append(createIdString(ids)).toString();
        LogUtil.i(TAG, "getDelete2Cart : " + url);
        Dao.getEntity(url, new TypeToken<Response<Object>>() {
        }, listener, false);
    }

    /**
     * 获取有效购物车列表 2604
     *
     * @return
     */
    public static void getCartListValid(DaoListener<EntityArray<CartSuppliers>> listener) {
        String url = new StringBuilder().append("&srv=2604").toString();
        LogUtil.i(TAG, "getCartList : " + url);
        Dao.getEntity(url, new TypeToken<Response<EntityArray<CartSuppliers>>>() {
        }, listener, false);
    }

    /**
     * 提交购物车到支付页面(客户端点击使用优惠券)   2605
     *
     * @param addressId 地址条目id,为-1时使用默认地址。
     * @param ids       购物车条目id的list
     * @param discouponId  优惠券的id
     * @return
     */
    public static void commitCart2OrderUrl(String addressId, String ids,String discouponId, DaoListener<OrdersDetail> listener) {
        String url = new StringBuilder().append("&srv=2605").append("&coupon_id=").append(discouponId).append("&address_id=").append(addressId).append("&ids=").append(ids).toString();
        LogUtil.i(TAG, "commitCart2OrderUrl : " + url);
        Dao.getEntity(url, new TypeToken<Response<OrdersDetail>>() {
        }, listener, false);
    }

    /**
     * 获取全部红点信息 2607
     *
     * @return
     */
    public static void getRedPointAll(DaoListener<RedPointInfo> listener) {
        String url = new StringBuilder().append("&srv=2607").append("&type=0").toString();
        LogUtil.i(TAG, "getRedPoint : " + url);
        Dao.getEntity(url, new TypeToken<Response<RedPointInfo>>() {
        }, listener, false);
    }

    /**
     * 获取购物车相关红点信息 2607
     *
     * @return
     */
    public static void getRedPointAboutCart( DaoListener<RedPointInfo> listener) {
        String url = new StringBuilder().append("&srv=2607").append("&type=1").toString();
        LogUtil.i(TAG, "getRedPoint : " + url);
        Dao.getEntity(url, new TypeToken<Response<RedPointInfo>>() {
        }, listener, false);
    }

    /**
     * 获取订单相关红点信息 2607
     *
     * @return
     */
    public static void getRedPointAboutOrder(DaoListener<RedPointInfo> listener) {
        String url = new StringBuilder().append("&srv=2607").append("&type=2").toString();
        LogUtil.i(TAG, "getRedPoint : " + url);
        Dao.getEntity(url, new TypeToken<Response<RedPointInfo>>() {
        }, listener, false);
    }

    /**
     * 获取无效购物车列表 2608
     *
     * @return
     */
    public static void getCartListInValid(DaoListener<EntityArray<CartItem>> listener) {
        String url = new StringBuilder().append("&srv=2608").toString();
        LogUtil.i(TAG, "getCartListInValid : " + url);
        Dao.getEntity(url, new TypeToken<Response<EntityArray<CartItem>>>() {
        }, listener, false);
    }

    /**
     * 2609 批量商品加入购物车
     * @param addition
     * @param listener
     */
    public static void addGoods2Cart(String addition, DaoListener<Object> listener) {
        String url =  UrlBuilder.getPublicParamUrl().append("&srv=2609").toString();
        List<HttpParams.NameValue> params = new ArrayList<HttpParams.NameValue>();
        params.add(new HttpParams.NameValue("addition", addition));
        Dao.putDatas(url, params, new TypeToken<Response<Object>>() {
        }, listener);
    }

    /**
     * 获取订单详情   2701
     *
     * @param orderSN
     * @return
     */
    public static void getOrderDetail(String orderSN, DaoListener<OrdersDetail> listener) {
        String url = new StringBuilder().append("&srv=2701").append("&order_sn=").append(orderSN).toString();
        LogUtil.i(TAG, "getGetOrderDetail : " + url);
        Dao.getEntity(url, new TypeToken<Response<OrdersDetail>>() {
        }, listener, false);
    }

    /**
     * 获取确认订单，提交并获取签名信息 支付宝 type=0    2702
     *
     * @param addressid
     * @param noteList
     * @return
     */
    public static void confirmOrderByAlipay(String posterIds,long couponId,List<Integer> ids, long addressid, List<Note> noteList, DaoListener<SignInfo> listener) {
        String notes = URLEncoder.encode(new Gson().toJson(noteList));
        String url = new StringBuilder().append("&srv=2702").append("&coupon_id=").append(couponId).append("&poster_ids=").append(posterIds).append("&add_id=").append(addressid).append("&type=0").append("&notes=").append(notes).append("&ids=").append(Util.createIdString(ids)).toString();
        LogUtil.i(TAG, "getConfirmOrderByAlipay : " + url);
        Dao.getEntity(url, new TypeToken<Response<SignInfo>>() {
        }, listener, false);
    }

    /**
     * 获取确认订单，提交并获取签名信息 微信 type=1  2702
     *
     * @param addressid
     * @param noteList
     * @return
     */
    public static void confirmOrderByWeixin(List<Integer> ids, long addressid,List<Note> noteList, DaoListener<SignInfo> listener) {
        String notes = URLEncoder.encode(new Gson().toJson(noteList));
        String url = new StringBuilder().append("&srv=2702").append("&add_id=").append(addressid).append("&type=1").append("&notes=").append(notes).append("&ids=").append(Util.createIdString(ids)).toString();
        LogUtil.i(TAG, "getConfirmOrderByWeixin : " + url);
        Dao.getEntity(url, new TypeToken<Response<SignInfo>>() {
        }, listener, false);
    }

    /**
     * 获取确认订单，提交并获取签名信息  银联  type = 2     2702
     *
     * @param addressid
     * @param noteList
     * @return
     */
    public static void confirmOrderByUnion(List<Integer> ids, long addressid, List<Note> noteList, DaoListener<SignInfo> listener) {
        String notes = URLEncoder.encode(new Gson().toJson(noteList));
        String url = new StringBuilder().append("&srv=2702").append("&add_id=").append(addressid).append("&type=2").append("&notes=").append(notes).append("&ids=").append(Util.createIdString(ids)).toString();
        LogUtil.i(TAG, "getConfirmOrderByUnion : " + url);
        Dao.getEntity(url, new TypeToken<Response<SignInfo>>() {
        }, listener, false);
    }

    /**
     * 获取支付结果   2703
     *
     * @param orderSN
     * @return
     */
    public static void getPayResult(String orderSN, DaoListener<PayResult> listener) {
        String url = new StringBuilder().append("&srv=2703").append("&order_sn=").append(orderSN).toString();
        LogUtil.i(TAG, "getGetPayResult : " + url);
        Dao.getEntity(url, new TypeToken<Response<PayResult>>() {
        }, listener, false);
    }

    /**
     *
     * @param sinceId
     * @return
     */
    public static void getOrderListAll(String sinceId, int curPg, DaoListener<EntityArray<ItemOrder>> listener) {
        String url = new StringBuilder().append("&srv=2704").append("&pg_cur=").append(curPg)
                .append("&pg_size=20").append("&type=").append("all").append("&since_=").append(sinceId).toString();
        LogUtil.i(TAG, "getItemOrderList : " + url);
        Dao.getEntity(url, new TypeToken<Response<EntityArray<ItemOrder>>>() {
        }, listener, false);
    }

    /**
     * 获取待付款订单列表 type=2      2704
     *
     * @param sinceId
     * @return
     */
    public static void getOrderListToBePaid(String sinceId, int curPg, DaoListener<EntityArray<ItemOrder>> listener) {
        String url = new StringBuilder().append("&srv=2704").append("&pg_cur=").append(curPg)
                .append("&pg_size=20").append("&type=").append("wait_pay").append("&since_id=").append(sinceId).toString();
        LogUtil.i(TAG, "getItemOrderList : " + url);
        Dao.getEntity(url, new TypeToken<Response<EntityArray<ItemOrder>>>() {
        }, listener, false);
    }

    /**
     * 获取待发货订单列表  type=3  2704
     *
     * @param sinceId
     * @return
     */
    public static void getOrderListToBeShipped(String sinceId, int curPg, DaoListener<EntityArray<ItemOrder>> listener) {
        String url = new StringBuilder().append("&srv=2704").append("&pg_cur=").append(curPg)
                .append("&pg_size=20").append("&type=").append("wait_send").append("&since_id=").append(sinceId).toString();
        LogUtil.i(TAG, "getItemOrderList : " + url);
        Dao.getEntity(url, new TypeToken<Response<EntityArray<ItemOrder>>>() {
        }, listener, false);
    }

    /**
     * 获取待收货订单列表  type=4     2704
     *
     * @param sinceId
     * @return
     */
    public static void getOrderListHarvested(String sinceId, int curPg, DaoListener<EntityArray<ItemOrder>> listener) {
        String url = new StringBuilder().append("&srv=2704").append("&pg_cur=").append(curPg)
                .append("&pg_size=20").append("&type=").append("wait_receive").append("&since_id=").append(sinceId).toString();
        LogUtil.i(TAG, "getItemOrderList : " + url);
        Dao.getEntity(url, new TypeToken<Response<EntityArray<ItemOrder>>>() {
        }, listener, false);
    }

    /**
     * 获取待评价订单列表 type=5   2704
     *
     * @param sinceId
     * @return
     */
    public static void getOrderListToBeCommented(String sinceId, int curPg, DaoListener<EntityArray<ItemOrder>> listener) {
        String url = new StringBuilder().append("&srv=2704").append("&pg_cur=").append(curPg)
                .append("&pg_size=20").append("&type=").append("wait_comment").append("&since_id=").append(sinceId).toString();
        LogUtil.i(TAG, "getItemOrderList : " + url);
        Dao.getEntity(url, new TypeToken<Response<EntityArray<ItemOrder>>>() {
        }, listener, false);
    }

    /**
     * 获取订单操作(确认收货)   2705
     *
     * @param orderSN
     * @return
     */
    public static void confirmOrder(String orderSN,DaoListener<Object> listener) {
        String url = new StringBuilder().append("&srv=2705").append("&order_sn=").append(orderSN).append("&opt=").append(0).toString();
        LogUtil.i(TAG, "getOrderOpt : " + url);
        Dao.getEntity(url, new TypeToken<Response<Object>>() {
        }, listener, false);
    }

    /**
     * 获取订单操作(取消收货)   2705
     *
     * @param orderSN
     * @return
     */
    public static void cancelOrder(String orderSN, DaoListener<Object> listener) {
        String url = new StringBuilder().append("&srv=2705").append("&order_sn=").append(orderSN).append("&opt=").append(1).toString();
        LogUtil.i(TAG, "getOrderOpt : " + url);
        Dao.getEntity(url, new TypeToken<Response<Object>>() {
        }, listener, false);
    }

    /**
     * 获取订单操作(删除收货)   2705
     *
     * @param orderSN
     * @return
     */
    public static void deleteOrder(String orderSN,DaoListener<Object> listener) {
        String url = new StringBuilder().append("&srv=2705").append("&order_sn=").append(orderSN).append("&opt=").append(2).toString();
        LogUtil.i(TAG, "getOrderOpt : " + url);
        Dao.getEntity(url, new TypeToken<Response<Object>>() {
        }, listener, false);
    }

    /**
     * 默认  获取发表评价   2706
     *
     * @param commentList
     * @return
     */
    public static void getSendComment(int opt, List<BaseComment> commentList, DaoListener<Object> listener) {
        String comments = URLEncoder.encode(new Gson().toJson(commentList));
        String url = new StringBuilder().append("&srv=2706").append("&opt=").append(opt).append("&comments=").append(comments).toString();
        LogUtil.i(TAG, "getSendComment : " + url);
        Dao.getEntity(url, new TypeToken<Response<Object>>() {
        }, listener, false);
    }

    /**
     * 获取物流详情   2707
     *
     * @param orderSN
     * @return
     */
    public static void getExpressDetail(String orderSN, DaoListener<ExpressDetail> listener) {
        String url = new StringBuilder().append("&srv=2707").append("&order_sn=").append(orderSN).toString();
        LogUtil.i(TAG, "getExpressDetail : " + url);
        Dao.getEntity(url, new TypeToken<Response<ExpressDetail>>() {
        }, listener, false);
    }

    /**
     * 获取未付款订单结算，提交并获取签名信息  2708
     *
     * @param orderSN
     * @return
     */
    public static void getOrderSignInfo(String orderSN, DaoListener<SignInfo> listener) {
        String url = new StringBuilder().append("&srv=2708").append("&order_sn=").append(orderSN).toString();
        LogUtil.i(TAG, "getBrandDetail : " + url);
        Dao.getEntity(url, new TypeToken<Response<SignInfo>>() {
        }, listener, false);
    }

    /**
     * 获取通过订单id获取发表评价列表 2709
     *
     * @param orderSN
     * @return
     */
    public static void getSendCommentList(String orderSN, DaoListener<EntityArray<CartItem>> listener) {
        String url = new StringBuilder().append("&srv=2709").append("&order_sn=").append(orderSN).toString();
        LogUtil.i(TAG, "getSendCommentList : " + url);
        Dao.getEntity(url, new TypeToken<Response<EntityArray<CartItem>>>() {
        }, listener, false);
    }

    /**
     * 获取收货地址列表 2801
     *
     * @return
     */
    public static void getAddressList(DaoListener<EntityArray<Address>> listener) {
        String url = new StringBuilder().append("&srv=2801").toString();
        LogUtil.i(TAG, "getAddressList : " + url);
        Dao.getEntity(url, new TypeToken<Response<EntityArray<Address>>>() {
        }, listener, false);
    }

    /**
     * 新增或修改收货地址    2802
     *
     * @param address 地址id为0时为默认新增
     * @return
     */
    public static void addOrChangeAddress(Address address, DaoListener<Address> listener) {
        String url = new StringBuilder().append("&srv=2802").append("&add_id=").append(address.id).append("&name=").append(URLEncoder.encode(address.name)).append("&phone=")
                .append(address.phone).append("&province=").append(address.province).append("&city=")
                .append(address.city).append("&proper=").append(address.proper).append("&full_add=")
                .append(URLEncoder.encode(address.street)).append("&type=").append(address.type).toString();
        LogUtil.i(TAG, "getAddOrChangeAddress : " + url);
        Dao.getEntity(url, new TypeToken<Response<Address>>() {
        }, listener, false);
    }

    /**
     * 删除收货地址  2803
     *
     * @param addressId
     * @return
     */
    public static void deleteAddress(long addressId, DaoListener<Object> listener) {
        String url = new StringBuilder().append("&srv=2803").append("&opt=").append(0).append("&add_id=").append(addressId).toString();
        LogUtil.i(TAG, "getDeleteOrSetAddress : " + url);
        Dao.getEntity(url, new TypeToken<Response<Object>>() {
        }, listener, false);
    }

    /**
     * 设置默认收货地址    2803
     *
     * @param addressId
     * @return
     */
    public static void setAddressDefault(long addressId, DaoListener<Object> listener) {
        String url = new StringBuilder().append("&srv=2803").append("&opt=").append(1).append("&add_id=").append(addressId).toString();
        LogUtil.i(TAG, "getDeleteOrSetAddress : " + url);
        Dao.getEntity(url, new TypeToken<Response<Object>>() {
        }, listener, false);
    }

    /**
     * 获取收藏夹商品列表 2804
     *
     * @param sinceId
     * @return
     */
    public static void getCollectGoodsList(long sinceId, int curPg, DaoListener<EntityArray<Goods>> listener) {
        String url = new StringBuilder().append("&srv=2804").append("&pg_cur=").append(curPg).append("&pg_size=20").append("&since_id=")
                .append(sinceId).append("&type=").append(0).toString();
        LogUtil.i(TAG, "getBrandDetail : " + url);
        Dao.getEntity(url, new TypeToken<Response<EntityArray<Goods>>>() {
        }, listener, false);
    }

    /**
     * 添加/收藏夹商品     2805
     *
     * @param favIds
     * @return
     */
    public static void addCollectGoods(List<Integer> favIds,  DaoListener<Object> listener) {
        String url = new StringBuilder().append("&srv=2805").append("&fav_id=").append(Util.createIdString(favIds)).append("&type=0").append("&opt=0").toString();
        LogUtil.i(TAG, "getCollectOpt : " + url);
        Dao.getEntity(url, new TypeToken<Response<Object>>() {
        }, listener, false);
    }

    /**
     * 移除收藏夹商品      2805
     *
     * @param favIds
     * @return
     */
    public static void deleteCollectGoods(List<Integer> favIds, DaoListener<Object> listener) {
        String url = new StringBuilder().append("&srv=2805").append("&fav_id=").append(Util.createIdString(favIds)).append("&type=0").append("&opt=1").toString();
        LogUtil.i(TAG, "getCollectOpt : " + url);
        Dao.getEntity(url, new TypeToken<Response<Object>>() {
        }, listener, false);
    }

    /**
     * 添加收藏夹专题      2805
     *
     * @param favIds
     * @return
     */
    public static void addCollectSubject(List<Integer> favIds,DaoListener<Object> listener) {
        String url = new StringBuilder().append("&srv=2805").append("&fav_id=").append(Util.createIdString(favIds)).append("&type=1").append("&opt=0").toString();
        LogUtil.i(TAG, "getCollectOpt : " + url);
        Dao.getEntity(url, new TypeToken<Response<Object>>() {
        }, listener, false);
    }

    /**
     * 移除收藏夹专题      2805
     *
     * @param favIds
     * @return
     */
    public static void deleteCollectSubject(List<Integer> favIds, DaoListener<Object> listener) {
        String url = new StringBuilder().append("&srv=2805").append("&fav_id=").append(Util.createIdString(favIds)).append("&type=1").append("&opt=1").toString();
        LogUtil.i(TAG, "getCollectOpt : " + url);
        Dao.getEntity(url, new TypeToken<Response<Object>>() {
        }, listener, false);
    }

    /**
     * 获取收藏夹专题列表    2806
     *
     * @param sinceId
     * @return
     */
    public static void getCollectSuject(long sinceId, int curPg, DaoListener<EntityArray<Subject>> listener) {
        String url = new StringBuilder().append("&srv=2806").append("&pg_cur=").append(curPg)
                .append("&pg_size=20").append("&type=").append(1).append("&since_id=").append(sinceId).toString();
        LogUtil.i(TAG, "getBrandDetail : " + url);
        Dao.getEntity(url, new TypeToken<Response<EntityArray<Subject>>>() {
        }, listener, false);
    }

    /**
     * 获取优惠券促销商品与特殊优惠    2902
     *
     * @param type  0：商品id;1：供应商id;2：优惠券集合id（首页、专题页的轮播图使用）
     * @param id  供应商id
     * @return
     */
    public static void getSpecialOffers(int type, long id, DaoListener<UrlBean> listener) {
        String url = new StringBuilder().append("&srv=2902").append("&id=").append(id).append("&type=").append(type).toString();
        LogUtil.i(TAG, "getSpecialOffers : " + url);
        Dao.getEntity(url, new TypeToken<Response<UrlBean>>() {
        }, listener, false);
    }

    /**
     * 查看相关优惠券促销商品   2903
     *
     * @param discouponId   优惠券id
     * @return
     */
    public static void getGoodsListAboutDiscoupon(long discouponId, long sinceId, int curPage, DaoListener<EntityArray<Goods>> listener) {
        String url = new StringBuilder().append("&srv=2903").append("&discoupon_id=").append(discouponId).append("&since_id=").append(sinceId).append("&pg_cur=")
                .append(curPage).append("&pg_size=20").toString();
        LogUtil.i(TAG, "getGoodsListAboutDiscoupon : " + url);
        Dao.getEntity(url, new TypeToken<Response<EntityArray<Goods>>>() {
        }, listener, false);
    }

    /**
     * 使用优惠券，有效优惠券（确认订单页）   2904
     *
     * @return
     */
    public static void getDiscouponListValid(String addressId,String ids,long sinceId,int curPg,DaoListener<EntityArray<DiscountCoupon>> listener) {
        String url = new StringBuilder().append("&srv=2904").append("&pg_cur=").append(curPg).append("&ids=").append(ids)
                .append("&address_id=").append(addressId).append("&pg_size=20").append("&type=").append(0).append("&since_id=").append(sinceId).toString();
        LogUtil.i(TAG, "getDiscouponListValid : " + url);
        Dao.getEntity(url, new TypeToken<Response<EntityArray<DiscountCoupon>>>() {
        }, listener, false);
    }

    /**
     * 使用优惠券，无效优惠券（确认订单页）   2904
     *
     * @return
     */
    public static void getDiscouponListInValid(String addressId,String ids,long sinceId,int curPg,DaoListener<EntityArray<DiscountCoupon>> listener) {
        String url = new StringBuilder().append("&srv=2904").append("&pg_cur=").append(curPg).append("&ids=").append(ids)
        .append("&address_id=").append(addressId).append("&pg_size=20").append("&type=").append(1).append("&since_id=").append(sinceId).toString();
        LogUtil.i(TAG, "getDiscouponListValid : " + url);
        Dao.getEntity(url, new TypeToken<Response<EntityArray<DiscountCoupon>>>() {
        }, listener, true);
    }

    /**
     * 获取优惠券详情   2906
     *type = 8,跳转大礼包
     * @return
     */
    public static void getDiscouponDetail(int type,long discouponId,DaoListener<UrlBean> listener) {
        String url = new StringBuilder().append("&srv=2906").append("&type=").append(type).append("&discoupon_id=").append(discouponId).toString();
        LogUtil.i(TAG, "getDiscouponDetail : " + url);
        Dao.getEntity(url, new TypeToken<Response<UrlBean>>() {
        }, listener, false);
    }

    /**
     * 领取优惠券   2907
     *type = 0 单张优惠券，type=1 优惠券集合
     * @return
     */
    public static void getDiscoupon(int type,long discouponId,DaoListener<StatusBean> listener) {
        String url = new StringBuilder().append("&type=").append(type).append("&srv=2907").append("&discoupon_id=").append(discouponId).toString();
        LogUtil.i(TAG, "getDiscoupon : " + url);
        Dao.getEntity(url, new TypeToken<Response<StatusBean>>() {
        }, listener, false);
    }

    /**
     * 获取我的优惠券   2908
     *
     * @return
     */
    public static void getDiscouponAboutMy(DaoListener<EntityArray<DiscountCoupon>> listener) {
        String url = new StringBuilder().append("&srv=2908").toString();
        LogUtil.i(TAG, "getDiscouponAboutMy : " + url);
        Dao.getEntity(url, new TypeToken<Response<EntityArray<DiscountCoupon>>>() {
        }, listener, false);
    }

    /**
     * 通过供应商id获取商品列表 2909
     *
     * @param supplierId
     * @param sinceId
     * @param curPage
     * @return
     */
    public static void getSupplierGoodsList(long supplierId, long sinceId, int curPage,DaoListener<EntityArray<Goods>> listener) {
        String url = new StringBuilder().append("&srv=2909").append("&supplier_id=").append(supplierId).append("&since_id=").append(sinceId).append("&pg_cur=")
                .append(curPage).append("&pg_size=20").toString();
        LogUtil.i(TAG, "getSupplierGoodsList : " + url);
        Dao.getEntity(url, new TypeToken<Response<EntityArray<Goods>>>() {
        }, listener, false);
    }

    /**
     * 优惠券使用规则   2917
     *
     * @return
     */
    public static void getDiscouponUsingRule(DaoListener<UrlBean> listener) {
        String url = new StringBuilder().append("&srv=2917").toString();
        LogUtil.i(TAG, "getDiscouponUsingRule : " + url);
        Dao.getEntity(url, new TypeToken<Response<UrlBean>>() {
        }, listener, false);
    }

    /**
     * 个人中心分享有礼成功之后调用该接   2922
     *
     * @return
     */
    public static void postAuthUrl(String code,DaoListener<Object> listener) {
        String url = new StringBuilder().append("&srv=2922").append("&code=").append(code).toString();
        LogUtil.i(TAG, "postAuthUrl : " + url);
        Dao.getEntity(url, new TypeToken<Response<Object>>() {
        }, listener, false);
    }

    /**
     * 修改用户昵称   2710
     *
     * @return
     */
    public static void modifyUserName(String nickname,DaoListener<Object> listener) {
        String url = new StringBuilder().append("&srv=2710").append("&nickname=").append(nickname).toString();
        LogUtil.i(TAG, "modifyUserName : " + url);
        Dao.getEntity(url, new TypeToken<Response<Object>>() {
        }, listener, false);
    }

    /**
     * 修改用户头像   2711
     *
     * @return
     */
    public static void modifyUserAvatar(String avatarUrl,DaoListener<Object> listener) {
        String url = new StringBuilder().append("&srv=2711").append("&avatar_url=").append(avatarUrl).toString();
        LogUtil.i(TAG, "modifyUserAvatar : " + url);
        Dao.getEntity(url, new TypeToken<Response<Object>>() {
        }, listener, false);
    }

    /**
     * 获取零食小喵使用协议 2104
     * @param listener
     */
    public static void getAgreement(DaoListener<UrlBean> listener) {
        String url = "&srv=2104";
        Dao.getEntity(url, new TypeToken<Response<UrlBean>>() {
        }, listener, false);
    }

    /**
     * 选择省市地址 2808
     * @param version
     * @param listener
     */
    public static void getSelectAddress (int version, DaoListener<UrlBean> listener) {
        String url = new StringBuilder().append("&srv=2808").append("&version=").append(version).toString();
        Dao.getEntity(url, new TypeToken<Response<UrlBean>>() {
        }, listener, false);
    }

    private static String createIdString(List<Long> ids) {
        StringBuilder builder = new StringBuilder();
        int length = ids.size();
        for (int i = 0; i < length; i++) {
            builder.append(ids.get(i));
            if (i != length - 1) {
                builder.append(",");
            }
        }
        return builder.toString();
    }
}
