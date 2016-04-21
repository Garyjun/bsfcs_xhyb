package com.brainsoon.common.util.dofile.util;

import java.util.Arrays;
import java.util.List;

/**
 * 
 * @ClassName: ConstantsDef 
 * @Description:文件操作相关的常量配置类  
 * @author tanghui 
 * @date 2014-7-14 下午12:32:18 
 *
 */
public class ConstantsDef {
	//webapp.properties
	public static final String ffmpegPath = "ffmpegPath";
	public static final String mencoderPath = "mencoderPath";
	public static final String pictureFormat = "pictureFormat";
	public static final String imgConverPath = "imgConverPath";
	public static final String videoFormat = "videoFormat";
	public static final String audioFormat = "audioFormat";
	public static final String animaFormat = "animaFormat";
	public static final String shellPath = "shellPath";
	public static final String mp4BoxPath = "mp4BoxPath";
	public static final String documentFormat = "documentFormat";
	public static final String converTheadNumber = "converTheadNumber";
	public static final String fileConverTempDir = "fileConverTempDir";
	public static final String fileDir = "fileDir";
	public static final String converFileRoot = "converFileRoot";
	public static final String fileRoot = "fileRoot";
	public static final String fileTemp = "fileTemp";
	public static final String sysUpLoadFile = "sysUpLoadFile";
	public static final String viewer = "viewer";
	public static final String prodFile = "prodFile";//产品发布根目录
	public static final String openOfficePath = "openOfficePath";//openoffice path
	public static final String openOfficePort = "openOfficePort";//openoffice port
	
	public static final String server = "SERVER";//主从机配置
	public static final String hostAddress = "HOSTADDRESS";//主机地址


	public static boolean isOfficeFile(String name){
		List<String> list = Arrays.asList("doc","docx","ppt","pptx","pptm","xls","xlsx");
		if(list.contains(name.toLowerCase()))
			return true;
		else
			return false;
	}
	
	public static boolean isTxtFile(String name){
		List<String> list = Arrays.asList("txt","xml","ncx","css","js","php",
				"java","properties","dtd","log","tld","jsp","sql","c","ini",
				"bat","py","cgi","asp","rtf","mht");
		if(list.contains(name.toLowerCase()))
			return true;
		else
			return false;		
	}
}
