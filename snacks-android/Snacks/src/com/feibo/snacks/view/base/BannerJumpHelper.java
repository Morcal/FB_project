package com.feibo.snacks.view.base;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.feibo.snacks.app.Constant;
import com.feibo.snacks.manager.global.Share4GiftConfigManager;
import com.feibo.snacks.manager.global.StatisticsManager;
import com.feibo.snacks.model.bean.Special;
import com.feibo.snacks.view.module.coupon.CouponDetailFragment;
import com.feibo.snacks.view.module.coupon.SpecialOfferFragment;
import com.feibo.snacks.view.module.goods.goodsdetail.H5GoodsDetailFragment;
import com.feibo.snacks.view.module.goods.goodsdetail.WebAcitivity;
import com.feibo.snacks.view.module.goods.goodslist.BannerGoodsListFragment;
import com.feibo.snacks.view.module.subject.H5SubjectDetailFragment;
import com.feibo.snacks.view.module.subject.SubjectListFragment;
import com.feibo.snacks.view.util.LaunchUtil;

/**
 * Created by Jayden on 2015/7/21.
 */
public class BannerJumpHelper {

    public static void turnBannerSituation(Context context, Special topic, int enterSource, String orginName) {
        if (topic != null) {
            Bundle bundle = new Bundle();
            bundle.putString(BaseFragment.ORIGIN, orginName);
            StatisticsManager.getInstance().statisticsVisitQuantity(topic.id, Constant.TOPIC, 0);
            switch (topic.action.type) {
                case Constant.SUBJECT_OF_LIST: {
                    bundle.putInt(SubjectListFragment.ID, Integer.parseInt(topic.action.info));
                    LaunchUtil.launchActivity(context, BaseSwitchActivity.class, SubjectListFragment.class, bundle);
                    break;
                }
                case Constant.SUBJECT_OF_DETAIL: {
                    bundle.putInt(H5SubjectDetailFragment.ID, Integer.parseInt(topic.action.info));
                    LaunchUtil.launchActivity(context, BaseSwitchActivity.class, H5SubjectDetailFragment.class, bundle);
                    break;
                }
                case Constant.GOODS_OF_LIST: {
                    bundle.putInt(BannerGoodsListFragment.BANNER_ID, Integer.parseInt(topic.action.info));
                    bundle.putString(BannerGoodsListFragment.BANNER_TITLE, topic.title);
                    LaunchUtil.launchActivity(context, BaseSwitchActivity.class, BannerGoodsListFragment.class, bundle);
                    break;
                }
                case Constant.GOODS_OF_DETAIL: {
                    bundle.putInt(H5GoodsDetailFragment.GOODS_ID, Integer.parseInt(topic.action.info));
                    bundle.putInt(H5GoodsDetailFragment.ENTER_SOURCE, enterSource);
                    LaunchUtil.launchActivity(context, BaseSwitchActivity.class, H5GoodsDetailFragment.class, bundle);
                    break;
                }
                case Constant.WEB_LINK: {
                    Intent intent = new Intent(context, WebAcitivity.class);
                    intent.putExtra(WebAcitivity.TITLE, topic.title);
                    intent.putExtra(WebAcitivity.SHOPPING_URL, topic.action.info);
                    context.startActivity(intent);
                    break;
                }
                case Constant.SHARE_GIFTS: {
                    Intent intent = new Intent(context, WebAcitivity.class);
                    intent.putExtra(WebAcitivity.TITLE, topic.title);
                    intent.putExtra(WebAcitivity.SHOPPING_URL, Share4GiftConfigManager.buildShare4GitfUrl());
                    context.startActivity(intent);
                    break;
                }
                case Constant.COUPON_LIST_DETAIL: {
                    bundle.putString(CouponDetailFragment.DISCOUPON_TITLE,topic.title);
                    bundle.putInt(CouponDetailFragment.ENTER_SOURCE, Constant.COUPON_LIST_DETAIL);
                    bundle.putLong(CouponDetailFragment.DISCOUPON_ID, Integer.parseInt(topic.action.info));
                    LaunchUtil.launchAppActivity(context, BaseSwitchActivity.class, CouponDetailFragment.class, bundle);
                    break;
                }
                case Constant.COUPON_DETAIL: {
                    bundle.putString(CouponDetailFragment.DISCOUPON_TITLE,topic.title);
                    bundle.putInt(CouponDetailFragment.ENTER_SOURCE, Constant.COUPON_DETAIL);
                    bundle.putLong(CouponDetailFragment.DISCOUPON_ID, Integer.parseInt(topic.action.info));
                    LaunchUtil.launchAppActivity(context, BaseSwitchActivity.class, CouponDetailFragment.class, bundle);
                    break;
                }
                case Constant.COUPON_LIST: {
                    bundle.putInt(SpecialOfferFragment.TYPE, 2);
                    bundle.putLong(SpecialOfferFragment.ID, Integer.parseInt(topic.action.info));
                    LaunchUtil.launchActivity(context, BaseSwitchActivity.class, SpecialOfferFragment.class, bundle);
                    break;
                }

            }
        }
    }
}
