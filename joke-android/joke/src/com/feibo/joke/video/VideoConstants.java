package com.feibo.joke.video;

public class VideoConstants {

    /**
     * 视频缩略图的命名前缀.
     */
    public static final String FRAME_PREFIX = "frame_";

    public static final String DRAFT_CONFIG_SUFFIX = ".draft";

    /**
     * 导入视频时的最大需要解析帧数.
     */
    public static final int MAX_IMPORT_FRAME_NUM = 10;

    public static final String VIDEO_COVER = ".cover";

    /**
     * 拍摄、导入的输出视频，作为编辑的输入源视频.
     */
    public static final String OUTPUT_VIDEO = "output.mp4";

    /**
     * 最终完成的视频，用于发布，保存到相册.
     */
    public static final String PUBLISH_VIDEO = "publish.mp4";
}
