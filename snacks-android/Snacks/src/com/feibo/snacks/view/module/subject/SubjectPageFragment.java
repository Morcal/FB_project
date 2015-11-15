package com.feibo.snacks.view.module.subject;

import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v7.widget.GridLayoutManager;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.feibo.snacks.R;
import com.feibo.snacks.app.Constant;
import com.feibo.snacks.model.bean.Special;
import com.feibo.snacks.model.bean.Subject;
import com.feibo.snacks.manager.global.RedPointManager;
import com.feibo.snacks.manager.global.StatisticsManager;
import com.feibo.snacks.manager.module.subject.SubjectPageManager;
import com.feibo.snacks.view.widget.loadingview.RefreshLoadingView4List;
import com.feibo.snacks.view.widget.operationview.RefreshListViewOperation;
import com.feibo.snacks.view.base.BannerJumpHelper;
import com.feibo.snacks.view.base.BaseSwitchActivity;
import com.feibo.snacks.view.base.BaseTitleFragment;
import com.feibo.snacks.view.base.LoadMoreScrollListener;
import com.feibo.snacks.view.module.MainActivity;
import com.feibo.snacks.view.util.LaunchUtil;
import com.feibo.snacks.view.util.RemindControl;
import com.feibo.snacks.view.widget.WrapRecyclerView;
import com.feibo.snacks.view.widget.pullToRefresh.PullToRefreshListView;
import com.umeng.analytics.MobclickAgent;

import java.util.List;

public class SubjectPageFragment extends BaseTitleFragment {

    public static final String ID = "subjectid";
    private static final String SUBJECT = "专题";

    private PullToRefreshListView pullToRefreshListView;
    private WrapRecyclerView recyclerView;
    private View scrollTopView;
    private List<Special> topics;
    private List<Subject> subjects;

    private SubjectPageManager subjectManager;
    private SubjectAdapter subjectAdapter;
    private SubjectAboveAdapter adapter;

    private TextView cartNum;
    private ListView listView;
    private View root;
    private View headView;

    @Override
    public int onCreateTitleBar() {
        return R.layout.layout_list_header;
    }

    @Override
    public View onCreateContentView() {
        root = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_pulltorefresh_list, null);
        initWidget(root);
        createAdapter();
        initListener();
        showData();
        return root;
    }

    private void showData() {
        if (getActivity() == null) {
            return;
        }
        if (subjectManager == null) {
            initManager();
        }

        topics = subjectManager.getBanSubjects();
        subjects = subjectManager.getDatas();
        if (topics == null || subjects == null) {
            initData();
            return;
        }
        hasData();
    }

    private void hasData() {
        showTopic();
        showSubject();
    }

    private void initManager() {
        RefreshLoadingView4List refreshLoadingView = new RefreshLoadingView4List(pullToRefreshListView) {
            @Override
            public void reFillData(Object data) {
                if (getActivity() == null) {
                    return;
                }
                createAdapter();
                setData(data);
            }

            @Override
            public void fillData(Object data) {
                setData(data);
            }

            private void setData(Object data) {
                if (getActivity() == null) {
                    return;
                }
                if (data != null && data instanceof List) {
                    topics = (List<Special>) data;
                    showTopic();
                }
            }

            @Override
            public void fillMoreData(Object data) {
                if (getActivity() == null) {
                    return;
                }
                if (data != null && data instanceof List) {
                    subjects = (List<Subject>) data;
                    showSubject();
                }
            }

            @Override
            public void loadMoreData(LoadMoreScrollListener listener) {
                subjectManager.loadMore(listener);
            }

            @Override
            public ViewGroup getLoadingParentView() {
                return (ViewGroup) root;
            }

            @Override
            public void onLoadingHelperFailButtonClick() {
                subjectManager.loadData();
            }
        };
        subjectManager = new SubjectPageManager(refreshLoadingView);
        RefreshListViewOperation operation = new RefreshListViewOperation(pullToRefreshListView, subjectManager) {
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

    private void initListener() {
        listView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                int pos = listView.getHeaderViewsCount() == 0 ? position : position - listView.getHeaderViewsCount();
                if (pos >= subjectAdapter.getCount()) {
                    return;
                }
                Subject subject = subjectAdapter.getItem(pos);
                if (subject == null) {
                    return;
                }
                Bundle bundle = new Bundle();
                bundle.putInt(ID, subject.id);
                bundle.putInt(H5SubjectDetailFragment.POS, pos);
                bundle.putInt(H5SubjectDetailFragment.LIKE_NUMBER, subject.hotindex);
                LaunchUtil.launchSubDetailForResult(LaunchUtil.SUBJECT_PAGE_REQUEST_CODE, getActivity(),
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

    private void initWidget(View view) {
        pullToRefreshListView = (PullToRefreshListView) view.findViewById(R.id.list);
        headView = LayoutInflater.from(getActivity()).inflate(R.layout.layout_header_subject, null);
        recyclerView = (WrapRecyclerView) headView.findViewById(R.id.my_recycler_view);
        scrollTopView = root.findViewById(R.id.scroll_top);
        recyclerView.setHasFixedSize(true);
        GridLayoutManager layoutManager = new GridLayoutManager(getActivity(), 2);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setRow(2);

        setTitle();
        listView = pullToRefreshListView.getRefreshableView();
    }

    private void setTitle() {
        TextView titleView = (TextView) getTitleBar().title.findViewById(R.id.head_title_name);
        ImageView titleImg = (ImageView) getTitleBar().title.findViewById(R.id.head_title_img);
        titleView.setText(SUBJECT);
        titleImg.setVisibility(View.GONE);
        getTitleBar().leftPart.setVisibility(View.GONE);
        cartNum = (TextView) getTitleBar().rightPart.findViewById(R.id.home_car_number);

        getTitleBar().rightPart.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                LaunchUtil.launchCartActivityForResult(MainActivity.ENTRY_HOME_SCENCE, getActivity());
            }
        });
    }

    private void showCarNumber() {
        RedPointManager.getInstance().setRedNumberView(cartNum);
    }

    private void initData() {
        subjectManager.loadData();
    }

    private void showSubject() {
        if (subjectAdapter != null) {
            subjectAdapter.setItems(subjects);
            subjectAdapter.notifyDataSetChanged();
        }
    }

    private void createAdapter() {
        subjectAdapter = new SubjectAdapter(getActivity());
        listView.setAdapter(subjectAdapter);
    }

    private void createAboveAdapter(List<Special> banners) {
        adapter = new SubjectAboveAdapter(getActivity());
        adapter.setBanners(banners);
        recyclerView.setAdapter(adapter);
        adapter.setListener(new SubjectAboveAdapter.OnBannerClickListener() {
            @Override
            public void click(int id) {
                MobclickAgent.onEvent(getActivity(), getResources().getString(R.string.click_subject_special));
                BannerJumpHelper.turnBannerSituation(getActivity(), topics.get(id), Constant.SUBJECT_BANNER, getActivity().getString(R.string.subjectAd));
            }
        });
    }

    private void showTopic() {
        topics = subjectManager.getBanSubjects();
        if (topics != null) {
            createAboveAdapter(topics);
            if (headView.getVisibility() != View.VISIBLE) {
                headView.setVisibility(View.VISIBLE);
                listView.addHeaderView(headView);
            }
        } else {
            headView.setVisibility(View.GONE);
        }
    }

    @Override
    public void onResume() {
        fragmentName = getResources().getString(R.string.subjectfragment);
        super.onResume();
        RemindControl.cancelToast();
        showCarNumber();
    }

    @Override
    public void onPause() {
        fragmentName = getResources().getString(R.string.subjectfragment);
        super.onPause();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == H5SubjectDetailFragment.RESULT_CODE && data != null) {
            subjects.get(data.getExtras().getInt(H5SubjectDetailFragment.POS)).hotindex = data.getExtras().getInt(
                    H5SubjectDetailFragment.LIKE_NUMBER);
            subjectAdapter.setItems(subjects);
            subjectAdapter.notifyDataSetChanged();
        }
    }
}
