package com.feibo.joke.view.group.impl;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.feibo.joke.R;
import com.feibo.joke.manager.work.UserManager;
import com.feibo.joke.video.manager.UploadVideoManager;
import com.feibo.joke.view.group.BasePullWaterGroup;
import com.feibo.joke.view.module.mine.FriendFindFragment;
import com.feibo.joke.view.util.LaunchUtil;

public abstract class DynamicListGroup extends BasePullWaterGroup {

    public DynamicListGroup(Context context) {
        super(context);
    }

    @Override
    public ViewGroup onPrepareView() {
        if (!UserManager.getInstance().isLogin()) {
            // 未登录状态
            ViewGroup view = (ViewGroup) LayoutInflater.from(getContext()).inflate(R.layout.prepared_promot, null);
            initPreparedView(view);
            return view;
        }
        return null;
    }

    private void initPreparedView(View view) {
        Button bt = (Button) view.findViewById(R.id.btn);
        bt.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                onLoginClick();
            }
        });
        bt.setText(getContext().getResources().getString(R.string.login_now));

        TextView tx = (TextView) view.findViewById(R.id.tx);
        tx.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.icon_not_login, 0, 0);
        tx.setText(getContext().getResources().getString(R.string.promot_login));
    }

    @Override
    public void onLoadingHelperFailButtonClick() {
        // 勾搭新朋友
        LaunchUtil.launchSubActivity(getContext(), FriendFindFragment.class, null);
    }

    public abstract void onLoginClick();


}
