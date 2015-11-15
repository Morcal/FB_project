package com.feibo.joke.manager.work;

import android.content.Context;
import android.net.Uri;

import com.feibo.joke.app.AppContext;
import com.feibo.joke.dao.IEntityListener;
import com.feibo.joke.dao.JokeDao;
import com.feibo.joke.dao.ReturnCode;
import com.feibo.joke.manager.LoadListener;
import com.feibo.joke.manager.SimpleEntityListener;
import com.feibo.joke.model.LoginInfo;
import com.feibo.joke.model.Response;
import com.feibo.joke.model.User;
import com.feibo.joke.utils.SPHelper;
import com.feibo.social.base.Platform;

/**
 * 自己相关
 * @author ml_bright
 * @version 2015-4-28  下午12:42:07
 */
public class UserManager {

    private static UserManager manager;
    private User user;

    /** 是否为新注册用户 */
    public boolean isNewUser;

    private UserManager() {
        user = new User();
    }

    public static UserManager getInstance() {
        if (manager == null) {
            manager = new UserManager();
        }
        return manager;
    }

    public User getUser() {
        return user;
    }

    public boolean isLogin() {
        return user != null && user.id > 0;
    }

    public boolean isFromMe(long userid) {
        return userid == user.id;
    }

    public void loginOut(final Context context, final LoadListener listener) {
        JokeDao.loginOut(new SimpleEntityListener(new LoadListener() {

            @Override
            public void onSuccess() {
                user = new User();
                SPHelper.clearUser(context);
                listener.onSuccess();
            }

            @Override
            public void onFail(int code) {
                listener.onFail(code);
            }
        }));
    }

    public Platform getPlatForm() {
        if(!isLogin()) {
            return null;
        }
        return user.platform == 2 ? Platform.QQ : (user.platform == 1 ? Platform.SINA : Platform.WEIXIN);
    }

    public void register(final Platform platform, String openId, String nickName, String avatarUrl, String accessToken,
                         final IEntityListener<LoginInfo> listener) {
        final String ttype = String.valueOf(platform == Platform.QQ ? 2 : (platform == Platform.SINA ? 1 : 3));
        nickName = Uri.encode(nickName);
        JokeDao.login(ttype, openId, accessToken, nickName, avatarUrl, new IEntityListener<LoginInfo>() {
            @Override
            public void result(Response<LoginInfo> response) {
                if(response.rsCode == ReturnCode.RS_SUCCESS) {
                    LoginInfo info = response.data;
                    user = (User)info;
                    user.platform = Integer.valueOf(ttype);
                    isNewUser = info.isNewUser == LoginInfo.NEW_REGISTER;
                }
                listener.result(response);
            }
        });
    }

    public void saveUserToSPHelp(){
        SPHelper.saveUser(AppContext.getContext(), user);
    }

    public void setBeLikeCount(int count) {
        if(user != null && user.detail.beLikeCount != count) {
            user.detail.beLikeCount = count;
            saveUserToSPHelp();
        }
    }

    public void saveBandingRelationship(Context context, int platform) {
        int currentPlatform = user.platform == 1 ? LoginInfo.BANDING_SINA :
                ( user.platform == 2 ? LoginInfo.BANDING_QQ :  LoginInfo.BANDING_WEIXIN);
        int relationship = currentPlatform | platform;
        SPHelper.saveBanding(context, relationship);
    }

    /**
     * 解绑平台
     *
     * @param platformType 代表 1:微博 2:QQ 3:微信 4:手机通讯录
     */
    public static void unBandingRelationship(Context context, int platformType) {
        int ship = SPHelper.getBanding(context);
        if(ship == 0) {
            return;
        }
        int newShip = ship & (~platformType);
        SPHelper.saveBanding(context, newShip);
    }

    public void bandingPlatform(final Platform platform, String openId, final String nickName, final String avatarUrl, String accessToken,
                                final IEntityListener<LoginInfo> listener) {
        final String ttype = String.valueOf(platform == Platform.QQ ? 2 : (platform == Platform.SINA ? 1 : 3));

        JokeDao.bandingPlatform(ttype, openId, accessToken, nickName, avatarUrl, new IEntityListener<LoginInfo>() {
            @Override
            public void result(Response<LoginInfo> response) {
                if(response.rsCode == ReturnCode.RS_SUCCESS) {
                }
                listener.result(response);
            }
        });
    }

    public void modifyUser(final String avatar, final String nickname, final int gender, final String province,
                           final String city, final String signature, final String birthday, final LoadListener listener) {

        JokeDao.modifyUserInfo(avatar, nickname, gender, province,
                city, signature, birthday, new IEntityListener<User>() {

                    @Override
                    public void result(Response<User> response) {
                        if(ReturnCode.RS_SUCCESS != response.rsCode) {
                            listener.onFail(response.rsCode);
                            return;
                        }
                        user.avatar = avatar;
                        user.nickname = nickname;
                        user.detail.gender = gender;
                        user.detail.province = province;
                        user.detail.city = city;
                        user.detail.signature = signature;
                        user.detail.birth = birthday;

                        saveUserToSPHelp();
                        listener.onSuccess();
                    }
                });
    }

}
