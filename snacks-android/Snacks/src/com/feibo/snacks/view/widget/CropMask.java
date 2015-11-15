package com.feibo.snacks.view.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.widget.Toast;

import com.feibo.snacks.R;


/**
 * -----------------------------------------------------------
 * 版 权 ： BigTiger 版权所有 (c) 2015
 * 作 者 : BigTiger
 * 版 本 ： 1.0
 * 创建日期 ：2015/7/11 16:46
 * 描 述 ：
 * <p>
 * -------------------------------------------------------------
 */
public class CropMask extends View {

    private Context context;
    private Paint paint;    //画笔
    private int width;      //屏幕的宽
    private int height;      //屏幕的高
    private Bitmap bitmap;     //要擦除的图片

    private Canvas can;      //临时画布
    private Bitmap bitmap_temp;    //临时画布中的临时图片
    private int mRadius;    //待选矩形半边长
    private Point mCenter;   //待选矩形中心

    public CropMask(Context context) {
        this(context, null);
    }

    public CropMask(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CropMask(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.CropMask);
        mRadius = (int)a.getDimension(R.styleable.CropMask_radius,
                TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 100, getResources().getDisplayMetrics()));
        a.recycle();
        initData();
    }

    private void initData(){

        paint = new Paint();
        paint.setAlpha(0);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
        paint.setAntiAlias(true);

        paint.setDither(true);
        paint.setStyle(Paint.Style.FILL);
        paint.setStrokeJoin(Paint.Join.ROUND);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setStrokeWidth(20);

        /*ViewTreeObserver observer = getViewTreeObserver();
        observer.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {

            @Override
            public boolean onPreDraw() {
                createView();
                return true;
            }
        });*/

    }

    private void createView(){
        /*if(!isInEditMode()){

        }*/
        mCenter = new Point();
        width = getWidth();
        height = getHeight();
        mCenter.x = width/2;
        mCenter.y = height/2;
        try {
            bitmap= Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.bg_mask),
                    width, height, true);   //将要擦除的图片缩放至全屏的大小
            bitmap_temp = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);    //创建临时图片
            can = new Canvas();    //创建临时画布
            can.setBitmap(bitmap_temp);  // 将画到临时画布上的对象都画到临时图片上
            can.drawBitmap(bitmap, 0, 0, null);  //将要擦除的图片画到临时图片上
            isInit = true;
        }catch(Exception e){
            Toast.makeText(getContext(), "您的手机内存不足,请重启重试!", Toast.LENGTH_SHORT).show();
            isInit = false;
        }

    }
    private boolean isInit;
    @Override
    protected void onDraw(Canvas canvas) {
        if(!isInit){
            createView();
        }
        if (bitmap_temp == null) return;
        canvas.drawBitmap(bitmap_temp, 0, 0, null); //将画布上的临时图片显示在屏幕上
        Rect rect = new Rect(mCenter.x - mRadius, mCenter.y - mRadius, mCenter.x + mRadius, mCenter.y + mRadius);
        can.drawRect(rect, paint);

    }
    public Point getCenter(){
        return this.mCenter;
    }
    public float getRadius(){
        return mRadius;
    }
}
