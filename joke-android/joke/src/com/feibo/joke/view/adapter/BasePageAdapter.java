package com.feibo.joke.view.adapter;

import java.util.ArrayList;
import java.util.List;

import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import com.feibo.joke.view.group.AbsLoadingGroup;

public class BasePageAdapter extends PagerAdapter {

    private AbsLoadingGroup[] groups;

    // 为了达到可以强制刷新的效果
    private int mChildCount = 0;

    private int fleshItem;
    private boolean fleshFlag;
    
    private List<Integer> hasLoadingOverList;
    
    public BasePageAdapter() {
        hasLoadingOverList = new ArrayList<Integer>();
    }

    public void refreshItem(int fleshItem) {
        this.fleshItem = fleshItem;
        this.fleshFlag = true;
    }

    public void setGroups(AbsLoadingGroup[] groups) {
        this.groups = groups;
    }
    
    @Override
    public int getCount() {
        return groups.length;
    }

    @Override
    public void notifyDataSetChanged() {
        mChildCount = getCount();
        super.notifyDataSetChanged();
    }

    @Override
    public int getItemPosition(Object object) {
        if (mChildCount > 0) {
            mChildCount--;
            return POSITION_NONE;
        }
        return super.getItemPosition(object);
    }

    @Override
    public boolean isViewFromObject(View view, Object o) {
        return ((AbsLoadingGroup) o).getRoot() == view;
    }

    public Object instantiateItem(ViewGroup container, int position) {
        AbsLoadingGroup group = groups[position];
        if (fleshFlag && position == fleshItem) {
            group.onResetView();
            fleshFlag = false;
        } else {
            group.onCreateView();
            if(!hasLoadingOverList.contains(position)) {
                hasLoadingOverList.add(position);
            }
        }
        container.addView(group.getRoot());
        return group;
    }
    
    public boolean hasLoadingPosition(int position) {
        return hasLoadingOverList.contains(position);
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        AbsLoadingGroup group = (AbsLoadingGroup) object;
        container.removeView(group.getRoot());
        if(hasLoadingOverList.contains(position)) {
            hasLoadingOverList.remove((Object)position);
        }
    }

}
