package com.feibo.joke.utils;

import android.app.Activity;

import com.feibo.joke.manager.LoadListener;
import com.feibo.joke.manager.SocialManager;
import com.feibo.joke.model.User;
import com.feibo.joke.model.Video;
import com.feibo.joke.view.util.ToastUtil;
import com.feibo.social.base.Platform.Extra;

public class ShareUtil {

    private static final String ICON_URL = "http://v.lengxiaohua.cn/img/logo.png";
    private static final String WEB_URL = "http://www.feibo.com";

    public interface IShareListener {
        public void onFail(int code);
    }

    private static String getShareUrl(String url) {
        return StringUtil.isEmpty(url) ? WEB_URL : url;
    }

    private static String getAvatar(String avatar) {
        return StringUtil.isEmpty(avatar) ? ICON_URL : avatar;
    }

    public static void onUserDetailShare(Activity activity, boolean isFromMe, User user, Extra extra, final IShareListener iShareListener) {
        if(extra == Extra.QQ_FRIEND){
            shareQQFromUserDetail(activity, isFromMe, user);
        } else if(extra == Extra.QQ_QZONE) {
            shareQzoneFromUserDetail(activity, isFromMe, user);
        } else if(extra == Extra.SINA) {
            shareSinaFromUserDetail(activity, isFromMe, user, new LoadListener() {

                @Override
                public void onSuccess() {

                }

                @Override
                public void onFail(int code) {
                    weiboTokenTimeout(iShareListener, code);
                }
            });
        } else if(extra == Extra.WX_SESSION ) {
            shareWXFromUserDetail(activity, isFromMe, user, null);
        } else if(extra == Extra.WX_TIMELINE) {
            shareWXFriendFromUserDetail(activity, isFromMe, user, null);
        }
    }

    private static void weiboTokenTimeout(final IShareListener iShareListener, int code) {
        if(code == SocialManager.WEIBO_TOKEN_TIMEOUT) {
            ToastUtil.showSimpleToast("新浪微博Token过期，请重新登录!");
            if(iShareListener != null) {
                iShareListener.onFail(code);
            }
        }
    }

    public static void onVideoDetialShare(Activity activity, boolean isFromMe, Video video, Extra extra, final IShareListener iShareListener) {
        if(video == null) {
            ToastUtil.showSimpleToast(activity, "分享失败");
            return;
        }
        if(extra == Extra.QQ_FRIEND){
            shareQQFromVideoDetail(activity, isFromMe, video);
        } else if (extra == Extra.QQ_QZONE) {
            shareQzoneFromVideoDetail(activity, isFromMe, video);
        } else if(extra == Extra.SINA) {
            shareSinaFromVideoDetail(activity, isFromMe, video, new LoadListener() {

                @Override
                public void onSuccess() {

                }

                @Override
                public void onFail(int code) {
                    weiboTokenTimeout(iShareListener, code);
                }
            });
        } else if(extra == Extra.WX_SESSION ) {
            shareWXFromVideoDetail(activity, isFromMe, video, null);
        } else if(extra == Extra.WX_TIMELINE) {
            shareWXFriendFromVideoDetail(activity, isFromMe, video, null);
        }
    }

    /*************************************  个人主页  ******************************************************/
    private static void shareQQFromUserDetail(Activity activity, boolean isFromMe, User user) {
        String title = "";
        String content = "魔性鬼畜神演技，分分钟戳笑点的短视频社区";
        if(!isFromMe) {
            title = "强烈推荐" +user.nickname +"笑点太低慎入 ！ (来自冷笑话精选)" ;
        } else {
            title = "我在冷笑话精选发布了很多搞笑片儿，这里的人都超爱演！" ;
        }
        String avatar = StringUtil.isEmpty(user.avatar) ? ICON_URL : user.avatar;
        String webUrl = StringUtil.isEmpty(user.detail.shareUrl) ? WEB_URL : user.detail.shareUrl;
        SocialManager.getInstance(activity).shareQQ(activity, title, content, avatar, webUrl, null);
    }

    private static void shareQzoneFromUserDetail(Activity activity, boolean isFromMe, User user) {
        String title = "";
        String content = "魔性鬼畜神演技，分分钟戳笑点的短视频社区";
        if(!isFromMe) {
            title = "强烈推荐" +user.nickname +"笑点太低慎入 ！ (来自冷笑话精选)" ;
        } else {
            title = "我在冷笑话精选发布了很多搞笑片儿，这里的人都超爱演！" ;
        }
        String avatar = StringUtil.isEmpty(user.avatar) ? ICON_URL : user.avatar;
        String webUrl = StringUtil.isEmpty(user.detail.shareUrl) ? WEB_URL : user.detail.shareUrl;
        SocialManager.getInstance(activity).shareQzone(activity, title, content, avatar, webUrl, null);
    }

    private static void shareSinaFromUserDetail(Activity activity, boolean isFromMe, User user, LoadListener listener) {
        String content;
        if(!isFromMe) {
            content = user.nickname +"这人超爱演, 笑点太低慎入 ! "+ user.detail.shareUrl +" #冷笑话精选#" ;
        } else {
            content = "我在冷笑话精选搞笑视频社区发布了很多片儿，这里的人都超爱演！ "+ user.detail.shareUrl +" #冷笑话精选#";
        }
        SocialManager.getInstance(activity).shareSina(activity, content, user.avatar, user.detail.shareUrl, null, listener, true);
    }

    /**
     * 朋友圈
     * @param activity
     * @param isFromMe
     * @param listener
     */
    private static void shareWXFriendFromUserDetail(Activity activity, boolean isFromMe, User user, LoadListener listener) {
        String title;
        String avatar = StringUtil.isEmpty(user.avatar) ? ICON_URL : user.avatar;
        String webUrl = StringUtil.isEmpty(user.detail.shareUrl) ? WEB_URL : user.detail.shareUrl;
        if(!isFromMe) {
            title = user.nickname +"这人超爱演, 笑点太低慎入 !【冷笑话精选】" ;
        } else {
            title = "来冷笑话精选搞笑短视频社区，这里的人都超爱演！ ";
        }
        SocialManager.getInstance(activity).shareWX(activity, title, title, Extra.WX_TIMELINE, webUrl, avatar, null, listener);
    }

    /**
     * 微信朋友
     * @param activity
     * @param isFromMe
     * @param listener
     */
    private static void shareWXFromUserDetail(Activity activity, boolean isFromMe, User user, LoadListener listener) {
        String title;
        String content = "";
        String avatar = StringUtil.isEmpty(user.avatar) ? ICON_URL : user.avatar;
        String webUrl = StringUtil.isEmpty(user.detail.shareUrl) ? WEB_URL : user.detail.shareUrl;
        if(!isFromMe) {
            title = user.nickname +"这人超爱演, 笑点太低慎入 !【冷笑话精选】" ;
            content = "魔性鬼畜神演技，分分钟戳笑点的短视频社区";
        } else {
            title = "快来冷笑话精选，这里的人都超爱演！";
            content = "魔性鬼畜神演技，分分钟戳笑点的短视频社区";
        }
        SocialManager.getInstance(activity).shareWX(activity, title, content, Extra.WX_SESSION, webUrl, avatar, null, listener);
    }

    /*************************************  视频详情页  ***************************************************/

    /**
     * 微信朋友
     * @param activity
     * @param isFromMe
     * @param listener
     */
    private static void shareWXFromVideoDetail(Activity activity, boolean isFromMe, Video video, final LoadListener listener) {
        String title;
        String webUrl = StringUtil.isEmpty(video.shareUrl) ? WEB_URL : video.shareUrl;
        String imgUrl = StringUtil.isEmpty(video.oriImage.url)? ICON_URL : video.oriImage.url;
        if(!isFromMe) {
            title = "分享" + video.author.nickname +"的搞笑片儿，笑点太低慎入! 【冷笑话精选】" ;
        } else {
            title = "我发了个搞笑片儿，笑点太低慎入! 【冷笑话精选】";
        }
        SocialManager.getInstance(activity).shareWX(activity, title, video.desc, Extra.WX_SESSION, webUrl, imgUrl, video.url, listener);
    }

    /**
     * 朋友圈
     * @param activity
     * @param isFromMe
     * @param listener
     */
    private static void shareWXFriendFromVideoDetail(Activity activity, boolean isFromMe, Video video, final LoadListener listener) {
        String title;
        String webUrl = StringUtil.isEmpty(video.shareUrl) ? WEB_URL : video.shareUrl;
        String imgUrl = StringUtil.isEmpty(video.oriImage.url)? ICON_URL : video.oriImage.url;
        if(!isFromMe) {
            title = "分享" + video.author.nickname +"的搞笑片儿，笑点太低慎入! 【冷笑话精选】" ;
        } else {
            title = "我发了个搞笑片儿，笑点太低慎入! 【冷笑话精选】";
        }
        SocialManager.getInstance(activity).shareWX(activity, title, null, Extra.WX_TIMELINE, webUrl, imgUrl, video.url, listener);
    }

    private static void shareSinaFromVideoDetail(final Activity activity, boolean isFromMe, Video video, final LoadListener listener) {
        String content;
        video.shareUrl = video.shareUrl.replace("/wap/", "/web/");
        if(!isFromMe) {
            String desc = video.desc;
            content = "这片儿太搞笑了，高能慎入！ 分享"+video.author.nickname+"的【"+desc+"】#冷笑话精选#" + video.shareUrl;
            if(content.length() > 139) {
                int moreLength = content.length() - 139;
                desc = video.desc.substring(0, Math.max(0, video.desc.length() - moreLength));
                content = "这片儿太搞笑了，高能慎入！ 分享"+video.author.nickname+"的【"+desc+"】#冷笑话精选#" + video.shareUrl;
            }
        } else {
            content = "我在#冷笑话精选#发了个搞笑视频，笑点太低慎入! " + video.shareUrl;
        }
        SocialManager.getInstance(activity).shareVideoSina(activity, content, video.shareUrl, listener, true);
    }

    private static void shareQzoneFromVideoDetail(Activity activity, boolean isFromMe, Video video) {
        String title = "";
        if(!isFromMe) {
            title = "这片儿太搞笑了，高能慎入！（来自冷笑话精选）" ;
        } else {
            title = "我发了个搞笑视频，笑点太低慎入！（来自冷笑话精选）" ;
        }
        String webUrl = StringUtil.isEmpty(video.shareUrl) ? WEB_URL : video.shareUrl;
        String img = video.oriImage != null ? StringUtil.isEmpty(video.oriImage.url) ? ICON_URL : video.oriImage.url : ICON_URL;
        SocialManager.getInstance(activity).shareQzone(activity, title, video.desc, img, webUrl, video.url);
    }

    private static void shareQQFromVideoDetail(Activity activity, boolean isFromMe, Video video) {
        String title = "";
        if(!isFromMe) {
            title = "这片儿太搞笑了，高能慎入！（来自冷笑话精选）" ;
        } else {
            title = "我发了个搞笑视频，笑点太低慎入！（来自冷笑话精选）" ;
        }
        String webUrl = StringUtil.isEmpty(video.shareUrl) ? WEB_URL : video.shareUrl;
        String img = video.oriImage != null ? StringUtil.isEmpty(video.oriImage.url) ? ICON_URL : video.oriImage.url : ICON_URL;
        SocialManager.getInstance(activity).shareQQ(activity, title, video.desc, img, webUrl, null);
    }

    /*************************************  邀请  ******************************************************/

    public static void invitationQQ (Activity activity, User user) {
        String avatar = user == null ? ICON_URL : getAvatar(user.avatar);
        String shareUrl = user == null ? WEB_URL : getShareUrl(user.detail.downloadUrl);
        String title = "卧槽快来！这里的人都超爱演！";
        SocialManager.getInstance(activity).shareQQ(activity, title, "魔性鬼畜神演技，分分钟戳笑点的短视频社区", avatar, shareUrl, null);
    }

    public static void invitationWX (Activity activity, User user) {
        String avatar = user == null ? ICON_URL : getAvatar(user.avatar);
        String shareUrl = user == null ? WEB_URL : getShareUrl(user.detail.downloadUrl);
        String title = "卧槽快来！这里的人都超爱演！";
        SocialManager.getInstance(activity).shareWX(activity, title, "魔性鬼畜神演技，分分钟戳笑点的短视频社区", Extra.WX_SESSION, shareUrl, avatar, null, null);
    }

    public static void invitationSina (Activity activity, User user, String invitaUsername, final LoadListener listener) {
        String avatar = user == null ? ICON_URL : getAvatar(user.avatar);
        String shareUrl = user == null ? WEB_URL : getShareUrl(user.detail.downloadUrl);
        String content = "@"+invitaUsername+" 我在冷笑话精选搞笑视频社区发布了很多片儿，这里的人都超爱演！ "+ shareUrl +"  #冷笑话精选#";
        SocialManager.getInstance(activity).shareSina(activity, content, avatar, shareUrl, null, listener, true);
    }

}
