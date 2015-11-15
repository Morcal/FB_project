package com.feibo.ffmpeg;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

import android.util.Log;

import fbcore.log.LogUtil;


public class ProcessRunnable implements Runnable {

	private static final String TAG = "FfmpegJob";

	private final ProcessBuilder mProcess;

	private ProcessListener mListener;

	public ProcessRunnable(ProcessBuilder process) {
		mProcess = process;
	}

	@Override
	public void run() {
		Process proc = null;
		try {
		    List<String> args = mProcess.command();
		    for (String arg : args) {
		        LogUtil.d(TAG, arg);
		    }
			proc = mProcess.start();
		} catch (IOException e) {
			Log.e(TAG, "IOException starting process", e);
			return;
		}

		// Consume the stdout and stderr
		if (mListener != null) {
			mListener.stdOut(proc.getInputStream());
			mListener.stdErr(proc.getErrorStream());
		}

		// Wait for process to exit
		int exitCode = 1; // Assume error
		try {
			exitCode = proc.waitFor();
		} catch (InterruptedException e) {
			Log.e(TAG, "Process interrupted!", e);
		}

		if (mListener != null) {
			mListener.onExit(exitCode);
		}
	}

	public void setProcessListener(ProcessListener listener) {
		mListener = listener;
	}

	public interface ProcessListener {
		public void stdOut(InputStream stream);
		public void stdErr(InputStream stream);
		public void onExit(int exitCode);
	}

	public static abstract class AbsProcessListener implements ProcessListener {
	    @Override
        public void stdOut(InputStream stream) {
	        LogUtil.d(TAG, "stdout, " + convertStreamToString(stream));
	    }
        @Override
        public void stdErr(InputStream stream) {
            LogUtil.e(TAG, "stderr, " + convertStreamToString(stream));
        }

        public String convertStreamToString(InputStream is) {
            /*
             * To convert the InputStream to String we use the
             * BufferedReader.readLine() method. We iterate until the
             * BufferedReader return null which means there's no more data to
             * read. Each line will appended to a StringBuilder and returned as
             * String.
             */
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            StringBuilder sb = new StringBuilder();

            String line = null;
            try {
                while ((line = reader.readLine()) != null) {
                    sb.append(line + "\n");
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            return sb.toString();
        }
	}

}
