package com.feibo.joke.view;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.feibo.joke.R;

public abstract class BaseTitleFragment extends BaseFragment {

    private LinearLayout root;
    private TitleBar titleBar;
    private View contentView;

    @Override
    public ViewGroup onCreateView(LayoutInflater inflater, Bundle savedInstanceState) {
        root = new LinearLayout(getActivity());
        root.setOrientation(LinearLayout.VERTICAL);
        root.setBackgroundColor(getResources().getColor(R.color.c9_white));
        addTitleBar();
        contentView = containChildView();
        setTitlebar();
        if (contentView != null) {
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT,
                    LayoutParams.MATCH_PARENT);
            contentView.setLayoutParams(params);
            attachToRoot(contentView);
        }
        return root;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        onReleaseView();
        if(root.getParent() != null) {
            ((ViewGroup) root.getParent()).removeView(root);
        }
        root = null;
        titleBar = null;
    }

    public void attachToRoot(View view) {
        if (view == null) {
            return;
        }
        if(view != contentView) {
            //目的是刷新UI
            root.removeView(contentView);
        }
        if (view.getParent() != null) {
            ((ViewGroup) view.getParent()).removeView(view);
        }
        root.addView(view);
    }

    private void addTitleBar() {
        if(setTitleLayoutId() == 0) {
            return;
        }
        root.removeAllViews();
        titleBar = new TitleBar(getActivity(), root, setTitleLayoutId());
        if(titleBar.leftPart != null) {
            titleBar.leftPart.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    onLeftBarClick();
                }
            });
        }
    }

    /**
     * titlebar点击返回事件
     */
    public void onLeftBarClick() {
        if(getActivity() == null) {
            return;
        }
        if(getActivity() instanceof BaseActivity) {
            ((BaseActivity)getActivity()).finish();
        } else {
            getActivity().finish();
        }
    }

    public TitleBar getTitleBar() {
        return titleBar;
    }

    public void postOnUiHandle(Runnable runnable) {
        if(getActivity() == null || runnable == null || !(getActivity() instanceof BaseActivity)) {
            return;
        }
        ((BaseActivity)getActivity()).postOnUiHandle(runnable);
    }
    
    public void postDelayed(Runnable run, int delayMillis) {
        if(getActivity() == null || run == null || !(getActivity() instanceof BaseActivity)) {
            return;
        }
        ((BaseActivity)getActivity()).postDelayed(run, delayMillis);
    }
    
    public void removeHandle(Runnable run) {
        if(getActivity() == null || run == null || !(getActivity() instanceof BaseActivity)) {
            return;
        }
        ((BaseActivity)getActivity()).removeHandle(run);
    }
    
    public abstract View containChildView();
    public abstract int setTitleLayoutId();
    public abstract void setTitlebar();
    public abstract void onReleaseView();

    public static class TitleBar {

        public View leftPart;
        public View rightPart;
        public View title;
        public View headView;

        public ImageView ivHeadLeft;
        public TextView tvHeadLeft;
        public ImageView ivHeadRight;
        public TextView tvHeadRight;
        private View bgLayout;

        public TitleBar(Context context, ViewGroup root, int id) {
            if (id == 0) {
                return;
            }

            headView = LayoutInflater.from(context).inflate(id, null);
            LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
            root.addView(headView, params);

            leftPart = headView.findViewById(R.id.head_left);
            rightPart = headView.findViewById(R.id.head_right);
            title = headView.findViewById(R.id.head_title);
            bgLayout = headView.findViewById(R.id.title_layout);

            if(id==R.layout.base_titlebar || id==R.layout.base_titlebar_video){
                ivHeadLeft=(ImageView)headView.findViewById(R.id.iv_head_left);
                tvHeadLeft=(TextView)headView.findViewById(R.id.tv_head_left);
                ivHeadRight=(ImageView)headView.findViewById(R.id.iv_head_right);
                tvHeadRight=(TextView)headView.findViewById(R.id.tv_head_right);
            }
        }
        
        public void setBackgroudColor(int color) {
            bgLayout.setBackgroundColor(color);
        }
        
        public void setTitleHeight(int height) {
            LayoutParams lp = bgLayout.getLayoutParams();
            lp.height = height;
            bgLayout.setLayoutParams(lp);
        }

    }
}
