package com.feibo.joke.video.manager;

import java.io.File;
import java.util.ArrayList;
import java.util.Map;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;

import fbcore.log.LogUtil;
import fbcore.utils.Strings;

import com.feibo.ffmpeg.FFmpeg;
import com.feibo.ffmpeg.FfmpegUtils;
import com.feibo.ffmpeg.ProcessRunnable.AbsProcessListener;
import com.feibo.ffmpeg.Utils;
import com.feibo.joke.app.DirContext;
import com.feibo.joke.video.VideoConstants;

/**
 * 裁剪视频
 *
 * @author Lidiqing
 *
 */
public class VideoCutManager {
	private static final String TAG = "VideoCutManager";
	private static int FRAME_NUM = 10;

	private static VideoCutManager manager;

	public static VideoCutManager getInstance() {
		if (manager == null) {
			manager = new VideoCutManager();
		}
		return manager;
	}

	private ArrayList<Bitmap> mFrameBitmaps;
	private File mFrameDir;
	private File mCutVideoFile;

	private VideoCutManager() {
		mFrameBitmaps = new ArrayList<Bitmap>();
		mCutVideoFile = null;
	}

	/**
	 * 初始化视频剪辑显示的关键帧，默认10张，异步
	 *
	 * @param videoPath
	 */
	public void initFrames(final String videoPath, final CutVideoListener listener) {
	    clearBitmaps();
	    mFrameBitmaps = new ArrayList<Bitmap>();

		VideoWorker.getInstance().execute(new Runnable() {

			@Override
			public void run() {
				File recordDir = DirContext.getInstance().getTmpDir();
				File frameDir = new File(recordDir.getAbsolutePath() + File.separator + System.currentTimeMillis());
				if (!frameDir.exists()) {
					frameDir.mkdir();
				}
				FFmpeg.saveFrameImage(videoPath, frameDir.getAbsolutePath(), VideoConstants.FRAME_PREFIX, FRAME_NUM);
				// 读出帧文件
				File[] files = frameDir.listFiles();
				if (files == null) {
				    listener.error();
				    return;
				}
				mFrameDir = frameDir;
				BitmapFactory.Options options = new BitmapFactory.Options();
				// 压缩图像
				options.inPreferredConfig = Config.RGB_565;
				options.inSampleSize = 2;

				for (int i = 0; i < files.length; i++) {
					LogUtil.i(TAG, "frame:" + i + "; totalMemory:"
							+ (Runtime.getRuntime().totalMemory() / 1024 / 1024)
							+ "; freeMemory:"
							+ (Runtime.getRuntime().freeMemory() / 1024 / 1024));

					Bitmap bitmap = BitmapFactory.decodeFile(files[i].getAbsolutePath(), options);
					mFrameBitmaps.add(bitmap);
				}
				listener.success(mFrameDir);
			}
		});
	}

	private String convertMs(int ms) {
	    int s = ms / 1000;

	    int sstr = s % 60;

	    int m = s / 60;

	    if (m == 0) {
	        return "00:00:" + sstr;
	    }

	    int mstr = m % 60;
	    int h = m / 60;

	    StringBuilder sb = new StringBuilder();
	    sb.append(h < 10 ? ("0"+h) : (""+h))
	        .append(":").append(mstr < 10 ? ("0"+mstr) : (""+mstr))
	        .append(":").append(sstr < 10 ? ("0"+sstr) : (""+sstr));
	    return sb.toString();
	}

	private void scale(String input, String output, final onScaleListener listener) {
	    final long now = System.currentTimeMillis();
	    String cmd = FfmpegUtils.getCmdPath() + " -i " + input + " -vf scale=480:-1 -strict -2 -y " + output;
	    LogUtil.d(TAG, "scale cmd:" + cmd);
	    FfmpegUtils.execute(cmd, new AbsProcessListener() {

            @Override
            public void onExit(int exitCode) {
                LogUtil.d(TAG, "scale exitCode:" + exitCode + ", elapsed:" + (System.currentTimeMillis()-now));
                if (exitCode == 0) {
                    listener.onScaled();
                } else {
                    listener.onFail();
                }
            }
        });
	}

	private static interface onScaleListener {
	    void onScaled();
	    void onFail();
	}

	/**
	 * 根据起始时间和终止时间裁剪视频，异步
	 *
	 * @param start
	 * @param end
	 * @param videoPath
	 * @param listener
	 */
	public void cutVideo(final int start, final int end, final String videoPath, final CutVideoListener listener) {
		// 开启异步裁剪视频，裁剪结束后初始化VideoManager
	    File recordDir = DirContext.getInstance().getTmpDir();
        File cutVideoDir = new File(recordDir.getAbsoluteFile() + File.separator + System.currentTimeMillis());
        if (!cutVideoDir.exists()) {
            cutVideoDir.mkdir();
        }

        final long ts = System.currentTimeMillis();
        final File cutVideoFile = new File(cutVideoDir.getAbsoluteFile()
                + File.separator + ts + ".mp4");
        final String output = cutVideoDir.getAbsolutePath() + File.separator + ts + "_scale.mp4";

        String cmd = FfmpegUtils.getCmdPath() + " -i " + videoPath
                + " -ss " + convertMs(start) + " -t " + convertMs(end-start)
                + " -vcodec copy -acodec copy -strict experimental -y " + cutVideoFile.getAbsolutePath();

        FfmpegUtils.execute(cmd, new AbsProcessListener() {
            @Override
            public void onExit(int exitCode) {
                LogUtil.d(TAG, "split, exitCode:" + exitCode + ", elapsed:" + (System.currentTimeMillis()-ts) + " ms");
                if (exitCode == 0) {
                    Map<String, String> meta = Utils.getMediaMeta(cutVideoFile.getAbsolutePath());
                    boolean needScale = false;
                    if (meta != null) {
                        int w = 0;
                        int h = 0;
                        if (meta.get("width") == null || meta.get("height") == null) {
                            LogUtil.i(TAG, "no exit width or height");
                            other(start, end, videoPath, listener);
                            return;
                        }

                        if (meta.get("width") != null) {
                            w = Strings.toInt(meta.get("width"));
                        }
                        if (meta.get("height") != null) {
                            h = Strings.toInt(meta.get("height"));
                        }
                        if (w >= 1080 || h > 1080) {
                            needScale = true;
                        }
                    }

                    if (!needScale) {
                        listener.success(cutVideoFile);
                        return;
                    }

                    scale(cutVideoFile.getAbsolutePath(), output, new onScaleListener() {
                        @Override
                        public void onScaled() {
                            listener.success(new File(output));
                        }

                        @Override
                        public void onFail() {
                            listener.success(cutVideoFile);
                        }
                    });
                } else {
                    listener.error();
                }
            }
        });
	}

	/**
     * 根据起始时间和终止时间裁剪视频，异步
     *
     * @param start
     * @param end
     * @param videoPath
     * @param listener
     */
    public void other(final int start, final int end, final String videoPath, final CutVideoListener listener) {
        // 开启异步裁剪视频，裁剪结束后初始化VideoManager
        File recordDir = DirContext.getInstance().getTmpDir();
        File cutVideoDir = new File(recordDir.getAbsoluteFile() + File.separator + System.currentTimeMillis());
        if (!cutVideoDir.exists()) {
            cutVideoDir.mkdir();
        }

        final long ts = System.currentTimeMillis();
        final File cutVideoFile = new File(cutVideoDir.getAbsoluteFile()
                + File.separator + ts + ".mp4");
        final String output = cutVideoDir.getAbsolutePath() + File.separator + ts + "_scale.mp4";

        String cmd = FfmpegUtils.getCmdPath() + " -i " + videoPath
                + " -ss " + convertMs(start) + " -t " + convertMs(end-start)
                + " -vcodec libx264 -acodec copy -strict experimental -y " + cutVideoFile.getAbsolutePath();

        FfmpegUtils.execute(cmd, new AbsProcessListener() {
            @Override
            public void onExit(int exitCode) {
                LogUtil.d(TAG, "split, exitCode:" + exitCode + ", elapsed:" + (System.currentTimeMillis()-ts) + " ms");
                if (exitCode == 0) {
                    Map<String, String> meta = Utils.getMediaMeta(cutVideoFile.getAbsolutePath());
                    boolean needScale = false;
                    if (meta != null) {
                        int w = 0;
                        int h = 0;
                        if (meta.get("width") != null) {
                            w = Strings.toInt(meta.get("width"));
                        }
                        if (meta.get("height") != null) {
                            h = Strings.toInt(meta.get("height"));
                        }
                        if (w >= 1080 || h > 1080) {
                            needScale = true;
                        }
                    }

                    if (!needScale) {
                        listener.success(cutVideoFile);
                        return;
                    }

                    scale(cutVideoFile.getAbsolutePath(), output, new onScaleListener() {
                        @Override
                        public void onScaled() {
                            listener.success(new File(output));
                        }

                        @Override
                        public void onFail() {
                            listener.success(cutVideoFile);
                        }
                    });
                } else {
                    listener.error();
                }
            }
        });
    }

	/**
	 * 获取帧
	 *
	 * @param pos
	 * @return
	 */
	public Bitmap getFrameBitmap(float progress) {
	    if (mFrameBitmaps == null || mFrameBitmaps.size() == 0) {
	        return null;
	    }
		int total = mFrameBitmaps.size() - 1;
		int pos = (int) (total * progress);
		LogUtil.i(TAG, "total : " + total + " pos : " + pos);
		return mFrameBitmaps.get(pos);
	}

	/**
	 * 获取剪辑视频
	 *
	 * @return
	 */
	public File getCutVideoFile() {
		return mCutVideoFile;
	}

	/**
	 * 释放资源，删除中间文件
	 */
	public void release() {
		VideoWorker.getInstance().execute(new Runnable() {
			@Override
			public void run() {
				// 清理位图
				clearBitmaps();
				if(mFrameDir == null || !mFrameDir.exists()){
					return;
				}
				// 删除帧文件
				synchronized (mFrameDir) {
					File[] childFiles = mFrameDir.listFiles();
					if (childFiles == null || childFiles.length == 0) {
						mFrameDir.delete();
						return;
					}

					for (int i = 0; i < childFiles.length; i++) {
						childFiles[i].delete();
					}
					mFrameDir.delete();
					manager = null;
				}
			}
		});
	}

	public synchronized void clearBitmaps() {
		for (Bitmap bitmap : mFrameBitmaps) {
			bitmap.recycle();
		}
		mFrameBitmaps.clear();
	}

	public static interface CutVideoListener {
		void success(File file);

		void error();
	}

}
