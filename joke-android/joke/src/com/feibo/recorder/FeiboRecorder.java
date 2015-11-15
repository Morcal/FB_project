package com.feibo.recorder;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Queue;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import fbcore.log.LogUtil;
import fbcore.utils.Files;
import fbcore.utils.Strings;

import com.feibo.ffmpeg.FFmpegRecorder;
import com.feibo.ffmpeg.FfmpegUtils;
import com.feibo.ffmpeg.ProcessRunnable.AbsProcessListener;
import com.feibo.joke.app.DirContext;
import com.feibo.recorder.AudioRecorder.AudioConvertListener;

public class FeiboRecorder {
    private static final String TAG = FeiboRecorder.class.getSimpleName();
    private static final String TMP = "tmp";
    // 当前录制文件存放的目录
    private String dir;

    private VideoRecorder videoRecorder;
    private AudioRecorder audioRecorder;
    private RecorderParameters parameters;

    private UserAction userAction;
    // 当前录制，最终输出的视频文件
    private String curOutputFile;

    private Map<String, Processer> processerMap = new HashMap<String, FeiboRecorder.Processer>();

    private State curState = State.IDLE;

    public enum State {
        IDLE("空闲"),
        PREPARED("已准备"),
        RECORDING("正在录制"),
        STOPPED("已停止"),
        RELEASED("已销毁");

        private String state;

        State(String state) {
            this.state = state;
        }

        String valueOf() {
            return this.state;
        }
    }

    private void setState(State state) {
        Log.d(TAG, "preState:" + curState.valueOf() + ", postState:" + state.valueOf());
        this.curState = state;
    }

    /**
     * 初始化录制参数等.
     */
    public void init(RecorderParameters parameters) {
        this.parameters = parameters;
        // 初始化本次录制的相关参数，如初始所有视频的存放文件路径等
        dir = DirContext.getInstance().getTmpDir() + File.separator + System.currentTimeMillis();
        userAction = new UserAction();

        // 开启音频录制线程
        audioRecorder = new AudioRecorder(parameters);
        audioRecorder.start();

        setState(State.PREPARED);
    }

    private int which = 0;

    public void setCarema(int which) {
        this.which = which;
    }

    /**
     * 开始录制.
     */
    public void start(final VideoStartRecordListener listener) {
        final long now = System.currentTimeMillis();
        curOutputFile = dir + File.separator + now + ".mp4";
        final String filePrefix = dir + File.separator + now + TMP;

        // 根据参数新建视频录制对象，并开始录制

        VideoRecorder vr = new VideoRecorder(parameters, which);

        Processer processer = new Processer(curOutputFile, filePrefix, String.valueOf(now), vr, audioRecorder);
        processerMap.put(curOutputFile, processer);
        processer.start();

        videoRecorder = vr;
        // 开启音频录制开关
        audioRecorder.recording(filePrefix);
        setState(State.RECORDING);
    }

    /**
     * 停止录制
     */
    public void stop() {
        if (!isRecording()) {
            return;
        }

        userAction.save(curOutputFile);
        processerMap.get(curOutputFile).stop();

        setState(State.STOPPED);
    }

    public void wait4CompleteWithTimeout(long timeout) {
        List<String> files = userAction.getFiles();

        boolean isReady = false;
        while (!isReady) {
            int count = 0;
            for (String file : files) {
                if (userAction.isDone(file)) {
                    count++;
                }
            }
            LogUtil.i(TAG, "file " + count + "  " + files.size());
            if (count == files.size()) {
                isReady = true;
            }

            if (isReady) {
                LogUtil.i(TAG, "finish");
                break;
            }

            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        clearProcessers();
    }

    private void clearProcessers() {
        if(processerMap != null) {
            for (Processer processer : processerMap.values()) {
                processer.destroy();
            }
            processerMap.clear();
        }
    }

    /**
     * 录制完成，做清理工作
     */
    public void release() {
        // 停止录制
        videoRecorder = null;

        // 停止音频录制线程
        audioRecorder.stopRecording();
        audioRecorder = null;

        userAction = null;

        clearProcessers();

        setState(State.RELEASED);
    }

    public void process(Object data) {
        if (isRecording() && data instanceof byte[] && data != null) {
            processerMap.get(curOutputFile).process((byte[]) data);
        }
    }

    /**
     * 当前是否在录制.
     * @return
     */
    public boolean isRecording() {
        return this.curState == State.RECORDING;
    }

    public String getOutputDir() {
        return dir;
    }

    public List<String> getTmpOutputFiles() {
        return userAction.getFiles();
    }

    public List<String> getThumbs() {
        File thumbsDir = new File(dir + File.separator + ".thumbs");
        String[] allFiles = thumbsDir.list(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String filename) {
                // 不符合缩略图命名规则的删除
                if (!filename.matches("\\d+\\.\\d+\\.jpg")) {
                    return false;
                }
                String[] subs = filename.split("\\.");
                for (String tsDir : userAction.getFiles()) {
                    if (tsDir.contains(subs[0])) {
                        return true;
                    }
                }
                return false;
            }
        });


        List<String> sorted = new ArrayList<String>();
        if (allFiles != null) {
            sorted = Arrays.asList(allFiles);
        }

        Collections.sort(sorted, new Comparator<String>() {
            @Override
            public int compare(String lhs, String rhs) {
                String[] lsubs = lhs.split("\\.");

                long l0 = Strings.toLong(lsubs[0]);
                long l1 = Strings.toLong(lsubs[1]);

                String[] rsubs = rhs.split("\\.");
                long r0 = Strings.toLong(rsubs[0]);
                long r1 = Strings.toLong(rsubs[1]);

                if (l0 != r0) {
                    return l0>r0 ? 1 : -1;
                } else {
                    if (l1 != r1) {
                        return l1>r1 ? 1 : -1;
                    }
                }
                return 0;
            }
        });

        List<String> ret = new ArrayList<String>();

        for (String file : sorted) {
            ret.add(thumbsDir + File.separator + file);
        }

        return ret;
    }

    public File getThumbsDir() {
        File thumbsDir = new File(dir + File.separator + ".thumbs");

        String[] allFiles = thumbsDir.list(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String filename) {
                // 不符合缩略图命名规则的删除
                if (!filename.matches("\\d+\\.\\d+\\.jpg")) {
                    return true;
                }
                String[] subs = filename.split("\\.");
                for (String tsDir : userAction.getFiles()) {
                    if (tsDir.contains(subs[0])) {
                        return false;
                    }
                }
                return true;
            }
        });

        if (allFiles == null || allFiles.length == 0) {
            return thumbsDir;
        }
        for (String file : allFiles) {
            Files.delete(thumbsDir + File.separator + file);
        }
        return thumbsDir;
    }

    public void rollback() {
        userAction.delete();
    }

    public static interface VideoStartRecordListener {
        void onStartRecord();
    }

    public static interface VideoStopRecordListener {
        void onStopRecord();
    }

    private class VideoRecorder {
        private long frameTime;
        private FFmpegRecorder ffmpegRecorder;
        private long videoTimestamp = 0;
        private int imageWidth;
        private int imageHeight;
        private SavedFrames savedFrames;
        private String outputFile;
        private RecorderParameters parameters;
        private int which = 0;

        private Object mVideoRecordLock = new Object();

        private VideoRecorder(RecorderParameters parameters, int which) {
            this.parameters = parameters;
            this.which = which;
        }

        private void record(String filePrefix, String ts, VideoStartRecordListener listener) {
            outputFile = filePrefix + ".mp4";
            int videoWidth = 480;
            int videoHeight = 480;
            this.imageWidth = 640;
            this.imageHeight = 480;
            frameTime = 100000L / 15;
            ffmpegRecorder = new FFmpegRecorder(outputFile, videoWidth, videoHeight);
            ffmpegRecorder.setSaveImagePath(dir + File.separator + ".thumbs", ts+".");
            ffmpegRecorder.setRecorderParameters(parameters);
            ffmpegRecorder.setPreviewSize(imageWidth, imageHeight);
            ffmpegRecorder.start();
            if (listener != null) {
                listener.onStartRecord();
            }
        }

        private void process(byte[] data) {
            synchronized (mVideoRecordLock) {
                if (savedFrames != null && savedFrames.getFrameBytesData() != null) {
                    videoTimestamp += frameTime;
                    if (savedFrames.getTimeStamp() > videoTimestamp) {
                        videoTimestamp = savedFrames.getTimeStamp();
                    }
                    try {
                        ffmpegRecorder.record(savedFrames.getFrameBytesData());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                byte[] tmp = null;
                if (which == 0) {
                    tmp = rotateYUV420Degree90(data, imageWidth, imageHeight);
                } else {
                    tmp = rotateYUV420Degree270(data, imageWidth, imageHeight);
                }
                savedFrames = new SavedFrames(tmp, videoTimestamp);
            }
        }

        private void stop(VideoStopRecordListener listener) {
            ffmpegRecorder.stop();
            if (listener != null) {
                listener.onStopRecord();
            }
        }
    }

    public static byte[] rotateYUV420Degree270(byte[] data, int imageWidth, int imageHeight) {
        byte[] yuv = new byte[imageWidth * imageHeight * 3 / 2];
        int nWidth = 0, nHeight = 0;
        int wh = 0;
        int uvHeight = 0;
        if (imageWidth != nWidth || imageHeight != nHeight) {
            nWidth = imageWidth;
            nHeight = imageHeight;
            wh = imageWidth * imageHeight;
            uvHeight = imageHeight >> 1;// uvHeight = height / 2
        }

        // 旋转Y
        int k = 0;
        for (int i = 0; i < imageWidth; i++) {
            int nPos = 0;
            for (int j = 0; j < imageHeight; j++) {
                yuv[k] = data[nPos + i];
                k++;
                nPos += imageWidth;
            }
        }

        for (int i = 0; i < imageWidth; i += 2) {
            int nPos = wh;
            for (int j = 0; j < uvHeight; j++) {
                yuv[k] = data[nPos + i];
                yuv[k + 1] = data[nPos + i + 1];
                k += 2;
                nPos += imageWidth;
            }
        }
        // 这一部分可以直接旋转270度，但是图像颜色不对
        // // Rotate the Y luma
        // int i = 0;
        // for(int x = imageWidth-1;x >= 0;x--)
        // {
        // for(int y = 0;y < imageHeight;y++)
        // {
        // yuv[i] = data[y*imageWidth+x];
        // i++;
        // }
        //
        // }
        // // Rotate the U and V color components
        // i = imageWidth*imageHeight;
        // for(int x = imageWidth-1;x > 0;x=x-2)
        // {
        // for(int y = 0;y < imageHeight/2;y++)
        // {
        // yuv[i] = data[(imageWidth*imageHeight)+(y*imageWidth)+x];
        // i++;
        // yuv[i] = data[(imageWidth*imageHeight)+(y*imageWidth)+(x-1)];
        // i++;
        // }
        // }
        return rotateYUV420Degree180(yuv, imageWidth, imageHeight);
    }

    public static byte[] rotateYUV420Degree180(byte[] data, int imageWidth, int imageHeight) {
        byte[] yuv = new byte[imageWidth * imageHeight * 3 / 2];
        int i = 0;
        int count = 0;

        for (i = imageWidth * imageHeight - 1; i >= 0; i--) {
            yuv[count] = data[i];
            count++;
        }

        i = imageWidth * imageHeight * 3 / 2 - 1;
        for (i = imageWidth * imageHeight * 3 / 2 - 1; i >= imageWidth * imageHeight; i -= 2) {
            yuv[count++] = data[i - 1];
            yuv[count++] = data[i];
        }
        return yuv;
    }

    public static byte[] rotateYUV420Degree90(byte[] data, int imageWidth, int imageHeight) {

        byte[] yuv = new byte[imageWidth * imageHeight * 3 / 2];
        // Rotate the Y luma
        int i = 0;
        for (int x = 0; x < imageWidth; x++) {
            for (int y = imageHeight - 1; y >= 0; y--) {
                yuv[i] = data[y * imageWidth + x];
                i++;
            }

        }
        // Rotate the U and V color components
        i = imageWidth * imageHeight * 3 / 2 - 1;
        for (int x = imageWidth - 1; x > 0; x = x - 2) {
            for (int y = 0; y < imageHeight / 2; y++) {
                yuv[i] = data[(imageWidth * imageHeight) + (y * imageWidth) + x];
                i--;
                yuv[i] = data[(imageWidth * imageHeight) + (y * imageWidth) + (x - 1)];
                i--;
            }
        }
        return yuv;
    }

    public class UserAction {
        private List<String> videos = new ArrayList<String>();
        private Map<String, Boolean> map = new HashMap<String, Boolean>();

        private void save(String file) {
            videos.add(file);
            map.put(file, false);
        }

        private boolean isDone(String file) {
            Boolean value = map.get(file);
            return value == null || value;
        }

        private void finish(String file) {
            // 如果是被删除的，则无效
            if (map.get(file) != null) {
                map.put(file, true);
            }
        }

        private void delete() {
            int lastIndex = videos.size()-1;
            if (lastIndex >= 0) {
                map.remove(videos.get(lastIndex));
                videos.remove(lastIndex);
            }
        }

        private List<String> getFiles() {
            return videos;
        }
    }

    private class ProcessData {
        int type;
        byte[] data;

        public ProcessData(int type, byte[] data) {
            this.type = type;
            this.data = data;
        }
    }

    private class Processer {
        VideoRecorder vr;
        AudioRecorder ar;
        HandlerThread ht;
        ProcesserH ph;

        private String filePrefix;
        private String ts;

        private int AUDIO_FLAG = 0x1;
        private int VIDEO_FLAG = 0x2;
        private int flag = 0x0;
        private long startTime;

        Queue<ProcessData> queue = new LinkedList<FeiboRecorder.ProcessData>();

        public Processer(String tag, String filePrefix, String ts, VideoRecorder vr, AudioRecorder ar) {
            this.filePrefix = filePrefix;
            this.ts = ts;
            this.vr = vr;
            this.ar = ar;
            ht = new HandlerThread(tag);
            ht.start();
            ph = new ProcesserH(ht.getLooper());
            startTime = System.currentTimeMillis();
        }

        private static final int MSG_START = 0;
        private static final int MSG_STARTED = 1;
        private static final int MSG_AUDIO_READY = 2;

        private static final int TYPE_DATA = 0;
        private static final int TYPE_STOP = 1;

        private class ProcesserH extends Handler {
            public ProcesserH(Looper looper) {
                super(looper);
            }

            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch(msg.what) {
                case MSG_START: {
                    vr.record(filePrefix, String.valueOf(ts), null);
                    sendEmptyMessage(MSG_STARTED);
                    break;
                }
                case MSG_STARTED: {
                    handleQueue();
                    break;
                }
                case MSG_AUDIO_READY: {
                    flag |= AUDIO_FLAG;
                    muxIfReady();
                    break;
                }
                }
            }
        }

        private void destroy() {
            ph.removeCallbacksAndMessages(null);
            vr = null;
            ar = null;
            ht.quit();
            ht = null;
            queue.clear();
            queue = null;
            ph = null;
        }

        private void start() {
            Message msg = ph.obtainMessage(MSG_START);
            msg.sendToTarget();
        }

        private void handleQueue() {
            LogUtil.d(TAG, "msg_start_handle_queue");
            while (true) {
                ProcessData pd = null;

                try {
                    pd = queue.poll();
                } catch (NoSuchElementException e) {
                    break;
                }

                if (pd != null) {
                    if (pd.type == TYPE_DATA) {
                        vr.process(pd.data);
                    } else if (pd.type == TYPE_STOP) {
                        LogUtil.i(TAG, "process stop");
                        flag |= VIDEO_FLAG;
                        vr.stop(null);
                        muxIfReady();
                        break;
                    }
                }
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        private void process(byte[] data) {
            queue.offer(new ProcessData(TYPE_DATA, data));
        }

        private void stop() {
            ar.pause(new AudioConvertListener() {
                @Override
                public void onSuccess(String filePrefix, String filePath) {
                    ph.sendEmptyMessage(MSG_AUDIO_READY);
                }

                @Override
                public void onFail(String filePrefix) {
                    ph.sendEmptyMessage(MSG_AUDIO_READY);
                }
            });
            queue.offer(new ProcessData(TYPE_STOP, null));
        }

        private void deleteTmpFile(final String filePrefix) {
            final File dirFile = new File(dir);
            String[] files = dirFile.list(new FilenameFilter() {
                @Override
                public boolean accept(File dir, String filename) {
                    String filePath = dir.getAbsolutePath() + File.separator + filename;
                    return filePath.startsWith(filePrefix);
                }
            });

            if (files == null || files.length == 0) {
                return;
            }
            for (String fileName : files) {
                File file = new File(dir, fileName);
                if (file.exists()) {
                    file.delete();
                }
            }
        }

        private void muxIfReady() {
            if (flag != (AUDIO_FLAG | VIDEO_FLAG)) {
                return;
            }

            final String videoFilePath = filePrefix + ".mp4";
            final String outFilePath = filePrefix.substring(0, filePrefix.length()-3) + ".mp4";

            String cmd = FfmpegUtils.getCmdPath() + " -i " + videoFilePath + " -i " + filePrefix + ".wav"
                    + " -c:v copy -c:a aac -strict experimental " + outFilePath;

            LogUtil.d(TAG, "merge cmd:" + cmd);

            FfmpegUtils.execute(cmd, new AbsProcessListener() {
                @Override
                public void onExit(int exitCode) {
                    LogUtil.d(TAG, "merge onExit, code:" + exitCode + ", mux elapsed:" + (System.currentTimeMillis() - startTime) + " ms");
                    if (exitCode == 0) {
                        userAction.finish(outFilePath);
                        deleteTmpFile(filePrefix);
                    }
                }
            });
        }
    }
}
