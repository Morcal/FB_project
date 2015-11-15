package com.feibo.snacks.model.dao.download;

import java.io.File;

import fbcore.conn.http.HttpParams;
import fbcore.conn.http.HttpResult;
import fbcore.conn.http.Method;
import fbcore.conn.http.client.ApacheHttpClient;
import fbcore.task.SyncTask;
import fbcore.utils.Files;
import fbcore.utils.IOUtil;

public class DownloadTask extends SyncTask {

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
