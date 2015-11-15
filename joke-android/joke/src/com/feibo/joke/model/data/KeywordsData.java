package com.feibo.joke.model.data;

import java.util.List;

import com.google.gson.annotations.SerializedName;

public class KeywordsData {

    @SerializedName("count")
    public int count;

    @SerializedName("items")
    public List<String> keywords;
}
