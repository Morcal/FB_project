package com.feibo.snacks.model.test;

import com.feibo.snacks.model.bean.Image;
import com.feibo.snacks.model.bean.User;
import com.feibo.snacks.model.bean.Address;
import com.feibo.snacks.model.bean.Brand;
import com.feibo.snacks.model.bean.CartItem;
import com.feibo.snacks.model.bean.CartSuppliers;
import com.feibo.snacks.model.bean.Classify;
import com.feibo.snacks.model.bean.Comment;
import com.feibo.snacks.model.bean.Goods;
import com.feibo.snacks.model.bean.GoodsDetail;
import com.feibo.snacks.model.bean.ItemOrder;
import com.feibo.snacks.model.bean.Kind;
import com.feibo.snacks.model.bean.Logistics;
import com.feibo.snacks.model.bean.OrdersDetail;
import com.feibo.snacks.model.bean.RedPointInfo;
import com.feibo.snacks.model.bean.Special;
import com.feibo.snacks.model.bean.SubKind;
import com.feibo.snacks.model.bean.Tag;
import com.feibo.snacks.model.bean.UpdateInfo;
import com.feibo.snacks.model.bean.Price;
import com.feibo.snacks.model.bean.group.BrandDetail;
import com.feibo.snacks.model.bean.group.HomePageHead;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Snacks 2.0测试工厂
 */
public class TestFactoryV20 {

    public static Object getData(int srv) {
        if (srv == 2201) {
            return getHomePageHead();
        } else if (srv == 2206) {
            return getGoodsList(306, 170);
        } else if (srv == 2102) {
            return getUser();
        } else if (srv == 1001) {
            return getUpdateInfo();
        } else if (srv == 2205) {
            return getBrandDetail();
        } else if (srv == 2402) {
            return getGoodsList(153, 100);
        } else if (srv == 2502) {
            return getGoodsDetail();
        } else if (srv == 2604) {
            return getCartSuppliersList();
        } else if (srv == 2605 || srv == 2606 || srv == 2701) {
            return getOrder();
        } else if (srv == 2801) {
            return getAddressList();
        } else if (srv == 2802) {
            return getAddress(2802);
        } else if (srv == 2103) {
            return getAddress(2802);
        } else if (srv == 2704) {
            return getItemOrder();
        } else if (srv == 2607) {
            return getRedPointInfo();
        }else if (srv == 2709) {
            return getCartItemList();
        }
        return null;
    }

    private static List<CartItem> getCartItemList() {
        List<CartItem> list = new ArrayList<CartItem>();

        for (int y = 0; y < (getRandom(2) + 1); y++) {
            list.add(createCartItem(y));
        }
        return list;
    }

    private static RedPointInfo getRedPointInfo() {
        RedPointInfo redPointInfo = new RedPointInfo();
        redPointInfo.cartNum = 5;
        redPointInfo.waitCommentNum = 4;
        redPointInfo.waitPayNum = 3;
        redPointInfo.waitSendNum = 2;
        redPointInfo.waitReceiveNum = 1;
        return redPointInfo;
    }


    private static Object getItemOrder() {
        ArrayList<ItemOrder> list = new ArrayList<ItemOrder>();
        for (int x = 0; x < 10; x++) {
            list.add(creatItemOrder(x));
        }
        return list;

    }

    private static ItemOrder creatItemOrder(int x) {
        ItemOrder ito = new ItemOrder();
        ito.id = x + "";
        ito.finalSum = 100.29+x;
        ito.type=x%7;
        ito.name="供应商"+x;
        ito.num=x*10;
        if(x % 2 == 0){
            ito.multi=new ArrayList<Image>();
            for (int y = 0; y < getRandom(6) + 3; y++) {
                ito.multi.add(createImage(getImageUrl(y), 50, 50));
            }
        } else {
            ito.single = createCartItem(x);
        }
        ito.freight = 10.00 + x;
        return ito;
    }

    private static Object getAddressList() {
        ArrayList<Address> list = new ArrayList<Address>();
        for (int x = 0; x < 10; x++) {
            list.add(getAddress(x));
        }
        return list;
    }

    private static Object getOrder() {
        OrdersDetail order = new OrdersDetail();
        order.address = getAddress(0);
        order.cartSuppliers = (List<CartSuppliers>) getCartSuppliersList();
        order.finalSum = 55.00;
        order.id = "0";
        order.logistics = getLogistics();
        order.infos = new ArrayList<String>();
        order.infos.add("成交时间：2015-06-07 20:28:01");
        order.infos.add("创建时间：2015-06-07 20:28:01");
        order.type = getRandom(5);
        return order;
    }

    private static Logistics getLogistics() {
        Logistics lo = new Logistics();
        lo.desc = "在前埔分布进行派件扫描";
        lo.time = "2015-07-01 10:14:2";
        return lo;
    }

    private static Address getAddress(int x) {
        Address add = new Address();
        if (x == 2) {
            add.type = 1;
        } else {
            add.type = 0;
        }
        add.city = "厦门市";
        add.street = "XXXX路XXX号";
        add.id = x;
        add.name = "姓名" + x;
        add.phone = "139XXXXXXX" + x;
        add.proper = "思明区";
        add.province = "福建省";
        return add;
    }

    private static Object getCartSuppliersList() {
        ArrayList<CartSuppliers> list = new ArrayList<CartSuppliers>();
        for (int x = 0; x < 10; x++) {
            list.add(createCartSuppliers(x));
        }
        return list;
    }

    private static CartSuppliers createCartSuppliers(int x) {
        CartSuppliers css = new CartSuppliers();
        css.id = x;
        css.freight = 20.50;
        css.name = "供货商" + x;
        css.note = "这是备注哦！！！" + x;
        css.items = new ArrayList<CartItem>();
        css.num = 5;
        css.sumPrice = 99999;

        for (int y = 0; y < (getRandom(3) + 1); y++) {
            css.items.add(createCartItem(y));
        }
        return css;
    }

    private static CartItem createCartItem(int x) {
        CartItem ci = new CartItem();
        ci.goodsId = x;
        ci.goodsTitle = "商品标题" + x;
        ci.id = x;
        ci.img = createImage(getImageUrl(x), 153, 100);
        ci.kinds = "选择的口味描述";
        ci.num = getRandom(3);
        ci.price = getPrice();
        ci.state = getRandom(2);
        return ci;
    }

    private static Object getGoodsDetail() {
        GoodsDetail goods = new GoodsDetail();
        goods.title = "这是标题";
        goods.id = 0;
        goods.desc = "这里是描述";
        goods.img = createImage(getImageUrl(1), 306, 170);
        goods.guideType = getRandom(2);
        goods.status = getRandom(3);


        goods.kinds = new ArrayList<Kind>();
        Kind kind = new Kind();
        kind.id = 0;
        kind.title = "口味";
        kind.kinds = new ArrayList<SubKind>();
        for (int x = 0; x < 3; x++) {
            kind.kinds.add(createSubKind(x));
        }
        goods.kinds.add(kind);

        return goods;
    }

    private static SubKind createSubKind(int x) {
        SubKind sk = new SubKind();
        sk.id = x;
        sk.price = getPrice();
        sk.surplusNum = getRandom(20);
        sk.title = "口味名" + x;
        return sk;
    }

    private static Comment createComment(int x) {
        Comment c = new Comment();
        c.avatar = createImage(getImageUrl(0), 20, 20);
        c.content = "评论内容" + x;
        c.nickname = "昵称" + x;
        c.id = x;
        return c;
    }

    private static Object getBrandDetail() {
        BrandDetail bd = new BrandDetail();
        bd.brand = creatBrand(0);
        bd.goodses = (List<Goods>) getGoodsList(306, 170);
        return bd;
    }

    private static Object getUpdateInfo() {
        UpdateInfo updateInfo = new UpdateInfo();
        updateInfo.desc = "更新描述";
        updateInfo.title = "更新标题";
        updateInfo.url = "http://";
        return updateInfo;
    }

    private static Object getUser() {
        User u = new User();
        u.setUid("12");
        u.setMobiNum(getRandom(2) == 1 ? "18638573678" : "");
        u.setWSKey("ZhK3jxo5FktXAWHk");
        return u;
    }

    private static Object getGoodsList(int imgWidth, int imgHeight) {
        ArrayList<Goods> list = new ArrayList<Goods>();
        for (int x = 0; x < 20; x++) {
            list.add(creatGoods(x, imgWidth, imgHeight));
        }

        return list;
    }

    private static Object getHomePageHead() {
        HomePageHead obj = new HomePageHead();
        obj.brands = new ArrayList<Brand>();
        for (int x = 0; x < 6; x++) {
            obj.brands.add(creatBrand(x));
        }
        obj.topics = new ArrayList<Special>();
        for (int x = 0; x < 3; x++) {
            obj.topics.add(creatSpecial(x, 320, 135));
        }
        obj.specials = new ArrayList<Special>();
        obj.specials.add(creatSpecial(0, 128, 160));
        obj.specials.add(creatSpecial(2, 192, 64));
        obj.specials.add(creatSpecial(4, 96, 96));
        obj.specials.add(creatSpecial(6, 96, 96));
        obj.classifies = new ArrayList<Classify>();
        for (int x = 0; x < 6; x++) {
            obj.classifies.add(creatClassify(x));
        }
        return obj;
    }

    private static Goods creatGoods(int x, int imgWidth, int imgHeight) {
        Goods goods = new Goods();
        goods.title = "这是标题" + x;
        goods.id = x;
        goods.desc = "这里是描述" + x;
        goods.img = createImage(getImageUrl(x), imgWidth, imgHeight);
        goods.guideType = getRandom(2);
        goods.posters = new ArrayList<Image>();
        goods.posters.add(createImage(getImageUrl(x), imgWidth, imgHeight));
        goods.price = getPrice();
        goods.soldNum = 100;
        goods.favNum = 88;
        goods.freight = 9.99;
        goods.status = getRandom(3);
        goods.surplusNum = 50;
        goods.tag = creatTag();
        goods.type = getRandom(2);
        goods.time = getEndTime();
        return goods;
    }

    /**
     * @return
     */
    private static long getEndTime() {
        return (System.currentTimeMillis() / 1000) + 60;
    }

    private static Price getPrice() {
        Price c = new Price();
        c.current = 150.00;
        c.prime = 250.00;
        return c;
    }

    private static Tag creatTag() {
        Tag t = new Tag();
        t.color = getRandom(3);
        String tagTitle[] = new String[] {"三折", "包邮", "限量"};
        String tagDesc[] = new String[] {"最低三折起", "不用邮费哦!", "限量快去抢"};
        int tagNum = getRandom(2);
        t.title = tagTitle[tagNum];

        return t;

    }

    private static Classify creatClassify(int x) {
        Classify classify = new Classify();
        classify.title = "这是标题" + x;
        classify.id = x;
        classify.desc = "这里是描述" + x;
        classify.img = createImage(getImageUrl(x), 42, 42);
        return classify;
    }

    private static Special creatSpecial(int x, int imgWidth, int imgHeight) {
        Special special = new Special();
        special.title = "这是标题" + x;
        special.id = x;
        special.desc = "这里是描述" + x;
        special.img = createImage(getImageUrl(x), imgWidth, imgHeight);
        special.action = new Special.Action();
        special.action.type = getRandom(1, 7);
        special.action.info = "";
        return special;
    }

    private static Brand creatBrand(int x) {
        Brand brand = new Brand();
        brand.time = 60;
        brand.title = "品牌名" + x;
        brand.id = x;
        brand.provider = "供货商" + x;
        brand.img = createImage(getImageUrl(x), 306, 125);
        return brand;
    }

    private static Image createImage(String imgUrl, int imgWidth, int imgHeight) {
        Image image = new Image();
        image.imgUrl = imgUrl;
        image.imgWidth = imgWidth;
        image.imgHeight = imgHeight;
        return image;
    }

    private static String getImageUrl(int x) {
        String ss[] = new String[7];
        ss[1] = "http://img.xd.feibo.com/xiandan/201409/6e08ccb4bc7ce034c6f24e6082cd9ee7.jpg";
        ss[2] = "http://img.xd.feibo.com/xiandan/201409/ce6fc06b16764d8b89759dd22dbfa93c.jpg";
        ss[3] = "http://img.xd.feibo.com/xiandan/201409/10f19491fbe540da8ce9cb4fb4e28f89.jpg";
        ss[4] = "http://img.xd.feibo.com/xiandan/201409/fb46a6fc541ac5f7ce5a6ef5694ab9b5.jpg";
        ss[5] = "http://img.xd.feibo.com/xiandan/201409/912c61ff93908ef51a1ee88b08672a09.jpg";
        ss[6] = "http://img.xd.feibo.com/xiandan/201409/b4c73b7a6070180b598e2bc0e1bdef32.jpg";
        ss[0] = "http://img.xd.feibo.com/xiandan/201409/7a6bb18002d7026dc83dc9c4adb3082c.jpg";

        int y = x % 7;
        return ss[y];
    }

    public static int getRandom(int range) {
        return new Random().nextInt(range);
    }

    /** 从 m 到 n-1 (不包括n) */
    public static int getRandom(int m, int n) {
        return (int) (Math.random() * (m - n) + n);
    }
}
