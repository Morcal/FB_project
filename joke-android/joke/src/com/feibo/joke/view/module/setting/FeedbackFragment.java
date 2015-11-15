package com.feibo.joke.view.module.setting;

import java.util.ArrayList;

import android.content.Context;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ListView;
import android.widget.TextView;

import com.feibo.joke.R;
import com.feibo.joke.manager.LoadListener;
import com.feibo.joke.manager.list.FeedbacksManager;
import com.feibo.joke.manager.work.OperateManager;
import com.feibo.joke.manager.work.UserManager;
import com.feibo.joke.model.Feedback;
import com.feibo.joke.utils.StringUtil;
import com.feibo.joke.utils.TimeUtil;
import com.feibo.joke.view.BaseTitleFragment;
import com.feibo.joke.view.adapter.FeedbackAdapter;
import com.feibo.joke.view.group.GroupConfig;
import com.feibo.joke.view.group.impl.FeedbacksListGroup;
import com.feibo.joke.view.util.ToastUtil;
import com.feibo.joke.view.widget.InputBarView;
import com.feibo.joke.view.widget.InputBarView.InputBarViewListener;

/**
 * com.feibo.joke.view.module.setting.FeedbackFragment
 * 
 * @author LinMW<br/>
 *         Creat at2015-5-30 下午12:17:24
 */
public class FeedbackFragment extends BaseTitleFragment {

    private static final int DELAY_TIME = 10000;
    
    private View root;
    private FeedbacksManager manager;
    private FeedbackAdapter adapter;
    private Context mContext;
    private InputBarView sendBar;// 发送评论文字按钮
    private String feedContent;
    private FeedbackPostListener feedbackPostListener;
    private Boolean isWaitResult = false;

    private FeedbacksListGroup group;

    @Override
    public View containChildView() {
        mContext = getActivity();
        manager = new FeedbacksManager();
        adapter = new FeedbackAdapter(mContext);
        group = new FeedbacksListGroup(mContext);
        group.setListAdapter(adapter);
        group.setListManager(manager);
        group.setGroupConfig(GroupConfig.create(GroupConfig.GROUP_DEFAULT));
        group.onCreateView();
        root = group.getRoot();
        findView();
        initView();
        return root;
    }

    private void findView() {
        sendBar = (InputBarView) group.getRoot().findViewById(R.id.send_bar_feedback);
    }

    @Override
    public void setTitlebar() {
        TitleBar titleBar = getTitleBar();
        titleBar.rightPart.setVisibility(View.GONE);
        TextView title = (TextView) titleBar.title;
        title.setText(R.string.feedback_title);
    }

    @Override
    public int setTitleLayoutId() {
        return R.layout.base_titlebar;
    }

    @Override
    public void onReleaseView() {
        manager.onDestroy();
        group.onDestroyView();
    }

    private void initView() {
        group.getListView().setTranscriptMode(ListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
        // group.getListView().setStackFromBottom(true);
        group.getListView().setOnScrollListener(new OnScrollListener() {

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if (scrollState == OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {// 隐藏软键盘
                    sendBar.hideKeyboard();
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
            }
        });
        sendBar.setMsg("亲，这条意见已经提交了！", "请输入你的宝贵意见哦！", "");
        sendBar.setListener(new InputBarViewListener() {

            @Override
            public void send(String content) {
                putFeedback(content);
                feedContent = content;
            }

            @Override
            public void onResetInputBar() {
                feedContent = "";
            }

            @Override
            public void onEditTextFocusChange(boolean hasFocus) {
                // TODO Auto-generated method stub
            }

            @Override
            public boolean beforeClickSend() {
                if (isWaitResult) {
                    ToastUtil.showSimpleToast("意见正在提交...");
                    return true;
                }
                return false;
            }
        });
    }

    /**
     * 通过网络提交建议
     * 
     * @param content
     */
    private void putFeedback(String content) {
        isWaitResult = true;
        if (feedbackPostListener == null) {
            feedbackPostListener = new FeedbackPostListener();
        }
        OperateManager.putFeedback(StringUtil.encode2Utf(content), feedbackPostListener);
        sendBar.hideKeyboard();
    }

    private class FeedbackPostListener implements LoadListener {

        @Override
        public void onSuccess() {
            sendBar.setLastComment(sendBar.getText(), TimeUtil.getPublishTime(), 0);
            addFeedback2List(Feedback.TYPE_USER, feedContent);
            sendBar.resetInputBar(true);
            // addFeedback2List(Feedback.TYPE_SYSTEM,"您的建议已经提交，谢谢您的反馈！(系统自动回复)");
            // group.getListView().setSelection(adapter.getCount());
            isWaitResult = false;
            postDelayed(refreshData, DELAY_TIME);
        }

        private void addFeedback2List(int type, String content) {
            Feedback feedback = new Feedback();
            feedback.id = 0;
            feedback.type = type;
            feedback.content = content;
            if (type == Feedback.TYPE_USER) {
                feedback.author = UserManager.getInstance().getUser();
            } else {
                feedback.author = null;
            }
            feedback.publishTime = TimeUtil.getPublishTime();

            if (adapter.getItems() == null) {
                adapter.setItems(new ArrayList<Feedback>());
            }

            adapter.getItems().add(feedback);
            adapter.notifyDataSetChanged();
        }

        @Override
        public void onFail(int code) {
            isWaitResult = false;
        }
    }

    private Runnable refreshData = new Runnable() {
        @Override
        public void run() {
            group.refreshData();
        }
    };

}
