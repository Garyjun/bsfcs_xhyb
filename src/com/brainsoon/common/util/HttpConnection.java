package com.brainsoon.common.util;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.channelsoft.appframe.common.BaseObject;
import com.channelsoft.appframe.exception.ServiceException;

/**
 * <dl>
 * <dt>HttpConnection</dt>
 * <dd>Description:http接口工具</dd>
 * <dd>Copyright: Copyright (C) 2009</dd>
 * <dd>Company: 青牛（北京）技术有限公司</dd>
 * <dd>CreateDate: Feb 12, 2009</dd>
 * </dl>
 * 
 * @author Fuwenbin
 */
public class HttpConnection extends BaseObject{
	private static Logger logger = Logger.getLogger(HttpConnection.class);
	
	private String connectionUrl;
	private StringBuilder paramBuf = new StringBuilder();
	
	public HttpConnection(){
		super();
	}
	
	public HttpConnection(String connectionUrl){
		setConnectionUrl(connectionUrl);
	}
	
	public void appendParam(String key, String value){
		if (StringUtils.isEmpty(value)) {
			return;
		}
		
		if (paramBuf.length() != 0) {
			paramBuf.append("&");
		}
		
		paramBuf.append(key).append("=").append(value);
	}
	
	public void appendParam(Map<String, String> params){
		if (params == null || params.isEmpty()) {
			return;
		}
		
		for (Map.Entry<String, String> entry : params.entrySet()) {
			if (paramBuf.length() != 0) {
				paramBuf.append("&");
			}
			paramBuf.append(entry.getKey()).append("=").append(entry.getValue());
		}
	}
	
	private String getConnectionUrl() {
		return connectionUrl;
	}

	public void setConnectionUrl(String connectionUrl) {
		this.connectionUrl = connectionUrl;
	}

	/**
	 * 调用接口
	 * @return
	 *
	 * @author Fuwenbin
	 * @date Feb 12, 2009 3:36:37 PM 
	 */
	public String sendRequest() {
		if (StringUtils.isBlank(getConnectionUrl())) {
			this.logger.error("调用失败：连接地址尚未初始化！");
			return "-1";
		}
		
		HttpURLConnection connection = null;
		try {
			URL url = new URL(getConnectionUrl());

			// 以post方式请求
			connection = (HttpURLConnection) url.openConnection();
			connection.setDoOutput(true);
			connection.setRequestMethod("POST");
			if (StringUtils.isNotBlank(paramBuf.toString())) {
				connection.getOutputStream().write(paramBuf.toString().getBytes());
			}
			connection.getOutputStream().flush();
			connection.getOutputStream().close();

			// 获取响应代码
			int code = connection.getResponseCode();
			if (code != 200) {
				throw new ServiceException();
			}

			java.io.InputStream in = connection.getInputStream();
			java.io.BufferedReader breader = new BufferedReader(
					new InputStreamReader(in, "gbk"));

			return breader.readLine();
		} catch (Exception e) {
			String errInfo = "接口调用失败！请确认接口服务是否正常。";
			this.logger.error(errInfo + "["+getConnectionUrl()+"]:" + e.getMessage());
			return "-1";
		} finally {
			if (connection != null){
				connection.disconnect();
			}
		}
	}
	
	//连接超时时间
	private final static int CONNECT_TIMEOUT = 1000;
	//连接重试次数
	private final static int TRY_CONNECT_TIMES = 2;
	
	/**
	 * 连接测试
	 * @param urlStr
	 * @return
	 *
	 * @author Fuwenbin
	 * @date Sep 16, 2009 1:38:43 PM 
	 */
	public static boolean testConnect(String urlStr) {
		return testConnect(urlStr, TRY_CONNECT_TIMES);
	}
	
	/**
	 * 连接测试
	 * @param urlStr
	 * @param reconnectTimes
	 * @return
	 *
	 * @author Fuwenbin
	 * @date Sep 16, 2009 1:38:46 PM 
	 */
	public static boolean testConnect(String urlStr, int reconnectTimes) {
		for (int i = 0; i < reconnectTimes; i++) {
			if (connect(urlStr)) {
				return true;
			}
		}
		
		return false;
	}
	
	/**
	 * 连接测试
	 * @param urlStr	http://localhost:8081/helpdesk/handshake.action?sdId=?
	 * @return
	 *
	 * @author Fuwenbin
	 * @date Sep 16, 2009 11:28:54 AM 
	 */
	private static boolean connect(String urlStr) {
		HttpURLConnection connection = null;
		
		String debugUrl = "";
		if (logger.isDebugEnabled()) {
			debugUrl = StringUtils.substringBeforeLast(urlStr, "/");
		}
		
		try {
			URL url = new URL(urlStr);
			connection = (HttpURLConnection) url.openConnection();
			connection.setDoOutput(true);
			connection.setRequestMethod("GET");
			connection.setConnectTimeout(CONNECT_TIMEOUT);

			int code = connection.getResponseCode();
			if (code == 200) {
				logger.debug("网络连接成功！["+debugUrl+"]");
				return true;
			} 
			logger.debug("网络连接故障！["+debugUrl+"]");
			return false;
		} catch (SocketTimeoutException e) {
			logger.debug("网络连接超时！["+debugUrl+"]");
			return false;
		} catch (Exception e) {
			logger.debug("网络连接失败！["+debugUrl+"]");
//			logger.debug("网络连接失败！["+debugUrl+"]", e);
			return false;
		} finally {
			if (connection != null){
				connection.disconnect();
			}
		}
	}
}






