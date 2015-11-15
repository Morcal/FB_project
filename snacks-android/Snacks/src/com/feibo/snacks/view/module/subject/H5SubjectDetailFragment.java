package com.feibo.snacks.view.module.subject;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.WebView;
import android.widget.TextView;

import com.feibo.snacks.R;
import com.feibo.snacks.app.AppContext;
import com.feibo.snacks.app.Constant;
import com.feibo.snacks.manager.ILoadingListener;
import com.feibo.snacks.manager.global.CollectSubjectManager;
import com.feibo.snacks.manager.global.RedPointManager;
import com.feibo.snacks.manager.global.ShareManager;
import com.feibo.snacks.manager.global.UserManager;
import com.feibo.snacks.manager.module.subject.H5SubjectDetailManager;
import com.feibo.snacks.manager.module.subject.SubjectWebManager;
import com.feibo.snacks.model.bean.NetResult;
import com.feibo.snacks.model.bean.RedPointInfo;
import com.feibo.snacks.model.bean.Subject;
import com.feibo.snacks.util.SPHelper;
import com.feibo.snacks.view.base.BaseFragment;
import com.feibo.snacks.view.base.BaseSwitchActivity;
import com.feibo.snacks.view.base.BaseTitleFragment;
import com.feibo.snacks.view.dialog.RemindDialog.RemindSource;
import com.feibo.snacks.view.module.MainActivity;
import com.feibo.snacks.view.module.goods.goodsdetail.H5GoodsDetailFragment;
import com.feibo.snacks.view.module.person.PersonFragment;
import com.feibo.snacks.view.module.person.login.LoginFragment;
import com.feibo.snacks.view.util.LaunchUtil;
import com.feibo.snacks.view.util.RemindControl;
import com.feibo.snacks.view.util.RemindControl.OnRemindListener;
import com.feibo.snacks.view.util.ShareUtil;
import com.feibo.snacks.view.widget.loadingview.AbsLoadingView;
import com.umeng.analytics.MobclickAgent;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import fbcore.log.LogUtil;

public class H5SubjectDetailFragment extends BaseTitleFragment {

    private static final String SUBJECT_DETAIL_TITLE = "专题详情";
    public static final String LIKE_NUMBER = "likeNumber";
    public static final String ISCANCEL = "isCancel";
    public static final String ID = "subjectid";
    public static final String POS = "pos";
    public static final int RESULT_CODE = 0X444;

    private WebView subjectWeb;
    private View rootView;

    private int keyId;
    private int pos; // banner专题页中专题的索引
    private int shareNum;
    private int likeNum;
    private boolean isCancel = false;
    private Subject subject;
    private SubjectWebManager webViewManager;
    private RedPointManager redPointManager;
    private H5SubjectDetailManager manager;

    private RedPointManager.RedPointObserver redPointObserver;

    private TitleViewHolder titleHolder;

    @Override
    public int onCreateTitleBar() {
        return R.layout.layout_list_header;
    }

    @Override
    public View onCreateContentView() {
        rootView = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_detail_subject_h5, null);
        subjectWeb = (WebView) rootView.findViewById(R.id.subject_webview);
        return rootView;
    }

    @Override
    protected void initTitleBar(TitleBar titleBar) {
        titleHolder = new TitleViewHolder(titleBar.headView);
        titleHolder.titleText.setText(SUBJECT_DETAIL_TITLE);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        Bundle bundle = getArguments();
        keyId = bundle.getInt(ID, 0);
        pos = bundle.getInt(POS);
        likeNum = bundle.getInt(LIKE_NUMBER);
        redPointManager = RedPointManager.getInstance();
        setFragmentName(getResources().getString(R.string.subjectH5DetailFragment));
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initManager();

        redPointObserver = new RedPointManager.RedPointObserver() {
            @Override
            public void updateRedPoint(RedPointInfo info) {
                redPointManager.setRedNumberView(titleHolder.carNumText);
            }
        };
        redPointManager.addObserver(redPointObserver);
    }

    @Override
    public void onResume() {
        super.onResume();
        RemindControl.cancelToast();
        // 购物车红点
        redPointManager.setRedNumberView(titleHolder.carNumText);
    }

    @Override
    public void onPause() {
        super.onPause();
        SPHelper.addShareSubject(keyId, shareNum);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        redPointManager.deleteObserver(redPointObserver);
        titleHolder.onDestroy();
    }

    private void clickItem(int goodsId) {
        Bundle bundle = new Bundle();
        bundle.putInt(H5GoodsDetailFragment.GOODS_ID, goodsId);
        bundle.putInt(H5GoodsDetailFragment.ENTER_SOURCE, Constant.SUBJECT_H5_LIST);
        bundle.putString(H5GoodsDetailFragment.ENTER_LOCATION, getString(R.string.click_subject_special_taobao_order));
        bundle.putString(BaseFragment.ORIGIN, getResources().getString(R.string.subjectH5DetailFragment));
        LaunchUtil.launchActivity(getActivity(), BaseSwitchActivity.class, H5GoodsDetailFragment.class, bundle);
        MobclickAgent.onEvent(getActivity(), getResources().getString(R.string.enter_subject_detail));
    }

    private void initManager() {
        AbsLoadingView absLoadingView = new AbsLoadingView() {
            @Override
            public View getLoadingParentView() {
                return rootView;
            }

            @Override
            public void onLoadingHelperFailButtonClick() {
                initManager();
            }

            @Override
            public void fillData(Object detail) {
                if (detail != null && detail instanceof Subject) {
                    subject = (Subject) detail;
                    hasData();
                }
            }
        };
        absLoadingView.setLauncherPositon(2);
        manager = new H5SubjectDetailManager(absLoadingView);
        manager.setSubjectId(keyId);
        manager.loadData();
    }

    private void initWebViewManager() {
        if (webViewManager == null) {
            webViewManager = new SubjectWebManager();
            webViewManager.setup(subjectWeb, new SubjectWebManager.WebViewListener() {
                @Override
                public void onPageStarted(WebView view, String url, Bitmap favicon) {
                    if(getActivity() == null){
                        return;
                    }
                    RemindControl.showProgressDialog(getActivity(), "", null);
                }

                @Override
                public void onPageFinished(WebView view, String url) {
                    if(getActivity() == null){
                        return;
                    }
                    RemindControl.hideProgressDialog();
                    boolean isCollect = subject.collectStatus == 1 ? true : false;
                    if(isCollect) {
                        SPHelper.addCollectSubject(keyId);
                    }
                    setCollectState(isCollect);
                    likeNum = subject.hotindex > likeNum ? subject.hotindex : likeNum;
                    setLikeNum();
                }

                @Override
                public void onClickShare(int id, String desc, String title, String imgUrl, String contentUrl,int type) {
                    subjectWeb.post(() -> share(id,desc,title,imgUrl,contentUrl,type));
                }

                @Override
                public void onClickItem(int id) {
                    LogUtil.d("wangfujun", "id = " + id);
                    clickItem(id);
                }

                @Override
                public void onClickCollect() {
                    LogUtil.d("wangfujun","collect " + keyId);
                    addCollect();
                }

            });
            subjectWeb.loadUrl(subject.webUrl);
        }
    }

    // 设置喜欢或者收藏的状态
    private void setCollectState(final boolean b) {
        LogUtil.d("wangf", "b = " + b);
        subjectWeb.post(new Runnable() {
            @Override
            public void run() {
                subjectWeb.loadUrl("javascript:changeCollect('" + b + "')");
            }
        });
    }

    // 设置喜欢或者收藏的数量
    private void setLikeNum() {
        subjectWeb.post(new Runnable() {
            @Override
            public void run() {
                subjectWeb.loadUrl("javascript:changeCollectNum('" + likeNum + "')");
            }
        });
        setForResult();
    }

    // 设置分享的数量
    private void setShareNum() {
        subjectWeb.post(new Runnable() {
            @Override
            public void run() {
                subjectWeb.loadUrl("javascript:changeShareNum('" + shareNum + "')");
            }
        });
    }

    private void setForResult() {
        if (getActivity() != null) {
            Intent intent = new Intent();
            Bundle b = new Bundle();
            b.putInt(LIKE_NUMBER, likeNum);
            b.putBoolean(ISCANCEL, isCancel);
            b.putInt(POS, pos);
            intent.putExtras(b);
            getActivity().setResult(RESULT_CODE, intent);
        }
    }

    private void hasData() {
        initWebViewManager();
        if(getActivity() == null){
            return;
        }
        shareNum = SPHelper.getShareSubjectNumber(keyId);
        shareNum = subject.shareNum > shareNum ? subject.shareNum : shareNum;
        setShareNum();
    }

    private void share(int id,String desc,String title,String imgUrl,String contentUrl,int type) {
        ShareUtil shareUtil = new ShareUtil();
        shareUtil.share(getActivity(), subject.desc, subject.title, subject.img.imgUrl, ShareManager.buildSnacksSubjectWebUrl(subject.id), new ShareUtil.IShareResult() {
            @Override
            public void onShareResult(int result) {
                //TODO 分享成功后统计次数
                statisticShare();
            }
        });
    }

    private void statisticShare() {
        shareNum++;
        setShareNum();
        CollectSubjectManager.getInstance().addShareNumber(keyId, new ILoadingListener() {

            @Override
            public void onSuccess() {

            }

            @Override
            public void onFail(String failMsg) {

            }
        });

    }

    private void addCollect() {
        if (!AppContext.isNetworkAvailable()) {
            RemindControl.showSimpleToast(getActivity(), getActivity()
                    .getResources().getString(R.string.not_network));
            return;
        }
        if (!UserManager.getInstance().isLogin()) {
            showLoginDialog();
            return;
        }
        boolean isCollect = SPHelper.isCollectSubject(keyId);
        if (isCollect) {
            cancelCollect();
            return;
        }
        CollectSubjectManager.getInstance().addCollect(keyId, new ILoadingListener() {
            @Override
            public void onSuccess() {
                if (getActivity() == null) {
                    return;
                }
                handleCollectSuccess();
            }

            @Override
            public void onFail(String failMsg) {
                if (getActivity() == null) {
                    return;
                }
                if (NetResult.HAS_COLLECTED_STRING.equals(failMsg)) {
                    handleCollectSuccess();
                }
            }
        });
    }

    private void handleCollectSuccess() {
        RemindControl.showSimpleToast(getActivity(), getResources().getString(R.string.add_collect_success));
        isCancel = false;
        likeNum++;
        setLikeNum();
        setCollectState(true);
    }

    private void cancelCollect() {
        CollectSubjectManager.getInstance().removeOneCollect(keyId, new ILoadingListener() {
            @Override
            public void onSuccess() {
                if (getActivity() == null) {
                    return;
                }
                RemindControl.showSimpleToast(getActivity(), R.string.cancel_collect_success);
                isCancel = true;
                likeNum = likeNum - 1 < 0 ? 0 : likeNum - 1;
                setLikeNum();
                setCollectState(false);
            }

            @Override
            public void onFail(String failMsg) {
                if (getActivity() == null) {
                    return;
                }
            }
        });
    }

    private void showLoginDialog() {
        RemindSource source = new RemindSource();
        source.contentStr = getResources().getString(R.string.dialog_login_content);
        source.confirm = getResources().getString(R.string.dialog_login_confirm);
        source.cancel = getResources().getString(R.string.dialog_login_cancle);
        RemindControl.showRemindDialog(getActivity(), source, new OnRemindListener() {
            @Override
            public void onConfirm() {
                Bundle bundle = new Bundle();
                bundle.putBoolean(PersonFragment.PARAM_NEED_SHOW, false);
                LaunchUtil.launchActivity(getActivity(), BaseSwitchActivity.class, LoginFragment.class, bundle);
            }

            @Override
            public void onCancel() {

            }
        });
    }

    // 进入购物车
    public void handleCart(){
        LaunchUtil.launchCartActivityForResult(MainActivity.ENTRY_HOME_SCENCE, getActivity());
    }

    // 退出程序
    public void handleQuit(){
        getActivity().finish();
    }

    class TitleViewHolder {

        @Bind(R.id.head_title_name)
        TextView titleText;

        @Bind(R.id.home_car_number)
        TextView carNumText;

        public TitleViewHolder(View view) {
            ButterKnife.bind(TitleViewHolder.this, view);
        }

        // 退出
        @OnClick(R.id.head_left)
        public void clickHeadLeft() {
            handleQuit();
        }

        // 进入购物车
        @OnClick(R.id.head_right)
        public void clickHeadRight() {
            handleCart();
        }

        public void onDestroy() {
            ButterKnife.unbind(TitleViewHolder.this);
        }
    }


}
