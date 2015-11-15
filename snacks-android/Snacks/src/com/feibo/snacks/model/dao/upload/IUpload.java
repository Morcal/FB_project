package com.feibo.snacks.model.dao.upload;

import java.io.File;

/**
 * Created by lidiqing on 15-9-14.
 */
public interface IUpload {
    void put(File file, UploadListener listener);
    void put(File file);
}
