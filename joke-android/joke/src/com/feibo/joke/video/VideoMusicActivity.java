package com.feibo.joke.video;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.feibo.joke.R;
import com.feibo.joke.video.util.TestData;
import com.feibo.joke.video.widget.MusicItemView;

public class VideoMusicActivity extends Activity{

	private ListView musicListView;
	private MusicAdapter adapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_video_music);
		initTitle();
		
		musicListView = (ListView) findViewById(R.id.list_music);
		adapter = new MusicAdapter(this);
		musicListView.setAdapter(adapter);
		musicListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				adapter.setSelectedPosition(position);
				adapter.notifyDataSetChanged();
			}
		});
	}
	
	private void initTitle(){
		((TextView)findViewById(R.id.text_title)).setText("所有音乐");
		findViewById(R.id.btn_back).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				finish();
			}
		});
	}
	
	public class MusicAdapter extends BaseAdapter{

		private String[] musics;
		private Context context;
		private int selectPosition;
		public MusicAdapter(Context context){
			this.context = context;
			musics = TestData.musicName;
			selectPosition = -1;
		}
		
		@Override
		public int getCount() {
			return musics.length;
		}

		@Override
		public Object getItem(int position) {
			return null;
		}

		@Override
		public long getItemId(int position) {
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View view = LayoutInflater.from(context).inflate(R.layout.item_music, null);
			MusicItemView itemView = (MusicItemView) view.findViewById(R.id.item_music);
			
			itemView.setMusicName(musics[position]);
			if(position == selectPosition){
				itemView.selected();
			}else {
				itemView.normal();
			}
			return view;
		}
		
		public void setSelectedPosition(int pos){
			selectPosition = pos;
		}
		
	}
}
