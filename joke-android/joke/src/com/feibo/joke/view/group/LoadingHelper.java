package com.feibo.joke.view.group;

import android.annotation.SuppressLint;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.feibo.joke.R;
import com.feibo.joke.utils.StringUtil;

@SuppressLint("InflateParams")
public class LoadingHelper {
    private View group;
    private View loadingLayout;
    private View failLayout;
    private TextView failImgText;
    private TextView failBtn;
    private ImageView animLoaing;
    private ImageView failLeftImg;
    private TextView failTopText;
    
    private LoadingHelper(View loadingGroup) {
        this.group = loadingGroup;
        loadingLayout = loadingGroup.findViewById(R.id.layout_loading);
        failLayout = loadingGroup.findViewById(R.id.layout_fail);
        animLoaing = (ImageView) loadingLayout.findViewById(R.id.loading_img);
        failImgText = (TextView) loadingGroup.findViewById(R.id.fail_text_img);
        failBtn = (TextView) loadingGroup.findViewById(R.id.fail_btn);
        failLeftImg = (ImageView) loadingGroup.findViewById(R.id.img_left);
        failTopText = (TextView) loadingGroup.findViewById(R.id.fail_msg_center);
    }

    public static LoadingHelper generateOnParent(ViewGroup parent) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.loading_help, null);
        parent.addView(view, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));
        return new LoadingHelper(view);
    }
    
    public ViewGroup getLoadingRootView() {
        return (ViewGroup) group;
    }

    public static LoadingHelper generateOnParentAtPosition(ViewGroup parent, int position) {
        if (parent == null) {
            return null;
        }
        if (position < 0) {
            position = 0;
        }
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.loading_help, null);
        parent.addView(view, position, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));
        return new LoadingHelper(view);
    }
    
    public void setBackgroupColor(int color) {
        this.group.setBackgroundColor(color);
    }
    
    public void setLoadingDesc(String loadingDesc) {
        ((TextView)loadingLayout.findViewById(R.id.loading_text)).setText(loadingDesc);
    }

    public void start() {
        group.setVisibility(View.VISIBLE);
        loadingLayout.setVisibility(View.VISIBLE);
        failLayout.setVisibility(View.GONE);
        animLoaing.setBackgroundResource(R.drawable.bg_loading);
        AnimationDrawable animation = (AnimationDrawable) animLoaing.getBackground();
        animation.start();
        group.setOnClickListener(null);
    }

    public void end() {
        group.setVisibility(View.GONE);
        AnimationDrawable animation = (AnimationDrawable) animLoaing.getBackground();
        animation.stop();
    }
    
    /**
     * 当前是否处于正在加载中
     * @return
     */
    public boolean isShowLoading() {
        return group.getVisibility() == View.VISIBLE;
    }

    /*
     * 加载失败和无数据的显示图片都不一样，所以需要设置图片
     */
    public void fail(String msg, int img, OnClickListener clickListener) {
        fail(msg,null,img,clickListener);
    }

    public void fail(String msg, String btnMsg, int img, OnClickListener clickListener) {
        fail(msg, btnMsg, img, clickListener, null);
    }
    
    public void noNetFail(OnClickListener clickListener) {
        group.setVisibility(View.VISIBLE);
        loadingLayout.setVisibility(View.GONE);
        failLayout.setVisibility(View.VISIBLE);
        Drawable drawable = group.getResources().getDrawable(R.drawable.icon_loading_fail);
        drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
        failImgText.setCompoundDrawablesWithIntrinsicBounds(null, drawable, null, null);
//        failImgText.setText(group.getResources().getString(R.string.not_network));
        failImgText.setText("啊哦 ~ 无网络");
        failBtn.setVisibility(View.GONE);
        if (clickListener != null) {
            group.setOnClickListener(clickListener);
        }
    }
    
    /**
     *
     * @param msg  图片下的文字，空则显示默认
     * @param btnMsg  按钮文字，空则按钮不显示
     * @param img   图片，空则显示默认
     * @param clickListener
     */
    public void fail(String msg, String btnMsg, int img, OnClickListener clickListener, final OnFaiClickListener failListener) {
        group.setVisibility(View.VISIBLE);
        loadingLayout.setVisibility(View.GONE);
        failLayout.setVisibility(View.VISIBLE);
        if (img == 0 ) {
            //不进行操作，显示默认图片
        }else if (img == R.drawable.icon_not_attention) {
            //贵圈无关注特殊布局
            failLeftImg.setVisibility(View.VISIBLE);
            failTopText.setVisibility(View.VISIBLE);
            failImgText.setVisibility(View.GONE);
        } else {
            Drawable drawable = group.getResources().getDrawable(img);
            drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
            failImgText.setCompoundDrawables(null, drawable, null, null);
        }
        
        if (msg == null || msg.length() == 0) {
            msg = failTopText.getResources().getString(R.string.loading_default_fail_text);
        } else {
            if(img == R.drawable.icon_not_attention) {
                failTopText.setText(msg);
            } else {
                failImgText.setText(msg);
            }
        }
        
        if (StringUtil.isEmpty(btnMsg)) {
//            failView2.setText(failView2.getResources().getString(R.string.loading_default_fail_text2));
            failBtn.setVisibility(View.GONE);
            if (clickListener != null) {
                group.setOnClickListener(clickListener);
            }
        } else {
            failBtn.setVisibility(View.VISIBLE);
            failBtn.setText(btnMsg);
            failBtn.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(failListener != null) {
                        failListener.onFailClick(0);
                    }
                }
            });
        }

    }
    
    public interface OnFaiClickListener {
        public void onFailClick(int failCode);
    }

}
