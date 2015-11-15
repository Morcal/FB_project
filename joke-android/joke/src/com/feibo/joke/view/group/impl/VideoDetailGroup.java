package com.feibo.joke.view.group.impl;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;

import com.feibo.joke.R;
import com.feibo.joke.dao.ReturnCode;
import com.feibo.joke.manager.LoadListener;
import com.feibo.joke.manager.detail.VideoDetailManager;
import com.feibo.joke.manager.list.CommentsVideoManager;
import com.feibo.joke.model.Comment;
import com.feibo.joke.view.group.BasePullListGroup;
import com.feibo.joke.view.widget.pullToRefresh.PullToRefreshLoadmoreListView;



public abstract class VideoDetailGroup extends BasePullListGroup<Comment> {

    private View mHeadView;
    private VideoDetailManager videoDetailManager;
    private IVideoDetailLoadListener listener;   
    
    private Boolean isFirst=true;//刷新时，用于判断是不是第一次加载
    private Boolean isSuccess=false;//视频详情是否已经获取成功
    
    private boolean videoIsDelete;
    
    private OnItemClickListener onItemClickListener;
    
    public VideoDetailGroup(Context context) {
        super(context,false,true);
    }
    
    public void setLoadListener(IVideoDetailLoadListener listener){
        this.listener=listener;
    }
    
    @Override
    public ViewGroup containChildView() {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        ViewGroup parent = (ViewGroup) inflater.inflate(R.layout.video_detail_comments, null);
        return parent;
    }
    
    @Override
    public void initListener() {
        super.initListener();
        initListListener(pullToRefreshListView);
        pullToRefreshListView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(position - 1 < 0) {
                    return;
                }
                if(getListAdapter().getCount() + 2 <= position) {
                    return;
                }
                if(onItemClickListener != null) {
                    onItemClickListener.onItemClick(parent, view, position - 1, id);
                } 
            }
        });
    }
    
    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }
    
    public abstract void initListListener(PullToRefreshLoadmoreListView pullToRefreshListView);
    
    public ListView getListView(){
        return pullToRefreshListView.getListView();
    }
    
    @Override
    public View initHeaderView() {
        mHeadView = View.inflate(getContext(), R.layout.video_detail_up, null);
        return mHeadView;
    }
    
    public View getHeadView(){
        return mHeadView;
    }
    
    public void setListManager(CommentsVideoManager listManager,VideoDetailManager videoDetailManager) {
        // TODO Auto-generated method stub
        this.videoDetailManager=videoDetailManager;
        super.setListManager(listManager);
    }
    
    @Override
    public void refreshData() {
        if(isFirst){
            videoDetailManager.readVideoDetail(new LoadListener() {
                
                @Override
                public void onSuccess() {
                    isSuccess=true;
                    isFirst=false;
                    listener.onDetailSuccess();
                    hideRefreshLoading();
                    showView(true);
                    loadComment();
                }
                
                @Override
                public void onFail(int code) {
                    listener.onDetailFail(code);
                    hideRefreshLoading();
                    if (code == ReturnCode.RS_EMPTY_ERROR || code == ReturnCode.NO_NET) {
                        if(code == ReturnCode.RS_EMPTY_ERROR) {
                            videoIsDelete = true;
                        }
                        showFailMessage(code);
                    }else if(code == ReturnCode.RS_VIDEO_HAS_REPORT){
                        showFailMessage(code,"这个片儿被举报了，正在受理");
                    }
                    isSuccess=false;
                }
            });
        }
    }
    
    public boolean getVideoIsDelete() {
        return videoIsDelete;
    }
    
    @Override
    public void showView(boolean isRefresh) {
        getListAdapter().setItems(getListManager().getDatas());
        getListAdapter().notifyDataSetChanged();

//        if (getListAdapter() != null && getListAdapter().getCount() > 0) {
            hideLoadHelper();
//        }
    }
    
    public interface IVideoDetailLoadListener {
        void onDetailSuccess();
        void onDetailFail(int code);
        void onCommentsSuccess();
        void onCommentsFail(int code);
    }
    
    private void loadComment(){
        getListManager().refresh(new LoadListener() {
            @Override
            public void onSuccess() {
                listener.onCommentsSuccess();
            }

            @Override
            public void onFail(int code) {
                listener.onCommentsFail(code);
            }
        });
    }
}
