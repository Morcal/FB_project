package com.feibo.snacks.manager.global;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import com.feibo.snacks.R;
import com.feibo.snacks.app.AppContext;
import com.feibo.snacks.manager.AbsBeanHelper;
import com.feibo.snacks.manager.AbsLoadHelper;
import com.feibo.snacks.manager.AbsSubmitHelper;
import com.feibo.snacks.manager.ILoadingListener;
import com.feibo.snacks.model.bean.ImgCodeImage;
import com.feibo.snacks.model.bean.NetResult;
import com.feibo.snacks.model.bean.Response;
import com.feibo.snacks.model.bean.UrlBean;
import com.feibo.snacks.model.bean.User;
import com.feibo.snacks.model.dao.DaoListener;
import com.feibo.snacks.model.dao.FileDao;
import com.feibo.snacks.model.dao.ResultCode;
import com.feibo.snacks.model.dao.SnacksDao;
import com.feibo.snacks.model.dao.cache.BaseDataType.PersonDataType;
import com.feibo.snacks.util.SPHelper;
import com.feibo.snacks.view.base.BaseSwitchActivity;
import com.feibo.snacks.view.module.person.login.LoginFragment;
import com.feibo.snacks.view.util.LaunchUtil;
import com.feibo.snacks.view.util.RemindControl;

import java.io.File;

public class UserManager {

    private static UserManager userManager;

    public static UserManager getInstance() {
        if (userManager == null) {
            userManager = new UserManager();
        }
        return userManager;
    }

    private AbsSubmitHelper socialRegisterHelper;   // 第三方登录
    private AbsSubmitHelper mobileRegisterHelper;   // 手机注册
    private AbsSubmitHelper mobileLoginHelper;      // 手机登录
    private AbsSubmitHelper alterPasswordHelper;    // 修改密码
    private AbsSubmitHelper bindMobileHelper;       // 绑定手机

    private AbsSubmitHelper validateCodeImageHelper;    // 获取图片验证码
    private AbsSubmitHelper validateCodeMobileHelper;   // 获取短信验证码

    private AbsSubmitHelper updateNickNameHelper;   // 修改用户昵称

    private AbsBeanHelper agreementHelper;  // 零食小喵协议

    // 第三方平台注册/登录
    private int registerType;
    private String registerOpenId;
    private String registerNickName;
    private String registerIcon;
    private String registerAccessToken;

    // 手机注册
    private String registerNumber;
    private String registerPassword;
    private String registerValidateCode;

    // 手机登录
    private String loginNumber;
    private String loginPassword;

    // 手机找回/修改密码
    private String alterNumber;
    private String alterPassword;
    private String alterValidateCode;

    // 绑定手机号
    private String bindNumber;
    private String bindPassword;
    private String bindValidateCode;

    // 获取短信验证码
    private String validateNumber;
    private String validateImageCode;

    private String updateNickname;


    private boolean isUploadAvatar;

    private Handler handler;

    private User user = new User();

    private UserManager() {
        socialRegisterHelper = new AbsSubmitHelper(PersonDataType.REGISTER) {
            @Override
            public void loadData(boolean needCache, Object params, DaoListener listener) {
                SnacksDao.register(registerType, registerOpenId, registerNickName, registerIcon, registerAccessToken, listener);
            }
        };

        mobileRegisterHelper = new AbsSubmitHelper(PersonDataType.REGISTER) {
            @Override
            public void loadData(boolean needCache, Object params, DaoListener listener) {
                SnacksDao.mobileRegister(registerNumber, registerPassword, registerValidateCode, listener);
            }
        };

        mobileLoginHelper = new AbsSubmitHelper(PersonDataType.REGISTER) {
            @Override
            public void loadData(boolean needCache, Object params, DaoListener listener) {
                SnacksDao.mobileLogin(loginNumber, loginPassword, listener);
            }
        };

        alterPasswordHelper = new AbsSubmitHelper(PersonDataType.REGISTER) {
            @Override
            public void loadData(boolean needCache, Object params, DaoListener listener) {
                SnacksDao.forgetPassword(alterNumber, alterPassword, alterValidateCode, listener);
            }
        };

        bindMobileHelper = new AbsSubmitHelper(PersonDataType.REGISTER) {
            @Override
            public void loadData(boolean needCache, Object params, DaoListener listener) {
                SnacksDao.bindMobile(bindNumber, bindPassword, bindValidateCode, listener);
            }
        };

        validateCodeImageHelper = new AbsSubmitHelper(PersonDataType.IMG_CODE) {
            @Override
            public void loadData(boolean needCache, Object params, DaoListener listener) {
                SnacksDao.getVarifyCodeImage(listener);
            }
        };

        validateCodeMobileHelper = new AbsSubmitHelper() {
            @Override
            public void loadData(boolean needCache, Object params, DaoListener listener) {
                SnacksDao.getVarifyCodeSMS(validateNumber, validateImageCode, listener);
            }
        };

        updateNickNameHelper = new AbsSubmitHelper() {
            @Override
            public void loadData(boolean needCache, Object params, DaoListener listener) {
                SnacksDao.modifyUserName(updateNickname, listener);
            }
        };

        agreementHelper = new AbsBeanHelper(PersonDataType.AGREEMENT) {
            @Override
            public void loadData(boolean needCache, Object paramsDao, DaoListener listener) {
                SnacksDao.getAgreement(listener);
            }
        };

        isUploadAvatar = false;
        handler = new Handler(Looper.getMainLooper());
    }

    /**
     * 初始化用户信息，在应用初始化时执行
     * @param context
     */
    public void initUser(Context context){
        SPHelper.fillUser(user);
    }

    /**
     * 第三方注册
     * @param type 注册平台类型：0，默认；1，QQ；2，微信；3，微博
     * @param openId
     * @param nickName
     * @param icon
     * @param accessToken
     * @param listener
     */
    public void registerBySocial(final int type, String openId, final String nickName, final String icon, String accessToken,
                                 final ResultListener listener) {
        registerType = type;
        registerOpenId = openId;
        registerNickName = nickName;
        registerIcon = icon;
        registerAccessToken = accessToken;
        socialRegisterHelper.submitData(new AbsLoadHelper.HelperListener() {

            public void onSuccess() {
                user = (User) socialRegisterHelper.getData();
                user.setPlatform(type);
                listener.onResult(true, null);
            }

            @Override
            public void onFail(String failMsg) {
                listener.onResult(false, failMsg);
            }
        });
    }

    /**
     * 手机注册
     * @param num
     * @param pwd
     * @param code
     * @param listener
     */
    public void registerByMobile(String num, String pwd, String code, final ResultListener listener) {
        registerNumber = num;
        registerPassword = pwd;
        registerValidateCode = code;
        mobileRegisterHelper.submitData(new AbsLoadHelper.HelperListener() {
            public void onSuccess() {
                user = (User) socialRegisterHelper.getData();
                user.setPlatform(User.PLATFORM_TYPE_DEFAULT);
                listener.onResult(true, null);
            }

            @Override
            public void onFail(String failMsg) {
                listener.onResult(false, failMsg);
            }
        });

    }

    /**
     * 手机登录
     * @param num
     * @param pwd
     * @param listener
     */
    public void loginByMobile(String num, String pwd, final ResultListener listener) {
        loginNumber = num;
        loginPassword = pwd;
        mobileLoginHelper.submitData(new AbsLoadHelper.HelperListener() {
            @Override
            public void onSuccess() {
                user = (User) mobileLoginHelper.getData();
                user.setPlatform(User.PLATFORM_TYPE_DEFAULT);
                listener.onResult(true, null);
            }

            @Override
            public void onFail(String failMsg) {
                listener.onResult(false, failMsg);
            }
        });
    }

    /**
     * 忘记密码后，修改密码的提交方法
     *
     * @param num
     * @param pwd
     * @param code
     * @param listener
     */
    public void changePasswordByMobile(String num, String pwd, String code, final ResultListener listener) {
        alterNumber = num;
        alterPassword = pwd;
        alterValidateCode = code;
        alterPasswordHelper.submitData(new AbsLoadHelper.HelperListener() {
            @Override
            public void onSuccess() {
                user = (User) mobileLoginHelper.getData();
                user.setPlatform(User.PLATFORM_TYPE_DEFAULT);
                listener.onResult(true, null);
            }

            @Override
            public void onFail(String failMsg) {
                listener.onResult(false, failMsg);
            }
        });
    }

    /**
     * 绑定手机号
     *
     * @param num
     * @param pwd
     * @param code
     * @param listener
     */
    public void bindMobile(String num, String pwd, String code, final ResultListener listener) {
        bindNumber = num;
        bindPassword = pwd;
        bindValidateCode = code;
        bindMobileHelper.submitData(new AbsLoadHelper.HelperListener() {
            @Override
            public void onSuccess() {
                user = (User) mobileLoginHelper.getData();
                user.setPlatform(User.PLATFORM_TYPE_DEFAULT);
                listener.onResult(true, null);
            }

            @Override
            public void onFail(String failMsg) {
                listener.onResult(false, failMsg);
            }
        });
    }

    /**
     * 获取获取短信验证码或图片验证码
     *
     * @param num 手机号，为null时获取验证码图片，非null时获取手机验证码
     * @param imgCode 图片验证码，可选，获取手机验证码时需要
     * @param listener
     */
    public void getCodeOrCodeImg(String num, String imgCode, final ResultListener listener) {

        if(num == null){
            getValidateCodeImage(listener);
        }else {
            getValidateCodeMobile(num, imgCode, listener);
        }
    }

    /**
     * 获取图片验证码
     * @param listener
     */
    public void getValidateCodeImage(final ResultListener listener){
        validateCodeImageHelper.submitData(new AbsLoadHelper.HelperListener() {
            @Override
            public void onSuccess() {
                listener.onResult(true, null);
            }

            @Override
            public void onFail(String failMsg) {
                listener.onResult(false, failMsg);
            }
        });
    }

    /**
     * 获取短信验证码
     * @param number
     * @param imageCode
     * @param listener
     */
    public void getValidateCodeMobile(String number, String imageCode, final ResultListener listener){
        validateNumber = number;
        validateImageCode = imageCode;
        validateCodeMobileHelper.submitData(new AbsLoadHelper.HelperListener() {
            @Override
            public void onSuccess() {
                listener.onResult(true, null);
            }

            @Override
            public void onFail(String failMsg) {
                listener.onResult(false, failMsg);
            }
        });
    }

    /**
     * 修改用户名
     * @param nickname
     * @param loadingListener
     */
    public void modifyNickname(String nickname, ILoadingListener loadingListener) {
        updateNickname = nickname;
        updateNickNameHelper.submitData(new AbsLoadHelper.HelperListener() {
            @Override
            public void onSuccess() {
                SPHelper.changeNickname(nickname);
                user.setNickname(nickname);
                loadingListener.onSuccess();
            }

            @Override
            public void onFail(String failMsg) {
                loadingListener.onFail(failMsg);
            }
        });
    }

    // 读取用户协议URL
    public void getAgreementUrl(final OnAgreementListener listener) {
        agreementHelper.loadBeanData(false, new AbsLoadHelper.HelperListener() {
            @Override
            public void onSuccess() {
                listener.onSuccess((UrlBean) agreementHelper.getData());
            }

            @Override
            public void onFail(String failMsg) {
                listener.onFail(failMsg);
            }
        });
    }

    /**
     * 退出登录
     */
    public void logout() {
        SPHelper.setauthUser(null, null);
        SPHelper.clearUser();
        user = new User();
    }

    public User getUser() {
        return user;
    }

    public boolean isLogin() {
        return user != null && user.isLogin();
    }

    public ImgCodeImage getValidateCodeImage() {
        return (ImgCodeImage) validateCodeImageHelper.getData();
    }

    public void uploadAvatar(File avatarFile, UploadAvatarListener listener) {
        if (isUploadAvatar) {
            listener.onFail("头像上传中...");
            return;
        }

        isUploadAvatar = true;
        FileDao.uploadAvatar(avatarFile, new FileDao.FileDaoListener() {
            @Override
            public void success(Object object) {
                runOnUiThread(() -> {
                    SnacksDao.modifyUserAvatar((String) object, new DaoListener<Object>() {
                        @Override
                        public void result(Response<Object> response) {
                            isUploadAvatar = false;
                            if (response.code.equals(NetResult.SUCCESS_CODE)) {
                                user.setAvatar((String) object);
                                SPHelper.changeAvatar((String) object);

                                runOnUiThread(() -> {
                                    listener.onSuccess((String) object);
                                });
                                return;
                            }

                            if (response.code.equals(NetResult.NOT_NET.responseCode)) {
                                runOnUiThread(() -> {
                                    listener.onFail(ResultCode.RS_NET_NOT_EXIST.msg);
                                });
                                return;
                            }

                            runOnUiThread(() -> {
                                listener.onFail(ResultCode.RS_UPLOAD_IMAGE_FAIL.msg);
                            });
                        }
                    });
                });
            }

            @Override
            public void progress(float progress) {
            }

            @Override
            public void fail(ResultCode resultCode) {
                isUploadAvatar = false;
                runOnUiThread(() -> {
                    listener.onFail(resultCode.msg);
                });
            }
        });
    }

    private void runOnUiThread(Runnable runnable) {
        handler.post(runnable);
    }

    public void getLoginState(Context context,ILoginStateListener listener) {
        if (!AppContext.isNetworkAvailable()) {
            RemindControl.showSimpleToast(context, context.getResources().getString(R.string.not_network));
            return ;
        }
        if (!isLogin()) {
            RemindControl.showSimpleToast(context, "登录后才能参加活动哟~");
            LaunchUtil.launchActivityForResult(LaunchUtil.LOGIN_REQUEST_CODE, context, BaseSwitchActivity.class, LoginFragment.class, null);
        } else {
            listener.onState(true);
        }
    }

    public interface UploadAvatarListener {
        void onSuccess(String url);
        void onFail(String msg);
    }

    public interface ResultListener {
        void onResult(boolean ifSuccess, String failMsg);
    }

    public interface OnAgreementListener {
        void onSuccess(UrlBean urlBean);
        void onFail(String msg);
    }

    public interface ILoginStateListener {
        void onState(boolean isLogin);
    }
}
