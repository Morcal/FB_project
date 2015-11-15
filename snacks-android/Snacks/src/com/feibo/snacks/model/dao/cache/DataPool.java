package com.feibo.snacks.model.dao.cache;

import java.util.HashMap;

/**
 * 数据池，数据的内存缓存
 */
public class DataPool {

    private HashMap<BaseDataType, Object> map = new HashMap<BaseDataType, Object>();
    private static DataPool sPool;

    public synchronized static DataPool getInstance() {
        if (sPool == null) {
            sPool = new DataPool();
        }
        return sPool;
    }

    private DataPool(){

    }

    public static class DataBox {
        BaseDataType option;
        Object data;

        public DataBox(BaseDataType option, Object data) {
            this.option = option;
            this.data = data;
        }
    }

    public Object getData(BaseDataType dataOption) {
        return map.get(dataOption);
    }

    public void putData(DataBox data) {
        map.put(data.option, data.data);
    }

    public void removeData(BaseDataType type) {
        map.remove(type);
    }
}
