package com.lovebaby.uploadfileprj.network;

import java.io.IOException;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import com.alibaba.fastjson.JSONObject;
import com.lovebaby.uploadfileprj.SynchNetResponseData;
import com.lovebaby.uploadfileprj.tools.JsonTools;

public class OkHttpUtils {
	
	 public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
	 private static final int ERROR_CODE_UNKNOW = 1000;//未知错误的错误id
	 
	/**
	 * 同步 post
	 * @param <T>
	 * @param url 请求地址
	 * @param paramData 数据
	 * @param paramCallBack 回调
	 */
	public static SynchNetResponseData postSynch(String url, Map<String, String> paramData)
	{
		SynchNetResponseData backData = new SynchNetResponseData();
		Request request = buildPostRequest(url, paramData);
		try {
			Response response = OkHttp.execute(request);
			if (response.isSuccessful()) {
				backData.setSuccess(true);
				backData.setResponseData(JsonTools.StrToJsObj(response.body().toString()));
			} else {
				backData.setSuccess(false);
				backData.setErrorCode(response.code());
				backData.setErrorMsg("网络请求异常");
			}
		} catch (IOException e) {
			e.printStackTrace();
			backData.setSuccess(false);
			backData.setErrorCode(ERROR_CODE_UNKNOW);
			backData.setErrorMsg("网络请求异常");
		}
		return backData;
	}
		
	/**
	 * 异步 post
	 * @param <T>
	 * @param url 请求地址
	 * @param paramData 数据
	 * @param paramCallBack 回调
	 */
	public static void postAsynch(String url, Map<String, String> paramData, NetResultCallBack paramCallBack)
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
	private static Request buildPostRequest(String url, Map<String, String> paramData)
	{
		RequestBody body = RequestBody.create(JSON, mapToJson(paramData));
	    return new Request.Builder()
                .url(url)
                .post(body)
                .build();
	}
	
	//map转json
	private static String mapToJson(Map<String, String> paramData)
	{
		JSONObject jsObj = new JSONObject();
		jsObj.putAll(paramData);
		return jsObj.toJSONString();
	}
	
	/**
	 * 发送异步请求
	 * @param request
	 * @param paramCallBack
	 */
	private static  void deliveryResult(Request request, final NetResultCallBack paramCallBack)
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
						  //TODO 其他判断逻辑
		            		paramCallBack.onSuccess(JsonTools.StrToJsObj(response.body().string()));
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
