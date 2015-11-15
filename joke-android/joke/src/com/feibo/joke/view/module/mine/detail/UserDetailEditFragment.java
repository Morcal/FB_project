package com.feibo.joke.view.module.mine.detail;

import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.feibo.joke.R;
import com.feibo.joke.manager.work.UserManager;
import com.feibo.joke.model.User;
import com.feibo.joke.utils.UIUtil;
import com.feibo.joke.view.BaseTitleFragment;
import com.feibo.joke.view.widget.BaseItemLayout;
import com.feibo.joke.view.widget.VImageView;

public class UserDetailEditFragment extends BaseTitleFragment implements OnClickListener {

    private View root;
    private User user;

    private View headImgItem;
    private VImageView headImg;
    private BaseItemLayout headBIL;
    private BaseItemLayout nicknameItem;
    private BaseItemLayout genderItem;
    private BaseItemLayout areaItem;
    private BaseItemLayout birthItem;
    private BaseItemLayout signatureItem;

    @Override
    public View containChildView() {
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        root = inflater.inflate(R.layout.userdetail_edit, null);

        initView();
        initData();
        initlistener();
        return root;
    }

    private void initData() {
        user = UserManager.getInstance().getUser();

        UIUtil.setImage(user.avatar, headImg.getImageView(), R.drawable.default_avatar, R.drawable.default_avatar);
        headBIL.setMessageHintTitle(false, null);
        nicknameItem.setMessageHintTitle(false, user.nickname);
        //暂无数据
//        genderItem.setMessageHintTitle(false, user.getDetail().gender+"");
//        areaItem.setMessageHintTitle(false, user.getDetail().province+"\t"+user.getDetail().city);
//        birthItem.setMessageHintTitle(false, user.getDetail().birth);
//        signatureItem.setMessageHintTitle(false, user.getDetail().signature);
        genderItem.setMessageHintTitle(false, "女女女女女");
        areaItem.setMessageHintTitle(false, "China");
        birthItem.setMessageHintTitle(false, "09090090090");
        signatureItem.setMessageHintTitle(false, "aaaaaaaaa");
    }

    private void initlistener() {
        headImgItem.setOnClickListener(this);
        nicknameItem.setOnClickListener(this);
        genderItem.setOnClickListener(this);
        areaItem.setOnClickListener(this);
        birthItem.setOnClickListener(this);
        signatureItem.setOnClickListener(this);
    }

    private void initView() {
        headImgItem = root.findViewById(R.id.item_head_img);
        headBIL = (BaseItemLayout) root.findViewById(R.id.bil_head);
        headImg = (VImageView) root.findViewById(R.id.iv_auother_head);

        nicknameItem = (BaseItemLayout) root.findViewById(R.id.item_nickname);
        genderItem = (BaseItemLayout) root.findViewById(R.id.item_gender);
        areaItem = (BaseItemLayout) root.findViewById(R.id.item_area);
        birthItem = (BaseItemLayout) root.findViewById(R.id.item_birth);
        signatureItem = (BaseItemLayout) root.findViewById(R.id.item_signature);
    }

    @Override
    public void setTitlebar() {
        TitleBar titleBar = getTitleBar();
        ((TextView) titleBar.title).setText("个人资料");
        titleBar.tvHeadRight.setText(R.string.save);
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
        // switch (v.getId()) {
        // case R.id.login_layout:
        // if (UserManager.getInstance().isLogin()) {
        // // 开启用户详情页
        // LaunchUtil.launchSubActivity(getActivity(),
        // BaseUserDetailFragment.class,
        // BaseUserDetailFragment.setBundle(false,
        // UserManager.getInstance().getUser().getId()));
        // } else {
        // LoginDialog.show(getActivity());
        // }
        // break;
        // case R.id.item_message:
        // LaunchUtil.launchSubActivity(getActivity(), MessageFragment.class,
        // null);
        // break;
        // case R.id.item_find_friend:
        // LaunchUtil.launchActivity(getActivity(), BaseSwitchActivity.class,
        // FindFriendFragment.class, null);
        // break;
        // case R.id.item_draft_box:
        // LaunchUtil.launchSubActivity(getActivity(), DraftFragment.class,
        // null);
        // break;
        // case R.id.item_setting:
        // LaunchUtil.launchActivity(getActivity(), BaseSwitchActivity.class,
        // SettingFragment.class, null);
        // break;
        // }
    }

}
