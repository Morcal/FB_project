/**
 * @file XListView.java
 * @package me.maxwin.view
 * @create Mar 18, 2012 6:28:41 PM
 * @author Maxwin
 * @description An ListView support (a) Pull down to refresh, (b) Pull up to load more.
 * 		Implement IXListViewListener, and see stopRefresh() / stopLoadMore().
 */
package com.feibo.joke.view.widget.waterpull;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.animation.DecelerateInterpolator;
import android.widget.ListAdapter;
import android.widget.RelativeLayout;
import android.widget.Scroller;
import android.widget.TextView;

import com.feibo.joke.R;
import com.feibo.joke.app.AppContext;
import com.feibo.joke.view.widget.waterpull.XListViewHeader.INoNetListener;
import com.feibo.joke.view.widget.waterpull.lib.MultiColumnListView;
import com.feibo.joke.view.widget.waterpull.lib.internal.PLA_AbsListView;
import com.feibo.joke.view.widget.waterpull.lib.internal.PLA_AbsListView.OnScrollListener;

public abstract class XListView extends MultiColumnListView implements OnScrollListener {
    
	private float mLastY = -1; // save event y
	private Scroller mScroller; // used for scroll back
	private OnScrollListener mScrollListener; // user's scroll listener

	// the interface to trigger refresh and load more.
	private IXListViewListener mListViewListener;

	// -- header view
	private XListViewHeader mHeaderView;
	// header view content, use it to calculate the Header's height. And hide it
	// when disable pull refresh.
	private RelativeLayout mHeaderViewContent;
	private TextView mHeaderTimeView;
	private int mHeaderViewHeight; // header view's height
	private boolean mEnablePullRefresh = true;
	private boolean mPullRefreshing = false; // is refreashing.

	// -- footer view
	private XListViewFooter mFooterView;
	private boolean mEnablePullLoad;
	private boolean mPullLoading;
	private boolean mIsFooterReady = false;

	// total list items, used to detect is at the bottom of listview.
	private int mTotalItemCount;

	// for mScroller, scroll back from header or footer.
	private int mScrollBack;
	private final static int SCROLLBACK_HEADER = 0;
	private final static int SCROLLBACK_FOOTER = 1;

	private final static int SCROLL_DURATION = 400; // scroll back duration
	private final static int PULL_LOAD_MORE_DELTA = 50; // when pull up >= 50px
														// at bottom, trigger
														// load more.
	private final static float OFFSET_RADIO = 1.8f; // support iOS like pull
													// feature.
	private final static float OFFSET_TOP_MARGIN_RATIO = 0.7f; //个人主页头部滑动速度调整

	private boolean loadMoreOverFlag = false;

	/** 是否是自定义的头  */
    protected boolean isCustomHeaderView = true;

    /** 头部可见高度 */
    private int headerVisibleHeight;
    
	/**
	 * @param context
	 */
	public XListView(Context context) {
		super(context);
		initWithContext(null, context);
	}

	public XListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initWithContext(attrs,context);
	}

	public XListView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initWithContext(attrs,context);
	}

    private void initWithContext(AttributeSet attrs, Context context) {
        TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.XListView, 0, 0);
//        refreshAble = a.getBoolean(R.styleable.CListView_refreshEnable, true);
//        loadMoreAble = a.getBoolean(R.styleable.CListView_loadMoreEnable, true);
        isCustomHeaderView = a.getBoolean(R.styleable.XListView_isCustomHeaderView, false);
        float mZoomRatio = a.getFloat(R.styleable.XListView_zoomRatio, 1);
        final int headerLayout = a.getResourceId(R.styleable.XListView_headerLayout, 0);
        a.recycle();

		mScroller = new Scroller(context, new DecelerateInterpolator());
		// XListView need the scroll event, and it will dispatch the event to
		// user's listener (as a proxy).
		super.setOnScrollListener(this);

		// initVideo header view
		mHeaderView = new XListViewHeader(context, mZoomRatio) {
            @Override
            public int getHeaderView() {
                return headerLayout == 0 ? XListView.this.getHeaderView() : headerLayout;
            }
        };
		mHeaderViewContent = (RelativeLayout) mHeaderView.findViewById(R.id.xlistview_header_content);
//		mHeaderTimeView = (TextView) mHeaderView.findViewById(R.id.xlistview_header_time);
		addHeaderView(mHeaderView);

		// initVideo footer view
		mFooterView = new XListViewFooter(context){
            @Override
            public int getFooterLayout() {
                return XListView.this.getFooterLayout();
            }
		};

		// initVideo header height
		mHeaderView.getViewTreeObserver().addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
			@Override
			public void onGlobalLayout() {
				mHeaderViewHeight = mHeaderViewContent.getHeight();
				getViewTreeObserver().removeGlobalOnLayoutListener(this);
			}
		});

	}

	@Override
	public void setAdapter(ListAdapter adapter) {
		// make sure XListViewFooter is the last footer view, and only add once.
		if (mIsFooterReady == false) {
			mIsFooterReady = true;
			addFooterView(mFooterView);
		}
		super.setAdapter(adapter);
	}

	/**
	 * enable or disable pull down refresh feature.
	 *
	 * @param enable
	 */
	public void setPullRefreshEnable(boolean enable) {
		mEnablePullRefresh = enable;
		if (!mEnablePullRefresh && !isCustomHeaderView) { // disable, hide the content
			mHeaderViewContent.setVisibility(View.INVISIBLE);
		} else {
			mHeaderViewContent.setVisibility(View.VISIBLE);
		}
	}

	/**
	 * enable or disable pull up load more feature.
	 *
	 * @param enable
	 */
	public void setPullLoadEnable(boolean enable) {
		mEnablePullLoad = enable;
		if (!mEnablePullLoad) {
			mFooterView.hide();
			mFooterView.setOnClickListener(null);
		} else {
			mPullLoading = false;
			mFooterView.show();
			mFooterView.setState(XListViewFooter.STATE_NORMAL);
			// both "pull up" and "click" will invoke load more.
			mFooterView.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					startLoadMore();
				}
			});
		}
	}

	/**
	 * stop refresh, reset header view.
	 */
	public void stopRefresh() {
		if (mPullRefreshing == true) {
			mPullRefreshing = false;
			resetHeaderHeight();
		}
	}

	public boolean isRefreshing() {
	    return mPullRefreshing;
	}

	/**
	 * stop load more, reset footer view.
	 */
	public void stopLoadMore(boolean end) {
		if (mPullLoading == true) {
			mPullLoading = false;
			if(end) {
			    loadMoreOverFlag = true;
			    mFooterView.setState(XListViewFooter.STATE_LOADING_OVER);
			} else {
			    mFooterView.setState(XListViewFooter.STATE_NORMAL);
			}
		}
	}

	/**
	 * set last refresh time
	 *
	 * @param time
	 */
	public void setRefreshTime(String time) {
		mHeaderTimeView.setText(time);
	}

	private void invokeOnScrolling() {
		if (mScrollListener instanceof OnXScrollListener) {
			OnXScrollListener l = (OnXScrollListener) mScrollListener;
			l.onXScrolling(this);
		}
	}

	private void setHeaderHight(float hight) {
	    if(isCustomHeaderView) {
	        mHeaderView.setImageViewHeight(-(int)hight);
	    } else if(mEnablePullRefresh) {
	        float delta = hight / OFFSET_RADIO;
	        int height = (int) delta + headerVisibleHeight;
	        mHeaderView.setVisiableHeight(false, height);
	        headerVisibleHeight = height;
	    }
	    setSelection(0); // scroll to top each time
	}
	private void updateHeaderOnTouch(float deltaY) {
	    if(isCustomHeaderView) {
	        mHeaderView.setImageViewHeight(-(int)deltaY);
        } else if(mEnablePullRefresh) {
            float delta = deltaY / OFFSET_RADIO;
            int height = (int) delta + headerVisibleHeight;
            mHeaderView.setVisiableHeight(false, height);
            headerVisibleHeight = height;
            if(!isCustomHeaderView) {
                if (mEnablePullRefresh && !mPullRefreshing) { // 未处于刷新状态，更新箭头
                    if (mHeaderView.getVisiableHeight() > mHeaderViewHeight) {
                        mHeaderView.setState(XListViewHeader.STATE_READY);
                    } else {
                        mHeaderView.setState(XListViewHeader.STATE_NORMAL);
                    }
                }
            }
        }
        setSelection(0); // scroll to top each time
	}

	public void showRefresh(){
	    mHeaderView.setState(XListViewHeader.STATE_READY);
	    mHeaderView.showRefreshUI();
	    int newHeadHight=mHeaderViewHeight+10;
	    setHeaderHight(newHeadHight);
//	    invokeOnScrolling();
	    mPullRefreshing = true;
        loadMoreOverFlag = false;
//	    resetHeaderHeight();
//	    MotionEvent ev=MotionEvent.obtain(0, 0, MotionEvent.ACTION_UP, 0, 0, 0);
//	    onTouchEvent(ev);
        mScrollBack = SCROLLBACK_HEADER;
        mScroller.startScroll(0, newHeadHight, 0, mHeaderViewHeight - newHeadHight, SCROLL_DURATION);
        // trigger computeScroll
        invalidate();
        computeScroll();
	}
	
	private void resetHeaderHeight() {
		if (headerVisibleHeight == 0) {
            return;
        }
		// refreshing and header isn't shown fully. do nothing.
		if (mPullRefreshing && headerVisibleHeight <= mHeaderViewHeight) {
			return;
		}
		int finalHeight = 0; // default: scroll back to dismiss header.
		// is refreshing, just scroll back to show all the header.
		if (mPullRefreshing && headerVisibleHeight > mHeaderViewHeight) {
			finalHeight = mHeaderViewHeight;
		}
		mScrollBack = SCROLLBACK_HEADER;
		mScroller.startScroll(0, headerVisibleHeight, 0, finalHeight - headerVisibleHeight, SCROLL_DURATION);
		// trigger computeScroll
		invalidate();
	}

	private void startLoadMore() {
	    if(mPullLoading) {
	        return;
	    }
		mPullLoading = true;
		mFooterView.setState(XListViewFooter.STATE_START_LOADING);
		if (mListViewListener != null) {
			mListViewListener.onListLoadMore();
		}
	}

	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		if (mLastY == -1) {
			mLastY = ev.getRawY();
		}
		switch (ev.getAction()) {
		case MotionEvent.ACTION_DOWN:
			mLastY = ev.getRawY();
			break;
		case MotionEvent.ACTION_MOVE:
			final float deltaY = ev.getRawY() - mLastY;
			mLastY = ev.getRawY();
			if (getFirstVisiblePosition() == 0 && (headerVisibleHeight > 0 || deltaY > 0)) {
				// the first item is showing, header has shown or pull down.
			    updateHeaderOnTouch(deltaY);
                invokeOnScrolling();
			} else if (getLastVisiblePosition() >= mTotalItemCount - 1) {
				// last item, already pulled up or want to pull up.
                
			}
			break;
        default:
			mLastY = -1; // reset
			if (getFirstVisiblePosition() == 0) {
				// invoke refresh
			    if(isCustomHeaderView) {
			        mHeaderView.setonTouchUp();
			        break;
			    }
				if (mEnablePullRefresh && headerVisibleHeight > mHeaderViewHeight) {
					mPullRefreshing = true;
					loadMoreOverFlag = false;
				}
				resetHeaderHeight();
			} else if (getLastVisiblePosition() == mTotalItemCount - 1) {
//				// invoke load more.
//				if (mEnablePullLoad && mFooterView.getBottomMargin() > PULL_LOAD_MORE_DELTA) {
//					startLoadMore();
//				}
//				resetFooterHeight();
			}
			break;
		}
		return super.onTouchEvent(ev);
	}

	@Override
	public void computeScroll() {
		if (mScroller.computeScrollOffset()) {
			if (mScrollBack == SCROLLBACK_HEADER) {
			    boolean isRefreshing = mHeaderView.getState() == XListViewHeader.STATE_REFRESHING;
			    boolean visibleHeightFlah = headerVisibleHeight - 1 <= mHeaderViewHeight;
			    if(mPullRefreshing && !isRefreshing && visibleHeightFlah) {
			        if(!AppContext.isNetworkAvailable()) {
	                    mHeaderView.setState(XListViewHeader.STATE_NO_NET, new INoNetListener() {
                            @Override
                            public void showNoNetAnimationFinish() {
                                stopRefresh();
                            }
                        });
			        } else {
                        mHeaderView.setState(XListViewHeader.STATE_REFRESHING);
                        if (mListViewListener != null) {
                            mListViewListener.onListRefresh();
                        }
			        }
			    }
			    mHeaderView.setVisiableHeight(false, mScroller.getCurrY());
			    headerVisibleHeight = mScroller.getCurrY();
			} else {
				mFooterView.setBottomMargin(mScroller.getCurrY());
			}
			postInvalidate();
			invokeOnScrolling();
		}
		super.computeScroll();
	}

	@Override
	public void setOnScrollListener(OnScrollListener l) {
		mScrollListener = l;
	}

	public void setXListViewListener(IXListViewListener l) {
		mListViewListener = l;
	}

	/**
	 * you can listen ListView.OnScrollListener or this one. it will invoke
	 * onXScrolling when header/footer scroll back.
	 */
	public interface OnXScrollListener extends OnScrollListener {
		public void onXScrolling(View view);
	}

	/**
	 * implements this interface to get refresh/load more event.
	 */
	public interface IXListViewListener {
		public void onListRefresh();

		public void onListLoadMore();
	}

	@Override
	public void onScrollStateChanged(PLA_AbsListView view, int scrollState) {
		if (mScrollListener != null) {
			mScrollListener.onScrollStateChanged(view, scrollState);
		}
	}

	private int lastFirstVisibleItem = -2;

	@Override
	public void onScroll(PLA_AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
		// send to user's listener
		mTotalItemCount = totalItemCount;
		if(lastFirstVisibleItem == firstVisibleItem) {
		    //防止重复调用加载更多
            return;
        }
		if(totalItemCount > 2 && firstVisibleItem + visibleItemCount == totalItemCount) {
	        if(loadMoreOverFlag) {
	            return;
	        }
	        lastFirstVisibleItem =firstVisibleItem;
	        if(visibleItemCount != totalItemCount) {
	            startLoadMore();
	        }
		}

		if (mScrollListener != null) {
			mScrollListener.onScroll(view, firstVisibleItem, visibleItemCount, totalItemCount);
		}
	}

    public abstract int getFooterLayout();
    public abstract int getHeaderView();

    public XListViewHeader getListHeaderView() {
        return mHeaderView;
    }

    public void setFooterLoadMoreOverText(String loadMoreOverText) {
        mFooterView.setFooterLoadMoreOverText(loadMoreOverText);
    }
}
