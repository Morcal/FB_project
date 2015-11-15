package com.feibo.ffmpeg;

import java.io.File;
import java.io.IOException;

import android.content.Context;
import android.util.Log;

import com.feibo.ffmpeg.ProcessRunnable.ProcessListener;
import com.feibo.joke.R;

public class FfmpegUtils {
    private static final String TAG = FfmpegUtils.class.getSimpleName();

    private static String FFMPEG_LIB_PATH;

    static {
        System.loadLibrary("ffmpeg");
    }

    public static String getCmdPath() {
        return mFfmpegInstallPath;
    }

    private static native int exec(String[] args);

    public static void execute(String cmd, ProcessListener listener) {
        String[] args = cmd.split(" ");
        final ProcessBuilder pb = new ProcessBuilder(args);
        pb.environment().put("LD_LIBRARY_PATH", FFMPEG_LIB_PATH);
        ProcessRunnable runnable = new ProcessRunnable(pb);
        runnable.setProcessListener(listener);
        new Thread(runnable).start();
    }

    private static String mFfmpegInstallPath = null;

    public static void installFfmpeg(Context context) {

        FFMPEG_LIB_PATH = context.getApplicationInfo().nativeLibraryDir;

        File ffmpegFile = new File(context.getCacheDir(), "ffmpegcmd");
        mFfmpegInstallPath = ffmpegFile.toString();
        Log.d(TAG, "ffmpeg install path: " + mFfmpegInstallPath + ", ffmpeg lib path:" + FFMPEG_LIB_PATH);

        if (ffmpegFile.exists()) {
            ffmpegFile.delete();
        }

        try {
            ffmpegFile.createNewFile();
        } catch (IOException e) {
            Log.e(TAG, "Failed to create new file!", e);
        }
        Utils.installBinaryFromRaw(context, R.raw.ffmpegcmd, ffmpegFile);

        ffmpegFile.setExecutable(true);
    }
}
