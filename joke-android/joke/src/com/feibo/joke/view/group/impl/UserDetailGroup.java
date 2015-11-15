package com.feibo.joke.view.group.impl;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.feibo.joke.R;
import com.feibo.joke.app.BundleUtil;
import com.feibo.joke.app.DataChangeEventCode;
import com.feibo.joke.manager.LoadListener;
import com.feibo.joke.manager.detail.UserDetailManager;
import com.feibo.joke.manager.work.UserManager;
import com.feibo.joke.model.User;
import com.feibo.joke.model.UserDetail;
import com.feibo.joke.model.Video;
import com.feibo.joke.utils.ShareUtil;
import com.feibo.joke.utils.StringUtil;
import com.feibo.joke.utils.ShareUtil.IShareListener;
import com.feibo.joke.utils.UIUtil;
import com.feibo.joke.video.VideoRecordActivity;
import com.feibo.joke.view.BaseActivity;
import com.feibo.joke.view.dialog.ShareDialog;
import com.feibo.joke.view.dialog.ShareDialog.OnShareClickListener;
import com.feibo.joke.view.group.BasePullWaterGroup;
import com.feibo.joke.view.group.LoadingHelper;
import com.feibo.joke.view.group.LoadingHelper.OnFaiClickListener;
import com.feibo.joke.view.module.mine.detail.AttentionFragment;
import com.feibo.joke.view.module.mine.detail.FansFragment;
import com.feibo.joke.view.module.mine.detail.VideoFavoriteFragment;
import com.feibo.joke.view.util.LaunchUtil;
import com.feibo.joke.view.util.ToastUtil;
import com.feibo.joke.view.widget.FocusStateView;
import com.feibo.joke.view.widget.FocusStateView.OnStatuChangeListener;
import com.feibo.joke.view.widget.FocusStateView.OnStatuClicklistener;
import com.feibo.joke.view.widget.TabDetailGroup;
import com.feibo.joke.view.widget.TabDetailGroup.IOnItemSelect;
import com.feibo.joke.view.widget.VImageView;
import com.feibo.social.base.Platform.Extra;

public class UserDetailGroup extends BasePullWaterGroup implements OnStatuClicklistener, OnStatuChangeListener {

    private boolean isFromMe;
    private long userId;

    private ImageView maxImg;
    private TabDetailGroup tabGroup;
    private VImageView avatar;
    private ImageView age;
    private TextView place, tvSignature;
//    private TextView signature;
    private TextView beLike;
    private FocusStateView btnFocus;
    private View btnShare;

    public User user;

    private Activity activity;

    private LoadingHelper loadingHelper;
    
    private int attentionFlag;
    
    private OnUserDetailGroupListener groupListener;
    
    public UserDetailGroup(Activity context, boolean isFromMe, long userId) {
        super(context, true);
        this.activity = context;
        this.isFromMe = isFromMe;
        this.userId = userId;
    }

    @Override
    public boolean getPreparedDataIfRefreshData() {
        final UserDetailManager userDetailManager = new UserDetailManager(userId);
        userDetailManager.getUserDetail(new LoadListener() {

            @Override
            public void onSuccess() {
                User user = userDetailManager.getUser();
                UserDetailGroup.this.user = user;
                UserDetailGroup.this.initUserDetail();
                if(UserManager.getInstance().isFromMe(user.id)) {
                    UserManager.getInstance().setBeLikeCount(user.detail.beLikeCount);
                }
            }

            @Override
            public void onFail(int code) {

            }
        });
        return true;
    }
    
    @Override
    public ViewGroup onAfterView() {
        LayoutInflater layoutInflater = LayoutInflater.from(getContext());
        ViewGroup group = (ViewGroup) layoutInflater.inflate(R.layout.header_userdetail, null);
        LinearLayout linearLayout = (LinearLayout) group.findViewById(R.id.layout_userdetail);

        if (loadingHelper == null) {
            loadingHelper = LoadingHelper.generateOnParent(linearLayout);
        }
        if (isFromMe) {
            loadingHelper.fail("快来跟世界拍个玩笑啊", "马上开拍", R.drawable.icon_no_gerenzhuye, null, new OnFaiClickListener() {

                @Override
                public void onFailClick(int failCode) {
                    activity.startActivityForResult(new Intent(activity, VideoRecordActivity.class),
                            BaseActivity.REQUEST_CODE);
                }
            });
        } else {
            loadingHelper.fail("什么都没有！空空的！", null, R.drawable.icon_no_duifanggerenzhuye, null, null);
        }

        initHeadView(group);
        initHeaderListener();
        initUserDetail();
        return group;
    }

    public void initHeadView(View head) {
    	tvSignature = (TextView) head.findViewById(R.id.user_signature);
        tabGroup = (TabDetailGroup) head.findViewById(R.id.tab_group);
        maxImg = (ImageView) head.findViewById(R.id.user_avatar_max);
        avatar = (VImageView) head.findViewById(R.id.user_avatar);
//        age = (ImageView) head.findViewById(R.id.user_age);
        place = (TextView) head.findViewById(R.id.user_place);
        beLike = (TextView) head.findViewById(R.id.user_like);
        btnShare = head.findViewById(R.id.iv_share);
        btnFocus = (FocusStateView) head.findViewById(R.id.btn_focus);
        btnFocus.setOnStatuClickListener(this);
        btnFocus.setOnStatuChangelistener(this);

        tabGroup.setFrom(isFromMe ? TabDetailGroup.FROM_MINE : TabDetailGroup.FROM_OTHER);
        btnFocus.setVisibility(isFromMe ? View.GONE : View.VISIBLE);
    }

    @SuppressLint("NewApi")
    public void initUserDetail() {
        if (user != null) {
            UIUtil.setBuldImage(getContext(), user.avatar, maxImg, R.color.user_detail_avatar_bg_blur_color);
            UIUtil.setVAvatar(user.avatar, user.isSensation(), avatar);

            String defaultSignature = getContext().getResources().getString(R.string.default_signature);
            tvSignature.setText(StringUtil.isEmpty(user.detail.signature) ? defaultSignature : user.detail.signature);
            
            btnFocus.setUser(user);
            // 设置标题
            String titleText = TextUtils.isEmpty(user.nickname) ? "" : user.nickname;
            if (groupListener != null) {
                groupListener.setFragmentTitle(titleText);
            }

            if (user.detail != null) {
                UserDetail detail = user.detail;
                if (!TextUtils.isEmpty(detail.province) || !TextUtils.isEmpty(detail.city)) {
                    place.setText(detail.province + "\t" + detail.city);
                }

                if (detail.beLikeCount > 0) {
                    beLike.setText("被爱过" + detail.beLikeCount + "次");
                }

                // 设置性别图片
                if(detail.gender != UserDetail.WOMAN && detail.gender != UserDetail.MAN) {
                    age.setVisibility(View.GONE);
                } else {
                    age.setVisibility(View.VISIBLE);
                    int gender = detail.gender == UserDetail.WOMAN ? R.drawable.icon_woman : (detail.gender == UserDetail.MAN ? R.drawable.icon_man : 0);
                    int ageColor = detail.gender == UserDetail.WOMAN ? R.drawable.bg_age_woman : (detail.gender == UserDetail.MAN ? R.drawable.bg_age_man : 0);
                    age.setImageResource(gender);
                    //setBackground存在兼容性问题
                    age.setBackgroundDrawable(getContext().getResources().getDrawable(ageColor));
                }

                // 设置tabGroup的数字
                initTabGroup();
            }
        }
    }

    private void initTabGroup() {
        if (user != null && user.detail != null) {
            UserDetail detail = user.detail;
            tabGroup.setAllNum(detail.worksCount, detail.likeCount, detail.friendsCount, detail.followersCount);
        }
    }

    @Override
    public void onDataChange(int code) {
        Bundle bundle = getFinishBundle();
        switch (code) {
        case DataChangeEventCode.CHANGE_TYPE_MODIFY_USER:
            UserDetailGroup.this.user = UserManager.getInstance().getUser();
            UserDetailGroup.this.initUserDetail();
        	break;
        case DataChangeEventCode.CHANGE_TYPE_VIDEO_DETAIL_CHANGE:
            if (bundle == null) {
                return;
            }
            Video video = (Video) bundle.getSerializable(BundleUtil.KEY_VIDEO);
            super.onDataChange(code);

            if(video == null) {
                user.detail.worksCount -= 1; 
            }
            setLikeAndAttentionCount(bundle);
            break;
        case DataChangeEventCode.CHANGE_TYPE_ATTENTION_COUNT_CHANGE:
            setLikeAndAttentionCount(bundle);
            break;
        case DataChangeEventCode.CHANGE_TYPE_LIKE_COUNT_CHANGE:
            setLikeAndAttentionCount(bundle);
            break;
        default:
            super.onDataChange(code);
            break;
        }
    }
    
    private void setLikeAndAttentionCount(Bundle bundle) {
        if (!isFromMe || bundle == null) {
            return;
        }
        int likeFlag = bundle.getInt(BundleUtil.KEY_LIKE_FLAG);
        int attentionCount = bundle.getInt(BundleUtil.KEY_ATTENTION_COUNT_CHANGE, 0);
        if(isFromMe) {
            if(likeFlag != 0 || attentionCount != 0) {
                if (user != null && user.detail != null) {
                    int likeCount = user.detail.likeCount;
                    user.detail.likeCount = likeCount + likeFlag;
                    user.detail.friendsCount = attentionCount + user.detail.friendsCount;
                }
            }
        }
        initTabGroup();
    }

    public void deleteVideoCount() {
        if (user != null && user.detail != null) {
            user.detail.worksCount -= 1;
            tabGroup.setAllNum(user.detail.worksCount, user.detail.likeCount, user.detail.friendsCount,
                    user.detail.followersCount);
        }
    }

    public void initHeaderListener() {
    	btnShare.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
                share();
			}
		});
        tabGroup.setOnItemClickListener(new IOnItemSelect() {
            @Override
            public void onItemClick(int pos) {
                switch (pos) {
                case 1:
                    LaunchUtil.launchSubActivity(getContext(), VideoFavoriteFragment.class, null);
                    break;
                case 2:
                    LaunchUtil.launchSubActivity(getContext(), AttentionFragment.class, AttentionFragment.buildBundle(userId));
                    break;
                case 3:
                    LaunchUtil.launchSubActivity(getContext(), FansFragment.class, AttentionFragment.buildBundle(userId));
                    break;
                }
            }
        });
    }

    public void share() {
        if (user == null) {
            ToastUtil.showSimpleToast("正在加载数据中..");
            return;
        }
        ShareDialog.show(getContext(), ShareDialog.ShareScene.SHARE_ONLY, new OnShareClickListener() {
            @Override
            public void onShare(Extra extra) {
                ShareUtil.onUserDetailShare(activity, isFromMe, user, extra, new IShareListener() {
                    
                    @Override
                    public void onFail(int code) {
                        if(groupListener != null) {
                            groupListener.onShareFail(code);
                        }
                    }
                });
            }

            @Override
            public void onDelete() {

            }

            @Override
            public void onReport() {

            }
        }, user.detail.shareUrl);
    }

    public void setOnUserDetailGroupListener(OnUserDetailGroupListener onUserDetailGroupListener) {
        this.groupListener = onUserDetailGroupListener;
    }

    public interface OnUserDetailGroupListener {
        public void setFragmentTitle(String titleText);
        public void onPreperadLogin();
        public void onShareFail(int code);
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
    
    @Override
    public void onPreperadLogin() {
        if(groupListener != null) {
            groupListener.onPreperadLogin();
        }
    }
    
    @Override
    public void onStatuChange(boolean isAttention) {
        attentionFlag = isAttention ? ++attentionFlag : --attentionFlag;
        Bundle bundle = BundleUtil.buildAttentionInUserDetailBundle(user.id, attentionFlag);
        setFinishBundle(bundle);
        setChangeType(DataChangeEventCode.CHANGE_TYPE_ATTENTION_IN_USERDETAIL);
    }

}
