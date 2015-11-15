package com.feibo.joke.view.dialog;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;

import com.feibo.joke.R;

public class PhotoChooseDialog extends BaseDialog {

	public static final int TAKE_PHOTO = 1;
	public static final int PHOTO_ALBUM = 2;
	
	private DialogItemClickListener listener;
	
	protected PhotoChooseDialog(Context context) {
		super(context);
	}

	@Override
	protected View initContentView(LayoutInflater inflater) {
		View view = inflater.inflate(R.layout.dialog_photo_choose, null);
		
		view.findViewById(R.id.tv_photo).setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(listener != null) {
					listener.onClick(PHOTO_ALBUM);
				}
				PhotoChooseDialog.this.dismiss();
			}
		});
		view.findViewById(R.id.tv_take_photo).setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(listener != null) {
					listener.onClick(TAKE_PHOTO);
				}
				PhotoChooseDialog.this.dismiss();
			}
		});
		view.findViewById(R.id.tv_cancel).setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				PhotoChooseDialog.this.dismiss();
			}
		});
		
		return view;
	}

    public static PhotoChooseDialog show(Context context) {
    	PhotoChooseDialog dialog = new PhotoChooseDialog(context);
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
