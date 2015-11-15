package com.feibo.joke.view.dialog;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;

import com.feibo.joke.R;
import com.feibo.social.base.Platform;

@SuppressLint("InflateParams")
public class LoginDialog extends BaseDialog implements OnClickListener {

    private ILoginListener listener;

    public LoginDialog(Context context) {
        super(context);
    }
    
    @Override
    public View initContentView(LayoutInflater inflater) {
        View view = inflater.inflate(R.layout.dialog_login_layout, null);
        
        View qqBtn = view.findViewById(R.id.login_with_qq);
        View weixinBtn = view.findViewById(R.id.login_with_weixin);
        View weiboBtn = view.findViewById(R.id.login_with_weibo);

        qqBtn.setOnClickListener(this);
        weixinBtn.setOnClickListener(this);
        weiboBtn.setOnClickListener(this);
        return view;
    }

    public static LoginDialog show(Context context, ILoginListener listener) {
        LoginDialog dialog = new LoginDialog(context);
        dialog.show();
        
        dialog.listener = listener;
        return dialog;
    }

    @Override
    public void onClick(View v) {
        this.dismiss();
        if (listener == null) {
            return;
        }
        switch (v.getId()) {
        case R.id.login_with_qq: 
            listener.onLoginClick(Platform.QQ);
            break;
        case R.id.login_with_weibo: 
            listener.onLoginClick(Platform.SINA);
            break;
        case R.id.login_with_weixin: 
            listener.onLoginClick(Platform.WEIXIN);
            break;
        default:
            break;
        }
    }

    public interface ILoginListener {
        public void onLoginClick(Platform platform);
    }

}
