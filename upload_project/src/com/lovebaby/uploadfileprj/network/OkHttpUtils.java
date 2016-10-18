package com.lovebaby.uploadfileprj.network;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import com.alibaba.fastjson.JSONObject;

public class OkHttpUtils {
	
	 public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
	 private static final int ERROR_CODE_UNKNOW = 1000;
	 
	 //TODO test
	 private static void callPost()
	 {
	    post("123", new HashMap<String, Object>(), new NetResultCallBack() {

			@Override
			public void onSuccess(String resultData) {
				
			}

			@Override
			public void onFailed(int paramFailCode, String paramFailMessage) {
				
			}
		});
	 }
	 
	/**
	 * 异步 post
	 * @param <T>
	 * @param url 请求地址
	 * @param paramData 数据
	 * @param paramCallBack 回调
	 */
	public static void post(String url, Map<String, Object> paramData, NetResultCallBack paramCallBack)
	{
		Request request = buildPostRequest(url, paramData);
		deliveryResult(request, paramCallBack);
	}
	
	/**
	 * 创建builder
	 * @param url
	 * @param paramData
	 * @return
	 */
	private static Request buildPostRequest(String url, Map<String, Object> paramData)
	{
		RequestBody body = RequestBody.create(JSON, mapToJson(paramData));
	    return new Request.Builder()
                .url(url)
                .post(body)
                .build();
	}
	
	//map转json
	private static String mapToJson(Map<String, Object> paramData)
	{
		JSONObject jsObj = new JSONObject();
		jsObj.putAll(paramData);
		return jsObj.toJSONString();
	}
	
	private static <T> void deliveryResult(Request request, final NetResultCallBack paramCallBack)
    {
		OkHttp.enqueue(request, new Callback(){
			@Override
			public void onFailure(Call arg0, IOException arg1) {
				arg1.printStackTrace();
				if( paramCallBack != null)
				{
					paramCallBack.onFailed(ERROR_CODE_UNKNOW, "网络请求异常");
				}
			}

			@Override
			public void onResponse(Call arg0, Response response) throws IOException {
				  if (response.isSuccessful()) {
					  
					  if( paramCallBack != null){
		            		paramCallBack.onSuccess(response.body().string());
		            	}
	            }else{
	            	if( paramCallBack != null){
	            		paramCallBack.onFailed(response.code(), "网络请求异常");
	            	}
	            }
			}
        });
    }
}
