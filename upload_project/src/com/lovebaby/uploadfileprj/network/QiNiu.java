package com.lovebaby.uploadfileprj.network;

import java.io.File;
import java.io.IOException;

import com.qiniu.android.storage.Configuration;
import com.qiniu.android.storage.KeyGenerator;
import com.qiniu.android.storage.Recorder;
import com.qiniu.android.storage.UpCompletionHandler;
import com.qiniu.android.storage.UploadManager;
import com.qiniu.android.storage.UploadOptions;
import com.qiniu.android.storage.persistent.FileRecorder;

public class QiNiu {
	
	private static UploadManager mUploadManagerSave;
	
	/**
	 * 上传文件到qiniu服务器
	 * @param paramHandler:回调handler
	 * @param paramFilePath:文件路径 ,与paramArrData 选其中之一填写
	 * @param paramKey：文件key
	 * @param paramToken：上传token
	 * @param savePath 保存记录的路径
	 * @param paramOption 配置 为了实现进度通知
	 * @param paramArrData 文件字节数据, 与paramFilePath选其中之一填写
	 * @throws IOException 
	 */
	public static void uploadFile(UpCompletionHandler paramHandler, String paramFilePath, String paramKey, 
			String paramToken, String savePath, UploadOptions paramOption, byte[] paramArrData) throws IOException
	{
		Recorder recorder = new FileRecorder(savePath);
		//默认使用 key 的url_safe_base64编码字符串作为断点记录文件的文件名。
		//避免记录文件冲突（特别是key指定为null时），也可自定义文件名(下方为默认实现)：
		KeyGenerator keyGen = new KeyGenerator(){
			@Override
		    public String gen(String key, File file){
		        // 不必使用url_safe_base64转换，uploadManager内部会处理
		        // 该返回值可替换为基于key、文件内容、上下文的其它信息生成的文件名
		        return key + "_._" + new StringBuffer(file.getAbsolutePath()).reverse();
		    }

		};
		
		Configuration config = new Configuration.Builder()
        // recorder 分片上传时，已上传片记录器
        // keyGen 分片上传时，生成标识符，用于片记录器区分是那个文件的上传记录
        .recorder(recorder, keyGen)  
        .build();
		
		if( mUploadManagerSave == null)
		{
			mUploadManagerSave = new UploadManager(config);
		}
		if( paramArrData == null)
		{
			mUploadManagerSave.put(paramFilePath, paramKey, paramToken, paramHandler, paramOption);
		}
		else
		{
			mUploadManagerSave.put(paramArrData, paramKey, paramToken, paramHandler, paramOption);
		}
	}
	

}
