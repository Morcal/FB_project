package com.feibo.snacks.view.module.person.collect;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.TextView;

import com.feibo.snacks.R;
import com.feibo.snacks.view.base.BaseTitleFragment;
import com.feibo.snacks.view.module.goods.goodsdetail.H5GoodsDetailFragment;
import com.feibo.snacks.view.module.subject.H5SubjectDetailFragment;
import com.feibo.snacks.view.util.RemindControl;

import java.util.ArrayList;

@SuppressLint("InflateParams")
public class CollectFragment extends BaseTitleFragment {

    public static final int COLLECT_GOODS = 0;
    public static final int COLLECT_SUBJECT = 1;
    private TextView operateTextView;
    private ViewPager viewPager;
    private TextView collectGoods;
    private TextView collectSubject;
    private View root;

    private int curPage = 0;
    private SparseArray<IDeleteOperate> deleteListeners = new SparseArray<IDeleteOperate>();
    private SparseArray<OnScrollChangeListener> scrollChangesListeners = new SparseArray<OnScrollChangeListener>();
    private ArrayList<Fragment> fragments = new ArrayList<Fragment>();

    @Override
    public int onCreateTitleBar() {
        return R.layout.layout_collect_header;
    }

    @Override
    public View onCreateContentView() {
        root = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_collect_main, null);
        initWidget(root);
        initListener();
        return root;
    }

    private void initWidget(View view) {
        viewPager = (ViewPager) view.findViewById(R.id.viewpager);

        operateTextView = (TextView) getTitleBar().rightPart;

        View titleView = getTitleBar().title;
        collectGoods = (TextView) titleView.findViewById(R.id.collect_goods);
        collectSubject = (TextView) titleView.findViewById(R.id.collect_subejct);

        fragments.add(new CollectGoodsFragment());
        fragments.add(new CollectSubjectFragment());

        viewPager.setAdapter(new CollectViewpagerAdapter(getChildFragmentManager()));

        collectGoods.setSelected(true);
        collectSubject.setSelected(false);
    }

    private void initListener() {
        getTitleBar().leftPart.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getActivity() == null) {
                    return;
                }
                getActivity().finish();
            }
        });

        operateTextView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
               deleteListeners.get(curPage).delete(curPage);
            }
        });

        collectGoods.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isEditMode()){
                    return;
                }
                viewPager.setCurrentItem(COLLECT_GOODS);
            }
        });

        collectSubject.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isEditMode()){
                    return;
                }
                viewPager.setCurrentItem(COLLECT_SUBJECT);
            }
        });

        //这里处理编辑状态下不能滚动页面，或可继承ViewPage重写onTouchEvent，onInterceptTouchEvent
        viewPager.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(isEditMode()){
                    if(viewPager.getCurrentItem() == COLLECT_GOODS){
                        viewPager.scrollTo(0,0);
                    }else{
                        WindowManager windowManager = getActivity().getWindowManager();
                        int width = windowManager.getDefaultDisplay().getWidth();
                        viewPager.scrollTo(width,0);
                    }
                    return true;
                }
                return false ;
            }
        });

        OnMainPageChangeListener listener = new OnMainPageChangeListener();
        viewPager.setOnPageChangeListener(listener);
    }

    @Override
    public void onResume() {
        fragmentName = getResources().getString(R.string.collectFragment);
        super.onResume();
        RemindControl.cancelToast();
    }

    @Override
    public void onPause() {
        fragmentName = getResources().getString(R.string.collectFragment);
        super.onPause();
    }

    class CollectViewpagerAdapter extends FragmentPagerAdapter {

        public CollectViewpagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int arg0) {
            return fragments.get(arg0);
        }

        @Override
        public int getCount() {
            return fragments.size();
        }
    }

    private class OnMainPageChangeListener implements OnPageChangeListener {
        @Override
        public void onPageScrollStateChanged(int arg0) {

        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {

        }

        @Override
        public void onPageSelected(int arg0) {
            scrollChangesListeners.get(arg0).onScrollChange(arg0);
            if (arg0 == COLLECT_GOODS) {
                curPage =COLLECT_GOODS;
                collectGoods.setSelected(true);
                collectSubject.setSelected(false);
            } else {
                curPage = COLLECT_SUBJECT;
                collectSubject.setSelected(true);
                collectGoods.setSelected(false);
            }
        }
    }

    public  boolean isEditMode(){
        if(operateTextView.getText().equals(getString(R.string.cancel)) && operateTextView.getVisibility() == View.VISIBLE){
            return true;
        }
        return false;
    }

    public interface IDeleteOperate {
        public void delete(int curPage);
    }
    
    public interface OnScrollChangeListener {
        public void onScrollChange(int pos);
    }
    
    public TextView getTextView() {
        return operateTextView;
    }

    public int getCurrentPageIndex() {
        return curPage;
    }
    
    public void setIDeleteOperate(int key,IDeleteOperate listener) {
        deleteListeners.put(key, listener);
    }
    
    public void setOnScrollChangeListener(int key, OnScrollChangeListener onScrollChangeListener) {
        scrollChangesListeners.put(key, onScrollChangeListener); 
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == H5GoodsDetailFragment.RESULT_CODE && data != null) {
            fragments.get(curPage).onActivityResult(requestCode,resultCode,data);
        }
        
        if(resultCode == H5SubjectDetailFragment.RESULT_CODE && data != null) {
            fragments.get(curPage).onActivityResult(requestCode,resultCode,data);
        }
    }

}
