package com.lovebaby.uploadfileprj;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Iterator;
import java.util.Map;

import com.lovebaby.uploadfileprj.UploadConstant.UploadStatus;

/**
 * @author chengr
 *
 */
public class UploadTask implements Runnable {

	//上传的文件信息
	private UploadFileInfo mUploadFileInfo;
	//上传结果回调
	private UploadFileInterface mUploadFileInterface;
	
	private UploadService mService;
	
	
	public UploadTask(UploadFileInfo paramUploadFileInfo, UploadFileInterface paramUploadFileInterface)
	{
		mUploadFileInfo = paramUploadFileInfo;
		mUploadFileInterface = paramUploadFileInterface;
	}
	
	/**
	 * 取消本任务
	 */
	public void cancel()
	{
		
	}
	
	@Override
	public void run() {
		if(mUploadFileInfo == null)
		{
			//出错
			LogUtils.e("mUploadFileInfo is null>error!");
			return;
		}
		//状态改为正在上传
		mUploadFileInfo.setUploadStatus(UploadConstant.UploadStatus.UPLOAD_STATUS_UPLOADING);
		
		if( mUploadFileInfo.getUploadType() == UploadConstant.UploadType.POST_BY_LOCAL_SERVER)
		{
			//本地服务器上传
			uploadFileToLocalServer();
		}else if( mUploadFileInfo.getUploadType() == UploadConstant.UploadType.POST_BY_QINIU_SERVER)
		{
			//传到七牛服务器
			uploadFileToQiNiuServer();
		}else{
			LogUtils.e("unknown mUploadFileInfo.uploadType >error!");
		}
	}
	
	/**
	 * 上传文件到本地服务器
	 */
	private void uploadFileToLocalServer()
	{
		 HttpURLConnection conn = null;  
	        String BOUNDARY = "---------------------------123821742118716"; //boundary就是request头和上传文件内容的分隔符    
	        try { 
	            URL url = new URL(mUploadFileInfo.getUploadUrl());  
	            conn = (HttpURLConnection) url.openConnection();  
	            conn.setConnectTimeout(5000);  
	            conn.setReadTimeout(30000);  
	            conn.setDoOutput(true);  
	            conn.setDoInput(true);  
	            conn.setUseCaches(false);  
	            conn.setRequestMethod("POST");  
	            conn.setRequestProperty("Connection", "Keep-Alive");  
	            conn.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows; U; Windows NT 6.1; zh-CN; rv:1.9.2.6)");  
	            conn.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + BOUNDARY);  
	            conn.connect();
	            
	            OutputStream out = new DataOutputStream(conn.getOutputStream());  
	            // text    
	            if (mUploadFileInfo.getBizDataMap() != null) {  
	                StringBuffer strBuf = new StringBuffer();  
	                Iterator<Map.Entry<String, String>> iter = mUploadFileInfo.getBizDataMap().entrySet().iterator();  
	                while (iter.hasNext()) {  
	                    Map.Entry<String, String> entry = iter.next();  
	                    String inputName = (String) entry.getKey();  
	                    String inputValue = (String) entry.getValue();  
	                    if (inputValue == null) {  
	                        continue;  
	                    }  
	                    strBuf.append("\r\n").append("--").append(BOUNDARY).append("\r\n");  
	                    strBuf.append("Content-Disposition: form-data; name=\"" + inputName + "\"\r\n\r\n");  
	                    strBuf.append(inputValue);  
	                }  
	                out.write(strBuf.toString().getBytes());  
	            }  
	  
	            // file    
	            if( Tools.isStringBlank(mUploadFileInfo.getFilePath()))
	            {
	            	//出错 TODO
	            	uploadFail(UploadConstant.UPLOAD_FAIL_FILE_NOT_EXIST);
	            	return;
	            }
	            
                String inputName = "multipartFile";  
                String inputValue = mUploadFileInfo.getFilePath();  
                File file = new File(inputValue);  
                long totalSize = file.length();
                    
                String filename = file.getName();  
                String contentType = Tools.getMimeType(inputValue); 
  
                StringBuffer strBuf = new StringBuffer();  
                strBuf.append("\r\n").append("--").append(BOUNDARY).append("\r\n");  
                strBuf.append("Content-Disposition: form-data; name=\"" + inputName + "\"; filename=\"" + filename + "\"\r\n");  
                strBuf.append("Content-Type:" + contentType + "\r\n\r\n");  
  
                out.write(strBuf.toString().getBytes());  
  
                DataInputStream in = new DataInputStream(new FileInputStream(file));  
                int bytes = 0;  
                byte[] bufferOut = new byte[1024];  
                while ((bytes = in.read(bufferOut)) != -1) {  
                     out.write(bufferOut, 0, bytes); 
                     //TODO 进度
                     bytes = bytes + bufferOut.length;
                     uploadProgress((bytes*1.0)/totalSize);
                }  
                in.close();  
	  
	            byte[] endData = ("\r\n--" + BOUNDARY + "--\r\n").getBytes();  
	            out.write(endData);  
	            out.flush();  
	            out.close();  
	  
	            //结果
	            int resultCode = conn.getResponseCode();
	            if( resultCode == 200)
	            {
	            	//TODO 成功
	            	uploadSuccess();
	            }else{
	            	//TODO 失败
	            	uploadFail(UploadConstant.UPLOAD_FAIL_NET_ERROR);
	            }
	            
	          /*  // 读取返回数据    
	            StringBuffer strBuf = new StringBuffer();  
	            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));  
	            String line = null;  
	            while ((line = reader.readLine()) != null) {  
	                strBuf.append(line).append("\n");  
	            }  
	            //res = strBuf.toString();  
	            reader.close();  
	            reader = null;  */
	        } catch (Exception e) {  
	           // System.out.println("发送POST请求出错。" + urlStr);  
	        	//LogUtils.e("发送POST请求出错!");
	            e.printStackTrace(); 
	            //TODO 出错
	            uploadFail(UploadConstant.UPLOAD_FAIL_FILE_NOT_EXIST);
	        } finally {  
	            if (conn != null) {  
	                conn.disconnect();  
	                conn = null;  
	            }  
	        }  
	}
	
	/**
	 * 上传文件到七牛服务器
	 */
	private void uploadFileToQiNiuServer()
	{
		//1、获取token
		getTokenFromServer();
		
		//2、上传到七牛
		uploaToQiniu();
		
		//3、设置信息到本地服务器
		setFileInfoToLocalServer();
	}
	
	/**
	 * 1、获取token
	 */
	private void getTokenFromServer()
	{
		//TODO
	}
	
	/**
	 * 2、上传照片到七牛
	 */
	private void uploaToQiniu()
	{
		//TODO
	}
	
	/**
	 * 3、设置文件信息到本地服务器
	 */
	private void setFileInfoToLocalServer()
	{
		//TODO
	}
	
	/**
	 * 上传成功的处理
	 */
	private void uploadSuccess()
	{
		if(!checkStatusIsUploading())
		{
			LogUtils.e("status is not uploading!");
			return;
		}
		
		//修改状态
		mUploadFileInfo.setUploadStatus(UploadConstant.UploadStatus.UPLOAD_STATUS_SUCCESS);
		
		//回调
		if( mUploadFileInterface == null)
		{
			LogUtils.e("mUploadFileInterface is null>error!");
		}
		else
		{
			mUploadFileInterface.onSuccess(mUploadFileInfo);
		}

		//移除线程池中的任务
		stopThisTaskInService();
	}
	
	/**
	 * 上传失败处理
	 */
	private void uploadFail(String paramErrMsg)
	{
		if(!checkStatusIsUploading())
		{
			LogUtils.e("status is not uploading!");
			return;
		}
		
		//修改状态
		mUploadFileInfo.setUploadStatus(UploadConstant.UploadStatus.UPLOAD_STATUS_FAIL);
		
		//回调
		if( mUploadFileInterface == null)
		{
			LogUtils.e("mUploadFileInterface is null>error!");
		}
		else
		{
			mUploadFileInterface.onError(mUploadFileInfo, paramErrMsg);
		}
		
		//移除线程池中的任务
		stopThisTaskInService();
	}
	
	/**
	 * 上传进度处理
	 */
	private void uploadProgress(double paramProgress)
	{
		if(!checkStatusIsUploading())
		{
			LogUtils.e("status is not uploading!");
			return;
		}
		//修改状态
		mUploadFileInfo.setUploadStatus(UploadConstant.UploadStatus.UPLOAD_STATUS_UPLOADING);
		
		//回调
		if( mUploadFileInterface == null)
		{
			LogUtils.e("mUploadFileInterface is null>error!");
		}
		else
		{
			mUploadFileInfo.setUploadProgress(paramProgress);
			mUploadFileInterface.onProgress(mUploadFileInfo);
		}
	}
	
	/**
	 * 移除线程池中的任务
	 */
	public void stopThisTaskInService()
	{
		if( mService == null)
		{
			LogUtils.e("mService is null>error!");
		}else
		{
			mService.taskCompleted(mUploadFileInfo.getUploadFileId());
		}
	}
	
	/**
	 * 检查是否正在上传 
	 * @return false为不可上传，返回的回调都不用执行
	 */
	private boolean checkStatusIsUploading()
	{
		//UploadStatus
		int status = mUploadFileInfo.getUploadStatus();
		switch(status)
		{
			case UploadStatus.UPLOAD_STATUS_CANCEL://取消
			case UploadStatus.UPLOAD_STATUS_PAUSE://暂停
				return false;
			default:
				return true;
		}
	}
	
	
	public UploadFileInfo getmUploadFileInfo() {
		return mUploadFileInfo;
	}

	public void setmUploadFileInfo(UploadFileInfo mUploadFileInfo) {
		this.mUploadFileInfo = mUploadFileInfo;
	}

	public UploadFileInterface getmUploadFileInterface() {
		return mUploadFileInterface;
	}

	public void setmUploadFileInterface(UploadFileInterface mUploadFileInterface) {
		this.mUploadFileInterface = mUploadFileInterface;
	}

	public UploadService getmService() {
		return mService;
	}

	public void setmService(UploadService mService) {
		this.mService = mService;
	}
}
