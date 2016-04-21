package com.brainsoon.common.util;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;


/**
 * 获取request请求中的所有参数
 * @author zuohongliang
 *
 */
public class ParametersKeyValue {
	//日志类
	@SuppressWarnings("unused")
	private static Logger logger = Logger.getLogger(ParametersKeyValue.class);
	/**
	 * 获取request中的参数并封装为HashMap
	 * @param request
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public static Map getKeyValues(HttpServletRequest request){
		HashMap<String, String> mp = new HashMap<String, String>();
		Iterator iter = request.getParameterMap().entrySet().iterator(); 
		while(iter.hasNext()){
			 Map.Entry entry = (Map.Entry)iter.next();
			 String key = entry.getKey().toString();
			 Object obj = entry.getValue();
			 String val = "";
			 if (obj instanceof String[]){
			    String[] strs = (String[])obj;
			    val = Arrays.toString(strs);//jdk1.5以上才支持，1.4的话就自己循环
			    if(!StringUtils.isEmpty(val)){
			    	val = StringUtils.substring(val, 1, val.length()-1);
			    }
			 }else{
			    val = obj.toString();
			 }
			 mp.put(key, val);
			 //logger.info("key=="+key+"==value=="+val);
		}
		return mp;
	}
}

