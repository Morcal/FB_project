package com.feibo.snacks.view.module.person;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.feibo.snacks.R;
import com.feibo.snacks.app.DirContext;
import com.feibo.snacks.util.BitmapUtil;
import com.feibo.snacks.util.FunctionUtil;
import com.feibo.snacks.util.IOUtil;
import com.feibo.snacks.util.ScreenShot;
import com.feibo.snacks.view.base.BaseTitleFragment;
import com.feibo.snacks.view.util.RemindControl;
import com.feibo.snacks.view.widget.MatrixImageView;

import java.io.File;
import java.util.Date;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import fbcore.log.LogUtil;
import fbcore.utils.Files;

/**
 * 裁剪图片
 */
public class ImageEditFragment extends BaseTitleFragment {
    private static final String TAG = ImageEditFragment.class.getSimpleName();
    private static final int MSG_SAVE = 0x10;

    @Bind(R.id.image_matrix)
    MatrixImageView matrixView;

    private TitleViewHolder titleHolder;
    private File bitmapFile;
    private Bitmap bitmap;

    @Override
    public int onCreateTitleBar() {
        return R.layout.layout_base_header;
    }

    @Override
    public View onCreateContentView() {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_edit_image, null);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    protected void initTitleBar(TitleBar titleBar) {
        titleHolder = new TitleViewHolder(titleBar.headView);
        titleHolder.titleText.setText("头像");
        titleHolder.saveBtn.setText("完成");
        titleHolder.saveBtn.setVisibility(View.VISIBLE);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Uri bitmapUri = getActivity().getIntent().getData();
        bitmapFile = IOUtil.uri2FileImage(getActivity(), bitmapUri);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (Files.isGif(bitmapFile)) {
            RemindControl.showSimpleToast("暂不支持此格式");
            handleQuitFail();
            return;
        }
        Bitmap bitmap = BitmapUtil.getFitBitmap(bitmapFile.getAbsolutePath());
        matrixView.setImageBitmap(bitmap);
        matrixView.initSize();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        titleHolder.onDestroy();
        ButterKnife.unbind(this);
    }

    // 保存截图
    private void saveToFile(final Bitmap bitmap) {
        if (bitmap == null) {
            return;
        }

        new Thread(new SaveImageRunnable(bitmap)).start();
    }

    // 裁剪完成
    public void handleCropComplete(String path) {
        Uri uri = Uri.parse(path);
        Intent intent = new Intent();
        intent.setData(uri);
        handleQuitSuccess(intent);
    }

    // 裁剪图片
    public void handleCrop() {
        matrixView.setDrawingCacheEnabled(true);
        bitmap = matrixView.clip();
        saveToFile(bitmap);
    }

    // 裁剪成功，退出
    public void handleQuitSuccess(Intent intent) {
        getActivity().setResult(Activity.RESULT_OK, intent);
        getActivity().finish();
    }

    // 裁剪失败，退出
    public void handleQuitFail() {
        getActivity().setResult(Activity.RESULT_CANCELED, null);
        getActivity().finish();
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(android.os.Message msg) {
            if (msg.what == MSG_SAVE) {
                String path = (String) msg.obj;
                FunctionUtil.notifyMediaScanner(getActivity(), path);
                Log.i("path", path);
                handleCropComplete(path);
            }
        }
    };

    class SaveImageRunnable implements Runnable {
        private static final int BITMAP_SIZE = 140;
        private Bitmap saveBitmap;

        public SaveImageRunnable(Bitmap bitmap) {
            saveBitmap = Bitmap.createScaledBitmap(bitmap, BITMAP_SIZE, BITMAP_SIZE, false);
        }

        @Override
        public void run() {
            String filePath = DirContext.getInstance().getDir(DirContext.DirEnum.IMAGE).getAbsolutePath() +
                    File.separator +
                    new Date().getTime() +
                    ".png";
            bitmap = Bitmap.createScaledBitmap(bitmap, 140, 140, false);
            ScreenShot.saveScreenshot(bitmap, filePath);
            Message msg = new Message();
            msg.what = MSG_SAVE;
            msg.obj = filePath;
            handler.sendMessage(msg);
        }
    }

    class TitleViewHolder {

        @Bind(R.id.head_title)
        TextView titleText;

        @Bind(R.id.head_right)
        TextView saveBtn;

        public TitleViewHolder(View view) {
            ButterKnife.bind(TitleViewHolder.this, view);
        }

        @OnClick(R.id.head_left)
        public void clickHeadLeft() {
            handleQuitFail();
        }

        @OnClick(R.id.head_right)
        public void clickHeadRight() {
            handleCrop();
        }

        public void onDestroy() {
            ButterKnife.unbind(TitleViewHolder.this);
        }
    }
}
