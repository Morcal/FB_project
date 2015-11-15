package com.feibo.snacks.manager;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.webkit.JavascriptInterface;

import com.feibo.snacks.R;
import com.feibo.snacks.app.Constant;
import com.feibo.snacks.manager.global.UserManager;
import com.feibo.snacks.manager.global.orders.opteration.CartOperationManager;
import com.feibo.snacks.view.base.BaseSwitchActivity;
import com.feibo.snacks.view.module.MainActivity;
import com.feibo.snacks.view.module.coupon.CouponDetailFragment;
import com.feibo.snacks.view.module.coupon.MyCouponFragment;
import com.feibo.snacks.view.module.coupon.SpecialOfferFragment;
import com.feibo.snacks.view.module.goods.goodsdetail.H5GoodsDetailFragment;
import com.feibo.snacks.view.module.goods.goodsdetail.WebAcitivity;
import com.feibo.snacks.view.module.goods.goodslist.BannerGoodsListFragment;
import com.feibo.snacks.view.module.home.BrandGroupFragment;
import com.feibo.snacks.view.module.home.category.CategoryFragment;
import com.feibo.snacks.view.module.home.search.SearchFragment;
import com.feibo.snacks.view.module.person.login.LoginFragment;
import com.feibo.snacks.view.module.subject.H5SubjectDetailFragment;
import com.feibo.snacks.view.module.subject.SubjectListFragment;
import com.feibo.snacks.view.util.LaunchUtil;
import com.feibo.snacks.view.util.RemindControl;

/**
 * Created by Jayden on 2015/8/31.
 */
public class BaseJSBridge {

    BaseWebViewListener listener;
    private Context context;

    public BaseJSBridge(Context context, BaseWebViewListener listener) {
        this.listener = listener;
        this.context = context;
    }

    @JavascriptInterface
    public void setShareInfo(int id,String desc,String title,String imgUrl,String contentUrl,int type) {
        if (listener != null) {
            listener.onClickShare(id,desc,title,imgUrl,contentUrl,type);
        }
    }

    @JavascriptInterface
    public void javascriptJumpAction(int type,String title,String param1,String param2) {
        if (context == null) {
            return;
        }
        JSJumpAction action = new JSJumpAction(type,title,param1,param2);
        ViewJump jump = ViewJump.valueOf(action.type);
        jump.action(context, action);
    }

    private class JSJumpAction {
        public int type;

        public String title;

        public String param1;

        public String param2;

        public JSJumpAction(int type, String title, String param1, String param2) {
            this.type = type;
            this.title = title;
            this.param1 = param1;
            this.param2 = param2;
        }
    }

    private enum ViewJump {
        H5(1) {
            @Override
            void action(Context context, JSJumpAction action) {
                Intent intent3 = new Intent(context, WebAcitivity.class);
                intent3.putExtra(WebAcitivity.TITLE, action.title);
                intent3.putExtra(WebAcitivity.SHOPPING_URL, action.param1);
                context.startActivity(intent3);
            }
        },
        HOME(2) {
            @Override
            void action(Context context, JSJumpAction action) {
                LaunchUtil.launchMainActivity(context, MainActivity.HOME_SCENCE);
            }
        },
        SEARCH(3) {
            @Override
            void action(Context context, JSJumpAction action) {
                LaunchUtil.launchActivity(context, BaseSwitchActivity.class, SearchFragment.class, null);
            }
        },
        CATEGORY(4) {
            @Override
            void action(Context context, JSJumpAction action) {
                Bundle bundle = new Bundle();
                bundle.putInt(CategoryFragment.CATEGORY_ID, Integer.parseInt(action.param1));
                bundle.putString(CategoryFragment.CATEGORY_TITLE, action.title);
                LaunchUtil.launchActivity(context, BaseSwitchActivity.class, CategoryFragment.class, bundle);
            }
        },
        BRAND_DETAIL(5) {
            @Override
            void action(Context context, JSJumpAction action) {
                Bundle bundle = new Bundle();
                bundle.putInt(BrandGroupFragment.BRAND_GROUP_ID, Integer.parseInt(action.param1));
                bundle.putString(BrandGroupFragment.BRAND_GROUP_TITLE,
                        action.title);
                LaunchUtil.launchActivity(context, BaseSwitchActivity.class, BrandGroupFragment.class, bundle);
            }
        },
        SPECIAL_SELL(6) {
            @Override
            void action(Context context, JSJumpAction action) {
                LaunchUtil.launchMainActivity(context, MainActivity.SPECIAL_SELLING_SCENCE);
            }
        },
        SUBJECT_FRAGMENT(7) {
            @Override
            void action(Context context, JSJumpAction action) {
                LaunchUtil.launchMainActivity(context, MainActivity.SUBJECT_SCENCE);
            }
        },
        SUBJECT_LIST(8) {
            @Override
            void action(Context context, JSJumpAction action) {
                Bundle bundle = new Bundle();
                bundle.putInt(SubjectListFragment.ID, Integer.parseInt(action.param1));
                LaunchUtil.launchAppActivity(context, BaseSwitchActivity.class, SubjectListFragment.class, bundle);
            }
        },
        GOODS_LIST(9) {
            @Override
            void action(Context context, JSJumpAction action) {
                Bundle bundle = new Bundle();
                bundle.putInt(BannerGoodsListFragment.BANNER_ID, Integer.parseInt(action.param1));
                LaunchUtil.launchActivity(context, BaseSwitchActivity.class, BannerGoodsListFragment.class, bundle);
            }
        },
        SUBJECT_DETAIL(10) {
            @Override
            void action(Context context, JSJumpAction action) {
                Bundle bundle = new Bundle();
                bundle.putInt(H5SubjectDetailFragment.ID, Integer.parseInt(action.param1));
                LaunchUtil.launchActivity(context, BaseSwitchActivity.class, H5SubjectDetailFragment.class, bundle);
            }
        },
        COUPON_LIST(11) {
            @Override
            void action(Context context, JSJumpAction action) {
                Bundle bundle = new Bundle();
                bundle.putInt(SpecialOfferFragment.TYPE, Integer.parseInt(action.param1));
                bundle.putLong(SpecialOfferFragment.ID, Integer.parseInt(action.param2));
                LaunchUtil.launchActivity(context, BaseSwitchActivity.class, SpecialOfferFragment.class, bundle);
            }
        },
        COUPON_DETAIL(12) {
            @Override
            void action(Context context, JSJumpAction action) {
                Bundle bundle = new Bundle();
                bundle.putLong(CouponDetailFragment.DISCOUPON_ID, Integer.parseInt(action.param1));
                LaunchUtil.launchAppActivity(context, BaseSwitchActivity.class, CouponDetailFragment.class, bundle);
            }
        },
        MY_COUPON(13) {
            @Override
            void action(Context context, JSJumpAction action) {
                if (!UserManager.getInstance().isLogin()) {
                    RemindControl.showSimpleToast(context, R.string.orders_account_user_no_login);
                    LaunchUtil.launchActivityForResult(LaunchUtil.COUPON_REQUEST_CODE, context, BaseSwitchActivity.class, LoginFragment.class, null);
                    return;
                }
                LaunchUtil.launchActivity(context, BaseSwitchActivity.class, MyCouponFragment.class, null);
            }
        },
        CART(14) {
            @Override
            void action(Context context, JSJumpAction action) {
                if (action == null) {
                    return;
                }
                String param1 = action.param1;
                int p1 = Integer.parseInt(param1);
                if (p1 == 0) {
                    LaunchUtil.launchCartActivity(context);
                } else if (p1 == 1) {
                    CartOperationManager.addGoods2Cart(action.param2, new ILoadingListener() {
                        @Override
                        public void onSuccess() {
                            if (context == null) {
                                return;
                            }
                            LaunchUtil.launchCartActivity(context);
                        }

                        @Override
                        public void onFail(String failMsg) {
                            if (context == null) {
                                return;
                            }
                            RemindControl.showSimpleToast(context, context.getResources().getString(R.string.big_gifts_received_error));
                        }
                    });
                }
            }
    },
        USER_LOGIN(15) {
            @Override
            void action(Context context, JSJumpAction action) {
                if (UserManager.getInstance().isLogin()) {
                    return;
                }
                LaunchUtil.launchActivityForResult(LaunchUtil.LOGIN_REQUEST_CODE, context, BaseSwitchActivity.class, LoginFragment.class, null);
            }
        },
        GOODS_DETAIL(16) {
            @Override
            void action(Context context, JSJumpAction action) {
                Bundle bundle = new Bundle();
                bundle.putInt(H5GoodsDetailFragment.GOODS_ID, Integer.parseInt(action.param1));
                bundle.putInt(H5GoodsDetailFragment.ENTER_SOURCE, Constant.WEB_ACTIVITY);
                LaunchUtil.launchActivity(context, BaseSwitchActivity.class, H5GoodsDetailFragment.class, bundle);
            }
        };
        private int value;
        public static ViewJump valueOf(int i) {
            switch (i) {
                case 1:
                    return H5;
                case 2:
                    return HOME;
                case 3:
                    return SEARCH;
                case 4:
                    return CATEGORY;
                case 5:
                    return BRAND_DETAIL;
                case 6:
                    return SPECIAL_SELL;
                case 7:
                    return SUBJECT_FRAGMENT;
                case 8:
                    return SUBJECT_LIST;
                case 9:
                    return GOODS_LIST;
                case 10:
                    return SUBJECT_DETAIL;
                case 11:
                    return COUPON_LIST;
                case 12:
                    return COUPON_DETAIL;
                case 13:
                    return MY_COUPON;
                case 14:
                    return CART;
                case 15:
                    return USER_LOGIN;
                case 16:
                    return GOODS_DETAIL;
                default: return null;
            }
        }


        abstract void action(Context context, JSJumpAction action);

        ViewJump(int i) {
            this.value = i;
        }
    }
}
