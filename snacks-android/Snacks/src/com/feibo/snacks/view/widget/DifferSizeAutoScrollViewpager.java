package com.feibo.snacks.view.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;

import com.feibo.snacks.R;
import com.feibo.snacks.model.bean.Image;
import com.feibo.snacks.view.util.UIUtil;

import java.util.ArrayList;
import java.util.List;

public class DifferSizeAutoScrollViewpager extends ViewPager {

    private static final String TAG = DifferSizeAutoScrollViewpager.class.getSimpleName();
    private List<Image> topics;
    private List<View> imageViews;
    private DifferSizeIndicatorView indicatorView;

    private float radio = 0.421875f;
    private int curPage = 0;

    private int type = 0;// 默认不做圆角处理

    private OnItemPicClickListener listener;

    private static final int AUTO_SCROLL_MODE = 0;
    private boolean isPause = false;
    private int delayMillis = 4000;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (isPause) {
                return;
            }
            if (msg.what == AUTO_SCROLL_MODE) {
                int count = imageViews.size();
                if (count > 1) {
                    int index = getCurrentItem();
                    index = index % (count - 2) + 1;
                    setCurrentItem(index, true);
                }
            }
        };
    };


    public DifferSizeAutoScrollViewpager(Context context) {
        super(context);
        init(null);
    }

    public DifferSizeAutoScrollViewpager(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    private void init(AttributeSet attrs) {
        TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.DifferSizeAutoScrollViewpager);
        radio = a.getFloat(R.styleable.DifferSizeAutoScrollViewpager_radio, 1);
        type = a.getInt(R.styleable.DifferSizeAutoScrollViewpager_type, 0);
        a.recycle();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = (int) (width * radio);
        heightMeasureSpec = MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    private float downX;
    private float downY;

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
        case MotionEvent.ACTION_DOWN:
            downX = ev.getX();
            downY = ev.getY();
            getParent().requestDisallowInterceptTouchEvent(true);
            break;

        case MotionEvent.ACTION_MOVE:
            if (Math.abs(ev.getX() - downX) > Math.abs(ev.getY() - downY)) {
                getParent().requestDisallowInterceptTouchEvent(true);
            } else {
                getParent().requestDisallowInterceptTouchEvent(false);
            }
            break;
        case MotionEvent.ACTION_UP:
        default:
            getParent().requestDisallowInterceptTouchEvent(false);
            break;
        }
        return super.dispatchTouchEvent(ev);
    }

    private void addPageChangeListener(final Context context) {
        setOnPageChangeListener(new OnPageChangeListener() {
            @Override
            public void onPageSelected(int arg0) {
            }

            private void setSelectTip(final Context context, int pos) {
                if (imageViews.size() > 1) {
                    if (pos < 1) {
                        setCurrentItem(topics.size(), false);
                    } else if (pos > topics.size()) {
                        setCurrentItem(1, false);
                    }
                }
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {
                curPage = arg0;
            }

            @Override
            public void onPageScrollStateChanged(int arg0) {
                if (arg0 == ViewPager.SCROLL_STATE_IDLE) {
                    setSelectTip(context, curPage);
                    changeIndicatorPosition();
                    startHandleMsg();
                } else {
                    if (arg0 == ViewPager.SCROLL_STATE_SETTLING) {
                        changeIndicatorPosition();
                    }
                    pauseHandleMsg();
                }
            }

            private void changeIndicatorPosition() {
                int indicatePosition = curPage;
                if (indicatePosition < 1) {
                    indicatePosition = topics.size() - 1;
                } else if (curPage > topics.size()) {
                    indicatePosition = 0;
                } else {
                    indicatePosition --;
                }
                if (topics.size() >= 1) {
                    indicatorView.setCurrentPosition(indicatePosition);
                }
            }

            private void startHandleMsg() {
                handler.removeMessages(AUTO_SCROLL_MODE);
                isPause = false;
                Message msg = handler.obtainMessage(AUTO_SCROLL_MODE);
                handler.sendMessageDelayed(msg, delayMillis);
            }
        });
    }

    private void pauseHandleMsg() {
        handler.removeMessages(AUTO_SCROLL_MODE);
        isPause = true;
    }

    private class AutoScrollViewPageAdapter extends PagerAdapter {

        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            return arg0 == arg1;
        }

        @Override
        public int getCount() {
            return imageViews.size();
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            if(position >= imageViews.size()) {
                return;
            }
            ((ViewPager) container).removeView(imageViews.get(position));
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            View view = imageViews.get(position);
            container.addView(view);
            return view;
        }
    }

    public void addIndicatorView(DifferSizeIndicatorView view) {
        this.indicatorView = view;
    }

    public void init(Context context, final List<Image>topics) {
        pauseHandleMsg();
        if (topics == null || topics.size() == 0) {
            return;
        }
        if (topics.equals(this.topics)) {
            return;
        }
        this.topics = topics;
        if (topics != null) {
            imageViews = new ArrayList<View>();
            if (topics.size() > 1) {
                addFirstView(context, topics.size() - 1);
                addAutoScrollViews(context, topics.size());
                addLastView(context, 0);
            } else {
                addItemView(context, 0);
            }
        }

        setAdapter(new AutoScrollViewPageAdapter());
        addPageChangeListener(context);
        if (topics.size() >= 1) {
            indicatorView.setCount(topics.size());
        }
        if (topics.size() > 1) {
            setCurrentItem(1);
        }
        isPause = false;
        Message msg = handler.obtainMessage(AUTO_SCROLL_MODE);

        handler.sendMessageDelayed(msg, delayMillis);
        requestLayout();
    }

    private void addFirstView(Context context, final int position) {
        addItemView(context, position);
    }

    private void addItemView(Context context, final int position) {
        if (type == 0) {
            ImageView imageView = new ImageView(context);
            imageView.setScaleType(ScaleType.FIT_XY);
            LayoutParams params = new LayoutParams();
            params.width = LayoutParams.MATCH_PARENT;
            params.height = LayoutParams.MATCH_PARENT;
            imageView.setLayoutParams(params);
            imageView.setBackgroundResource(R.drawable.default_home_banner_640_270);
            Image image = topics.get(position);
            UIUtil.setImage(image.imgUrl, imageView, R.drawable.default_home_banner_640_270, R.drawable.default_home_banner_640_270);

            imageView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                }
            });
            imageViews.add(imageView);
        } else {
            Image image = topics.get(position);
            View view = LayoutInflater.from(context).inflate(R.layout.item_topic, null);
            view.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener == null) {
                        return;
                    }
                    listener.onClick(position);
                }
            });
            ImageView imageView = (ImageView) view.findViewById(R.id.item_topic_bg);
            UIUtil.setImage(image.imgUrl, imageView, R.drawable.default_topic_640_270, R.drawable.default_topic_640_270);
            imageViews.add(view);
        }
    }

    private void addLastView(Context context, int position) {
        addFirstView(context, position);
    }

    private void addAutoScrollViews(Context context, int size) {
        for (int i = 0; i < size; i++) {
            addItemView(context, i);
        }
    }

    public void setOnItemClickListener(OnItemPicClickListener listener) {
        this.listener = listener;
    }

    public static interface OnItemPicClickListener {
        void onClick(int position);
    }

    public void pauseAutoScroll() {
        isPause = true;
        handler.removeCallbacks(null);
        handler = null;
    }
}
