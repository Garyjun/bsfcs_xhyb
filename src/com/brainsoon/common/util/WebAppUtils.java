package com.brainsoon.common.util;

import java.io.File;
import java.net.URL;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.SystemUtils;
import org.apache.log4j.Logger;
import org.springframework.web.util.WebUtils;

import com.brainsoon.command.CompLaunch;

/**
 * <dl>
 * <dt>WebUtils</dt>
 * <dd>Description:web应用工具</dd>
 * <dd>Copyright: Copyright (C) 2008</dd>
 * <dd>Company: 青牛（北京）技术有限公司</dd>
 * <dd>CreateDate: Nov 6, 2008</dd>
 * </dl>
 * 
 * @author Fuwenbin
 */
public abstract class WebAppUtils {
	
	private static Logger logger = Logger.getLogger(WebAppUtils.class.getName());
	
	/**
	 * Web app root key parameter at the servlet context level
	 * (i.e. a context-param in <code>web.xml</code>): "webAppRootKey".
	 */
	private static final String WEB_APP_ROOT_KEY = "app.root";
	
	//配置应用根路径
	public static String baseProjectPath = "";
	static{
		baseProjectPath = getProPath();
	}
	
	/**
	 * 获取web应用的根目录
	 * @return
	 *
	 * @author Fuwenbin
	 * @date Nov 6, 2008 11:49:16 AM 
	 */
	public static String getWebAppRoot() {
		String webAppRoot = System.getProperty(WEB_APP_ROOT_KEY);
		return StringUtils.isBlank(webAppRoot) ? System
				.getProperty(WebUtils.DEFAULT_WEB_APP_ROOT_KEY) : webAppRoot;
	}
	
	/**
	 * 获取web应用的WEB-INF目录
	 * @return
	 *
	 * @author Fuwenbin
	 * @date Nov 6, 2008 12:33:21 PM 
	 */
	public static String getWebAppWebInfo() {
		String webAppRoot = getWebAppRoot();
		if (StringUtils.isBlank(webAppRoot)) {
			return null;
		}
		
		return webAppRoot + "WEB-INF" + SystemUtils.FILE_SEPARATOR;
	}
	
	/**
	 * 获取web应用的classes目录
	 * @return
	 *
	 * @author Fuwenbin
	 * @date Nov 6, 2008 12:33:23 PM 
	 */
	public static String getWebAppClassesPath() {
		String webInfo = getWebAppWebInfo();
		if (StringUtils.isBlank(webInfo)) {
			return null;
		}
		
		return webInfo + "classes" + SystemUtils.FILE_SEPARATOR;
	}
	
	/**
	 * 获取web应用的临时目录
	 * @return
	 *
	 * @author Fuwenbin
	 * @date Nov 6, 2008 1:16:30 PM 
	 */
	public static String getTempDir() {
		String webInfo = getWebAppWebInfo();
		if (StringUtils.isBlank(webInfo)) {
			return null;
		}
		
		return webInfo + "tmp" + SystemUtils.FILE_SEPARATOR;
	}
	
	
	/**
	 * 
	 * @Title: getProPath 
	 * @Description: 获取项目根路径
	 * @param   
	 * @return String 
	 * @throws
	 */
	public static String getProPath(){
		if(StringUtils.isBlank(baseProjectPath)){
			URL url =  new CompLaunch().getClass().getProtectionDomain().getCodeSource().getLocation();
			if(url != null){
				String basePath = url.getPath();
				basePath = basePath.replaceAll("\\\\", "/");
				if(StringUtils.isNotBlank(basePath)){
					File file = new File(basePath);
					//如果是目录
					if(file.isDirectory()){
						baseProjectPath = basePath;
					}else if(file.isFile()){ //否则就是执行Jar文件
						baseProjectPath = basePath.substring(0, basePath.lastIndexOf("/")+1);
					}
				}
				
				if(!baseProjectPath.endsWith("/")){
					baseProjectPath += "/";
				  }
				
			}else{
				logger.error("加载工程路径失败！");
			}
			
//				String baseProjectPath1 = ScaConfigUtil.getParameter("PROJECT_ROOT_PATH");
//				System.out.println("---baseProjectPath------" + baseProjectPath1);
//				//class编译路径
//				String classHomePath = PathUtils.getHomePath();
//				//如果 baseProjectPath为空,则默认到class编译的路径下去找文件
//				if(StringUtils.isBlank(baseProjectPath) || !new File(baseProjectPath).exists()){
//					if(!new File(classHomePath).exists()){
//						logger.error("加载配置文件异常! 请确认是否在sca-config文件中已经配置过[PROJECT_ROOT_PATH]的路径！");
//					}else{
//						baseProjectPath = classHomePath;
//					}
//				}
//				//转换
//				baseProjectPath = baseProjectPath.replaceAll("\\\\", "/");
//			
//			 <!-- 
//		    	定义本工程的绝对根路径,在部署SCA应用时需要根据实际做修改
//		    	1) 不用包含工程名
//		    	2) window是包含盘符的根路径,如：D:/project35
//		    	3) linux是相对根路径 ,如：/hone/app
//		    -->
//		    <param name="PROJECT_ROOT_PATH" value="D:\project35\BSRCM_MDM_SCA\bin\classes"/>
			
			logger.debug("应用部署的根路径:" + baseProjectPath);
		}
		return baseProjectPath;
	}

	public WebAppUtils() {
		super();
	}
	
	
	
}
