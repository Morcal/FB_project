package com.feibo.joke.upyun;

import com.feibo.joke.utils.MD5Util;
import com.feibo.joke.utils.ThreadManager;
import com.feibo.joke.utils.TimeUtil;
import com.feibo.joke.view.util.ToastUtil;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import fbcore.log.LogUtil;

/**
 * -----------------------------------------------------------
 * 版 权 ： BigTiger 版权所有 (c) 2015
 * 作 者 : BigTiger
 * 版 本 ： 1.0
 * 创建日期 ：2015/7/13 9:42
 * 描 述 ：
 * <p>
 * -------------------------------------------------------------
 */
public class PicBucket  {
    // 运行前先设置好以下三个参数
    private static final String BUCKET_NAME = "img-v-lxh";
    private static final String OPERATOR_NAME = "videolxh";
    private static final String OPERATOR_PWD = "videolxh0427ji";

    //http://img.v.lengxiaohua.cn/img/avatar/2015/04/7/c/d/1430224244470894.jpg
    /** 绑定的域名 */
    private static final String URL = "http://img.v.lengxiaohua.cn";

    /** 根目录 */
    private static final String DIR_ROOT = "/img/avatar/";

    /** 更具今天的时期生成的路径 **/
    private static final String DATA_DIR = TimeUtil.dateDir();

    private static UpYun upyun = null;

    private UploadListener mListener;

    List<String> uploadLocalPaths;
    List<Picture> uploadSuccessList;
    int totalCount;
    int currentSuccessCount;
    int currentFailCount;

    private IUploadPicturesListener iUploadPicturesListener;

    static{
        // 初始化空间
        upyun = new UpYun(BUCKET_NAME, OPERATOR_NAME, OPERATOR_PWD);

        // ****** 可选设置 begin ******

        // 切换 API 接口的域名接入点，默认为自动识别接入点
        // upyun.setApiDomain(UpYun.ED_AUTO);

        // 设置连接超时时间，默认为30秒
        // upyun.setTimeout(60);

        // 设置是否开启debug模式，默认不开启
        upyun.setDebug(false);

        // ****** 可选设置 end ******

		/*
		 * 一般性操作参考文件空间的demo（FileBucketDemo.java）
		 *
		 * 注：图片的所有参数均可以自由搭配使用
		 */
    }

    public void uploadSinglePic(final String picPath){
        File picFile = new File(picPath);

        if (!picFile.isFile()) {
            ToastUtil.showSimpleToast("本地待上传的图片不存在!");
            return;
        }

        final String picName = MD5Util.getMD5String(TimeUtil.generateTimestamp()+ new Random().nextInt(Integer.MAX_VALUE)) + ".jpeg";
        System.out.println("图片MD5名称:" + picName);


        // 1.上传文件（文件内容）

        ThreadManager.getLongPool().execute(new Runnable() {
			
			@Override
			public void run() {
	            try {
	                writeFile(picPath, picName, 0);
	            } catch (IOException e) {
	                e.printStackTrace();
	            }
			}
		});
        // 2.图片做缩略图；若使用了该功能，则会丢弃原图
        //uploadGmkerl();

        // 3.图片旋转；若使用了该功能，则会丢弃原图
        //uploadRotate();

        // 4.图片裁剪；若使用了该功能，则会丢弃原图
        //uploadCrop();

    }

    /**
     * 上传一组照片
     * @param picPaths
     */
    public void uploadPictures(List<String> picPaths){
        this.uploadLocalPaths = picPaths;
        totalCount = picPaths.size();
        currentSuccessCount = 0;
        currentFailCount = 0;

        //初始化
        uploadSuccessList = new ArrayList<Picture>();
        for(int i=0; i<totalCount; i++) {
            uploadSuccessList.add(i, new Picture());
        }

        for(int i=0; i<picPaths.size(); i++){
            final String picPath = picPaths.get(i);
            File picFile = new File(picPath);
            if (!picFile.isFile()) {
                ToastUtil.showSimpleToast("本地待上传的图片不存在!");
                return;
            }
            final String picName = MD5Util.getMD5String(TimeUtil.generateTimestamp()+ new Random().nextInt(Integer.MAX_VALUE)) + ".jpeg";

            final int finalPos = i;
            ThreadManager.getLongPool().execute(new Runnable() {
				
				@Override
				public void run() {
					try {
	                    writeFile(picPath, picName, finalPos);
	                } catch (IOException e) {
	                    e.printStackTrace();
	                }	
				}
			});
        }
    }

    /**
     * 上传文件
     *
     * @throws IOException
     */
    public void writeFile(String picPath, String picName, int pos) throws IOException {
        // 要传到upyun后的文件路径
        String filePath = DIR_ROOT + DATA_DIR +picName;

        // 本地待上传的图片文件
        File file = new File(picPath);

        // 设置待上传文件的 Content-MD5 值
        // 如果又拍云服务端收到的文件MD5值与用户设置的不一致，将回报 406 NotAcceptable 错误
        upyun.setContentMD5(UpYun.md5(file));

        // 设置待上传文件的"访问密钥"
        // 注意：
        // 仅支持图片空！，设置密钥后，无法根据原文件URL直接访问，需带URL后面加上（缩略图间隔标志符+密钥）进行访问
        // 举例：
        // 如果缩略图间隔标志符为"!"，密钥为"bac"，上传文件路径为"/folder/test.jpg"，
        // 那么该图片的对外访问地址为：http://空间域名 /folder/test.jpg!bac
        //upyun.setFileSecret("bac");

        // 上传文件，并自动创建父级目录（最多10级）
        boolean result = upyun.writeFile(filePath, file, true);

//        System.out.println(filePath + " 上传" + isSuccess(result));

        // 获取上传文件后的信息（仅图片空间有返回数据）
//        System.out.println("\r\n****** " + file.getName() + " 的图片信息 *******");
//        // 图片宽度
//        System.out.println("图片宽度:" + upyun.getPicWidth());
//        // 图片高度
//        System.out.println("图片高度:" + upyun.getPicHeight());
//        // 图片帧数
//        System.out.println("图片帧数:" + upyun.getPicFrames());
//        // 图片类型
//        System.out.println("图片类型:" + upyun.getPicType());
//
//        System.out.println("****************************************\r\n");
//
//        System.out.println("若设置过访问密钥(bac)，且缩略图间隔标志符为'!'，则可以通过以下路径来访问图片：");
//        //System.out.println(URL + filePath + "!bac");
//        System.out.println(URL + filePath);
//        System.out.println();

        if(iUploadPicturesListener != null) {
            if (result) {
                Picture picture = new Picture();
                picture.picUrl = URL + filePath;
                picture.localPath = picPath;
                picture.width = Integer.parseInt(upyun.getPicWidth() == null ? "0" : upyun.getPicWidth());
                picture.height = Integer.parseInt(upyun.getPicHeight() == null ? "0" : upyun.getPicHeight());

                uploadSuccessList.set(pos, picture);

                currentSuccessCount++;
            } else {
                currentFailCount++;
            }
            if(currentFailCount + currentSuccessCount == totalCount) {
                if (currentSuccessCount == totalCount) {
                    iUploadPicturesListener.onSuccess(uploadSuccessList);
                } else {
                    iUploadPicturesListener.onFail();
                }
            }


            LogUtil.d("publish", "publish -> pos=" + pos + ", " + isSuccess(result) +
                    ", totalCount=" + totalCount + ", succuss=" + currentSuccessCount + ", fail=" + currentFailCount);
        }

        if (mListener != null){
            if (result){
                mListener.onSuccess(URL + filePath,
                        picPath,
                        Integer.parseInt(upyun.getPicWidth() == null ? "0" : upyun.getPicWidth()),
                        Integer.parseInt(upyun.getPicHeight() == null ? "0" : upyun.getPicHeight()));
            }else {
                mListener.onFail(URL + filePath, picPath);
            }

        }
    }

    /**
     * 图片做缩略图
     * <p>
     * 注意：若使用了缩略图功能，则会丢弃原图
     *
     * @throws IOException
     */
    public static void uploadGmkerl(String picName, String picPath) throws IOException {

        // 要传到upyun后的文件路径
        String filePath = DIR_ROOT + DATA_DIR +picName;

        // 本地待上传的图片文件
        File file = new File(picPath);

        // 设置缩略图的参数
        Map<String, String> params = new HashMap<String, String>();

        // 设置缩略图类型，必须搭配缩略图参数值（KEY_VALUE）使用，否则无效
        params.put(UpYun.PARAMS.KEY_X_GMKERL_TYPE.getValue(),
                UpYun.PARAMS.VALUE_FIX_BOTH.getValue());

        // 设置缩略图参数值，必须搭配缩略图类型（KEY_TYPE）使用，否则无效
        params.put(UpYun.PARAMS.KEY_X_GMKERL_VALUE.getValue(), "150x150");

        // 设置缩略图的质量，默认 95
        params.put(UpYun.PARAMS.KEY_X_GMKERL_QUALITY.getValue(), "95");

        // 设置缩略图的锐化，默认锐化（true）
        params.put(UpYun.PARAMS.KEY_X_GMKERL_UNSHARP.getValue(), "true");

        // 若在 upyun 后台配置过缩略图版本号，则可以设置缩略图的版本名称
        // 注意：只有存在缩略图版本名称，才会按照配置参数制作缩略图，否则无效
        params.put(UpYun.PARAMS.KEY_X_GMKERL_THUMBNAIL.getValue(), "small");

        // 上传文件，并自动创建父级目录（最多10级）
        boolean result = upyun.writeFile(filePath, file, true, params);

        System.out.println(filePath + " 制作缩略图" + isSuccess(result));
        System.out.println("可以通过该路径来访问图片：" + URL + filePath);
        System.out.println();
    }

    /**
     * 图片旋转
     *
     * @throws IOException
     */
    public static void uploadRotate(String picPath, String picName) throws IOException {

        // 要传到upyun后的文件路径
        String filePath = DIR_ROOT + DATA_DIR +picName;

        // 本地待上传的图片文件
        File file = new File(picPath);

        // 图片旋转功能具体可参考：http://wiki.upyun.com/index.php?title=图片旋转
        Map<String, String> params = new HashMap<String, String>();

        // 设置图片旋转：只接受"auto"，"90"，"180"，"270"四种参数
        params.put(UpYun.PARAMS.KEY_X_GMKERL_ROTATE.getValue(),
                UpYun.PARAMS.VALUE_ROTATE_90.getValue());

        // 上传文件，并自动创建父级目录（最多10级）
        boolean result = upyun.writeFile(filePath, file, true, params);

        System.out.println(filePath + " 图片旋转" + isSuccess(result));
        System.out.println("可以通过该路径来访问图片：" + URL + filePath);
        System.out.println();
    }

    /**
     * 图片裁剪
     *
     * @throws IOException
     */
    public static void uploadCrop(String picPath, String picName) throws IOException {

        // 要传到upyun后的文件路径
        String filePath = DIR_ROOT + DATA_DIR +picName;

        // 本地待上传的图片文件
        File file = new File(picPath);

        // 图片裁剪功能具体可参考：http://wiki.upyun.com/index.php?title=图片裁剪
        Map<String, String> params = new HashMap<String, String>();

        // 设置图片裁剪，参数格式：x,y,width,height
        params.put(UpYun.PARAMS.KEY_X_GMKERL_CROP.getValue(), "50,50,300,300");

        // 上传文件，并自动创建父级目录（最多10级）
        boolean result = upyun.writeFile(filePath, file, true, params);

        System.out.println(filePath + " 图片裁剪" + isSuccess(result));
        System.out.println("可以通过该路径来访问图片：" + URL + filePath);
        System.out.println();
    }

    private static String isSuccess(boolean result) {
        return result ? " 成功" : " 失败";
    }

    public void setOnUploadListener(UploadListener listener){
        mListener = listener;
    }

    public void setUploadPicturesListener(IUploadPicturesListener iUploadPicturesListener) {
        this.iUploadPicturesListener = iUploadPicturesListener;
    }

    public interface UploadListener {
        /**
         * 图片上传成功的回调
         * @param picUrl 图片上传后的url地址
         * @param localPath 图片在本地的路径
         */
        void onSuccess(String picUrl, String localPath, int width, int height);

        void onFail(String picUrl, String localPath);
    }

    public interface IUploadPicturesListener {
        void onSuccess(List<Picture> list);
        void onFail();
    }

    public class Picture {
        public String picUrl;
        public String localPath;
        public int width;
        public int height;

        @Override
        public String toString() {
            return "picUrl:"+ picUrl + ", localPath:" + localPath + ", width:" + width + ", height" + height;
        }
    }

}
