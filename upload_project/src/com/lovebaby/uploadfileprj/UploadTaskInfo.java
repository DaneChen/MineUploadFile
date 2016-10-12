package com.lovebaby.uploadfileprj;

import java.util.ArrayList;
import java.util.List;

/**
 * 上传任务信息 ，里面可能包含多个文件的上传信息
 * @author chengr
 *
 */
public class UploadTaskInfo {
	private int uploadTaskId;//上传的任务id,单次提交来区分
	private UploadOption uploadOption; //上传选项
	private List<UploadFileInfo> uploadFileInfoList; //任务中的文件信息列表
	
	private int taskUploadStatus;//上传状态
	private int successNum;//已成功的个数
	
	public UploadTaskInfo(int taskID)
	{
		uploadTaskId = taskID;
		uploadFileInfoList = new ArrayList<UploadFileInfo>();
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
}
