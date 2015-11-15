package com.feibo.joke.view.module.mine;

import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.feibo.joke.R;
import com.feibo.joke.app.DataChangeEventCode;
import com.feibo.joke.manager.work.UserManager;
import com.feibo.joke.utils.ShareUtil;
import com.feibo.joke.view.BaseTitleFragment;
import com.feibo.joke.view.util.LaunchUtil;
import com.feibo.joke.view.util.MessageHintManager;
import com.feibo.joke.view.widget.BaseItemLayout;

public class FriendFindFragment extends BaseTitleFragment implements OnClickListener {
    private View contentView;

    private BaseItemLayout funnyItem;
    private BaseItemLayout weiboItem;
    private BaseItemLayout phoneItem;
    private BaseItemLayout weixinItem;
    private BaseItemLayout qqItem;

    @Override
    public View containChildView() {
        contentView = View.inflate(getActivity(), R.layout.fragment_find_friend, null);
        initView();
        initlistener();
        return contentView;
    }
    
    @Override
    public int setTitleLayoutId() {
        return R.layout.base_titlebar;
    }

    @Override
    public void onReleaseView() {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        case R.id.item_add_funny_friend:
            LaunchUtil.launchSubActivity(getActivity(), FriendFindFunnyFragment.class, null);
            break;
        case R.id.item_add_weibo_friend:
            LaunchUtil.launchSubActivity(getActivity(), FriendFindWeiboFragment.class, null);
            break;
        case R.id.item_add_weixin_friend:
            ShareUtil.invitationWX(getActivity(), UserManager.getInstance().getUser());
            break;
        case R.id.item_add_qq_friend:
            ShareUtil.invitationQQ(getActivity(), UserManager.getInstance().getUser());
            break;
        default:
            break;
        }
    }

    private void initView() {
        funnyItem = (BaseItemLayout) contentView.findViewById(R.id.item_add_funny_friend);
        weiboItem = (BaseItemLayout) contentView.findViewById(R.id.item_add_weibo_friend);
        phoneItem = (BaseItemLayout) contentView.findViewById(R.id.item_add_phone_friend);
        weixinItem = (BaseItemLayout) contentView.findViewById(R.id.item_add_weixin_friend);
        qqItem = (BaseItemLayout) contentView.findViewById(R.id.item_add_qq_friend);
    }

    @Override
    public void setTitlebar() {
        TitleBar titleBar = getTitleBar();
        titleBar.rightPart.setVisibility(View.GONE);
        TextView title = (TextView) titleBar.title;
        title.setText(R.string.add_friend);
    }

    private void initlistener() {
        funnyItem.setOnClickListener(this);
        weiboItem.setOnClickListener(this);
        weixinItem.setOnClickListener(this);
        qqItem.setOnClickListener(this);
        phoneItem.setOnClickListener(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        initRedhint();
    }

    private void initRedhint() {
        MessageHintManager.initFindFriend(getActivity(), funnyItem, weiboItem);
    }
    
    @Override
    public void onDataChange(int code) {
        if(code == DataChangeEventCode.CODE_EVENT_BUS_REDHINT) {
            initRedhint();
        }
    }

}

