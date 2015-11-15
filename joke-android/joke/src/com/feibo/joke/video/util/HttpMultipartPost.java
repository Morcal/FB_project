package com.feibo.joke.video.util;

import java.io.File;
import java.nio.charset.Charset;
import java.util.Map;

import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;

import fbcore.log.LogUtil;

import com.feibo.joke.video.util.CustomMultipartEntity.ProgressListener;

public class HttpMultipartPost extends AsyncTask<String, Integer, String> {
	private static final String TAG = "HttpMultipartPost";
	private long mTotalSize;
	private Map<String, Object> mPostParams;
	private String mUrl;
	private OnPostListener mListener;

	Handler mHandle;

	public HttpMultipartPost(String url, Map<String, Object> params, OnPostListener listener) {
		mPostParams = params;
		mUrl = url;
		mListener = listener;
		mHandle = new Handler(Looper.getMainLooper());
	}

	@Override
	protected void onPreExecute() {
		mHandle.post(new Runnable() {
			@Override
			public void run() {
				mListener.onStart();
			}
		});
	}

	@Override
	protected String doInBackground(String... params) {
		String serverResponse = null;

		HttpClient httpClient = new DefaultHttpClient();
		HttpContext httpContext = new BasicHttpContext();

		HttpPost httpPost = new HttpPost(mUrl);

		try {
			CustomMultipartEntity multipartContent = new CustomMultipartEntity(
					new ProgressListener() {
						@Override
						public void transferred(long num) {
							publishProgress((int) ((num / (float) mTotalSize) * 100));
						}
					});

			for(Map.Entry<String, Object> entry : mPostParams.entrySet()){
				if (entry.getValue() instanceof String) {
					LogUtil.i(TAG, "string:"+entry.getValue());
					multipartContent.addPart(entry.getKey(), new StringBody((String) entry.getValue(), Charset.forName("UTF-8")));
					continue;
				}

				if (entry.getValue() instanceof File) {
					multipartContent.addPart(entry.getKey(), new FileBody((File) entry.getValue()));
					continue;
				}

			}

			mTotalSize = multipartContent.getContentLength();

			httpPost.setEntity(multipartContent);
			HttpResponse response = httpClient.execute(httpPost, httpContext);
			serverResponse = EntityUtils.toString(response.getEntity());

		} catch (Exception e) {
			e.printStackTrace();
		}

		return serverResponse;
	}

	@Override
	protected void onProgressUpdate(final Integer... progress) {
		mHandle.post(new Runnable() {
			@Override
			public void run() {
				mListener.onProgress(progress[0].intValue());
			}
		});
	}

	@Override
	protected void onPostExecute(final String result) {
		mHandle.post(new Runnable() {
			@Override
			public void run() {
				mListener.onResult(result);
			}
		});
	}

	@Override
	protected void onCancelled() {
		mHandle.post(new Runnable() {
			@Override
			public void run() {
				mListener.onCancel();
			}
		});
	}

	public static interface OnPostListener{
		void onStart();
		void onProgress(int progress);
		void onResult(String result);
		void onCancel();
	}
}
