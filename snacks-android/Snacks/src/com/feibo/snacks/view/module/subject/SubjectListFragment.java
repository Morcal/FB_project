package com.feibo.snacks.view.module.subject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.feibo.snacks.R;
import com.feibo.snacks.app.Constant;
import com.feibo.snacks.manager.global.RedPointManager;
import com.feibo.snacks.model.bean.RedPointInfo;
import com.feibo.snacks.model.bean.Subject;
import com.feibo.snacks.manager.global.StatisticsManager;
import com.feibo.snacks.manager.module.subject.SubjectManager;
import com.feibo.snacks.view.module.MainActivity;
import com.feibo.snacks.view.widget.loadingview.LoadingMoreView4List;
import com.feibo.snacks.view.widget.operationview.ListViewOperation;
import com.feibo.snacks.view.base.BaseSwitchActivity;
import com.feibo.snacks.view.base.BaseTitleFragment;
import com.feibo.snacks.view.base.LoadMoreScrollListener;
import com.feibo.snacks.view.util.LaunchUtil;
import com.feibo.snacks.view.util.RemindControl;
import com.feibo.snacks.view.util.UIUtil;
import com.umeng.analytics.MobclickAgent;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SubjectListFragment extends BaseTitleFragment {

    public static final String ID = "subjectid";
    private static final String SUBJECT = "专题列表";

    private View root;
    private ListView listView;
    private View scrollTopView;
    private TitleViewHolder titleHolder;

    private SubjectManager manager;
    private RedPointManager redPointManager;
    private SubjectAdapter adapter;

    private RedPointManager.RedPointObserver redPointObserver;

    private List<Subject> subjects;

    private int subjectId = 0;

    @Override
    public int onCreateTitleBar() {
        return R.layout.layout_list_header;
    }

    @Override
    public View onCreateContentView() {
        root = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_subject, null);
        scrollTopView = root.findViewById(R.id.scroll_top);
        listView = (ListView) root.findViewById(R.id.list);
        createAdapter();
        return root;
    }

    @Override
    protected void initTitleBar(TitleBar titleBar) {
        titleHolder = new TitleViewHolder(titleBar.headView);
        titleHolder.titleText.setText(SUBJECT);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        setFragmentName(getResources().getString(R.string.subjectfragment));
        subjectId = getArguments().getInt(ID);
        redPointManager = RedPointManager.getInstance();
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initListener();
        showData();
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
    public void onDestroyView() {
        super.onDestroyView();
        titleHolder.onDestroy();
        redPointManager.deleteObserver(redPointObserver);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        manager.clear();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == H5SubjectDetailFragment.RESULT_CODE && data != null) {
            adapter.getItem(data.getExtras().getInt(H5SubjectDetailFragment.POS)).hotindex = data.getExtras()
                    .getInt(H5SubjectDetailFragment.LIKE_NUMBER);
            adapter.notifyDataSetChanged();
        }
    }

    private void showData() {
        if (getActivity() == null) {
            return;
        }
        if (manager == null) {
            initManager();
        }

        subjects = manager.getDatas();
        if (subjects == null) {
            initData();
            return;
        }
        hasData();
    }

    private void hasData() {
        adapter.setItems(subjects);
        adapter.notifyDataSetChanged();
    }

    private void initData() {
        manager.loadData();
    }

    private void initManager() {
        LoadingMoreView4List refreshLoadingView4List = new LoadingMoreView4List(listView) {
            @Override
            public void fillMoreData(Object data) {
                if(getActivity() != null) {
                    setData(data);
                }
            }

            @Override
            public void loadMoreData(LoadMoreScrollListener listener) {
                manager.loadMore(listener);
            }

            @Override
            public View getLoadingParentView() {
                return root;
            }

            @Override
            public void onLoadingHelperFailButtonClick() {
                showData();
            }

            @Override
            public void fillData(Object data) {
                if(getActivity() != null) {
                    setData(data);
                }
            }
        };
        manager = new SubjectManager(refreshLoadingView4List);
        manager.setSubjectId(subjectId);
        ListViewOperation operation = new ListViewOperation(listView, manager) {
            @Override
            public void operationItemAtPosition(int position) {
                if (scrollTopView != null) {
                    if (position > 5) {
                        scrollTopView.setVisibility(View.VISIBLE);
                    } else {
                        scrollTopView.setVisibility(View.GONE);
                    }
                }
            }
        };
    }

    private void setData(Object data) {
        if (data != null && data instanceof List) {
            subjects = (List<Subject>) data;
            hasData();
        }
    }

    private void createAdapter() {
        if (adapter == null) {
            adapter = new SubjectAdapter(getActivity());
        }
        listView.setAdapter(adapter);
    }


    private void initListener() {
        listView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Subject subject = adapter.getItem(position);
                if (subject == null) {
                    return;
                }
                Bundle bundle = new Bundle();
                bundle.putInt(ID, subject.id);
                bundle.putInt(H5SubjectDetailFragment.POS, position);
                bundle.putInt(H5SubjectDetailFragment.LIKE_NUMBER, subject.hotindex);
                LaunchUtil.launchSubDetailForResult(LaunchUtil.SUBJECT_REQUEST_CODE, getActivity(),
                        BaseSwitchActivity.class, H5SubjectDetailFragment.class, bundle);
                MobclickAgent.onEvent(getActivity(), getResources().getString(R.string.click_subject_item));
                StatisticsManager.getInstance().statisticsVisitQuantity(subject.id, Constant.SUBJECT_TYPE, 0);
            }
        });

        scrollTopView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listView.dispatchTouchEvent(MotionEvent.obtain(SystemClock.uptimeMillis(), SystemClock.uptimeMillis(),
                        MotionEvent.ACTION_CANCEL, 0, 0, 0));
                listView.setSelection(0);
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
