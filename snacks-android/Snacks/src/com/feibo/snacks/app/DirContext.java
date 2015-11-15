package com.feibo.snacks.app;

import java.io.File;

/**
 * 应用文件系统上下文.
 *
 * 负责应用中文件目录的初始化等工作.
 *
 *
 */
public class DirContext {

	private static DirContext sInstance = null;

	public enum DirEnum {

		ROOT_dir("snacks"), IMAGE("image"), CACHE("cache"), DOWNLOAD("download");

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
		return file;
	}
}
