package com.feibo.joke.view.module;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.ImageView;

import com.feibo.joke.R;
import com.feibo.joke.view.BaseActivity;
import com.feibo.joke.view.adapter.LaunchPageAdapter;
import com.feibo.joke.view.widget.IndicatorView;

@SuppressLint("InflateParams")
public class GuideActivity extends BaseActivity implements OnClickListener{

    private int[] guides;
    
    private ImageView[] views;
    private ViewPager viewPager;
    private IndicatorView indicatorView;
    
    private int curPage = 0;
    
    private Button btnStart;
    
    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.activity_guide);
        initData();
        initView();
        initListener();
    }

    private void initData() {
        guides = new int[] {R.drawable.guide1, R.drawable.guide2,R.drawable.guide3};
        views = new ImageView[guides.length];
        LayoutParams lp = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        for(int i=0; i<guides.length; i++) {
            views[i] = new ImageView(this);
            views[i].setBackgroundResource(guides[i]);
            views[i].setLayoutParams(lp);
        }
    }

    private void initView() {
        indicatorView = (IndicatorView) findViewById(R.id.guide_indicator);
        indicatorView.setCount(guides.length);
        indicatorView.setCurrentPosition(0);
        btnStart = (Button) findViewById(R.id.btn_start);
        btnStart.setOnClickListener(this);
        
        viewPager = (ViewPager) findViewById(R.id.pager);

        LaunchPageAdapter adapter = new LaunchPageAdapter();
        adapter.setGroups(views);
        viewPager.setAdapter(adapter);
    }
    
    private void initListener() {
        viewPager.setOnPageChangeListener(new OnPageChangeListener() {
            
            @Override
            public void onPageSelected(int position) {
                if(position != guides.length - 1) {
                    indicatorView.setCurrentPosition(position);
                    indicatorView.setVisibility(View.VISIBLE);
                } else {
                    indicatorView.setVisibility(View.GONE);
                }
            }
            
            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {
                curPage = arg0;
            }
            
            @Override
            public void onPageScrollStateChanged(int arg0) {
//                if (arg0 == ViewPager.SCROLL_STATE_IDLE) {
//                    if (curPage == views.length - 1) {
//                        finish();
//                        overridePendingTransition(0, 0);
//                    }
//                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        if(curPage == guides.length - 1) {
            finish();
            overridePendingTransition(0, 0);
        }
    }
}
