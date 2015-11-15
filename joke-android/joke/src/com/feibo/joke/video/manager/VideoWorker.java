package com.feibo.joke.video.manager;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class VideoWorker {
	
	private static VideoWorker woker;
	
	public static VideoWorker getInstance(){
		if(woker == null){
			woker = new VideoWorker();
		}
		return woker;
	}
	
	private Executor mExecutor;
	private VideoWorker(){
		mExecutor = Executors.newCachedThreadPool();
	}
	
	public void execute(Runnable runnable){
		mExecutor.execute(runnable);
	}
}
