package com.lovebaby.uploadfileprj.tools;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

/**
 * @author chengr 封装fastJson
 *
 */
public class JsonTools {

	public static JSONObject StrToJsObj(String jsonStr)
	{
		return JSON.parseObject(jsonStr);
	}
	
}
