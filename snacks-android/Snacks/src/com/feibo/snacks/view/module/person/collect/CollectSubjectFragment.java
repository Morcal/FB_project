package com.feibo.snacks.view.module.person.collect;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.feibo.snacks.R;
import com.feibo.snacks.manager.ILoadingListener;
import com.feibo.snacks.manager.global.CollectSubjectManager;
import com.feibo.snacks.model.bean.NetResult;
import com.feibo.snacks.model.bean.Subject;
import com.feibo.snacks.view.base.BaseFragment;
import com.feibo.snacks.view.base.BaseSwitchActivity;
import com.feibo.snacks.view.base.LoadMoreScrollListener;
import com.feibo.snacks.view.dialog.LoadingDialog;
import com.feibo.snacks.view.module.person.collect.CollectFragment.IDeleteOperate;
import com.feibo.snacks.view.module.person.collect.CollectFragment.OnScrollChangeListener;
import com.feibo.snacks.view.module.subject.H5SubjectDetailFragment;
import com.feibo.snacks.view.util.LaunchUtil;
import com.feibo.snacks.view.util.RemindControl;
import com.feibo.snacks.view.widget.loadingview.LoadingViewHelper;

import java.util.List;

@SuppressLint("InflateParams")
public class CollectSubjectFragment extends BaseFragment {

    private ListView listView;
    private TextView loadMoreTextView;
    private TextView operateTextView;
    private TextView deleteTextView;
    private ProgressBar progressBar;
    private View footView;
    private View root;

    private CollectSubjectManager manager;
    private SubjectCollectAdapter adapter;
    private List<Subject> subjects;

    private LoadingViewHelper loadingHelper;

    @Override
    public ViewGroup onCreateView(LayoutInflater inflater, Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_list, null);
        manager = CollectSubjectManager.getInstance();
        initWidget();
        initData();
        initListener();
        return (ViewGroup) root;
    }

    private void initWidget() {
        listView = (ListView) root.findViewById(R.id.list);
        adapter = new SubjectCollectAdapter(getActivity());
        listView.setAdapter(adapter);

        footView = LayoutInflater.from(getActivity()).inflate(R.layout.layout_load_more, null);
        progressBar = (ProgressBar) footView.findViewById(R.id.pull_to_refresh_progress);
        loadMoreTextView = (TextView) footView.findViewById(R.id.load_more_text);
        listView.addFooterView(footView, null, false);

        operateTextView = ((CollectFragment) getParentFragment()).getTextView();
        operateTextView.setText(R.string.edit);

        deleteTextView = (TextView) root.findViewById(R.id.fragment_collect_delete);
    }

    private void initListener() {
        deleteTextView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getActivity() == null) {
                    return;
                }
                if (adapter.getClearRecord().length <= 0) {
                    RemindControl.showSimpleToast(getActivity(), R.string.no_select_subject);
                    return;
                }
                final LoadingDialog dialog = LoadingDialog.create(getActivity());
                dialog.show();
                manager.removeCollects(adapter.getClearRecord(), new ILoadingListener() {
                    @Override
                    public void onSuccess() {
                        adapter.clearRecord();
                        adapter.notifyDataSetChanged();
                        dialog.dismiss();
                        showEmptyView();
                    }

                    private void showEmptyView() {
                        if (adapter.getCount() == 0) {
                            footView.setVisibility(View.GONE);
                            deleteTextView.setVisibility(View.GONE);
                            operateTextView.setVisibility(View.GONE);
                            launchHelper();
                            showNoData();
                        } else {
                            changeShowMode();
                            operateTextView.setVisibility(View.VISIBLE);
                        }
                    }

                    @Override
                    public void onFail(String failMsg) {
                        if(getActivity() == null){
                            return;
                        }
                        RemindControl.showSimpleToast(getActivity(),failMsg);
                        dialog.dismiss();
                    }
                });
            }
        });

        adapter.setOnItemSelectListener(position -> {
            adapter.changeItemState(position);
            // changeDelTextViewBackColor()
            changeDelTextViewBackColor();
        });

        adapter.setItemClickListener(position -> clickItem(position));

        ((CollectFragment) getParentFragment()).setIDeleteOperate(1, new IDeleteOperate() {
            @Override
            public void delete(int curPage) {
                if (curPage != 1) {
                    return;
                }
                changeShowMode();
            }
        });

        ((CollectFragment) getParentFragment()).setOnScrollChangeListener(1, new OnScrollChangeListener() {
            @Override
            public void onScrollChange(int pos) {
                if (pos != 1) {
                    return;
                }
                String operate = deleteTextView.getVisibility() == View.GONE ? getString(R.string.edit)
                        : getString(R.string.cancel);
                if (operate.equals(getResources().getString(R.string.edit))) {
                    operateTextView.setVisibility(adapter.getCount() > 0 ? View.VISIBLE : View.GONE);
                } else {
                    operateTextView.setVisibility(View.VISIBLE);
                }
                operateTextView.setText(operate);
            }
        });

        LoadMoreScrollListener loadMoreScrollListener = new LoadMoreScrollListener() {
            @Override
            public void firstVisibleItem(int position) {
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                super.onScroll(view, firstVisibleItem, visibleItemCount, totalItemCount);
                if (!adapter.isEditMode) {
                    footView.setVisibility(View.VISIBLE);
                } else {
                    footView.setVisibility(View.GONE);
                }
            }

            @Override
            public void loadMore(final LoadMoreScrollListener listener) {
                if (manager.isLoadingMore() || adapter.isEditMode) {
                    listener.endLoading();
                    return;
                }
                progressBar.setVisibility(View.VISIBLE);
                loadMoreTextView.setVisibility(View.GONE);
                manager.loadCollect(false, new ILoadingListener() {
                    @Override
                    public void onSuccess() {
                        if (getActivity() == null) {
                            return;
                        }
                        List<Subject> list = manager.getCollectSubjects();
                        adapter.setItems(list);
                        adapter.notifyDataSetChanged();
                        listener.endLoading();
                    }

                    @Override
                    public void onFail(String failMsg) {
                        if (getActivity() == null) {
                            return;
                        }
                        listener.endLoading();
                        listener.setHasMore(false);
                        footView.setVisibility(View.VISIBLE);
                        progressBar.setVisibility(View.GONE);
                        if (NetResult.NOT_NETWORK_STRING.equals(failMsg)) {
                            loadMoreTextView.setText(R.string.loading_default_fail_text);
                            loadMoreTextView.setOnClickListener(new OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    listener.setHasMore(true);
                                    loadMore(listener);
                                }
                            });
                            return;
                        }
                        loadMoreTextView.setText(R.string.loading_more_no_data);
                    }
                });
            }

            @Override
            public boolean canLoadMore() {
                return adapter != null;
            }
        };
        listView.setOnScrollListener(loadMoreScrollListener);
    }

    private void clickItem(int position) {
        if (adapter.isEditMode) {
            return;
        }
        Bundle bundle = new Bundle();
        if (manager == null || manager.getCollectSubjects() == null) {
            return;
        }
        bundle.putInt(H5SubjectDetailFragment.ID, manager.getCollectSubjects().get(position).id);
        bundle.putInt(H5SubjectDetailFragment.POS, position);
        bundle.putInt(H5SubjectDetailFragment.LIKE_NUMBER, manager.getCollectSubjects().get(position).hotindex);
        LaunchUtil.launchSubDetailForResult(LaunchUtil.COLLECT_SUB_REQUEST_CODE, getActivity(),
                BaseSwitchActivity.class, H5SubjectDetailFragment.class, bundle);
    }

    private void changeShowMode() {
        String edit = getResources().getString(R.string.edit);
        if (edit.equals(operateTextView.getText().toString())) {
            operateTextView.setText(R.string.cancel);
            deleteTextView.setAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.bottom_out));
            deleteTextView.setVisibility(View.VISIBLE);
        } else {
            operateTextView.setText(edit);
            deleteTextView.setAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.bottom_in));
            deleteTextView.setVisibility(View.GONE);
        }
        adapter.changeMode();
        adapter.notifyDataSetChanged();
    }

    private void changeDelTextViewBackColor() {
        if (adapter.getIntArray().size() > 0) {
            deleteTextView.setBackgroundColor(getActivity().getResources().getColor(R.color.red));
        } else {
            deleteTextView.setBackgroundColor(getActivity().getResources().getColor(R.color.light_red));
        }
    }

    private void initData() {
        listView.setVisibility(View.GONE);
        launchHelper();
        if (loadingHelper != null) {
            loadingHelper.start();
        }
        loadList(true);
    }

    private void launchHelper() {
        if (loadingHelper == null) {
            loadingHelper = LoadingViewHelper.generateOnParentAtPosition(0, (ViewGroup) root, 0);
        }
    }

    private void loadList(boolean isFirst) {
        manager.loadCollect(isFirst, new ILoadingListener() {
            @SuppressWarnings("null")
            @Override
            public void onSuccess() {
                if (getActivity() == null) {
                    return;
                }
                subjects = manager.getCollectSubjects();
                adapter.setItems(subjects);
                adapter.notifyDataSetChanged();
                if (subjects != null || subjects.size() > 0) {
                    listView.setVisibility(View.VISIBLE);
                    if (((CollectFragment) getParentFragment()).getCurrentPageIndex() == CollectFragment.COLLECT_SUBJECT) {
                        operateTextView.setVisibility(View.VISIBLE);
                    }
                } else {
                    if (((CollectFragment) getParentFragment()).getCurrentPageIndex() == CollectFragment.COLLECT_SUBJECT) {
                        operateTextView.setVisibility(View.GONE);
                    }
                }
                if (loadingHelper != null) {
                    loadingHelper.end();
                    loadingHelper = null;
                }
            }

            @Override
            public void onFail(String failMsg) {
                if (getActivity() == null) {
                    return;
                }
                showFailMsg(failMsg);
            }
        });
    }

    private void showFailMsg(String failMsg) {
        if (loadingHelper == null) {
            return;
        }
        if (NetResult.NOT_DATA_STRING.equals(failMsg)) {
            showNoData();
            return;
        }
        showNoNetWork();
        footView.setVisibility(View.GONE);
        operateTextView.setVisibility(View.GONE);
    }

    private void showNoNetWork() {
        loadingHelper.showNoNetFailView(new OnClickListener() {
            @Override
            public void onClick(View v) {
                initData();
            }
        });
    }

    private void showNoData() {
        loadingHelper.showCollectEmptyFailView(null);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == H5SubjectDetailFragment.RESULT_CODE && data != null) {
            int pos = data.getExtras().getInt(H5SubjectDetailFragment.POS);
            if (data.getExtras().getBoolean(H5SubjectDetailFragment.ISCANCEL)) {// 取消收藏
                if (pos < subjects.size()) {// 处理为0的情况
                    subjects.remove(pos);
                    adapter.setItems(subjects);
                    adapter.notifyDataSetChanged();
                    if (subjects.size() == 0) {
                        launchHelper();
                        showNoData();
                        return;
                    }
                }
            } else {
                subjects.get(pos).hotindex = data.getExtras().getInt(H5SubjectDetailFragment.LIKE_NUMBER);
                adapter.setItems(subjects);
                adapter.notifyDataSetChanged();
            }
        }
    }
}
