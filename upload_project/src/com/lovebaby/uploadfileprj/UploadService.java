package com.lovebaby.uploadfileprj;

import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

/**
 * 使用http post方式，在后台上传文件的service
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
        Tools.openFixedThread(getTaskFromMap(taskId));
        return START_STICKY;
    }
	
    @Override
    public void onDestroy() {
        super.onDestroy();
        stopAllUploads();
        uploadTasksMap.clear();
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
    protected synchronized void taskCompleted(String uploadId) {
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

        Iterator<Integer> iterator = uploadTasksMap.keySet().iterator();
        while (iterator.hasNext()) {
            UploadTask taskToCancel = uploadTasksMap.get(iterator.next());
            taskToCancel.cancel();
        }
    }

    private int shutdownIfThereArentAnyActiveTasks() {
        if (uploadTasksMap.isEmpty()) {
            stopSelf();
            return START_NOT_STICKY;
        }
        return START_STICKY;
    }
    
    private UploadTask getTaskFromMap(int taskId)
    {
    	if( uploadTasksMap == null)
    	{
    		return null;
    	}
    	return uploadTasksMap.get(taskId);
    }
    
}