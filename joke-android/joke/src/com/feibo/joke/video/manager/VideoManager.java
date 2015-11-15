package com.feibo.joke.video.manager;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Looper;

import fbcore.log.LogUtil;
import fbcore.utils.Strings;

import com.feibo.ffmpeg.FFmpeg;
import com.feibo.joke.app.AppContext;
import com.feibo.joke.app.DirContext;
import com.feibo.joke.video.manager.VideoDraftManager.Draft;
import com.feibo.joke.video.manager.VideoDraftManager.OnDraftSaveListener;
import com.feibo.joke.view.util.MessageHintManager;

/**
 * 帧数据管理，在每一次视频录制结束后并解码就要执行初始化，每一次提交就要销毁内存
 *
 * @author Lidiqing
 */
public class VideoManager {
    private static String TAG = "VideoManager";
    public final static int FRAME_NUM = 50;
    private static final int INTRODUCE_MAX = 140;

    private static VideoManager manager;

    public static VideoManager getInstance(Context context) {
        if (manager == null) {
            manager = new VideoManager(context);
        }
        return manager;
    }

    // 帧列表
    private List<File> mFrameFiles;
    private List<Bitmap> mFrameBitmaps;
    private int mFrameBitmapNum;

    // 封面
    private int mCoverBitmapPosition;
    private int mCoverFilePosition;
    // 草稿封面，在重设封面后取消，下一版本加入视频编辑页后，放弃草稿封面变量的处理
    @Deprecated
    private Bitmap mCoverDraftBitmap;
    @Deprecated
    private File mCoverDraftFile;
    // 视频
    private File mVideoFile;
    // 简介
    private String mIntroduce;

    private long mVideoDuration;
    private float mRatio;
    // 是否需要上传
    private boolean mShouldUpload;

    // 同步到微博的标记
    private boolean needShareToSina;

    // 是否已经保存
    private boolean mHasSave;

    // 保存的草稿
    private Draft mDraft;

    private String mDraftDir;

    private Handler mMainThreadHandler;

    private VideoManager(Context context) {
        mFrameFiles = new ArrayList<File>();
        mFrameBitmaps = new ArrayList<Bitmap>();
        mMainThreadHandler = new Handler(Looper.getMainLooper());
        initVideoDatas();
    }

    private int getPosition(float progress) {
        if (mFrameBitmapNum == 0) {
            return 0;
        }
        return (int) ((mFrameBitmapNum - 1) * progress);
    }

    private void initVideoDatas() {
        mCoverBitmapPosition = 0;
        mIntroduce = "";
        mShouldUpload = false;
        mHasSave = false;
        needShareToSina = false;
        mVideoDuration = 10000;
        mRatio = 1.0f;
        mDraft = null;
        mDraftDir = null;
    }

    /**
     * 读取帧
     */
    private void readFrames() {
        LogUtil.i(TAG, "initFrames: fileNums:" + mFrameFiles.size());

        mFrameBitmapNum = FRAME_NUM;
        if (mFrameFiles.size() < mFrameBitmapNum) {
            mFrameBitmapNum = mFrameFiles.size();
        }

        // 步长
        double step = mFrameFiles.size() * 1.0 / mFrameBitmapNum;
        double offset = 0;

        BitmapFactory.Options options = new BitmapFactory.Options();
        // 压缩图像
        options.inPreferredConfig = Config.RGB_565;
        options.inSampleSize = 2;

        for (int i = 0; i < mFrameBitmapNum; i++) {
           LogUtil.i(TAG, "frame:" + i + "; totalMemory:" + (Runtime.getRuntime().totalMemory() / 1024 / 1024)
                   + "; freeMemory:" + (Runtime.getRuntime().freeMemory() / 1024 / 1024));
            Bitmap bitmap = BitmapFactory.decodeFile(mFrameFiles.get((int)offset).getAbsolutePath(), options);
            mFrameBitmaps.add(bitmap);
            offset += step;
        }

        LogUtil.i(TAG, "frame size:" + mFrameBitmaps.size());

        // 根据第一帧获取视频宽高比
        Bitmap bitmap = mFrameBitmaps.get(0);
        if (bitmap != null) {
            mRatio = bitmap.getWidth() / (float) bitmap.getHeight();
        }
    }

    private void getThumbs(File thumbsDir) {
        String[] allFiles = thumbsDir.list(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String filename) {
                // 不符合缩略图命名规则的删除
                return filename.matches("\\d+\\.\\d+\\.jpg");
            }
        });

        if (allFiles == null || allFiles.length == 0) {
            LogUtil.d(TAG, "there is no valid thumbs in " + thumbsDir);
            return;
        }

        List<String> files = new ArrayList<String>();

        if (allFiles != null) {
            files = Arrays.asList(allFiles);
        }

        Collections.sort(files, new Comparator<String>() {
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

        for (String file : files) {
            LogUtil.d(TAG, "file:" + file);
            mFrameFiles.add(new File(thumbsDir, file));
        }
    }

    /**
     * 初始化帧，同步
     * 所有编辑操作的入口，包括分享/发布，封面选择及后续版本的编辑.
     * @param frameDir
     */
    public boolean init(File frameDir, File videoFile) {
        mFrameBitmapNum = FRAME_NUM;
        clearBitmaps();
//        File[] files = frameDir.listFiles();
        /*String[] files = frameDir.list(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String filename) {
                return false;
            }
        });*/
        mFrameFiles.clear();

        getThumbs(frameDir);

        readFrames();
        initVideoDatas();
        mVideoFile = videoFile;
        mDraftDir = frameDir.getParent();
        return true;
    }

    private class DraftParser implements Runnable {
        private Draft draft;
        private OnParseDraftListener listener;

        public DraftParser(Draft draft, OnParseDraftListener listener) {
            this.draft = draft;
            this.listener = listener;
        }

        @Override
        public void run() {
            File draftDir = new File(draft.dir);
            File thumbsDir = DirContext.getInstance().getThumbsDir(draftDir);
            String [] subs = thumbsDir.list();

            String videoPath = draft.videoPath;
            if (subs == null || subs.length == 0) {
                if (!FFmpeg.saveFrameImage(videoPath, thumbsDir.getAbsolutePath(), System.currentTimeMillis() + ".")) {
                    mMainThreadHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            listener.onFail();
                        }
                    });
                    return;
                }
            }

            init(thumbsDir, new File(videoPath));
            // 初始化草稿
            setDraft(draft);
            setHasSave(true);
            setIntroduce(draft.content);
            // 初始化封面
            mCoverDraftFile = new File(draft.coverPath);
            mCoverDraftBitmap = BitmapFactory.decodeFile(draft.coverPath);
            mMainThreadHandler.post(new Runnable() {
                @Override
                public void run() {
                    listener.onSuccess();
                }
            });
        }
    }

    /**
     * 进入草稿箱，解析草稿
     *
     * @param draft
     * @param listener
     */
    public void parseDraft(final Draft draft, final OnParseDraftListener listener) {
        VideoWorker.getInstance().execute(new DraftParser(draft, listener));
    }

    /**
     * 清除帧位图
     */
    private synchronized void clearBitmaps() {
        if (mFrameBitmaps.size() != 0) {
            for (Bitmap bitmap : mFrameBitmaps) {
                bitmap.recycle();
            }
            mFrameBitmaps.clear();
        }
    }

    /**
     * 在上传结束，保存草稿箱后回到主界面后执行，回收内存和删除中间文件，异步执行.
     * 如果上传失败，则先保存到草稿中，再执行以下方法释放资源.
     */
    public void release() {
        VideoWorker.getInstance().execute(new Runnable() {

            @Override
            public void run() {
                // 清理位图
                clearBitmaps();

                // 删除帧文件
                synchronized (mFrameFiles) {
                    Iterator<File> iterator = mFrameFiles.iterator();
                    while (iterator.hasNext()) {
                        File file = iterator.next();
                        iterator.remove();

                        if (file.exists()) {
                            file.delete();
                        }
                    }
                }

                // 删除视频文件
                synchronized (mVideoFile) {
                    if (mVideoFile != null && mVideoFile.exists()) {
                        mVideoFile.delete();
                    }
                }
            }
        });
    }

    /**
     * 根据进度百分比获取帧位图
     *
     * @param progress
     * @return
     */
    public Bitmap getFrameBitmap(float progress) {
        int pos = getPosition(progress);
        return mFrameBitmaps.get(pos);
    }

    /**
     * 获取封面位图
     *
     * @return
     */
    public Bitmap getCoverBitmap() {
        if (mFrameBitmaps == null || mFrameBitmaps.size() == 0) {
            return null;
        }

        return mFrameBitmaps.get(mCoverBitmapPosition);
    }

    /**
     * 设置封面位图
     *
     * @param progress
     */
    public void setCover(float progress) {
        mCoverBitmapPosition = getPosition(progress);
        mCoverFilePosition = (int) ((mFrameFiles.size() - 1) * progress);

        // 重设封面后取消草稿封面
        mCoverDraftBitmap = mFrameBitmaps.get(mCoverBitmapPosition);
        mCoverDraftFile = mFrameFiles.get(mCoverFilePosition);
    }

    /**
     * 获取封面文件
     *
     * @return
     */
    public File getCoverFile() {

        // 草稿封面存在，返回草稿封面
        //这里存在bug,故要注释
        /*if (mCoverDraftFile != null) {
            return mCoverDraftFile;
        }*/

        return mFrameFiles.get(mCoverFilePosition);
    }

    /**
     * 获取视频简介支持的最大字数
     *
     * @return
     */
    public int getIntroduceMaxWordNum() {
        return INTRODUCE_MAX;
    }

    /**
     * 保存视频简介
     *
     * @param introduce
     */
    public void setIntroduce(String introduce) {
        mIntroduce = introduce;
    }

    /**
     * 获取视频简介
     *
     * @return
     */
    public String getIntroduce() {
        return mIntroduce;
    }

    public void clearIntroduce() {
        mIntroduce = "";
    }

    public File getVideoFile() {
        return mVideoFile;
    }

    public long getVideoDuration() {
        return mVideoDuration;
    }

    public float getVideoRatio() {
        return mRatio;
    }

    public void setShouldUpload(boolean update) {
        mShouldUpload = update;
    }

    public boolean shouldUpload() {
        return mShouldUpload;
    }

    public void setHasSave(boolean save) {
        mHasSave = save;
    }

    public boolean hasSave() {
        return mHasSave;
    }

    public void setDraft(Draft draft) {
        mDraft = draft;
    }

    public Draft getDraft() {
        return mDraft;
    }

    public static interface OnFrameBitmapInitListener {
        void onFinished(boolean success);
    }

    /** 获取是否同步到微博 */
    public boolean isNeedShareToSina() {
        return needShareToSina;
    }

    /** 设置是否同步到微博 */

    public void setNeedShareToSina(boolean needShareToSina) {
        this.needShareToSina = needShareToSina;
    }

    public static interface OnParseDraftListener {
        void onSuccess();

        void onFail();
    }

    public static interface OnSaveDraftListener {
        void onStart();

        void onSuccess();

        void onFail();
    }

    public void saveDraft(final OnSaveDraftListener listener) {
        listener.onStart();

        VideoDraftManager.getInstance().saveDraft(mDraftDir, getCoverBitmap(),
                getIntroduce(), new OnDraftSaveListener() {

            @Override
            public void onSuccess(Draft draft, boolean firstSave) {
                if(!hasSave()) {
                    MessageHintManager.addOrDeleteDraft(AppContext.getContext(), true);
                }
                setHasSave(true);
                setDraft(draft);
                mMainThreadHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        listener.onSuccess();
                    }
                });
            }

            @Override
            public void onFail() {
                mMainThreadHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        listener.onFail();
                    }
                });
            }
        });
    }

    public void deleteDraft(String videoPath) {
        VideoDraftManager.getInstance().deleteDraft(videoPath);
    }
}
