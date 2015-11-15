package com.feibo.snacks.view.base;

import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;

public abstract class LoadMoreScrollListener implements OnScrollListener {

	private static final String TAG = LoadMoreScrollListener.class.getSimpleName();
	private boolean isLoadingMore = false;
	private boolean hasMore = true;

    private Object mLoadingLock = new Object();

	public LoadMoreScrollListener() {

	}

	private boolean isLoadingMore() {
		synchronized (mLoadingLock) {
			return isLoadingMore;
		}
	}

	private void setLoadingMore(boolean value) {
		synchronized (mLoadingLock) {
			this.isLoadingMore = value;
		}
	}

	public void endLoading() {
		setLoadingMore(false);
	}

	public void setHasMore(boolean hasMore) {
		this.hasMore = hasMore;
	}

	private boolean hasMore() {
		return hasMore;
	}

	/**
	 * 是否可以开始尝试加载更多.
	 * @return
	 */
	public abstract boolean canLoadMore();

	public abstract void firstVisibleItem(int position);

	/**
	 * 执行加载更多过程，操作结束务必设置加载结束标记.
	 * @param listener
	 */
	public abstract void loadMore(LoadMoreScrollListener listener);

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {

	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {
	    firstVisibleItem(firstVisibleItem);
		if (!hasMore() || !canLoadMore()) {
			return;
		}
		//没有正在加载，且已经到达最后一项可见
		if (!isLoadingMore() && (firstVisibleItem + visibleItemCount >= totalItemCount || visibleItemCount >= totalItemCount)) {
			setLoadingMore(true);//设置正在加载
			loadMore(this);//执行加载
		}
	}
}