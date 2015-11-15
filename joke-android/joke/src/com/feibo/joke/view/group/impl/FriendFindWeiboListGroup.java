package com.feibo.joke.view.group.impl;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.feibo.joke.R;
import com.feibo.joke.dao.ReturnCode;
import com.feibo.joke.manager.work.UserManager;
import com.feibo.joke.model.LoginInfo;
import com.feibo.joke.model.WeiboFriendsItem;
import com.feibo.joke.view.group.BasePullListGroup;
import com.feibo.social.base.Platform;

public class FriendFindWeiboListGroup extends BasePullListGroup<WeiboFriendsItem> implements OnClickListener{

    private OnBandingListener listener;
    
    public FriendFindWeiboListGroup(Context context) {
        super(context, true, false);
    }
    
    @Override
    public ViewGroup onPrepareView() {
        if(UserManager.getInstance().getPlatForm() != Platform.SINA 
                && !LoginInfo.getBandingRelationship(getContext(), LoginInfo.BANDING_SINA)) {
            //未绑定或者登陆的状态
            ViewGroup view = (ViewGroup) LayoutInflater.from(getContext()).inflate(R.layout.prepared_promot, null);
            initPreparedView(view);
            return view;
        }
        return null;
    }
    
    @Override
    public boolean onErrorConsumption(int code) {
        if(code == ReturnCode.RS_WEIBO_OAUTH_TIMEOUT) {
            listener.onWeiboAuthTimeout();
            return true;
        }
        return false;
    }

    private void initPreparedView(View view) {
        Button bt = (Button) view.findViewById(R.id.btn);
        bt.setOnClickListener(this);
        bt.setText("立即召唤");
        
        TextView tx = (TextView) view.findViewById(R.id.tx);
        tx.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.icon_weibo_promote, 0, 0);
        tx.setText("想召唤微博的粉丝吗？");
    }

    @Override
    public void onClick(View v) {
        if(listener != null) {
            listener.onBanding(Platform.SINA);
        }
    }
    
    public void onBandingWeiboListener(OnBandingListener listener) {
        this.listener = listener;
    }
    
    public interface OnBandingListener {
        public void onBanding(Platform platform);
        public void onWeiboAuthTimeout();
    }

}
