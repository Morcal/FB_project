package com.feibo.joke.view.module.setting;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.feibo.joke.R;
import com.feibo.joke.manager.LoadListener;
import com.feibo.joke.manager.work.PushManager;
import com.feibo.joke.model.Push;
import com.feibo.joke.utils.SPHelper;
import com.feibo.joke.view.BaseTitleFragment;
import com.feibo.joke.view.util.ToastUtil;
import com.feibo.joke.view.widget.BaseItemLayout;
import com.feibo.joke.view.widget.BaseItemLayout.OnToggleListener;

public class SettingMessageFragment extends BaseTitleFragment implements OnToggleListener{

    private BaseItemLayout itemMessage;
    private BaseItemLayout itemLike;
    private BaseItemLayout itemFans;
    private BaseItemLayout itemNotice;
    
    private boolean messageFail;
    private boolean likeFail;
    private boolean fansFail;
    private boolean noticeFail;
    
    @Override
    public View containChildView() {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_setting_message, null);
        itemMessage = (BaseItemLayout) view.findViewById(R.id.item_message);
        itemLike = (BaseItemLayout) view.findViewById(R.id.item_like);
        itemFans = (BaseItemLayout) view.findViewById(R.id.item_fans);
        itemNotice = (BaseItemLayout) view.findViewById(R.id.item_notice);
        
        itemMessage.setOnToggleStateChangeListener(this, itemMessage.getId());
        itemLike.setOnToggleStateChangeListener(this, itemLike.getId());
        itemFans.setOnToggleStateChangeListener(this, itemFans.getId());
        itemNotice.setOnToggleStateChangeListener(this, itemNotice.getId());
        
        itemMessage.initToggleButton(SPHelper.getPushNotice(getActivity(), Push.NOTICE_NEW_MESSGAE));
        itemLike.initToggleButton(SPHelper.getPushNotice(getActivity(), Push.NOTICE_LIKE));
        itemFans.initToggleButton(SPHelper.getPushNotice(getActivity(), Push.NOTICE_FANS));
        itemNotice.initToggleButton(SPHelper.getPushNotice(getActivity(), Push.NOTICE_NEW_SYSTEM_MESSAGE));
        
        return view;
    }

    @Override
    public void setTitlebar() {
        ((TextView)getTitleBar().title).setText("消息设置");
        getTitleBar().rightPart.setVisibility(View.INVISIBLE);
    }

    @Override
    public int setTitleLayoutId() {
        return R.layout.base_titlebar;
    }

    @Override
    public void onReleaseView() {
        
    }

    @Override
    public void onStateChange(int viewId, boolean open) {
        switch (viewId) {
        case R.id.item_message:
            if(messageFail) {
                messageFail = false;
                return;
            }
            if(itemMessage.isEnabled()) {
                itemMessage.setEnabled(false);
                setPushNotice(Push.NOTICE_NEW_MESSGAE, open);
            } else {
                ToastUtil.showSimpleToast("正在设置中..");
            }
            break;
        case R.id.item_like:
            if(likeFail) {
                likeFail = false;
                return;
            }
            if(itemLike.isEnabled()) {
                itemLike.setEnabled(false);
                setPushNotice(Push.NOTICE_LIKE, open);
            } else {
                ToastUtil.showSimpleToast("正在设置中..");
            }
            break;
        case R.id.item_fans:
            if(fansFail) {
                fansFail = false;
                return;
            }
            if(itemFans.isEnabled()) {
                itemFans.setEnabled(false);
                setPushNotice(Push.NOTICE_FANS, open);
            } else {
                ToastUtil.showSimpleToast("正在设置中..");
            }
            break;
        case R.id.item_notice:
            if(noticeFail) {
                noticeFail = false;
                return;
            }
            if(itemNotice.isEnabled()) {
                itemNotice.setEnabled(false);
                setPushNotice(Push.NOTICE_NEW_SYSTEM_MESSAGE, open);
            } else {
                ToastUtil.showSimpleToast("正在设置中..");
            }
            break;
        }
    }
    
    private void setPushNotice(final int pushType, final boolean open) {
        PushManager.setPushNotice(pushType, open, new LoadListener() {
            
            @Override
            public void onSuccess() {
                if(getActivity() == null) {
                    return;
                }
                switch (pushType) {
                case Push.NOTICE_NEW_MESSGAE:
                    SPHelper.setPushNotice(getActivity(), Push.NOTICE_NEW_MESSGAE, open);
                    itemMessage.setEnabled(true);
                    break;
                case Push.NOTICE_LIKE:
                    SPHelper.setPushNotice(getActivity(), Push.NOTICE_LIKE, open);
                    itemLike.setEnabled(true);
                    break;
                case Push.NOTICE_FANS:
                    SPHelper.setPushNotice(getActivity(), Push.NOTICE_FANS, open);
                    itemFans.setEnabled(true);
                    break;
                case Push.NOTICE_NEW_SYSTEM_MESSAGE:
                    SPHelper.setPushNotice(getActivity(), Push.NOTICE_NEW_SYSTEM_MESSAGE, open);
                    itemNotice.setEnabled(true);
                    break;
                }
                ToastUtil.showSimpleToast("设置成功!");
            }
            
            @Override
            public void onFail(int code) {
                switch (pushType) {
                case Push.NOTICE_NEW_MESSGAE:
                    messageFail = true;
                    itemMessage.setToggleButton(!open);
                    itemMessage.setEnabled(true);
                    break;
                case Push.NOTICE_LIKE:
                    likeFail = true;
                    itemLike.setToggleButton(!open);
                    itemLike.setEnabled(true);
                    break;
                case Push.NOTICE_FANS:
                    fansFail = true;
                    itemFans.setToggleButton(!open);
                    itemFans.setEnabled(true);
                    break;
                case Push.NOTICE_NEW_SYSTEM_MESSAGE:
                    noticeFail = true;
                    itemNotice.setToggleButton(!open);
                    itemNotice.setEnabled(true);
                    break;
                }
                ToastUtil.showSimpleToast("设置失败!");
            }
        });
    }

}
