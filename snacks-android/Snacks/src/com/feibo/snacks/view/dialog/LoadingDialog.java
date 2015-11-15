package com.feibo.snacks.view.dialog;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import com.feibo.snacks.R;

public class LoadingDialog extends AlertDialog{
	public LoadingDialog(Context context) {
		super(context);
	}
	public LoadingDialog(Context context, int theme) {
		super(context, theme);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		getWindow().requestFeature(Window.FEATURE_NO_TITLE);
		WindowManager.LayoutParams lp = getWindow().getAttributes();
		lp.gravity = Gravity.CENTER;
        lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
		getWindow().setAttributes(lp);

		View view = getLayoutInflater().inflate(R.layout.widget_dialog_loading, null);
		ImageView pageLoaing = (ImageView) view.findViewById(R.id.widget_loading_pb);
		AnimationDrawable animation = (AnimationDrawable) pageLoaing.getBackground();
        animation.start();

		ViewGroup.LayoutParams clp = new ViewGroup.LayoutParams(
				ViewGroup.LayoutParams.WRAP_CONTENT,
				ViewGroup.LayoutParams.WRAP_CONTENT);
		setContentView(view, clp);
		setCancelable(true);
		setCanceledOnTouchOutside(true);
	}

	public static LoadingDialog create(Context context) {
		LoadingDialog dialog = new LoadingDialog(context);
		return dialog;
	}
}
