package com.brainsoon.fileService.thread;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.Statement;
import java.util.List;

import org.apache.log4j.Logger;

import com.brainsoon.common.support.GlobalAppCacheMap;
import com.brainsoon.fileService.po.DoFileQueue;
import com.brainsoon.fileService.service.IFileService;
import com.brainsoon.fileService.utils.SQLiteDBUtil;
import com.channelsoft.appframe.utils.BeanFactoryUtil;

/**
 * @ClassName: ProcessFileP
 * @Description: 生成线程
 * @author xiehewei
 * @date 2015年5月15日下午3:31:48
 *
 */
public class ProcessFileP implements Runnable {

	private static final Logger logger = Logger.getLogger(ProcessFileP.class);
	
	ProcessFilePC processFilePC = ProcessFilePC.getInstance();
	
	private Long releaseId;

	/**
	 * 生产进程(查询数据库中的待转换文件列表)
	 */
	public void run() {
		logger.info("生产线程开始！");
		while(true){
			//等待一会
			try {
				List<DoFileQueue> cf = getFileService().doQueryAllQueues();
				
				if(cf != null && cf.size() > 0){
					processFilePC.pushFile(cf);
					//移除生产者KEY
				}else{
					Thread.sleep(10000);
					logger.info("目前没有任务，休息一会。。。");
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
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
