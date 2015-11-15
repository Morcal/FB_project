package com.feibo.joke.model;

import com.google.gson.annotations.SerializedName;

/**
 * 
 * @author kcode(ml_bright)
 * @version 2015/03/23.
 */
public class Topic extends BaseTopic {

    @SerializedName("thumbnail")
    public Image thumbnail;

    @SerializedName("ori_image")
    public Image oriImage;

    @SerializedName("works_count")
    public int worksCount;

    @SerializedName("play_count")
    public int playCount;

    @SerializedName("ad_image")
    public Image adImage;

}

