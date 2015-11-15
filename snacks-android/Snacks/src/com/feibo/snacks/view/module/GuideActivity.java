package com.feibo.snacks.view.module;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.feibo.snacks.R;
import com.feibo.snacks.util.SPHelper;
import com.feibo.snacks.view.base.BaseActivity;
import com.feibo.snacks.view.widget.IndicatorView;

import java.util.ArrayList;
import java.util.List;

@SuppressLint("InflateParams")
public class GuideActivity extends BaseActivity {

    private int[] guides = new int[] {R.drawable.bg_guide_1, R.drawable.bg_guide_2, R.drawable.bg_guide_3,
            R.drawable.bg_guide_4};

    private static final int INDICATOR_COUNT = 4;

    private ViewPager viewPager;

    private int curPage = 0;
    private List<View> list;

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.activity_viewpager);
        initView();
        list = new ArrayList<View>();
        viewPager.setBackgroundColor(Color.TRANSPARENT);
        for (int i = 0; i < guides.length; i++) {
            View view = LayoutInflater.from(this).inflate(R.layout.activity_guide, null);
            ImageView imageView = (ImageView) view.findViewById(R.id.launch_bg);
            imageView.setBackgroundResource(guides[i]);
            IndicatorView indicatorView = (IndicatorView) view.findViewById(R.id.guide_indicator);
            indicatorView.setCount(INDICATOR_COUNT);
            indicatorView.setCurrentPosition(i);
            list.add(view);
            if (i == guides.length - 1) {
                addLastView();
            }
        }
        viewPager.setAdapter(new GuidePageAdapter(list));
        viewPager.setOnPageChangeListener(new OnPageChangeListener() {

            @Override
            public void onPageSelected(int arg0) {

            }

            @SuppressLint("ResourceAsColor")
            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {
                curPage = arg0;
            }

            @Override
            public void onPageScrollStateChanged(int arg0) {
                if (arg0 == ViewPager.SCROLL_STATE_IDLE) {
                    if (curPage == list.size() - 1) {
                        SPHelper.cancelGuide();
                        finish();
                        overridePendingTransition(0, 0);
                    }
                }
            }
        });
    }

    private void addLastView() {
        View view = LayoutInflater.from(this).inflate(R.layout.activity_guide, null);
        ImageView imageView = (ImageView) view.findViewById(R.id.launch_bg);
        imageView.setBackgroundColor(Color.TRANSPARENT);
        IndicatorView indicatorView = (IndicatorView) view.findViewById(R.id.guide_indicator);
        indicatorView.setVisibility(View.GONE);
        list.add(view);
    }

    private void initView() {
        viewPager = (ViewPager) findViewById(R.id.pager);
    }

    private static class GuidePageAdapter extends PagerAdapter {
        private List<View> list;

        public GuidePageAdapter(List<View> list) {
            this.list = list;
        }

        @Override
        public int getCount() {

            if (list != null && list.size() > 0) {
                return list.size();
            } else {
                return 0;
            }
        }

        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            return arg0 == arg1;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            container.addView(list.get(position));
            return list.get(position);
        }

        @Override
        public int getItemPosition(Object object) {
            return POSITION_NONE;
        }
    }
}
