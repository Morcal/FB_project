package com.feibo.snacks.model.bean.group;

import java.util.List;

import com.feibo.snacks.model.bean.Subject;
import com.google.gson.annotations.SerializedName;

import com.feibo.snacks.model.bean.Goods;

public class SubjectDetail {
    
    @SerializedName("subject")
    public Subject subject;
    
    @SerializedName("goodses")
    public List<Goods> goodses;
}
