package com.feibo.joke.video;

import java.io.File;
import java.util.List;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import fbcore.log.LogUtil;

import com.feibo.ffmpeg.FFmpeg;
import com.feibo.joke.R;
import com.feibo.joke.app.DataChangeEventCode;
import com.feibo.joke.video.manager.VideoCutManager;
import com.feibo.joke.video.manager.VideoCutManager.CutVideoListener;
import com.feibo.joke.video.util.ThumbnailUtil;
import com.feibo.joke.video.util.VideoProvider;
import com.feibo.joke.video.util.VideoProvider.Video;
import com.feibo.joke.view.BaseActivity;
import com.feibo.joke.view.util.ToastUtil;

/**
 * 导入视频
 *
 * @author Lidiqing
 *
 */
public class VideoImportActivity extends BaseActivity {

	private static final String TAG = "VideoImportActivity";
	private GridView mGridView;
	private VideoGridAdapter mGridAdapter;
	private ProgressDialog mProgressDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_video_import);
		initTitle();
		initGrid();
	}

	@Override
	public void onDataChange(int code) {
		switch (code) {
		case DataChangeEventCode.CHANGE_TYPE_VIDEO_PRODUCE_SUCESS:
            setChangeTypeAndFinish(code);
            break;
		case DataChangeEventCode.CHANGE_TYPE_VIDEO_CUT_CANCEL:
			// 取消视频剪辑，释放视频剪辑资源
			VideoCutManager.getInstance().release();
			break;
		default:
			break;
		}
	}

	private void initTitle() {
		((TextView) findViewById(R.id.text_title))
				.setText(getString(R.string.title_video_import));
		findViewById(R.id.btn_back).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// 取消视频导入
				setChangeTypeAndFinish(DataChangeEventCode.CHANGE_TYPE_VIDEO_IMPORT_CANCEL);
			}
		});
	}

	private void initGrid() {
		mGridView = (GridView) findViewById(R.id.grid_video);

		// 读取媒体库中的视频
		List<Video> videos = new VideoProvider(this).getVideos();
		LogUtil.i(TAG, "video size:" + videos.size());

		mGridAdapter = new VideoGridAdapter(this, videos);
		mGridView.setAdapter(mGridAdapter);

		mGridView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

				if(!mGridAdapter.isThumbSuccess(position)){
					ToastUtil.showSimpleToast(VideoImportActivity.this, "视频文件可能损坏");
					return;
				}

				String path = ((Video)mGridAdapter.getItem(position)).path;

				long duration = FFmpeg.getMediaFileDuration(path) / 1000;
				if (duration < 3 || duration > 150) { // 视频时长限制
				    ToastUtil.showSimpleToast(VideoImportActivity.this, getText(R.string.video_duration_tip_import).toString());
				    return;
				}

				mProgressDialog = new ProgressDialog(VideoImportActivity.this);
				mProgressDialog.setCancelable(false);
				mProgressDialog.show();

				// 启动导入视频
				handleImport(path);
			}
		});
	}

	public void handleImport(final String path){
		VideoCutManager.getInstance().initFrames(path, new CutVideoListener() {

			@Override
			public void success(File file) {
				runOnUiThread(new Runnable() {
					@Override
                    public void run() {
						mProgressDialog.dismiss();
						Intent intent = new Intent(VideoImportActivity.this, VideoCutActivity.class);
						Bundle bundle = new Bundle();
						bundle.putString(VideoCutActivity.PARAM_VIDEO_PATH, path);
						intent.putExtras(bundle);
						startActivity(intent);
					}
				});
			}

			@Override
			public void error() {

			}
		});
	}

	public static class VideoGridAdapter extends BaseAdapter {
		private Context mContext;
		private List<Video> mVideos;

		public VideoGridAdapter(Context context, List<Video> list) {
			mContext = context;
			mVideos = list;
		}

		@Override
		public int getCount() {
			return mVideos.size();
		}

		@Override
		public Object getItem(int position) {
			return mVideos.get(position);
		}

		@Override
		public long getItemId(int position) {
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				convertView = LayoutInflater.from(mContext).inflate(
						R.layout.item_grid_video, null);
			}
			LogUtil.i(TAG, "pos:"+position+"; videopath:" + mVideos.get(position).path);
			ImageView imageView = ((ImageView) convertView
					.findViewById(R.id.image_video));
			ThumbnailUtil.getInstance().setImage(mVideos.get(position).path,
					imageView, R.drawable.default_video_small);
			return convertView;
		}

		public boolean isThumbSuccess(int postion){
			return ThumbnailUtil.getInstance().isVideoThumbSuccess(mVideos.get(postion).path);
		}

		public void setVideos(List<Video> videos) {
			mVideos = videos;
		}
	}
}
