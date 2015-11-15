package com.feibo.snacks.view.module.person.login;

import android.view.View;
import android.widget.TextView;

import com.feibo.snacks.R;

/**
 * User: LinMIWi(80383585@qq.com) Time: 2015-07-06 22:32 FIXME
 */
public class ForgetPwdFragment extends BaseLoginFragment {

    private View rightPart;

    @Override
    public View onCreateContentView() {
        View root = super.onCreateContentView();
        loginBtn.setVisibility(View.GONE);
        return root;
    }

    @Override
    protected void executeCommit(String mPhone, String mCode, String mPwd) {
        group.changePwdByMobi(mPhone, mPwd, mCode);
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
        title.setText(R.string.login_title_forget);
        this.rightPart = rightPart;
        rightPart.setVisibility(View.VISIBLE);
        rightPart.setText(R.string.finish);

        rightPart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attemptLogin();
            }
        });
    }

//    @Override
//    protected void changeLoginBtnState(boolean isCanClick) {
//        super.changeLoginBtnState(isCanClick);
//        if (isCanClick && rightPart.getVisibility() != View.VISIBLE) {
//            rightPart.setVisibility(View.VISIBLE);
//        } else if (!isCanClick && rightPart.getVisibility() == View.VISIBLE) {
//            rightPart.setVisibility(View.GONE);
//        }
//    }
}
