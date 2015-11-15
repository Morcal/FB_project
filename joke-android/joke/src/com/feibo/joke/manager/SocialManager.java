package com.feibo.joke.manager;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

import com.sina.weibo.sdk.utils.LogUtil;

import fbcore.cache.image.ImageLoader.OnLoadListener;

import com.feibo.joke.R;
import com.feibo.joke.app.AppContext;
import com.feibo.joke.cache.DataProvider;
import com.feibo.joke.dao.IEntityListener;
import com.feibo.joke.dao.ReturnCode;
import com.feibo.joke.manager.work.UserManager;
import com.feibo.joke.model.LoginInfo;
import com.feibo.joke.model.Response;
import com.feibo.joke.utils.StringUtil;
import com.feibo.joke.view.util.ToastUtil;
import com.feibo.social.ResultListener;
import com.feibo.social.base.Config;
import com.feibo.social.base.Platform;
import com.feibo.social.base.Platform.Extra;
import com.feibo.social.manager.SocialComponent;
import com.feibo.social.model.PlatformInfo;
import com.feibo.social.model.ShareEntity;
import com.feibo.social.model.ShareEntityBuilder;
import com.feibo.social.utils.AccessTokenManager;

public class SocialManager {

    private static SocialManager manager;
    private Context context;

    /**
     * 新浪微博Token过期
     */
    public static final int WEIBO_TOKEN_TIMEOUT = 1;

    private SocialManager(Context context) {
        this.context = context;
    }

    public static SocialManager getInstance(Context context) {
        if (manager == null) {
            manager = new SocialManager(context);
        }
        return manager;
    }

    public void initSocial() {
        Config.config(
                Platform.SINA,
                new Config().buildAppKey("374438288")
                        .buildRedirectUrl("http://admin.appd.lengxiaohua.cn/app/sinaoauth/callback").buildScope("all"));

        Config.config(Platform.QQ, new Config().buildAppId("100265368").buildScope("all"));

        Config.config(Platform.WEIXIN,
                new Config().buildAppId("wxe6a1e2faeb9bd540").buildAppSecret("f6460e7fb341d5280926d00f67fd3933")
                        .buildScope("snsapi_userinfo"));
    }

    public void onActivityResult(Activity activity, int requestCode, int resultCode, Intent data) {
        SocialComponent.onActivityResult(activity, requestCode, resultCode, data);
    }

    public void onNewIntent(Activity activity, Intent intent) {
        SocialComponent.onNewIntent(activity, intent);
    }

    public void login(final Activity activity, final boolean isBanding, final Platform platform,
                      final SocialResultListener listener) {
        if (!AppContext.hasAvailableNetwork()) {
            listener.onError(0, "当前无网络连接");
        }
        reset(activity, platform, null);
        SocialComponent.create(platform, activity).login(new ResultListener() {
            @Override
            public void onResult(boolean isSuccess, String result) {
                if (isSuccess) {
                    listener.onStart();
                    getUserInfoAndLogin(activity, isBanding, platform, listener);
                } else {
                    listener.onError(0, result);
                }
            }
        });
    }

    public boolean isLogin(Platform platform) {
        boolean result = false;
        // 验证是否过期
        switch (platform) {
            case QQ:
                result = (AccessTokenManager.readTencentAccessToken(context) != null)
                        && AccessTokenManager.validateQQToken(context);
                break;
            case SINA:
                result = (AccessTokenManager.readSinaAccessToken(context) != null)
                        && AccessTokenManager.validateSinaToken(context);
                break;
            case WEIXIN:
                result = (AccessTokenManager.readWXAccessToken(context) != null)
                        && AccessTokenManager.validateWXAccessToken(context);
                break;
        }
        return result;
    }

    public void reset(final Activity activity, final Platform platform, final ILoginOutListener listener) {
        if (isLogin(platform)) {
            SocialComponent.create(platform, activity).logout(new ResultListener() {
                @Override
                public void onResult(boolean isSuccess, String result) {
                    if (isSuccess) {
                        LogUtil.d("重置第三方登陆", "第三方：" + platform.toString() + ", 取消授权成功");
                    } else {
                        LogUtil.d("重置第三方登陆", "第三方：" + platform.toString() + ", 取消授权失败");
                    }

                    if (listener != null) {
//                        listener.onLoginOutResult(isSuccess);
                    }
                }

            });
        }
    }

    public void loginOut(final Activity activity, final Platform platform, final ILoginOutListener listener) {
        SocialComponent.create(platform, activity).logout(new ResultListener() {
            @Override
            public void onResult(boolean isSuccess, String result) {
                if (isSuccess) {
                    LogUtil.d("重置第三方登陆", "第三方：" + platform.toString() + ", 取消授权成功");
                } else {
                    LogUtil.d("重置第三方登陆", "第三方：" + platform.toString() + ", 取消授权失败");
                }

                if (listener != null) {
                    listener.onloginOutResult(isSuccess);
                }
            }
        });
    }

    public interface ILoginOutListener {
        public void onloginOutResult(boolean success);
    }

    private void getUserInfoAndLogin(final Activity activity, boolean isBanding, Platform platform,
                                     final SocialResultListener onLoaded) {
        PlatformInfo platformInfo = SocialComponent.create(platform, activity).getPlatformUserInfo();
        String nickname = platformInfo.getNickName();
        String avatar = platformInfo.getHeadImgUrl();
        String accessToken = platformInfo.getAccessToken();
        String openId = platformInfo.getOpenid();

        if (isBanding) {
            UserManager.getInstance().bandingPlatform(platform, openId, nickname, avatar, accessToken,
                    new IEntityListener<LoginInfo>() {

                        @Override
                        public void result(Response<LoginInfo> response) {
                            if (response.rsCode == ReturnCode.RS_SUCCESS && response.data != null) {
                                onLoaded.onSuccess(response.data);
                            } else {
                                onLoaded.onError(response.rsCode, response.rsMsg);
                            }
                        }
                    });
        } else {
            UserManager.getInstance().register(platform, openId, nickname, avatar, accessToken,
                    new IEntityListener<LoginInfo>() {

                        @Override
                        public void result(Response<LoginInfo> response) {
                            if (response.rsCode == ReturnCode.RS_SUCCESS && response.data != null) {
                                onLoaded.onSuccess(response.data);
                            } else {
                                onLoaded.onError(response.rsCode, response.rsMsg);
                            }
                        }
                    });
        }

    }

    /************************************
     * 下面是分享
     ********************************************/
    public void shareQQ(Activity activity, String title, String content, String imgUrls, String webUrl, String videoUrl) {
        ShareEntityBuilder entityBuilder = getEntity(title, webUrl, content);
        entityBuilder.setNetworkImageUrl(imgUrls);
        if (!StringUtil.isEmpty(videoUrl)) {
            entityBuilder.setVideoUrl(webUrl);
            entityBuilder.setWebpageUrl(null);
        }
        ToastUtil.showSimpleToast("分享中");
        share(activity, Extra.QQ_FRIEND, entityBuilder.create());
    }

    public void shareQzone(Activity activity, String title, String content, String imgUrl, String webUrl,
                           String videoUrl) {
        shareQzone(activity, title, content, new String[]{imgUrl}, webUrl, videoUrl);
    }

    public void shareQzone(final Activity activity, String title, String content, String[] imgUrls, String webUrl,
                           String videoUrl) {
        ShareEntityBuilder entityBuilder = getEntity(title, webUrl, content);
        setImgUrlsBuilder(entityBuilder, imgUrls);
        if (!StringUtil.isEmpty(videoUrl)) {
            entityBuilder.setVideoUrl(webUrl);
//            entityBuilder.setWebpageUrl(null);
        }
        ToastUtil.showSimpleToast("分享中");
        share(activity, Extra.QQ_QZONE, entityBuilder.create());
    }

    public void shareSina(final Activity activity, String content, String imgUrl, String webUrl, String videoUrl,
                          final LoadListener listener, boolean isToast) {
        if (StringUtil.isEmpty(imgUrl) && isToast) {
            ToastUtil.showSimpleToast("分享图片不能为空");
        } else {
            if (isToast) {
                ToastUtil.showSimpleToast("分享中");
            }
            shareWithBitmap(activity, Extra.SINA, getEntity(null, webUrl, content), imgUrl, webUrl, videoUrl, listener, isToast);
        }
    }

    public void shareVideoSina(final Activity activity, String content, String webUrl, final LoadListener listener, boolean isToast) {
        if (isToast) {
            ToastUtil.showSimpleToast("分享中");
        }

        share(activity, Extra.SINA, getEntity(null, webUrl, content).create(), listener, isToast);
    }

    public void shareWX(final Activity activity, final String title, final String content, final Extra extra,
                        final String webUrl, String imgUrl, String videoUrl, final LoadListener listener) {
        if (StringUtil.isEmpty(imgUrl)) {
            ToastUtil.showSimpleToast("分享图片不能为空");
            return;
        }
        ToastUtil.showSimpleToast("分享中");

        shareWithBitmap(activity, extra, getEntity(title, webUrl, content), imgUrl, videoUrl, webUrl, listener, true);
    }

    /***********************************
     * 分享公共方法
     ***************************************/
    private void share(Activity activity, Extra extra, ShareEntity entity) {
        share(activity, extra, entity, true);
    }

    private void share(Activity activity, Extra extra, ShareEntity entity, final boolean isToast) {
        share(activity, extra, entity, null, isToast);
    }

    private void share(Activity activity, Extra extra, ShareEntity entity, final LoadListener listener, final boolean isToast) {
        SocialComponent.create(extra.belong(), activity).share(entity, extra, new ResultListener() {
            @Override
            public void onResult(boolean isSuccess, String result) {
                LogUtil.d("Social", "isSuccess -> " + isSuccess + ", result = " + result);
                if (isSuccess) {
                    ToastUtil.showSimpleToast("分享成功");
                    if (listener != null) {
                        listener.onSuccess();
                    }
                } else {
                    if (listener != null) {
                        if (!StringUtil.isEmpty(result)) {
                            if (result.contains("21332")) {
                                //新浪token过期
                                listener.onFail(WEIBO_TOKEN_TIMEOUT);
                            } else {
                                ToastUtil.showSimpleToast(result);
                                listener.onFail(0);
                            }
                        } else {
                            ToastUtil.showSimpleToast(result);
                        }
                    } else {
                        ToastUtil.showSimpleToast(result);
                    }
                }
            }
        });
    }

    private ShareEntityBuilder getEntity(String title, String webUrl, String content) {
        ShareEntityBuilder entityBuilder = new ShareEntityBuilder().builder();
        if (title != null) {
            entityBuilder.setTitle(title);
        }
        if (content != null) {
            entityBuilder.setContent(content);
        }
        if (webUrl != null) {
            entityBuilder.setWebpageUrl(webUrl);
        }
        return entityBuilder;
    }

    private void shareWithBitmap(final Activity activity, final Extra extra, final ShareEntityBuilder builder,
                                 String imgUrl, final String shareUrl, final String videoUrl, final LoadListener listener, final boolean isToast) {
        // 下面这个方法会自动去判断网络和文件中是否有缓存
        DataProvider.getInstance().getImageLoader().loadImage(imgUrl, new OnLoadListener() {
            @Override
            public void onSuccess(Drawable drawable, boolean immediate) {
                if (drawable == null) {
                    return;
                }

                Bitmap origin = null;

                if (drawable instanceof BitmapDrawable) {
                    origin = ((BitmapDrawable) drawable).getBitmap();
                } else {
                    origin = BitmapFactory.decodeResource(activity.getResources(), R.drawable.ic_launcher);
                }

                Bitmap thumbBmp = null;
                if (extra == Extra.SINA) {
                    thumbBmp = Bitmap.createScaledBitmap(origin, origin.getWidth() - 1, origin.getHeight() - 1, true);
                } else {
                    int width = 100;
                    int height = 100;
                    if (origin.getWidth() == 100 && origin.getHeight() == 100) {
                        width = 101;
                        height = 101;
                    }
                    thumbBmp = Bitmap.createScaledBitmap(origin, width, height, true);
                }
                builder.setBitmap(thumbBmp);
                if (!StringUtil.isEmpty(videoUrl) && extra != Extra.WX_SESSION && extra != Extra.WX_TIMELINE) {
                    builder.setVideoUrl(videoUrl);
                    builder.setWebpageUrl(null);
                }
                share(activity, extra, builder.create(), listener, isToast);
//                if (listener != null) {
//                    listener.onSuccess();
//                }
            }

            @Override
            public void onFail() {
                if (listener != null) {
                    listener.onFail(ReturnCode.NO_NET);
                }
            }
        });
    }

    private void setImgUrlsBuilder(ShareEntityBuilder entityBuilder, String[] imgUrls) {
        ArrayList<String> list = new ArrayList<String>();
        if (imgUrls != null && imgUrls.length > 0) {
            for (int i = 0; i < imgUrls.length; i++) {
                list.add(imgUrls[i]);
            }
        }
        if (list.size() > 0) {
            entityBuilder.setNetworkImageUrls(list);
        }
    }

    /***********************************
     * 回调接口
     *******************************************/
    public interface SocialResultListener {
        void onError(int code, String error);

        void onSuccess(LoginInfo info);

        void dialogDismiss();

        void onStart();
    }

}
