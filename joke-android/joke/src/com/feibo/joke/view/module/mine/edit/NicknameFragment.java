package com.feibo.joke.view.module.mine.edit;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import com.feibo.joke.R;
import com.feibo.joke.app.AppContext;
import com.feibo.joke.manager.LoadListener;
import com.feibo.joke.manager.work.OperateManager;
import com.feibo.joke.manager.work.UserManager;
import com.feibo.joke.utils.StringUtil;
import com.feibo.joke.view.BaseTitleFragment;
import com.feibo.joke.view.util.ToastUtil;

public class NicknameFragment extends BaseTitleFragment {

    EditText etName;
    View ivDelete;

    String s;

    private static final int MAX_LENGTH = 15;

    public static Bundle buildBundle(String s) {
        Bundle b = new Bundle();
        b.putString("text", s);
        return b;
    }

    @Override
    public View containChildView() {
        View view = LayoutInflater.from(this.getActivity()).inflate(R.layout.fragment_nickname, null);

        etName = (EditText) view.findViewById(R.id.et_name);
        ivDelete = view.findViewById(R.id.iv_delete);

        s = getActivity().getIntent().getExtras().getString("text");
        if (!StringUtil.isEmpty(s)) {
            etName.setText(s);
            etName.setSelection(etName.getText().toString().length());
        }

        etName.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                boolean isEmpty = StringUtil.isEmpty(etName.getText().toString());
                ivDelete.setVisibility(isEmpty ? View.INVISIBLE : View.VISIBLE);

                getTitleBar().tvHeadRight.setVisibility(isEmpty ? View.INVISIBLE : View.VISIBLE);

                int nSelStart = etName.getSelectionStart();
                int nSelEnd = etName.getSelectionEnd();

                if (s.length() > MAX_LENGTH) {
//                    ToastUtil.showSimpleToast("最大输入字符长度为" + MAX_LENGTH);
                    s.delete(nSelStart - 1, nSelEnd);
                    etName.setTextKeepState(s);
                    //请读者注意这一行，保持光标原先的位置，而 mEditText.setText(s)会让光标跑到最前面，
                    //就算是再加mEditText.setSelection(nSelStart) 也不起作用
                }
            }
        });
        ivDelete.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                etName.setText("");
                ivDelete.setVisibility(View.INVISIBLE);
            }
        });

        NicknameFragment.this.getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        InputMethodManager imm = (InputMethodManager) NicknameFragment.this.getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInputFromInputMethod(etName.getWindowToken(), 0);
        return view;
    }

    @Override
    public int setTitleLayoutId() {
        return R.layout.base_titlebar;
    }

    @Override
    public void setTitlebar() {
        ((TextView) getTitleBar().title).setText("更换签名");
        getTitleBar().tvHeadRight.setVisibility(View.INVISIBLE);
        getTitleBar().tvHeadRight.setText("确认");
        getTitleBar().tvHeadRight.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                String text = etName.getText().toString();
                if (StringUtil.isEmpty(text)) {
                    ToastUtil.showSimpleToast("请输入昵称");
                    return;
                }
                invalidate(text);
            }
        });
    }

    private void invalidate(final String msg) {
        if(s.equals(msg)) {
            this.getActivity().finish();
            return;
        }

        if (!AppContext.isNetworkAvailable()) {
            ToastUtil.showSimpleToast(AppContext.getContext().getResources().getString(R.string.not_network));
            return;
        }
        showLoadingDialog(true, "请稍等..");
        OperateManager.invalidateText(OperateManager.InvalidateType.NAME, msg, new LoadListener() {
            @Override
            public void onSuccess() {
                if (getActivity() == null) {
                    return;
                }
                showLoadingDialog(false, null);

                Bundle bundle = new Bundle();
                bundle.putString("text", msg);
                setFinishBundle(bundle);
                setChangeTypeAndFinish(2);
            }

            @Override
            public void onFail(int code) {
            }
        });
    }

    @Override
    public void onReleaseView() {

    }

}
