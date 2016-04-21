package com.brainsoon.fileService.service.impl;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.brainsoon.command.CompLaunch;
import com.brainsoon.common.util.JSONConvertor;
import com.brainsoon.common.util.dofile.util.PropertiesReader;
import com.brainsoon.fileService.po.DoFileQueue;
import com.brainsoon.fileService.service.IFileService;
import com.brainsoon.fileService.thread.ProcessFilePC;
import com.brainsoon.fileService.utils.SQLiteDBUtil;
import com.channelsoft.appframe.utils.BeanFactoryUtil;
import com.channelsoft.appframe.utils.WebappConfigUtil;
/**
 *
 * @ClassName: FileService
 * @Description: 文件待转换任务队列相关处理
 * @author tanghui
 * @date 2014-6-11 下午6:14:13
 *
 */
public class FileService implements
IFileService {
	
	private ProcessFilePC processFilePC = ProcessFilePC.getInstance();
	private static Logger logger = Logger.getLogger(CompLaunch.class.getName());


	public void addDoFileQueue(DoFileQueue doFileQueue) throws Exception {
		SQLiteDBUtil.addDoFileQueue(doFileQueue);
	}
	
	public String saveDoFileQueueToDB(DoFileQueue doFileQueue){
		SQLiteDBUtil util = (SQLiteDBUtil) BeanFactoryUtil.getBean("sQLiteDBUtil");
		String sql = "";
		util.delete(sql);
		return "";
	}
	
	public String getTask(){
		List<DoFileQueue> list = new ArrayList<DoFileQueue>();
		for(int i=0; i<20; i++){
			DoFileQueue doFileQueue = processFilePC.popFile();
			if(doFileQueue!=null)
				list.add(doFileQueue);
		}
		return JSONConvertor.list2Json(list);
	}

	@Override
	/**
	 * 
	 * @Title: checkFileCanPreview 
	 * @Description: 校验文件是否可以预览
	 * @param  filePath 源文件路径 （相对）
	 * @return num 文件状态 
	 * 0：待转换 
	 * 1：转换中 
	 * 2：转换成功 
	 * 3：转换失败
	 * 4：无需转换
	 * 5：文件未进入待转换队列，写入出错
	 * 6：传入的文件路径为空
	 * 7：文件不存在
	 * 8：无法识别文件扩展名
	 * 具体类型参照：ViewStatus 枚举类
	 * @throws
	 */	
	public String checkFileIsExsit(String filePath) {
		JSONObject json = new JSONObject();
		SQLiteDBUtil sqliteDBUtil = new SQLiteDBUtil();
		String sql = "select * from dofile_queue where srcPath = '"
				+ filePath + "'";
		List<DoFileQueue> list = sqliteDBUtil.query(sql);
		if(list!=null&&list.size()>0){
			json.put("status", 0);
			return json.toString();
		}
		
		String sqlHistory =  "select * from dofile_history where srcPath = '"
				+ filePath + "'";
		List<DoFileQueue> historyList = sqliteDBUtil.query(sqlHistory);
		if(historyList!=null&&historyList.size()>0){
			DoFileQueue doFileQueue = historyList.get(0);
			if(doFileQueue.getStatusConvered()==1){
				json.put("status", 2);
			}else{
				json.put("status", 3);
			}
			return json.toString();			
		}
		
		json.put("status", 5);
		return json.toString();
	}
	
	/**
	 * 更新任务状态
	 * @param args
	 */
	public void updateDoFileQueue(DoFileQueue doFileQueue){
		String result = doFileQueue.getActionConveredfileUrl();
		SQLiteDBUtil sqliteDBUtil = new SQLiteDBUtil();
		sqliteDBUtil.writeTaskHistory(result, doFileQueue);
	}
	
	/**
	 * 删除任务记录
	 * @param args
	 */
	public void deleteDoFileQueue(DoFileQueue doFileQueue){
		SQLiteDBUtil sqliteDBUtil = new SQLiteDBUtil();
		sqliteDBUtil.deleteTask(doFileQueue);
	}
	

	/**
	 * 对表数据处理借口
	 * type：deleteById 根据id删除dofile_queue表中数据  	value：传入Id值(多个以逗号隔开)
	 * 		deleteTable 清空表数据                                                            	value：传入表名（dofile_queue，dofile_history）
	 * @param type
	 * @param value
	 * @return
	 */
	public String dataProcess(String type,String value) {
		String result = "SUCCESS";
		//String result = "FAILURE";
		//根据id： 删除dofile_queue表中数据
		if (StringUtils.isNotBlank(type) && "deleteById".equals(type)) {
			if (StringUtils.isNotBlank(value)) {
				String[] ids = value.split(",");
				for (int i = 0; i < ids.length; i++) {
					String sql = "delete from dofile_queue where id = "+ Long.parseLong(ids[i]);
					Connection conn = SQLiteDBUtil.getConnection();
					int flag = 0;
					try {
						Statement stat = conn.createStatement();
						flag = stat.executeUpdate(sql);
					} catch (SQLException e) {
						result = "FAILURE";
						e.printStackTrace();
					}
					//根据受影响的记录判断语句是否执行成功
					if (flag <= 0) {
						result = "FAILURE";
					}
					//System.out.println("---根据id： "+value+" 删除dofile_queue表中数据---"+sql+"  受影响数据: "+flag+" row(s) affected");
					logger.info("---根据id： "+ids[i]+" 删除dofile_queue表中数据---"+sql+"  受影响数据: "+flag+" row(s) affected");
				}
			}
			
		//清空表 value数据
		}else if (StringUtils.isNotBlank(type) && "deleteTable".equals(type)) {
			if (StringUtils.isNotBlank(value)) {
				String sql = "delete from "+value;
				Connection conn = SQLiteDBUtil.getConnection();
				int flag = 0;
				try {
					Statement stat = conn.createStatement();
					flag = stat.executeUpdate(sql);
				} catch (SQLException e) {
					result = "FAILURE";
					e.printStackTrace();
				}
				//根据受影响的记录判断语句是否执行成功
				if (flag <= 0) {
					result = "FAILURE";
				}
				//System.out.println("---清空表 "+value+"数据---"+sql+"  受影响数据: "+flag+" row(s) affected");
				logger.info("---清空表 "+value+"数据---"+sql+"  受影响数据: "+flag+" row(s) affected");
			}
		}
		return result;
	}

	@Override
	public void isCreatTable() {
		
		String table = "";
		for (int i = 0; i < 2; i++) {
			if (i==0) {
				table = "dofile_queue";
			}else {
				table = "dofile_history";
			}
			
			String dofile_history_sql = "create table if not exists "+table+"("
					+ "id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, "
					+ "resId VARCHAR(100) NULL, "
					+ "fileId VARCHAR(100) NULL, "
					+ "srcPath VARCHAR(2000) NULL, "
					+ "file_format VARCHAR(20) NULL, "
					+ "pending_type VARCHAR(20) NULL, "
					+ "stauts_extract_matedata TINYINT(2) NULL, "
					+ "status_convered TINYINT(2) NULL, "
					+ "stauts_extract_image TINYINT(2) NULL, "
					+ "stauts_extract_txt TINYINT(2) NULL, "
					+ "result_converedfile_path VARCHAR(2000)  NULL, "
					+ "result_txt VARCHAR(2000) NULL, "
					+ "result_matedata VARCHAR(2000) NULL, "
					+ "result_image_path  VARCHAR(1000) NULL, "
					+ "action_converedfile_url VARCHAR(500) NULL, "
					+ "action_txt_url VARCHAR(500) NULL, "
					+ "action_matedata_url VARCHAR(500) NULL, "
					+ "action_image_url VARCHAR(2000) NULL, "
					+ "sync_stauts_matedata TINYINT(2) NULL, "
					+ "sync_stauts_convered TINYINT(2) NULL, "
					+ "sync_stauts_image TINYINT(2) NULL, "
					+ "sync_stauts_txt TINYINT(2) NULL, "
					+ "retryNum INT(11) NULL, "
					+ "describes VARCHAR(2000) NULL, "
					+ "timesflag VARCHAR(50) NULL, "
					+ "platformId INT(11) NULL, "
					+ "priority TINYINT(2) NULL, "
					+ "createTime DATETIME NULL, "
					+ "updateTime DATETIME NULL,"
					+ "imageWH char(20) NULL,"
					+ "txtName char(100) NULL,"
					+ "fontSize char(20) NULL,"
					+ "fontName char(20) NULL,"
					+ "position char(20) NULL,"
					+ "color char(20) NULL,"
					+ "isBold char(20) NULL,"
					+ "alpha char(20) NULL,"
					+ "wmImage char(20) NULL);";
			
			Connection conn = SQLiteDBUtil.getConnection();
			Statement stat = null;
			int flag = 0;
			try {
				stat = conn.createStatement();
				flag = stat.executeUpdate(dofile_history_sql);
			} catch (SQLException e) {
				e.printStackTrace();
			}finally{
				SQLiteDBUtil.closeStatement(stat);
				SQLiteDBUtil.closeConnection(conn);
			}
			//System.out.println("---创建"+table+"---"+dofile_history_sql+"  受影响数据: "+flag+" row(s) affected");
			logger.info("---创建"+table+"---"+dofile_history_sql+"  受影响数据: "+flag+" row(s) affected");
		}
	}
	
	/*public static void main(String[] args){
		new FileService().dataProcess("deleteById","2730");
	}*/

}
 