package com.brainsoon.common.util.dofile.util;

import java.io.File;

import org.apache.commons.lang.StringUtils;

/**
 * 
 * @ClassName: FilePathUtil 
 * @Description: 文件预览处理总配置类
 * @author tanghui 
 * @date 2014-5-8 下午3:31:48 
 *
 */
public class FilePathUtil {

	/**
	 * 
	 * @Title: getConverFileSaveRelPath 
	 * @Description: 文件转换后存在的根目录(绝对路径)
	 * @param   
	 * @return String 
	 * @throws
	 */
	public static String getConverFileSaveRelPath(String filePath){
		// 文件类型
		filePath = filePath.replaceAll("\\\\", "/").replaceAll("//", "/");
		String[] filePaths = filePath.split("/");
		String fileType = DoFileUtils.getExtensionName(filePath);// 获取不带扩展名的文件名
		if(filePaths.length > 1){
			return DoFileUtils.getParentFileDir(filePath)  + "/" + fileType + "/";
		}else{
			return  fileType + "/";
		}
    }
	
	
	
	/**
	 * 
	 * @Title: getFileRelPath 
	 * @Description: 文件存在的根目录(相对路径)-截取根路径
	 * @param   
	 * @return String 
	 * @throws
	 */
	public static String getFileRelPath(String basePath,String filePath){
		if (StringUtils.isNotBlank(filePath)){
			filePath = filePath.replaceAll("\\\\", "/");
			if (StringUtils.isNotBlank(basePath)){
				basePath = basePath.replaceAll("\\\\", "/");
				int num = basePath.length();
				filePath = filePath.substring(num, filePath.length());
			}
		}
		return filePath;
    }

	/**
	 * 
	 * @Title: getFileViewerBaseDir 
	 * @Description: 在线阅读临时根目录(绝对路径)
	 * @param   
	 * @return String 
	 * @throws
	 */
	public static String getFileViewerBaseDir(){
		return WebAppUtils.getWebAppRoot() + getFileViewerRelDir();
    }
	
	
	/**
	 * 
	 * @Title: getFileViewerRelFileDir 
	 * @Description: 在线阅读临时根目录(相对路径)
	 * @param   
	 * @return String 
	 * @throws
	 */
	public static String getFileViewerRelDir(){
		return WebAppUtils.getWebAppRelFileDir() + "viewer" + File.separator;
    }
	
	
	/**
	 * 
	 * @Title: getViewerBasePathByType 
	 * @Description: 预览路径(绝对路径)
	 * @param   
	 * @return String 
	 * @throws
	 */
	public static String getViewerBasePathByType(String fileType,String dirChar){
		//获取pdf阅读的临时目录
		String bookTempPath = getFileViewerBaseDir() + fileType + File.separator +  dirChar;
		File zipTempFile = new File(bookTempPath);
		if (!zipTempFile.exists()) {
			DoFileUtils.mkdir(bookTempPath); //创建目录
		}
		return bookTempPath.replaceAll("\\\\", "/");
	}
	
	/**
	 * 
	 * @Title: getViewerRelPathByType 
	 * @Description: 预览路径(相对路径)
	 * @param   
	 * @return String 
	 * @throws
	 */
	public static String getViewerRelPathByType(String fileType,String dirChar){
		//获取pdf阅读的临时目录
		String bookTempPath = getFileViewerRelDir() + File.separator + fileType + File.separator +  dirChar;
		File zipTempFile = new File(bookTempPath);
		if (!zipTempFile.exists()) {
			DoFileUtils.mkdir(bookTempPath); //创建目录
		}
		return bookTempPath.replaceAll("\\\\", "/");
	}
}
