package com.lovebaby.uploadfileprj;

import java.net.FileNameMap;
import java.net.URLConnection;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Tools {

	
	/**
	 * 获取mineType
	 * @param fileUrl
	 * @return
	 * @throws java.io.IOException
	 */
	public static String getMimeType(String fileUrl) throws java.io.IOException {
		FileNameMap fileNameMap = URLConnection.getFileNameMap();
		String type = fileNameMap.getContentTypeFor(fileUrl);
		return type;
	}
	
	/**
	 * 字符串是否为空
	 * @param val
	 * @return
	 */
	public static boolean isStringBlank(String val){
		return val == null || val.equals("") || val.trim().equals("");
	}
	
	/**
	 * 限制线程启动数量
	 * @param runnable
	 */
	private static ExecutorService fixedThreadPool = null;
	public static synchronized void openFixedThread(Runnable runnable) {
		if (runnable!=null) {
			if (fixedThreadPool==null) {
				fixedThreadPool = Executors.newFixedThreadPool(3);//创建一个定长线程池，可控制线程最大并发数，超出的线程会在队列中等待
			}
			fixedThreadPool.execute(runnable);
		}
	}
	
}
