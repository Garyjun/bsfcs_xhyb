package com.brainsoon.common.util.dofile.conver;
import java.io.File;

import org.apache.log4j.Logger;
import org.artofsolving.jodconverter.OfficeDocumentConverter;
import org.artofsolving.jodconverter.office.ExternalOfficeManagerConfiguration;
import org.artofsolving.jodconverter.office.OfficeException;
import org.artofsolving.jodconverter.office.OfficeManager;

import com.brainsoon.common.support.GlobalAppCacheMap;
import com.brainsoon.common.util.dofile.util.DoFileException;
import com.brainsoon.common.util.dofile.util.DoFileUtils;


/**
 * 
 * @ClassName: Office2PdfThread
 * @Description:office系列文档转pdf工具类
 * @author tanghui
 * @date 2014-11-18 上午10:29:15
 * 
 */
public class Office2PdfThread implements Runnable {
	protected static final Logger logger = Logger.getLogger(Office2PdfThread.class);
	
	public File inputFile;
	public File outputFile;
	public int i;
	
	
	public Office2PdfThread(File inputFile,
			File outputFile, int i) {
		super();
		this.inputFile = inputFile;
		this.outputFile = outputFile;
		this.i = i;
	}

	public File getInputFile() {
		return inputFile;
	}


	public void setInputFile(File inputFile) {
		this.inputFile = inputFile;
	}


	public File getOutputFile() {
		return outputFile;
	}


	public void setOutputFile(File outputFile) {
		this.outputFile = outputFile;
	}


	public int getI() {
		return i;
	}


	public void setI(int i) {
		this.i = i;
	}


	/**
	 * 
	 * 消费进程(转换文件)
	 * 
	 */
	@Override
	public void run() {
		try {
			boolean b = false;
			Object openOfficeSize = GlobalAppCacheMap.getValue("openOfficeSize");
			if(openOfficeSize == null){
				openOfficeSize = "1";
			}else{
				if((openOfficeSize+"").equals("40")){
					openOfficeSize = "1";
					b = true;
				}else{
					openOfficeSize = (Integer.parseInt(openOfficeSize+"") + 1) + "";
				}
			}
			GlobalAppCacheMap.putKey("openOfficeSize", openOfficeSize);
			String[] cmds = {"soffice.exe","soffice.bin"};
			for (int i = 0; i < cmds.length; i++) {
				boolean b1 = DoFileUtils.exeQueryCmd(cmds[i]);
				if(!b1){
					b = true;
					break;
				}
			}
			//重试第二次必须重启服务
			logger.info("getI() value：" + getI());
			if(getI() > 0){
				b = true;
			}
			if(b){
				b = getOfficeManager(true);
			}else{
				b = getOfficeManager(false);
			}
			//如果服务启动成功则开始转换逻辑
			OfficeDocumentConverter converter = null;
			if(b){
				logger.info("Office开始转换...");
				converter = new OfficeDocumentConverter(officeManager);
				converter.convert(inputFile, outputFile, i);
				if(outputFile.exists() && outputFile.length() > 0){
					logger.info("Office转换成功。");
				}else{
					logger.info("Office转换失败。");
				}
			}else{
				GlobalAppCacheMap.putKey("converstatus","error");
				//throw new DoFileException("Office服务未启动，连接不成功，无法进行转换服务。");
			}
		} catch (Exception e) {
			GlobalAppCacheMap.putKey("converstatus","error");
			e.printStackTrace();
		} finally{
			destriyOfficeManager(); //停止
		}
	}
	
	

	//Office服务管理
	private OfficeManager officeManager;
	
	/**
	 * 
	 * @Title: getOfficeManager 
	 * @Description:取得一个Office连接 
	 * @param   
	 * @return OfficeManager 
	 * @throws
	 */
	private boolean getOfficeManager(boolean b){
		return initOfficeManager(b);
	}
	
	
	
	public boolean checkInitOO(){
		boolean cs = true;
		try {
			officeManager = new ExternalOfficeManagerConfiguration().buildOfficeManager();
			officeManager.start();
		} catch (OfficeException e) {
			cs = false;
			logger.error("Office服务连接失败。");
		} finally{
		}
		return cs;
	}
	
	/**
	 * 
	 * @Title: startOfficeServce 
	 * @Description: 启动Office服务
	 * @param   
	 * @return void 
	 * @throws
	 */
	public static synchronized void startOfficeServce(){
		try {
			logger.info("Office开始自启动服务...");
			//Office的安装目录
			String OfficeHome = "";
			Object openOfficePath  = GlobalAppCacheMap.getValue("openOfficePath");
			logger.info("--------------openOfficePath:"+openOfficePath);
			if(openOfficePath == null){
				OfficeHome = "c:/Program Files/LibreOffice 4/program";  //默认
			}else{
				OfficeHome = openOfficePath + "";
			}
			logger.info("--------------OfficeHome:"+OfficeHome);
			// 假如从文件中读取的URL地址最后一个字符不是 '\'，则添加'\'  
            if (OfficeHome.charAt(OfficeHome.length() - 1) != '/') {  
            	OfficeHome += "/";  
            }  
            logger.info("--------------OfficeHome:"+OfficeHome);
            if(!new File(OfficeHome).exists()){
            	throw new DoFileException("Office安装路径配置不正确，请检查。");
            }
            
			//Office端口
			String officePort = "";
			Object openOfficePort  = GlobalAppCacheMap.getValue("openOfficePort");
			if(openOfficePort == null){
				officePort = "8100"; //默认
			}else{
				officePort = openOfficePort + "";
			}
			
        	String os = System.getProperty("os.name");
        	String command = ""; //执行命令参数
			if (os != null && os.toLowerCase().startsWith("windows")) {
				command = OfficeHome +  "soffice  --nologo --nodefault  -headless -accept=\"socket,host=127.0.0.1,port=" + officePort + ";urp;\" -nofirststartwizard";  
			}else{
				command = "sudo -u hsjc nohup " + OfficeHome +  "soffice -headless -accept=\"socket,host=127.0.0.1,port=" + officePort + ";urp;\" -nofirststartwizard &";  
			}
			//第一步：结束soffice的进程
			boolean b = true;
			String[] cmds = {"soffice","soffice.bin","soffice.exe"};
			for (int i = 0; i < cmds.length; i++) {
				b = DoFileUtils.exeWinKillCmd(cmds[i]);
			}
			
			if(b){
				//第二步：启动soffice服务进程
				logger.info("Office启动命令为：" + command);
				try {
					Runtime.getRuntime().exec(command);
					//执行完毕命令后等待3秒,让全部启动完毕
					Thread.sleep(3000);
				} catch (Exception e) {
					e.printStackTrace();
				}
				logger.info("Office自启动服务成功.");
			}
		} catch (Exception e2) {
			e2.printStackTrace();
		}finally{
		}
	}
	
	
	/**
	 * 
	 * @Title: initOfficeManager 
	 * @Description: 初始化及连接Office服务
	 * @param   
	 * @return void 
	 * @throws
	 */
	public synchronized boolean initOfficeManager(boolean b){
		try {
			logger.info("Office开始连接服务...");
			if(b){//强制重启office进程
				startOfficeServce();
			}
			b = checkInitOO();
			if(!b){//如果未连接成功，则重新启动服务
				startOfficeServce();
				b = checkInitOO();
			}
		} catch (Exception e) {
			logger.error("Office服务未启动，连接不成功，无法进行转换服务。");
		}finally{
			if(!b){
				GlobalAppCacheMap.putKey("converstatus","error");
				logger.error("Office服务未正常启动，无法连接Office。");
				//throw new DoFileException("Office服务未正常启动，无法连接Office。");
			}else{
				logger.info("Office服务连接成功。");
			}
		}
		return b;
	}
	
	
	/**
	 * 
	 * @Title: destriyOfficeManager 
	 * @Description: 关闭Office连接
	 * @param   
	 * @return void 
	 * @throws
	 */
	public void destriyOfficeManager(){
		try {
			if(officeManager != null){
				officeManager.stop();
				logger.info("Office连接成功关闭。");
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new DoFileException("Office连接未正常关闭。");
		}
	}

}
