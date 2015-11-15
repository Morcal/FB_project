package com.feibo.joke.view.module.mine.detail;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.feibo.joke.R;
import com.feibo.joke.app.BundleUtil;
import com.feibo.joke.app.DataChangeEventCode;
import com.feibo.joke.dao.ReturnCode;
import com.feibo.joke.manager.LoadListener;
import com.feibo.joke.manager.detail.UserDetailManager;
import com.feibo.joke.manager.list.VideosPublishManager;
import com.feibo.joke.manager.work.UserManager;
import com.feibo.joke.model.User;
import com.feibo.joke.model.UserDetail;
import com.feibo.joke.model.Video;
import com.feibo.joke.utils.ShareUtil;
import com.feibo.joke.utils.StringUtil;
import com.feibo.joke.utils.TimeUtil;
import com.feibo.joke.utils.UIUtil;
import com.feibo.joke.video.VideoRecordActivity;
import com.feibo.joke.view.BaseActivity;
import com.feibo.joke.view.dialog.ShareDialog;
import com.feibo.joke.view.group.VideoFlowGroup;
import com.feibo.joke.view.module.mine.BaseLoginFragment;
import com.feibo.joke.view.module.mine.edit.PersonEditFragment;
import com.feibo.joke.view.util.LaunchUtil;
import com.feibo.joke.view.util.ToastUtil;
import com.feibo.joke.view.widget.FocusStateView;
import com.feibo.joke.view.widget.TabDetailGroup;
import com.feibo.joke.view.widget.VImageView;
import com.feibo.social.base.Platform;

import java.util.List;

import fbcore.log.LogUtil;

/**
 * Created by lidiqing on 15-10-22.
 */
public class UserDetailFragment2 extends BaseLoginFragment {

    private static final String TAG = UserDetailFragment2.class.getSimpleName();

    private static final String BUNDLE_KEY_FROM = "from";
    private static final String BUNDLE_KEY_USERID = "userId";

    private static final int LOGIN_OPERATE = 0x2;

    private boolean isFromMe;
    private long userId;

    private VideoFlowGroup videoFlowGroup;
    private PageGroup pageGroup;
    private HeaderViewHolder headerViewHolder;

    private UserDetailManager userDetailManager;
    private VideosPublishManager videosPublishManager;

    private VideoFlowGroup.OperateData operateData;
    private boolean isLoading = false;

    TextView tvHeadRight;

    public static Bundle buildBundle(boolean isFromMe, long id) {
        Bundle args = new Bundle();
        args.putBoolean(BUNDLE_KEY_FROM, isFromMe);
        args.putLong(BUNDLE_KEY_USERID, id);
        return args;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getActivity().getIntent().getExtras();
        isFromMe = bundle.getBoolean(BUNDLE_KEY_FROM, true);
        userId = bundle.getLong(BUNDLE_KEY_USERID);

        userDetailManager = new UserDetailManager(userId);
        videosPublishManager = new VideosPublishManager(userId);
        videoFlowGroup = new VideoFlowGroup(getActivity());
    }

    @Override
    public View containChildView() {
        return View.inflate(getActivity(), R.layout.fragment_userdetail2, null);
    }

    @Override
    public int setTitleLayoutId() {
        return R.layout.base_titlebar;
    }

    @Override
    public void setTitlebar() {
        TitleBar titleBar = getTitleBar();
        tvHeadRight = titleBar.tvHeadRight;

        if (UserManager.getInstance().isFromMe(userId)) {
            tvHeadRight.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    LaunchUtil.launchSubActivity(UserDetailFragment2.this.getActivity(), PersonEditFragment.class, null);
                }
            });
            tvHeadRight.setText("编辑");
            tvHeadRight.setVisibility(View.INVISIBLE);
        } else {
            tvHeadRight.setVisibility(View.GONE);
        }

    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.list);

        pageGroup = new PageGroup(view);
        videoFlowGroup.setRecyclerView(recyclerView);
        videoFlowGroup.enableEnterDetail(false);
        videoFlowGroup.enableFocus(false);

        View headView = View.inflate(getActivity(), R.layout.header_userdetail, null);
        headerViewHolder = new HeaderViewHolder(getActivity(), headView, isFromMe);

        headerViewHolder.btnShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final User user = userDetailManager.getUser();
                if (user == null) {
                    ToastUtil.showSimpleToast("正在加载数据中..");
                    return;
                }

                ShareDialog.show(getActivity(), ShareDialog.ShareScene.SHARE_ONLY, new ShareDialog.OnShareClickListener() {
                    @Override
                    public void onShare(Platform.Extra extra) {
                        ShareUtil.onUserDetailShare(getActivity(), isFromMe, user, extra, null);
                    }

                    @Override
                    public void onDelete() {

                    }

                    @Override
                    public void onReport() {

                    }
                }, user.detail.shareUrl);
            }
        });

        headerViewHolder.tabGroup.setOnItemClickListener(new TabDetailGroup.IOnItemSelect() {
            @Override
            public void onItemClick(int pos) {
                final User user = userDetailManager.getUser();
                switch (pos) {
                    case 1:
                        LaunchUtil.launchSubActivity(getActivity(),
                                VideoFavoriteFragment.class,
                                null);
                        break;
                    case 2:
                        LaunchUtil.launchSubActivity(getActivity(),
                                AttentionFragment.class,
                                AttentionFragment.buildBundle(user.id));
                        break;
                    case 3:
                        LaunchUtil.launchSubActivity(getActivity(),
                                FansFragment.class,
                                AttentionFragment.buildBundle(user.id));
                        break;
                }
            }
        });

        videoFlowGroup.setOnOperateLoginListener(new VideoFlowGroup.OnOperateLoginListener() {
            @Override
            public void executeLogin(VideoFlowGroup.OperateData operateData) {
                UserDetailFragment2.this.operateData = operateData;
                loginClick(LOGIN_OPERATE);
            }
        });


        videoFlowGroup.setOnLoadMoreListener(new VideoFlowGroup.OnLoadMoreListener() {
            @Override
            public void loadMore() {
                onLoadMoreData();
            }
        });

        // 对视频播放单元操作结果的监听
        videoFlowGroup.setOperateResultListener(new VideoFlowGroup.OperateResultListener() {
            @Override
            public void result(int type, boolean success) {
                if (type == VideoFlowGroup.OperateResultListener.TYPE_DELETE && success) {
                    // 更新片儿数
                    userDetailManager.getUser().detail.worksCount -= 1;
                    headerViewHolder.initUserDetail(userDetailManager.getUser());

                    if (videoFlowGroup.isEmpty()) {
                        pageGroup.showEmptyPage();
                    }
                }
            }
        });

        pageGroup.showLoadPage();
        onRefreshData();
    }

    @Override
    public void onDataChange(int code) {
        LogUtil.i(TAG, "onDataChange " + code);
        videoFlowGroup.enablePlay(true);
        Bundle bundle = getFinishBundle();
        switch (code) {
            case DataChangeEventCode.CHANGE_TYPE_FRESH_PAGE:
                pageGroup.showLoadPage();
                onRefreshData();
                break;
            case DataChangeEventCode.CHANGE_TYPE_VIDEO_DETAIL_CHANGE:
                Video video = (Video) bundle.getSerializable(BundleUtil.KEY_VIDEO);
                int adapterPosition = bundle.getInt(BundleUtil.KEY_ADAPTER_POSITION);
                if (adapterPosition == -1) {
                    return;
                }
                // 删除视频
                if (video == null) {
                    pageGroup.showLoadPage();
                    onRefreshData();
                    return;
                }
                videoFlowGroup.notifyVideo(video, adapterPosition);
                break;
            case DataChangeEventCode.CHANGE_TYPE_ATTENTION_COUNT_CHANGE: // 关注变动
            case DataChangeEventCode.CHANGE_TYPE_LIKE_COUNT_CHANGE: // 爱过变动
                if (isFromMe) {
                    //　我的爱过和关注变动
                    headerViewHolder.setLikeAndAttentionCount(userDetailManager.getUser(), bundle);
                }
                break;
            case DataChangeEventCode.CHANGE_TYPE_VIDEO_PRODUCE_SUCESS:
                super.setChangeTypeAndFinish(code);
                break;
            case DataChangeEventCode.CHANGE_TYPE_MODIFY_USER:
                //　修改个人信息
                ((TextView) getTitleBar().title).setText(UserManager.getInstance().getUser().nickname);
//                headerViewHolder.initUserDetail(UserManager.getInstance().getUser());
                userDetailManager.setUser(UserManager.getInstance().getUser());
                pageGroup.showLoadPage();
                onRefreshData();
                break;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        videoFlowGroup.enablePlay(true);
    }

    @Override
    public void onPause() {
        super.onPause();
        if (videoFlowGroup != null) {
            videoFlowGroup.enablePlay(false);
            videoFlowGroup.stopVideo();
        }
    }

    @Override
    public void loginResult(boolean result, int operationCode) {
        videoFlowGroup.enablePlay(true);
        if (result) {
            switch (operationCode) {
                // 操作视频
                case LOGIN_OPERATE:
                    videoFlowGroup.operateVideo(operateData);
                    break;
            }
        }
    }

    private void onRefreshData() {
        if (isLoading) {
            return;
        }
        isLoading = true;
        userDetailManager.getUserDetail(new LoadListener() {
            @Override
            public void onSuccess() {
                if (getActivity() == null) {
                    isLoading = false;
                    return;
                }
                User user = userDetailManager.getUser();
                if (isFromMe) {
                    // 如果是我的主页, 更新到最新的用户detail
                    UserManager.getInstance().getUser().detail = user.detail;
                }
                headerViewHolder.initUserDetail(user);
                // 设置标题
                String titleText = TextUtils.isEmpty(user.nickname) ? "" : user.nickname;
                ((TextView) getTitleBar().title).setText(titleText);

                videosPublishManager.refresh(new LoadListener() {
                    @Override
                    public void onSuccess() {
                        isLoading = false;
                        if (getActivity() == null) {
                            return;
                        }
                        List<Video> videos = videosPublishManager.getDatas();
                        if (videos.size() == 0) {
                            pageGroup.showEmptyPage();
                        } else {
                            videoFlowGroup.setRefreshVideos(videos);
                            videoFlowGroup.addHeaderView(headerViewHolder.getHeaderView());
                            pageGroup.showListPage();
                        }
                    }

                    @Override
                    public void onFail(int code) {
                        isLoading = false;
                        if (getActivity() == null) {
                            return;
                        }
                        switch (code) {
                            case ReturnCode.NO_NET:
                                pageGroup.showErrorPage(getString(R.string.page_no_net));
                                break;
                            case ReturnCode.RS_EMPTY_ERROR:
                                pageGroup.showEmptyPage();
                                break;
                            default:
                                pageGroup.showErrorPage("未知错误, 错误码:" + code);
                                break;
                        }
                    }
                });
            }

            @Override
            public void onFail(int code) {
                isLoading = false;
                if (getActivity() == null) {
                    return;
                }
                switch (code) {
                    case ReturnCode.NO_NET:
                        pageGroup.showErrorPage(getString(R.string.page_no_net));
                        break;
                    case ReturnCode.RS_EMPTY_ERROR:
                        pageGroup.showEmptyPage();
                        break;
                    default:
                        pageGroup.showErrorPage("未知错误, 错误码:" + code);
                        break;
                }
            }
        });
    }

    private void onLoadMoreData() {
        if (isLoading) {
            return;
        }
        isLoading = true;

        videosPublishManager.loadMore(new LoadListener() {
            @Override
            public void onSuccess() {
                if (getActivity() == null) {
                    return;
                }
                videoFlowGroup.setLoadVideos(videosPublishManager.getLoadMoreDatas());
                isLoading = false;
            }

            @Override
            public void onFail(int code) {
                isLoading = false;
                if (getActivity() == null) {
                    return;
                }
                switch (code) {
                    case ReturnCode.NO_NET:
                        videoFlowGroup.showFooterText(getString(R.string.loading_default_fail_text));
                        break;
                    case ReturnCode.RS_EMPTY_ERROR:
                    default:
                        videoFlowGroup.showFooterText(getString(R.string.load_more_over_text_default));
                        break;
                }
            }
        });
    }

    // 页面切换, 如加载页, 错误页
    private class PageGroup {
        private ViewGroup infoBoard;
        private ViewGroup detailBoard;
        private View listBoard;

        public PageGroup(View view) {
            infoBoard = (ViewGroup) view.findViewById(R.id.board_info);
            detailBoard = (ViewGroup) view.findViewById(R.id.board_detail);
            listBoard = view.findViewById(R.id.list);
        }

        private void showLoadPage() {
            infoBoard.removeAllViews();
            View view = View.inflate(getActivity(), R.layout.page_load, null);
            infoBoard.addView(view);
            infoBoard.setVisibility(View.VISIBLE);
            listBoard.setVisibility(View.GONE);
            detailBoard.setVisibility(View.GONE);
        }

        private void showErrorPage(String error) {
            infoBoard.removeAllViews();
            View view = View.inflate(getActivity(), R.layout.page_load_error, null);
            ((TextView) view.findViewById(R.id.text)).setText(error);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onRefreshData();
                }
            });
            infoBoard.addView(view);
            infoBoard.setVisibility(View.VISIBLE);
            listBoard.setVisibility(View.GONE);
            detailBoard.setVisibility(View.GONE);
        }

        private void showEmptyPage() {
            FrameLayout headBox = (FrameLayout) detailBoard.findViewById(R.id.head_detail);
            FrameLayout listBox = (FrameLayout) detailBoard.findViewById(R.id.head_list);

            headBox.removeAllViews();
            listBox.removeAllViews();

            View header = headerViewHolder.getHeaderView();
            if (header.getParent() != null) {
                ((ViewGroup) header.getParent()).removeAllViews();
            }
            header.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            headBox.addView(header);

            if (isFromMe) {
                View empty = View.inflate(getActivity(), R.layout.page_no_video_me, null);
                empty.findViewById(R.id.btn).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // 进入视频拍摄页
                        getActivity().startActivityForResult(new Intent(getActivity(), VideoRecordActivity.class),
                                BaseActivity.REQUEST_CODE);
                    }
                });
                empty.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
                listBox.addView(empty);
            } else {
                View empty = View.inflate(getActivity(), R.layout.page_no_video_other, null);
                empty.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
                listBox.addView(empty);
            }

            listBoard.setVisibility(View.GONE);
            detailBoard.setVisibility(View.VISIBLE);
            infoBoard.setVisibility(View.GONE);

            if (UserManager.getInstance().isFromMe(userId)) {
                tvHeadRight.setVisibility(View.VISIBLE);
            }
        }

        private void showListPage() {
            infoBoard.setVisibility(View.GONE);
            listBoard.setVisibility(View.VISIBLE);
            detailBoard.setVisibility(View.GONE);

            if (UserManager.getInstance().isFromMe(userId)) {
                tvHeadRight.setVisibility(View.VISIBLE);
            }
        }
    }

    // 视频流的header, 用来保存用户信息详情
    private class HeaderViewHolder {
        private View headerView;
        private Context context;

        private ImageView maxImg;
        private TabDetailGroup tabGroup;
        private VImageView avatar;
        private TextView age;
        private TextView place, tvSignature;
        private TextView beLike;
        private FocusStateView btnFocus;
        private View btnShare;

        public HeaderViewHolder(Context context, View head, boolean isFromMe) {
            this.context = context;
            headerView = head;
            tvSignature = (TextView) head.findViewById(R.id.user_signature);
            tabGroup = (TabDetailGroup) head.findViewById(R.id.tab_group);
            maxImg = (ImageView) head.findViewById(R.id.user_avatar_max);
            avatar = (VImageView) head.findViewById(R.id.user_avatar);
            age = (TextView) head.findViewById(R.id.tx_age);
            place = (TextView) head.findViewById(R.id.user_place);
            beLike = (TextView) head.findViewById(R.id.user_like);
            btnShare = head.findViewById(R.id.iv_share);
            btnFocus = (FocusStateView) head.findViewById(R.id.btn_focus);

            tabGroup.setFrom(isFromMe ? TabDetailGroup.FROM_MINE : TabDetailGroup.FROM_OTHER);
            btnFocus.setVisibility(isFromMe ? View.GONE : View.VISIBLE);
        }

        public void initUserDetail(final User user) {
            if (user == null) {
                return;
            }
            UIUtil.setBuldImage(context, user.avatar, maxImg, R.color.user_detail_avatar_bg_blur_color);
            UIUtil.setVAvatar(user.avatar, user.isSensation(), avatar);

            String defaultSignature = context.getResources().getString(R.string.default_signature);
            tvSignature.setText(StringUtil.isEmpty(user.detail.signature) ? defaultSignature : user.detail.signature);
            btnFocus.setUser(user);

            if (user.detail != null) {
                UserDetail detail = user.detail;
                if (!TextUtils.isEmpty(detail.province) || !TextUtils.isEmpty(detail.city)) {
                    place.setText(detail.province + "\t" + detail.city);
                } else {
                    place.setText("");
                }

                if (detail.beLikeCount >= 0) {
                    beLike.setText("被爱过" + detail.beLikeCount + "次");
                }

                // 设置性别图片
                if (detail.gender != UserDetail.WOMAN && detail.gender != UserDetail.MAN) {
                    age.setVisibility(View.GONE);
                } else {
                    int gender = detail.gender == UserDetail.WOMAN ? R.drawable.icon_woman : R.drawable.icon_man;
                    int ageColor = detail.gender == UserDetail.WOMAN ? R.drawable.bg_sex_woman1 : R.drawable.bg_sex_man1;

                    age.setVisibility(View.VISIBLE);
                    age.setText(TimeUtil.getAge(detail.birth) + "");
                    age.setBackgroundResource(ageColor);

                    Drawable d = UserDetailFragment2.this.getResources().getDrawable(gender);
                    d.setBounds(0, 0, d.getMinimumWidth(), d.getMinimumHeight());
                    age.setCompoundDrawables(d, null, null, null);
//                        ViewCompat.setBackground(age, context.getResources().getDrawable(ageColor));
                }

                // 设置tabGroup的数字
                tabGroup.setAllNum(detail.worksCount, detail.likeCount, detail.friendsCount, detail.followersCount);
            }
        }

        public void setLikeAndAttentionCount(User user, Bundle bundle) {
            int likeFlag = bundle.getInt(BundleUtil.KEY_LIKE_FLAG);
            int attentionCount = bundle.getInt(BundleUtil.KEY_ATTENTION_COUNT_CHANGE, 0);
            if (likeFlag != 0 || attentionCount != 0) {
                if (user != null && user.detail != null) {
                    int likeCount = user.detail.likeCount;
                    user.detail.likeCount = likeCount + likeFlag;
                    user.detail.friendsCount = attentionCount + user.detail.friendsCount;
                }
            }
            tabGroup.setAllNum(user.detail.worksCount,
                    user.detail.likeCount,
                    user.detail.friendsCount,
                    user.detail.followersCount);
        }

        public View getHeaderView() {
            return headerView;
        }
    }

    // ----------------------废弃或者无用的方法------------------------
    @Override
    public void onReleaseView() {

    }
}
