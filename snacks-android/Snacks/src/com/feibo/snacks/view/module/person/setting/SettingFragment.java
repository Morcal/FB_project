package com.feibo.snacks.view.module.person.setting;

import android.app.Activity;
import android.content.Intent;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.feibo.snacks.R;
import com.feibo.snacks.app.AppContext;
import com.feibo.snacks.manager.global.SettingManager;
import com.feibo.snacks.model.bean.User;
import com.feibo.snacks.manager.global.RedPointManager;
import com.feibo.snacks.manager.global.UserManager;
import com.feibo.snacks.manager.global.UpdateManager;
import com.feibo.snacks.view.base.BaseSwitchActivity;
import com.feibo.snacks.view.base.BaseTitleFragment;
import com.feibo.snacks.view.module.person.address.AddressFragment;
import com.feibo.snacks.view.module.person.login.LoginFragment;
import com.feibo.snacks.view.module.person.login.LoginGroup;
import com.feibo.snacks.view.module.person.setting.UpdateController.OnUpdateListener;
import com.feibo.snacks.view.util.LaunchUtil;
import com.feibo.snacks.view.util.RemindControl;
import com.feibo.snacks.view.util.RemindControl.OnRemindListener;
import com.feibo.snacks.view.widget.BaseItemLayout;
import com.feibo.snacks.view.dialog.RemindDialog.RemindSource;
import com.feibo.social.ResultListener;
import com.feibo.social.base.Platform;
import com.feibo.social.manager.SocialComponent;

import fbcore.log.LogUtil;

public class SettingFragment extends BaseTitleFragment implements OnClickListener {
    private static final String TAG = SettingFragment.class.getSimpleName();
    private static final int REQUEST_LOGIN_PERSON = 0x100;
    private static final int REQUEST_LOGIN_ADDRESS = 0x200;

    private BaseItemLayout personDataItem;
    private BaseItemLayout addressItem;
    private BaseItemLayout clearCacheItem;
    private BaseItemLayout checkVersionItem;
    private BaseItemLayout aboutSnacksItem;
    private Button logoutBtn;

    private RedPointManager redPointManager;
    private SettingManager settingManager;
    private UpdateManager updateManager;
    private UserManager userManager;

    public SettingFragment(){
    }

    @Override
    public int onCreateTitleBar() {
        return R.layout.layout_base_header;
    }

    @Override
    public View onCreateContentView() {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_setting, null);
        initWidget(view);
        initListener();
        return view;
    }

    @Override
    protected void initTitleBar(TitleBar titleBar) {
        ((TextView)titleBar.title).setText(R.string.setting);
        titleBar.leftPart.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                handleQuit();
            }
        });
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        redPointManager = RedPointManager.getInstance();
        settingManager = SettingManager.getInstance();
        updateManager = new UpdateManager();
        userManager = UserManager.getInstance();
        setFragmentName(getResources().getString(R.string.settingActivity));
    }

    @Override
    public void onResume() {
        super.onResume();
        initData();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // 登录成功

        if (requestCode == REQUEST_LOGIN_PERSON && resultCode == LoginGroup.RESULT_CODE_FOR_LOGINFRAGMENT) {
            handlePersonData();
        }

        if (requestCode == REQUEST_LOGIN_ADDRESS && resultCode == LoginGroup.RESULT_CODE_FOR_LOGINFRAGMENT) {
            handleAddress();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.setting_item_data:{
                LogUtil.i(TAG, "setting_item_data");
                handlePersonData();
                break;
            }
            case R.id.setting_item_address:{
                handleAddress();
                break;
            }
            case R.id.setting_item_clear: {
                handleClearCache();
                break;
            }
            case R.id.setting_item_check_version: {
                handleCheckVersion();
                break;
            }
            case R.id.setting_item_about: {
                handleAboutInfo();
                break;
            }
            case R.id.setting_exit_btn: {
                handleLogout();
                break;
            }
            default:
                break;
        }
    }

    private void initListener() {
        personDataItem.setOnClickListener(this);
        addressItem.setOnClickListener(this);
        clearCacheItem.setOnClickListener(this);
        checkVersionItem.setOnClickListener(this);
        aboutSnacksItem.setOnClickListener(this);
        logoutBtn.setOnClickListener(this);
    }

    private void initWidget(View view) {
        personDataItem = (BaseItemLayout) view.findViewById(R.id.setting_item_data);
        addressItem = (BaseItemLayout) view.findViewById(R.id.setting_item_address);
        clearCacheItem = (BaseItemLayout) view.findViewById(R.id.setting_item_clear);
        checkVersionItem = (BaseItemLayout) view.findViewById(R.id.setting_item_check_version);
        aboutSnacksItem = (BaseItemLayout) view.findViewById(R.id.setting_item_about);
        logoutBtn = (Button) view.findViewById(R.id.setting_exit_btn);
    }

    private void initData(){
        // 清除缓存
        settingManager.getDiskSize(new SettingManager.ResultListener() {
            @Override
            public void onResult(boolean success, String failMsg, Object result) {
                if(success && getActivity() != null){
                    clearCacheItem.setMessageHintTitle((String)result);
                }
            }
        });

        // 检查更新
        checkVersionItem.setMessageHintTitle(getResources().getString(R.string.check_new_version_default_hint)
                + updateManager.getVersionCode());

        // 退出登录
        if (userManager.isLogin()) {
            logoutBtn.setVisibility(View.VISIBLE);
        } else {
            logoutBtn.setVisibility(View.GONE);
        }
    }

    // 个人资料
    private void handlePersonData(){
        LogUtil.i(TAG, "handlePersonData");
        if (checkLogin(REQUEST_LOGIN_PERSON)) {
            LogUtil.i(TAG, "edit");
            LaunchUtil.launchActivity(getActivity(),
                    BaseSwitchActivity.class,
                    EditFragment.class,
                    null);
        }
    }

    // 地址管理
    private void handleAddress(){
        if(checkLogin(REQUEST_LOGIN_ADDRESS)){
            LaunchUtil.launchActivity(getActivity(),
                    BaseSwitchActivity.class,
                    AddressFragment.class,
                    null);
        }
    }

    // 清除缓存
    private void handleClearCache() {
        RemindSource remindSource = new RemindSource();
        remindSource.contentStr = getActivity().getString(R.string.dialog_clear_content);
        remindSource.confirm = getActivity().getString(R.string.str_confirm);
        remindSource.cancel = getActivity().getString(R.string.str_cancel);
        RemindControl.showRemindDialog(getActivity(), remindSource, new OnRemindListener() {
            @Override
            public void onConfirm() {
                settingManager.clearDiskCache();
                clearCacheItem.setEnabled(false);
                RemindControl.showSimpleToast(getActivity(), R.string.setting_item_clear_finish);
                clearCacheItem.setMessageHintTitle(getActivity().getString(R.string.clear_cache_finish_hint));
                clearCacheItem.setEnabled(true);
            }

            @Override
            public void onCancel() {

            }
        });
    }

    // 检查更新
    private void handleCheckVersion() {
        if (getActivity() == null || !AppContext.isNetworkAvailable()) {
            return;
        }
        UpdateController controller = new UpdateController();
        controller.checkUpdate(getActivity(), false, new OnUpdateListener() {
            @Override
            public void onStart() {
            }

            @Override
            public void onFinish() {
            }

            @Override
            public void onFail(String failMsg) {
                if (!TextUtils.isEmpty(failMsg) && getActivity() != null) {
                    RemindControl.showSimpleToast(getActivity(), failMsg);
                }
            }
        });
    }

    // 关于我们
    private void handleAboutInfo() {
        LaunchUtil.launchActivity(getActivity(),
                BaseSwitchActivity.class,
                AboutFragment.class,
                null);
    }

    // 退出登录
    private void handleLogout() {
        RemindSource source = new RemindSource();
        source.contentStr = getResources().getString(R.string.dialog_exit_content);
        source.confirm = getResources().getString(R.string.dialog_exit_cancle);
        source.cancel = getResources().getString(R.string.dialog_exit_confirm);
        RemindControl.showRemindDialog(getActivity(), source, new OnRemindListener() {
            @Override
            public void onConfirm() {
            }

            @Override
            public void onCancel() {
                exitAccount();
            }
        });
    }

    private void handleQuit(){
        if(getFragmentManager().getBackStackEntryCount() == 0) {
            getActivity().finish();
        }else{
            getFragmentManager().popBackStack();
        }
    }

    private void exitAccount() {
        int type = UserManager.getInstance().getUser().getPlatform();
        Platform platform = null;
        if (type == User.PLATFORM_TYPE_QQ) {
            platform = Platform.QQ;
        } else if (type == User.PLATFORM_TYPE_WX) {
            platform = Platform.WEIXIN;
        } else if (type == User.PLATFORM_TYPE_WB) {
            platform = Platform.SINA;
        } else {
            userManager.logout();
            redPointManager.resetRedPoint();
            getActivity().finish();
            return;
        }

        SocialComponent.create(platform, getActivity()).logout(new ResultListener() {
            @Override
            public void onResult(boolean isSuccess, String result) {
                if (isSuccess) {
                    userManager.logout();
                    redPointManager.resetRedPoint();
                    getActivity().finish();
                }
            }
        });
    }

    private boolean checkLogin(int requestCode) {
        if (!userManager.isLogin()) {
            LogUtil.i(TAG, "checkLogin false");
            LaunchUtil.launchActivityForResult(requestCode, getActivity(),
                    BaseSwitchActivity.class,
                    LoginFragment.class,
                    null);
            return false;
        }
        LogUtil.i(TAG, "checkLogin true");
        return true;
    }
}
