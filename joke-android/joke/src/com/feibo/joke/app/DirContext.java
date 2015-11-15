package com.feibo.joke.app;

import java.io.File;
import java.io.IOException;

import android.content.Context;

import fbcore.utils.Files;
import fbcore.utils.Utils;

import com.feibo.joke.utils.TimeUtil;

/**
 * 应用文件系统上下文.
 *
 * 负责应用中文件目录的初始化等工作.
 *
 *
 */
public class DirContext {

	private static DirContext sInstance = null;

	private String mCacheDir;

	public enum DirEnum {

		ROOT_dir("lamejoke"), IMAGE("image"), CACHE("cache"), DOWNLOAD("download"), RECORD("record"), OUTPUT("output"), PUBLISH("publish");

		private String value;

		private DirEnum(String value) {
			this.value = value;
		}

		public String getValue() {
			return value;
		}
	}

	private DirContext() {
		initDirContext();
	}

	public static DirContext getInstance() {
		if (sInstance == null) {
            sInstance = new DirContext();
        }
		return sInstance;
	}

	private void initDirContext() {

	}

	public void initCacheDir(Context context) {
	    this.mCacheDir = Utils.getDiskCacheDir(context, "").getAbsolutePath();
	}

	public File getRootDir() {
		File file = new File(
				android.os.Environment.getExternalStorageDirectory(),
				DirEnum.ROOT_dir.getValue());

		if(!file.exists() || !file.isDirectory()) {
            file.mkdirs();
        }

		return file;
	}

	public File getDir(DirEnum dirEnum) {
		File file = new File(getRootDir(), dirEnum.getValue());
		if(!file.exists() || !file.isDirectory()) {
            file.mkdirs();
        }
		if (dirEnum.equals(DirEnum.RECORD) || dirEnum.equals(DirEnum.OUTPUT)) {
		    try {
                Files.create(new File(file, ".nomedia"));
            } catch (IOException e) {
                e.printStackTrace();
            }
		}
		return file;
	}

	/**
	 * 新建或获得保存视频的路径.
	 * @return
	 */
	public File getOutputDir() {
	    File outputRoot = getDir(DirEnum.OUTPUT);
	    File file = new File(outputRoot, TimeUtil.generateTimestamp());
	    if (!file.exists() || !file.isDirectory()) {
	        file.mkdirs();
	    }
	    return file;
	}

	/**
	 * 获取目录parent下的缩略图目录.
	 * @param parent
	 * @return
	 */
	public File getThumbsDir(File parent) {
	    File file = new File(parent, ".thumbs");
	    if (!file.exists() || !file.isDirectory()) {
            file.mkdirs();
        }
        return file;
	}

	/**
	 * 获取临时目录
	 * @return
	 */
	public File getTmpDir() {
	    File file = new File(mCacheDir, "temp");
	    if (!file.exists() || !file.isDirectory()) {
            file.mkdirs();
        }
	    try {
            Files.create(new File(file, ".nomedia"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return file;
	}
}