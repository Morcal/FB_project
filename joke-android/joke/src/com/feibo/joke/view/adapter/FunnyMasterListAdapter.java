package com.feibo.joke.view.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.feibo.joke.R;
import com.feibo.joke.model.FunnyMaster;
import com.feibo.joke.model.User;
import com.feibo.joke.utils.UIUtil;
import com.feibo.joke.view.widget.FocusStateView;
import com.feibo.joke.view.widget.FocusStateView.OnStatuClicklistener;
import com.feibo.joke.view.widget.VImageView;

public class FunnyMasterListAdapter extends OnKeyAttentionAdapter<FunnyMaster> {

    public FunnyMasterListAdapter(Context context) {
        super(context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_suggest_friend, null);
            holder = new ViewHolder();
            holder.head = (VImageView) convertView.findViewById(R.id.iv_item_avatar);
            holder.name = (TextView) convertView.findViewById(R.id.tv_item_nickname);
            holder.reson = (TextView) convertView.findViewById(R.id.tv_reason);
            holder.statu = (FocusStateView) convertView.findViewById(R.id.btn_focus);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        FunnyMaster user = getItem(position);

        UIUtil.setVAvatar(user.avatar, user.isSensation(), holder.head);
        
        holder.name.setText(user.nickname);
        holder.reson.setText(user.description);
        holder.statu.setOnStatuClickListener(new OnStatuClicklistener() {
            @Override
            public void onPreperadLogin() {
                //判断是否登陆
            }
        });
        holder.statu.setUser(user);
        
        return convertView;
    }

    public boolean getHasAttention() {
        if(getCount() <= 0) {
            return false;
        }
        boolean has = false;
        for(FunnyMaster fm : getItems()) {
            if(fm.relationship == User.RELATIONSHIP_NULL || fm.relationship == User.RELATIONSHIP_USER_BE_ATTENTION) {
                has = true;
                break;
            }
        }
        return has;
    }
    
    public static class ViewHolder {
        VImageView head;
        TextView name;
        TextView reson;
        FocusStateView statu;
    }

    public void setOneKeySuccess() {
        List<FunnyMaster> list=this.getItems();
        if(list==null || list.size()==0){
            return;
        }
        for(FunnyMaster user:list){
            setOneKeySuccess(user);
        }
        this.notifyDataSetChanged();
    }
}
