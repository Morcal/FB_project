package com.feibo.snacks.model.bean.group;

import java.util.List;

import com.feibo.snacks.model.bean.Goods;
import com.feibo.snacks.model.bean.SubClassify;
import com.google.gson.annotations.SerializedName;

public class ClassifyList {
    
    @SerializedName("sub_classifies")
    public List<SubClassify> subClassifies;
    
    @SerializedName("goodses")
    public List<Goods> goodses;
    
}
