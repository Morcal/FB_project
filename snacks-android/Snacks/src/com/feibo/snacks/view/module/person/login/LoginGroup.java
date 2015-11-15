package com.feibo.snacks.view.module.person.login;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.text.TextUtils;

import com.feibo.snacks.R;
import com.feibo.snacks.app.AppContext;
import com.feibo.snacks.manager.global.RedPointManager;
import com.feibo.snacks.manager.global.UserManager;
import com.feibo.snacks.manager.global.UserManager.ResultListener;
import com.feibo.snacks.model.bean.User;
import com.feibo.snacks.util.SPHelper;
import com.feibo.snacks.view.base.BaseSwitchActivity;
import com.feibo.snacks.view.util.LaunchUtil;
import com.feibo.snacks.view.util.RemindControl;
import com.feibo.social.base.Platform;
import com.feibo.social.manager.SocialComponent;
import com.feibo.social.model.PlatformInfo;

import java.util.Timer;
import java.util.TimerTask;

public class LoginGroup {

    /**
     * 倒计时
     */
    public static final int COUNT_DOWN_TIME = 60;
    public static final int COUNT_DOWN_DELAY = 0;
    public static final int COUNT_DOWN_PERIOD = 1000;

    public static final int RESULT_CODE_FOR_LOGINFRAGMENT = 0x123;
    public static final int REQUEST_CODE_FOR_LOGINFRAGMENT = 0x124;
    public static final String RESULT_KEY_IS_SUCCESS = "result_key_is_success";

    private Timer timer;
    private TimerTask task;
    private int curtSecond;

    private Context context;
    private UserManager userManager;

    private ProgressDialog dialog;

    private boolean isLoadingAuthorize = false;

    public LoginGroup(Context context) {
        this.context = context;
        userManager = UserManager.getInstance();
    }

    public void registerByMobi(String num, String pwd, String code) {
        if (!isCanLogin()) {
            return;
        }
        userManager.registerByMobile(num, pwd, code, new MResultListener(R.string.register_success,
                R.string.register_fail));
    }

    public void changePwdByMobi(String num, String pwd, String code) {
        if (!isCanLogin()) {
            return;
        }
        userManager.changePasswordByMobile(num, pwd, code, new MResultListener(R.string.change_pwd_success,
                R.string.change_pwd_fail));
    }

    public void bindMobi(String num, String pwd, String code) {
        if (!isCanLogin()) {
            return;
        }
        userManager.bindMobile(num, pwd, code, new MResultListener(R.string.bind_success, R.string.bind_fail));
    }

    public void longinByMobi(String num, String pwd) {
        if (!isCanLogin()) {
            return;
        }
        userManager.loginByMobile(num, pwd, new MResultListener(R.string.login_success, R.string.login_fail));
    }

    public void longinByQQ() {
        authorizePlatform(Platform.QQ);
    }

    public void longinByWX() {
        authorizePlatform(Platform.WEIXIN);
    }

    public void longinByWB() {
        authorizePlatform(Platform.SINA);
    }

    private void authorizePlatform(final Platform platform) {
        if (!isCanLogin()) {
            return;
        }

        SocialComponent.create(platform, (Activity) context).login(new com.feibo.social.ResultListener() {
            @Override
            public void onResult(final boolean isSuccess, String result) {
                hidProgressDialog();
                if (isSuccess) {
                    showProgressDialog();
                    PlatformInfo platformInfo = SocialComponent.create(platform, (Activity) context)
                            .getPlatformUserInfo();
                    int type = User.PLATFORM_TYPE_DEFAULT;
                    if (platform == Platform.QQ) {
                        type = User.PLATFORM_TYPE_QQ;
                    } else if (platform == Platform.WEIXIN) {
                        type = User.PLATFORM_TYPE_WX;
                    } else if (platform == Platform.SINA) {
                        type = User.PLATFORM_TYPE_WB;
                    }
                    String nickName = platformInfo.getNickName();
                    String icon = platformInfo.getHeadImgUrl();
                    String accessToken = platformInfo.getAccessToken();
                    String openId = platformInfo.getOpenid();

                    userManager.registerBySocial(type, openId, nickName, icon, accessToken, new ResultListener() {
                        @Override
                        public void onResult(boolean ifSuccess, String failMsg) {
                            hidProgressDialog();
                            User user = userManager.getUser();
                            if (user == null) {
                                RemindControl.showSimpleToast(context, R.string.login_author_fail);
                                return;
                            }
                            if (ifSuccess) {
                                if (user.isNeedBind()) {
                                    RemindControl.showSimpleToast(context, R.string.login_no_bind);
                                    loginSuccess(user);
                                    LaunchUtil.launchActivityForResult(LoginGroup.REQUEST_CODE_FOR_LOGINFRAGMENT,
                                            context, BaseSwitchActivity.class, BindMobiFragment.class, null);
                                } else {
                                    loginSuccess(user, R.string.login_success);
                                }

                            } else {
                                RemindControl.showSimpleToast(context, failMsg);
                            }
                        }
                    });
                } else {

//                    if (result.equals(context.getString(R.string.sc_download_wx))) {
//                        RemindDialog.RemindSource source = new RemindDialog.RemindSource();
//                        source.title = context.getResources().getString(R.string.love_notify);
//                        source.contentStr = context.getResources().getString(R.string.wx_no_install);
//                        source.confirm = context.getResources().getString(R.string.dialog_confirm);
//                        RemindControl.showRemindDialog(context, source, new RemindControl.OnRemindListener() {
//                            @Override
//                            public void onConfirm() {
//                            }
//
//                            @Override
//                            public void onCancel() {
//                            }
//                        });
//                    }
                    RemindControl.showSimpleToast(context, result);
                }
            }
        });
        hidProgressDialog();
    }

    private boolean isCanLogin() {
        if (isLoadingAuthorize) {
            return false;
        }
        if (!AppContext.isNetworkAvailable()) {
            RemindControl.showSimpleToast(context, context.getResources().getString(R.string.not_network));
            return false;
        }
        showProgressDialog();
        return true;
    }

    private void showProgressDialog() {
        RemindControl.showProgressDialog(context, R.string.login_loading, new OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                isLoadingAuthorize = false;
            }
        });
        isLoadingAuthorize = true;
    }

    private void hidProgressDialog() {
        RemindControl.hideProgressDialog();
    }


    /**
     * 获取图片验证码
     *
     * @param listener
     */
    public void getCodeImg(GetCodeListener listener) {
        getCodeOrCodeImg(null, null, listener);
    }

    /**
     * 获取短信验证码
     *
     * @param mPhone   手机号，为空时获取验证码图片，非空时获取手机验证码
     * @param imgCode  图片验证码，可选，获取手机验证码时需要
     * @param listener
     */
    public void getCode(final String mPhone, final String imgCode, final GetCodeListener listener) {
        curtSecond = COUNT_DOWN_TIME;
        creatTimerTask(listener);
        timer.schedule(task, COUNT_DOWN_DELAY, COUNT_DOWN_PERIOD);
        getCodeOrCodeImg(mPhone, imgCode, listener);
    }

    private void getCodeOrCodeImg(final String mPhone, String imgCode, final GetCodeListener listener) {
        userManager.getCodeOrCodeImg(mPhone, imgCode, new ResultListener() {

            @Override
            public void onResult(boolean ifSuccess, String failMsg) {
                if (ifSuccess) {
                    listener.onSuccess();
                } else {
                    endCountDown();
                    listener.onFail();
                    if (failMsg != null) {
                        RemindControl.showSimpleToast(context, failMsg);
                    } else if (!TextUtils.isEmpty(mPhone)) {
                        RemindControl.showSimpleToast(context, R.string.login_get_code_fail);
                    } else {
                        RemindControl.showSimpleToast(context, R.string.login_get_img_code_fail);
                    }
                }
            }
        });
    }

    public void endCountDown() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
        if (task != null) {
            task.cancel();
            task = null;
        }
    }

    private void creatTimerTask(final GetCodeListener listener) {
        endCountDown();
        timer = new Timer();
        task = new TimerTask() {
            @Override
            public void run() {
                curtSecond--;
                ((Activity) context).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (curtSecond >= 0) {
                            listener.onTimeChange(curtSecond);
                        } else {
                            endCountDown();
                            listener.onTimeEnd();
                        }
                    }
                });
            }
        };
    }

    private class MResultListener implements ResultListener {

        private int strSucc;
        private int strFail;

        /**
         * 构造函数
         */
        public MResultListener(int strSucc, int strFail) {
            this.strSucc = strSucc;
            this.strFail = strFail;
        }

        @Override
        public void onResult(boolean ifSuccess, String failMsg) {
            hidProgressDialog();
            User user = userManager.getUser();
            if (!ifSuccess || user == null) {
                if (failMsg != null) {
                    RemindControl.showSimpleToast(context, failMsg);
                } else {
                    RemindControl.showSimpleToast(context, strFail);
                }
                return;
            }
            loginSuccess(user, strSucc);
        }
    }

    private void loginSuccess(User user, int strSucc) {
        loginSuccess(user);
        RemindControl.showSimpleToast(context, strSucc);
        RedPointManager.getInstance().loadRedPoint();
        Intent intent = new Intent();
        intent.putExtra(RESULT_KEY_IS_SUCCESS, true);
        ((Activity) context).setResult(RESULT_CODE_FOR_LOGINFRAGMENT, intent);
        ((Activity) context).finish();
    }

    private void loginSuccess(User user) {
        SPHelper.saveUser(user);
        SPHelper.setauthUser(user.getUid(), user.getWSKey());
    }

    public interface GetCodeListener {

        void onTimeChange(int curtSecond);

        void onTimeEnd();

        void onSuccess();

        void onFail();
    }
}
