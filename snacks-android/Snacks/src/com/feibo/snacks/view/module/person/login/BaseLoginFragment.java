package com.feibo.snacks.view.module.person.login;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.graphics.Bitmap;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager.LayoutParams;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import fbcore.utils.Utils;

import com.feibo.snacks.R;
import com.feibo.snacks.manager.global.UserManager;
import com.feibo.snacks.util.Util;
import com.feibo.snacks.view.base.BaseTitleFragment;
import com.feibo.snacks.view.module.person.login.LoginGroup.GetCodeListener;
import com.feibo.snacks.view.util.RemindControl;

public abstract class BaseLoginFragment extends BaseTitleFragment implements OnClickListener {

    private View root;
    private EditText phoneEdit;
    private EditText codeEdit;
    private Button codeBtn;
    private EditText pwdEdit;
    protected View pwdEye;
    protected View phoneCancelBtn;
    protected View pwdCancelBtn;
    protected View codeCancelBtn;
    protected Button loginBtn;

    protected LoginGroup group;
    private EditText imgCodeEdit;
    private ImageView imgCodeIv;
    private ImageView imgCodeRefresh;
    private View imgCodeClearBtn;
    private RotateAnimation rotateAnimation;
    private TextView imgCodeTv;

    @Override
    public int onCreateTitleBar() {
        return R.layout.layout_base_header;
    }

    @Override
    public View onCreateContentView() {
        getActivity().getWindow().setSoftInputMode(
                LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN | LayoutParams.SOFT_INPUT_ADJUST_NOTHING);

        root = LayoutInflater.from(getActivity()).inflate(getLayoutId(), null);
        group = new LoginGroup(getActivity());
        initView();
        setTitleBar();
        initListener();
        initEditTextListener();
        if(isCommitCode()){
            refreshCodeImg();
        }
        return root;
    }

    @Override
    public void onClick(View arg0) {
        switch (arg0.getId()) {
        case R.id.login_code_btn:
            getMobiCode();
            break;
        case R.id.login_pwd_eye:
            setPwdState();
            break;
        case R.id.login_btn:
            attemptLogin();
            break;
        case R.id.login_phone_clear:
            phoneEdit.setText(null);
            break;
        case R.id.login_pwd_clear:
            pwdEdit.setText(null);
            break;
        case R.id.login_code_clear:
            codeEdit.setText(null);
            break;
        case R.id.login_img_code_clear:
            imgCodeEdit.setText(null);
            break;
        case R.id.login_img_code_refresh:
            refreshCodeImg();
            break;
        default:
            break;
        }
    }

    private void refreshCodeImg() {
        if(getActivity() == null ){
            return;
        }
        startRefreshAnimation();
        imgCodeIv.setImageResource(R.drawable.default_loading);
        group.getCodeImg(new GetCodeListener() {
            @Override
            public void onTimeChange(int curtSecond) {
            }

            @Override
            public void onTimeEnd() {
            }

            @Override
            public void onSuccess() {
                resetCodeImg();
                stopRefreshAnimation();
            }

            @Override
            public void onFail() {
                if(getActivity() == null){
                    return;
                }
                stopRefreshAnimation();
                imgCodeIv.setImageResource(R.drawable.default_load_fail);
            }
        });
    }

    public void startRefreshAnimation() {
        if(rotateAnimation == null){
            rotateAnimation = new RotateAnimation(0, 360,
                    RotateAnimation.RELATIVE_TO_SELF, 0.5f,
                    RotateAnimation.RELATIVE_TO_SELF, 0.5f);
            rotateAnimation.setDuration(500);
            rotateAnimation.setInterpolator(new LinearInterpolator());
            rotateAnimation.setRepeatCount(RotateAnimation.INFINITE);
            rotateAnimation.setRepeatMode(RotateAnimation.ABSOLUTE);
        }
        imgCodeRefresh.startAnimation(rotateAnimation);
        imgCodeRefresh.setEnabled(false);
    }

    public void stopRefreshAnimation() {
        imgCodeRefresh.clearAnimation();
        imgCodeRefresh.setEnabled(true);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        group.endCountDown();
        group = null ;
    }

    private void setPwdState() {
        if (pwdEye.isSelected()) {
            pwdEye.setSelected(false);
            pwdEdit.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            pwdEdit.setSelection(pwdEdit.getText().toString().length());
        } else {
            pwdEye.setSelected(true);
            pwdEdit.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            pwdEdit.setSelection(pwdEdit.getText().toString().length());
        }
    }

    private void initEditTextListener() {
        MTextWather phoneWather = new MTextWather(phoneEdit, phoneCancelBtn, R.string.login_edit_err_not_num);
        MTextWather codeWather = new MTextWather(codeEdit, codeCancelBtn, R.string.login_edit_err_chanese);
        MTextWather imgCodeWather = new MTextWather(imgCodeEdit, imgCodeClearBtn, R.string.login_edit_err_chanese);
        MTextWather pwdWather = new MTextWather(pwdEdit, pwdCancelBtn, R.string.login_edit_err_chanese);

        phoneEdit.addTextChangedListener(phoneWather);
        codeEdit.addTextChangedListener(codeWather);
        imgCodeEdit.addTextChangedListener(imgCodeWather);
        pwdEdit.addTextChangedListener(pwdWather);

        phoneEdit.setOnFocusChangeListener(phoneWather);
        codeEdit.setOnFocusChangeListener(codeWather);
        imgCodeEdit.setOnFocusChangeListener(imgCodeWather);
        pwdEdit.setOnFocusChangeListener(pwdWather);
    }

    private void initListener() {
        codeBtn.setOnClickListener(this);
        pwdEye.setOnClickListener(this);
        loginBtn.setOnClickListener(this);
        phoneCancelBtn.setOnClickListener(this);
        pwdCancelBtn.setOnClickListener(this);
        codeCancelBtn.setOnClickListener(this);
        imgCodeClearBtn.setOnClickListener(this);
        imgCodeRefresh.setOnClickListener(this);
        loginBtn.setEnabled(false);
        codeBtn.setEnabled(false);
    }

    private void getMobiCode() {
        if (codeBtn.isSelected()) {
            return;
        }
        String mPhone = phoneEdit.getText().toString();
        String mImgCode = imgCodeEdit.getText().toString();

        boolean cancel = false;
        EditText focusView = null;

        // 判断图片验证码
        if (TextUtils.isEmpty(mImgCode)) {
            RemindControl.showSimpleToast(getActivity(), R.string.login_edit_err_code_null);
            focusView = imgCodeEdit;
            cancel = true;
        } else if (!mImgCode.matches(getString(R.string.login_img_code_rule))) {
            RemindControl.showSimpleToast(getActivity(), R.string.login_edit_err_img_code_rule);
            focusView = imgCodeEdit;
            cancel = true;
        }

        // 判断手机号格式
        if (TextUtils.isEmpty(mPhone)) {
            RemindControl.showSimpleToast(getActivity(), R.string.login_edit_err_phone_null);
            focusView = phoneEdit;
            cancel = true;
        } else if (!mPhone.matches(getString(R.string.login_phone_rule))) {
            RemindControl.showSimpleToast(getActivity(), R.string.login_edit_err_phone_rule);
            focusView = phoneEdit;
            cancel = true;
        }

        if (cancel) {
            // 如果格式错误，输入框重新获得输入焦点
            cursorMove2End(focusView, focusView.getText().toString().length());
            return;
        }

        group.getCode(mPhone, mImgCode, new GetCodeListener() {

            @Override
            public void onTimeEnd() {
                codeBtn.setText(R.string.get_code);
                codeBtn.setSelected(false);
            }

            @Override
            public void onSuccess() {
                cursorMove2End(codeEdit, codeEdit.getText().toString().length());
            }

            @Override
            public void onFail() {
                codeBtn.setText(R.string.get_code);
                codeBtn.setEnabled(false);
                codeBtn.setSelected(false);
                codeBtn.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        refreshCodeImg();
                    }
                },500);

//                resetCodeImg();
            }

            @Override
            public void onTimeChange(int curtSecond) {
                codeBtn.setSelected(true);
                codeBtn.setText(curtSecond + getString(R.string.login_wait_get_code));
            }
        });
    }

    private void resetCodeImg() {
//        UIUtil.setImage(UserManager.getInstance().getValidateCodeImage().imgUrl, imgCodeIv, 0,0);

//        if(getActivity() == null ){
//            return;
//        }
//        Picasso.with(getActivity()).load(UserManager.getInstance().getValidateCodeImage().imgUrl).placeholder(R.drawable.default_loading).error(R.drawable.default_load_fail).into(imgCodeIv, new Callback() {
//            @Override
//            public void success() {
//                stopRefreshAnimation();
//            }
//
//            @Override
//            public void onError() {
//                stopRefreshAnimation();
//            }
//        });
        String imgBytes = UserManager.getInstance().getValidateCodeImage().imgBytes;

        Bitmap bitmap = Util.stringtoBitmap(imgBytes);
        if(bitmap == null){
            imgCodeIv.setImageResource(R.drawable.default_load_fail);
            return;
        }
        imgCodeIv.setImageBitmap(bitmap);
    }

    protected void attemptLogin() {
        String mPhone = phoneEdit.getText().toString();
        String mCode = codeEdit.getText().toString();
        String mPwd = pwdEdit.getText().toString();
        boolean cancel = false;
        EditText focusView = null;

        // 判断密码格式
        if (TextUtils.isEmpty(mPwd)) {
            RemindControl.showSimpleToast(getActivity(), R.string.login_edit_err_pwd_null);
            focusView = pwdEdit;
            cancel = true;
        } else if (!mPwd.matches(getString(R.string.login_pwd_rule))) {
            RemindControl.showSimpleToast(getActivity(), R.string.login_edit_err_pwd_rule);
            focusView = pwdEdit;
            cancel = true;
        }
        // 判断验证码格式
        if (isCommitCode()) {
            if (TextUtils.isEmpty(mCode)) {
                RemindControl.showSimpleToast(getActivity(), R.string.login_edit_err_code_null);
                focusView = codeEdit;
                cancel = true;
            } else if (!mCode.matches(getString(R.string.login_code_rule))) {
                RemindControl.showSimpleToast(getActivity(), R.string.login_edit_err_code_rule);
                focusView = codeEdit;
                cancel = true;
            }
        }
        // 判断手机号格式
        if (TextUtils.isEmpty(mPhone)) {
            RemindControl.showSimpleToast(getActivity(), R.string.login_edit_err_phone_null);
            focusView = phoneEdit;
            cancel = true;
        } else if (!mPhone.matches(getString(R.string.login_phone_rule))) {
            RemindControl.showSimpleToast(getActivity(), R.string.login_edit_err_phone_or_pwd_rule);
            focusView = phoneEdit;
            cancel = true;
        }

        if (cancel) {
            // 如果格式错误，输入框重新获得输入焦点
            cursorMove2End(focusView, focusView.getText().toString().length());
        } else {
            executeCommit(mPhone, mCode, mPwd);
        }
    }

    private void cursorMove2End(EditText focusView, int length) {
        focusView.requestFocus();
        focusView.setSelection(length);
    }

    private void setTitleBar() {
        TextView title = (TextView) getTitleBar().title;
        TextView rightPart = (TextView) getTitleBar().rightPart;
        onSetTitleBar(title, rightPart);

        getTitleBar().leftPart.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
//                Utils.hideInputKeyboard(getActivity());
                getActivity().finish();
            }
        });
    }

    private void initView() { // findView，并设置显示隐藏View
        phoneEdit = (EditText) root.findViewById(R.id.login_phone_edit);
        codeEdit = (EditText) root.findViewById(R.id.login_code_edit);
        imgCodeEdit = (EditText) root.findViewById(R.id.login_img_code_edit);
        codeBtn = (Button) root.findViewById(R.id.login_code_btn);
        imgCodeIv = (ImageView) root.findViewById(R.id.login_img_code_iv);
        imgCodeTv = (TextView) root.findViewById(R.id.login_img_code_tv);
        imgCodeRefresh = (ImageView) root.findViewById(R.id.login_img_code_refresh);
        pwdEdit = (EditText) root.findViewById(R.id.login_pwd_edit);
        pwdEye = root.findViewById(R.id.login_pwd_eye);
        loginBtn = (Button) root.findViewById(R.id.login_btn);
        phoneCancelBtn = root.findViewById(R.id.login_phone_clear);
        imgCodeClearBtn = root.findViewById(R.id.login_img_code_clear);
        pwdCancelBtn = root.findViewById(R.id.login_pwd_clear);
        codeCancelBtn = root.findViewById(R.id.login_code_clear);
    }

    protected void changeLoginBtnState(boolean isCanClick) {
        if (isCanClick && !loginBtn.isEnabled()) {
            loginBtn.setEnabled(true);
            loginBtn.setBackgroundResource(R.drawable.bg_login);
        } else if (!isCanClick && loginBtn.isEnabled()) {
            loginBtn.setEnabled(false);
            loginBtn.setBackgroundResource(R.drawable.btn_login_unable);
        }
    }

    protected abstract void executeCommit(String mPhone, String mCode, String mPwd);

    protected abstract void onSetTitleBar(TextView title, TextView rightPart);

    protected abstract boolean isCommitCode();

    protected abstract int getLayoutId();

    private class MTextWather implements TextWatcher, View.OnFocusChangeListener {

        // 监听改变的文本框
        private EditText editText;
        private View clearView;
        private int errorInputStrId;

        public MTextWather(EditText editText, View clearView, int errorInputStrId) {
            this.editText = editText;
            this.clearView = clearView;
            this.errorInputStrId = errorInputStrId;
        }

        @Override
        public void onTextChanged(CharSequence ss, int start, int before, int count) {
            String editable = editText.getText().toString();
            String str = stringFilter(editable.toString());
            if (!editable.equals(str)) {
                RemindControl.showSimpleToast(getActivity(), errorInputStrId);
                editText.setText(str);
                editText.setSelection(str.length());
            }
        }

        @Override
        public void afterTextChanged(Editable s) {
            setClearViewState(editText.getText().toString(), clearView);

            String mPhone = phoneEdit.getText().toString();
            String mCode = codeEdit.getText().toString();
            String mImgCode = imgCodeEdit.getText().toString();
            String mPwd = pwdEdit.getText().toString();

            if (isCommitCode()) {
                if (!TextUtils.isEmpty(mPhone) && !TextUtils.isEmpty(mImgCode)) {
                    codeBtn.setEnabled(true);
                } else {
                    codeBtn.setEnabled(false);
                }
            }

            if (!TextUtils.isEmpty(mPhone) && !TextUtils.isEmpty(mPwd)) {
                if (isCommitCode() && TextUtils.isEmpty(mCode)) {
                    // changeLoginBtnState(false);
                    loginBtn.setEnabled(false);
                    return;
                }
                // changeLoginBtnState(true);
                loginBtn.setEnabled(true);
            } else {
                // changeLoginBtnState(false);
                loginBtn.setEnabled(false);
            }
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            if (hasFocus) {
                setClearViewState(editText.getText().toString(), clearView);
            } else {
                clearView.setVisibility(View.GONE);
            }
        }

        private String stringFilter(String str) {
            // 只允许字母和数字
            String regEx = getString(R.string.login_rule_ch_null);
            Pattern p = Pattern.compile(regEx);
            Matcher m = p.matcher(str);
            return m.replaceAll("").trim();
        }

        private void setClearViewState(String editString, View view) {
            if (TextUtils.isEmpty(editString) && view.getVisibility() == View.VISIBLE) {
                view.setVisibility(View.GONE);
            } else if (!TextUtils.isEmpty(editString) && view.getVisibility() != View.VISIBLE) {
                view.setVisibility(View.VISIBLE);
            }
        }
    }
}
