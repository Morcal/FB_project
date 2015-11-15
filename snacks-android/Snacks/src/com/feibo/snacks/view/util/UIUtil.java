package com.feibo.snacks.view.util;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.View;
import android.widget.ImageView;

import com.feibo.snacks.R;
import com.feibo.snacks.app.AppContext;
import com.feibo.snacks.model.dao.cache.DataDiskProvider;
import com.feibo.snacks.view.module.home.NewProductAdapter;
import com.squareup.picasso.Picasso;

import fbcore.cache.image.ImageLoader.DefaultLoadListener;
import fbcore.cache.image.ImageLoader.ImageContainer;
import fbcore.log.LogUtil;

public class UIUtil {

    private UIUtil() {
    }

    public static void setDefaultImage(String url, ImageView img) {
        if (TextUtils.isEmpty(url)) {
            return;
        }
        setImage(url, img, R.drawable.default_class_296_296, R.drawable.default_class_296_296);
    }

    /**
     * 使用Picasso库异步设置图片
     * @param context
     * @param path
     * @param defaultId
     * @param errorId
     * @param imageView
     */
    public static void setImage(Context context, String path, int defaultId, int errorId, ImageView imageView){
        // Picasso支持参数为null, 不支持空字符串
        if (TextUtils.isEmpty(path)) {
            path = null;
        }
        Picasso.with(context).load(path).placeholder(defaultId).error(errorId).into(imageView);
    }

    public static void setImage(String url, ImageView img, int loading, int fail) {
        img.setImageResource(loading);
        Object tag = img.getTag();
        boolean validTag = tag == null || tag instanceof ImageContainer;
        if (validTag && tag != null) {
            ImageContainer lastContainer = (ImageContainer) tag;
          /*  if (lastContainer.getUrl().equals(url)) {
                return;
            }*/
            lastContainer.cancel();
        }
        if (isEmpty(url)) {
            img.setImageResource(fail);
            img.setTag(null);
            return;
        }
        ImageContainer container = DataDiskProvider.getInstance().getImageLoader()
                .loadImage(url, new ImageLoadListener(img, loading, fail));
        if (validTag) {
            img.setTag(container);
        }
    }

    public static CharSequence getPrice(Context context,double current) {
        return context.getResources().getString(R.string.show_cur_price,current);
    }

    public static void setBrandGroupHeaderImage(String imgUrl, ImageView img) {
        setImage(imgUrl, img, R.drawable.default_class_brandteam_640_256, R.drawable.default_class_brandteam_640_256);
    }

    public static void setSubjectHeaderImage(String imgUrl, ImageView img) {
        setImage(imgUrl, img, R.drawable.default_projects_small300_150, R.drawable.default_projects_small300_150);
    }

    public static void setHomeTodayImage(String imgUrl, ImageView img) {
        setImage(imgUrl, img, R.drawable.default_class_activiyt612_340, R.drawable.default_class_activiyt612_340);
    }

    public static void setHomeSpecialImage(int pos, String imgUrl, ImageView view) {
        switch (pos) {
            case NewProductAdapter.BTN_LOVER : {
                setImage(imgUrl,view,R.drawable.btn_lover,R.drawable.btn_lover);
                break;
            }
            case NewProductAdapter.BTN_MOVIE : {
                setImage(imgUrl,view,R.drawable.btn_movie,R.drawable.btn_movie);
                break;
            }
            case NewProductAdapter.BTN_TEA : {
                setImage(imgUrl,view,R.drawable.btn_beer,R.drawable.btn_beer);
                break;
            }
            case NewProductAdapter.BTN_BEAR : {
                setImage(imgUrl,view,R.drawable.btn_beer,R.drawable.btn_beer);
                break;
            }
        }
    }

    public static class ImageLoadListener extends DefaultLoadListener {
        private ImageView imageView;

        public ImageLoadListener(ImageView imageView, int defaultResId, int errorResId) {
            super(imageView, defaultResId, errorResId);
            this.imageView = imageView;
        }

        @Override
        public void onFail() {
            super.onFail();
            imageView.setTag(null);
        }

        @Override
        public void onSuccess(Drawable drawable, boolean immediate) {
            if (drawable == null) {
                LogUtil.i("ImageLoader", "drawable is null");

                return;
            }
            super.onSuccess(drawable, immediate);
            if (!immediate && imageView != null) {
                Object tag = imageView.getTag();
                if (tag instanceof ImageContainer) {
                    imageView.setTag(null);
                }
            }
        }
    }

    @SuppressLint("NewApi")
    private static boolean isEmpty(String str) {
        return str == null || str.isEmpty();
    }

    public static int dp2Px(Context context, int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, AppContext.getContext().getResources()
                .getDisplayMetrics());
    }

    public static int px2Dp(Context context, int px) {
        float density = context.getResources().getDisplayMetrics().density;
        return (int) (px * density);
    }

    public static void setDefaultFeedbackImage(String imgUrl, ImageView img) {
        setImage(imgUrl, img, R.drawable.default_photo, R.drawable.default_photo);
    }

    public static void setDefaultCommentImage(String imgUrl, ImageView img) {
        setImage(imgUrl, img, R.drawable.default_comment_icon, R.drawable.default_comment_icon);
    }

    public static void setDefaultHomeCategoryImage(String imgUrl, ImageView img) {
        setImage(imgUrl, img, R.drawable.default_hone_class84_84, R.drawable.default_hone_class84_84);
    }

    public static void setViewGone(View view) {
        view.setVisibility(View.GONE);
    }

    public static void setViewVisible(View view) {
        view.setVisibility(View.VISIBLE);
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public static void setBackGround(View view,Drawable drawable) {
        int sdk = android.os.Build.VERSION.SDK_INT;
        if(sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
            view.setBackgroundDrawable(drawable);
        } else {
            view.setBackground(drawable);
        }
    }
}