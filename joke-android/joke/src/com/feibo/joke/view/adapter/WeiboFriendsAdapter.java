package com.feibo.joke.view.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;

import com.feibo.joke.R;
import com.feibo.joke.model.User;
import com.feibo.joke.model.WeiboFriendsItem;
import com.feibo.joke.utils.UIUtil;
import com.feibo.joke.view.widget.FocusStateView;
import com.feibo.joke.view.widget.FocusStateView.OnStatuClicklistener;
import com.feibo.joke.view.widget.VImageView;

public class WeiboFriendsAdapter extends OnKeyAttentionAdapter<WeiboFriendsItem> {

    // 这里一定要注意使用getItemViewType 要从0开始计数
    private final int TYPE_DIVISION = 0;
    private final int TYPE_CAN_ADD = 1;
    private final int TYPE_CAN_INVITA = 2;

    private int addCount = 0;
    private int invitaCount = 0;
    
    private IOnWeiboItemClickListener onItemListener;

    public WeiboFriendsAdapter(Context context) {
        super(context);
    }

    @Override
    public void setItems(List<WeiboFriendsItem> items) {
        super.setItems(items);
        addCount = 0;
        invitaCount = 0;
        if (items != null && items.size() > 0) {
            for (WeiboFriendsItem item : items) {
                if (item.type == WeiboFriendsItem.TYPE_CAN_ADD) {
                    ++addCount;
                } else if (item.type == WeiboFriendsItem.TYPE_CAN_INVITA) {
                    ++invitaCount;
                }
            }
        }
    }

    @Override
    public int getViewTypeCount() {
        return 3;
    }

    @Override
    public int getItemViewType(int position) {
        int t = 0;
        WeiboFriendsItem item = getItem(position);
        switch (item.type) {
        case WeiboFriendsItem.TYPE_DIVISION_HEADER:
        case WeiboFriendsItem.TYPE_DIVISION_CENTER:
            t = TYPE_DIVISION;
            break;
        case WeiboFriendsItem.TYPE_CAN_INVITA:
            t = TYPE_CAN_INVITA;
            break;
        case WeiboFriendsItem.TYPE_CAN_ADD:
            t = TYPE_CAN_ADD;
            break;
        }
        return t;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        WeiboFriendsItem item = getItem(position);
        if (item.type == WeiboFriendsItem.TYPE_CAN_ADD) {
            return getAddView(item, convertView);
        } else if (item.type == WeiboFriendsItem.TYPE_CAN_INVITA) {
            return getInvitaView(item, convertView);
        } else {
            return getDivisionView(item, convertView, item.type == WeiboFriendsItem.TYPE_DIVISION_HEADER);
        }
    }

    public View getAddView(final WeiboFriendsItem item, View convertView) {
        AddView view = null;
        if (convertView == null) {
            view = new AddView();
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_weibo_friend, null);
            view.head = (VImageView) convertView.findViewById(R.id.item_avatar);
            view.name = (TextView) convertView.findViewById(R.id.item_nickname);
            view.works = (TextView) convertView.findViewById(R.id.item_works);
            view.fans = (TextView) convertView.findViewById(R.id.item_fans);
            view.statu = (FocusStateView) convertView.findViewById(R.id.item_focus);
            view.likes = (TextView) convertView.findViewById(R.id.item_like);
            convertView.setTag(view);
        } else {
            view = (AddView) convertView.getTag();
        }
        
        convertView.setOnClickListener(new OnClickListener() {
            
            @Override
            public void onClick(View v) {
                if(onItemListener != null) {    
                    onItemListener.onLaunchUserDetail(item.user.id);
                }
            }
        });
        
        UIUtil.setVAvatar(item.user.avatar, item.user.isSensation(), view.head);
        
        view.name.setText(item.user.nickname);
        view.works.setText("片儿：" + item.user.detail.worksCount);
        view.fans.setText("粉丝：" + item.user.detail.followersCount);
        view.likes.setText(String.valueOf(item.user.detail.beLikeCount));
        view.statu.setOnStatuClickListener(new OnStatuClicklistener() {
            @Override
            public void onPreperadLogin() {
                //判断是否登陆
            }
        });
        view.statu.setUser(item.user);
        return convertView;
    }

    public View getInvitaView(final WeiboFriendsItem item, View convertView) {
        InvitaView view = null;
        if (convertView == null) {
            view = new InvitaView();
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_weibo_invitation, null);
            view.name = (TextView) convertView.findViewById(R.id.item_nickname);
            view.head = (VImageView) convertView.findViewById(R.id.item_avatar);
            view.statu = (FocusStateView) convertView.findViewById(R.id.item_focus);
            convertView.setTag(view);
        } else {
            view = (InvitaView) convertView.getTag();
        }
        UIUtil.setVAvatar(item.user.avatar, false, view.head);
        view.name.setText(item.user.nickname);
        view.statu.setOnInvitationListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(onItemListener != null) {
					onItemListener.invitation(item.user.nickname);
				}
			}
		});
        return convertView;
    }
    
    public interface IOnWeiboItemClickListener {
    	public void invitation(String beInvitationName);
    	public void onLaunchUserDetail(long userId);
    }
    
    public void setOnInvitationListener(IOnWeiboItemClickListener invitationLister) {
    	this.onItemListener = invitationLister;
    }

    public View getDivisionView(WeiboFriendsItem item, View convertView, boolean isAdd) {
        DivisionView view = null;
        if (convertView == null) {
            view = new DivisionView();
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_weibo_division, null);
            view.tx = (TextView) convertView.findViewById(R.id.tx_division);
            convertView.setTag(view);
        } else {
            view = (DivisionView) convertView.getTag();
        }
        if (isAdd) {
            view.tx.setText(addCount + "个好友待添加");
        } else {
            view.tx.setText(invitaCount + "个好友可邀请");
        }
        return convertView;
    }
    
    public boolean getHasAttention() {
        if(getCount() <= 0) {
            return false;
        }
        boolean has = false;
        for(WeiboFriendsItem fm : getItems()) {
            if(fm.type == WeiboFriendsItem.TYPE_CAN_ADD ) {
                if(fm.user.relationship == User.RELATIONSHIP_NULL || fm.user.relationship == User.RELATIONSHIP_USER_BE_ATTENTION) {
                    has = true;
                    break;
                }
            }
        }
        return has;
    }
    
    public static class AddView {
        VImageView head;
        TextView name;
        TextView works;
        TextView fans;
        TextView likes;
        FocusStateView statu;
        View foucusLayout;
    }

    public static class InvitaView {
        VImageView head;
        TextView name;
        FocusStateView statu;
    }

    public static class DivisionView {
        public TextView tx;
    }

    public void setOneKeySuccess() {
        List<WeiboFriendsItem> list = this.getItems();
        if(list==null || list.size()==0){
            return;
        }
        for(WeiboFriendsItem fm : list){
            if(fm.type == WeiboFriendsItem.TYPE_CAN_ADD) {
                setOneKeySuccess(fm.user);
            }
        }
        this.notifyDataSetChanged();
    }
    
}
