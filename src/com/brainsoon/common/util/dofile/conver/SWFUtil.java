package com.brainsoon.common.util.dofile.conver;

import java.io.File;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.brainsoon.common.util.dofile.util.DoFileUtils;
import com.brainsoon.common.util.dofile.util.PropertiesReader;


/**
 * 
 * @ClassName: SWFUtil 
 * @Description: SWF文件提取工具类 
 * @author tanghui 
 * @date 2014-11-10 下午3:04:16 
 *
 */
public class SWFUtil {
	private static Log log = LogFactory.getLog(PdfToSwfUtil.class);
	private static String swfextractPath;
	    
    static{
    	swfextractPath = PropertiesReader.getInstance().getProperty("swfextractPath");
    }
	
	/**
	 * 
	 * @Title: swfextract2Image 
	 * @Description: SWF文件截图
	 * @param  swfFilePath  SWF文件绝对路径
	 * @param  imagePath 截取的图片文件绝对路径
	 * @return void 
	 * @throws
	 */
	public static boolean swfextract2Image(String swfFilePath,String imagePath){
		try {
    	    if(StringUtils.isBlank(swfextractPath)) {  
               log.warn("未找到swfextract工具的路径");  
               return false;  
            }  
    	    String cmd = swfextractPath;
    	    cmd += " -j";
    	    cmd += " \"" + swfFilePath + "\"";
//    	    cmd += " -j \"" + imagePath + "\"";
    	    //执行转换
			DoFileUtils.exeShell(cmd.toString());
			File img = new File(imagePath);  
			if (!img.exists()) {  
			    return false;  
			}  
			log.info("截取SWF缩略图成功!!!"); 
		  } catch (Exception e) {
	    	   e.printStackTrace();
	      }
		return true;
	}
	
	
	//test
	public static void main(String[] args) {
		String swfFilePath="d:/《燕子专列》动画-读一读-欧 - 副本.swf";
		String imagePath ="d:/1-1-2.jpg";
		swfextract2Image(swfFilePath, imagePath);
	}
}
