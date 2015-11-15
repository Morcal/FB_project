package com.feibo.snacks.view.widget.loadingview;

import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.feibo.snacks.R;
import com.feibo.snacks.model.bean.NetResult;
import com.feibo.snacks.manager.ILoadMoreView;
import com.feibo.snacks.view.base.LoadMoreScrollListener;

/**
*
* @author hcy
*
* 可以自动添加footerView,样式为目前全局的加载更多样式。
*/
public abstract class LoadingMoreView4List extends AbsLoadingView implements ILoadMoreView{

    private ListView listView;
    private View footerView;
    private TextView loadMoreTextView;
    private ProgressBar progressBar;

    public LoadingMoreView4List(final ListView listView) {
        super(listView);
        this.listView = listView;
        setFootView();
    }

    @Override
    public void showLoadMoreView() {
        progressBar.setVisibility(View.VISIBLE);
        loadMoreTextView.setVisibility(View.GONE);
    }

    @Override
    public void showLoadingView() {
        super.showLoadingView();
        footerView.setVisibility(View.GONE);
    }


    @Override
    public void hideLoadingView() {
        super.hideLoadingView();
        footerView.setVisibility(View.VISIBLE);
    }

    //TODO
    @Override
    public void hideLoadMoreView(final LoadMoreScrollListener listener, String failMsg) {
        progressBar.setVisibility(View.GONE);
        loadMoreTextView.setVisibility(View.VISIBLE);
        loadMoreTextView.setText(loadMoreTextView.getResources().getString(R.string.loading_more_no_data));
        if (NetResult.NOT_NETWORK_STRING.equals(failMsg)) {
            loadMoreTextView.setText(loadMoreTextView.getResources().getString(R.string.loading_default_fail_text));
            loadMoreTextView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        listener.setHasMore(true);
                    }
                    loadMoreData(listener);
                }
            });
        }
    }

    public void hideLoadMoreNoFootview(final LoadMoreScrollListener listener, String failMsg) {
        progressBar.setVisibility(View.GONE);
        loadMoreTextView.setVisibility(View.GONE);
        if (NetResult.NOT_NETWORK_STRING.equals(failMsg)) {
            loadMoreTextView.setText(loadMoreTextView.getResources().getString(R.string.loading_default_fail_text));
            loadMoreTextView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        listener.setHasMore(true);
                    }
                    loadMoreData(listener);
                }
            });
        }
    }

    public void hideLoadMoreView4Orders(final LoadMoreScrollListener listener, String failMsg) {
        progressBar.setVisibility(View.GONE);
        loadMoreTextView.setVisibility(View.VISIBLE);
    }

    @Override
    public void setFootView() {
        footerView = LayoutInflater.from(listView.getContext()).inflate(R.layout.layout_load_more, null);
        loadMoreTextView = (TextView) footerView.findViewById(R.id.load_more_text);
        progressBar = (ProgressBar) footerView.findViewById(R.id.pull_to_refresh_progress);
        listView.addFooterView(footerView);
    }

    public TextView getLoadMoreTextView() {
        return loadMoreTextView;
    }
}
