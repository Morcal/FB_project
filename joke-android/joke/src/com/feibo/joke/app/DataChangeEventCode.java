package com.feibo.joke.app;

public class DataChangeEventCode {

    public static final int CODE_EVENT_BUS_REDHINT = 1;

    public static final int CODE_CURRENT_ITEM_FRESH = 2;

    /** 关闭页面 */
    public static final int CHANGE_TYPE_CLOSE = 4;

    /** 刷新页面命令 */
    public static final int CHANGE_TYPE_FRESH_PAGE = 5;

    /** 视频详情页喜欢数和播放数变化或者删除举报（需要刷新返回后的页面） */
    public static final int CHANGE_TYPE_VIDEO_DETAIL_CHANGE = 6;

    /** 关注数变化 */
    public static final int CHANGE_TYPE_ATTENTION_COUNT_CHANGE = 7;

    /** 喜欢数变化 */
    public static final int CHANGE_TYPE_LIKE_COUNT_CHANGE = 8;

    /** 在用户个人主页点击关注，返回上一层，如果是视频详情页的话要在关注状态 */
    public static final int CHANGE_TYPE_ATTENTION_IN_USERDETAIL = 9;

    /** 刷录制视频成功 */
    public static final int CHANGE_TYPE_VIDEO_PRODUCE_SUCESS = 0x10;

    /** 取消视频生成 **/
    public static final int CHANGE_TYPE_VIDEO_PRODUCE_CANCEL = 0x11;

    /** 取消视频导入 **/
    public static final int CHANGE_TYPE_VIDEO_IMPORT_CANCEL = 0x20;

    /** 取消视频剪辑 **/
    public static final int CHANGE_TYPE_VIDEO_CUT_CANCEL = 0x30;

    /** 选择新封面成功 **/
    public static final int CHANGE_TYPE_VIDEO_COVER_SUCCESS = 0x40;

    /** 修改草稿箱成功 **/
    public static final int CHANGE_TYPE_SAVE_VIDEO_DRAFT = 0x50;

    /** 修改用户资料后成功 */
    public static final int CHANGE_TYPE_MODIFY_USER = 0x51;

    /** ListView刷新Adapter */
    public static final int HANDLE_SHOW_VIEW = 0x100;


}
