package com.brainsoon.common.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.commons.httpclient.DefaultHttpMethodRetryHandler;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.brainsoon.common.util.dofile.util.DoFileException;
/**
 * http请求工具类
 * @author zuo
 *
 */
public class HttpClientUtil {
	private static Logger logger = Logger.getLogger(HttpClientUtil.class);
	private static String encoding = "UTF-8";
	private String connectionUrl;
	private String [] params = null;
	
	public HttpClientUtil() {
	}
	/**
	 * 初始化
	 * @param connectionUrl 发送请求的链接带参数
	 * @param encoding 请求编码默认UTF-8
	 */
	public HttpClientUtil(String connectionUrl,String encoding) {
		this.connectionUrl = connectionUrl;
		HttpClientUtil.encoding = encoding;
		initParams();
	}
	
	
	public void initParams(){
		if(!StringUtils.isBlank(connectionUrl)) {
			String paramStr = StringUtils.substring(connectionUrl, StringUtils.indexOf(connectionUrl, "?")+1);
			this.connectionUrl = StringUtils.substring(connectionUrl,0,StringUtils.indexOf(connectionUrl, "?"));
			params = paramStr.split("&");
		}
	}
	/**
	 * 发送请求,可以指定url
	 * @param url
	 * @return
	 */
	public String sendRequest(String url){
		if (StringUtils.isBlank(connectionUrl)) {
			logger.error("调用失败：连接地址尚未初始化！");
			return "-1";
		}
		HttpClient client = new HttpClient(); 
		//设置超时时间 
		client.getHttpConnectionManager().getParams().setSoTimeout(180*1000); 
		//使用post方式，参数长度不受限制 
		PostMethod postMethod = new PostMethod(url);
		postMethod.getParams().setParameter(HttpMethodParams.HTTP_CONTENT_CHARSET, encoding);
		//设置参数 
		NameValuePair[] nameValue = null;
		if(null != params){
			nameValue = new NameValuePair[params.length];
			for (int i = 0; i < params.length; i++) {
				String paramsDes = params[i];
				String [] pairs = StringUtils.split(paramsDes, "=", 2);
				String name = pairs[0];
				String value = "";
				if(pairs.length >= 2){
					value = pairs[1];
				}
				nameValue[i] = new NameValuePair(name, value);
			}
		}
		postMethod.setRequestBody(nameValue); 
		try {
			// 设置成了默认的恢复策略，在发生异常时候将自动重试3次，在这里你也可以设置成自定义的恢复策略 
			postMethod.getParams().setParameter(HttpMethodParams.RETRY_HANDLER,new  DefaultHttpMethodRetryHandler());
			//发送请求 
			int statusCode  = client.executeMethod(postMethod);
			if(statusCode  !=  HttpStatus.SC_OK) {
		          System.err.println( " Method failed:  "   +  postMethod.getStatusLine());
		    }else{
		    	java.io.InputStream in = postMethod.getResponseBodyAsStream();
				java.io.BufferedReader breader = new BufferedReader(
						new InputStreamReader(in, encoding));
				return breader.readLine();
		    }
		} catch (Exception e) {
			String errInfo = "接口调用失败！请确认接口服务是否正常。";
			logger.error(errInfo + "["+connectionUrl+"]:" + e.getMessage());
			return "-1";
		} 
		return "-1";
	}
	
	public static String executeGet(String url){
		//替换参数中的|
		url = StringUtils.replace(url, "|", "%7C");
		// 1 定义HttpClient
		HttpClient client = new HttpClient();
		/* 2 生成 GetMethod 对象并设置参数 */
		GetMethod getMethod = new GetMethod(url);
		// 设置 get 请求超时为 5 秒
		getMethod.getParams().setParameter(HttpMethodParams.SO_TIMEOUT, 10000);
		// 设置请求重试处理，用的是默认的重试处理：请求三次
		getMethod.getParams().setParameter(HttpMethodParams.RETRY_HANDLER,
				new DefaultHttpMethodRetryHandler());
		getMethod.getParams().setParameter(HttpMethodParams.HTTP_CONTENT_CHARSET, encoding);
		String response = "";
		/* 3 执行 HTTP GET 请求 */
		try {
			int statusCode = client.executeMethod(getMethod);
			/* 4 判断访问的状态码 */
			if (statusCode != HttpStatus.SC_OK) {
				System.err.println("Method failed: " + getMethod.getStatusLine());
			}

			/* 5 处理 HTTP 响应内容 */
			// HTTP响应头部信息，这里简单打印
//			Header[] headers = getMethod.getResponseHeaders();
//			for (Header h : headers){
//				System.out.println(h.getName() + "------------ " + h.getValue());
//			}

			logger.info("HTTP GET 请求的URL: " + url);
			
			// 读取 HTTP 响应内容，这里简单打印网页内容
			byte[] responseBody = getMethod.getResponseBody(); // 读取为字节数组
			response = new String(responseBody, encoding);

			// 读取为 InputStream，在网页内容数据量大时候推荐使用
			// InputStream response = getMethod.getResponseBodyAsStream();

		} catch (HttpException e) {
			// 发生致命的异常，可能是协议不对或者返回的内容有问题
			e.printStackTrace();
		} catch (IOException e) {
			// 发生网络异常
			e.printStackTrace();
		} finally {
			/* 6 .释放连接 */
			getMethod.releaseConnection();
		}
		
		return response;
	}
	
	@SuppressWarnings("deprecation")
	public static String postJson(String url,String json){
		if (StringUtils.isBlank(url)) {
			logger.error("调用失败：连接地址尚未初始化！");
			throw new DoFileException("调用失败：连接地址尚未初始化！");
		}
		HttpClient client = new HttpClient(); 
		//设置超时时间 
		client.getHttpConnectionManager().getParams().setSoTimeout(180*1000); 
		
		//使用post方式，参数长度不受限制 
		PostMethod postMethod = new PostMethod(url);
		postMethod.getParams().setParameter(HttpMethodParams.HTTP_CONTENT_CHARSET, encoding);
		// 设置成了默认的恢复策略，在发生异常时候将自动重试3次，在这里你也可以设置成自定义的恢复策略 
		postMethod.getParams().setParameter(HttpMethodParams.RETRY_HANDLER,new  DefaultHttpMethodRetryHandler());
		
		postMethod.setRequestHeader("Content-Type", "application/json;charset=UTF-8");
		
		postMethod.setRequestBody(json);
		try {
			//发送请求 
			client.executeMethod(postMethod);
			java.io.InputStream in = postMethod.getResponseBodyAsStream();
			java.io.BufferedReader breader = new BufferedReader(new InputStreamReader(in, encoding));
			return breader.readLine();
		} catch (Exception e) {
			String errInfo = "接口调用失败！请确认接口服务是否正常。";
			logger.error(errInfo + "["+url+"]:" + e.getMessage());
			throw new DoFileException(errInfo);
		} 
	}
	/**
	 * 发送请求，失败返回-1
	 * @return
	 */
	public String sendRequest() {
		return sendRequest(connectionUrl);
	}
	
	public static void main(String[] args) {
		HttpClientUtil http = new HttpClientUtil();
		//System.out.println(http.executeGet("http://10.130.29.14:8080/semantic_index_server/ontologyListQuery/domainNode?codes=P%7CV01&domainType=0"));
		//String url = "http://10.130.29.14:8080/semantic_index_server/createOntology/asset";
		//String body = "{\"@type\":\"asset\",\"commonMetaDatas\":{\"entry\":[{\"key\":\"module\",\"value\":\"1\"},{\"key\":\"keywords\",\"value\":\"小学,语文\"},{\"key\":\"format\",\"value\":\"F06\"},{\"key\":\"educational_phase\",\"value\":\"P\"},{\"key\":\"libType\",\"value\":\"0\"},{\"key\":\"subject\",\"value\":\"S01\"},{\"key\":\"type\",\"value\":\"T01\"},{\"key\":\"version\",\"value\":\"V01\"},{\"key\":\"unit\",\"value\":\"http://www.brainsoon.com/resource#domain-ea2dbdea-992b-4d42-b892-3e7cad40e9b3\"},{\"key\":\"title\",\"value\":\"课程图片\"},{\"key\":\"description\",\"value\":\"小学课\"},{\"key\":\"grade\",\"value\":\"G11\"},{\"key\":\"modified_time\",\"value\":\"2014-04-25 09:54:00:638\"},{\"key\":\"fascicule\",\"value\":\"B1\"}]},\"thumbPath\":\"http://img.dajianet.net/thumb/thumb.jpg\",\"extendMetaDatas\":{\"entry\":[{\"key\":\"resolution\",\"value\":\"1000 x 900\"}]} ,\"file\":{\"md5\":\"Md5\",\"path\":\"chapter/chapter01.xml\"}}";
		//System.out.println(http.postJson(url, body));
		
		String get = "http://10.130.29.27:8080/semantic_index_server/ontologyListQuery/customMetaData?name=YD";
		System.out.println(http.executeGet(get));
	}
}
