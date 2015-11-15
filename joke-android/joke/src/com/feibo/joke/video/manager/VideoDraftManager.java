package com.feibo.joke.video.manager;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

import fbcore.log.LogUtil;
import fbcore.utils.Files;
import fbcore.utils.Strings;

import com.feibo.joke.app.AppContext;
import com.feibo.joke.app.DirContext;
import com.feibo.joke.app.DirContext.DirEnum;
import com.feibo.joke.video.VideoConstants;
import com.feibo.joke.view.util.MessageHintManager;

public class VideoDraftManager {
    private static final String TAG = "VideoDraftManager";
    public static final String JSON = "json";
    public static final String DATA = "data";

    private static VideoDraftManager manager;

    public static VideoDraftManager getInstance() {
        if (manager == null) {
            manager = new VideoDraftManager();
        }
        return manager;
    }

    /**
     * 读取草稿
     *
     * @param listener
     */
    public void readDrafts(OnDraftReadListener listener) {
        VideoWorker.getInstance().execute(new DraftReader(listener));
    }

    /**
     * 通过草稿所在文件夹路径，删除草稿箱
     * @param draftDir
     */
    public void deleteDraft(final String videoPath) {
        VideoWorker.getInstance().execute(new Runnable() {

            @Override
            public void run() {
                File outputDir = DirContext.getInstance().getDir(DirEnum.OUTPUT);
                String draftDir = new File(videoPath).getParent();
                File draftFile = new File(draftDir);
                String draftFileName = draftFile.getName();
                File draftConfig = new File(outputDir, draftFileName + VideoConstants.DRAFT_CONFIG_SUFFIX);
                Files.delete(draftConfig.getAbsolutePath());
                Files.delete(draftDir);

                MessageHintManager.addOrDeleteDraft(AppContext.getContext(), false);
            }
        });
    }


    public void saveDraft(String draftDir, Bitmap bitmap, String desc, OnDraftSaveListener listener) {
        VideoWorker.getInstance().execute(new DraftSaver(draftDir, bitmap, desc, listener));
    }

    public static int[] getImageFileSize(String path) {
        BitmapFactory.Options decodeOptions = new BitmapFactory.Options();

        decodeOptions.inJustDecodeBounds = true;
        try {
            BitmapFactory.decodeFile(path, decodeOptions);
        } catch (Exception e) {
            LogUtil.printStackTrace(e);
        }

        int actualWidth = decodeOptions.outWidth;
        int actualHeight = decodeOptions.outHeight;
        return new int[]{actualWidth, actualHeight};
    }

    public static class DraftSaver implements Runnable {
        private String draftDir;
        private Bitmap bitmap;
        private String desc;

        private OnDraftSaveListener listener;

        public DraftSaver(String draftDir, Bitmap bitmap, String desc, OnDraftSaveListener listener) {
            this.draftDir = draftDir;
            this.bitmap = bitmap;
            this.desc = desc;
            this.listener = listener;
        }

        @Override
        public void run() {
            File dir = DirContext.getInstance().getDir(DirEnum.OUTPUT);
            File configFile = new File(dir, new File(draftDir).getName() + VideoConstants.DRAFT_CONFIG_SUFFIX);
            FileOutputStream outputStream = null;
            try {
                Draft draft = new Draft();
                draft.dir = draftDir;
                draft.content = desc;
                draft.videoPath = draftDir + File.separator + VideoConstants.OUTPUT_VIDEO;

                if(bitmap != null) {
                    String coverPath = draftDir + File.separator + VideoConstants.VIDEO_COVER;
                    saveImage2File(bitmap, coverPath);
                    
                    draft.coverPath = coverPath;
                }
                draft.coverWidth = bitmap.getWidth();
                draft.coverHeight = bitmap.getHeight();
                /*if (cover.exists() && cover.isFile()) {
                    draft.coverPath = cover.getAbsolutePath();
                    int[] size = getImageFileSize(draft.coverPath);
                    draft.coverWidth = size[0];
                    draft.coverHeight = size[1];
                }*/

                draft.jsonPath = configFile.getAbsolutePath();

                boolean firstSave = true;
                if (configFile.exists() && configFile.isFile()) {
                    Files.delete(configFile.getAbsolutePath());
                    firstSave = false;
                }

                String json = new Gson().toJson(draft);
                outputStream = new FileOutputStream(configFile);
                outputStream.write(json.getBytes());
                listener.onSuccess(draft, firstSave);
                return;
            } catch (Exception e) {
                LogUtil.printStackTrace(TAG, e);
            }
            listener.onFail();
        }
    }

    public static void saveImage2File(Bitmap bitmap, String path) {
        if (bitmap == null || TextUtils.isEmpty(path)) {
            return;
        }
        FileOutputStream fos = null;
        File file = new File(path);
        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            bitmap.compress(CompressFormat.PNG, 0, bos);
            byte[] bitmapdata = bos.toByteArray();

            fos = new FileOutputStream(file);
            fos.write(bitmapdata);
            fos.flush();
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    public class DraftReader implements Runnable {
        private OnDraftReadListener listener;

        public DraftReader(OnDraftReadListener listener) {
            this.listener = listener;
        }

        @Override
        public void run() {
            File draftDir = DirContext.getInstance().getDir(DirEnum.OUTPUT);
            File[] draftsconfig = draftDir.listFiles(new FilenameFilter() {
                @Override
                public boolean accept(File dir, String filename) {
                    return filename.endsWith(VideoConstants.DRAFT_CONFIG_SUFFIX);
                }
            });

            if (draftsconfig == null || draftsconfig.length == 0) {
                listener.onResult(new ArrayList<VideoDraftManager.Draft>());
                return;
            }

            List<File> sortedDraft = Arrays.asList(draftsconfig);
            Collections.sort(sortedDraft, new Comparator<File>() {
                @Override
                public int compare(File lhs, File rhs) {
                    long delta = lhs.lastModified() - rhs.lastModified();
                    if (delta == 0) {
                        return 0;
                    }
                    return delta > 0 ? -1 : 1;
                }
            });

            List<Draft> drafts = new ArrayList<VideoDraftManager.Draft>();
            for (File config : sortedDraft) {
                Draft draft = parseDraft(config);
                if (draft != null) {
                    drafts.add(draft);
                }
            }

            listener.onResult(drafts);
        }

        private Draft parseDraft(File file) {
            BufferedReader reader = null;
            try {
                reader = new BufferedReader(new FileReader(file));
                StringBuilder totalJson = new StringBuilder();
                String tempString = null;
                while ((tempString = reader.readLine()) != null) {
                    totalJson.append(tempString);
                }

                // 解析json
                Draft draft = new Gson().fromJson(totalJson.toString(), Draft.class);

                // 判断文件是否存在
                File coverFile = null;
                if (!Strings.isEmpty(draft.coverPath)) {
                    coverFile = new File(draft.coverPath);
                }
                File videoFile = new File(draft.videoPath);
                if (/*coverFile.exists() && */videoFile.exists()) {
                    return draft;
                } else {
                    // 因为文件不全，删除草稿
                    Files.delete(coverFile.getAbsolutePath());
                    Files.delete(videoFile.getAbsolutePath());
                    Files.delete(file.getAbsolutePath());
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            return null;
        }
    }

    public static interface OnDraftSaveListener {
        void onSuccess(Draft draft, boolean firstSave);

        void onFail();
    }

    public static interface OnDraftReadListener {
        void onResult(List<Draft> drafts);
    }

    @SuppressWarnings("serial")
    public static class Draft implements Serializable{

        @SerializedName("JsonPath")
        public String jsonPath;

        @SerializedName("VideoPath")
        public String videoPath;

        @SerializedName("CoverPath")
        public String coverPath;

        @SerializedName("CoverWidth")
        public int coverWidth;

        @SerializedName("CoverHeight")
        public int coverHeight;

        @SerializedName("Content")
        public String content;

        /**
         * 具体草稿文件所在的目录.
         */
        @SerializedName("dir")
        public String dir;
    }
}
