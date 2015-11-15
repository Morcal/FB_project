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
import com.feibo.joke.view.util.AnimUtil;
import com.feibo.joke.view.util.ToastUtil;

public class SignatureFragment extends BaseTitleFragment {

	private static final int MAX_LENGTH = 70;
	
	EditText etSignature;
	View ivDelete;
	TextView tvLength;

	String s;

	public static Bundle buildBundle(String s) {
		Bundle b = new Bundle();
		b.putString("text", s);
		return b;
	}
	
	@Override
	public View containChildView() {
		View view = LayoutInflater.from(this.getActivity()).inflate(R.layout.fragment_signature, null);
		
		etSignature = (EditText) view.findViewById(R.id.et_signature);
		tvLength = (TextView) view.findViewById(R.id.tv_legth);
		ivDelete = view.findViewById(R.id.iv_delete);

		s = getActivity().getIntent().getExtras().getString("text");
		int lesLength = MAX_LENGTH - s.trim().length();
		int textColor = lesLength < 10 ? R.color.c3_red : R.color.c5_dark_grey;

		tvLength.setText(lesLength + "");
		tvLength.setTextColor(getResources().getColor(textColor));

		if(!StringUtil.isEmpty(s)) {
			etSignature.setText(s.trim());
			etSignature.setSelection(etSignature.getText().toString().length());

			ivDelete.setVisibility(View.VISIBLE);
		} else {
			ivDelete.setVisibility(View.INVISIBLE);
		}
		
		etSignature.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {

			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {

			}

			@Override
			public void afterTextChanged(Editable s) {
				boolean isEmpty = StringUtil.isEmpty(etSignature.getText().toString());
				ivDelete.setVisibility(isEmpty ? View.INVISIBLE : View.VISIBLE);

				getTitleBar().tvHeadRight.setVisibility(isEmpty ? View.INVISIBLE : View.VISIBLE);

				int nSelStart = etSignature.getSelectionStart();
				int nSelEnd = etSignature.getSelectionEnd();

				if (s.length() > MAX_LENGTH) {
//					ToastUtil.showSimpleToast("最大输入字符长度为" + MAX_LENGTH);
					s.delete(nSelStart - 1, nSelEnd);
					etSignature.setTextKeepState(s);
					//请读者注意这一行，保持光标原先的位置，而 mEditText.setText(s)会让光标跑到最前面，
					//就算是再加mEditText.setSelection(nSelStart) 也不起作用
				}

				int lesLength = MAX_LENGTH - s.length();
				int textColor = lesLength < 10 ? R.color.c3_red : R.color.c5_dark_grey;
				tvLength.setText(lesLength + "");
				tvLength.setTextColor(getResources().getColor(textColor));

				if (lesLength <= 0) {
					AnimUtil.scaleAnim(tvLength);
				}
			}
		});
		ivDelete.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				etSignature.setText("");
				ivDelete.setVisibility(View.INVISIBLE);
			}
		});


		SignatureFragment.this.getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
		InputMethodManager imm = (InputMethodManager) SignatureFragment.this.getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.showSoftInputFromInputMethod(etSignature.getWindowToken(), 0);

		return view;
	}

	@Override
	public int setTitleLayoutId() {
		return R.layout.base_titlebar;
	}

	@Override
	public void setTitlebar() {
		((TextView) getTitleBar().title).setText("更换签名");
		getTitleBar().tvHeadRight.setText("确认");
		getTitleBar().tvHeadRight.setVisibility(View.INVISIBLE);
		getTitleBar().tvHeadRight.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				String text = etSignature.getText().toString();
				if(StringUtil.isEmpty(text)) {
					ToastUtil.showSimpleToast("请输入个性签名");
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
		OperateManager.invalidateText(OperateManager.InvalidateType.SIGNATURE, msg, new LoadListener() {
			@Override
			public void onSuccess() {
				if(getActivity() == null) {
					return;
				}
				showLoadingDialog(false, null);
				Bundle bundle = new Bundle();
				bundle.putString("text", msg);
				setFinishBundle(bundle);
				setChangeTypeAndFinish(1);
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
