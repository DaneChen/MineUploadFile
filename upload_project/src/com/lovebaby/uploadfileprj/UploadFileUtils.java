package com.lovebaby.uploadfileprj;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import android.content.Context;
import android.content.Intent;

/**
 * 上传文件的对外接口
 * @author chengr
 *
 */
/**
 * @author Administrator
 *
 */
public class UploadFileUtils {
	 
	//上传中的任务队列
	private static Map<Integer, UploadTaskInfo> uploadTasksMap = new ConcurrentHashMap<Integer, UploadTaskInfo>();
	
	/********************操作单文件*************************/
	/**
	 * 上传单个文件
	 * @param paramFileInfo 文件信息
	 * @return 返回文件信息的操作id
	 */
	public static int uploadSingleFile(UploadFileInfo paramFileInfo, Context paramContext)
	{
		if( paramFileInfo == null)
		{
			return 0;
		}
		
		//1、生成上传任务id
		int taskId = createTaskId();
		UploadTaskInfo taskInfo = new UploadTaskInfo(taskId);
		
		//2、生成文件id
		int fileId = createFileId(taskId, 1);
		paramFileInfo.setUploadTaskId(taskId);
		paramFileInfo.setUploadFileId(fileId);
		taskInfo.getUploadFileInfoList().add(paramFileInfo);
		uploadTasksMap.put(taskId, taskInfo);
		
		//3、生成上传任务task
		UploadTask uploadTask = new UploadTask(paramFileInfo, sUploadFileInterface);
		
		//4、提交任务到service
		commitTask(fileId, uploadTask, paramContext);
		//5、更新状态
		return 0;
	}
	
	public static void cancelOneFile()
	{
		
	}
	
	public static void pauseOneFile(){
		
	}
	
	public static void continueOneFile()
	{
		
	}
	
	/*********************************操作一个任务************************************/
	public static void uploadMultiFiles()
	{
		
	}
	
	public static void cancelOneTask()
	{
		
	}
	
	public void pauseOneTask(){
		
	}
	
	public static void continueOneTask(){
		
	}
	
	
	/*******************************操作所有任务************************************/
	public static void cancelAllTask(){
		
	}
	
	public static void pauseAllTask(){
		
	}

	/************************UploadFileInterface*******************************/
	
	private static UploadFileInterface sUploadFileInterface = new UploadFileInterface() {
		
		@Override
		public void onSuccess(UploadFileInfo uploadInfo) {
			// TODO Auto-generated method stub
			
		}
		
		@Override
		public void onProgress(UploadFileInfo uploadInfo) {
			// TODO Auto-generated method stub
			
		}
		
		@Override
		public void onError(UploadFileInfo uploadInfo, String errorMsg) {
			// TODO Auto-generated method stub
			
		}
		
		@Override
		public void onCompleted(UploadFileInfo uploadInfo) {
			// TODO Auto-generated method stub
			
		}
		
		@Override
		public void onCancelled(UploadFileInfo uploadInfo) {
			// TODO Auto-generated method stub
			
		}
	};
	
	/**
	 * 生成任务id
	 * @return
	 */
	private static int createTaskId()
	{
		if( uploadTasksMap == null)
		{
			uploadTasksMap = new ConcurrentHashMap<Integer, UploadTaskInfo>();
		}
		return uploadTasksMap.size() + 1;
	}
	
	/**
	 * 生成fileId
	 * @param taskId 任务id
	 * @param fileIndex 文件编号
	 * @return
	 */
	private static int createFileId(int taskId, int fileIndex)
	{
		return taskId*10 + fileIndex;
	}
	
	/**
	 * 提交任务到service
	 * @param paramTask
	 */
	private static void commitTask(int fileId, UploadTask paramTask, Context paramContext)
	{
		//1、加到队列
		UploadService.addTaskToService(fileId, paramTask);
		
		//2、启动service
	    final Intent intent = new Intent(paramContext, UploadService.class);
	    intent.putExtra(UploadService.PARAM_TASK_FILE_ID, fileId);
	    paramContext.startService(intent);
	}
}
