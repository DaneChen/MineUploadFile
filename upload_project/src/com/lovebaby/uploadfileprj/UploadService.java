package com.lovebaby.uploadfileprj;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

/**
 * 使用http post方式，在后台上传文件的service
 * 所以提交下来的任务都是以文件为单位
 * @author chengr
 *
 */
public class UploadService extends Service {
	
	protected static final String PARAM_TASK_FILE_ID = "task_file_Id";
	
	//上传中的任务队列 (key 为fileId)
	private static final Map<Integer, UploadTask> uploadTasksMap = new ConcurrentHashMap<Integer, UploadTask>();

	@Override
	public void onCreate(){ 
		super.onCreate();
	}
	
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
	
   @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent == null ) {
            return shutdownIfThereArentAnyActiveTasks();
        }
        
        int taskId = intent.getIntExtra(PARAM_TASK_FILE_ID, 0);
        if( taskId == 0)
        {
        	LogUtils.e("taskId is 0 >error!");
        	return START_STICKY;
        }
        UploadTask task =  getTaskFromMap(taskId);
        if( task != null)
        {
        	task.setmService(this);
            Tools.openFixedThread(task);
        }
        return START_STICKY;
    }
	
    @Override
    public void onDestroy() {
        super.onDestroy();
        stopAllUploads();
        uploadTasksMap.clear();
        Tools.shutDowThreadPool();
    }
	
    public static synchronized boolean addTaskToService(int fileId, UploadTask paramTask)
    {
    	if( paramTask == null)
    	{
    		return false;
    	}
    	if( uploadTasksMap.get(fileId) == null)
    	{
    		uploadTasksMap.put(fileId, paramTask);
    		return true;
    	}else{
    		//已经存在的任务id
    		LogUtils.e("task is exist already!");
    		return false;
    	}
    }
    
    /**
     * 结束了某个任务
     * @param uploadId
     */
    public synchronized  void taskCompleted(int uploadId) {
        UploadTask task = uploadTasksMap.remove(uploadId);
        // 当所有任务都结束了，停止server本身
        if (uploadTasksMap.isEmpty()) {
            stopSelf();
        }
    }
    
    /**
     *停止所有活动的任务
     */
    public static synchronized void stopAllUploads() {
        if (uploadTasksMap.isEmpty()) {
            return;
        }

        Integer[] keySet = uploadTasksMap.keySet().toArray(new Integer[0]);
        for( Integer key : keySet)
        {
        	 UploadTask taskToCancel = uploadTasksMap.get(key);
        	 if( taskToCancel != null)
        	 {
        		 taskToCancel.cancel();
        	 }else
        	 {
        		 LogUtils.e("taskToCancel is null>error!");
        	 }
        }
    }

    private int shutdownIfThereArentAnyActiveTasks() {
        if (uploadTasksMap.isEmpty()) {
            stopSelf();
            return START_NOT_STICKY;
        }
        return START_STICKY;
    }
    
    public static UploadTask getTaskFromMap(int paramFileId)
    {
    	if( uploadTasksMap == null)
    	{
    		return null;
    	}
    	return uploadTasksMap.get(paramFileId);
    }
    
}
