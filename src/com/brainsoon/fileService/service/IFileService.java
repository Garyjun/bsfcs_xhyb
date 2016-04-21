package com.brainsoon.fileService.service;

import java.sql.SQLException;
import java.util.List;

import com.brainsoon.fileService.po.DoFileQueue;


public interface IFileService {
	
	
	/**
	 * 查询数据库中的待转换文件列表
	 */
	public List<DoFileQueue> doQueryAllQueues();
	
	/**
	 * @throws Exception 
	 * 
	 * @Title: updateResDescription 
	 * @Description: 更新摘要
	 * jsonStr为id的json串
	 * @return String 
	 * @throws
	 */
	public void addDoFileQueue(DoFileQueue doFileQueue) throws Exception;
	
	public String getTask();
	
	public String doCheckFileIsExsit(String filePath);
	
	/**
	 * 更新任务状态
	 * @param args
	 */
	public void updateDoFileQueue(DoFileQueue doFileQueue);
	
	/**
	 * 写入历史表
	 * @param result
	 * @param doFileQueue
	 */
	public void insertTaskHistory(String result,DoFileQueue doFileQueue);
	
	/**
	 * 更新转换状态
	 * @param args
	 */
	public void updateStatusConvered(DoFileQueue doFileQueue);
	
	/**
	 * 更新状态
	 * @param result
	 * @param doFileQueue
	 */
	public void updateFailTask(String result, DoFileQueue doFileQueue);
	
	/**
	 * 更新时间戳
	 * @param doFileQueue
	 * @throws SQLException
	 */
	public void updateQueueTimestamp(DoFileQueue doFileQueue);
	
	/**s
	 * 删除任务
	 * @param doFileQueue
	 */
	
	public void deleteTask(DoFileQueue doFileQueue);
	
	/**
	 * 删除任务记录
	 * @param args
	 */
	public void deleteDoFileQueue(DoFileQueue doFileQueue);

	/**
	 * 对表数据处理借口
	 * type：deleteById 根据id删除dofile_queue表中数据  	value：传入Id值(多个以逗号隔开)
	 * 		deleteTable 清空表数据                                                            	value：传入表名（dofile_queue，dofile_history）
	 * @param type
	 * @param value
	 * @return
	 */
	public String doDataProcess(String type,String value);

	/**
	 * 初始化的时候判断表是否创建  若没有则创建
	 */
	public void doCreatTable();
}
