package com.feibo.joke.view.dialog;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;

import com.feibo.joke.R;

public class SexDialog extends BaseDialog {

	public static final int MAN = 1;
	public static final int WOMAN = 2;
	
	private DialogItemClickListener listener;
	
	protected SexDialog(Context context) {
		super(context);
	}

	@Override
	protected View initContentView(LayoutInflater inflater) {
		View view = inflater.inflate(R.layout.dialog_sex_choose, null);
		
		view.findViewById(R.id.tv_man).setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(listener != null) {
					listener.onClick(MAN);
				}
				SexDialog.this.dismiss();
			}
		});
		view.findViewById(R.id.tv_woman).setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(listener != null) {
					listener.onClick(WOMAN);
				}
				SexDialog.this.dismiss();
			}
		});
		view.findViewById(R.id.tv_cancel).setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				SexDialog.this.dismiss();
			}
		});
		
		return view;
	}

    public static SexDialog show(Context context) {
    	SexDialog dialog = new SexDialog(context);
        dialog.show();
        return dialog;
    }
	
	public void setOnDialogItemClickListener(DialogItemClickListener listener) {
		this.listener = listener;
	}
	
	public interface DialogItemClickListener {
		void onClick(int type);
	}

}
