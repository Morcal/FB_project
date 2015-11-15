package com.feibo.snacks.view.module.coupon;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.List;

/**
 * Created by Jayden on 2015/9/6.
 */
public class CouponViewpagerAdapter extends FragmentPagerAdapter {

    /**
     * 页面内容集合
     */
    private List<Fragment> fgs = null;
    private FragmentManager mFragmentManager;

    public CouponViewpagerAdapter(FragmentManager fm, List<Fragment> fgs) {
        super(fm);
        mFragmentManager = fm;
        this.fgs = fgs;
    }

    @Override
    public Fragment getItem(int arg0) {
        return fgs.get(arg0);
    }

    @Override
    public int getCount() {
        return fgs.size();
    }

    /**
     * 重新设置页面内容
     *
     * @param items
     */
    public void setPagerItems(List<Fragment> items) {
        if (items != null) {
            for (int i = 0; i < fgs.size(); i++) {
                mFragmentManager.beginTransaction().remove(fgs.get(i)).commit();
            }
            fgs = items;
        }
    }
}
