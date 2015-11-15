package com.feibo.joke.view.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import fbcore.widget.BaseSingleTypeAdapter;

import com.feibo.joke.R;
import com.feibo.joke.manager.work.UserManager;
import com.feibo.joke.model.User;
import com.feibo.joke.model.UserDetail;
import com.feibo.joke.utils.UIUtil;
import com.feibo.joke.view.widget.FocusStateView;
import com.feibo.joke.view.widget.FocusStateView.OnStatuChangeListener;
import com.feibo.joke.view.widget.VImageView;

public class UserListAdapter extends BaseSingleTypeAdapter<User>{

    private Drawable man;
    private Drawable woman;
    
    private OnStatuChangeListener onStatuChangeListener;

    public UserListAdapter(Context context) {
        super(context);
        man = context.getResources().getDrawable(R.drawable.icon_man_big);
        woman = context.getResources().getDrawable(R.drawable.icon_woman_big);
        man.setBounds(0, 0, man.getMinimumWidth(), man.getMinimumHeight());
        woman.setBounds(0, 0, woman.getMinimumWidth(), woman.getMinimumHeight());
    }

    public static class Holder {
        public VImageView avatar;
        public TextView name;
//        public TextView desc;
        public FocusStateView statu;
    }
    
    public void setOnStatuChangelistener(OnStatuChangeListener onStatuChangeListener) {
        this.onStatuChangeListener = onStatuChangeListener;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Holder h = null;
        if(convertView == null) {
            h = new Holder();
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_fans, null);
            h.avatar = (VImageView) convertView.findViewById(R.id.item_avatar);
            h.name = (TextView) convertView.findViewById(R.id.item_nickname);
//            h.desc = (TextView) convertView.findViewById(R.id.item_desc);
            h.statu = (FocusStateView) convertView.findViewById(R.id.btn_focus);
            convertView.setTag(h);
        } else {
            h = (Holder) convertView.getTag();
        }
        final User user = getItem(position);

        UIUtil.setVAvatar(user.avatar, user.isSensation(), h.avatar);
        
        h.name.setText(user.nickname);
        h.statu.setUser(user);
        h.statu.setOnStatuChangelistener(onStatuChangeListener);
        if(user.id == UserManager.getInstance().getUser().id) {
            h.statu.setVisibility(View.INVISIBLE);
        } else {
            h.statu.setVisibility(View.VISIBLE);
        }
        
        if(user.detail.gender == UserDetail.MAN) {
            h.name.setCompoundDrawables(null, null, man, null);
        } else if(user.detail.gender == UserDetail.WOMAN){
            h.name.setCompoundDrawables(null, null, woman, null);
        } else {
            h.name.setCompoundDrawables(null, null, null, null);
        }

//        if(user.detail.signature == null) {
//            h.desc.setVisibility(View.GONE);
//        } else {
//            h.desc.setVisibility(View.VISIBLE);
//            h.desc.setText(user.detail.signature);
//        }

        return convertView;
    }

}
