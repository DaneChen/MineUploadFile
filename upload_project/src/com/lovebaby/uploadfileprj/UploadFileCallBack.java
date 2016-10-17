package com.lovebaby.uploadfileprj;

public interface UploadFileCallBack {
	//单张的进度提示
    void onProgress(int fileId, double progress);

    //单张失败
    void onError(int fileId, final String errorMsg);

    //单张成功
    void onSuccess(int fileId);
    
    //一个任务的进度
    void onTaskProgress(int taskId, int totalNum, int successNum);
}
