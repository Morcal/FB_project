package com.feibo.ffmpeg;

import java.io.File;
import java.io.FileOutputStream;

import fbcore.log.LogUtil;
import fbcore.utils.Files;

import com.feibo.recorder.RecorderParameters;

public class FFmpegRecorder extends FFmpeg {
    private static final String TAG = FFmpegRecorder.class.getSimpleName();
    private RecorderParameters parameters;
    private FileOutputStream fos;
    private long key = -1;

    public FFmpegRecorder(String outFilePath, int videoWidth, int videoHeight) {
        super();
        key = native_recorder_init(outFilePath, videoWidth, videoHeight);
        setRecorderParameters(new RecorderParameters());
    }

    /**
     * 设置视频录制过程中生成缩略图的路径
     * @param fileDir
     * @param filePrefix
     */
    public void setSaveImagePath(String fileDir, String filePrefix) {
        File dir = new File(fileDir);
        int i = 0;
        while (i < 3) {
            if (Files.mkdir(fileDir)){
                break;
            }
            i++;
        }

        LogUtil.d(TAG, "try " + i + " times, setSaveImagePath:" + fileDir + ", exist?" + dir.exists());
        native_recorder_set_save_image(key, fileDir, filePrefix);
    }

    public void setRecorderParameters(RecorderParameters parameters) {
        this.parameters = parameters;
        native_recorder_init_audio(key, parameters.getAudioSamplingRate(), parameters.getAudioChannel(), parameters.getAudioBitrate());
        native_recorder_init_video(key, parameters.getVideoFrameRate());
    }

    public RecorderParameters getRecorderParameters() {
        return parameters;
    }

    public synchronized void start() {
        native_recorder_start(key);
    }

    public void setPreviewSize(int previewWidth, int previewHeight) {
        //后置摄像头旋转90
        native_recorder_set_preview_size(key, previewHeight, previewWidth);
    }

    /**
     *
     * @param data
     * @return 返回输出帧的位置，通过setSaveImagePath(String fileDir, String filePrefix);
     * 组合得到帧对应的图片位置。
     */
    public synchronized int record(byte[] data) {
        return native_recorder_video(key, data);
    }

    public synchronized void stop() {
        native_recorder_stop(key);
    }

    private native long native_recorder_init(String outFilePath, int imageWidth, int imageHeight);

    private native int native_recorder_start(long key);

    private native int native_recorder_video(long key, byte[] data);

    private native int native_recorder_stop(long key);

    private native void native_recorder_set_preview_size(long key, int previewWidth, int previewHeight);

    private native int native_recorder_audio(long key, byte[] data, int inputSize);

    private native void native_recorder_init_audio(long key, int sampleRate, int channelLayout, int bitRate);

    private native void native_recorder_init_video(long key, int videoFrameRate);

    private native void native_recorder_set_save_image(long key, String fileDir, String filePrefix);

}
