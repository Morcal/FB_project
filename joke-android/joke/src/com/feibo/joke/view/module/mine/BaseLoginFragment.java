package com.feibo.joke.view.module.mine;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;

import com.feibo.joke.R;
import com.feibo.joke.app.AppContext;
import com.feibo.joke.app.UmengConstant;
import com.feibo.joke.dao.ReturnCode;
import com.feibo.joke.manager.LoadListener;
import com.feibo.joke.manager.SocialManager;
import com.feibo.joke.manager.SocialManager.ILoginOutListener;
import com.feibo.joke.manager.SocialManager.SocialResultListener;
import com.feibo.joke.manager.work.UserManager;
import com.feibo.joke.model.LoginInfo;
import com.feibo.joke.utils.SPHelper;
import com.feibo.joke.view.BaseTitleFragment;
import com.feibo.joke.view.dialog.LoginDialog;
import com.feibo.joke.view.dialog.LoginDialog.ILoginListener;
import com.feibo.joke.view.util.ToastUtil;
import com.feibo.social.base.Platform;
import com.umeng.analytics.MobclickAgent;

public abstract class BaseLoginFragment extends BaseTitleFragment {

    public static final int OPERATION_CODE_FOUCES = 1;
    public static final int OPERATION_RESET_WEIBO_TOKEN = 2;
    //等在登陆的标记
    private boolean isLoadingAuthorize = false;
    private int operationCode;
    
    private LoginDialog loginDialog;

    private ProgressDialog loadingDialog;

    private OnOperateShareDialogListener onOperateShareDialogListener;
    
    private void login(Platform platform) {
        login(platform, false);
    }
    
    private void login(Platform platform, final boolean isBanding) {
        if(!AppContext.isNetworkAvailable()) {
            ToastUtil.showSimpleToast(getResources().getString(R.string.not_network));
            return;
        }
        if (isLoadingAuthorize || getActivity() == null) {
            return;
        }
        isLoadingAuthorize = true;

        if(platform == Platform.QQ) {
            MobclickAgent.onEvent(getActivity(), UmengConstant.LOGIN, UmengConstant.LOGIN_QQ);
        } else if(platform == Platform.SINA) {
            MobclickAgent.onEvent(getActivity(), UmengConstant.LOGIN, UmengConstant.LOGIN_WEIBO);
        } else if(platform == Platform.WEIXIN) {
            MobclickAgent.onEvent(getActivity(), UmengConstant.LOGIN, UmengConstant.LOGIN_WEIXIN);
        }

        showDialog(isBanding, true);

        postDelayed(loginTimeoutDialog, 6000);

        SocialManager.getInstance(getActivity()).login(getActivity(), isBanding, platform, new SocialResultListener() {
            @Override
            public void onSuccess(LoginInfo info) {
                if (getActivity() == null) {
                    return;
                }
                dialogDismiss();
                if (UserManager.getInstance().isLogin()) {
                    if (!isBanding) {
                        SPHelper.saveBanding(getActivity(), info.bandRelationship);
                        UserManager.getInstance().saveUserToSPHelp();
                        SPHelper.setauthUser(getActivity(), String.valueOf(info.id), info.wskey);
                        ToastUtil.showSimpleToast("登录成功");
                        if (info.nicknameRepeat == 1) {
                            ToastUtil.showSimpleToast("昵称与当前系统冲突，系统默认帮你修改了昵称");
                        }
                    } else {
                        UserManager.getInstance().saveBandingRelationship(getActivity(), LoginInfo.BANDING_SINA);
                        ToastUtil.showSimpleToast("绑定成功");
                    }
                    loginResult(true, operationCode);
                } else {
                    ToastUtil.showSimpleToast("登陆失败");
                }
            }

            @Override
            public void onError(int code, String error) {
                if (getActivity() == null) {
                    return;
                }
                dialogDismiss();
                if (isBanding && code == ReturnCode.RS_ACCOUNT_HAS_BE_BANDING) {
                    ToastUtil.showSimpleToast("该账号已被绑定哦!");
                } else {
                    ToastUtil.showSimpleToast(error);
                }
            }

            @Override
            public void dialogDismiss() {
                removeHandle(loginTimeoutDialog);
                showDialog(isBanding, false);
            }

            @Override
            public void onStart() {
                removeHandle(loginTimeoutDialog);
                showDialog(isBanding, true);
            }
        });
    }

    private void showDialog(boolean isBanding, boolean show) {
        if(show) {
            if (loadingDialog == null) {
                loadingDialog = new ProgressDialog(getActivity());
                loadingDialog.setOnDismissListener(new OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        isLoadingAuthorize = false;
                    }
                });
                loadingDialog.setMessage(isBanding ? "正在绑定中..." : "正在登录中...");
                loadingDialog.setCancelable(true);
                loadingDialog.setCanceledOnTouchOutside(true);
                loadingDialog.show();
            } else {
                if (!loadingDialog.isShowing()) {
                    loadingDialog.show();
                }
            }
        } else {
            if(loadingDialog != null && loadingDialog.isShowing()) {
                loadingDialog.dismiss();
            }
        }
    }

    Runnable loginTimeoutDialog = new Runnable() {
        @Override
        public void run() {
            showDialog(false, false);
        }
    };
    
    /** 绑定 */
    public void bandPlatform(Platform platform) {
        login(platform, true);
    }
    
    public void loginClick() {
        loginClick(0);
    }
    
    /**弹出登录对话框
     * 
     * @param operationCode 操作码，这里传入，回调loginResult()时传回，后续可判断后继续执行之前的操作
     */
    public void loginClick(int operationCode) {
        if(loginDialog != null && loginDialog.isShowing()){
            return;
        }
        this.operationCode = operationCode;
        loginDialog = LoginDialog.show(getActivity(), new ILoginListener() {
            @Override
            public void onLoginClick(Platform platform) {
                login(platform);
            }
        }); 
    }
    
    public void loginOut(int pType, final OnLoginOutListener loginOutListener) {
        final Platform platform = pType == 2 ? Platform.QQ : pType == 1 ? Platform.SINA : Platform.WEIXIN;

        loginOutListener.isLogining();
        UserManager.getInstance().loginOut(getActivity(), new LoadListener() {
            
            @Override
            public void onSuccess() {
                SocialManager.getInstance(getActivity()).loginOut(getActivity(), platform, new ILoginOutListener() {
                    @Override
                    public void onloginOutResult(boolean success) {
                        //不管第三方退出登陆的时候成功，这边都要显示成功
                        loginOutListener.result(true);
                        loginResult(false, operationCode);
                    }
                });
            }
            
            @Override
            public void onFail(int code) {
                loginOutListener.result(false);
                if(code == ReturnCode.NO_NET) {
                    ToastUtil.showSimpleToast(getActivity().getResources().getString(R.string.not_network));
                } else {
                    ToastUtil.showSimpleToast("退出登录失败");
                }
            }
        });
    }

    public interface OnLoginOutListener {
        void result(boolean loginOutSuccess);
        void isLogining();
    }
    
    /**登陆返回结果
     * 
     * @param result 是否登录成功
     * @param operationCode  操作码，由loginClick方法传入，这里传回，用于回调时，可判断后继续执行之前的操作
     */
    public abstract void loginResult(boolean result, int operationCode);
    
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void setOnOperateShareDialogListener(OnOperateShareDialogListener onOperateShareDialogListener) {
        this.onOperateShareDialogListener = onOperateShareDialogListener;
    }

    public interface OnOperateShareDialogListener {
        void delete();

        void report();
    }


}
