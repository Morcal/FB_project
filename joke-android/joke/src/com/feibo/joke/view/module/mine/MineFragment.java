package com.feibo.joke.view.module.mine;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.feibo.joke.R;
import com.feibo.joke.app.DataChangeEventCode;
import com.feibo.joke.manager.work.UserManager;
import com.feibo.joke.model.User;
import com.feibo.joke.utils.UIUtil;
import com.feibo.joke.view.module.mine.detail.UserDetailFragment2;
import com.feibo.joke.view.module.setting.SettingFragment;
import com.feibo.joke.view.util.LaunchUtil;
import com.feibo.joke.view.util.MessageHintManager;
import com.feibo.joke.view.widget.BaseItemLayout;
import com.feibo.joke.view.widget.VImageView;

public class MineFragment extends BaseLoginFragment implements OnClickListener {

    private View root;
    private View loginLayout;

    private View loginBoard;
    private View unloginBoard;

    private VImageView userAvatar;
    private TextView userName;
    private TextView beLike;

    private BaseItemLayout messageItem;
    private BaseItemLayout friendItem;
    private BaseItemLayout draftItem;
    private BaseItemLayout settingItem;

    @Override
    public View containChildView() {
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        root = inflater.inflate(R.layout.fragment_mine, null);

        initView();
        initlistener();
        return root;
    }

    private void initLoginState() {
        if (UserManager.getInstance().isLogin()) {
            onLogin();
        } else {
            unLogin();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        setBeLikeCount();
        initRedhint();
        initLoginState();
    }

    private void unLogin() {
        loginBoard.setVisibility(View.GONE);
        unloginBoard.setVisibility(View.VISIBLE);
        userAvatar.getImageView().setImageResource(R.drawable.default_avatar);
        userAvatar.showSensation(false);
    }

    private void onLogin() {
        loginBoard.setVisibility(View.VISIBLE);
        unloginBoard.setVisibility(View.GONE);
        initUserView();
    }

    private void initUserView() {
        // 填充用户头像等数据
        User user = UserManager.getInstance().getUser();
        UIUtil.setVAvatar(user.avatar, user.isSensation(), userAvatar);

        userName.setText(user.nickname);
        setBeLikeCount();
    }

    private void setBeLikeCount() {
        if (UserManager.getInstance().isLogin()) {
            User user = UserManager.getInstance().getUser();
            if (user.detail != null) {
                beLike.setText("被爱过" + user.detail.beLikeCount + "次");
            }
        }
    }

    private void initlistener() {
        loginLayout.setOnClickListener(this);

        messageItem.setOnBaseItemClickListener(this);
        friendItem.setOnBaseItemClickListener(this);
        draftItem.setOnBaseItemClickListener(this);
        settingItem.setOnBaseItemClickListener(this);
    }

    private void initView() {
        loginLayout = root.findViewById(R.id.login_layout);
        loginBoard = root.findViewById(R.id.board_login);
        unloginBoard = root.findViewById(R.id.board_unlogin);
        userAvatar = (VImageView) root.findViewById(R.id.user_avatar);
        userName = (TextView) root.findViewById(R.id.user_nickname);
        beLike = (TextView) root.findViewById(R.id.user_like);

        messageItem = (BaseItemLayout) root.findViewById(R.id.item_message);
        friendItem = (BaseItemLayout) root.findViewById(R.id.item_find_friend);
        draftItem = (BaseItemLayout) root.findViewById(R.id.item_draft_box);
        settingItem = (BaseItemLayout) root.findViewById(R.id.item_setting);

        draftItem.setMessageHintTitle(false, "");
    }

    @Override
    public void setTitlebar() {
        TitleBar titleBar = getTitleBar();
        titleBar.leftPart.setVisibility(View.GONE);
        titleBar.rightPart.setVisibility(View.GONE);
        TextView title = (TextView) titleBar.title;
        title.setText(R.string.mine);
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
            case R.id.login_layout:
                loginLayoutClick();
                break;
            case R.id.item_message:
                if (UserManager.getInstance().isLogin()) {
                    LaunchUtil.launchSubActivity(getActivity(), MessageFragment.class, true, null);
                } else {
                    loginClick();
                }
                break;
            case R.id.item_find_friend:
                if (UserManager.getInstance().isLogin()) {
                    LaunchUtil.launchSubActivity(getActivity(), FriendFindFragment.class, true, null);
                } else {
                    loginClick();
                }
                break;
            case R.id.item_draft_box:
                LaunchUtil.launchSubActivity(getActivity(), VideoDraftFragment.class, null);
                break;
            case R.id.item_setting:
                LaunchUtil.launchSubActivity(getActivity(), SettingFragment.class, true, null);
                break;
        }
    }

    private void loginLayoutClick() {
        if (UserManager.getInstance().isLogin()) {
            // 开启用户详情页
//            LaunchUtil.launchSubActivity(getActivity(), UserDetailFragment.class,
//                    UserDetailFragment.buildBundle(true, UserManager.getInstance().getUser().id));
            LaunchUtil.launchSubActivity(getActivity(), UserDetailFragment2.class,
                    UserDetailFragment2.buildBundle(true, UserManager.getInstance().getUser().id));

        } else {
            loginClick();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void loginResult(boolean result, int operationCode) {
        if (result) {
            initLoginState();
        }
    }

    private void initRedhint() {
        MessageHintManager.initMine(getActivity(), messageItem, friendItem, draftItem, settingItem);
    }

    @Override
    public void onDataChange(int code) {
        switch (code) {
            case DataChangeEventCode.CODE_EVENT_BUS_REDHINT:
                initRedhint();
                break;
            case DataChangeEventCode.CHANGE_TYPE_VIDEO_PRODUCE_SUCESS:
                setChangeTypeAndFinish(code);
                break;
            default:
                break;
        }
    }

}
