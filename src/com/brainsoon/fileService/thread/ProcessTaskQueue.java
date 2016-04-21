package com.brainsoon.fileService.thread;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import com.brainsoon.fileService.po.DoFileQueue;

/**
 * @ClassName: ProcessTaskQueue
 * @Description: 文件处理队列
 * @author xiehewei
 * @date 2015年5月15日 下午3:18:01
 *
 */
public class ProcessTaskQueue {

	private BlockingQueue<DoFileQueue> queue;	
	private int maxQueueSize = 5;
	
	
	private static class ContextQueueHolder {
		private static ProcessTaskQueue instance = buildSingleton();
		
		private static ProcessTaskQueue buildSingleton() {			
			return new ProcessTaskQueue();
		}
	}

	public static ProcessTaskQueue getInst() {
		return ContextQueueHolder.instance;
	}
	
	private ProcessTaskQueue() {
		this.queue = new LinkedBlockingQueue<DoFileQueue>(maxQueueSize);
	}
	
	/**
	 * 添加队列
	 * @param message
	 */
	public void addMessage(DoFileQueue message) {
		queue.offer(message);
	}
	
	/**
	 * 获取队列
	 * @return
	 * @throws InterruptedException
	 */
	public DoFileQueue getMessage()
		throws InterruptedException {
		DoFileQueue info = queue.take(); //take为线程阻塞方法
		return info;
	}
	
	public int size() {
		return queue.size();
	}
	
	public void setMaxQueueSize(int maxQueueSize) {
		this.maxQueueSize = maxQueueSize;
	}
	
	public int getMaxQueueSize() {
		return maxQueueSize;
	}
}
