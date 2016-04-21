package com.brainsoon.fileService.thread;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.apache.log4j.Logger;

import com.brainsoon.fileService.po.DoFileQueue;
import com.brainsoon.fileService.service.IFileService;
import com.brainsoon.fileService.utils.SQLiteDBUtil;
import com.channelsoft.appframe.utils.BeanFactoryUtil;

/**
 * @ClassName: ProcessFilePC
 * @Description: 文件处理总类
 * @author xiehewei
 * @date 2015年5月15日 下午3:18:01
 *
 */
public class ProcessFilePC {

	private static final Logger logger = Logger.getLogger(ProcessFilePC.class);
	/** 待转换的文件队列 */
	private static List<DoFileQueue> list = new ArrayList<DoFileQueue>();
	/** 队列中cf对象的最小值,默认为10 */
	int minCFNum = 15;
	
	private static class ContextQueueHolder {
		private static ProcessFilePC processFilePC = new ProcessFilePC();
	}
	
	private ProcessFilePC(){}
	
	public static ProcessFilePC getInstance(){
		return ContextQueueHolder.processFilePC;
	}

	/**
	 * @throws SQLException 
	 * 
	 * @Title: push
	 * @Description: 去数据库中查询待处理的资源
	 * @show 生产方法.
	 * @show 该方法为同步方法，持有方法锁；
	 * @show 首先循环判断满否，满的话使该线程等待，释放同步方法锁，允许消费；
	 * @show 当不满时首先唤醒正在等待的消费方法，但是也只能让其进入就绪状态，
	 * @show 等生产结束释放同步方法锁后消费才能持有该锁进行消费
	 * @param
	 * @return void
	 * @throws
	 */
	public synchronized void pushFile(List<DoFileQueue> cfItem) {
		try {
			while (list.size() >= minCFNum) {
				logger.info("!!!!!!!!!生产满了!!!!!!!!!");
				this.wait(); // 等待
			}
			for (DoFileQueue resConverfileTask : cfItem) {
				if("new".equals(resConverfileTask.getTimestamp()) || needRedoTask(resConverfileTask)){
					//SQLiteDBUtil.changeFileQueueStatus(resConverfileTask);
					getFileService().updateQueueTimestamp(resConverfileTask);
					list.add(resConverfileTask);
				}
			}
			logger.info("生产了：" + cfItem.size() + "个， 当前还有：" + list.size() + "个未处理!");
			notifyAll();// 唤醒等待线程
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (IllegalMonitorStateException e) {
			e.printStackTrace();
		}
		
	}

	/**
	 * 
	 * @Title: pop
	 * @Description: 返回待处理的资源对象
	 * @show 消费方法
	 * @show 该方法为同步方法，持有方法锁
	 * @show 首先循环判断空否，空的话使该线程等待，释放同步方法锁，允许生产；
	 * @show 当不空时首先唤醒正在等待的生产方法，但是也只能让其进入就绪状态
	 * @show 等消费结束释放同步方法锁后生产才能持有该锁进行生产
	 * @param
	 * @return ResReleaseDetail
	 * @throws
	 */
	public synchronized DoFileQueue popFile() {
		DoFileQueue details = null;
		try {
//			while (list.size() == 0) {
//				logger.info("!!!!!!!!!消费光了!!!!!!!!!");
//				this.wait(); // 等待
//			}
			if(list.size()>0){
				for (Iterator<DoFileQueue> it = list.iterator(); it.hasNext();){
					details = (DoFileQueue) it.next();
					list.remove(details);
					break;
				}
				logger.info("消费了：" + details.getClass() + " 当前还剩：" + list.size() + "个未处理!");
			}
			notifyAll();// 唤醒等待线程
		}catch (IllegalMonitorStateException e) {
			e.printStackTrace();
		}
		return details;
	}
	
	public int getTaskNum(){
		return list.size();
	}
	
	private boolean needRedoTask(DoFileQueue resConverfileTask){
		long time = Long.parseLong(resConverfileTask.getTimestamp());
		long now = System.currentTimeMillis();	
		if((now-time)>3600000)
			return true;
		else
			return false;
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
