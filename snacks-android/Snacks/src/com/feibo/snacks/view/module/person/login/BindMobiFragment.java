package com.feibo.snacks.view.module.person.login;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.feibo.snacks.R;
import com.feibo.snacks.manager.global.UserManager;
import com.feibo.snacks.model.bean.UrlBean;
import com.feibo.snacks.view.base.BaseSwitchActivity;
import com.feibo.snacks.view.util.LaunchUtil;
import com.feibo.snacks.view.util.RemindControl;

/**
 * User: LinMIWi(80383585@qq.com) Time: 2015-07-06 22:29 FIXME
 */
public class BindMobiFragment extends BaseLoginFragment {

    private View agreementBoard;
    private View agreementBtn;

    @Override
    public View onCreateContentView() {
        View root=super.onCreateContentView();
        agreementBoard = root.findViewById(R.id.board_agreement);
        agreementBoard.setVisibility(View.VISIBLE);
        agreementBtn = root.findViewById(R.id.btn_agreement);

        agreementBtn.setOnClickListener((View view) -> {
            handleAgreement();
        });

        loginBtn.setText(R.string.login_btn_bind);
        return root;
    }

    @Override
    protected void executeCommit(String mPhone, String mCode, String mPwd) {
        group.bindMobi(mPhone, mPwd, mCode);
    }

    @Override
    protected boolean isCommitCode() {
        return true;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_login;
    }

    @Override
    protected void onSetTitleBar(TextView title, TextView rightPart) {
        title.setText(R.string.login_title_bind);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(!UserManager.getInstance().isLogin()){
            UserManager.getInstance().logout();
        }
    }

    // 进入协议详情页
    public void handleAgreement() {
        RemindControl.showProgressDialog(getActivity(), R.string.dialog_progress, null);
        UserManager.getInstance().getAgreementUrl(new UserManager.OnAgreementListener() {
            @Override
            public void onSuccess(UrlBean urlBean) {
                RemindControl.hideProgressDialog();
                if (!TextUtils.isEmpty(urlBean.url)) {
                    Bundle bundle = new Bundle();
                    bundle.putString(AgreementFragment.PARAM_URL, urlBean.url);
                    LaunchUtil.launchActivity(getActivity(), BaseSwitchActivity.class, AgreementFragment.class, bundle);
                }
            }

            @Override
            public void onFail(String msg) {
                RemindControl.hideProgressDialog();
                RemindControl.showSimpleToast(getActivity(), msg);
            }
        });
    }
}
