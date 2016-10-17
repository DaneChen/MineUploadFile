package com.lovebaby.uploadfileprj;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import android.content.Context;
import android.content.Intent;

/**
 * UploadFileUtils 上传文件的对外接口
 * @author chengr
 *
 */
public class UploadFileUtils {
	 
	//上传中的任务队列
	private static Map<Integer, UploadTaskInfo> uploadTasksMap = new ConcurrentHashMap<Integer, UploadTaskInfo>();
	
	/*******************************操作单文件***************************/
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
	
	/**
	 * 上传多个文件
	 * @param paramFileInfoList 文件信息列表
	 * @param paramContext 上下文
	 */
	public static void uploadMultiFiles(List<UploadFileInfo> paramFileInfoList, Context paramContext)
	{
		
	}
	
	/**
	 * 取消上传
	 * @param paramFileId 文件id
	 */
	public static void cancelOneFile(int paramFileId)
	{
		//移除file信息
		 removeOneFile(paramFileId);
	     
	     //清除service中的任务
	     UploadTask task = UploadService.getTaskFromMap(paramFileId);
	     if( task == null)
	     {
	    	 LogUtils.e("task is null");
	    	 return;
	     }
	     task.stopThisTaskInService();
	}
	
	/**
	 * 暂停文件上传
	 * @param paramFileId
	 */
	public static void pauseOneFile(int paramFileId){
		
	}
	
	/**
	 * 继续单个文件上传
	 * @param paramFileId 文件id
	 */
	public static void continueOneFile(int paramFileId)
	{
		
	}
	
	/*********************************操作一个任务************************************/
	/**
	 * 取消单个任务
	 * @param paramTaskId 任务id
	 */
	public static void cancelOneTask(int paramTaskId)
	{
		
	}
	
	/**
	 * 暂停单个任务
	 * @param paramTaskId 任务id
	 */
	public void pauseOneTask(int paramTaskId){
		
	}
	
	/**
	 * 暂停单个任务
	 * @param paramTaskId 任务id
	 */
	public static void continueOneTask(int paramTaskId){
		
	}
	
	/*******************************操作所有任务**************************************/
	/**
	 * 取消所有任务
	 */
	public static void cancelAllTask(){
		
	}
	
	/**
	 * 暂停所有任务
	 * @param paramTaskId 任务id
	 */
	public static void pauseAllTask(){
		
	}
	
	/**
	 * 继续所有任务
	 * @param paramTaskId 任务id
	 */
	public static void continueAllTask(){
		
	}
	
	/***************任务上传回调************UploadFileInterface*********************************/
	
	private static UploadFileInterface sUploadFileInterface = new UploadFileInterface() {
		
		@Override
		public void onSuccess(UploadFileInfo uploadInfo) {
			LogUtils.d("onSuccess..");
			// 移除任务队列中的文件
			removeOneFile(uploadInfo.getUploadFileId());
		}
		
		@Override
		public void onProgress(UploadFileInfo uploadInfo) {
			LogUtils.d("onProgress..");
		}
		
		@Override
		public void onError(UploadFileInfo uploadInfo, String errorMsg) {
			LogUtils.d("onError..");
		}
		
		@Override
		public void onCompleted(UploadFileInfo uploadInfo) {
			LogUtils.d("onCompleted..");
		}
		
		@Override
		public void onCancelled(UploadFileInfo uploadInfo) {
			LogUtils.d("onCancelled..");
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
		int size = uploadTasksMap.size();
		if( size == 0)
		{
			return ((size + 1)*10 + 1);
		}else{
			int maxKey = 0;
			for( Map.Entry<Integer, UploadTaskInfo> entry:uploadTasksMap.entrySet())
			{
				int key = entry.getKey().intValue();
				if(key > maxKey)
				{
					maxKey = key;
				}
			}
			return maxKey + 1;
		}
	}
	
	/**
	 * 生成fileId 
	 * taskId*10 + index
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
	
	/**
	 * 移除一个文件 如果文件对应的任务中只有一个
	 * @param paramFileId 文件id
	 */
	private static void removeOneFile(int paramFileId)
	{
		 Integer[] keySet = uploadTasksMap.keySet().toArray(new Integer[0]);
	     for( Integer key : keySet)
	     {
	    	 UploadTaskInfo taskToCancel = uploadTasksMap.get(key);
        	 if( taskToCancel != null)
        	 {
        		 List<UploadFileInfo> fileInfoList = taskToCancel.getUploadFileInfoList();
        		 if(!Tools.isListAvailable(fileInfoList))
        		 {
        			 //list 为空
        			 LogUtils.e("fileInfoList is null>error!");
        			 break;
        		 }
        		 
        		 int size = fileInfoList.size();
        		 if( size == 1)
        		 {
        			 //如果只有一个文件，则移除任务即可
        			 boolean removeRet = taskToCancel.removeFileInfoByFileId(paramFileId);
        			 if( removeRet)
        			 {
        				 //如果移除成功
        				 uploadTasksMap.remove(key);
        				 break;
        			 }
        		 }else{
        			 //如果有多个文件，则移除对应的文件
        			 boolean removeRet = taskToCancel.removeFileInfoByFileId(paramFileId);
        			 if( removeRet)
        			 {
        				 //如果移除成功
        				 break;
        			 }
        		 }
        	 }else
        	 {
        		 LogUtils.e("taskToCancel is null>error!");
        	 }
        }
	}
	
	/**
	 * 移除一个任务
	 * @param paramTaskId 任务id
	 */
	private void removeOneTask(int paramTaskId)
	{
		uploadTasksMap.remove(paramTaskId);
	}
	
	/**
	 * 根据fileId获取所在的taskInfo
	 */
	private UploadTaskInfo getTaskInfoByFileId(int paramFileId)
	{
		Integer[] keySet = uploadTasksMap.keySet().toArray(new Integer[0]);
	     for( Integer key : keySet)
	     {
	    	 UploadTaskInfo taskToCancel = uploadTasksMap.get(key);
	       	 if( taskToCancel != null)
	       	 {
	       		UploadFileInfo fileInfo = taskToCancel.getFileInfoByFileId(paramFileId);
	       		if( fileInfo != null)
	       		{
	       			return uploadTasksMap.get(key);
	       		}
	       	 }else
	       	 {
	       		 LogUtils.e("taskToCancel is null>error!");
	       	 }
	     }
	     return null;
	}
	
	
}
