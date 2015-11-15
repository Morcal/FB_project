//package com.feibo.joke.manager.work;
//
//import java.io.File;
//import java.util.Date;
//
//import android.content.Context;
//import android.text.TextUtils;
//
//import com.google.gson.reflect.TypeToken;
//
//import fbcore.cache.image.ImageLoader.OnLoadListener;
//import fbcore.cache.image.ImageLoader.OnSavedListener;
//import fbcore.security.Md5;
//import fbcore.task.SyncTask;
//
//import com.feibo.joke.app.AppContext;
//import com.feibo.joke.app.DirContext;
//import com.feibo.joke.app.DirContext.DirEnum;
//import com.feibo.joke.cache.DataProvider;
//import com.feibo.joke.dao.UrlBuilder;
//import com.feibo.joke.model.Response;
//import com.feibo.joke.utils.SPHelper;
//
//
//
//public class LaunchManager {
//
//    private AbsLoadingHelper launchHelper;
//    private Context context;
//    private String imgPath;
//    private Long endTime;
//    private Long startTime;
//    private Launch launch;
//
//    public LaunchManager() {
//        context = AppContext.getContext();
//        launchHelper = new AbsLoadingHelper(AppDataType.LAUNCH) {
//            @Override
//            protected SyncTask generateSyncTask(String url, boolean needCache) {
//                TypeToken<Response<Launch>> token = new  TypeToken<Response<Launch>>(){};
//                return new BaseTask<Launch>(url, token, needCache);
//            }
//        };
//    }
//
//    ILoadingListener iLoadingListener = new ILoadingListener() {
//        private boolean needLoadLaunch() {
//            return (!(startTime == launch.startTime) || !(endTime == launch.endTime) || !imgPath
//                    .equals(launch.img.imgUrl));
//        }
//
//        private void loadLaunchImg() {
//            String fileName = Md5.digest32(launch.img.imgUrl);
//            File dir = DirContext.getInstance().getDir(DirEnum.IMAGE);
//            final String targetDir = new File(dir, fileName).getAbsolutePath();
//            DataProvider.getInstance().getImageLoader().saveImage(targetDir, launch.img.imgUrl, new OnSavedListener() {
//                @Override
//                public void onSaved(String srcPath, String destPath) {
//                    SPHelper.setLaunchStartTime(context, launch.startTime);
//                    SPHelper.setLaunchEndTime(context, launch.endTime);
//                    SPHelper.setLaunchPath(context, targetDir);
//                }
//
//                @Override
//                public void onFail() {
//
//                }
//            });
//        }
//
//        @Override
//        public void onFail(String failMsg) {
//
//        }
//
//        @Override
//        public void onSuccess() {
//            if (launch == null || TextUtils.isEmpty(launch.img.imgUrl)) {
//                return;
//            }
//            if (needLoadLaunch()) {
//                loadLaunchImg();
//            }
//        }
//    };
//
//    public void getLaunchBitmap(OnLoadListener listener, int width, int height) {
//        if (hasLaunchBitmap()) {
//            File file = new File(imgPath);
//            DataProvider.getInstance().getImageLoader().loadImage(file, listener, width, height);
//        }
//        loadLaunch(iLoadingListener);
//    }
//
//    private void loadLaunch(final ILoadingListener listener) {
//        String url = UrlBuilder.buildLaunchUrl();
//        launchHelper.loadData(true, url, false, new IHelperListener() {
//            @Override
//            public void onSuccess() {
//                launch = (Launch) DataProvider.getInstance().getData(AppDataType.LAUNCH);
//                if (listener != null) {
//                    listener.onSuccess();
//                }
//            }
//
//            @Override
//            public void onFail(String failMsg) {
//
//            }
//        });
//    }
//
//    private boolean hasLaunchBitmap() {
//        Date date = new Date();
//        long currentTime = (date.getTime() / 1000);
//        startTime = SPHelper.getLaunchStartTime(context);
//        endTime = SPHelper.getLaunchEndTime(context);
//        imgPath = SPHelper.getLaunchPath(context);
//        return ((currentTime >= startTime) && (endTime >= currentTime) && (imgPath != null) && new File(imgPath)
//                .exists());
//    }
//}
