package com.lovebaby.uploadfileprj;

import java.util.Map;

/**
 * 上传任务中单个文件信息
 * @author chengr
 *
 */
public class UploadFileInfo {
	
	/***传入数据**/
	private int uploadType;//上传类型 UploadConstant.UploadType
	private String filePath;//本地文件路径
	private String uploadUrl;//上传文件的url  如果uploadType为0，则uploadUrl为上传文件的url;如果为1，则为从服务器获取token的url
	private String setFileInfoUrl; //设置传完后的文件信息，如果uploadType为0，则不用填写，如果是1，则需要填写
	private Map<String,String> bizDataMap; //需要的业务数据,由上层传入，不管里面的细节，只作提交
	
	/***内部数据**/
	private int uploadTaskId;//归属的任务id
	private int uploadFileId;//上传的文件id
	
	private int uploadStatus;//上传状态 UploadStatus
	private double uploadProgress;//上传进度
	
	public int getUploadTaskId() {
		return uploadTaskId;
	}
	public void setUploadTaskId(int uploadTaskId) {
		this.uploadTaskId = uploadTaskId;
	}
	public int getUploadFileId() {
		return uploadFileId;
	}
	public void setUploadFileId(int uploadFileId) {
		this.uploadFileId = uploadFileId;
	}
	public int getUploadType() {
		return uploadType;
	}
	public void setUploadType(int uploadType) {
		this.uploadType = uploadType;
	}
	public String getFilePath() {
		return filePath;
	}
	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}
	public String getUploadUrl() {
		return uploadUrl;
	}
	public void setUploadUrl(String uploadUrl) {
		this.uploadUrl = uploadUrl;
	}
	public String getSetFileInfoUrl() {
		return setFileInfoUrl;
	}
	public void setSetFileInfoUrl(String setFileInfoUrl) {
		this.setFileInfoUrl = setFileInfoUrl;
	}
	public Map<String, String> getBizDataMap() {
		return bizDataMap;
	}
	public void setBizDataMap(Map<String, String> bizDataMap) {
		this.bizDataMap = bizDataMap;
	}
	public int getUploadStatus() {
		return uploadStatus;
	}
	public void setUploadStatus(int uploadStatus) {
		this.uploadStatus = uploadStatus;
	}
	public double getUploadProgress() {
		return uploadProgress;
	}
	public void setUploadProgress(double uploadProgress) {
		this.uploadProgress = uploadProgress;
	}
}
