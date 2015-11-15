package com.feibo.snacks.view.module.person.setting;

import android.app.Activity;
import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import com.feibo.snacks.R;
import com.feibo.snacks.manager.ILoadingListener;
import com.feibo.snacks.manager.global.UserManager;
import com.feibo.snacks.util.StringUtil;
import com.feibo.snacks.view.base.BaseTitleFragment;
import com.feibo.snacks.view.util.RemindControl;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import fbcore.log.LogUtil;

/**
 * 修改昵称
 * Created by lidiqing on 15-9-6.
 */
public class EditNicknameFragment extends BaseTitleFragment{

    private static final String TAG = EditNicknameFragment.class.getSimpleName();

    @Bind(R.id.edit_nickname)
    EditText nicknameEdit;

    @Bind(R.id.btn_clear)
    View clearBtn;

    TitleViewHolder titleHolder;

    UserManager userManager;

    @Override
    public int onCreateTitleBar() {
        return R.layout.layout_base_header;
    }

    @Override
    public View onCreateContentView() {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_edit_nickname, null);
        ButterKnife.bind(this, view);
        initListener();
        return view;
    }

    @Override
    protected void initTitleBar(TitleBar titleBar) {
        titleHolder = new TitleViewHolder(titleBar.headView);
        titleHolder.titleText.setText(getString(R.string.title_edit_nickname));
        titleHolder.saveBtn.setTextColor(getResources().getColor(R.color.c1));
        titleHolder.saveBtn.setVisibility(View.VISIBLE);
        titleHolder.saveBtn.setText(getString(R.string.btn_save));
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        setFragmentName(getResources().getString(R.string.modifyNickFragment));
        userManager = UserManager.getInstance();
    }

    @Override
    public void onResume() {
        super.onResume();
        nicknameEdit.setHintTextColor(getResources().getColor(R.color.color_one));
        nicknameEdit.setText(userManager.getUser().getNickname());
        nicknameEdit.setSelection(nicknameEdit.getText().length());
        nicknameEdit.requestFocus();
        showInputMethod(nicknameEdit);
    }

    @Override
    public void onPause() {
        super.onPause();
        hideInputMethod();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        titleHolder.onDestroy();
        ButterKnife.unbind(this);
    }

    private void initListener() {
        nicknameEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                int len = s.length();
                if (len == 0) {
                    clearBtn.setVisibility(View.GONE);
                } else {
                    clearBtn.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }

    // 清除用户名
    @OnClick(R.id.btn_clear)
    public void handleClear(){
        nicknameEdit.setText("");
    }

    // 退出
    public void handleQuit(){
        if(getFragmentManager().getBackStackEntryCount() == 0) {
            getActivity().finish();
        }else{
            getFragmentManager().popBackStack();
        }
    }

    // 保存用户名
    public void handleSave() {
        String nick = nicknameEdit.getText().toString().trim();
        if (nick == null || "".equals(nick) || " ".equals(nick)) {
            RemindControl.showSimpleToast(getActivity(), getResources().getString(R.string.toast_nickname_empty));
            return;
        }

        if (nick.length() < 2 || nick.length() > 15) {
            RemindControl.showSimpleToast(getActivity(), getResources().getString(R.string.toast_nickname_error));
            return;
        }

        userManager.modifyNickname(nick, new ILoadingListener() {
            @Override
            public void onSuccess() {
                RemindControl.showSimpleToast(getResources().getString(R.string.change_nick_success));
                handleQuit();
            }

            @Override
            public void onFail(String failMsg) {
                RemindControl.showSimpleToast(failMsg);
            }
        });
    }

    private void hideInputMethod() {
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(nicknameEdit.getWindowToken(), 0);
    }

    private void showInputMethod(EditText editText) {
        InputMethodManager inputManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        inputManager.showSoftInput(editText, 0);
    }



    class TitleViewHolder{

        @Bind(R.id.head_title)
        TextView titleText;

        @Bind(R.id.head_right)
        TextView saveBtn;

        public TitleViewHolder(View view){
            ButterKnife.bind(TitleViewHolder.this, view);
        }

        @OnClick(R.id.head_left)
        public void clickHeadLeft(){
            handleQuit();
        }

        @OnClick(R.id.head_right)
        public void clickHeadRight(){
            handleSave();
        }

        public void onDestroy(){
            ButterKnife.unbind(TitleViewHolder.this);
        }
    }
}
