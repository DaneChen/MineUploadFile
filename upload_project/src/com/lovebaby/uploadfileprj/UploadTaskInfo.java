package com.lovebaby.uploadfileprj;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.lovebaby.uploadfileprj.tools.LogUtils;
import com.lovebaby.uploadfileprj.tools.Tools;

/**
 * 上传任务信息 ，里面可能包含多个文件的上传信息
 * @author chengr
 *
 */
public class UploadTaskInfo {
	private int uploadTaskId;//上传的任务id,单次提交来区分
	private UploadOption uploadOption; //上传选项
	private List<UploadFileInfo> uploadFileInfoList; //任务中的文件信息列表
	
	//上传结果回调(给上层的回调)
	private UploadFileCallBack mUploadFileInterface;
	
	private int taskUploadStatus;//上传状态
	private int successNum;//已成功的个数
	
	public UploadTaskInfo(int taskID, UploadFileCallBack paramInterface)
	{
		uploadTaskId = taskID;
		uploadFileInfoList = Collections.synchronizedList(new ArrayList<UploadFileInfo>());
		mUploadFileInterface = paramInterface;
	}
	
	public int getUploadTaskId() {
		return uploadTaskId;
	}
	public void setUploadTaskId(int uploadTaskId) {
		this.uploadTaskId = uploadTaskId;
	}
	public UploadOption getUploadOption() {
		return uploadOption;
	}
	public void setUploadOption(UploadOption uploadOption) {
		this.uploadOption = uploadOption;
	}
	public List<UploadFileInfo> getUploadFileInfoList() {
		return uploadFileInfoList;
	}
	public void setUploadFileInfoList(List<UploadFileInfo> uploadFileInfoList) {
		this.uploadFileInfoList = uploadFileInfoList;
	}
	public int getTaskUploadStatus() {
		return taskUploadStatus;
	}
	public void setTaskUploadStatus(int taskUploadStatus) {
		this.taskUploadStatus = taskUploadStatus;
	}
	public int getSuccessNum() {
		return successNum;
	}
	public void setSuccessNum(int successNum) {
		this.successNum = successNum;
	}
	
	public UploadFileCallBack getmUploadFileInterface() {
		return mUploadFileInterface;
	}

	public void setmUploadFileInterface(UploadFileCallBack mUploadFileInterface) {
		this.mUploadFileInterface = mUploadFileInterface;
	}

	/**
	 * 根据fileId 获取文件信息类
	 * @param paramFileInfoList
	 * @param paramFileId
	 * @return
	 */
	public  UploadFileInfo getFileInfoByFileId(int paramFileId)
	{
		if(!Tools.isListAvailable(uploadFileInfoList))
		{
			return null;
		}
		int size = uploadFileInfoList.size();
		for( int i = 0; i < size; i ++)
		{
			UploadFileInfo tempInfo = uploadFileInfoList.get(i);
			if(tempInfo == null)
				continue;
			
			if( tempInfo.getUploadFileId() == paramFileId)
			{
				return tempInfo;
			}
		}
		return null;
	}
	
	/**
	 * 根据fileId 移除文件信息
	 * @param paramFileId
	 */
	public boolean removeFileInfoByFileId(int paramFileId)
	{
		UploadFileInfo fileInfo = getFileInfoByFileId(paramFileId);
		 if( fileInfo == null)
		 {
			 LogUtils.e("fileInfo is null>error!");
			 return false;
		 }
		 return uploadFileInfoList.remove(fileInfo);
	}
	
	
	//单张的进度提示
    public void onProgress(int fileId, double progress)
    {
    	if( mUploadFileInterface == null)
    	{
    		LogUtils.e("mUploadFileInterface is null>error!");
    		return;
    	}
    	mUploadFileInterface.onProgress(fileId, progress);
    }

    //单张失败
    public void onError(int fileId, final String errorMsg)
    {
    	if( mUploadFileInterface == null)
    	{
    		LogUtils.e("mUploadFileInterface is null>error!");
    		return;
    	}
    	mUploadFileInterface.onError(fileId, errorMsg);
    }

    //单张成功
    public void onSuccess(int fileId)
    {
    	if( mUploadFileInterface == null)
    	{
    		LogUtils.e("mUploadFileInterface is null>error!");
    		return;
    	}
    	mUploadFileInterface.onSuccess(fileId);
    }
    
    
    //一个任务的进度
    public void onTaskProgress(int taskId, int totalNum, int successNum)
    {
    	if( mUploadFileInterface == null)
    	{
    		LogUtils.e("mUploadFileInterface is null>error!");
    		return;
    	}
    	mUploadFileInterface.onTaskProgress(taskId, totalNum, successNum);
    }
    
}
