package com.feibo.joke.view.module.home;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.feibo.joke.R;
import com.feibo.joke.app.BundleUtil;
import com.feibo.joke.app.DataChangeEventCode;
import com.feibo.joke.dao.ReturnCode;
import com.feibo.joke.manager.LoadListener;
import com.feibo.joke.manager.list.SearchFriendsManager;
import com.feibo.joke.manager.list.SearchVideoDetialManager;
import com.feibo.joke.manager.work.UserManager;
import com.feibo.joke.model.User;
import com.feibo.joke.model.Video;
import com.feibo.joke.utils.UIUtil;
import com.feibo.joke.view.BaseActivity;
import com.feibo.joke.view.group.VideoFlowGroup;
import com.feibo.joke.view.module.mine.BaseLoginFragment;
import com.feibo.joke.view.module.mine.detail.UserDetailFragment2;
import com.feibo.joke.view.util.KeyboardUtil;
import com.feibo.joke.view.util.LaunchUtil;
import com.feibo.joke.view.widget.VImageView;

import java.util.List;

import fbcore.log.LogUtil;
import in.srain.cube.views.ptr.PtrDefaultHandler;
import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.PtrHandler;

/**
 * Created by Administrator on 2015/11/7.
 */
public class SearchDetailFragment extends BaseLoginFragment implements View.OnClickListener {

    private static final String TAG = SearchDetailFragment.class.getSimpleName();
    private static final int LOGIN_OPERATE = 0x2;

    private static final String PARAM_KEYWORD = "searchWord";
    private boolean isLoading = false;
    private String msg;
    private List<User> users;

    private SearchVideoDetialManager manager;
    private SearchFriendsManager friendsManager;
    private VideoFlowGroup.OperateData operateData;

    private HeaderViewHolder headerViewHolder;
    private VideoFlowGroup videoFlowGroup;
    private PageGroup pageGroup;

    private EditText editText;
    private ImageView clear;
    private Button canclel;

    public Bundle bundle;

    public static final Bundle bulidBundle(String keyWord) {
        Bundle bundle = new Bundle();
        bundle.putString(PARAM_KEYWORD, keyWord);
        return bundle;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bundle = getActivity().getIntent().getExtras();
        String key = bundle.getString(PARAM_KEYWORD);
        videoFlowGroup = new VideoFlowGroup(getActivity());
        manager = new SearchVideoDetialManager(key);
        friendsManager = new SearchFriendsManager(key);
    }

    @Override
    public View containChildView() {
        canclel = (Button) getTitleBar().headView.findViewById(R.id.head_right);
        editText = (EditText) getTitleBar().headView.findViewById(R.id.discoivery_edit);
        clear = (ImageView) getTitleBar().headView.findViewById(R.id.btn_clear_input);
        initListener();
        return View.inflate(getActivity(), R.layout.fragment_topic_detail, null);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.list);

        // 此处添加头布局
        View headerView = view.inflate(getActivity(), R.layout.search_detial_header, null);

        headerViewHolder = new HeaderViewHolder(getActivity(), headerView);

        headerViewHolder.initListener();

        videoFlowGroup.setRecyclerView(recyclerView);
        pageGroup = new PageGroup(view);

        videoFlowGroup.addHeaderView(headerViewHolder.getHeaderView());

        videoFlowGroup.setOnOperateLoginListener(new VideoFlowGroup.OnOperateLoginListener() {
            @Override
            public void executeLogin(VideoFlowGroup.OperateData operateData) {
                SearchDetailFragment.this.operateData = operateData;
                loginClick(LOGIN_OPERATE);
            }
        });

        videoFlowGroup.setOnLoadMoreListener(new VideoFlowGroup.OnLoadMoreListener() {
            @Override
            public void loadMore() {
                onLoadMoreData();
            }
        });

        pageGroup.refreshBoard.setPtrHandler(new PtrHandler() {
            @Override
            public boolean checkCanDoRefresh(PtrFrameLayout frame, View content, View header) {
                return PtrDefaultHandler.checkContentCanBePulledDown(frame, content, header);
            }

            @Override
            public void onRefreshBegin(final PtrFrameLayout frame) {
                onRefreshData();
            }
        });

        pageGroup.showLoadPage();
        onRefreshData();

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

    private void initListener() {
        canclel.setOnClickListener(this);
        clear.setOnClickListener(this);
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // 详情页再编辑时跳转到搜索页
                msg = s.toString();
                KeyboardUtil.hideKeyboard(editText);
                Bundle bundle = SearchFragment.bulidBudle(msg);

                LaunchUtil.launchSubActivity(getActivity(), SearchFragment.class, bundle);
                // getActivity().finish();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        editText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    editText.clearFocus();
                    Bundle bundle = SearchFragment.bulidBudle(msg);
                    LaunchUtil.launchSubActivity(getActivity(), SearchFragment.class, bundle);

                    FragmentTransaction transaction = getFragmentManager().beginTransaction();
                    transaction.setCustomAnimations(R.anim.fragment_in, R.anim.fragment_out);
                    getActivity().finish();

                }
            }
        });
    }

    @Override
    public int setTitleLayoutId() {
        return R.layout.search_head;
    }

    @Override
    public void setTitlebar() {
        Bundle bundle = getActivity().getIntent().getExtras();
        editText.setText(bundle.getString(PARAM_KEYWORD));
    }

    @Override
    public void onReleaseView() {
        manager.onDestroy();
        friendsManager.onDestroy();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        case R.id.head_right:
            LaunchUtil.launchSubActivity(getActivity(), DiscoveryFragment.class, null);
            getActivity().finish();
            break;
        case R.id.btn_clear_input:
            editText.setText("");
            break;
        default:
            break;
        }
    }

    @Override
    public void loginResult(boolean result, int operationCode) {
        videoFlowGroup.enablePlay(true);
        if (result) {
            switch (operationCode) {
            case LOGIN_OPERATE:
                videoFlowGroup.operateVideo(operateData);
                break;
            }
        }
    }

    @Override
    public void onDataChange(int code) {
        videoFlowGroup.enablePlay(true);
        switch (code) {
        case DataChangeEventCode.CHANGE_TYPE_FRESH_PAGE:
            pageGroup.showLoadPage();
            onRefreshData();
            break;
        case DataChangeEventCode.CHANGE_TYPE_VIDEO_DETAIL_CHANGE:
            Bundle bundle = ((BaseActivity) getActivity()).getFinishBundle();
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

            // 改变视频状态
            videoFlowGroup.notifyVideo(video, adapterPosition);
            break;
        }
    }

    private void onRefreshData() {
        LogUtil.i(TAG, "onRefreshData");
        videoFlowGroup.stopVideo();

        if (isLoading) {
            return;
        }
        isLoading = true;

        friendsManager.getUserDetail(new LoadListener() {
            @Override
            public void onSuccess() {
                if (getActivity() == null) {
                    isLoading = false;
                    return;
                }
                users = friendsManager.getUsers();
                int rsCode = friendsManager.getRsCode();
                LogUtil.i("相关校友的数量----------------------------------------->", users.size() + "");
                LogUtil.i("List集合data 1：------------------------------------------------>", users.get(0).nickname + "");
                LogUtil.i("List集合data 2：------------------------------------------------>", users.get(1).nickname + "");

                // if (rsCode == 1004) {
                // headerViewHolder.headFriends.setVisibility(View.GONE);
                //
                // }

                if (users.size() > 5) {
                    showAvatar(users);
                } else if (users.size() == 5) {
                    headerViewHolder.butMore.setVisibility(View.INVISIBLE);
                    showAvatar(users);
                } else {
                    switch (users.size()) {
                    case 0:
                        headerViewHolder.headFriends.setVisibility(View.GONE);
                        break;
                    case 4:
                        headerViewHolder.initUserDetail04(users.get(3));
                        headerViewHolder.initUserDetail03(users.get(2));
                        headerViewHolder.initUserDetail02(users.get(1));
                        headerViewHolder.initUserDetail01(users.get(0));
                        headerViewHolder.author05.setVisibility(View.INVISIBLE);
                        headerViewHolder.butMore.setVisibility(View.INVISIBLE);
                        break;
                    case 3:
                        headerViewHolder.initUserDetail03(users.get(2));
                        headerViewHolder.initUserDetail02(users.get(1));
                        headerViewHolder.initUserDetail01(users.get(0));
                        headerViewHolder.author04.setVisibility(View.INVISIBLE);
                        headerViewHolder.author05.setVisibility(View.INVISIBLE);
                        headerViewHolder.butMore.setVisibility(View.INVISIBLE);
                        break;
                    case 2:
                        headerViewHolder.initUserDetail02(users.get(1));
                        headerViewHolder.initUserDetail01(users.get(0));
                        headerViewHolder.author03.setVisibility(View.INVISIBLE);
                        headerViewHolder.author04.setVisibility(View.INVISIBLE);
                        headerViewHolder.author05.setVisibility(View.INVISIBLE);
                        headerViewHolder.butMore.setVisibility(View.INVISIBLE);
                        break;
                    case 1:
                        headerViewHolder.initUserDetail01(users.get(0));
                        headerViewHolder.author02.setVisibility(View.INVISIBLE);
                        headerViewHolder.author03.setVisibility(View.INVISIBLE);
                        headerViewHolder.author04.setVisibility(View.INVISIBLE);
                        headerViewHolder.author05.setVisibility(View.INVISIBLE);
                        headerViewHolder.butMore.setVisibility(View.INVISIBLE);
                        break;
                    default:
                        break;
                    }
                }
            }

            private void showAvatar(List<User> users) {
                headerViewHolder.initUserDetail01(users.get(0));
                headerViewHolder.initUserDetail02(users.get(1));
                headerViewHolder.initUserDetail03(users.get(2));
                headerViewHolder.initUserDetail04(users.get(3));
                headerViewHolder.initUserDetail05(users.get(4));
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
                    // 空数据时隐藏头部
                    headerViewHolder.headFriends.setVisibility(View.GONE);

                    pageGroup.showErrorPage(getString(R.string.loading_more_no_data));
                    break;
                default:
                    pageGroup.showErrorPage("未知错误, 错误码:" + code);
                    break;
                }
            }
        });

        manager.refresh(new LoadListener() {
            @Override
            public void onSuccess() {
                LogUtil.i(TAG, "onSuccess");
                isLoading = false;
                if (getActivity() == null) {
                    return;
                }
                pageGroup.refreshComplete();
                if (manager.getDatas() != null && manager.getDatas().size() == 0) {
                    pageGroup.showErrorPage(getString(R.string.loading_more_no_data));
                    return;
                }
                pageGroup.showListPage();
                videoFlowGroup.setRefreshVideos(manager.getDatas());
            }

            @Override
            public void onFail(int code) {
                LogUtil.i(TAG, "onFail");
                isLoading = false;
                if (getActivity() == null) {
                    return;
                }
                pageGroup.refreshComplete();
                switch (code) {
                case ReturnCode.NO_NET:
                    pageGroup.showErrorPage(getString(R.string.page_no_net));
                    break;
                case ReturnCode.RS_EMPTY_ERROR:
                    pageGroup.showErrorPage(getString(R.string.loading_more_no_data));
                    break;
                default:
                    pageGroup.showErrorPage("未知错误");
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

        manager.loadMore(new LoadListener() {
            @Override
            public void onSuccess() {
                if (getActivity() == null) {
                    return;
                }
                videoFlowGroup.setLoadVideos(manager.getLoadMoreDatas());
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

    private class PageGroup {
        private ViewGroup infoBoard;
        private PtrFrameLayout refreshBoard;

        public PageGroup(View view) {
            refreshBoard = (PtrFrameLayout) view.findViewById(R.id.refresh);
            infoBoard = (ViewGroup) view.findViewById(R.id.board_info);
        }

        private void showLoadPage() {
            infoBoard.removeAllViews();
            View view = View.inflate(getActivity(), R.layout.page_load, null);
            infoBoard.addView(view);
            infoBoard.setVisibility(View.VISIBLE);
            refreshBoard.setVisibility(View.GONE);
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
            refreshBoard.setVisibility(View.GONE);
        }

        private void showListPage() {
            infoBoard.setVisibility(View.GONE);
            refreshBoard.setVisibility(View.VISIBLE);
        }

        private void refreshComplete() {
            refreshBoard.refreshComplete();
        }
    }

    private class HeaderViewHolder implements View.OnClickListener {
        private View headerView;
        private Context context;

        // headerView中的控件
        private LinearLayout headFriends;
        private RelativeLayout author01, author02, author03, author04, author05;
        private VImageView imageView01, imageView02, imageView03, imageView04, imageView05;
        private TextView name01, name02, name03, name04, name05;
        private Button butMore;

        public HeaderViewHolder(Context context, View header) {
            this.headerView = header;
            this.context = context;
            headFriends = (LinearLayout) header.findViewById(R.id.head_friend);
            author01 = (RelativeLayout) header.findViewById(R.id.author_01);
            author02 = (RelativeLayout) header.findViewById(R.id.author_02);
            author03 = (RelativeLayout) header.findViewById(R.id.author_03);
            author04 = (RelativeLayout) header.findViewById(R.id.author_04);
            author05 = (RelativeLayout) header.findViewById(R.id.author_05);
            imageView01 = (VImageView) header.findViewById(R.id.author_img01);
            imageView02 = (VImageView) header.findViewById(R.id.author_img02);
            imageView03 = (VImageView) header.findViewById(R.id.author_img03);
            imageView04 = (VImageView) header.findViewById(R.id.author_img04);
            imageView05 = (VImageView) header.findViewById(R.id.author_img05);
            name01 = (TextView) headerView.findViewById(R.id.author_name01);
            name02 = (TextView) headerView.findViewById(R.id.author_name02);
            name03 = (TextView) headerView.findViewById(R.id.author_name03);
            name04 = (TextView) headerView.findViewById(R.id.author_name04);
            name05 = (TextView) headerView.findViewById(R.id.author_name05);
            butMore = (Button) header.findViewById(R.id.more_friend);
        }

        public void initUserDetail01(User user) {
            if (user == null) {
                return;
            }
            UIUtil.setVAvatar(user.avatar, user.isSensation(), imageView01);
            name01.setText(user.nickname);

        }

        public void initUserDetail02(User user) {
            if (user == null) {
                return;
            }
            UIUtil.setVAvatar(user.avatar, user.isSensation(), imageView02);
            name02.setText(user.nickname);

        }

        public void initUserDetail03(User user) {
            if (user == null) {
                return;
            }
            UIUtil.setVAvatar(user.avatar, user.isSensation(), imageView03);
            name03.setText(user.nickname);
        }

        public void initUserDetail04(User user) {
            if (user == null) {
                return;
            }
            UIUtil.setVAvatar(user.avatar, user.isSensation(), imageView04);
            name04.setText(user.nickname);
        }

        public void initUserDetail05(User user) {
            if (user == null) {
                return;
            }
            UIUtil.setVAvatar(user.avatar, user.isSensation(), imageView05);
            name05.setText(user.nickname);
        }

        public void initListener() {
            butMore.setOnClickListener(this);
            author01.setOnClickListener(this);
            author02.setOnClickListener(this);
            author03.setOnClickListener(this);
            author04.setOnClickListener(this);
            author05.setOnClickListener(this);

        }

        public View getHeaderView() {
            return headerView;
        }

        @Override
        public void onClick(View v) {

            switch (v.getId()) {
            case R.id.more_friend:
                Bundle b = SearchFriendsFragment.bulidBundle(bundle.getString(PARAM_KEYWORD));
                LaunchUtil.launchSubActivity(getActivity(), SearchFriendsFragment.class, b);
                break;
            case R.id.author_01:

                boolean isFromMe1 = UserManager.getInstance().isFromMe((int) users.get(0).id);
                LaunchUtil.launchSubActivity(getActivity(), UserDetailFragment2.class,
                        UserDetailFragment2.buildBundle(isFromMe1, users.get(0).id));
                break;
            case R.id.author_02:
                boolean isFromMe2 = UserManager.getInstance().isFromMe((int) users.get(1).id);
                LaunchUtil.launchSubActivity(getActivity(), UserDetailFragment2.class,
                        UserDetailFragment2.buildBundle(isFromMe2, users.get(1).id));
                break;
            case R.id.author_03:
                boolean isFromMe3 = UserManager.getInstance().isFromMe((int) users.get(2).id);
                LaunchUtil.launchSubActivity(getActivity(), UserDetailFragment2.class,
                        UserDetailFragment2.buildBundle(isFromMe3, users.get(2).id));
                break;
            case R.id.author_04:
                boolean isFromMe4 = UserManager.getInstance().isFromMe((int) users.get(3).id);
                LaunchUtil.launchSubActivity(getActivity(), UserDetailFragment2.class,
                        UserDetailFragment2.buildBundle(isFromMe4, users.get(3).id));
                break;
            case R.id.author_05:
                boolean isFromMe5 = UserManager.getInstance().isFromMe((int) users.get(4).id);
                LaunchUtil.launchSubActivity(getActivity(), UserDetailFragment2.class,
                        UserDetailFragment2.buildBundle(isFromMe5, users.get(4).id));
                break;
            default:
                break;
            }
        }
    }
}
