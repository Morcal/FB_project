package com.feibo.snacks.view.module.person.login;

import android.app.Activity;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.feibo.snacks.R;
import com.feibo.snacks.model.bean.User;
import com.feibo.snacks.manager.global.RedPointManager;
import com.feibo.snacks.manager.global.UserManager;
import com.feibo.snacks.view.base.BaseSwitchActivity;
import com.feibo.snacks.view.util.LaunchUtil;
import com.feibo.social.ResultListener;
import com.feibo.social.base.Platform;
import com.feibo.social.manager.SocialComponent;

import fbcore.utils.Strings;

/**
 * User: LinMIWi(80383585@qq.com) Time: 2015-07-06 22:26 FIXME
 */
public class LoginFragment extends BaseLoginFragment {

    private View qqLogin;
    private View wxLogin;
    private View wbLogin;
    private View forgetText;
    private View quickRegister;

    @Override
    public View onCreateContentView() {
        View root = super.onCreateContentView();

        qqLogin = root.findViewById(R.id.login_qq);
        wxLogin = root.findViewById(R.id.login_weixin);
        wbLogin = root.findViewById(R.id.login_weibo);
        forgetText = root.findViewById(R.id.login_forget_pwd);
        quickRegister = root.findViewById(R.id.login_quick_register);

        forgetText.setVisibility(View.VISIBLE);
        quickRegister.setVisibility(View.VISIBLE);
        root.findViewById(R.id.login_three_title).setVisibility(View.VISIBLE);
        root.findViewById(R.id.login_item_three).setVisibility(View.VISIBLE);
        root.findViewById(R.id.login_item_code).setVisibility(View.GONE);
        root.findViewById(R.id.login_item_img_code).setVisibility(View.GONE);

        qqLogin.setOnClickListener(this);
        wxLogin.setOnClickListener(this);
        wbLogin.setOnClickListener(this);
        forgetText.setOnClickListener(this);
        quickRegister.setOnClickListener(this);
        return root;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_login;
    }

    @Override
    protected void executeCommit(String mPhone, String mCode, String mPwd) {
        group.longinByMobi(mPhone, mPwd);
    }

    @Override
    protected boolean isCommitCode() {
        return false;
    }

    @Override
    protected void onSetTitleBar(TextView title, TextView rightPart) {
        title.setText(R.string.login_title_login);
    }

    @Override
    public void onClick(View arg0) {
        super.onClick(arg0);
        switch (arg0.getId()) {
        case R.id.login_qq:
            group.longinByQQ();
            break;
        case R.id.login_weixin:
            group.longinByWX();
            break;
        case R.id.login_weibo:
            group.longinByWB();
            break;
        case R.id.login_forget_pwd:
            LaunchUtil.launchActivityForResult(LoginGroup.REQUEST_CODE_FOR_LOGINFRAGMENT, getActivity(),
                    BaseSwitchActivity.class, ForgetPwdFragment.class, null);
            break;
        case R.id.login_quick_register:
            LaunchUtil.launchActivityForResult(LoginGroup.REQUEST_CODE_FOR_LOGINFRAGMENT, getActivity(),
                    BaseSwitchActivity.class, RegisterFragment.class, null);
            break;
        default:
            break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == LoginGroup.RESULT_CODE_FOR_LOGINFRAGMENT) {
            boolean isFinish = false;
            if(data != null ){
                isFinish = data.getBooleanExtra(LoginGroup.RESULT_KEY_IS_SUCCESS, false);
            }
            if(isFinish && getActivity() != null){
                Intent intent = new Intent();
                intent.putExtra(LoginGroup.RESULT_KEY_IS_SUCCESS, true);
                ((Activity) getActivity()).setResult(LoginGroup.RESULT_CODE_FOR_LOGINFRAGMENT, intent);
                getActivity().finish();
            }
        }else if(requestCode == LoginGroup.REQUEST_CODE_FOR_LOGINFRAGMENT ){
            User user= UserManager.getInstance().getUser();
            if(TextUtils.isEmpty(user.getMobiNum()) && Strings.toInt(user.getUid()) > 0){
                exitAccount();
            }
        }
    }

    private void exitAccount() {
        int type = UserManager.getInstance().getUser().getPlatform();
        Platform platform = null;
        if (type == User.PLATFORM_TYPE_QQ) {
            platform = Platform.QQ;
        } else if (type == User.PLATFORM_TYPE_WX) {
            platform = Platform.WEIXIN;
        } else if (type == User.PLATFORM_TYPE_WB) {
            platform = Platform.SINA;
        } else {
            UserManager.getInstance().logout();
            resetRedPoint();
            getActivity().finish();
            return;
        }

        SocialComponent.create(platform, getActivity()).logout(new ResultListener() {
            @Override
            public void onResult(boolean isSuccess, String result) {
                if (isSuccess) {
                    UserManager.getInstance().logout();
                    resetRedPoint();
                    getActivity().finish();
                }
            }
        });
    }

    private void resetRedPoint(){
        RedPointManager.getInstance().resetRedPoint();
    }
}
