package com.feibo.joke.view.group;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;

import com.feibo.joke.R;
import com.feibo.joke.dao.ReturnCode;
import com.feibo.joke.utils.StringUtil;
import com.feibo.joke.view.group.LoadingHelper.OnFaiClickListener;

/**
 * 正在加载中，加载数据失败等提示信息组件
 * 
 * @author ml_bright
 * @version 2015-4-2 下午5:12:30
 */
public abstract class AbsLoadingGroup {

    /**
     * 当前页面的根布局
     */
    private ViewGroup root;
    
    private ViewGroup contentRoot;

    /**
     * 显示加载状态页面的帮助类
     */
    private LoadingHelper loadingHelper;

    /**
     * group组件的配置信息
     */
    private GroupConfig groupConfig;

    private MyHandler mHandle;

    public AbsLoadingGroup() {
        mHandle = new MyHandler(Looper.getMainLooper());
        groupConfig = new GroupConfig("", "", 0);
    }

    /**
     * 当进入当前页面后,去服务器获取数据的时候 , 显示加载状态的页面
     */
    public void launchLoadHelper() {
        launchLoadHelper(0, null);
    }
    
    public void launchLoadHelper(int color, String loadingDesc) {
        onLoadingHelpStateChange(true);
        if (loadingHelper == null) {
            loadingHelper = LoadingHelper.generateOnParentAtPosition(contentRoot, 0);
        }
        if(color != 0) {
            loadingHelper.setBackgroupColor(color);
        }
        loadingHelper.start();
        if(!StringUtil.isEmpty(loadingDesc)) {
            loadingHelper.setLoadingDesc(loadingDesc);
        }
    }
    
    /** 当前是否处于正在加载中的布局 */
    public boolean isShowLoading() {
        return loadingHelper == null ? false : loadingHelper.isShowLoading();
    }
    
    /** 得到loading也的根布局 */
    public ViewGroup getLoadingRootView() {
        return loadingHelper == null ? null : loadingHelper.getLoadingRootView();
    }

    /**
     * 当根布局内容填充完成后, 隐藏加载状态相关的页面
     */
    public void hideLoadHelper() {
        onLoadingHelpStateChange(false);
        if (loadingHelper != null) {
            loadingHelper.end();
            loadingHelper = null;
        }
    }
    
    public void showFailMessage(int code) {
        showFailMessage(code, groupConfig.message);
    }
    
    /**
     * 当从服务器获取数据失败的时候显示加载失败的 页面
     * 
     * @param msg
     * @param btnText
     * @param img
     */
    public void showFailMessage(int code, String message) {
        if (loadingHelper == null) {
            return;
        }
        int imgRes = groupConfig.imageRes;
        if(code == ReturnCode.NO_NET) {
            loadingHelper.noNetFail(noNetListener);
        } else {
            loadingHelper.fail(message, groupConfig.btnText, imgRes, noNetListener, new OnFaiClickListener() {
                @Override
                public void onFailClick(int failCode) {
                    onLoadingHelperFailButtonClick();
                }
            });
        }
    }
    
    private OnClickListener noNetListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            onLoadingHelperFailClick();
        }
    };
    
    public View getTopView() {
        return null;
    }
    
    public View getPopView() {
        return null;
    }

    public void setRoot(ViewGroup contentRoot) {
        this.contentRoot = contentRoot;
        if(contentRoot == null) {
            this.root = null;
            return;
        }
        root = (ViewGroup)LayoutInflater.from(contentRoot.getContext()).inflate(R.layout.group_layout, null);
        View top = getTopView();
        if(top != null) {
            LinearLayout topLayout = (LinearLayout) root.findViewById(R.id.top_layout);
            topLayout.addView(top, 0, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
        }
        LinearLayout contentLayout = (LinearLayout) root.findViewById(R.id.content_layout);
        contentLayout.addView(contentRoot, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        
        View popView = getPopView();
        if(popView != null) {
            root.addView(popView, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
        }
    }

    public ViewGroup getRoot() {
        return root;
    }
    
    public void setGroupConfig(GroupConfig config) {
        this.groupConfig = config;
    }

    public abstract void onLoadingHelpStateChange(boolean loadingHelpVisible);

    public abstract void onLoadingHelperFailClick();
    
    public void onLoadingHelperFailButtonClick() {}

    public abstract void onCreateView();

    public void onResetView(){}

    public void onDestroyView() {
        if(mHandle != null) {
            mHandle.removeCallbacksAndMessages(null);
            mHandle = null;
        }
    }
    
    public void onScollToTop(boolean refresh){}
    public void onDataChange(int code){}
    public void onDataChange(Message code){}
    
    public void sendUiMessage(int what, Object obj) {
        if(mHandle != null) {
            mHandle.sendMessage(mHandle.obtainMessage(what, obj));
        }
    }
    
    public void postOnUiThead(Runnable runnable) {
        if(mHandle != null) {
            mHandle.post(runnable);
        }
    }

    private class MyHandler extends Handler {
        
        public MyHandler(Looper mainLooper) {
            super(mainLooper);
        }

        @Override
        public void handleMessage(Message msg) {
            onDataChange(msg);
        }
    }
        
    
}
