package com.feibo.joke.view.widget.pullToRefresh;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ListView;
import android.widget.TextView;

import com.feibo.joke.R;
import com.feibo.joke.utils.StringUtil;

public class PullToRefreshLoadmoreListView extends PullToRefreshListView {

    /** 一开始的状态， 为不可见状态 **/
    public static final int STATU_LOAD_MORE_DEFAULT = 0;
    /** 开始加载数据 */
    public static final int STATU_LOAD_MORE_START = 1;
    /** 加载成功，但是还是有数据（还有下一页） **/
    public static final int STATU_LOAD_MORE_SUCCESS = 2;
    /** 加载成功，但是没有更多数据了 **/
    public static final int STATU_LOAD_MORE_NO_DATA = 3;
    /** 加载数据失败(比如网络原因，服务器无返回值或者返回值错误) */
    public static final int STATU_LOAD_MORE_FALIUR = 4;
    
    private ListView listView;
    
    private View footerView;
    private TextView loadMoreText;
    private View progress;
    
    private boolean loadMoreEnable = true;
    private int loadMoreStatu = -1;
    
    private String loadMoreOverText;
    
    /** 使用自定义的脚 */
    private boolean useDefaultFooterView = false; 
    
    private boolean nomoreDataFlag;
    
    private final int ROTATE_ANIM_DURATION = 500;
    private Animation rotateAnim;
    
    public PullToRefreshLoadmoreListView(Context context) {
        super(context);
        initAnimation();
    }

    public PullToRefreshLoadmoreListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initAnimation();
    }

    public PullToRefreshLoadmoreListView(Context context, Mode mode) {
        super(context, mode);
        initAnimation();
    }

    public PullToRefreshLoadmoreListView(Context context, Mode mode, AnimationStyle style) {
        super(context, mode, style);
        initAnimation();
    }

    private void initAnimation() {
        nomoreDataFlag = false;
        rotateAnim = new RotateAnimation(0, 359, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        rotateAnim.setDuration(ROTATE_ANIM_DURATION);
        rotateAnim.setRepeatCount(-1);
        rotateAnim.setInterpolator(new LinearInterpolator());// 设置匀速，无加速度不卡顿
        rotateAnim.setRepeatMode(Animation.RESTART);
    }
    
    public void setRefreshLoadMoreEnable(boolean refreshEnable, boolean loadMoreEnable) {
        this.loadMoreEnable = loadMoreEnable;
        if (!refreshEnable) {
            this.setMode(Mode.DISABLED); // 不开启下拉刷新
        }
    }
    
    public void initView(View headView, View footView) {
        this.footerView = footView;
        listView = this.getRefreshableView();
        if(headView != null){
            listView.addHeaderView(headView);
        }
        if(footView != null) {
            useDefaultFooterView = true;
            listView.addFooterView(footView);
        } else if(loadMoreEnable) {
            listView.addFooterView(initFooterView());
            setLoadMoreSatu(STATU_LOAD_MORE_DEFAULT);
        }
    }
    
    public boolean isNoMoreDataStatus() {
        return nomoreDataFlag;
    }
    
    /** 恢复下拉刷新 */
    public void setLoadMoreStatus() {
        nomoreDataFlag = false;
    }
    
    public void setLoadMoreSatu(int statu) {
        if(loadMoreStatu == statu || useDefaultFooterView) {
            return;
        }
        footerView.setVisibility(View.VISIBLE);
        if(!loadMoreEnable) {
            statu = STATU_LOAD_MORE_NO_DATA;
        }
        switch (statu) {
        case STATU_LOAD_MORE_DEFAULT:
            progress.clearAnimation();
            footerView.setVisibility(View.INVISIBLE);
            break;
        case STATU_LOAD_MORE_START:
            nomoreDataFlag = false;
            footerView.setVisibility(View.VISIBLE);
            progress.setVisibility(View.VISIBLE);
            progress.startAnimation(rotateAnim);
            loadMoreText.setVisibility(View.GONE);
            break;
        case STATU_LOAD_MORE_SUCCESS:
            progress.clearAnimation();
            footerView.setVisibility(View.VISIBLE);
            progress.setVisibility(View.GONE);
            loadMoreText.setText("加载数据成功");
            break;
        case STATU_LOAD_MORE_NO_DATA:
            nomoreDataFlag = true;
            progress.clearAnimation();
            progress.setVisibility(View.GONE);
            loadMoreText.setVisibility(View.VISIBLE);
            loadMoreText.setText(StringUtil.isEmpty(loadMoreOverText) ? "" : loadMoreOverText);
            break;
        case STATU_LOAD_MORE_FALIUR:
            footerView.setVisibility(View.VISIBLE);
            progress.setVisibility(View.GONE);
            loadMoreText.setText("加载数据失败[点击重试]");
            break;
        }
        loadMoreStatu = statu;
    }
    
    public int getLoadMoreStatu() {
        return loadMoreStatu;
    }

    private View initFooterView(){
        footerView = LayoutInflater.from(this.getContext()).inflate(R.layout.layout_load_more, null);
        loadMoreText = (TextView) footerView.findViewById(R.id.load_more_text);
        progress = footerView.findViewById(R.id.pull_to_refresh_progress);
        return footerView;
    }
    
    public void setFooterLoadMoreOverText(String loadMoreOverText) {
        this.loadMoreOverText = loadMoreOverText; 
    }
    
    public ListView getListView(){
        return listView;
    }
    
}
