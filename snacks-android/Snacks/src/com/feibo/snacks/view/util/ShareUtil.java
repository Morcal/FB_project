package com.feibo.snacks.view.util;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

import com.feibo.snacks.R;
import com.feibo.snacks.app.AppContext;
import com.feibo.snacks.model.bean.ShareBean;
import com.feibo.snacks.model.dao.cache.DataDiskProvider;
import com.feibo.snacks.util.StringUtil;
import com.feibo.snacks.util.Util;
import com.feibo.snacks.view.dialog.ShareDialog;
import com.feibo.social.ResultListener;
import com.feibo.social.base.Platform;
import com.feibo.social.base.Platform.Extra;
import com.feibo.social.manager.SocialComponent;
import com.feibo.social.model.ShareEntity;
import com.feibo.social.model.ShareEntityBuilder;

import fbcore.cache.image.ImageLoader;

public class ShareUtil {

    private static final String WEIXIN_RESULT_SUCCESS = "分享成功";

    public ShareUtil() {
        
    }
    
    public ShareEntity createEntity(Extra extra, ShareBean shareBean) {
        String title = shareBean.getTitle();
        if (extra.belong() == Platform.SINA) {
            final String content = StringUtil.subString(shareBean.getDesc(), shareBean.getContentUrl(), 280);

            ShareEntity entity = new ShareEntityBuilder().builder().setBitmap(((BitmapDrawable) shareBean.getDrawable()).getBitmap())
                    .setTitle(title).setContent(content).create();
            return entity;
        }

        String content = buildContent(extra, shareBean);
        if (extra.belong() != Platform.QQ) {
            ShareEntity entity = new ShareEntityBuilder().builder().setBitmap(((BitmapDrawable) shareBean.getDrawable()).getBitmap())
                    .setTitle(title).setWebpageUrl(shareBean.getContentUrl()).setContent(content).create();
            return entity;
        } else {
            ShareEntity entity = new ShareEntityBuilder().builder().setTitle(title).setWebpageUrl(shareBean.getContentUrl())
                    .setContent(content).setNetworkImageUrl(shareBean.getImgUrl()).create();
            return entity;
        }
    }

    private String buildContent(final Extra extra, ShareBean shareBean ) {
        StringBuilder sb = new StringBuilder();
        if (extra == Extra.WX_TIMELINE) {
            sb.append(shareBean.getTitle());
            sb.append(shareBean.getContentUrl());
        } else if (extra == Extra.QQ_FRIEND) {
            int urlLen = shareBean.getContentUrl().length();
            if (urlLen < 100) {
                int contentLen = 100 - urlLen;
                CharSequence content = (shareBean.getDesc().length() > contentLen) ? shareBean.getDesc().subSequence(0, contentLen)
                        : shareBean.getDesc();
                sb.append(content);
                sb.append(shareBean.getContentUrl());
            } else {
                sb.append(shareBean.getDesc());
            }
        } else {
            sb.append(shareBean.getDesc());
            sb.append(shareBean.getContentUrl());
        }
        return sb.toString();
    }

    public void share(Context context,String desc,String title,String imgUrl,String contentUrl,IShareResult listener) {
        if (!AppContext.isNetworkAvailable()) {
            RemindControl.showSimpleToast(context, context.getResources().getString(R.string.not_network));
            return;
        }

        final ShareDialog dialog = ShareDialog.show(context);
        dialog.setOnClickListener(new ShareDialog.OnClickListener() {
            @Override
            public void onShare(final Extra extra) {
                if (!AppContext.isNetworkAvailable()) {
                    RemindControl.showSimpleToast(context,
                            context.getResources().getString(R.string.not_network));
                    return;
                }

                if (extra == null) {
                    copyUrl(context,contentUrl);
                    RemindControl.showSimpleToast(context,
                            context.getResources().getString(R.string.copySuccess));
                    return;
                }

                RemindControl.showSimpleToast(context, "分享中...");
                DataDiskProvider.getInstance().getImageLoader().loadImage(imgUrl, new ImageLoader.OnLoadListener() {
                    @Override
                    public void onSuccess(Drawable drawable, boolean immediate) {
                        if (drawable == null) {
                            return;
                        }

                        if (extra.belong() == Platform.WEIXIN) {
                            initListener(context,listener);//微信回调
                        }

                        ShareBean shareBean = new ShareBean();
                        shareBean.setContentUrl(contentUrl);
                        shareBean.setDesc(desc);
                        shareBean.setTitle(title);
                        shareBean.setDrawable(drawable);
                        shareBean.setImgUrl(imgUrl);

                        SocialComponent.create(extra.belong(), ((android.app.Activity)context)).share(
                                createEntity(extra, shareBean), extra, new ResultListener() {
                                    @Override
                                    public void onResult(boolean isSuccess, String result) {
                                        if (isSuccess) {
                                            RemindControl.showSimpleToast(context, "分享成功");
                                            // 分享成功后统计次数
                                            listener.onShareResult(5);
                                        } else {
                                            if (extra.belong() != Platform.QQ) {
                                                RemindControl.showSimpleToast(context, result);
                                            }
                                        }
                                    }
                                });
                        dialog.setCancelable(true);
                        dialog.setCanceledOnTouchOutside(true);
                        dialog.dismiss();
                    }

                    @Override
                    public void onFail() {
                    }
                });
            }

            @Override
            public void onCancel() {
                dialog.dismiss();
            }
        });
    }

    private void initListener(Context context,IShareResult listener) {
        Util.setListener(result -> {
            if (result.contains(WEIXIN_RESULT_SUCCESS)) {
                RemindControl.showSimpleToast(context, "分享成功");
                // 分享成功后统计次数
                listener.onShareResult(5);
            }
        });
    }

    private void copyUrl(Context context,String contentUrl) {
        Util.copyText(context, contentUrl);
    }

    public interface IShareResult{
        void onShareResult(int type);
    }
}
