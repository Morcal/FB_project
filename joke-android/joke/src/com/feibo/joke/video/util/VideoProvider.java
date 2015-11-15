package com.feibo.joke.video.util;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore;

import fbcore.log.LogUtil;

public class VideoProvider {

	public static final String PREFIX_MP4 = "MP4";
	public static final int DURATION_MAX = 150000;
	public static final int DURATION_MIN = 3000;
	private static final String TAG = "VideoProvider";

	private Context mContext;

	public VideoProvider(Context context) {
		mContext = context;
	}

	/**
	 * 获取视频列表
	 * @return
	 */
	public List<Video> getVideos() {
		Cursor cursor = mContext.getContentResolver().query(
				MediaStore.Video.Media.EXTERNAL_CONTENT_URI, null, null, null,
				null);
		List<Video> list = new ArrayList<VideoProvider.Video>();
		while (cursor.moveToNext()) {
			Video video = new Video();
			 video.id = cursor.getInt(cursor
                     .getColumnIndexOrThrow(MediaStore.Video.Media._ID));
			 video.title = cursor
                     .getString(cursor
                             .getColumnIndexOrThrow(MediaStore.Video.Media.TITLE));
			 video.album = cursor
                     .getString(cursor
                             .getColumnIndexOrThrow(MediaStore.Video.Media.ALBUM));
			 video.artist = cursor
                     .getString(cursor
                             .getColumnIndexOrThrow(MediaStore.Video.Media.ARTIST));
			 video.displayName = cursor
                     .getString(cursor
                             .getColumnIndexOrThrow(MediaStore.Video.Media.DISPLAY_NAME));
			 video.mimeType = cursor
                     .getString(cursor
                             .getColumnIndexOrThrow(MediaStore.Video.Media.MIME_TYPE));
			 video.path = cursor
                     .getString(cursor
                             .getColumnIndexOrThrow(MediaStore.Video.Media.DATA));
			 video.duration = cursor
                     .getInt(cursor
                             .getColumnIndexOrThrow(MediaStore.Video.Media.DURATION));
			 video.size = cursor
                     .getLong(cursor
                             .getColumnIndexOrThrow(MediaStore.Video.Media.SIZE));
			 list.add(video);
		}

		filterVideos(list);
		return list;
	}

	/**
	 * 过滤掉不符合要求的视频
	 * @param videos
	 */
	private void filterVideos(List<Video> videos){
		Iterator<Video> iterator = videos.iterator();
		while(iterator.hasNext()){
			Video video = iterator.next();
			LogUtil.i(TAG, "video path:"+video.path);
			File file = new File(video.path);

			if(!file.isFile()){
				iterator.remove();
				continue;
			}

			String fileName=file.getName();
			String prefix=fileName.substring(fileName.lastIndexOf(".")+1);
			if(prefix == null || !prefix.toUpperCase().equals(PREFIX_MP4)){
				LogUtil.i(TAG, "not mp4:"+video.path);
				iterator.remove();
				continue;
			}

			if(video.duration > DURATION_MAX || video.duration < DURATION_MIN){
				LogUtil.i(TAG, "dur over:"+video.duration);
				iterator.remove();
				continue;
			}
		}
	}

	public static class Video {
		public int id;
		public String title;
		public String album;
		public String artist;
		public String displayName;
		public String mimeType;
		public String path;
		public long size;
		public long duration;
	}
}
