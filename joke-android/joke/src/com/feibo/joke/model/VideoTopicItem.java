package com.feibo.joke.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Create by：ml_bright on 2015/10/21 15:54
 * Email: 2504509903@qq.com
 */
public class VideoTopicItem implements Serializable {

    public static final int TYPE_VIDEO = 1;
    public static final int TYPE_TOPIC = 2;

    @SerializedName("type")
    public int type;            //1.视频 2话题

    @SerializedName("video")
    public Video video;

    @SerializedName("topic")
    public Topic topic;

    public boolean isVideo() {
        return type == TYPE_VIDEO;
    }


    public long getId() {
        long ids = 0;
        if(isVideo()) {
            if(video != null) {
                ids = video.id;
            }
        } else {
            if(topic != null) {
                ids = topic.id;
            }
        }

        return ids;
    }



}
