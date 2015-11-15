package com.feibo.snacks.model.bean.group;

import java.util.List;

import com.feibo.snacks.model.bean.Comment;
import com.google.gson.annotations.SerializedName;

public class CommentsList{
    
    @SerializedName("total_num")
    public int totalNum;
    
    @SerializedName("comments")
    public List<Comment> comments;
    
}
