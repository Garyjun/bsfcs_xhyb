package com.brainsoon.common.util.dofile.conver;
import java.io.File;
import java.io.IOException;
import java.util.Date;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;

import com.brainsoon.common.support.GlobalAppCacheMap;
import com.brainsoon.common.util.dofile.util.DoFileException;
import com.brainsoon.common.util.dofile.util.DoFileUtils;
import com.brainsoon.common.util.dofile.util.PinyingUtil;


/**
 * 
 * @ClassName: OfficeToPdfUtils
 * @Description:office系列文档转pdf工具类
 * @author tanghui
 * @date 2014-11-18 上午10:29:15
 * 
 */
public class OfficeToPdfUtils {
	protected static final Logger logger = Logger.getLogger(OfficeToPdfUtils.class);
	
	/**
	 * 
	 * @Title: convertToPdf 
	 * @Description: office 转 pdf
	 * @param officePath
	 *            office文件绝对路径
	 * @param targetPdfPath
	 *            转换后的pdf绝对路径
	 * @return void 
	 * @throws
	 */
	public static String convertToPdf(String officePath,String targetPdfPath){
		File inputFile = null;
		boolean	cb  = true;
		try {
			logger.info("office系列文档转pdf开始...");  
			inputFile = new File(officePath);   
			logger.info("office文档输入路径：" + officePath);
			File outputFile = new File(targetPdfPath);
			//如果pdf不存在，则准换，否则不转换
			if(!outputFile.exists()){
				if(!new File(outputFile.getParent()+"/").exists()){
					DoFileUtils.mkdir(outputFile.getParent()+ "/");
				}
				logger.info("office文档转换后输出路径：" + outputFile);
				//重试三次
				cb  = convertThread(inputFile, outputFile, 0);
				logger.info("Office000000000：" + cb);
				if(!cb){
					cb  = convertThread(inputFile, outputFile, 1);
					logger.info("Office111111111111：" + cb);
					if(!cb){
						cb  = convertThread(inputFile, outputFile, 2);
						logger.info("Office2222222222222：" + cb);
					}
				}
			}else{
				logger.info("恭喜。文件{" + outputFile.getName() +"}已存在，无需转换");
			}
		}catch (Exception e) {
			e.printStackTrace();
			logger.info("Office4444444444444");
			throw new DoFileException("office系列文档转换失败。");
		}finally{
		   if(!cb && !new File(targetPdfPath).exists()){
			   logger.info("Office5555555555555555");
			   throw new DoFileException("office系列文档转换失败。");
			}
		}
		return targetPdfPath;
	}
	
	/**
	 * 
	 * @Title: convertThread 
	 * @Description: office 转 pdf（仅支持单线程）
	 * @param officePath
	 *            office文件绝对路径
	 * @param targetPdfPath
	 *            转换后的pdf绝对路径
	 * @return void 
	 * @throws
	 */
	public static boolean convertThread(File inputFile,File outputFile,int i){
		long begin_time = new Date().getTime();
		boolean b = true;
		boolean cs = false;
		Thread tc = null;
		Office2PdfThread o2pt = null;
		try {
			int waitTime = 0;
			int waitMaxTime = 120000; //最大等待时间问：120秒=2分钟
			long size = inputFile.length();
			if(5192000l > size && size  >= 2048000l){
				waitMaxTime = 240000; //240秒=4分钟
			}else if(10240000l > size  && size >= 5192000l){
				waitMaxTime = 480000; //480秒=8分钟
			}else if(size >= 102400000l){
				waitMaxTime = 720000; //720秒=12分钟
			}
			GlobalAppCacheMap.removeKey("converstatus");
			logger.info("Office开始转换文档，第【" + (i+1) +"】尝试...");
			while(b){
				Object converstatus  = GlobalAppCacheMap.getValue("converstatus");
				if(waitTime < waitMaxTime){
					if(waitTime == 0){
						o2pt = new Office2PdfThread(inputFile,outputFile,i);
						tc = new Thread(o2pt);
						tc.start();
						Thread.sleep(1000);
						waitTime += 1000;
						logger.info("Office1");
					}else if(waitTime < waitMaxTime){
						if(converstatus != null){
							if(converstatus.equals("ok")){
								cs = true;
							}else{
								cs = false;
							}
							b = false;
							logger.info("Office2");
							break;
						}
						if(outputFile.exists()){
							cs = true;
							b = false;
							logger.info("Office3");
							break;
						}
						Thread.sleep(1000);
						waitTime += 1000;
						logger.info("等待了:【" + waitTime/1000  + "】s");
					}
				}else{ //waitTime >= waitMaxTime
					if(converstatus != null){
						if(converstatus.equals("ok")){
							cs = true;
							logger.info("Office5");
						}else{
							cs = false;
							logger.info("Office6");
						}
					}
					if(outputFile.exists()){
						cs = true;
						logger.info("Office7");
					}
					b = false;
					logger.info("Office8");
					break;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.info("Office9");
			//最后来一遍校验
			if(outputFile.exists()){
				cs = true;
			}else{
				cs = false;
				throw new DoFileException(e.getMessage());
			}
		}finally{
			logger.info("Office10");
			GlobalAppCacheMap.removeKey("converstatus");
			long end_time = new Date().getTime();
			//最后来一遍校验
			if(outputFile.exists()){
				cs = true;
			}else{
				cs = false;
				//结束掉soffice进程，防止出现死进程
				String[] cmds = {"soffice.exe","soffice.bin"};
				for (int j = 0; j < cmds.length; j++) {
					DoFileUtils.exeWinKillCmd(cmds[j]);
				}
			}
			if(cs){
				logger.info("恭喜，转换成功。第【" + (i+1) +"】次Office转换文档进程结束，文件{" + inputFile.getName() +"}，耗时：[" + (end_time - begin_time) + "]ms");
			}else{
				if(i != 2){
					logger.info("SORRY，转换失败。第【" + (i+1) +"】次Office转换文档进程结束，文件{" + inputFile.getName() +"}，耗时：[" + (end_time - begin_time) + "]ms，还有" + (2 - i) +"次重试机会。");
				}else{
					logger.info("SORRY，转换失败。第【" + (i+1) +"】次Office转换文档进程结束，文件{" + inputFile.getName() +"}，耗时：[" + (end_time - begin_time) + "]ms，没有重试机会了。");
				}
			}
		}
		return cs;
	}


	/**
	 * 
	 * @Title: cleanDirectory 
	 * @Description: 根据文件路径清空其路径所在的文件夹中的所有文件
	 * @param   
	 * @return void 
	 * @throws
	 */
	public static void cleanDirectory(String url) throws IOException{
		File file = new File(url);
		if(!file.isDirectory()){
			url = url.replaceAll("\\\\", "\\/");
			file = new File(url.substring(0, url.lastIndexOf("/")));
		}
		if(file.exists()&&file.isDirectory())
			FileUtils.cleanDirectory(file);
	}
	
	/**
	 * @throws Exception 
	 * 
	 * @Title: convertOfficeToPdf
	 * @Description: 转换office到pdf
	 * @param officePath office绝对路径
	 * @return String
	 * @throws
	 */
	public static String convertOfficeToPdf(String officePath){
		String outPdfTempFile =  "";
		try {
			//获取不带扩展名的文件名
		    String fileName = DoFileUtils.getFileNameNoEx(officePath); 
		    //文件类型
		    String fileType = DoFileUtils.getExtensionName(officePath); 
		    outPdfTempFile = DoFileUtils.getFileConverTempDir();
		    //判断是否是office系列的，如果是，则需要转换成pdf
		    if("doc".contains(fileType.toLowerCase()) || "docx".contains(fileType.toLowerCase())){
		    	outPdfTempFile += "doc2pdf/";
		    }else if("ppt".contains(fileType.toLowerCase()) || "pptx".contains(fileType.toLowerCase())){
		    	outPdfTempFile += "ppt2pdf/";
		    }else if("xls".contains(fileType.toLowerCase()) || "xlsx".contains(fileType.toLowerCase())){
		    	outPdfTempFile += "xls2pdf/";
		    }else{
		    	outPdfTempFile += "other2pdf/";
		    }
		    outPdfTempFile += PinyingUtil.spellNoneTone(fileName) + ".pdf";
			//转换doc为pdf,将其做为临时文件放置到指定位置
			convertToPdf(officePath,outPdfTempFile);
		} catch (Exception e) {
			e.printStackTrace();
		}
		//返回转后的pdf临时位置路径
		return outPdfTempFile;
	}
	
	

	
	
	public static void main(String[] args) {
		String officePath = "D:/12/测试文档/如何进行SWOT分析(带案例).doc";
		String targetPdfPath = "D:/12/测试文档-change/资源上传查重验证处理办法111322.pdf";
		convertToPdf(officePath, targetPdfPath);
	}
}
