package com.feibo.joke.view.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.feibo.joke.view.widget.waterpull.CListView;
/**
 * 顶部View滚动拉伸或隐藏，
 * 继承自LinearLayout，
 * 需通过setHeadView方法传入相应参数。
 *
 * com.feibo.joke.view.widget.ScrollHeadView
 * @author LinMW<br/>
 * Creat at2015-4-10 上午10:10:36
 */
public class ScrollHeadView extends LinearLayout {

    private ViewGroup headView;
    private ImageView maxImg;
    private CListView cListView;  //CListView可替换为ListView
    private ShowMode showMode = ShowMode.SHOW;
    private int mHeaderViewHeight;
    private int imgDefaultHeight;

    private enum ShowMode {
        SHOW, MID, HIDE
    }

    public ScrollHeadView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public ScrollHeadView(Context context) {
        super(context);
        initView(context);
    }

    private void initView(Context context) {
         //待扩展
    }

    /**
     *
     * @param headView   需隐藏ViewGroup
     * @param maxImg   背景图
     * @param cListView 瀑布流列表对象，必要时可以改为ListView
     */
    public void setHeadView(ViewGroup headView, ImageView maxImg, CListView cListView) {
        this.headView = headView;
        this.maxImg = maxImg;
        this.cListView = cListView;
        headView.measure(0, 0);
        mHeaderViewHeight = headView.getMeasuredHeight();
        imgDefaultHeight = maxImg.getMeasuredHeight();
    }

    int startY;
    private int firstVisiblePosition = 0;
    private int itemTop = 0;
    private int paddingTop;
    private int newImgHeight;
    private boolean isStopUp;//是否拦截点击事件

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (cListView != null && cListView.getChildCount() > 0) {
            firstVisiblePosition = cListView.getFirstVisiblePosition();
            itemTop = cListView.getChildAt(0).getTop();
        }

        switch (ev.getAction()) {

        case MotionEvent.ACTION_DOWN:
            startY = (int) ev.getRawY();
            isStopUp = false;
            break;

        case MotionEvent.ACTION_MOVE:
            int newY = (int) ev.getRawY();
            int dY = newY - startY;

            if (firstVisiblePosition == 0 && itemTop >= 0) {
                //判断是否只是单纯的点击事件，避免点击时的轻微滑动
                if (Math.abs(dY) > 2) {
                    isStopUp = true;
                }

                paddingTop = headView.getPaddingTop();
                paddingTop = paddingTop + dY;

                if (paddingTop < 0 && paddingTop > -mHeaderViewHeight) {
                    headView.setPadding(0, paddingTop, 0, 0);
                    startY = newY;
                    showMode = ShowMode.MID;
                    return true;
                } else if (showMode == ShowMode.MID && paddingTop <= -mHeaderViewHeight && dY < 0) {
                    hideHead();
                    return true;
                } else if (showMode == ShowMode.MID && paddingTop >= 0 && dY > 0) {
                    showHead();
                    return true;
                } else if (showMode == ShowMode.SHOW && dY > 0) {
                    newImgHeight = imgDefaultHeight + dY / 3;
                    setMaxImgHeight(newImgHeight);
                    return true;
                } else if (showMode != ShowMode.HIDE) {
                    return true;
                }

            }

            startY = newY;

            break;

        case MotionEvent.ACTION_UP:
            if (imgDefaultHeight != maxImg.getHeight()) {
                setMaxImgHeight(imgDefaultHeight);
                return true;
            } else if (isStopUp) {
                return true;
            }
            break;
        //避免系统自动取消滑动时的BUG
        case MotionEvent.ACTION_CANCEL:
            if (imgDefaultHeight != maxImg.getHeight()) {
                setMaxImgHeight(imgDefaultHeight);
                return true;
            } else if (isStopUp) {
                return true;
            }
            break;

        default:
            break;
        }

        return super.dispatchTouchEvent(ev);
    }

    private void setMaxImgHeight(int height) {
        if (height == 0) {
            return;
        }
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
                height);
        maxImg.setLayoutParams(params);
    }

    private void showHead() {
        paddingTop = 0;
        headView.setPadding(0, paddingTop, 0, 0);
        showMode = ShowMode.SHOW;
    }

    private void hideHead() {
        paddingTop = -mHeaderViewHeight;
        headView.setPadding(0, paddingTop, 0, 0);
        showMode = ShowMode.HIDE;
    }

}
