package com.feibo.joke.dao;

import java.util.ArrayList;
import java.util.List;

import com.feibo.joke.app.AppContext;
import com.feibo.joke.manager.work.CountManager;
import com.feibo.joke.model.Comment;
import com.feibo.joke.model.DiscoveryTopicItem;
import com.feibo.joke.model.Feedback;
import com.feibo.joke.model.FunnyMaster;
import com.feibo.joke.model.GlobalConfiguration;
import com.feibo.joke.model.HandpickItem;
import com.feibo.joke.model.LoginInfo;
import com.feibo.joke.model.Message;
import com.feibo.joke.model.Notification;
import com.feibo.joke.model.PushSetting;
import com.feibo.joke.model.Response;
import com.feibo.joke.model.SplashImage;
import com.feibo.joke.model.Topic;
import com.feibo.joke.model.UpdateInfo;
import com.feibo.joke.model.User;
import com.feibo.joke.model.Video;
import com.feibo.joke.model.VideoTopicItem;
import com.feibo.joke.model.data.BaseListData;
import com.feibo.joke.model.data.WeiboFriendsData;
import com.google.gson.reflect.TypeToken;

import fbcore.conn.http.HttpParams.NameValue;

public class JokeDao {

    /**
     * 检查更新 1001
     *
     * @param type
     * @param listener
     */
    public static void getUpdateInfo(int type, IEntityListener<UpdateInfo> listener) {
        String url = new StringBuilder().append("&srv=1001").append("&type=").append(type).toString();
        Dao.getEntity(url, new TypeToken<Response<UpdateInfo>>() {
        }, listener, false);
    }

    /**
     * 获取全局配置 1003
     *
     * @param listener
     */
    public static void getGlobalConfiguration(IEntityListener<GlobalConfiguration> listener) {
        String url = "&srv=1003";
        Dao.getEntity(url, new TypeToken<Response<GlobalConfiguration>>() {
        }, listener, false);
    }

    /**
     * 获取启动图配置 1004
     */
    public static void getSplashImage(String radio, IEntityListener<SplashImage> listener, boolean cache) {
        String url = "&srv=1004&ratio=" + radio;
        Dao.getEntity(url, new TypeToken<Response<SplashImage>>() {
        }, listener, false);
    }

    /**
     * 提交用户反馈 1101
     *
     * @param content
     * @param listener
     */
    public static void putFeedback(String content, IEntityListener<Object> listener) {
        String url = new StringBuilder().append("&srv=1101").append("&os_version=").append(AppContext.OS_VERSION)
                .append("&content=").append(content).toString();
        Dao.getEntity(url, new TypeToken<Response<Object>>() {
        }, listener, false);
        // String url = new
        // StringBuilder().append("&srv=1101").append("&os_version=").append(AppContext.OS_VERSION)
        // .toString();
        // List<NameValue> list = new ArrayList<NameValue>();
        // NameValue param0 = new NameValue("content", content);
        // list.add(param0);
        // Dao.putDatas(url, list, new TypeToken<Response<Object>>() {
        // }, listener);
    }

    /**
     * 获取用户反馈 1102
     *
     * @param listener
     */
    public static void getFeedback(IEntityListener<BaseListData<Feedback>> listener, boolean cache) {
        String url = new StringBuilder().append("&srv=1102").toString();
        Dao.getEntity(url, new TypeToken<Response<BaseListData<Feedback>>>() {
        }, listener, cache);
    }

    /**
     * 获取精选视频列表 3001
     *
     * @param listener
     */
    public static void getEssenseVideos(long sinceId, IEntityListener<BaseListData<VideoTopicItem>> listener, boolean cache) {
        String url = new StringBuilder().append("&srv=3001").append("&since_id=").append(sinceId).toString();
        Dao.getEntity(url, new TypeToken<Response<BaseListData<VideoTopicItem>>>() {
        }, listener, cache, true);
    }

    /**
     * 获取最新的视频列表 2002
     *
     * @param listener
     * @param cache
     */
    public static void getFreshVideos(long sinceId, IEntityListener<BaseListData<Video>> listener, boolean cache) {
        String url = new StringBuilder().append("&srv=2002").append("&since_id=").append(sinceId).toString();
        Dao.getEntity(url, new TypeToken<Response<BaseListData<Video>>>() {
        }, listener, cache);
    }

    /**
     * 获取话题的视频列表 2003
     *
     * @param listener
     * @param cache
     */
    public static void getTopicVideos(long topicId, long sinceId, IEntityListener<BaseListData<Video>> listener,
                                      boolean cache) {
        String url = new StringBuilder().append("&srv=2003").append("&topic_id=").append(topicId).append("&since_id=")
                .append(sinceId).toString();
        Dao.getEntity(url, new TypeToken<Response<BaseListData<Video>>>() {
        }, listener, cache);
    }

    /**
     * 获取动态的视频列表 2004
     *
     * @param sinceId
     * @param listener
     */
    public static void getDynamicVideos(long sinceId, IEntityListener<BaseListData<Video>> listener, boolean cache) {
        String url = new StringBuilder().append("&srv=2004").append("&since_id=").append(sinceId).toString();
        Dao.getEntity(url, new TypeToken<Response<BaseListData<Video>>>() {
        }, listener, cache);
    }

    /**
     * 获取用户创作的视频列表 2005
     *
     * @param userId
     * @param sinceId
     * @param listener
     * @param cache
     */
    public static void getPublishVideos(long userId, long sinceId, IEntityListener<BaseListData<Video>> listener,
                                        boolean cache) {
        String url = new StringBuilder().append("&srv=2005").append("&since_id=").append(sinceId).append("&user_id=")
                .append(userId).toString();
        Dao.getEntity(url, new TypeToken<Response<BaseListData<Video>>>() {
        }, listener, cache);
    }

    /**
     * 获取用户喜欢的视频列表 2006
     *
     * @param sinceId
     * @param listener
     * @param cache
     */
    public static void getFavoriteVideos(long sinceId, IEntityListener<BaseListData<Video>> listener, boolean cache) {
        String url = new StringBuilder().append("&srv=2006").append("&since_id=").append(sinceId).toString();
        Dao.getEntity(url, new TypeToken<Response<BaseListData<Video>>>() {
        }, listener, cache);
    }

    /**
     * 获取视频详情 2007
     *
     * @param videoId
     * @param listener
     * @param cache
     */
    public static void getVideoDetail(long videoId, IEntityListener<Video> listener, boolean cache) {
        String url = new StringBuilder().append("&srv=2007").append("&video_id=").append(videoId).toString();
        Dao.getEntity(url, new TypeToken<Response<Video>>() {
        }, listener, cache);
    }

    /**
     * 删除/举报/喜欢/取消喜欢视频 2009
     *
     * @param opt
     * @param videoId
     * @param listener
     */
    public static void operateVideo(int opt, long videoId, IEntityListener<Object> listener) {
        String url = new StringBuilder().append("&srv=2009").append("&video_id=").append(videoId).append("&opt=")
                .append(opt).toString();
        Dao.getEntity(url, new TypeToken<Response<Object>>() {
        }, listener, false);
    }

    /**
     * 获取搞笑达人列表 2101
     *
     * @param sinceId
     * @param listener
     * @param cache
     */
    public static void getFunnyMaters(long sinceId, IEntityListener<BaseListData<FunnyMaster>> listener, boolean cache) {
        String url = new StringBuilder().append("&srv=2101").append("&since_id=").append(sinceId).toString();
        Dao.getEntity(url, new TypeToken<Response<BaseListData<FunnyMaster>>>() {
        }, listener, false);
    }

    /**
     * 获取微博朋友列表 2102;
     *
     * @param listener
     */
    public static void getWeiboFriends(IEntityListener<WeiboFriendsData> listener, boolean cache) {
        String url = "&srv=2102";
        Dao.getEntity(url, new TypeToken<Response<WeiboFriendsData>>() {
        }, listener, false);
    }

    /**
     * 获取关注好友列表 2103
     *
     * @param sinceId
     * @param listener
     * @param cache
     */
    public static void getAttentionFriends(long userId, long sinceId, IEntityListener<BaseListData<User>> listener,
                                           boolean cache) {
        String url = new StringBuilder().append("&srv=2103").append("&user_id=").append(userId).append("&since_id=")
                .append(sinceId).toString();
        Dao.getEntity(url, new TypeToken<Response<BaseListData<User>>>() {
        }, listener, cache);
    }

    /**
     * 获取用户的粉丝列表 2104
     *
     * @param userId
     * @param sinceId
     * @param listener
     * @param cache
     */
    public static void getFans(long userId, long sinceId, IEntityListener<BaseListData<User>> listener, boolean cache) {
        String url = new StringBuilder().append("&srv=2104").append("&user_id=").append(userId).append("&since_id=")
                .append(sinceId).toString();
        Dao.getEntity(url, new TypeToken<Response<BaseListData<User>>>() {
        }, listener, cache);
    }

    /**
     * 关注/取消关注好友 2105
     *
     * @param userId
     * @param opt
     * @param listener
     */
    public static void operateFriend(long userId, int opt, IEntityListener<Object> listener) {
        String url = new StringBuilder().append("&srv=2105").append("&user_id=").append(userId).append("&opt=")
                .append(opt).toString();
        Dao.getEntity(url, new TypeToken<Response<Object>>() {
        }, listener, false);
    }

    /**
     * 获取通讯录好友列表 2106
     *
     * @param since_id
     * @param listener
     * @param cache
     */
    public static void getPhoneFriends(long since_id, IEntityListener<BaseListData<User>> listener, boolean cache) {
        String url = new StringBuilder().append("&srv=2106").append("&since_id=").append(since_id).toString();
        Dao.getEntity(url, new TypeToken<Response<BaseListData<User>>>() {
        }, listener, cache);
    }

    /**
     * 一键关注微博好友 2108
     *
     * @param listener
     */
    public static void attentAllWeiboFriends(IEntityListener<Object> listener) {
        String url = "&srv=2108";
        Dao.getEntity(url, new TypeToken<Response<Object>>() {
        }, listener, false);
    }

    /**
     * 邀请微博好友 2109
     *
     * @param unique_id
     * @param listener
     */
    public static void inviteWeiboFriend(long unique_id, IEntityListener<User> listener) {
        String url = new StringBuilder().append("&srv=2109").append("&unique_id=").append(unique_id).toString();
        Dao.getEntity(url, new TypeToken<Response<User>>() {
        }, listener, false);
    }

    /**
     * 一键关注推荐达人 2110
     *
     * @param listener
     */
    public static void attentAllUser(boolean isWeibo, IEntityListener<Object> listener) {
        String url = isWeibo ? "&srv=2108" : "&srv=2110";
        Dao.getEntity(url, new TypeToken<Response<Object>>() {
        }, listener, false);
    }

    /**
     * 获取视频最新的评论列表 2201
     *
     * @param videoId
     * @param sinceId
     * @param listener
     */
    public static void getVideoComments(long videoId, long sinceId, IEntityListener<BaseListData<Comment>> listener) {
        String url = new StringBuilder().append("&srv=2201").append("&video_id=").append(videoId).append("&since_id=")
                .append(sinceId).toString();
        Dao.getEntity(url, new TypeToken<Response<BaseListData<Comment>>>() {
        }, listener, false);
    }

    /**
     * 评论视频 2202
     *
     * @param videoId
     * @param commentId
     * @param listener
     */
    public static void commentVideo(long videoId, long commentId, String content, IEntityListener<Comment> listener) {
        String url = new StringBuilder().append("&srv=2202").append("&video_id=").append(videoId)
                .append("&comment_id=").append(commentId).append("&content=").append(content).toString();
        Dao.getEntity(url, new TypeToken<Response<Comment>>() {
        }, listener, false);
    }

    /**
     * 删除/举报评论 2203
     *
     * @param commentId
     * @param opt
     * @param listener
     */
    public static void operateComment(long commentId, int opt, IEntityListener<Object> listener) {
        String url = new StringBuilder().append("&srv=2203").append("&comment_id=").append(commentId).append("&opt=")
                .append(opt).toString();
        Dao.getEntity(url, new TypeToken<Response<Object>>() {
        }, listener, false);
    }

    /**
     * 用户登录 2301
     *
     * @param ttype
     * @param openid
     * @param token
     * @param nickname
     * @param avatarUrl
     * @param listener
     */
    public static void login(String ttype, String openid, String token, String nickname, String avatarUrl,
                             IEntityListener<LoginInfo> listener) {
        String url = new StringBuilder().append("&srv=2301").append("&ttype=").append(ttype).append("&openid=")
                .append(openid).append("&token=").append(token).append("&nickname=").append(nickname)
                .append("&avatar_url=").append(avatarUrl).toString();
        Dao.getEntity(url, new TypeToken<Response<LoginInfo>>() {
        }, listener, false);
    }

    /**
     * 退出登陆 2306
     *
     * @param listener
     */
    public static void loginOut(IEntityListener<Object> listener) {
        String url = new StringBuilder().append("&srv=2306").toString();
        Dao.getEntity(url, new TypeToken<Response<Object>>() {
        }, listener, false);
    }

    /**
     * 用户绑定 2305
     *
     * @param ttype
     * @param openid
     * @param token
     * @param nickname
     * @param avatarUrl
     * @param listener
     */
    public static void bandingPlatform(String ttype, String openid, String token, String nickname, String avatarUrl,
                                       IEntityListener<LoginInfo> listener) {
        String url = new StringBuilder().append("&srv=2305").append("&ttype=").append(ttype).append("&openid=")
                .append(openid).append("&token=").append(token).append("&nickname=").append(nickname)
                .append("&avatar_url=").append(avatarUrl).toString();
        Dao.getEntity(url, new TypeToken<Response<LoginInfo>>() {
        }, listener, false);
    }

    /**
     * 获取用户信息 2302
     *
     * @param userId
     * @param listener
     */
    public static void getUserInfo(long userId, IEntityListener<User> listener, boolean cache) {
        String url = new StringBuilder().append("&srv=2302").append("&user_id=").append(userId).toString();
        Dao.getEntity(url, new TypeToken<Response<User>>() {
        }, listener, false);
    }

    /**
     * 修改用户信息 3201
     *
     * @param listener
     */
    public static void modifyUserInfo(String avatar, String nickname, int gender, String province,
                                      String city, String signature, String birthday, IEntityListener<User> listener) {

        List<NameValue> params = new ArrayList<NameValue>();
        NameValue tSignature = new NameValue("signature", signature);
        NameValue tGender = new NameValue("gender", gender);
        NameValue tBirth = new NameValue("birth", birthday);
        NameValue tProvince = new NameValue("province", province);
        NameValue tCity = new NameValue("city", city);
        NameValue tIcon = new NameValue("icon", avatar);
        NameValue tNickname = new NameValue("nickname", nickname);

        params.add(tSignature);
        params.add(tGender);
        params.add(tBirth);
        params.add(tProvince);
        params.add(tCity);
        params.add(tIcon);
        params.add(tNickname);

        Dao.putDatas("&srv=3201", params, new TypeToken<Response<User>>() {
        }, listener, false);
    }

    /**
     * 发现中推荐的话题列表 2401
     *
     * @param listener
     * @param cache
     */
    public static void getDiscoveryTopics(IEntityListener<BaseListData<DiscoveryTopicItem>> listener, boolean cache) {
        String paramUrl = "&srv=2401";
        Dao.getEntity(paramUrl, new TypeToken<Response<BaseListData<DiscoveryTopicItem>>>() {
        }, listener, cache);
    }

    /**
     * 发现中搜索关键字 3101
     *
     * @param listener
     * @param cache
     */
    public static void getSearchMsg(String msg,IEntityListener<BaseListData<String>> listener, boolean cache) {
        String url = new StringBuilder().append("&srv=3101").append("&msg=").append(msg).toString();
        Dao.getEntity(url,new TypeToken<Response<BaseListData<String>>>(){
        },listener,cache);
    }


    /**
     * 发现中搜索相关笑友 3102
     *
     * @param listener
     * @param cache
     */
    public static void getSearchUserMsg(long pageId,String msg,IEntityListener<BaseListData<User>> listener, boolean cache) {
        String url = new StringBuilder().append("&srv=3102").append("&page_id=").append(pageId).append("&msg=").append(msg).toString();
        Dao.getEntity(url,new TypeToken<Response<BaseListData<User >>>(){
        },listener,cache);
    }


    /**
     * 发现中搜索相关视频 3103
     *
     * @param listener
     * @param cache
     */
    public static void getSearchVedioMsg(long pageId,String msg,IEntityListener<BaseListData<Video>> listener, boolean cache) {
        String url = new StringBuilder().append("&srv=3103").append("&page_id=").append(pageId).append("&msg=").append(msg).toString();
        Dao.getEntity(url,new TypeToken<Response<BaseListData<Video>>>(){
        },listener,cache);
    }

    /**
     * 所有热门话题列表 2402
     *
     * @param sinceId
     * @param listener
     * @param cache
     */
    public static void getHotTopics(long sinceId, IEntityListener<BaseListData<Topic>> listener, boolean cache) {
        String url = new StringBuilder().append("&srv=2402").append("&since_id=").append(sinceId).toString();
        Dao.getEntity(url, new TypeToken<Response<BaseListData<Topic>>>() {
        }, listener, cache);
    }

    /**
     * 所有话题列表 2003
     *
     * @param sinceId
     * @param listener
     * @param cache
     */
    public static void getTopics(long topicID, long sinceId, IEntityListener<BaseListData<Topic>> listener,
                                 boolean cache) {
        String url = new StringBuilder().append("&srv=2003").append("&since_id=").append(sinceId).append("&topic_id=")
                .append(topicID).toString();
        Dao.getEntity(url, new TypeToken<Response<BaseListData<Topic>>>() {
        }, listener, cache);
    }

    /**
     * 获取用户消息列表 2501
     *
     * @param sinceId
     * @param listener
     * @param cache
     */
    public static void getUserMessages(long sinceId, IEntityListener<BaseListData<Message>> listener, boolean cache) {
        String url = new StringBuilder().append("&srv=2501").append("&since_id=").append(sinceId).toString();
        Dao.getEntity(url, new TypeToken<Response<BaseListData<Message>>>() {
        }, listener, cache);
    }

    /**
     * 获取系统消息列表 2502
     *
     * @param sinceId
     * @param listener
     * @param cache
     */
    public static void getSystemMessages(long sinceId, IEntityListener<BaseListData<Message>> listener, boolean cache) {
        String url = new StringBuilder().append("&srv=2502").append("&since_id=").append(sinceId).toString();
        Dao.getEntity(url, new TypeToken<Response<BaseListData<Message>>>() {
        }, listener, cache);
    }

    /**
     * 修改消息状态 2503
     *
     * @param msgId
     * @param listener
     */
    public static void operateMessage(long msgId, IEntityListener<Object> listener) {
        String url = new StringBuilder().append("&srv=2503").append("&msg_id=").append(msgId).toString();
        Dao.getEntity(url, new TypeToken<Response<Object>>() {
        }, listener, false);
    }

    /**
     * 发送个推ID
     *
     * @param clientID
     * @param listener
     */
    public static void sendGetuiClientId(String clientID, IEntityListener<Object> listener) {
        String url = new StringBuilder().append("&srv=2701").append("&token=").append(clientID).toString();
        Dao.getEntity(url, new TypeToken<Response<Object>>() {
        }, listener, false);
    }

    /**
     * 设置消息推送开关
     *
     * @param listener
     */
    public static void setPushNotice(int pushType, int statu, IEntityListener<Object> listener) {
        String url = new StringBuilder().append("&srv=2702").append("&type=").append(pushType).append("&state=")
                .append(statu).toString();
        Dao.getEntity(url, new TypeToken<Response<Object>>() {
        }, listener, false);
    }

    /**
     * 获取消息推送开关
     *
     * @param listener
     */
    public static void getPushNotice(IEntityListener<PushSetting> listener) {
        String url = new StringBuilder().append("&srv=2703").toString();
        Dao.getEntity(url, new TypeToken<Response<PushSetting>>() {
        }, listener, false);
    }

    /**
     * 获取所有的推送消息
     *
     * @param listener
     */
    public static void getPushMassages(IEntityListener<BaseListData<Notification>> listener) {
        String url = new StringBuilder().append("&srv=2704").toString();
        Dao.getEntity(url, new TypeToken<Response<BaseListData<Notification>>>() {
        }, listener, false);
    }

    /**
     * 统计视频播放次数 6001
     *
     * @param videoId
     * @param listener
     */
    public static void addPlayVideoCount(long videoId, IEntityListener<Object> listener) {
        String url = new StringBuilder().append("&srv=6001").append("&video_id=").append(videoId).toString();
        Dao.getEntity(url, new TypeToken<Response<Object>>() {
        }, listener, false);
    }

    /**
     * 统计弹窗需求 3202
     */
    public static void countPopDialog(long id, CountManager.Action action, IEntityListener<Object> listener) {
        int a = action == CountManager.Action.SHOW ? 0 : (CountManager.Action.LEFT == action ? 1 : 2);
        String url = new StringBuilder().append("&srv=3202").append("&id=").append(id).append("&action=").append(a).toString();
        Dao.getEntity(url, new TypeToken<Response<Object>>() {
        }, listener, false);
    }


    /**
     * 验证昵称、签名有效性
     */
    public static void invalidateTextd(int type, String msg, IEntityListener<Object> listener) {
        String url = new StringBuilder().append("&srv=3203").append("&name=").append(msg).append("&type=").append(type).toString();
        Dao.getEntity(url, new TypeToken<Response<Object>>() {
        }, listener, false);
    }

}
