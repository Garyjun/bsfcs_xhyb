package com.brainsoon.common.util.dofile.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import jcifs.smb.SmbException;
import jcifs.smb.SmbFile;
import jcifs.smb.SmbFileInputStream;
import jcifs.smb.SmbFileOutputStream;

import com.channelsoft.appframe.utils.WebappConfigUtil;


/**
 * 
 * @ClassName: JcifsUtils 
 * @Description:  此类仅限用于华师项目，用于其他项目需要稍微改造一下，去掉"ftpLocalMappingUrl"的配置即可
 * 1 相关知识介绍
1.1 SMB
      Microsoft网络配置中主要采用SMB形式实现文件共享和打印服务，SMB（服务器消息块）是一种客户端/服务器文件共享协议。
      IBM于20世纪80年代末期开发了服务器信息块（SMB），用于规范共享网络资源（如目录、文件、打印机以及串行端口）的结构。
      这是一种请求/响应协议。与FTP协议支持的文件共享不同，SMB协议中的客户端要与服务器建立长期连接。一旦建立连接，
      客户端用户就可以访问服务器上的资源，就如同资源位于客户端主机上一样。
      从Windows 2000系列软件开始，Microsoft修改了软件的基础结构，使其适用SMB协议。而在以前的Microsoft产品中，
      SMB服务需要使用非TCP/IP协议族来执行域名解析。从Windows 2000开始，Microsoft的所有产品都采用DNS系统。
      由此，TCP/IP协议族可以直接支持SMB资源共享。
      SMB协议中规定了文件系统访问和客户如何请求文件的方式以及SMB协议进程间通信的方式。所有的SMB消息都采用一种格式。
      该格式采用固定大小的文件头，后跟可变 大小的参数以及数据组件。
1.2 jcifs
     Jcifs pan>是一个用JAVA开发的SMB客户端库，利用jcifs可以操作windows共享文件，
     可以得到域用户，实现单点登录，最新版本为：1.3.12，官方网址：http://jcifs.samba.org/
1.3 remoteUrl说明
	remoteUrl如何填写是值得注意的
	如果是无需密码的共享，则类似如下格式：
     smb://ip/sharefolder（例如：smb://192.168.0.77/test）
	如果需要用户名、密码，则类似如下格式：
    Smb://username:password@ip/sharefolder（例如：smb://chb:123456@192.168.0.1/test）
 * @author tanghui 
 * @date 2014-11-20 下午4:48:00 
 *
 */
public class JcifsUtils {

	public static String ftpLocalMappingUrl = WebappConfigUtil.getParameter("FTP_LOCAL_MAPPING").replaceAll("\\\\", "\\/");


	/**
	 * 
	 * @Title: smbGet 
	 * @Description: 从共享目录拷贝文件到本地
	 * @param   
	 * @return boolean 
	 * @throws
	 */
	 @SuppressWarnings("unused")
	public static boolean smbGet(String remoteUrl,String localPath) {  
	    InputStream in = null;  
	    OutputStream out = null;
	    boolean b = true;
	    try {  
	        SmbFile remoteFile = new SmbFile(remoteUrl); 
	        if(remoteFile == null){
	        	throw new DoFileException("&远程文件【" + remoteUrl +"】不存在。");
	        }
	        DoFileUtils.mkdir(localPath);
			File localFile = new File(localPath);  
			in = new BufferedInputStream(new SmbFileInputStream(remoteFile));  
			out = new BufferedOutputStream(new FileOutputStream(localFile));     
			int size = remoteFile.getContentLength();
	        byte[] buffer = new byte[1024];  
	        if(size < buffer.length){
	        	buffer = new byte[size]; 
	        }
	        int count = 1;
        	while(in.read(buffer) != -1){  
 	            out.write(buffer);
 	            int sylen = (size - count * buffer.length);
 	            if(sylen != 0 && sylen <= buffer.length){
 	            	buffer = new byte[sylen];  
 	            }else if(sylen <= 0){
 	            	break;
 	            }
 	           count ++;
 	        } 
			
	    } catch (SmbException e) { 
	    	e.printStackTrace();
	    	throw new DoFileException("映射盘文件路径不存在或没有权限或本地磁盘不存在或文件无法通过系统读取.");
	    } catch (Exception e) {  
	        e.printStackTrace();  
	        b = false;
	    } finally {  
	        try {  
	        	if(in != null){
	        		in.close();  
	        	}
	        	
	        	if(out != null){
	        		out.close();  
	        	} 
	        } catch (IOException e) {  
	        	 b = false;
	           e.printStackTrace();  
	        }  
	    }  
	    return b;
	 } 
	 
	 
	/**
	 *   
	 * @Title: smbPut 
	 * @Description: 从本地上传文件到共享目录 
	 * @param   
	 * @return boolean 
	 * @throws
	 */
	 @SuppressWarnings("unused")
	public static boolean smbPut(String remoteUrl,String localFilePath) {  
	    InputStream in = null;  
	    OutputStream out = null;  
	    boolean b = true;
	    SmbFile remoteFile = null;
	    try {  
	    	String ftpLocalMappingUrlStr = ftpLocalMappingUrl;
	        File localFile = new File(localFilePath); 
	        if(localFile == null){
	        	throw new DoFileException("&本地文件【" + localFile +"】不存在。");
	        }
	        //如果目录不存在
	        remoteUrl = remoteUrl.replaceAll("\\\\", "\\/");
	        String remotePUrl = remoteUrl.substring(0, remoteUrl.lastIndexOf("/"));
			String smburlStr = remotePUrl.substring(ftpLocalMappingUrl.length(), remotePUrl.length());
			String[] smburls = smburlStr.split("/");
			//递归生成目录
			for (int i = 0; i < smburls.length; i++) {
				if(ftpLocalMappingUrlStr.endsWith("/")){
					ftpLocalMappingUrlStr = ftpLocalMappingUrlStr + smburls[i];
				}else{
					ftpLocalMappingUrlStr = ftpLocalMappingUrlStr + "/" + smburls[i];
				}
				smbMkDir(ftpLocalMappingUrlStr);
			}
	        remoteFile = new SmbFile(remoteUrl);  
	        in = new BufferedInputStream(new FileInputStream(localFile));     
	        out = new BufferedOutputStream(new SmbFileOutputStream(remoteFile)); 
	        int size = in.available();
	        byte[] buffer = new byte[1024];  
	        if(size < buffer.length){
	        	buffer = new byte[size]; 
	        }
	        int count = 1;
        	while(in.read(buffer) != -1){  
 	            out.write(buffer);
 	            int sylen = (size - count * buffer.length);
 	            if(sylen != 0 && sylen <= buffer.length){
 	            	buffer = new byte[sylen];  
 	            }else if(sylen <= 0){
 	            	break;
 	            }
 	           count ++;
 	        } 
	    } catch (SmbException e) {  
	    	e.printStackTrace();
	    	throw new DoFileException("本地文件不存在或远程映射盘没有写入权限.");
	    } catch (Exception e) {  
	    	try {
				if (remoteFile != null && remoteFile.exists()){
					b = true;
				}else{
					b = false;
				}
			} catch (SmbException e1) {
				e1.printStackTrace();
			}
	    	e.printStackTrace();  
	    } finally {  
	        try {  
	        	if(in != null){
	        		in.close();  
	        	}
	        	
	        	if(out != null){
	        		out.close();  
	        	}
	        } catch (IOException e) {  
	           b = false;
	           e.printStackTrace();  
	        }  
	    }  
	    return b;
	 }  
	 
	 /**
	  * 
	  * @Title: createDir 
	  * @Description: 远程创建目录
	  * @param   
	  * @return void 
	  * @throws
	  */
	 public static void smbMkDir(String smburl){  
		 try {
		 	SmbFile fp = new SmbFile(smburl);  
	        // 目录已存在创建文件夹    
	        if (fp.exists() && fp.isDirectory()) {    
	              //
	        } else{  
	            // 目录不存在的情况下，会抛出异常    
	            fp.mkdir();    
	        }  
		} catch (Exception e) {
			e.printStackTrace();
		}
    }  
	 
	 //test
	 public static void main(String[] args) {
		 //smbMkDir("smb://10.130.100.105/data/fileDir/converFileRoot/TB/T01/G16/hsjc_TB_M_V01_G91-B1_S01_T01_F10_18049732/doc");
		//smbGet("smb://10.130.100.105/data/fileDir/converFileRoot/TB/T02/G3/hsjc_TB_M_V01_G91-QC_S04_T02_F10_13247695/doc/关于电和热的教学建议.xml", "D:/temp/fileDir/converFileRoot/TB/T02/G3/hsjc_TB_M_V01_G91-QC_S04_T02_F10_13247695/doc/关于电和热的教学建议.xml");
		smbPut("smb://10.130.100.105/data/fileDir/converFileRoot/TB/T02/G3/hsjc_TB_M_V01_G91-QC_S04_T02_F10_13247695/doc/关于电和热的教学建议.xml", "D:/temp/fileDir/converFileRoot/TB/T02/G3/hsjc_TB_M_V01_G91-QC_S04_T02_F10_13247695/doc/DocViewerController.java");
	}
}
