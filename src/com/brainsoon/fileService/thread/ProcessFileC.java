package com.brainsoon.fileService.thread;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.brainsoon.common.util.HttpClientUtil;
import com.brainsoon.common.util.JSONConvertor;
import com.brainsoon.common.util.dofile.util.ConstantsDef;
import com.brainsoon.common.util.dofile.util.PropertiesReader;
import com.brainsoon.fileService.component.Context;
import com.brainsoon.fileService.component.ExtractImage;
import com.brainsoon.fileService.component.ExtractMetaData;
import com.brainsoon.fileService.component.ExtractTxt;
import com.brainsoon.fileService.component.FileConver;
import com.brainsoon.fileService.component.IFileProcess;
import com.brainsoon.fileService.po.DoFileQueue;
import com.brainsoon.fileService.service.IFileService;
import com.brainsoon.fileService.utils.SQLiteDBUtil;
import com.channelsoft.appframe.utils.BeanFactoryUtil;

/**
 * @ClassName: ProcessFileC
 * @Description: 消费线程
 * @author xiehewei
 * @date 2015年5月15日下午3:36:43
 *
 */
public class ProcessFileC implements Runnable {

	private static final Logger logger = Logger.getLogger(ProcessFileC.class);
	
	ProcessFilePC processFilePC = ProcessFilePC.getInstance();
	private static final String SERVER = PropertiesReader.getInstance().getProperty(ConstantsDef.server);
	private static final String UPDATEDOFILEADDRESS = PropertiesReader.getInstance().getProperty("UPDATEDOFILEADDRESS");
	private static final String DELETEDOFILEADDRESS = PropertiesReader.getInstance().getProperty("DELETEDOFILEADDRESS");
	private Long orderId;

	/**
	 * 
	 * 消费进程(转换文件)
	 * 
	 */
	@Override
	public void run() {
		while (true) {
			logger.info("消费线程：【" + Thread.currentThread().getName() + "】处理开始...");
			try {
				DoFileQueue task = processFilePC.popFile();
				if(task!=null){
					processByCommandAndFileType(task);
					Thread.sleep(1000);
				}else{
					Thread.sleep(3000);
				}
			} catch (Exception e) {
				logger.error("消费线程：【" + Thread.currentThread().getName() + "】" + "获得任务失败");
				e.printStackTrace();
			}
		}
	}

	private void processByCommandAndFileType(DoFileQueue task) {
		String type = task.getPendingType(); // 待处理类型
		if (!StringUtils.isBlank(type)) {
			String result = getFileProcessResult(type,task);
			processResult(result,task);
		}
	}
	
	private String getFileProcessResult(String type,DoFileQueue task){
		IFileProcess fileProcess = null;
		if (type.indexOf("0") != -1) {// 0，文件转换
			fileProcess = new FileConver();
			logger.info("处理了ID为" + task.getId() + "的任务，任务类型为文件转换");
			Context context = new Context(fileProcess);
			boolean success = context.contextInterface(task);
			type = processedType(success,type,"0");
			
		}
		if (type.indexOf("1") != -1) {// 1，抽取图片
			fileProcess = new ExtractImage();
			logger.info("处理了ID为" + task.getId() + "的任务，任务类型为抽取图片");
			Context context = new Context(fileProcess);
			boolean success = context.contextInterface(task);
			type = processedType(success,type,"1");
		}
		if (type.indexOf("2") != -1) {// 2抽取文本
			fileProcess = new ExtractTxt();
			logger.info("处理了ID为" + task.getId() + "的任务，任务类型为抽取文本");
			Context context = new Context(fileProcess);
			boolean success = context.contextInterface(task);
			type = processedType(success,type,"2");
		}
		if (type.indexOf("3") != -1) {// 3抽元数据
			fileProcess = new ExtractMetaData();
			logger.info("处理了ID为" + task.getId() + "的任务，任务类型为抽元数据");
			Context context = new Context(fileProcess);
			boolean success = context.contextInterface(task);
			type = processedType(success,type,"3");
		}	
		return type;
	}
	
	private String processedType(boolean status,String type,String processType){
		if(status){
			type = type.replaceAll(processType, "");
		}
		return type;
	}
	
	private void processResult(String result, DoFileQueue task){
		//如果是从机，调用http接口更新主机数据库记录
		if("2".equals(SERVER)){
			HttpClientUtil http = new HttpClientUtil();
			task.setActionConveredfileUrl(result);
			String json = JSONConvertor.bean2Json(task);
			String updateStatus = http.postJson(UPDATEDOFILEADDRESS, json);
			String deleteStatus = http.postJson(DELETEDOFILEADDRESS, json);
		}else{
			getFileService().insertTaskHistory(result,task);
			getFileService().updateFailTask(result,task);//更新状态
			getFileService().deleteTask(task);
		}
	}
	

	public Long getOrderId() {
		return orderId;
	}

	public void setOrderId(Long orderId) {
		this.orderId = orderId;
	}
	
	private IFileService getFileService(){
		IFileService fileService = null;
		try {
			fileService = (IFileService) BeanFactoryUtil.getBean("fileService");
		} catch (Exception e) {
			logger.debug("bean['fileService']尚未装载到容器中！");
			e.printStackTrace();
		}
		return fileService;
	}

}
