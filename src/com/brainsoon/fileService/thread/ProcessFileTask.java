package com.brainsoon.fileService.thread;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.brainsoon.common.util.HttpClientUtil;
import com.brainsoon.common.util.JSONConvertor;
import com.brainsoon.common.util.dofile.util.ConstantsDef;
import com.brainsoon.common.util.dofile.util.PropertiesReader;
import com.brainsoon.fileService.po.DoFileQueue;

public class ProcessFileTask implements Runnable {
	
	private static final Logger logger = Logger.getLogger(ProcessFileC.class);
	
	private ProcessFilePC processFilePC = ProcessFilePC.getInstance();
	
	private static final String HOSTADDRESS = PropertiesReader.getInstance().getProperty(ConstantsDef.hostAddress);

	@Override
	public void run() {
		// TODO Auto-generated method stub
		while(true){
			logger.info("从机获取任务线程：【" + Thread.currentThread().getName() + "】处理开始...");
			try {
				if(processFilePC.getTaskNum()<=1){
					HttpClientUtil http = new HttpClientUtil();
					logger.info("hostAddress++++++++++++++" + HOSTADDRESS);
					String array = http.executeGet(HOSTADDRESS);
					if(!StringUtils.isBlank(array)){
						List<DoFileQueue> list = JSONConvertor.json2List(
								array, DoFileQueue.class);
						processFilePC.pushFile(list);
					}
					Thread.sleep(5000);
				}else{
					Thread.sleep(1000*60);
				}
			} catch (Exception e) {
				e.printStackTrace();
				logger.info("获取任务失败...");
			}
		}
	}
}
