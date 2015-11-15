package com.feibo.joke.video.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Paint.FontMetrics;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.RectF;
import android.text.TextPaint;
import android.view.View;

import com.feibo.joke.R;

public class TextToast extends View {

	private int mWidth;
	private int mHeight;

	private String mText;

	private RectF mRoundRectF;
	private float mRoundRadius;

	private Paint mPaint;
	private TextPaint mTextPaint;

	private int mTextColor;
	private int mBackgroundColor;

	private float mPaddingTopBottom;
	private float mPaddingLeftRight;

	private float mTextSize;

	private Path mTrianglePath;

	public TextToast(Context context, String text, int heightPx) {
		super(context);
		mText = text;
		mHeight = heightPx;

		mRoundRectF = new RectF();

		mPaint = new Paint();
		mPaint.setAntiAlias(true);
		mPaint.setStyle(Style.FILL_AND_STROKE);

		mTextPaint = new TextPaint();
		mTextPaint.setAntiAlias(true);
		mTextPaint.setTextAlign(Align.CENTER);

		mTrianglePath = new Path();

		mTextColor = getResources().getColor(R.color.c5_dark_grey);
		mBackgroundColor = getResources().getColor(R.color.c1_light_orange);
	}

	public void setText(String text) {
	    mText = text;
	    requestLayout();
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

		// 圆角背景高度
		float roundHeight = mHeight * 0.8f;

		// 文字区域宽高
		mTextSize = roundHeight * 0.6f;
		mPaddingTopBottom = roundHeight * 0.2f;
		mPaddingLeftRight = mTextSize;

		float mTextAreaWidth = mTextSize * mText.length();

		// 真实宽度
		mWidth = (int) (mTextAreaWidth + mPaddingLeftRight * 2);

		// 圆角背景宽度
		float roundWidth = mWidth;
		mRoundRadius = roundHeight * 0.2f;
		mRoundRectF.set(0, 0, roundWidth, roundHeight);

		float triSize = mHeight * 0.2f;
		mTrianglePath.reset();
		mTrianglePath.moveTo(mWidth/(float)2 - triSize, roundHeight-1);
		mTrianglePath.lineTo(mWidth/(float)2 + triSize, roundHeight-1);
		mTrianglePath.lineTo(mWidth/(float)2, mHeight);
		mTrianglePath.lineTo(mWidth/(float)2 - triSize, roundHeight-1);
		mTrianglePath.close();
		super.onMeasure(
				MeasureSpec.makeMeasureSpec(mWidth, MeasureSpec.EXACTLY),
				MeasureSpec.makeMeasureSpec(mHeight, MeasureSpec.EXACTLY));

	}

	@Override
	protected void onDraw(Canvas canvas) {
		// 圆角背景
		mPaint.setColor(mBackgroundColor);
		canvas.drawRoundRect(mRoundRectF, mRoundRadius, mRoundRadius, mPaint);

		mTextPaint.setColor(mTextColor);
		mTextPaint.setTextSize(mTextSize);

		// 文字
		FontMetrics metrics = mTextPaint.getFontMetrics();

		float offsetX = mPaddingLeftRight + mTextSize / 2;
		float offsetY = mPaddingTopBottom + mTextSize - (mTextSize + metrics.ascent);

//		Toast.makeText(getContext(), "leading:"+metrics.leading+"; ascent:"+metrics.ascent+"; descent:"+metrics.descent+"; size:"+mTextSize, Toast.LENGTH_LONG).show();

		for (int i = 0; i < mText.length(); i++) {
			canvas.drawText(String.valueOf(mText.charAt(i)), offsetX, offsetY,
					mTextPaint);
			offsetX += mTextSize;
		}


		// 测试点
//		FontMetrics metrics= mTextPaint.getFontMetrics();
//		Paint pointPaint = new Paint();
//		pointPaint.setStrokeWidth(4);
//		pointPaint.setColor(Color.BLUE);
//		canvas.draw

		// 三角形底部
		canvas.drawPath(mTrianglePath, mPaint);
	}

}
