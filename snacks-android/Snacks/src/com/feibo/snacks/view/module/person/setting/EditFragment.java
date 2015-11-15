package com.feibo.snacks.view.module.person.setting;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.feibo.snacks.R;
import com.feibo.snacks.manager.global.UserManager;
import com.feibo.snacks.view.base.BaseSwitchActivity;
import com.feibo.snacks.view.base.BaseTitleFragment;
import com.feibo.snacks.view.module.person.ImageSelectFragment;
import com.feibo.snacks.view.util.LaunchUtil;
import com.feibo.snacks.view.util.RemindControl;
import com.feibo.snacks.view.util.UIUtil;
import com.feibo.snacks.view.widget.CircleImageView;

import java.io.File;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import fbcore.log.LogUtil;

/**
 * Created by lidiqing on 15-9-6.
 */
public class EditFragment extends BaseTitleFragment {
    private static final String TAG = EditFragment.class.getSimpleName();
    private static final int REQUEST_IMAGE = 0x100;

    @Bind(R.id.nickname)
    TextView nickNameView;

    @Bind(R.id.avatar)
    CircleImageView avatarView;

    TitleViewHolder titleHolder;

    UserManager userManager;

    @Override
    public int onCreateTitleBar() {
        return R.layout.layout_base_header;
    }

    @Override
    public View onCreateContentView() {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_edit_data, null);
        ButterKnife.bind(this, view);

        nickNameView.setText(userManager.getUser().getNickname());
        UIUtil.setImage(getActivity(),
                userManager.getUser().getAvatar(),
                R.drawable.default_photo,
                R.drawable.default_photo,
                avatarView);
        return view;
    }

    @Override
    protected void initTitleBar(TitleBar titleBar) {
        titleHolder = new TitleViewHolder(titleBar.headView);
        titleHolder.titleText.setText(R.string.title_edit_data);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        userManager = UserManager.getInstance();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        titleHolder.onDestroy();
        ButterKnife.unbind(this);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_IMAGE && data != null) {
            LogUtil.i(TAG, "onActivityResult success");
            handleAvatarUpload(data.getData());
        }
    }

    // 用户头像修改
    @OnClick(R.id.btn_avatar)
    public void handleAvatar() {
        LaunchUtil.launchActivityForResult(REQUEST_IMAGE,
                getActivity(),
                BaseSwitchActivity.class,
                ImageSelectFragment.class,
                null);
    }

    // 用户昵称修改
    @OnClick(R.id.btn_nickname)
    public void handleNickname() {
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment, new EditNicknameFragment());
        transaction.addToBackStack(null);
        transaction.commit();
    }

    // 上传头像
    public void handleAvatarUpload(final Uri uri) {
        LogUtil.i(TAG, "handleAvatarUpload:" + uri.getPath());
        RemindControl.showProgressDialog(getActivity(), "正在上传头像");
        userManager.uploadAvatar(new File(uri.getPath()), new UserManager.UploadAvatarListener() {
            @Override
            public void onSuccess(String url) {
                RemindControl.hideProgressDialog();
                RemindControl.showSimpleToast("头像上传成功");
                avatarView.setImageURI(uri);
            }

            @Override
            public void onFail(String msg) {
                RemindControl.hideProgressDialog();
                RemindControl.showSimpleToast(msg);
            }
        });
    }

    public void handleQuit() {
        if (getFragmentManager().getBackStackEntryCount() == 0) {
            getActivity().finish();
        } else {
            getFragmentManager().popBackStack();
        }
    }

    class TitleViewHolder {

        @Bind(R.id.head_title)
        TextView titleText;

        public TitleViewHolder(View view) {
            ButterKnife.bind(TitleViewHolder.this, view);
        }

        // 退出
        @OnClick(R.id.head_left)
        public void clickHeadLeft() {
            handleQuit();
        }

        public void onDestroy() {
            ButterKnife.unbind(TitleViewHolder.this);
        }
    }
}
