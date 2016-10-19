package com.lovebaby.uploadfileprj.network;

import com.alibaba.fastjson.JSONObject;


public interface NetResultCallBack {
	/**
	 * 成功回调函数
	 * @param paramData 数据
	 */
	void onSuccess(JSONObject resultData);
	
/*	*//**
	 * 成功回调函数
	 * @param paramData 数据
	 *//*
	void onSuccess(JSONObject paramData, String paramSuccessMessage);
	*/
	/**
	 * 失败回调函数
	 * @param paramFailCode 错误码
	 * @param paramFailMessage 错误描述信息
	 */
	void onFailed(int paramFailCode, String paramFailMessage);
	
}
