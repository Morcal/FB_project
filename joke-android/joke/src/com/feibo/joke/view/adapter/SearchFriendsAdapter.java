package com.feibo.joke.view.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.feibo.joke.R;
import com.feibo.joke.manager.work.UserManager;
import com.feibo.joke.model.User;
import com.feibo.joke.utils.UIUtil;
import com.feibo.joke.view.widget.FocusStateView;
import com.feibo.joke.view.widget.VImageView;
import com.feibo.joke.view.widget.FocusStateView.OnStatuChangeListener;

import fbcore.log.LogUtil;
import fbcore.widget.BaseSingleTypeAdapter;

/**
 * Created by Administrator on 2015/11/11.
 */
public class SearchFriendsAdapter extends BaseSingleTypeAdapter<User> {

    private OnStatuChangeListener onStatuChangeListener;

    public SearchFriendsAdapter(Context context) {
        super(context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {
        ViewHolder holder = null;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_search_friends, null);
            holder.avatar = (VImageView) convertView.findViewById(R.id.item_avatar);
            holder.statu = (FocusStateView) convertView.findViewById(R.id.btn_focus);
            holder.nikeName = (TextView) convertView.findViewById(R.id.item_nickname);
            holder.works = (TextView) convertView.findViewById(R.id.item_works);
            holder.fens = (TextView) convertView.findViewById(R.id.item_fans);
            holder.loves = (TextView) convertView.findViewById(R.id.item_love);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        final User user = getItem(position);

        LogUtil.i("user---------------------------05->",getItem(0).nickname);
        LogUtil.i("user---------------------------01->",getItem(1).nickname);
        LogUtil.i("user---------------------------02->",getItem(2).nickname);

        LogUtil.i("User Count-------------------------------->",getCount()+"");

        UIUtil.setVAvatar(user.avatar, user.isSensation(), holder.avatar);
        holder.statu.setUser(user);
        holder.statu.setOnStatuChangelistener(onStatuChangeListener);
        if (user.id == UserManager.getInstance().getUser().id) {
            holder.statu.setVisibility(View.INVISIBLE);
        } else {
            holder.statu.setVisibility(View.VISIBLE);
        }
        holder.nikeName.setText(user.nickname);
        holder.works.setText("片儿：" + user.detail.worksCount);
        holder.fens.setText("粉丝：" + user.detail.followersCount);
        holder.loves.setText(user.detail.likeCount+"");

        return convertView;
    }

    /***
     * 获取到笑友条目的数量
     * @return
     */
    public int getItemsCount(){
        return getCount();
    }

    public void setOnStatuChangeListener(OnStatuChangeListener onStatuChangeListener) {
        this.onStatuChangeListener = onStatuChangeListener;
    }

    public static class ViewHolder {
        private VImageView avatar;
        private FocusStateView statu;
        private TextView nikeName;
        private TextView works;
        private TextView fens;
        private TextView loves;

    }
}
