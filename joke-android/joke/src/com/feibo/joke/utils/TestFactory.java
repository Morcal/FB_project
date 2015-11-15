package com.feibo.joke.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.feibo.joke.model.Comment;
import com.feibo.joke.model.DiscoveryTopicItem;
import com.feibo.joke.model.Feedback;
import com.feibo.joke.model.Image;
import com.feibo.joke.model.Message;
import com.feibo.joke.model.SplashImage;
import com.feibo.joke.model.Topic;
import com.feibo.joke.model.User;
import com.feibo.joke.model.UserDetail;
import com.feibo.joke.model.Video;

public class TestFactory {

    private static String avatar1 = "http://img.yule.feibo.com/yuleimg/201412/383607e19da357f75d2ed242e46a0fc0.jpg";
    private static String avatar2 = "http://img.yule.feibo.com/yuleimg/201411/5f90eb043398f0defd009674871286e4.jpg";
    private static String avatar3 = "http://img.yule.feibo.com/yuleimg/201411/eadba5c16630d7cb624f60da9764b734.jpg";
    private static String avatar4 = "http://img.yule.feibo.com/yuleimg/201411/3a0018a7210b7c443da3c00f614c5bce.jpg";
    private static String avatar5 = "http://img.yule.feibo.com/yuleimg/201411/af6dfdc263640c4678d7b3cea78436b4.jpg";
    private static String avatar6 = "http://img.yule.feibo.com/yuleimg/201411/dcbc6525384a51cfd634f6a9f4e393e0.jpg";
    private static String avatar7 = "http://img.yule.feibo.com/yuleimg/201411/c7ef2b271cc8dc3276e9c02b6f7cd6f2.jpg";
    private static String avatar8 = "http://img.yule.feibo.com/yuleimg/201411/4c1d26062c6dce32cf77281b68bbe585.jpg";
    private static String avatar9 = "http://img.yule.feibo.com/yuleimg/201411/0feb2de5e0ff38946186c24d116e78c6.jpg";
    private static String avatar10 = "http://img.yule.feibo.com/yuleimg/201411/9f030e6ac79c10aa80c0c53249f3efc0.jpg";

    //    private static String image1 = "http://mvimg1.meitudata.com/550e0ef44baf43949.jpg";
    private static String image2 = "http://mvimg2.meitudata.com/550d5db570fab900.jpg";
    private static String image3 = "http://mvimg2.meitudata.com/54fa6e21727b86372.jpg";
    private static String image4 = "http://mvimg1.meitudata.com/5507ba9d559023331.jpg";
    //    private static String image5 = "http://mvimg2.meitudata.com/548bcc6acc09d5234.jpg";
    private static String image6 = "http://mvimg2.meitudata.com/5507998db0ed76132.jpg";
    //    private static String image7 = "http://mvimg1.meitudata.com/54f44477a8ab05382.jpg";
    private static String image8 = "http://mvimg2.meitudata.com/550a41dd8b0185379.jpg";
    //    private static String image9 = "http://mvimg1.meitudata.com/54eede817773f3351.jpg";
    //    private static String image10 = "http://mvimg2.meitudata.com/55081453afee07860.jpg";

    private static String image1 = "http://i.zeze.com/attachment/forum/201503/20/185848pw0gpxlxhsqaowpp.jpg";
    //    private static String image2 = "http://www.feizl.com/upload2007/2013_07/130724141496902.jpg";
    //    private static String image3 = "http://cimg.taohuaan.net/upload/201111/19/120629SPMzG.jpg";
    //    private static String image4 = "http://img0.137home.com/scart/2011/11/24/266/1299053468_36320800.jpg";
    private static String image5 = "http://www.quutoo.net/up_files/2010-08/2172149266.jpg";
    //    private static String image6 = "http://img1.gamersky.com/image2012/02/20120224s_10/image024_wm.jpg";
    private static String image7 = "http://picview01.baomihua.com/photos/20120717/m_14_634781552096562500_37235474.jpg";
    //    private static String image8 = "http://i.kl688.com/kl688File/2013-11/2013112615563457807.jpg";
    private static String image9 = "http://i1.kl688.com/kl688File/2013-12/2013121212282045059.jpg";
    private static String image10 = "http://t2.qpic.cn/mblogpic/5c14f720db19125fda5a/2000/94996.jpg";

    private static String videoUrl1 = "http://mvvideo1.meitudata.com/550e0e7e9bdef3652.mp4";
    private static String videoUrl2 = "http://mvvideo1.meitudata.com/550d5d39e347e2465.mp4";
    private static String videoUrl3 = "http://mvvideo2.meitudata.com/54fa6e217bb013121.mp4";
    private static String videoUrl4 = "http://mvvideo2.meitudata.com/5507b7ab0d6833455.mp4";
    private static String videoUrl5 = "http://mvvideo1.meitudata.com/548bcc666eee04770.mp4";
    private static String videoUrl6 = "http://mvvideo2.meitudata.com/550799206da3f2255.mp4";
    private static String videoUrl7 = "http://mvvideo2.meitudata.com/54f444776f89c4647.mp4";
    private static String videoUrl8 = "http://mvvideo1.meitudata.com/550a412c34b9b7142.mp4";
    private static String videoUrl9 = "http://mvvideo2.meitudata.com/54eede5391f5a7949.mp4";
    private static String videoUrl10 = "http://mvvideo2.meitudata.com/5508142ca31171964.mp4";

    private static String topicImg1 = "http://img.lingshi.cccwei.com/lingshi/f3f/3f/f/5e94ac2c7d8574ac0f6f0839ed46df3f.jpg";
    private static String topicImg2 = "http://img.lingshi.cccwei.com/lingshi/96b/6b/b/2cc5227049a75495163c75825f61996b.jpg";
    private static String topicImg3 = "http://img.lingshi.cccwei.com/lingshi/5f2/f2/2/f4c21a842cd3d51a41fb7e0fe97205f2.jpg";
    private static String topicImg4 = "http://img.lingshi.cccwei.com/lingshi/9f1/f1/1/a7cb007833c8bbd08c46dada562bd9f1.jpg";
    private static String topicImg5 = "http://img.lingshi.cccwei.com/lingshi/8db/db/b/4046b9a04edd40c7691d2a4bffd188db.jpg";
    private static String topicImg6 = "http://img.lingshi.cccwei.com/lingshi/ed2/d2/2/bdc715abcc2ed1a93510da99ee535ed2.jpg";
    private static String topicImg7 = "http://img.lingshi.cccwei.com/lingshi/03c/3c/c/c3fb8d3639b3426a1bdc7c7c18bad03c.jpg";
    private static String topicImg8 = "http://img.lingshi.cccwei.com/lingshi/715/15/5/504c64fe8bded8536e0e9445205c0715.jpg";
    private static String topicImg9 = "http://img.lingshi.cccwei.com/lingshi/1cb/cb/b/3a26470d8f4989ee759cf7331556c1cb.jpg";
    private static String topicImg10 = "http://img.lingshi.cccwei.com/lingshi/757/57/7/c9560638de4bbc78eee678768af3a757.jpg";

    private static String name1 = "中国立领塘主";
    private static String name2 = "女金刚长着芭比脸";
    private static String name3 = "馨馨的露露";
    private static String name4 = "驻马店政协主席";
    private static String name5 = "贝特蕾迪爱饭团";
    private static String name6 = "捡肥皂的小卫";
    private static String name7 = "李大大阳";
    private static String name8 = "长夜行lx";
    private static String name9 = "絡小凡";
    private static String name10 = "前辈小心！";

    private static String splashImage1 = "http://b.zol-img.com.cn/sjbizhi/images/4/320x510/1368174632880.jpg";
    private static String splashImage2 = "http://www.33.la/uploads/20140403sj/4561.jpg";
    private static String splashImage3 = "http://i6.download.fd.pchome.net/t_600x1024/g1/M00/0A/13/oYYBAFPp6IKIbnf6AAtXAEuwtEEAAB1rwCFINoAC1cY039.jpg";

    private static List<String> splashImages = new ArrayList<String>();
    private static List<String> imgs = new ArrayList<String>();
    private static List<String> names = new ArrayList<String>();
    private static List<String> avatars = new ArrayList<String>();
    private static List<String> videoUrls = new ArrayList<String>();
    private static List<String> topicImgUrls = new ArrayList<String>();

    private static List<UserDetail> userDetails = new ArrayList<UserDetail>();
    private static List<User> users = new ArrayList<User>();
    private static List<Image> avatarImgs = new ArrayList<Image>();
    private static List<Image> listImgs = new ArrayList<Image>();
    private static List<Image> topicImgs = new ArrayList<Image>();
    private static List<Video> videos = new ArrayList<Video>();

    private static List<Topic> topics = new ArrayList<Topic>();
    private static List<DiscoveryTopicItem> discoveryTopicItems = new ArrayList<DiscoveryTopicItem>();

    private static List<Feedback> feedback = new ArrayList<Feedback>();
    private static List<Message> messages = new ArrayList<Message>();
    private static List<Comment> comments = new ArrayList<Comment>();

    private static final int DEFAULT_COUNT = 10;

    private static int messageID = 0;

    public TestFactory() {
        init();
    }

    private static void resetData() {
        userDetails.clear();
        users.clear();
        //        avatarImgs.clear();
        listImgs.clear();
        topicImgs.clear();
        videos.clear();
        topics.clear();
        discoveryTopicItems.clear();

        init();
        setUsers();
        setListImages();
        setVideos();
        setTopics();
    }

    private static void init() {
        splashImages.add(splashImage1);
        splashImages.add(splashImage2);
        splashImages.add(splashImage3);

        names.add(name1);
        names.add(name2);
        names.add(name3);
        names.add(name4);
        names.add(name5);
        names.add(name6);
        names.add(name7);
        names.add(name8);
        names.add(name9);
        names.add(name10);

        avatars.add(avatar1);
        avatars.add(avatar2);
        avatars.add(avatar3);
        avatars.add(avatar4);
        avatars.add(avatar5);
        avatars.add(avatar6);
        avatars.add(avatar7);
        avatars.add(avatar8);
        avatars.add(avatar9);
        avatars.add(avatar10);

        videoUrls.add(videoUrl1);
        videoUrls.add(videoUrl2);
        videoUrls.add(videoUrl3);
        videoUrls.add(videoUrl4);
        videoUrls.add(videoUrl5);
        videoUrls.add(videoUrl6);
        videoUrls.add(videoUrl7);
        videoUrls.add(videoUrl8);
        videoUrls.add(videoUrl9);
        videoUrls.add(videoUrl10);

        imgs.add(image1);
        imgs.add(image2);
        imgs.add(image3);
        imgs.add(image4);
        imgs.add(image5);
        imgs.add(image6);
        imgs.add(image7);
        imgs.add(image8);
        imgs.add(image9);
        imgs.add(image10);

        topicImgUrls.add(topicImg1);
        topicImgUrls.add(topicImg2);
        topicImgUrls.add(topicImg3);
        topicImgUrls.add(topicImg4);
        topicImgUrls.add(topicImg5);
        topicImgUrls.add(topicImg6);
        topicImgUrls.add(topicImg7);
        topicImgUrls.add(topicImg8);
        topicImgUrls.add(topicImg9);
        topicImgUrls.add(topicImg10);

    }

    private static int getRandom(int range) {
        return new Random().nextInt(range);
    }

    /** 从 m 到 n-1 (不包括n)*/
    private static int getRandom(int m, int n) {
        return (int) (Math.random()*(m-n)+n);
    }

    private static void setListImages() {
        listImgs.clear();
        topicImgs.clear();
        avatarImgs.clear();

        int width = 300;
        int maxHeight = 400;

        for(int i=0; i<DEFAULT_COUNT; i++) {
            Image img = new Image();
            img.height = getRandom(width, maxHeight);
            img.width = width;
            img.url = imgs.get(i);
            listImgs.add(img);
        }

        for(int i=0; i<DEFAULT_COUNT; i++) {
            Image img = new Image();
            img.height = 290;
            img.width = 290;
            img.url = topicImgUrls.get(i);
            topicImgs.add(img);
        }

        for(int i=0; i<DEFAULT_COUNT; i++) {
            Image img = new Image();
            img.height = 50;
            img.width = 50;
            img.url = avatars.get(i);
            avatarImgs.add(img);
        }

    }

    private static void setUsers() {
        users.clear();
        userDetails.clear();

        int userCount = getRandom(DEFAULT_COUNT, 20);
        for(int i=0; i<userCount; i++) {
            UserDetail ud = new UserDetail();
            ud.beLikeCount = getRandom(30);
            ud.birth = "1992-06-27";
            ud.city = getRandom(3) == 2 ? "厦门" : "福州";
            ud.followersCount = getRandom(10000);
            ud.friendsCount = getRandom(10000);
            ud.gender = getRandom(2);
            ud.likeCount = getRandom(300);
            ud.province = getRandom(2) == 0 ? "福建" : "北京";
            ud.signature = "用户签名" + getRandom(1000);
            ud.worksCount = getRandom(100);
            userDetails.add(ud);
        }

        for(int i=0; i<userCount; i++) {
            User u = new User();
            u.avatar = avatars.get(getRandom(avatars.size()));
            u.id = i + 1000;
            u.nickname = names.get(getRandom(names.size()));
            u.detail = userDetails.get(i);
            u.relationship = getRandom(0, 3); //三种用户关系
            users.add(u);
        }

    }

    private static void setVideos() {
        videos.clear();
        for(int i=0; i<getRandom(DEFAULT_COUNT, 20); i++) {
            Video v = new Video();
            v.author = users.get(getRandom(users.size()));
            v.beLikeCount = getRandom(3000);
            v.desc = "好搞笑啊好搞笑啊好搞笑啊 --- 视频精选" + getRandom(100);
            v.id = i + 100;
            int img = getRandom(listImgs.size());
            v.oriImage = listImgs.get(img);
            v.playCount = getRandom(100);
            v.publishTime = 1426597359;
            v.thumbnail = listImgs.get(img);
            v.url = videoUrls.get(getRandom(videoUrls.size()));
            videos.add(v);
        }
    }

    private static void setTopics() {
        topics.clear();
        discoveryTopicItems.clear();

        int len = getRandom(DEFAULT_COUNT, 20);
        for(int i=0; i<len; i++) {
            Topic t = new Topic();
            t.id = i + 500;
            Image img = avatarImgs.get(getRandom(avatarImgs.size()));
            t.oriImage = img;
            t.playCount = getRandom(10000);
            t.thumbnail = img;
            t.title = getRandom(1, 4) % 3 == 0 ? "#不作死就不会死系列:"+i +"#":
                ( getRandom(1, 4) % 3 == 1 ?"#失恋后你会怎么做系列:"+i+"#" : "#Duang~~Duang~~Duang~~系列:"+i+"#");
            t.worksCount = getRandom(100);
            topics.add(t);
        }

        for(int i=0; i<len; i++) {
            DiscoveryTopicItem dti = new DiscoveryTopicItem();
            dti.topic = topics.get(i);

            List<Video> ts = new ArrayList<Video>();
            int jCount = getRandom(1, 5);
            for(int j = 0; j < jCount; j++) {
                ts.add(videos.get(getRandom(videos.size())));
            }
            discoveryTopicItems.add(dti);
        }
    }

    private static void setMessages() {
        messages.clear();
        comments.clear();
        setComments();

        int lenght = getRandom(0, 30);
        for(int i=0; i<lenght; i++) {
            Message m = new Message();
            m.id = messageID++;
            m.type = getRandom(1,4);
            m.content = "消息内容:" + getRandom(10);
            m.user = users.get(getRandom(users.size()));
            m.video = videos.get(getRandom(videos.size()));
            m.comment = comments.get(getRandom(comments.size()));
            m.publishTime = 20150409;
            messages.add(m);
        }
    }
    private static void setFeedback() {
        feedback.clear();
        comments.clear();
        setComments();

        int lenght = getRandom(0, 30);
        for(int i=0; i<lenght; i++) {
            Feedback m = new Feedback();
            m.id = messageID++;
            m.type = getRandom(0,2);
            m.content = "消息内容:" + getRandom(10);
            m.author = users.get(getRandom(users.size()));
            m.publishTime = 20150409;
            feedback.add(m);
        }
    }

    private static void setComments() {
        for(int i=0; i<getRandom(10, 30); i++) {
            Comment c = new Comment();
            c.author = users.get(getRandom(users.size()));
            c.content = "这个是评论内容:" + getRandom(1000);
            c.id = 100 + i;
            c.publishTime = (int) System.currentTimeMillis();
            c.replyAuthor = getRandom(2) == 0 ? null : users.get(getRandom(users.size()));
            c.replyId = c.replyAuthor == null ? 0 : c.id-1;
            comments.add(c);
        }
    }

    public static User getDefaultUser() {
        resetData();
        return users.get(0);
    }

//    /** 获取首页精选视频 */
//    public static VideosData getHandpickVideoData() {
//        resetData();
//        VideosData hvp = new VideosData();
//        hvp.count = videos.size();
//        hvp.items = videos;
//        return hvp;
//    }
//
//    /** 获取首页发现模块的话题 **/
//    public static DiscoveryTopicsData getDiscoveryTopicData() {
//        resetData();
//        DiscoveryTopicsData dt = new DiscoveryTopicsData();
//        dt.count = discoveryTopicItems.size();
//        dt.items = discoveryTopicItems;
//        return dt;
//    }
//
//    /** 获取热门话题列表 */
//    public static TopicsData getTopicPageData() {
//        resetData();
//        TopicsData tp = new TopicsData();
//        tp.count = topics.size();
//        tp.items = topics;
//        return tp;
//    }
//
//    /** 获取最新视频列表 */
//    public static VideosData getTimelineVideoPageData() {
//        resetData();
//        VideosData tvp = new VideosData();
//        tvp.count = videos.size();
//        tvp.items = videos;
//        return tvp;
//    }
//
//    /** 获取用户动态中的视频列表 */
//    public static VideosData getDynamicVideoPageData() {
//        resetData();
//        VideosData dp = new VideosData();
//        dp.count = videos.size();
//        dp.items = videos;
//        return dp;
//    }
//
//    /** 获取话题视频列表 */
//    public static VideosData getTopicVideoPageData() {
//        resetData();
//        VideosData tp = new VideosData();
//        tp.count = videos.size();
//        tp.items = videos;
//        return tp;
//    }
//
//    /** 获取推荐的达人用户列表 */
//    public static FunnyMastersData getSuggestUserPageData() {
//        resetData();
//        users.addAll(users);
//
//        List<FunnyMaster> list = new ArrayList<FunnyMaster>();
//        for(User u : users) {
//            FunnyMaster fm = new FunnyMaster();
//            fm.avatar = u.avatar;
//            fm.description = "推荐"+getRandom(100);
//            fm.detail = u.detail;
//            fm.id = u.id;
//            fm.nickname= u.nickname;
//            fm.relationship = u.relationship;
//            list.add(fm);
//        }
//
//        FunnyMastersData su = new FunnyMastersData();
//        su.count = list.size();
//        su.items = list;
//        return su;
//    }

//    /** 获取微博好友列表 */
//    public static WeiboFriendsData getWeiboFriendsPageData() {
//        resetData();
//        WeiboFriendsData wp = new WeiboFriendsData();
//        UsersData f1 = new UsersData();
//        f1.count = users.size();
//        f1.items = users;
//        UsersData f2 = new UsersData();
//        f2.count = users.size();
//        f2.items = users;
//
//        wp.friends = f1;
//        wp.invitations = f2;
//        return wp;
//    }

//    /** 获取我喜欢的视频列表 */
//    public static VideosData getFavoriteVideoPageData() {
//        resetData();
//        VideosData dp = new VideosData();
//        dp.count = videos.size();
//        dp.items = videos;
//        return dp;
//    }
//
//    /** 获取用户创作的视频作品列表 */
//    public static VideosData getUserVideoPageData() {
//        resetData();
//        VideosData sp = new VideosData();
//        sp.count = videos.size();
//        sp.items = videos;
//        return sp;
//    }

//    /** 获取用户关注的用户列表 */
//    public static UsersData getFriendsPageData() {
//        resetData();
//        UsersData fp = new UsersData();
//        fp.count = users.size();
//        fp.items = users;
//        return fp;
//    }
//
//    /** 获取用户的粉丝列表 */
//    public static UsersData getFollowersPageData() {
//        resetData();
//        UsersData fp = new UsersData();
//        fp.count = users.size();
//        fp.items = users;
//        return fp;
//    }

//    /** 获取用户消息列表 */
//    public static MessagesData getMessagesPageData() {
//        setMessages();
//        MessagesData mp = new MessagesData();
//        mp.count = messages == null ? 0 : messages.size();
//        mp.items = messages;
//        return mp;
//    }
//    /** 获取反馈列表 */
//    public static FeedbacksData getFeedbackPageData() {
//        setFeedback();
//        FeedbacksData mp = new FeedbacksData();
//        mp.count = feedback == null ? 0 : feedback.size();
//        mp.items = feedback;
//        return mp;
//    }

    /** 获取启动图 */
    public static SplashImage getSplashImageData() {
        SplashImage si = new SplashImage();
        Image img = new Image();
        img.height = 1080;
        img.width = 720;
        img.url = splashImages.get(getRandom(splashImages.size()));
        si.endTime = (int) (System.currentTimeMillis() + 3600 *10);
        si.startTime = (int) (System.currentTimeMillis() - 3600);
        si.image = img;
        return si;
    }

//    /** 获取详情页评论列表 */
//    public static CommentsData getCommentPageData() {
//        resetData();
//        setComments();
//        CommentsData c = new CommentsData();
//        c.count = comments.size();
//        c.items = comments;
//        return c;
//    }
//    public static Video getVideoDetail(){
//        int count = getDynamicVideoPageData().items.size();
//        return  getDynamicVideoPageData().items.get(getRandom(count-2));
//    }

}
