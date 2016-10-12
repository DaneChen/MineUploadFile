package com.lovebaby.uploadfileprj;

/**
 *内部的结果反馈接口
 * @author chengr
 *
 */
public interface UploadFileInterface {

	//单张的进度提示
    void onProgress(final UploadFileInfo uploadInfo);

    //单张失败
    void onError(final UploadFileInfo uploadInfo, final String errorMsg);

    void onCompleted(final UploadFileInfo uploadInfo);
    
    //单张成功
    void onSuccess(final UploadFileInfo uploadInfo);
    
    //取消
    void onCancelled(final UploadFileInfo uploadInfo);
}
