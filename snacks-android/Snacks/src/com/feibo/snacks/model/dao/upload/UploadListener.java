package com.feibo.snacks.model.dao.upload;

import com.feibo.snacks.model.dao.ResultCode;

/**
 * Created by lidiqing on 15-9-14.
 */
public interface UploadListener {
    void success(Object object);
    void progress(float progress);
    void fail(ResultCode resultCode);
}
