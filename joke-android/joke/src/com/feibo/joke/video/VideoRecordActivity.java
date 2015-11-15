package com.feibo.joke.video;

import java.io.File;
import java.util.List;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.hardware.Camera.Size;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ImageView;

import fbcore.log.LogUtil;
import fbcore.utils.Files;
import fbcore.utils.IOUtil;

import com.feibo.ffmpeg.FFmpeg;
import com.feibo.ffmpeg.FfmpegUtils;
import com.feibo.ffmpeg.ProcessRunnable.AbsProcessListener;
import com.feibo.ffmpeg.Utils;
import com.feibo.joke.R;
import com.feibo.joke.app.AppContext;
import com.feibo.joke.app.DataChangeEventCode;
import com.feibo.joke.app.DirContext;
import com.feibo.joke.video.camera.CamParaUtil;
import com.feibo.joke.video.camera.CameraEngine;
import com.feibo.joke.video.camera.CameraEngine.CamOpenedCallback;
import com.feibo.joke.video.camera.CameraView;
import com.feibo.joke.video.camera.CameraView.OnPreviewCallback;
import com.feibo.joke.video.manager.VideoCutManager;
import com.feibo.joke.video.manager.VideoManager;
import com.feibo.joke.video.widget.FallbackButton;
import com.feibo.joke.video.widget.FallbackButton.OnFallBackListener;
import com.feibo.joke.video.widget.RecordButton;
import com.feibo.joke.video.widget.RecordButton.OnRecordClickListener;
import com.feibo.joke.video.widget.RecordTrackView;
import com.feibo.joke.video.widget.RecordTrackView.OnRecordTrackListener;
import com.feibo.joke.video.widget.TextToast;
import com.feibo.joke.view.BaseActivity;
import com.feibo.joke.view.dialog.RemindDialog;
import com.feibo.joke.view.dialog.RemindDialog.OnDialogClickListener;
import com.feibo.joke.view.dialog.RemindDialog.RemindSource;
import com.feibo.joke.view.util.LaunchUtil;
import com.feibo.joke.view.util.ToastUtil;
import com.feibo.recorder.FeiboRecorder;
import com.feibo.recorder.FeiboRecorder.VideoStartRecordListener;
import com.feibo.recorder.RecorderParameters;


@SuppressWarnings("deprecation")
public class VideoRecordActivity extends BaseActivity {
    private static final String TAG = VideoRecordActivity.class.getSimpleName();

    private FrameLayout mVideoContainer;
    private CameraView mCameraView;
    private RecordTrackView mRecordTrackView;
    private RecordButton mRecordButton;
    private FallbackButton mFallBackButton;
    private View mFinishButton;
    private View mImportButton;

    private View mBackBtn;
    private View mFlashBtn;
    private ImageView mCameraSwitchBtn;

    private TextToast mWarnToast;

    private CameraEngine mCameraEngine;
    private FeiboRecorder mRecorder;
    private WakeLock mWakeLock;

    public VideoRecordActivity() {
        super(true);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_record);

        initTitle();

		mCameraEngine = new CameraEngine();
		mCameraView = new CameraView(this, mCameraEngine);
		mVideoContainer = (FrameLayout) findViewById(R.id.container_video);

		boolean supportCamera = CamParaUtil.getInstance().hasPermission(this, android.Manifest.permission.CAMERA);
        LogUtil.d(TAG, "support camera?" + supportCamera
                + "video capture?" + CamParaUtil.getInstance().hasPermission(this, android.Manifest.permission.CAPTURE_VIDEO_OUTPUT)
                + "audio capture?" + CamParaUtil.getInstance().hasPermission(this, android.Manifest.permission.CAPTURE_AUDIO_OUTPUT)
                + "other?" + CamParaUtil.getInstance().hasPermission(this, android.Manifest.permission.CAPTURE_SECURE_VIDEO_OUTPUT));
        if (!supportCamera) {
            finish();
            return;
        }

		mVideoContainer.addView(mCameraView, -1, -1);
		mCameraEngine.setCamOpenedCallback(new CamOpenedCallback() {
			@Override
			public void onCamOpened(final int which) {
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						initVideoView();
						initEvents();

						mFlashBtn.setVisibility(mCameraEngine.isFrontCamera() ? View.GONE : View.VISIBLE);
						mCameraSwitchBtn.setEnabled(true);

						if (mRecorder == null) {
							mRecorder = new FeiboRecorder();
							RecorderParameters parameters = new RecorderParameters();
							parameters.setVideoFrameRate(mCameraEngine.getCameraFrameRate());
							mRecorder.init(parameters);
						} else {
							mRecorder.setCarema(mCameraEngine.isFrontCamera() ? 1 : 0);
						}
					}
				});
			}

            @Override
            public void onFail() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        RemindSource source = new RemindSource("部分用户无法启动，请到系统设置或者360安全卫士等管理软件开启相机权限，或者重启手机。如有其它问题，记得及时反馈给我们哦~", "确定", null);
                        final RemindDialog dialog = RemindDialog.show(VideoRecordActivity.this, source);
                        dialog.setOnDialogClickListener(new OnDialogClickListener() {
                            @Override
                            public void onConfirm() {
                                dialog.dismiss();
                                finish();
                            }

                            @Override
                            public void onCancel() {
                                dialog.dismiss();
                            }
                        });
                    }
                });
            }
		});
    }

    @Override
    public void onDataChange(int code) {
        boolean close = false;
        switch (code) {
        case DataChangeEventCode.CHANGE_TYPE_VIDEO_PRODUCE_SUCESS:
            // 视频生成成功
            setChangeTypeAndFinish(code);
            break;
        case DataChangeEventCode.CODE_EVENT_BUS_REDHINT:
            close = true;
            break;
        default:
            break;
        }
        if(close) {
            return;
        }
        mFinishButton.setClickable(true);
        // 清除VideoCutManager
        VideoCutManager.getInstance().release();
    }

    private void initTitle() {
        mBackBtn = findViewById(R.id.btn_back);
        mBackBtn.setOnClickListener(mOnClickListener);

        mFlashBtn = findViewById(R.id.btn_light);
        mFlashBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mCameraEngine.updateFlashMode();
                mFlashBtn.setSelected(!mCameraEngine.isFlashModeOff());
            }
        });
        mFlashBtn.setSelected(false);
        mFlashBtn.setVisibility(View.VISIBLE);

        mCameraSwitchBtn = (ImageView) findViewById(R.id.btn_camera);
        mCameraSwitchBtn.setOnClickListener(mOnClickListener);
        mCameraSwitchBtn.setEnabled(false);
        mCameraSwitchBtn.setVisibility(CameraEngine.hasFrontFacingCamera() ? View.VISIBLE : View.GONE);
    }

    @Override
    public void onBackPressed() {
        onBack();
    }

    private void onBack() {
        if (mRecordTrackView != null && mRecordTrackView.getCurProgress() > 0) {
            RemindSource source = new RemindSource("退出后，拍摄好的会没掉哦", "确定", "取消");
            final RemindDialog dialog = RemindDialog.show(VideoRecordActivity.this, source);
            dialog.setOnDialogClickListener(new OnDialogClickListener() {
                @Override
                public void onConfirm() {
                    dialog.dismiss();
                    finish();
                }

                @Override
                public void onCancel() {
                    dialog.dismiss();
                }
            });
        } else {
            finish();
        }
    }

    private OnClickListener mOnClickListener = new OnClickListener() {

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
            case R.id.btn_back: {
                onBack();
                break;
            }
            case R.id.btn_light: {
                break;
            }
            case R.id.btn_camera: {
                mCameraSwitchBtn.setEnabled(false);
                mCameraEngine.changeCamera(mCameraEngine.isFrontCamera());
                break;
            }

            default:
                break;
            }
        }
    };

    private void initVideoView() {
        // mVideoContainer = (FrameLayout) findViewById(R.id.container_video);
        if (mVideoContainer.getChildCount() == 4 && mCameraView != null) {
            mCameraView.startPreview();
            return;
        }
        // 录制界面 TODO
        // mCameraView = new CameraView(this, mCameraEngine);
        mCameraView.setOnPreviewCallback(new OnPreviewCallback() {
            @Override
            public void onPreviewFrame(byte[] data) {
                if (mRecorder != null) {
                    mRecorder.process(data);
                }
            }
        });
        Size size = mCameraEngine.getPropPreviewSize();
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int width = dm.widthPixels;
        int height = width * size.width / size.height;

        FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) mCameraView.getLayoutParams();
        lp.width = width;
        lp.height = height;
        mCameraView.setLayoutParams(lp);
        // mVideoContainer.addView(mCameraView, width, height);

        // 录制轨道
        mRecordTrackView = new RecordTrackView(this);
        int trackViewHeight = (int) (width * 0.078125);
        LayoutParams trackParams = new LayoutParams(width, trackViewHeight);
        trackParams.topMargin = width - trackViewHeight;
        mVideoContainer.addView(mRecordTrackView, trackParams);

        // 录制控制
        View barView = LayoutInflater.from(this).inflate(R.layout.bar_video_record_ctrl, null);
        LayoutParams barParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        barParams.topMargin = width;
        mVideoContainer.addView(barView, barParams);

        mRecordButton = (RecordButton) barView.findViewById(R.id.btn_record);
        mFallBackButton = (FallbackButton) barView.findViewById(R.id.btn_fall_back);
        mFinishButton = barView.findViewById(R.id.btn_finish);
        mImportButton = barView.findViewById(R.id.btn_import);
        hideEditView();

        // 录制长度提醒，在三次提醒后再也不出现
		mWarnToast = new TextToast(this, "太短了！给我继续拍", trackViewHeight);
		mWarnToast.measure(
				MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED),
				MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));

		LayoutParams warnParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		int minPoint = (int) (width * 0.3);
		warnParams.leftMargin = minPoint - mWarnToast.getMeasuredWidth()/2;
		warnParams.topMargin = (int) (width - mWarnToast.getMeasuredHeight() - mRecordTrackView.getTrackHeight()- mRecordTrackView.getTrackHeight()*0.6f);
		mVideoContainer.addView(mWarnToast, warnParams);
		mWarnToast.setVisibility(View.INVISIBLE);
    }


    private boolean haveRecordFull() {
        return mRecordTrackView.getCurProgress() >= 1.0f;
    }

    private void initEvents() {
        mRecordTrackView.setRecordTrackListener(new OnRecordTrackListener() {
	        @Override
	        public void onRecord(float progress) {
	            if (mRecordTrackView.isOverMinProgress() && !mFinishButton.isShown()) {
	                mFinishButton.setVisibility(View.VISIBLE);
	            }
	            if (progress >= 1.0 && mRecordTrackView.getTag() == null) {
	                mRecordTrackView.setTag(true);
	                mRecordTrackView.stopRecord();
	                mRecorder.stop();
	                new ConcatTask(VideoRecordActivity.this).execute(mRecorder.getOutputDir());
	                showEditView();
	                enableEditView();
	            }
	        }
	    });

        mRecordButton.setRecordClickListener(new OnRecordClickListener() {

            @Override
            public void onStart() {
                if (haveRecordFull()) {
                    new ConcatTask(VideoRecordActivity.this).execute(mRecorder.getOutputDir());
                    return;
                }

                // 启动播放
                final long startTime = System.currentTimeMillis();
                mWarnToast.setVisibility(View.INVISIBLE);
                showEditView();
                disableEditView();
                mFallBackButton.cancelSeleted();
                mRecordTrackView.startRecord();
                mRecorder.start(new VideoStartRecordListener() {
                    @Override
                    public void onStartRecord() {
                        LogUtil.d(TAG, "start time pass==========" + (System.currentTimeMillis() - startTime));
                    }
                });
            }

            @Override
            public void onStop() {
                if (haveRecordFull()) {
                    return;
                }
                // 停止播放
                mRecordTrackView.stopRecord();
                mRecorder.stop();

                if (!mRecordTrackView.isOverMinProgress()) {
                    mWarnToast.setVisibility(View.VISIBLE);
                }
                showEditView();
                enableEditView();
            }
        });

        mFallBackButton.setFallBackListener(new OnFallBackListener() {

            @Override
            public void onSelected() {
                // 轨道设置选中项
                mRecordTrackView.selected();
            }

            @Override
            public void onFallback() {
                // 轨道删除旧播放数据
                mRecordTrackView.delectedPart();
                mRecordTrackView.setTag(null);
                if (!mRecordTrackView.isOverMinProgress()) {
                    mFinishButton.setVisibility(View.INVISIBLE);
                }

                if (mRecordTrackView.getVideoPartsNum() == 0) {
                    hideEditView();
                    mWarnToast.setVisibility(View.INVISIBLE);
                }

                mRecorder.rollback();
            }
        });

        mFinishButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mFinishButton.setClickable(false);
                new ConcatTask(VideoRecordActivity.this).execute(mRecorder.getOutputDir());
            }
        });

        mImportButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                startActivity(new Intent(VideoRecordActivity.this, VideoImportActivity.class));
            }
        });
    }

    public class ConcatTask extends AsyncTask<String, Void, String> {
        private long startTime;
        private File listFile;
        private File outputFile;

        private Context context;
        private ProgressDialog dialog;

        public ConcatTask(Context context) {
            this.context = context;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = new ProgressDialog(context);
            dialog.setCancelable(false);
            dialog.setCanceledOnTouchOutside(false);
            dialog.show();
        }

        @Override
        protected String doInBackground(String... params) {
            startTime = System.currentTimeMillis();

            final File dir = new File(params[0]);

            outputFile = new File(dir, "output.mp4");
            Files.delete(outputFile.getAbsolutePath());
            LogUtil.d(TAG, "before wait4Ready elapsed:" + (System.currentTimeMillis()-startTime));
            mRecorder.wait4CompleteWithTimeout(10 * 1000);
            LogUtil.d(TAG, "wait4Ready elapsed:" + (System.currentTimeMillis()-startTime));
            final List<String> sortedFiles = mRecorder.getTmpOutputFiles();

            listFile = new File(dir, "concat.txt");

            StringBuilder sb = new StringBuilder();
            for (String file : sortedFiles) {
                // 过滤可能无效的文件
                if (new File(file).length() < 10 * 1024) {
                    continue;
                }
                sb.append("file '" + file + "'\n");
            }
            IOUtil.writeStringTo(listFile, sb.toString());
            String cmd = FfmpegUtils.getCmdPath() + " -f concat -i " + listFile.getAbsolutePath() + " -c copy " + outputFile.getAbsolutePath();
            LogUtil.d("ffmpegutils", "cmd:" + cmd);

            return cmd;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            doConcat(result);
        }

        private void doConcat(String cmd) {
            FfmpegUtils.execute(cmd, new AbsProcessListener() {

                @Override
                public void onExit(int exitCode) {
                    LogUtil.d(TAG, "concat, exitCode:" + exitCode + ", elapsed:" + (System.currentTimeMillis() - startTime) + " ms.");
                    if (exitCode == 0) {

                        // 删除合成配置文件
                        Files.delete(listFile.getAbsolutePath());

                        File outputDir = DirContext.getInstance().getOutputDir();
                        File thumbsDir = DirContext.getInstance().getThumbsDir(outputDir);

                        List<String> thumbs = mRecorder.getThumbs();

                        for (String path : thumbs) {
                            String thumb = path.substring(path.lastIndexOf(File.separator));
                            Files.copy(path, thumbsDir + File.separator + thumb);
                        }

                        File finalOutputFile = new File(outputDir, VideoConstants.OUTPUT_VIDEO);
                        Files.copy(outputFile, finalOutputFile);
                        LogUtil.d(TAG, "copy thumbs, elapsed:" + (System.currentTimeMillis()-startTime));
                        VideoManager mVideoManager = VideoManager.getInstance(VideoRecordActivity.this);

                        mVideoManager.init(thumbsDir, finalOutputFile);
                        LogUtil.d(TAG, "initVideo thumbs, elapsed:" + (System.currentTimeMillis()-startTime));
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                dialog.dismiss();
                                LaunchUtil.launchSubActivity(VideoRecordActivity.this, VideoShareFragment.class, null);
                                mFinishButton.setClickable(true);
                            }
                        });
                    } else {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                dialog.dismiss();
                                ToastUtil.showSimpleToast(VideoRecordActivity.this, "天有不测风云，可能失败了...");
                                mFinishButton.setClickable(true);
                            }
                        });
                    }
                }
            });
        }
    }

    public void watermark(String input) {
        final long startTime = System.currentTimeMillis();
        String watermark = DirContext.getInstance().getRootDir() + File.separator + "watermark.png";
        Utils.installBinaryFromRaw(this, R.raw.watermark, new File(watermark));
        String output = input.substring(0, input.lastIndexOf(File.separator)) + "wm.mp4";
        long duration = FFmpeg.getMediaFileDuration(input) / 1000;// in second
        String cmd = FfmpegUtils.getCmdPath() + " -i " + input + " -i " + watermark + " -filter_complex "
                + "\"overlay=(W-w)/2:(H-h)/2:enable='gte(t,"+(duration-1)+")'\" -y " + output;
        LogUtil.d(TAG, "watermark cmd:" + cmd);
        FfmpegUtils.execute(cmd, new AbsProcessListener() {
            @Override
            public void onExit(int exitCode) {
                LogUtil.d(TAG, "watermark, onExit code:" + exitCode + ", elapsed:" + (System.currentTimeMillis()-startTime));
            }
        });
    }

    /**
     * 显示回退和结束录制按钮
     */
    private void showEditView() {
        mImportButton.setVisibility(View.GONE);
        mFallBackButton.setVisibility(View.VISIBLE);
        if (mRecordTrackView.isOverMinProgress()) {
            mFinishButton.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 隐藏回退和结束录制按钮
     */
    private void hideEditView() {
        mFallBackButton.setVisibility(View.GONE);
        mFinishButton.setVisibility(View.GONE);
        mImportButton.setVisibility(View.VISIBLE);
    }

    /**
     * 允许回退和结束录制
     */
    private void enableEditView() {
        mFallBackButton.setClickable(true);
        mFinishButton.setClickable(true);
    }

    /**
     * 不允许回退和结束录制
     */
    private void disableEditView() {
        mFallBackButton.setClickable(false);
        mFinishButton.setClickable(false);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mFlashBtn.isSelected()) {
            mFlashBtn.setSelected(false);
            mCameraEngine.updateFlashMode();
        }
        if (mWakeLock != null) {
            mWakeLock.release();
            mWakeLock = null;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mWakeLock == null) {
            PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
            mWakeLock = pm.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK, "feibo");
            mWakeLock.acquire();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mCameraEngine.stopPreview(true);
        mCameraEngine = null;

        if (mRecorder != null) {
            final String optPath = mRecorder.getOutputDir();
            CleanService.cleanFile(AppContext.getContext(), optPath);
            mRecorder.release();
            mRecorder = null;
        }
    }
}
