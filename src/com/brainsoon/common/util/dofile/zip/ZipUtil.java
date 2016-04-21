package com.brainsoon.common.util.dofile.zip;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Date;
import java.util.Enumeration;
import java.util.zip.ZipException;
import org.apache.commons.compress.archivers.ArchiveException;
import org.apache.commons.compress.archivers.ArchiveOutputStream;
import org.apache.commons.compress.archivers.ArchiveStreamFactory;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream;
import org.apache.commons.compress.archivers.zip.ZipFile;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.output.ByteArrayOutputStream;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import com.brainsoon.common.exception.ServiceException;
import com.brainsoon.common.util.date.DateUtil;

import de.idyl.winzipaes.AesZipFileEncrypter;


/**
 * 
 * @ClassName: ZipDoUtil 
 * @Description: ZIP压缩解压处理工具类
 * 主要方法：
 * 1.解压：unZipToFolder(String zipFileName,String outputDir);
 * 2.打包：zipFileOrFolder(String sourceFileOrFolder,String outputZipfilepath,String[] suffixArr);
 * 3.多目录打包：zipFileOrFolder(String[] sourceFilesOrFolders,String outputZipfilepath,String[] suffixArr);
 * 4.多目录打包,并重命名：zipFileOrFolder(String[][] sourceFilesOrFolders,String outputZipfilepath,String[] suffixArr);
 * 5.加密：encryptZipFile(File srcFile, String outputFile,String password);
 * 6.校验ZIP：checkZipFileIsExist(String inputFilePath);
 * 7.创建目录：createDir(String inputFilePath);
 * @author tanghui 
 * @date 2013-8-14 下午4:05:12 
 *
 */
public class ZipUtil {
	private static Log logger = LogFactory.getLog(ZipUtil.class);
    
    /**
     * 
     * @Title: unZipToFolder 
     * @Description: 把zip文件解压到一个指定的目录中
     * @param zipFileName   zip文件绝对地址
     * @param outputDir     解压后目录绝对地址  
     * @return void 
     * @throws
     */
    @SuppressWarnings("rawtypes")
	public static void unZipToFolder(String zipFileName, String outputDir) throws Exception {
    	//测试开始
    	long ss = getStartTime("unZipToFolder");
    	ZipFile zf = null;
    	try {
    		zipFileName = zipFileName.replaceAll("\\\\", "/");
            File zipfile = new File(zipFileName);
            if (zipfile.exists()){
            	if(zipFileName.lastIndexOf("/") == -1){
            		outputDir += "/";
            	}
                FileUtils.forceMkdir(new File(outputDir));
                zf = new ZipFile(zipfile, "GBK");
                Enumeration zipArchiveEntrys = zf.getEntries();
                while (zipArchiveEntrys.hasMoreElements()) {
                    ZipArchiveEntry zipArchiveEntry = (ZipArchiveEntry) zipArchiveEntrys.nextElement();
                    if (zipArchiveEntry.isDirectory()) {
                        FileUtils.forceMkdir(new File(outputDir + File.separator + zipArchiveEntry.getName() + File.separator));
                    } else {
                        IOUtils.copy(zf.getInputStream(zipArchiveEntry), FileUtils.openOutputStream(new File(outputDir  + File.separator+ zipArchiveEntry.getName())));
                    }	
                }
            } else {
                throw new IOException("指定的解压文件不存在：\t" + zipFileName);
            }
		}catch (IOException e) {
    		throw new ServiceException("解压失败：找不到文件！"+ zipFileName);
		}catch (Exception e) {
    		throw new ServiceException("解压失败:内部解压处理错误！"+e.getStackTrace()[0].toString());
		}finally {
			if(zf != null){
				zf.close();
			}
			//测试结束
	    	getTotaltime("unZipToFolder",ss);
		}
    }

   
    
    /**
     * 
     * @Title: zipFileOrFolder 
     * @Description: 打包目录为ZIP（不包含压缩算法）
     * @param   sourceFileOrFolder 要打包的文件或目录绝对地址（是目录->会遍历目录下的所有文件）  
     * 如："d:/dir/" or "d:/dir/1.jpg"
     * @param   outputZipfilepath 打包后的ZIP文件绝对地址   
     * 如："d:/dir/1.zip"
     * @param   suffixArr 打包要过滤的后缀名,
     * 如：  new String[]{"jpg","pdf"} -> 代表只打包jpg/pdf格式的文件;打包全部文件为:null 
     * @return void 
     * @throws
     */
	public static void zipFileOrFolder(String sourceFileOrFolder,
			String outputZipfilepath,String[] suffixArr) throws Exception{
		//测试开始
    	long ss = getStartTime("zipFilesOrFolders");
    	File f = new File(outputZipfilepath);
        //创建目录
    	mkdir(outputZipfilepath);
    	f.createNewFile();
    	OutputStream out = new FileOutputStream(f);
    	BufferedOutputStream bos = new BufferedOutputStream(out);
    	ZipArchiveOutputStream zaos  = new ZipArchiveOutputStream(bos);
    	zaos.setEncoding("GBK");
    	try {
    		if(StringUtils.isNotBlank(sourceFileOrFolder)){
				File file = new File(sourceFileOrFolder);
				if (file.exists()){
					String newFileName =  "";
					sourceFileOrFolder = sourceFileOrFolder.replaceAll("\\\\", "/");
					if(sourceFileOrFolder.endsWith("/")){
						sourceFileOrFolder = sourceFileOrFolder.substring(0, sourceFileOrFolder.length()-1);
					}
					//目录
					if(file.isDirectory()){
						//newFileName = sourceFileOrFolder.substring(sourceFileOrFolder.lastIndexOf("/")+1, sourceFileOrFolder.length());
					}else{//文件
	//					String parentPath = file.getParent();
	//					newFileName =  sourceFileOrFolder.substring(parentPath.length()+1, sourceFileOrFolder.length());
					}
					packFileOrDirToZip(zaos,sourceFileOrFolder, newFileName,suffixArr);
				}else{
					throw new ServiceException("打ZIP包异常: 文件或文件夹不存在！  " + sourceFileOrFolder);
				}
    		}
		} catch (FileNotFoundException e) {
			throw new ServiceException("打ZIP包异常: 文件或文件夹不存在！  ");
		} catch (Exception e) {
			throw new ServiceException("打ZIP包异常:内部处理错误！  " +  e.getStackTrace());
		}finally {//关闭流
			zaos.flush();
	        zaos.close();
	        bos.flush();
	        bos.close();
	        out.flush();
	        out.close();
		}
		//测试结束
    	getTotaltime("zipFilesOrFolders",ss);
		
		
		
    }
	
	
	 /**
     * 
     * @Title: zipFilesOrFolders 
     * @Description: 打包目录为ZIP（不包含压缩算法）
     * @param   sourceFileOrFolder 要打包的文件或目录绝对地址（是目录->会遍历目录下的所有文件）  
     * 如new String[][]{{"d:/dir/","dir_new"},{"d:/dir_1/","dir_1_new"}}
     * @param   outputZipfilepath 打包后的ZIP文件绝对地址   
     * 如："d:/dir/1.zip"
     * @param   suffixArr 打包要过滤的后缀名,
     * 如：  new String[]{"jpg","pdf"} -> 代表只打包jpg/pdf格式的文件;打包全部文件为:null 
     * @return void 
     * @throws
     */
	public static void zipFileOrFolder(String[][] sourceFilesOrFolders,String outputZipfilepath,
			String[] suffixArr) throws Exception{
		//测试开始
    	long ss = getStartTime("zipFilesOrFolders");
    	File f = new File(outputZipfilepath);
        //创建目录
    	mkdir(outputZipfilepath);
    	f.createNewFile();
    	OutputStream out = new FileOutputStream(f);
    	BufferedOutputStream bos = new BufferedOutputStream(out);
    	ZipArchiveOutputStream zaos  = new ZipArchiveOutputStream(bos);
    	zaos.setEncoding("GBK");
    	try {
    		if(sourceFilesOrFolders != null && sourceFilesOrFolders.length > 0){
    			for (int i = 0; i < sourceFilesOrFolders.length; i++) {
    				String[] arr = sourceFilesOrFolders[i];
    				String filePath = arr[0];
    				if(StringUtils.isNotBlank(filePath)){
	    				File file = new File(filePath);
	    				if (file.exists()){
	    					if(StringUtils.isNotBlank(filePath)){
	    						if(StringUtils.isNotBlank(arr[1])){
	        						packFileOrDirToZip(zaos,filePath, arr[1],suffixArr);
	        					}else{
	        						String newFileName = "";
	        						filePath = filePath.replaceAll("\\\\", "/");
	        						if(filePath.endsWith("/")){
	        							filePath = filePath.substring(0, filePath.length()-1);
	        						}
	        						//目录
	        						if(file.isDirectory()){
	        							newFileName = filePath.substring(filePath.lastIndexOf("/")+1, filePath.length());
	        						}else{//文件
//	        							String parentPath = file.getParent();
//	        							newFileName =  filePath.substring(parentPath.length()+1, filePath.length());
	        							
	        						}
        	        				packFileOrDirToZip(zaos,filePath, newFileName,suffixArr);
        	        			 }
        	    			}
    					}else{
    						throw new ServiceException("打ZIP包异常: 文件或文件夹不存在！  " + filePath);
    					}
    				}else{
    					throw new ServiceException("打ZIP包异常: 文件或文件夹不存在！  " + filePath);
    				}
    		    }
    		}
		} catch (FileNotFoundException e) {
			throw new ServiceException("打ZIP包异常: 文件或文件夹不存在！  ");
		} catch (Exception e) {
			throw new ServiceException("打ZIP包异常:内部处理错误！  " +  e.getStackTrace());
		}finally {//关闭流
			zaos.flush();
	        zaos.close();
	        bos.flush();
	        bos.close();
	        out.flush();
	        out.close();
		}
		//测试结束
    	getTotaltime("zipFileOrFolder",ss);
    }
	
	
	/**
     * 
     * @Title: zipFilesOrFolders 
     * @Description: 打包目录为ZIP（不包含压缩算法）
     * @param   sourceFileOrFolder 要打包的文件或目录绝对地址（是目录->会遍历目录下的所有文件）  
     * 如：new String[]{"d:/dir/","d:/dir_1"}
     * @param   outputZipfilepath 打包后的ZIP文件绝对地址   
     * 如："d:/dir/1.zip"
     * @param   suffixArr 打包要过滤的后缀名,
     * 如：  new String[]{"jpg","pdf"} -> 代表只打包jpg/pdf格式的文件;打包全部文件为:null 
     * @return void 
     * @throws
     */
	public static void zipFileOrFolder(String[] sourceFilesOrFolders,String outputZipfilepath,
			String[] suffixArr) throws Exception{
		//测试开始
    	long ss = getStartTime("zipFilesOrFolders");
    	File f = new File(outputZipfilepath);
        //创建目录
    	mkdir(outputZipfilepath);
    	f.createNewFile();
    	OutputStream out = new FileOutputStream(f);
    	BufferedOutputStream bos = new BufferedOutputStream(out);
    	ZipArchiveOutputStream zaos  = new ZipArchiveOutputStream(bos);
    	zaos.setEncoding("GBK");
    	try {
    		if(sourceFilesOrFolders != null && sourceFilesOrFolders.length > 0){
    			for (int i = 0; i < sourceFilesOrFolders.length; i++) {
    				String filePath = sourceFilesOrFolders[i];
    				if(StringUtils.isNotBlank(filePath)){
    					File file = new File(filePath);
    					if (file.exists()){
    						String newFileName = "";
    						filePath = filePath.replaceAll("\\\\", "/");
    						if(filePath.endsWith("/")){
    							filePath = filePath.substring(0, filePath.length()-1);
    						}
    						//目录
    						if(file.isDirectory()){
    							newFileName = filePath.substring(filePath.lastIndexOf("/")+1, filePath.length());
    						}else{//文件
//    							String parentPath = file.getParent();
//    							newFileName =  filePath.substring(parentPath.length()+1, filePath.length());
    						}
	        				packFileOrDirToZip(zaos,filePath, newFileName,suffixArr);
        				}else{
        					throw new ServiceException("打ZIP包异常: 文件或文件夹不存在！  " + filePath);
        				}
    				}else{
    					throw new ServiceException("打ZIP包异常: 文件或文件夹不存在！  " + filePath);
    				}
    			}
    		}
    		
		} catch (FileNotFoundException e) {
			throw new ServiceException("打ZIP包异常: 文件或文件夹不存在！  ");
		} catch (Exception e) {
			throw new ServiceException("打ZIP包异常:内部处理错误！  " +  e.getStackTrace());
		}finally {//关闭流
			zaos.flush();
	        zaos.close();
	        bos.flush();
	        bos.close();
	        out.flush();
	        out.close();
		}
		//测试结束
    	getTotaltime("zipFileOrFolder",ss);
    }
	
	/**
	 * 
	 * @Title: encryptZipFile 
	 * @Description: 文件加密压缩 --如果是多个文件，则先压缩成ZIP文件，再调用该方法，生成加密的ZIP文件
	 * @param srcFile 要加密压缩的的文件绝对路径和名称
	 * 如："d:/dir/1.zip"
	 * @param outputFile 生成的压缩文件绝对路径和名称
	 * 如："d:/dir_new/1.zip"
	 * @param password 压缩密码
	 * 如："111111"
	 * @author zhanglei
	 * @update author tanghui
	 * @return File 
	 * @throws
	 */
	public static File encryptZipFile(File srcFile, String outputFile,String password) throws Exception{
		//要返回的文件
		File aesZipFile = null;
		AesZipFileEncrypter enc  = null;
		try {
			//判断文件夹或文件是否存在
	    	if(srcFile.exists()){
	    		//创建目录
				mkdir(outputFile);
				File directory = new File(outputFile);
				directory.createNewFile();
				enc  = new AesZipFileEncrypter(outputFile);
				enc.add(srcFile, password);
				
	    	}
		} catch (IOException e) {
			throw new ServiceException("要加密的文件不存在！  " + srcFile);
		}catch (Exception e) {
    		throw new ServiceException("加密异常:内部处理错误！  " + srcFile);
		} finally {
			try {
				if(enc != null){
					enc.close();
				}
				aesZipFile = new File(outputFile);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return aesZipFile;
	}
	
	
    
	

    /**
     * 
     * @Title: packFilesOrDirsToZip 
     * @Description: 把一个目录打包到一个指定的zip文件中
     * @param zaos   zip输出对象
     * @param dirpath  要打包的文件或目录绝对地址 如：d:\\1
     * @param pathName  zip文件绝对地址 如： 1\1 
     * @param suffixArr  要过滤的后缀名
     * @return void 
     * @throws
     */
	public static void packFileOrDirToZip(ZipArchiveOutputStream zaos, String dirPath, String pathName,String[] suffixArr) throws FileNotFoundException, IOException {
		if (StringUtils.isNotEmpty(pathName)) {
            pathName += File.separator;
        }
		File dir = new File(dirPath);
    	try {
    		//如果是一个目录
    		if(dir.isDirectory()){
    			File[] files = dir.listFiles();
	            if (files == null || files.length < 1) {
//	            	zaos.putArchiveEntry(new ZipArchiveEntry(pathName));
//	            	zaos.closeArchiveEntry();
	                return;
	            }
	            //获取根路径长度
	            for (int i = 0; i < files.length; i++) {
	                //判断此文件是否是一个文件夹
	                if (files[i].isDirectory()) {
	                	packFileOrDirToZip(zaos, files[i].getAbsolutePath(), pathName + files[i].getName() + File.separator,suffixArr);
	                } else {
	                	if(isDoFile(files[i].getName(), suffixArr)){
	                		zaos.putArchiveEntry(new ZipArchiveEntry(pathName + files[i].getName()));
		                    IOUtils.copy(new FileInputStream(files[i].getAbsolutePath()), zaos);
		                    zaos.closeArchiveEntry();
	                	}
	                }
	            }
		    }else{//否则是文件,直接打ZIP包
		    	if(isDoFile(dir.getName(), suffixArr)){
		    		zaos.putArchiveEntry(new ZipArchiveEntry(pathName + dir.getName()));
		    		IOUtils.copy(new FileInputStream(dir.getAbsolutePath()), zaos);
		    		zaos.closeArchiveEntry();
		    	}
		    }
		} catch (Exception e) {
			e.printStackTrace();
		}
    }
	
	
	
	//多重打包   
    public static void makeZip() throws IOException, ArchiveException{   
    	OutputStream out = null;
//        BufferedOutputStream bos = null;
//        ZipArchiveOutputStream zaos = null;
    	File f1 = new File("D:/加班.png");   
        File f2 = new File("D:/1 - 副本.zip");   
        File f = new File("d:\\1.zip");
        out = new FileOutputStream(f);
//        bos = new BufferedOutputStream(out);
//        ByteArrayOutputStream tempbaos = new ByteArrayOutputStream();
//        BufferedOutputStream tempbos = new BufferedOutputStream(bos);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();   
           
        //ArchiveOutputStream ostemp = new ArchiveStreamFactory().createArchiveOutputStream("zip", baos);   
        ZipArchiveOutputStream ostemp = new ZipArchiveOutputStream(baos);   
        ostemp.setEncoding("GBK");   
        ostemp.putArchiveEntry(new ZipArchiveEntry(f1.getName()));   
        IOUtils.copy(new FileInputStream(f1), ostemp);   
        ostemp.closeArchiveEntry();   
        ostemp.putArchiveEntry(new ZipArchiveEntry(f2.getName()));   
        IOUtils.copy(new FileInputStream(f2), ostemp);   
        ostemp.closeArchiveEntry();   
        ostemp.finish();   
        ostemp.close();   
  
//      final OutputStream out = new FileOutputStream("D:/testcompress.zip");   
//        final OutputStream out = new FileOutputStream("D:/中文名字1.zip");   
        ArchiveOutputStream os = new ArchiveStreamFactory().createArchiveOutputStream("zip", out);   
        os.putArchiveEntry(new ZipArchiveEntry("打包1.zip"));   
//        baos.writeTo(os);   
        os.closeArchiveEntry();   
//        baos.close();   
        os.finish();   
        os.close();   
    }   
	
    
    //检测ZIP文件是否存在，或格式是否正确
  	@SuppressWarnings("unused")
  	private static void checkZipFileIsExist(String inputFilePath) throws ServiceException {
  		ZipFile zipFile = null;
  		try {
  			File inFile = new File(inputFilePath);
  			if (!inFile.exists() || inFile.isDirectory()) {
  				throw new ServiceException("压缩文件不存在");
  			}
  			zipFile = new ZipFile(inFile);
  		} catch (ZipException zipe) {
  			throw new ServiceException("文件格式异常");
  		} catch (IOException ioe) {
  			throw new ServiceException("文件读取异常");
  		} finally {
  			try {
  				if (zipFile != null)
  					zipFile.close();
  			} catch (IOException e) {
  				throw new ServiceException("文件关闭异常");
  			}
  		}
  	}
      
	
	//判断是否需要处理
	public static boolean isDoFile(String fileName,String[] suffixArr){
		boolean b = false;
		if(suffixArr != null && suffixArr.length > 0){
			for (int i = 0; i < suffixArr.length; i++) {
				fileName = fileName.substring(fileName.lastIndexOf(".")+1,fileName.length());
				if(StringUtils.isNotBlank(fileName) && fileName.toLowerCase().equals(suffixArr[i])){
					b = true;
					break;
				}
			}
		}else{
			b = true;
		}
		return b;
	}
	
    
    //创建目录
  	@SuppressWarnings("unused")
  	private static String createDir(String inputFilePath) {
  		int lastPoint = inputFilePath.lastIndexOf('.');
  		if (lastPoint != -1)
  			inputFilePath = inputFilePath.substring(0, lastPoint);
	  		File newdir = new File(inputFilePath);
	  		if (!newdir.mkdir()) {
	  			throw new ServiceException("创建文件目录失败！");
	  		}
  		return inputFilePath;
  	}
  	
  	
  	// 创建文件上传路径
  	public static void mkdir(String path) {
  		File fd = null;
  		try {
  			if (StringUtils.isNotEmpty(path)) {
  				path = path.substring(0,path.replaceAll("\\\\","/").lastIndexOf("/"));
  				fd = new File(path);
  				if (!fd.exists()) {
  					fd.mkdirs();
  				}
  			}
  		} catch (Exception e) {
  			e.printStackTrace();
  		} finally {
  			fd = null;
  		}
  	}

    
    //当前开始时间（返回秒数）
  	public static long getStartTime(String method){
  	    Date dt = new Date();  
  	    logger.info("(" + method + ")方法开始执行:[" + DateUtil.convertDateTimeToString(dt) + "]");
  	    logger.info("(" + method + ")方法处理中....");
  		return dt.getTime();
  	}
  	
  	//结束时间差（总时间差-总秒数）
  	public static String getTotaltime(String method,long startTime){
          Date endDate = new Date();  			
          long endTime = endDate.getTime();
          long timecha = (endTime - startTime);  			
          String totalTime = sumTime(timecha);  
          logger.info("(" + method + ")处理完成时间：[" +  DateUtil.convertDateTimeToString(endDate) + "] 共用:{" + totalTime + "}");
          return totalTime;
  	}
  	
  	/**
	 * 计算转换的总时间
	 * 
	 * @param ms
	 * @return
	 */
	public static String sumTime(long ms) {
		int ss = 1000;
		long mi = ss * 60;
		long hh = mi * 60;
		long dd = hh * 24;
		long day = ms / dd;
		long hour = (ms - day * dd) / hh;
		long minute = (ms - day * dd - hour * hh) / mi;
		long second = (ms - day * dd - hour * hh - minute * mi) / ss;
		long milliSecond = ms - day * dd - hour * hh - minute * mi - second
				* ss;
		String strDay = day < 10 ? "0" + day + "天" : "" + day + "天";
		String strHour = hour < 10 ? "0" + hour + "小时" : "" + hour + "小时";
		String strMinute = minute < 10 ? "0" + minute + "分" : "" + minute + "分";
		String strSecond = second < 10 ? "0" + second + "秒" : "" + second + "秒";
		String strMilliSecond = milliSecond < 10 ? "0" + milliSecond : ""
				+ milliSecond;
		strMilliSecond = milliSecond < 100 ? "0" + strMilliSecond + "毫秒" : ""
				+ strMilliSecond + " 毫秒";
		return strDay + " " + strHour + ":" + strMinute + ":" + strSecond + " "
				+ strMilliSecond;
	}
	
	
	//测试
    public static void main(String[] args) {
    	//打包1
    	try {
    		//new String[]{"txt"}
			//zipFileOrFolder("D:\\3","D:\\4\\11.zip",null);
		} catch (Exception e1) {
			e1.printStackTrace();
		}
    	//打包2
    	//String[][] arr2 ={{"D:\\1\\dpdf\\","西游记\\dpdf"},{"D:\\2","水浒传"},{"D:\\3","红楼梦"}};
    	try {
			//zipFileOrFolder(arr2, "d:\\4\\22.zip",null);
		} catch (Exception e) {
			e.printStackTrace();
		}
    	//打包3
    	//String[] arr ={"D:\\1\\新建 PowerDesigner 15 Requirements Model File.RQM","D:\\2","D:\\3"};
    	try {
			//zipFileOrFolder(arr, "d:\\4\\33.zip",null);
		} catch (Exception e) {
			e.printStackTrace();
		}
    	
    	//解压
    	try {
			//unZipToFolder("D:\\4\\22.zip", "D:\\4");
		} catch (Exception e) {
			e.printStackTrace();
		}
    	//加密
		try {
			String s= null;
			System.out.println(Integer.parseInt(s));
			//encryptZipFile(new File("D:\\1.zip"),  "D:\\1\\1.zip", "222222");
		} catch (Exception e) {
			StackTraceElement[] trace = e.getStackTrace();
    		throw new ServiceException("解压失败:内部解压处理错误！"+trace[0].toString());
		}
		
		
    	
	}
}	
