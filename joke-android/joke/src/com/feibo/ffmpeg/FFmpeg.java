package com.feibo.ffmpeg;

import java.io.File;
import java.util.List;

import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;

import fbcore.log.LogUtil;

/**
 * ffmpeg功能调用，建议在异步线程中调用。
 * @author hcy
 *
 */
public class FFmpeg {

    private static final String TAG = "FFmpeg";
    private static boolean isLoaded;

    static{
        loadLibs();
    }

    public FFmpeg() {
        if (!isLoaded) {
            loadLibs();
        }
    }

    private static boolean loadLibs() {
        if (isLoaded) {
            return true;
        }
        boolean err = false;
        try {
            /*System.loadLibrary("avutil-54");
            System.loadLibrary("avcodec-56");
            System.loadLibrary("swresample-1");
            System.loadLibrary("avformat-56");
            System.loadLibrary("swscale-3");
            System.loadLibrary("avfilter-5");
            System.loadLibrary("postproc-53");
            System.loadLibrary("ffmpeg_codec");*/
            System.loadLibrary("ffmpeg");
            System.loadLibrary("engine");
        } catch (UnsatisfiedLinkError e) {
            err = true;
            Log.i(TAG, "UnsatisfiedLinkError " + e.getMessage());
        }
        if (!err) {
            isLoaded = true;
        }
        return isLoaded;
    }

    private static void deleteFile(String filePath) {
        File file = new File(filePath);
        if (file.exists()) {
            file.delete();
        }
    }

    public static boolean deMuxer(String srcFilePath, String videoFilePath, String audioFilePath) {
        if (TextUtils.isEmpty(srcFilePath) || TextUtils.isEmpty(audioFilePath) || TextUtils.isEmpty(videoFilePath)) {
            return false;
        }
        deleteFile(videoFilePath);
        deleteFile(audioFilePath);
        int res = native_demuxer(srcFilePath, videoFilePath, audioFilePath);
        return res >= 0;
    }

    public static boolean transcodeVedio(String srcFilePath, String outFilePath) {
        boolean res = false;
        String dirPath = Environment.getExternalStorageDirectory().getAbsolutePath();
        String tmpFilePath = dirPath + File.separator + "33/12.yuv";
        res = decode(srcFilePath, tmpFilePath);
        Log.i(TAG, "decode : " + res);
        if (res) {
            res = encode(srcFilePath, tmpFilePath, outFilePath);
        }
        return res;
    }

    public static boolean transcodeAudio(String srcFilePath, String outFilePath) {
        deleteFile(outFilePath);
        return native_audio_transcode_aac(srcFilePath, outFilePath) >= 0;
    }

    public static boolean decode(String srcFilePath, String outFilePath) {
        if (TextUtils.isEmpty(srcFilePath) || TextUtils.isEmpty(outFilePath)) {
            return false;
        }
        deleteFile(outFilePath);
        int res = native_decode_format(srcFilePath, outFilePath);
        return res >= 0;
    }

    public static boolean encode(String decodeSrcFilePath, String srcFilePath, String outFilePath) {
        if (TextUtils.isEmpty(srcFilePath) || TextUtils.isEmpty(outFilePath)) {
            return false;
        }
        deleteFile(outFilePath);
        int res = native_encode(decodeSrcFilePath, srcFilePath, outFilePath);
        return res >= 0;
    }

    public static boolean muxer(String outFilePath, String videoFilePath, String audioFilePath) {
        if (TextUtils.isEmpty(outFilePath) || TextUtils.isEmpty(audioFilePath) || TextUtils.isEmpty(videoFilePath)) {
            return false;
        }
        deleteFile(outFilePath);
        int res = native_muxer(videoFilePath, audioFilePath, outFilePath);
        return res >= 0;
    }

    public static boolean addWaterMark(List<WaterMark> marks, String srcFilePath, String outFilePath) {
        if (TextUtils.isEmpty(outFilePath) || TextUtils.isEmpty(srcFilePath)) {
            return false;
        }
        if (marks == null || marks.size() <= 0) {
            return false;
        }
        deleteFile(outFilePath);
        return native_add_waterMark(srcFilePath, outFilePath, marks) >= 0;
    }

    public static boolean saveFrameImage(String srcVideoPath, String outFileDir, String filePrefix) {
        if (TextUtils.isEmpty(srcVideoPath) || TextUtils.isEmpty(outFileDir) || TextUtils.isEmpty(filePrefix)) {
            return false;
        }
        long start = System.currentTimeMillis();
        File dir = new File(outFileDir);
        if (!dir.isDirectory() || !dir.exists()) {
            dir.mkdirs();
        }

        boolean  ret = native_save_frame_image(srcVideoPath, outFileDir, filePrefix) >= 0;
        LogUtil.d(TAG, "save all frame elapsed:" + (System.currentTimeMillis()-start) + " ms");
        return ret;
    }

    public static boolean saveFrameImage(String srcVideoPath, String outFileDir, String filePrefix, int imagesCount) {
        if (TextUtils.isEmpty(srcVideoPath) || TextUtils.isEmpty(outFileDir) || TextUtils.isEmpty(filePrefix) || imagesCount < 1) {
            return false;
        }
        return native_save_frame_image_4_set_images_count(srcVideoPath, outFileDir, filePrefix, imagesCount) >= 0;
    }

    /**
     * 裁剪视频
     * @param inputFilePath 输入的文件位置
     * @param outFilePath 输出的文件位置
     * @param startTime 裁剪的起始位置，以毫秒为单位
     * @param endTime 裁剪的终止位置，以毫秒为单位
     * @return
     */
    public static boolean splitVideo(String inputFilePath, String outFilePath, int startTime, int endTime) {
        if (TextUtils.isEmpty(inputFilePath) || TextUtils.isEmpty(outFilePath)) {
            return false;
        }
        if (startTime < 0 || endTime < 0 || startTime >= endTime) {
            return false;
        }
        File inFile = new File(inputFilePath);
        if (!inFile.exists()) {
            return false;
        }
        File outFile = new File(outFilePath);
        if (outFile.exists()) {
            outFile.delete();
        }
        return native_split(inputFilePath, outFilePath, startTime, endTime) >= 0;
    }

    /**
     *
     * @param filePath
     * @return 返回媒体文件的时间，单位为毫秒  return > 0
     */
    public static long getMediaFileDuration(String filePath) {
        return native_get_media_file_duration(filePath);
    }

    private static native int native_demuxer(String srcFilePath, String videoFilePath, String audioFilePath);

    private static native int native_decode(String srcFilePath, String outFilePath);

    private static native int native_encode(String decodeSrcFilePath, String srcFilePath, String outFilePath);

    private static native int native_muxer(String videoFilePath, String audioFilePath, String outFilePath);

    private static native int native_decode_format(String srcFilePath, String outFilePath);

    private static native int native_audio_transcode_aac(String srcFilePath, String outFilePath );

    private static native int native_add_waterMark(String srcFilePath, String outFilePath, List<WaterMark> waterMarks);

    private static native int native_save_frame_image(String srcVideoFilePath, String outFileDir, String filePrefix);

    private static native int native_save_frame_image_4_set_images_count(String srcVideoFilePath, String outFileDir, String filePrefix, int imagesCount);

    private static native int native_split(String srcFilePath, String outFilePath, int startTime, int endTime);

    private static native long native_get_media_file_duration(String filePath);

}
