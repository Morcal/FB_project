package com.feibo.joke.video.widget;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.feibo.joke.R;

/**
 * 音乐选项
 * @author Lidiqing 2015/5/4
 *
 */
public class MusicItemView extends LinearLayout{

	private Drawable mMusicNomal;
	private Drawable mMusicSelected;
	private int mTextNorml;
	private int mTextSelectd;
	
	private View mAuditionView;
	private ImageView mMusicView;
	private TextView mNameView;
	
	public MusicItemView(Context context, AttributeSet attrs, int defStyleAttr,
			int defStyleRes) {
		super(context, attrs, defStyleAttr, defStyleRes);
		init(context);
	}

	public MusicItemView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	public MusicItemView(Context context) {
		super(context);
		init(context);
	}
	
	private void init(Context context){
		mMusicNomal = context.getResources().getDrawable(R.drawable.icon_music_normal);
		mMusicSelected = context.getResources().getDrawable(R.drawable.icon_music_selected);
		mTextNorml = Color.WHITE;
		mTextSelectd = context.getResources().getColor(R.color.c2_orange);
	}
	
	@Override
	protected void onFinishInflate() {
		mMusicView = (ImageView) findViewById(R.id.music);
		mNameView = (TextView) findViewById(R.id.name);
		mAuditionView = findViewById(R.id.audition);
	}
	
	public void normal(){
		mMusicView.setImageDrawable(mMusicNomal);
		mNameView.setTextColor(mTextNorml);
		mAuditionView.setVisibility(View.INVISIBLE);
	}
	
	public void selected(){
		mMusicView.setImageDrawable(mMusicSelected);
		mNameView.setTextColor(mTextSelectd);
		mAuditionView.setVisibility(View.VISIBLE);
	}
	
	public void setMusicName(String name){
		mNameView.setText(name);
	}
}
