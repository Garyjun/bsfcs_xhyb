package com.brainsoon.fileService.sca.impl;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import com.brainsoon.common.support.JsonDataObject;
import com.brainsoon.common.util.CommonUtil;
import com.brainsoon.common.util.JSONConvertor;
import com.brainsoon.fileService.po.DoFileQueue;
import com.brainsoon.fileService.po.DoFileQueueList;
import com.brainsoon.fileService.sca.IFileServiceSCA;
import com.brainsoon.fileService.service.IConvertTxtFileService;
import com.brainsoon.fileService.service.IFileService;
import com.channelsoft.appframe.utils.BeanFactoryUtil;

/**
 * 
 * @ClassName: FileServiceSCA 
 * @Description:  文件待转换任务队列相关处理SCA组件服务
 * @author tanghui 
 * @date 2014-10-30 上午11:14:26 
 *
 */
public class FileServiceSCA implements IFileServiceSCA{
	
	/**
	 * 
	 * @Title: updateResDescription 
	 * @Description: 更新摘要
	 * jsonStr为id的json串
	 * @return String 
	 * @throws
	 */
	@POST
    @Path("writeToQueue")
	@Consumes(MediaType.APPLICATION_JSON)
//	public String writeToQueue(@QueryParam("fileQueueJsonStr") String fileQueueJsonStr){
	public String writeToQueue(DoFileQueueList doFileQueueList){
		JsonDataObject result = new JsonDataObject();
		result.setStatus(CommonUtil.SUCCESS);
		result.setMsg(CommonUtil.msgMap.get(CommonUtil.SUCCESS));
		try{
			for(DoFileQueue doFileQueue : doFileQueueList.getDoFileQueueList()){
				getResConverfileTaskService().addDoFileQueue(doFileQueue);
			}
		}catch(Exception e){
			result.setStatus(CommonUtil.EXCEPTION);
			result.setMsg(CommonUtil.msgMap.get(CommonUtil.EXCEPTION));
			e.printStackTrace();			
		}
		return JSONConvertor.bean2Json(result);
	}
	
	/**
	 * 
	 * @Title: updateResDescription 
	 * @Description: 更新摘要
	 * jsonStr为id的json串
	 * @return String 
	 * @throws
	 */
	@GET
    @Path("saveConverStatusToDB")
    @Produces(MediaType.TEXT_PLAIN)
	public String saveConverStatusToDB(@QueryParam("fileName") String fileName){
		//return getResConverfileTaskService().saveConverStatusToDB(fileName);
		return "";
	}
	
	@POST
    @Path("updateDoFileQueue")
	@Consumes(MediaType.APPLICATION_JSON)
	public String updateDoFileQueue(DoFileQueue doFileQueue){
		String result = "0";
		try {
			getResConverfileTaskService().updateDoFileQueue(doFileQueue);
		} catch (Exception e) {
			e.printStackTrace();
			result = "-1";
		}
		return result;
	}
	
	@POST
    @Path("deleteDoFileQueue")
	@Consumes(MediaType.APPLICATION_JSON)
	public String deleteDoFileQueue(DoFileQueue doFileQueue){
		String result = "0";
		try {
			getResConverfileTaskService().deleteDoFileQueue(doFileQueue);
		} catch (Exception e) {
			e.printStackTrace();
			result = "-1";
		}
		return result;
	}
	
	@GET
    @Path("getTask")
	@Consumes(MediaType.APPLICATION_JSON)
	public String getTask(){
		return getResConverfileTaskService().getTask();
	}
	
	/**
	 * 抽取文件文本方法，成功后调用url更新状态
	 */
	@GET
    @Path("convertTxt")
	@Consumes(MediaType.APPLICATION_JSON)
	public void convertTxt(@QueryParam("url") String url){
		getConvertTxtFileService().convertTxt(url);
	}
	
	@GET
    @Path("checkFileIsExsit")
	@Consumes(MediaType.APPLICATION_JSON)
	public String checkFileIsExsit(@QueryParam("filePath") String filePath){
		return getResConverfileTaskService().doCheckFileIsExsit(filePath);
	}	
	
	/**
	 * 对表数据处理借口
	 * type：deleteById 根据id删除dofile_queue表中数据  	value：传入Id值(多个以逗号隔开)
	 * 		deleteTable 清空表数据                                                            	value：传入表名（dofile_queue，dofile_history）
	 * @param type
	 * @param value
	 * @return
	 */
	@GET
    @Path("dataProcess")
	@Consumes(MediaType.APPLICATION_JSON)
	public String dataProcess(@QueryParam("type") String type, @QueryParam("value") String value){
		return getResConverfileTaskService().doDataProcess(type,value);
	}
	
  /**
	* 
	* @Title: pdfProcess
	* @Description: pdf处理接口
	* @param pdfPath	pdf路径	C:/temp/xml/001/9787502039639/pdf/9787502039639（宣传）.pdf
	* @param convertPath	转换后的文件需要存的路径，需在该路径下新建swf和txt目录，分别存放处理后的文件	C:/temp/convert/9787502039639
	* @param swfFormat	sswf代表一个pdf生成一个swf文件，mswf代表一个pdf生成多个swf文件
	* @param hasFileName	生成的swf文件是否包含原文件名 true：9787502039639_1.swf false：1.swf
	* @return    参数
	* @return String    返回类型
	* @throws
	 */
	@GET
    @Path("pdfProcess")
	@Consumes(MediaType.APPLICATION_JSON)
	public String pdfProcess(@QueryParam("objectId") String objectId, @QueryParam("pdfPath") String pdfPath, @QueryParam("convertPath") String convertPath,
			@QueryParam("swfFormat") String swfFormat, @QueryParam("hasFileName") Boolean hasFileName){
		try {
			pdfPath = URLDecoder.decode(pdfPath,"UTF-8");
			convertPath = URLDecoder.decode(convertPath,"UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return getConvertTxtFileService().pdfProcess(objectId,pdfPath,convertPath,swfFormat,hasFileName);
	}
	
	/**
	 * 
	 * @Title: doTIFAndPDF
	 * @Description: 将tif转换为jpg AND 将pdf转换为swf
	 * @param path	C:/temp/xml/001/9787502039639
	 * @return    参数
	 * @return String    返回类型
	 * @throws
	 */
	@GET
	@Path("doTIFAndPDF")
	@Consumes(MediaType.APPLICATION_JSON)
	public String doTIFAndPDF(@QueryParam("path") String path){
		try {
			path = URLDecoder.decode(path,"UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return getConvertTxtFileService().doTIFAndPDF(new File(path));
	}
	
	/**
	 * 
	 * @Title: doPdf2Swf
	 * @Description: 将pdf转换为swf
	 * @param path	C:/temp/xml/001/9787502039639
	 * @return    参数
	 * @return String    返回类型
	 * @throws
	 */
	@GET
	@Path("doPdf2Swf")
	@Consumes(MediaType.APPLICATION_JSON)
	public String doPdf2Swf(@QueryParam("path") String path){
		try {
			path = URLDecoder.decode(path,"UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return getConvertTxtFileService().doPdf2Swf(new File(path));
	}
	/**
	* 
	* @Title: doTif2Min
	* @Description: 获取插图缩略图
	* @param path	C:/temp/xml/001/9787502039639
	* @return    参数
	* @return String    返回类型
	* @throws
	 */
	@GET
    @Path("doTif2Min")
	@Consumes(MediaType.APPLICATION_JSON)
	public String doTif2Min(@QueryParam("path") String path){
		try {
			path = URLDecoder.decode(path,"UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return getConvertTxtFileService().doTif2Min(new File(path));
	}
	
	/**
	 * 抽取文章xml成html
	 * @param path
	 * @return
	 */
	@GET
    @Path("doArticleHtml")
	@Consumes(MediaType.APPLICATION_JSON)
	public String doArticleHtml(@QueryParam("path") String path){
		try {
			path = URLDecoder.decode(path,"UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return getConvertTxtFileService().doHtmlToXml(new File(path));
	}
	
	private IFileService getResConverfileTaskService() {
    	return (IFileService)BeanFactoryUtil.getBean("fileService");
    }
	
	private IConvertTxtFileService getConvertTxtFileService(){
		return (IConvertTxtFileService)BeanFactoryUtil.getBean("convertTxtFileService");
	}
	
}
