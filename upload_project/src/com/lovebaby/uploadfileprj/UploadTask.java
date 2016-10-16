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

/**
 * @author chengr
 *
 */
public class UploadTask implements Runnable {

	//上传的文件信息
	private UploadFileInfo mUploadFileInfo;
	//上传结果回调
	private UploadFileInterface mUploadFileInterface;
	
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
	            	//出错
	            	return;
	            }
	            
	            if (mUploadFileInfo.getFilePath() != null) {  
                    String inputName = "multipartFile";  
                    String inputValue = (String)mUploadFileInfo.getFilePath();  
                    File file = new File(inputValue);  
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
                        //TODO进度
                        
                    }  
                    in.close();  
                }  
	  
	            byte[] endData = ("\r\n--" + BOUNDARY + "--\r\n").getBytes();  
	            out.write(endData);  
	            out.flush();  
	            out.close();  
	  
	            //结果
	            int resultCode = conn.getResponseCode();
	            if( resultCode == 200)
	            {
	            	//成功
	            	
	            }else{
	            	//失败
	            	
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
	            //TODO出错
	            
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
		
		//2、上传到七牛
		
	}
	
	/**
	 * 设置文件信息到本地服务器
	 */
	private void setFileInfoToLocalServer()
	{
		
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
}
