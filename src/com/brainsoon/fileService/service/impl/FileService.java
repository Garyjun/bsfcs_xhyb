package com.brainsoon.fileService.service.impl;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.brainsoon.command.CompLaunch;
import com.brainsoon.common.service.BaseService;
import com.brainsoon.common.util.JSONConvertor;
import com.brainsoon.common.util.date.DateUtil;
import com.brainsoon.fileService.po.DoFileHistory;
import com.brainsoon.fileService.po.DoFileQueue;
import com.brainsoon.fileService.service.IFileService;
import com.brainsoon.fileService.thread.ProcessFilePC;
import com.brainsoon.fileService.utils.CopyUtil;
/**
 *
 * @ClassName: FileService
 * @Description: 文件待转换任务队列相关处理
 * @author huagnjun
 * @date 2015年9月7日18:19:23
 *
 */
public class FileService extends BaseService implements IFileService {
	
	private ProcessFilePC processFilePC = ProcessFilePC.getInstance();
	private static Logger logger = Logger.getLogger(CompLaunch.class.getName());

	
	/**
	 * 查询数据库中的待转换文件列表
	 */
	public List<DoFileQueue> doQueryAllQueues(){
		String hql = " from DoFileQueue";
		return getBaseDao().query(hql);
	}
	
	
	/**
	 * 添加一条转换文件记录
	 */
	public void addDoFileQueue(DoFileQueue doFileQueue) throws Exception {
		String time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
		doFileQueue.setTimestamp("new");
		doFileQueue.setPlatformId(1);
		doFileQueue.setCreateTime(time);
		getBaseDao().saveOrUpdate(doFileQueue);
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
	public String doCheckFileIsExsit(String filePath) {
		
		JSONObject json = new JSONObject();
		
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("srcPath", filePath);
		String hql = " from DoFileQueue where srcPath=:srcPath";
		List<DoFileQueue> queues = getBaseDao().query(hql, parameters);
		if(queues!=null&&queues.size()>0){
			json.put("status", 0);
			return json.toString();
		}
		
		String hqlHistory = " from DoFileHistory where srcPath=:srcPath";
		List<DoFileHistory> histories = getBaseDao().query(hqlHistory, parameters);
		if (histories != null && histories.size() > 0) {
			DoFileHistory History = histories.get(0);
			if(History.getStatusConvered() == 1){
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
	 * 准备写入历史表
	 * @param args
	 */
	public void updateDoFileQueue(DoFileQueue doFileQueue){
		String result = doFileQueue.getActionConveredfileUrl();
		insertTaskHistory(result,doFileQueue);
	}
	
	/**
	 * 更新转换状态
	 * @param args
	 */
	public void updateStatusConvered(DoFileQueue doFileQueue){
		doFileQueue.setStatusConvered(1);//转换状态更新  已转换
		getBaseDao().saveOrUpdate(doFileQueue);
	}
	
	
	/**
	 * 写入历史表
	 * @param result
	 * @param doFileQueue
	 */
	public void insertTaskHistory(String result,DoFileQueue doFileQueue){
		
		DoFileHistory doFileHistory = new DoFileHistory();
		
		int statusConvered = getStatusConvered(result,doFileQueue.getPendingType());
		int stautsExtractImage = getStautsExtractImage(result,doFileQueue.getPendingType());
		int stautsExtractTxt = getStautsExtractTxt(result,doFileQueue.getPendingType());
		int stautsExtractMatedata = getStautsExtractMatedata(result,doFileQueue.getPendingType());
		String timestamp = System.currentTimeMillis() + "";
		String time = DateUtil.convertDateToString(new Date());
		
		doFileQueue.setStatusConvered(statusConvered);
		doFileQueue.setStautsExtractImage(stautsExtractImage);
		doFileQueue.setStautsExtractMatedata(stautsExtractMatedata);
		doFileQueue.setStautsExtractMatedata(stautsExtractMatedata);
		doFileQueue.setStautsExtractTxt(stautsExtractTxt);
		doFileQueue.setTimestamp(timestamp);
		doFileQueue.setCreateTime(time);
		doFileQueue.setUpdateTime(time);
		
		
		try {
			CopyUtil.Copy(doFileQueue, doFileHistory);
		} catch (Exception e) {
			e.printStackTrace();
		}
		getBaseDao().saveOrUpdate(doFileHistory);
	}
	
	/**
	 * 更新状态
	 * @param result
	 * @param doFileQueue
	 */
	public void updateFailTask(String result, DoFileQueue doFileQueue){
		
		
		int statusConvered = 0;
		int stautsExtractImage = 0;
		int stautsExtractTxt = 0;
		int stautsExtractMatedata = 0;
		
		if(doFileQueue.getPendingType().contains("0")&&result.indexOf("1")==-1)
			statusConvered = 1;
		if(doFileQueue.getPendingType().contains("1")&&result.indexOf("1")==-1)
			stautsExtractImage = 1;
		if(doFileQueue.getPendingType().contains("2")&&result.indexOf("2")==-1)
			stautsExtractTxt = 1;
		if(doFileQueue.getPendingType().contains("3")&&result.indexOf("3")==-1)
			stautsExtractMatedata = 1;
		
		doFileQueue.setStatusConvered(statusConvered);
		doFileQueue.setStautsExtractImage(stautsExtractImage);
		doFileQueue.setStautsExtractMatedata(stautsExtractMatedata);
		doFileQueue.setStautsExtractTxt(stautsExtractTxt);
		
		getBaseDao().update(doFileQueue);
	}
	
	/**
	 * 删除任务
	 * @param doFileQueue
	 */
	
	public void deleteTask(DoFileQueue doFileQueue){
		
		Map<String, String> resultMap = getFileConverStatus(doFileQueue);
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("id", doFileQueue.getId());
		
		Collection<String> coll = resultMap.values();
		String result = coll.toString();
		String hql = "";
		if(result.contains("0")){
			Set<String> keySet = resultMap.keySet();
			Iterator<String> it = keySet.iterator();
			String param = "";
			while(it.hasNext()){
				String key = it.next();
				String value = resultMap.get(key);
				if("conver".equals(key)){
					parameters.put("statusConvered", Integer.valueOf(value));
					param = " statusConvered=:statusConvered";
				}else if("image".equals(key)){
					parameters.put("stautsExtractImage", Integer.valueOf(value));
					param = " stautsExtractImage=:stautsExtractImage";
				}else if("txt".equals(key)){
					parameters.put("stauts_extract_txt", Integer.valueOf(value));
					param = " stautsExtractTxt=:stautsExtractTxt";
				}else if("matedata".equals(key)){
					parameters.put("stautsExtractMatedata", Integer.valueOf(value));
					param = " stautsExtractMatedata=:stautsExtractMatedata";
				}
			}
			hql = "update DoFileQueue set " + param + " where id=:id";
		}else{
			hql = "delete from DoFileQueue where id =:id";
		}
		
		if (StringUtils.isNotBlank(hql)) {
			logger.info("SQLiteDBUtil deleteTask() 打印hql语句:"+hql);
			getBaseDao().executeUpdate(hql, parameters);
		}
	}
	
	/**
	 * 更新时间戳
	 * @param doFileQueue
	 * @throws SQLException
	 */
	public void updateQueueTimestamp(DoFileQueue doFileQueue){
		long timestamp = System.currentTimeMillis();
		doFileQueue.setTimestamp(timestamp+"");
		getBaseDao().saveOrUpdate(doFileQueue);
		
	}
	
	/**
	 * 删除任务记录
	 * @param args
	 */
	public void deleteDoFileQueue(DoFileQueue doFileQueue){
		deleteTask(doFileQueue);
	}
	

	/**
	 * 对表数据处理借口
	 * type：deleteById 根据id删除dofile_queue表中数据  	value：传入Id值(多个以逗号隔开)
	 * 		deleteTable 清空表数据                                                            	value：传入表名（dofile_queue，dofile_history）
	 * @param type
	 * @param value
	 * @return
	 */
	public String doDataProcess(String type,String value) {
		
		Map<String, Object> parameters = new HashMap<String, Object>();
		
		String result = "SUCCESS";
		//String result = "FAILURE";
		//根据id： 删除dofile_queue表中数据
		if (StringUtils.isNotBlank(type) && "deleteById".equals(type)) {
			if (StringUtils.isNotBlank(value)) {
				String[] ids = value.split(",");
				for (int i = 0; i < ids.length; i++) {
					parameters.put("id", ids[i]);
					String hql = "delete from DoFileQueue where id=:id";
					getBaseDao().executeUpdate(hql, parameters);
					logger.info("---根据id： "+ids[i]+" 删除dofile_queue表中数据---"+hql);
				}
			}
			
		//清空表 value数据
		}else if (StringUtils.isNotBlank(type) && "deleteTable".equals(type)) {
			if (StringUtils.isNotBlank(value)) {
				String hql = "delete from "+value;
				getBaseDao().executeUpdate(hql, null);
				logger.info("---清空表 "+value+"数据---"+hql);
			}
		}
		return result;
	}

	@Override
	public void doCreatTable() {
		
		String table = "";
		for (int i = 0; i < 2; i++) {
			if (i==0) {
				table = "dofile_queue";
			}else {
				table = "dofile_history";
			}
			
			String dofile_history_sql = "create table if not exists "+table+"("
					+ "id INTEGER PRIMARY KEY AUTO_INCREMENT NOT NULL, "
					+ "resId VARCHAR(100) NULL, "
					+ "fileId VARCHAR(100) NULL, "
					+ "objectId VARCHAR(100) NULL, "
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
					+ "timestamp VARCHAR(50) NULL, "
					+ "platformId INT(11) NULL, "
					+ "priority TINYINT(2) NULL, "
					+ "createTime VARCHAR(50) NULL, "
					+ "updateTime VARCHAR(50) NULL,"
					+ "imageWH char(20) NULL,"
					+ "txtName char(100) NULL,"
					+ "fontSize char(20) NULL,"
					+ "fontName char(20) NULL,"
					+ "position char(20) NULL,"
					+ "color char(20) NULL,"
					+ "isBold char(20) NULL,"
					+ "alpha char(20) NULL,"
					+ "wmImage char(20) NULL);";
			
			
			getBaseJdbcDao().execute(dofile_history_sql);

			
			logger.info("---创建"+table+"---"+dofile_history_sql);
		}
	}
	
	/*public static void main(String[] args){
		new FileService().dataProcess("deleteById","2730");
	}*/

	private int getStatusConvered(String result, String type){
		if(type.indexOf("0")!=-1&&result.indexOf("0")==-1)
			return 1;
		else
			return 0;
	}
	
	private int getStautsExtractImage(String result, String type){
		if(type.indexOf("1")!=-1&&result.indexOf("1")==-1)
			return 1;
		else
			return 0;
	}	
	
	private int getStautsExtractTxt(String result, String type){
		if(type.indexOf("2")!=-1&&result.indexOf("2")==-1)
			return 1;
		else
			return 0;
	}
	
	private int getStautsExtractMatedata(String result, String type){
		if(type.indexOf("3")!=-1&&result.indexOf("3")==-1)
			return 1;
		else
			return 0;
	}
	
	public static Map<String, String> getFileConverStatus(DoFileQueue doFileQueue){
		// 待处理类型：0，文件转换 1，抽取图片，2抽取文本 3抽元数据
		String pendingType = doFileQueue.getPendingType();
		Map<String, String> map = new HashMap<String, String>();
		if(pendingType.contains("0")){
			int converedStatus = doFileQueue.getStatusConvered();
			if(converedStatus==1){
				map.put("conver", "1");
			}else{
				map.put("conver", "0");
			}
		}
		if(pendingType.contains("1")){
			int imageStatus = doFileQueue.getStautsExtractImage();
			if(imageStatus==1){
				map.put("image", "1");
			}else{
				map.put("image", "0");
			}
		}if(pendingType.contains("2")){
			int txtStatus = doFileQueue.getStautsExtractTxt();
			if(txtStatus==1){
				map.put("txt", "1");
			}else{
				map.put("txt", "0");
			}
		}if(pendingType.contains("3")){
			int matedataStatus = doFileQueue.getStautsExtractMatedata();
			if(matedataStatus==1){
				map.put("matedata", "1");
			}else{
				map.put("matedata", "0");
			}
		}
		return map;
	}
	
}
 