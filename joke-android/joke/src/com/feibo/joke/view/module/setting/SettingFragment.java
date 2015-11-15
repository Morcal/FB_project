package com.feibo.joke.view.module.setting;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.feibo.joke.R;
import com.feibo.joke.app.AppContext;
import com.feibo.joke.app.Constant;
import com.feibo.joke.app.DataChangeEventCode;
import com.feibo.joke.cache.DataProvider;
import com.feibo.joke.manager.work.UserManager;
import com.feibo.joke.utils.EncryptUtil;
import com.feibo.joke.utils.SPHelper;
import com.feibo.joke.view.dialog.RemindDialog;
import com.feibo.joke.view.dialog.RemindDialog.OnDialogClickListener;
import com.feibo.joke.view.dialog.RemindDialog.RemindSource;
import com.feibo.joke.view.dialog.SimpleResoure;
import com.feibo.joke.view.dialog.SimpleTextDialog;
import com.feibo.joke.view.module.mine.BaseLoginFragment;
import com.feibo.joke.view.module.setting.UpdateController.OnUpdateListener;
import com.feibo.joke.view.util.LaunchUtil;
import com.feibo.joke.view.util.MessageHintManager;
import com.feibo.joke.view.util.ToastUtil;
import com.feibo.joke.view.widget.BaseItemLayout;
import com.feibo.joke.view.widget.BaseItemLayout.OnToggleListener;

public class SettingFragment extends BaseLoginFragment implements OnClickListener, OnToggleListener {

    private BaseItemLayout itemMessage;
    private BaseItemLayout itemSaveVideo;
    private BaseItemLayout itemWifi;
    private BaseItemLayout itemFeedback;
    private BaseItemLayout itemVersion;
    private BaseItemLayout itemCache;
    private BaseItemLayout itemProtocol;

    private Button loginBtn;

    private int clickCountForInfo = 0;

    @Override
    public View containChildView() {
        View view = View.inflate(getActivity(), R.layout.fragment_setting, null);
        initWidget(view);
        initListener();
        initView();
        return view;
    }

    @Override
    public void setTitlebar() {
        ((TextView) getTitleBar().title).setText("设置");
        getTitleBar().rightPart.setVisibility(View.INVISIBLE);
        getTitleBar().title.setOnClickListener(this);
    }

    private void initView() {
        itemWifi.initToggleButton(SPHelper.getPlayVideoOnWifi(getActivity()));
        itemSaveVideo.initToggleButton(SPHelper.getSaveVideoLocal(getActivity()));

        setLoginbtn(UserManager.getInstance().isLogin());
        itemCache.setMessageHintTitle(false, "正在计算缓存中");
        postOnUiHandle(new Runnable() {
            @Override
            public void run() {
                String size = DataProvider.getInstance().getDiskSize();
                if (getActivity() == null) {
                    return;
                }
                itemCache.setMessageHintTitle(false, size);
            }
        });
    }

    private void initListener() {
        itemMessage.setOnBaseItemClickListener(this);
        itemFeedback.setOnBaseItemClickListener(this);
        itemProtocol.setOnBaseItemClickListener(this);

        itemSaveVideo.setOnClickListener(this);
        itemWifi.setOnClickListener(this);
        itemVersion.setOnClickListener(this);
        itemCache.setOnClickListener(this);

        loginBtn.setOnClickListener(this);

        itemSaveVideo.setOnToggleStateChangeListener(this, itemSaveVideo.getId());
        itemWifi.setOnToggleStateChangeListener(this, itemWifi.getId());
    }

    private void initWidget(View view) {
        itemMessage = (BaseItemLayout) view.findViewById(R.id.item_message);
        itemSaveVideo = (BaseItemLayout) view.findViewById(R.id.item_save_video);
        itemWifi = (BaseItemLayout) view.findViewById(R.id.item_wifi);
        itemFeedback = (BaseItemLayout) view.findViewById(R.id.item_feedback);
        itemVersion = (BaseItemLayout) view.findViewById(R.id.item_version);
        itemCache = (BaseItemLayout) view.findViewById(R.id.item_clearCache);
        itemProtocol = (BaseItemLayout) view.findViewById(R.id.item_protocol);
        loginBtn = (Button) view.findViewById(R.id.btn_login);
    }

    @Override
    public int setTitleLayoutId() {
        return R.layout.base_titlebar;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        case R.id.item_message:
            LaunchUtil.launchSubActivity(getActivity(), SettingMessageFragment.class, null);
            break;
        case R.id.item_feedback:
            LaunchUtil.launchSubActivity(getActivity(), FeedbackFragment.class, null);
            break;
        case R.id.item_version:
            checkVersion();
            break;
        case R.id.item_protocol:
            LaunchUtil.launchSubActivity(getActivity(), ProtocolFragment.class, ProtocolFragment.buildBundle(Constant.URL_USER_PROTOCOL, true));
            break;
        case R.id.item_clearCache:
            clearCache();
            break;
        case R.id.btn_login:
            loginButtonClick((Button)v);
            break;
        case R.id.head_title:
            clickTitle();
            break;
        }
    }

    private void clickTitle() {
        clickCountForInfo++;
        if (clickCountForInfo >= 5) {
            clickCountForInfo = 0;
            try {
                ApplicationInfo appInfo = AppContext.getContext().getPackageManager()
                        .getApplicationInfo(AppContext.getContext().getPackageName(), PackageManager.GET_META_DATA);
                String msg = "version : " + AppContext.APP_VERSION_NAME + "(" + AppContext.APP_VERSION_CODE + ")\n"
                        + "channel : " + appInfo.metaData.getString("UMENG_CHANNEL") + "\n" + "debuggable : "
                        + Constant.DEBUG + "\n"
                        + "signature : " + (EncryptUtil.checkReleaseSignature() ? "release" : "debug") + "\n"
                        + "server : " + AppContext.SERVER_HOST + "\n" + "getuiId : " + AppContext.DEVICE_ID;
                RemindDialog.show(getActivity(), new RemindSource(msg, "确定", ""), true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void loginButtonClick(final Button btn) {
        if (UserManager.getInstance().isLogin()) {
            RemindDialog d = RemindDialog.show(getActivity(), new RemindSource("确定退出登录吗"), true);
            d.setOnDialogClickListener(new OnDialogClickListener() {
                @Override
                public void onConfirm() {
                    loginOut(UserManager.getInstance().getUser().platform, new OnLoginOutListener() {

                        @Override
                        public void result(boolean loginOutSuccess) {
                            btn.setEnabled(true);
                            setLoginbtn(!loginOutSuccess);
                        }

                        @Override
                        public void isLogining() {
                            btn.setEnabled(false);
                            btn.setText("正在退出登录...");
                        }
                    });
                }

                @Override
                public void onCancel() {

                }
            });
        } else {
            loginClick();
        }
    }

    private void checkVersion() {
        if (getActivity() == null) {
            return;
        }
        itemVersion.setEnabled(false);
        if (!AppContext.hasAvailableNetwork()) {
            SimpleTextDialog.show(getActivity(), new SimpleResoure("无网络...请稍后再试", R.drawable.icon_no_network), true);
            itemVersion.setEnabled(true);
            return;
        }

        UpdateController controller = new UpdateController();
        controller.checkUpdate(getActivity(), false, new OnUpdateListener() {
            @Override
            public void onStart() {
            }

            @Override
            public void onFinish() {
                itemVersion.setEnabled(true);
            }

            @Override
            public void onFail(String failMsg) {
                if (!TextUtils.isEmpty(failMsg) && getActivity() != null) {
                    ToastUtil.showSimpleToast(getActivity(), failMsg);
                }
                itemVersion.setEnabled(true);
            }
        });
    }

    private void clearCache() {
        postOnUiHandle(new Runnable() {
            @Override
            public void run() {
                itemCache.setEnabled(false);
                DataProvider.getInstance().clearAllCache();
                if (getActivity() == null) {
                    return;
                }
                itemCache.setMessageHintTitle(false, "0.0 MB");
                itemCache.setEnabled(true);
                SimpleTextDialog.show(getActivity(), new SimpleResoure("清除成功", R.drawable.icon_clear_success), true);
            }
        });
    }

    public void setLoginbtn(boolean isLogin) {
        if (isLogin) {
            loginBtn.setBackgroundResource(R.drawable.bg_grey_dark);
            loginBtn.setText(getActivity().getResources().getString(R.string.setting_login_out));
        } else {
            loginBtn.setBackgroundResource(R.drawable.bg_orange);
            loginBtn.setText(getActivity().getResources().getString(R.string.setting_login_now));
        }
    }

    @Override
    public void loginResult(boolean result, int operationCode) {
        if (result) {
            setLoginbtn(result);
        } else {
            ToastUtil.showSimpleToast("退出登录成功");
            setLoginbtn(false);
        }
    }

    @Override
    public void onStateChange(int viewId, boolean open) {
        StringBuilder sb = new StringBuilder();
        switch (viewId) {
        case R.id.item_wifi:
            SPHelper.setPlayVideoOnWifi(getActivity(), open);
            sb.append(open ? "设置" : "取消").append(getResources().getString(R.string.setting_wifi_play)).append("成功");
            ToastUtil.showSimpleToast(sb.toString());
            break;
        case R.id.item_save_video:
            SPHelper.setSaveVideoLocal(getActivity(), open);
            sb.append(open ? "设置" : "取消").append(getResources().getString(R.string.setting_save_video_to_local)).append("成功");
            ToastUtil.showSimpleToast(sb.toString());
            break;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        initRedhint();
    }

    private void initRedhint() {
        MessageHintManager.initSetting(getActivity(), itemVersion, AppContext.APP_VERSION_NAME);
    }

    @Override
    public void onDataChange(int code) {
        if(code == DataChangeEventCode.CODE_EVENT_BUS_REDHINT) {
            initRedhint();
        }
    }

    @Override
    public void onReleaseView() {

    }

}
