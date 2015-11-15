package com.feibo.snacks.view.module.person.setting;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.feibo.snacks.R;
import com.feibo.snacks.model.bean.Feedback;
import com.feibo.snacks.model.bean.NetResult;
import com.feibo.snacks.model.bean.User;
import com.feibo.snacks.manager.ILoadingListener;
import com.feibo.snacks.manager.global.UserManager;
import com.feibo.snacks.manager.module.person.FeedbackManager;
import com.feibo.snacks.view.base.BaseTitleFragment;
import com.feibo.snacks.view.widget.loadingview.LoadingViewHelper;
import com.feibo.snacks.view.util.RemindControl;

import java.util.List;

@SuppressLint("InflateParams")
public class FeedbackFragment extends BaseTitleFragment {

    private ListView listView;
    private EditText editText;
    private View send;

    private LoadingViewHelper loadingHelper;
    private FeedbackManager manager;
    private FeedbackAdapeter adapter;
    private List<Feedback> list;

    @Override
    public int onCreateTitleBar() {
        return R.layout.layout_base_header;
    }

    @Override
    public View onCreateContentView() {
        RemindControl.cancelToast();
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_feedback, null);
        initWidget(view);
        initData();
        initListener();
        return view;
    }

    private void initListener() {
        getTitleBar().leftPart.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().finish();
            }
        });

        send.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                final String content = editText.getText().toString().trim();
                if (content.length() != 0) {
                    if(getActivity()!=null){
                        RemindControl.showProgressDialog(getActivity(),null,null);
                    }
                    manager.submit(content, new ILoadingListener() {
                        @Override
                        public void onSuccess() {
                            RemindControl.hideProgressDialog();
                            Feedback feedback = new Feedback(false, content);
                            User user = UserManager.getInstance().getUser();
                            if (user != null) {
                                feedback.setIcon(user.getAvatar());
                            }
                            list.add(feedback);
                            adapter.setItems(list);
                            adapter.notifyDataSetChanged();
                            listView.setSelection(adapter.getCount() - 1);
                            editText.setText("");
                            hideInputMethod();
                        }

                        @Override
                        public void onFail(String failMsg) {
                            RemindControl.hideProgressDialog();
                            //提交反馈成功不返回data
                            if (failMsg.equals(NetResult.NOT_DATA_STRING)) {
                                onSuccess();
                                return;
                            }
                            //提交失败
                            if (getActivity() != null) {
                                RemindControl.showSimpleToast(getActivity(), failMsg);
                            }
                        }
                    });

                } else {
                    RemindControl.showSimpleToast(getActivity(), "请输入反馈信息哦");
                }
            }

        });
    }

    private void initData() {
        TextView title = (TextView) getTitleBar().headView.findViewById(R.id.head_title);
        title.setText("意见反馈");
        editText.setHintTextColor(getResources().getColor(R.color.color_six));

        adapter = new FeedbackAdapeter(getActivity());
        listView.setAdapter(adapter);
        if (manager == null) {
            manager = new FeedbackManager();
        }
        loadData();
    }

    private void loadData() {
        if (loadingHelper == null) {
            loadingHelper = LoadingViewHelper.generateOnParentAtPosition(0, (ViewGroup) listView.getParent(), 0);
        }
        loadingHelper.start();
        manager.loadData(new ILoadingListener() {
            @Override
            public void onSuccess() {
                if (getActivity() == null) {
                    return;
                }
                loadingHelper.end();
                loadingHelper = null;
                list = manager.getFeedbacks();
                if(list != null) {
                    adapter.setItems(list);
                    adapter.notifyDataSetChanged();
                    listView.setSelection(adapter.getCount() - 1);
                }
            }

            @Override
            public void onFail(String failMsg) {
                loadingHelper.fail(failMsg, new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        loadData();
                    }
                });
            }
        });
    }

    private void initWidget(View view) {
        send = view.findViewById(R.id.feedback_send);
        editText = (EditText) view.findViewById(R.id.feedback_content);
        listView = (ListView) view.findViewById(R.id.feedback_list);
    }

    private void hideInputMethod() {
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
    }

    @Override
    public void onPause() {
        super.onPause();
        hideInputMethod();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        manager.clear();
        manager = null;
    }
}
