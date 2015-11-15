package com.feibo.joke.view.group;

import com.feibo.joke.R;

public class GroupConfig {
	public static final int GROUP_DEFAULT = 0; // 默认(不用)
	
	public static final int GROUP_VIDEO_ESSENCE = 1; // 精选
	public static final int GROUP_VIDEO_FRESH = 5; // 最新视频
	public static final int GROUP_VIDEO_DISCOVERY = 2; // 发现
	public static final int GROUP_VIDEO_DYNAMIC = 3; // 动态（贵圈）
	public static final int GROUP_VIDEO_FAVORITE = 13; // 我喜欢的视频
	public static final int GROUP_VIDEO_DRAFT = 7; // 草稿箱
	
	public static final int GROUP_MESSAGE_USER = 8; // 消息中心
	public static final int GROUP_MESSAGE_SYSTEM = 9; // 通知中心
	
	public static final int GROUP_USER_FUNNY = 10; // 搞笑达人推荐
	public static final int GROUP_USER_WEIBO_FRIENDS = 11; // 微博好友
	public static final int GROUP_USER_FANS = 12; // 我的粉丝
	public static final int GROUP_USER_ATTENTION = 14; // 我关注的

	public static final int GROUP_TOPICS_HOT = 4; // 更多热门话题
	public static final int GROUP_DETAIL_USER = 6; // 个人主页
	public static final int GROUP_FEEDBACK = 15; // 意见反馈
	
    public static final int GROUP_TOPIC_VIDEOS = 16; // 意见反馈
    public static final int GROUP_VEDIO_DETAIL = 17; // 视频详情页

    public static final int GROUP_VEDIO_SHARE = 18; // 视频发布页

    public static final int GROUP_PROTOCOL = 19; // 用户协议
    
	public String message;
	public String btnText;
	public int imageRes;
	
	public GroupConfig(String msg, String btnText, int imageRes){
		this.message = msg;
		this.btnText = btnText;
		this.imageRes = imageRes;
	}
	
	public static GroupConfig create(int groupType){
		String msg = "";
		String btnText = "";
		int img = 0;
		
		switch (groupType) {
		
		case GROUP_VIDEO_DYNAMIC: // 动态视频列表
			msg = "一个玩容易胖\n交些朋友出去浪";
			btnText = "勾搭新朋友";
			img = R.drawable.icon_not_attention;
			break;
		case GROUP_VIDEO_FAVORITE: // 我的喜欢
			msg = "居然没爱过";
			img = R.drawable.icon_no_like;
			break;
		case GROUP_DETAIL_USER: // 个人主页
			msg = "快来跟世界拍个玩笑啊";
			btnText = "马上开拍";
			img = R.drawable.icon_no_gerenzhuye;
			break;
		case GROUP_MESSAGE_SYSTEM: // 通知中心
			msg = "还没有收到通知";
			break;
		case GROUP_MESSAGE_USER: // 消息中心
			msg = "还没有消息";
			img = R.drawable.icon_no_new;
			break;
		case GROUP_USER_FANS: // 我的粉丝
			msg = "粉丝为零";
			break;
//		case GROUP_USER_WEIBO_FRIENDS: // 微博好友
//            msg = "你还没有关注好友哦";
//            img = R.drawable.icon_no_new;
//			break;
		case GROUP_VEDIO_DETAIL: // 视频详情页
		    msg = "这个视频被我删了";
		    img = R.drawable.icon_no_like;
		    break;
		case GROUP_FEEDBACK: // 意见反馈
			msg = "期待您的宝贵建议！";
			break;
		case GROUP_TOPIC_VIDEOS: //话题视频列表
            msg = "该话题没有视频哦";
		    break;
		case GROUP_USER_FUNNY:
		    msg = "卧槽！被你关注完了！\n 骑我这匹野马帮我找些大咖来";
            img = R.drawable.icon_funy;
		    break;
//		case GROUP_VIDEO_DRAFT:
//		    msg = "什么都没有！空空的！";
//            img = R.drawable.icon_no_duifanggerenzhuye;
//		    break;
		case GROUP_VEDIO_SHARE:
            msg = "打开草稿箱失败";
            img = R.drawable.icon_no_like;
		    break;
        case GROUP_PROTOCOL:
            msg = "网络出错";
            img = R.drawable.icon_no_duifanggerenzhuye;
            break;
        case GROUP_USER_ATTENTION:
            msg = "这位演员超高冷\n 还没有关注任何人";
            img = R.drawable.icon_no_new;
            break;
		default:
            msg = "什么都没有！空空的！";
            img = R.drawable.icon_no_duifanggerenzhuye;
			break;
		}
		return new GroupConfig(msg, btnText, img);
	}
	
	
}
