package com.brainsoon.fileService.utils;

import java.io.File;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.brainsoon.common.util.HttpClientUtil;
import com.brainsoon.common.util.dofile.util.DoFileUtils;
import com.brainsoon.common.util.dofile.util.JcifsUtils;
import com.brainsoon.common.util.dofile.util.PropertiesReader;
import com.channelsoft.appframe.utils.WebappConfigUtil;


/**
 * 
 * @ClassName: UpdateMetadataTool 
 * @Description:  操作Rdf的工具类
 * @author tanghui 
 * @date 2014-12-4 下午1:49:59 
 *
 */
public class UpdateFileTool {
	protected static  Log logger = LogFactory.getLog(UpdateFileTool.class);

	//本地路径
	private static String ftpLocalMappingUrl = WebappConfigUtil.getParameter("FTP_LOCAL_MAPPING");
	//RDF路径
	private static String semanticIndexServerUrl = WebappConfigUtil.getParameter("SEMANTIC_INDEX_SERVER");
	//临时目录
	private static String fileConverTempDir = PropertiesReader.getInstance().getProperty("fileConverTempDir");
	
	/**
	 * 
	 * @Title: updCover 
	 * @Description: 更新封面到RDF元数据
	 * @param   imageRealPath cover相对路径
	 * @return void 
	 * @throws
	 */
	public static String updCover(String resId,String imageRealPath){
		String describe = "";
		try {
			//出版库的资源不用更新
			if(StringUtils.isNotBlank(resId) && !resId.startsWith("urn:publish")){
				//拼接参数-取相对路径
	        	String urlParams = "/semantic_index_server/createOntology/updateResCover?resourceId=" + resId +"&path=" + URLEncoder.encode(imageRealPath, "utf8");
	        	//执行操作
	        	String url = semanticIndexServerUrl + urlParams;
	       	 	String reTurnStr = HttpClientUtil.executeGet(url);
	       	 	if(StringUtils.isBlank(reTurnStr) || !reTurnStr.equals("0")){
	       	 		describe += "{失败}-*封面文件抽取成功，但更新到RDF元数据失败！\n";
	       	 		logger.error("#" + describe);
	       	 	}else{
	       	 		logger.info("*{成功}-封面文件抽取成功，更新到RDF元数据成功。");
	       	 	}
			}
		} catch (Exception e) {
			describe += "{失败}-&封面文件抽取成功，但更新到RDF元数据失败！\n";
			e.printStackTrace();
		}
		 return describe;
	}
	
	
	/**
	 * 
	 * @Title: updTxt 
	 * @Description: 更新文本到RDF元数据
	 * @param   
	 * @return String 
	 * @throws
	 */
	public static String updTxt(String txtStr,String resId){
		String describe = "";
		try {
			if(StringUtils.isNotBlank(txtStr)){
				//出版库的资源不用更新
				if(StringUtils.isNotBlank(resId) && !resId.startsWith("urn:publish")){
					//拼接参数
					txtStr = URLEncoder.encode(txtStr, "utf8");
					String jsonStr = "{\"@type\":\"commonMetaData\",\"resource\":\"" + resId +"\",\"commonMetaDatas\":{\"description\":\"" + (txtStr==null?"":txtStr) +"\"}}";
		        	String urlParams = "/semantic_index_server/createOntology/updateResDescription";
		        	//执行操作
		        	String url = semanticIndexServerUrl + urlParams;
		        	logger.info("====保存摘要url:　" + url + " =====jsonStr: " + jsonStr);
		       	 	String reTurnStr = HttpClientUtil.postJson(url,jsonStr);
		       	 	if(StringUtils.isBlank(reTurnStr) || !reTurnStr.equals("0")){
		       	 		describe += "{失败}-*文本抽取成功，但更新到RDF元数据失败！\n";
		       	 		logger.error("#" + describe);
		       	 	}else{
		       	 		logger.info("{成功}-文本抽取成功，更新到RDF元数据成功！");
		       	 	}
				}
			}
		} catch (Exception e) {
			describe += "{失败}-&文本抽取成功，但更新到RDF元数据失败！\n";
			e.printStackTrace();
		}
		return describe;
	}
	
	
	/**
	 * 
	 * @Title: upLConverFile 
	 * @Description: 上传转换后的文件
	 * @param   
	 * @return String 
	 * @throws
	 */
	public static String upLConverdFile(String tarParentPath){
		String describe = "";
		int n1 = 0;
		int n2 = 0;
		boolean bbb = true;
		List<File> lists = new ArrayList<File>();
	    try {
	    	File fileDir = new File(tarParentPath);
			lists = DoFileUtils.getDirFiles(fileDir,lists);
			logger.info("=======开始上传转换后的文件到映射盘开始=======");
			for (File file : lists) {
				String localPath = file.getAbsolutePath().replaceAll("\\\\", "\\/");
				String localRelPath = localPath.substring((fileConverTempDir).length(), localPath.length());
				String remotePath = DoFileUtils.connectFilePath(ftpLocalMappingUrl, localRelPath).replaceAll("\\\\", "\\/"); 
				logger.info("====*远程路径为：" + remotePath + "\n"); 
				logger.info("====*本地路径为：" + localPath + "\n"); 
				boolean bb = JcifsUtils.smbPut(remotePath, localPath);
				if(!bb){
					bbb = false;
					describe += "{失败}-文件转换成功，但存储到映射盘失败！\n";
					logger.info("*" + describe);
					n2++;
				}else{
					n1++;
					logger.info("【" + localPath + "】存储到映射盘成功！\n");
					}
				}
				//删除带doi的那级目录
				try {
					if(bbb){
						DoFileUtils.deleteDir(new File(tarParentPath).getParent());
						logger.info("*删除临时文件成功。");
					}
				} catch (Exception e) {
					e.printStackTrace();
					logger.error("*删除临时文件失败。");
				}
			} catch (Exception e) {
				e.printStackTrace();
				describe += "{失败}-文件转换成功，但存储到映射盘失败！" + e.getMessage() + "\n";
				logger.error("#" + describe);
			}finally{
				logger.info("存储到映射盘共【" + lists.size() + "】，成功【" + n1 + "】，失败【" + n2 + "】！\n");
				logger.info("=======上传转换后的文件到映射盘结束=======");
		}
		return describe;
	}
	
	
	/**
	 * 
	 * @Title: upLCoverFile 
	 * @Description: 上传封面文件
	 * @param   
	 * @return String 
	 * @throws
	 */
	public static String upLCoverFile(String imagePath){
		String describe = "";
		try {
			imagePath = imagePath.replaceAll("\\\\", "\\/");
			String localRelPath = imagePath.substring((fileConverTempDir).length(), imagePath.length());
			String remotePath = DoFileUtils.connectFilePath(ftpLocalMappingUrl, localRelPath).replaceAll("\\\\", "\\/");
			logger.info("=======*远程路径为：" + remotePath.replaceAll("\\\\", "\\/") + "\n"); 
			logger.info("=======*本地路径为：" + imagePath + "\n"); 
			boolean bb = JcifsUtils.smbPut(remotePath.replaceAll("\\\\", "\\/"), imagePath);
			if(bb){
				//设置结果
				logger.info("存储到映射盘成功！\n");
				//删除带doi的那级目录
				try {
					DoFileUtils.deleteDir(new File(new File(imagePath).getParent()).getParent());
					logger.info("#删除临时文件成功。");
				} catch (Exception e) {
					e.printStackTrace();
					logger.info("#删除临时文件失败。");
				}
			}else{
				describe += "{失败}-封面文件抽取成功，但存储到映射盘失败！\n";
				logger.info("*" + describe);
			}
		} catch (Exception e) {
			e.printStackTrace();
			describe += "{失败}-封面文件抽取成功，但上传到远程映射盘失败！" + e.getMessage() + "\n";
			logger.info("#" + describe);
		}
		return describe;
	}
}
