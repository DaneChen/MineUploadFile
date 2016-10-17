package com.lovebaby.uploadfileprj;

public class UploadConstant {
	
	public static boolean isDebug = true;
	
	//上传线程池中线程数量
	public static int UPLOAD_THREAD_POOL_SIZE = 3;
	
	/**
	 *上传的类型
	 */
	public static class UploadType {
		public static final int POST_BY_LOCAL_SERVER = 0;// 自己的服务器上传
		public static final int POST_BY_QINIU_SERVER = 1;// 七牛服务器上传
	}
	
	/**
	 *上传时的状态
	 */
	public static class UploadStatus{
		public static final int UPLOAD_STATUS_NO_ACTION = 0;//初始状态，无操作
		public static final int UPLOAD_STATUS_UPLOADING = 1;//正在上传
		public static final int UPLOAD_STATUS_SUCCESS = 2;//上传成功
		public static final int UPLOAD_STATUS_CANCEL = 3;//上传取消
		public static final int UPLOAD_STATUS_PAUSE = 4;//上传暂停
		public static final int UPLOAD_STATUS_FAIL = 5;//上传失败
	}
	
	
	//上传出错提示
	public static final String UPLOAD_FAIL_FILE_NOT_EXIST = "上传文件不存在";
	public static final String UPLOAD_FAIL_FILE_DEAL_ERROR = "文件处理出错";
	public static final String UPLOAD_FAIL_NET_ERROR = "网络出错";
	
}
