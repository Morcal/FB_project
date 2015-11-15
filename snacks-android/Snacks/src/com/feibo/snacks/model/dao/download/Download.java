package com.feibo.snacks.model.dao.download;

import java.io.File;

import fbcore.conn.http.HttpParams;
import fbcore.conn.http.HttpResult;
import fbcore.conn.http.Method;
import fbcore.conn.http.client.ApacheHttpClient;
import fbcore.task.AsyncTaskManager;
import fbcore.task.SyncTask;
import fbcore.task.TaskFailure;
import fbcore.task.TaskHandler;
import fbcore.utils.Files;
import fbcore.utils.IOUtil;

/**
 * Created by lidiqing on 15-9-10.
 */
public class Download {

    // 下载
    public static void download(String urlPath, String saveDir, String saveFileName, final OnDownloadListener listener) {
        AsyncTaskManager.INSTANCE.execute(new DownloadTask(urlPath, saveDir, saveFileName), new TaskHandler() {
            @Override
            public void onSuccess(Object o) {
                listener.onSuccess();
            }

            @Override
            public void onFail(TaskFailure taskFailure) {
                listener.onFail();
            }

            @Override
            public void onProgressUpdated(Object... objects) {
            }
        });
    }

    public interface OnDownloadListener {
        void onSuccess();
        void onFail();
    }

    public static class DownloadTask extends SyncTask {

        private String urlPath;
        private String saveFileName;
        private String saveDir;

        public DownloadTask(String urlPath, String saveDir, String saveFileName) {
            this.urlPath = urlPath;
            this.saveDir = saveDir;
            this.saveFileName = saveFileName;
        }

        @Override
        protected Object execute() {
            try {
                HttpParams params = new HttpParams.Builder(Method.GET, urlPath).create();
                ApacheHttpClient client = new ApacheHttpClient();
                client.request(params);
                HttpResult result = client.getHttpResult();
                byte[] content = result.getContent();
                File file = new File(saveDir, saveFileName);
                if (Files.create(file)) {
                    IOUtil.writeBytesTo(file, content);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

    }

}
