package com.brainsoon.fileService.utils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.dbcp.BasicDataSource;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.brainsoon.fileService.po.DoFileQueue;

/**
 * @ClassName: SQLiteDBUtil
 * @Description: sqlite数据库操作工具类
 * @author xiehewei
 * @date 2015年5月14日 下午2:25:47
 *
 */
public class SQLiteDBUtil {
	protected static Log logger = LogFactory.getLog(UpdateFileTool.class);

	/**
	 * 数据源
	 */
	private static BasicDataSource dataSource;
	/**
	 * 数据库连接
	 */
	public static Connection conn;

	/**
	 * 获取数据源
	 * 
	 * @return 数据源
	 */
	public BasicDataSource getDataSource() {
		return dataSource;
	}

	/**
	 * 设置数据源
	 * 
	 * @param dataSource
	 *            数据源
	 */
	public void setDataSource(BasicDataSource dataSource) {
		this.dataSource = dataSource;
	}

	/**
	 * 获取数据库连接
	 * 
	 * @return conn
	 */
	public static Connection getConnection() {
		try {
			conn = dataSource.getConnection();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return conn;
	}

	/**
	 * 关闭数据库连接
	 * 
	 * @param conn
	 */
	public static void closeConnection(Connection conn) {
		if (null != conn) {
			try {
				conn.close();
				conn = null;
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 获取执行SQL的工具
	 * 
	 * @param conn
	 *            数据库连接
	 * @return stmt
	 */
	public static int getFoundRows(Connection conn) {
		Statement stmt = null;
		ResultSet rs = null;
		try {
			stmt = getStatement(conn);
			rs = stmt.executeQuery("SELECT FOUND_ROWS()");
			if (rs.next()) {
				return rs.getInt(1);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			closeStatement(stmt);
			closeResultSet(rs);
		}
		return 0;
	}

	/**
	 * 获取执行SQL的工具
	 * 
	 * @param conn
	 *            数据库连接
	 * @return stmt
	 */
	public static Statement getStatement(Connection conn) {
		Statement stmt = null;
		try {
			stmt = conn.createStatement();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return stmt;
	}

	/**
	 * 获取执行SQL的工具
	 * 
	 * @param conn
	 *            数据库连接
	 * @param sql
	 *            SQL语句
	 * @return prepStmt
	 */
	public static PreparedStatement getPrepStatement(Connection conn, String sql) {
		PreparedStatement prepStmt = null;
		try {
			prepStmt = conn.prepareStatement(sql);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return prepStmt;
	}

	/**
	 * 关闭数据库资源
	 * 
	 * @param stmt
	 */
	public static void closeStatement(Statement stmt) {
		if (null != stmt) {
			try {
				stmt.close();
				stmt = null;
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 关闭数据库资源
	 * 
	 * @param prepStmt
	 */
	public static void closePrepStatement(PreparedStatement prepStmt) {
		if (null != prepStmt) {
			try {
				prepStmt.close();
				prepStmt = null;
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 获取结果集
	 * 
	 * @param stmt
	 *            执行SQL的工具
	 * @param sql
	 *            SQL语句
	 * @return 结果集
	 */
	public static ResultSet getResultSet(Statement stmt, String sql) {
		ResultSet rs = null;
		try {
			rs = stmt.executeQuery(sql);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return rs;
	}

	/**
	 * 关闭数据库资源
	 * 
	 * @param rs
	 */
	public static void closeResultSet(ResultSet rs) {
		if (null != rs) {
			try {
				rs.close();
				rs = null;
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	public static Boolean setAutoCommit(Connection conn, boolean commitStatus) {
		if (conn == null) {
			return true;
		}
		try {
			boolean commit = conn.getAutoCommit();
			conn.setAutoCommit(commitStatus);
			return commit;
		} catch (SQLException e1) {
			e1.printStackTrace();
			return true;
		}
	}

	public static boolean rollback(Connection conn, boolean oldCommitStatus) {
		if (conn == null) {
			return true;
		}
		try {
			conn.rollback(); // 事物回滚
			conn.setAutoCommit(oldCommitStatus);
			return true;
		} catch (SQLException e1) {
			e1.printStackTrace();
			return false;
		}
	}

	public static boolean commit(Connection conn, boolean oldCommitStatus) {
		if (conn == null) {
			return true;
		}
		try {
			conn.commit(); // 事物回滚
			conn.setAutoCommit(oldCommitStatus);
			return true;
		} catch (SQLException e1) {
			e1.printStackTrace();
			return false;
		}
	}

	public static int getLastId(PreparedStatement ps) {
		ResultSet rs = null;
		try {
			rs = ps.getGeneratedKeys();
			if (rs != null && rs.next()) {
				return rs.getInt(1);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			closeResultSet(rs);
		}
		return -1;
	}

	/**
	 * 判断是否是管理员
	 * 
	 * @param conn
	 *            mysql连接
	 * @param ip
	 *            请求ip
	 * @param password
	 *            管理员密码
	 * @author yaofuyuan
	 * @since 2011-08-02 12:58:00
	 * @return void 0：不是,1:是,-1:数据库出错
	 */
	public int isSuperAdmin(Connection conn, String ip, String password) {
		if (conn == null) {
			return -1;
		}
		PreparedStatement ps = getPrepStatement(conn,
				"select count(*) as count from auth_admin_server where ip=? and password=?");
		ResultSet rs = null;
		try {
			// 查询帐号，用户名和密码
			ps.setString(1, ip);
			ps.setString(2, password);
			rs = ps.executeQuery();
			if (rs.next()) {
				if (rs.getInt("count") == 0) {
					// 用户名密码错误
					return 0;
				} else {
					return 1;
				}
			}
			return -1;
		} catch (Exception e) {
			e.printStackTrace();
			return -1;
		} finally {
			closeResultSet(rs);
			closePrepStatement(ps);
		}
	}

	public int test(Connection conn) {
		PreparedStatement pst = getPrepStatement(conn, "select 123");
		// 获取结果集
		ResultSet rs = null;
		try {
			rs = pst.executeQuery();
			if (rs.next()) {
				return rs.getInt(1);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			// 关闭数据库资源
			closeResultSet(rs);
			closePrepStatement(pst);
		}
		return -1;
	}
	
	public void save(String sql){
		Connection conn = getConnection();
		try {
			Statement stat = conn.createStatement();
			stat.executeUpdate(sql);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public boolean delete(String sql){
		Connection conn = getConnection();
		boolean flag = true;
		try {
			Statement stat = conn.createStatement();
			flag = stat.execute(sql);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return flag;
	}
	
	public List<DoFileQueue> query(String sql){
		Connection conn = getConnection();
		List<DoFileQueue> queue = null;
		Statement stat = null;
		ResultSet set = null;
		try {
			stat = conn.createStatement();
			set = stat.executeQuery(sql);
			queue = changeResultSetToDoFileQueue(set);
		} catch (SQLException e) {
			e.printStackTrace();
		} finally{
			closeResultSet(set);
			closeStatement(stat);
			closeConnection(conn);
		}
		return queue;
	}
	
	private List<DoFileQueue> changeResultSetToDoFileQueue(ResultSet set) throws SQLException{
		List<DoFileQueue> queue = new ArrayList<DoFileQueue>();
		while (set.next()){
			DoFileQueue dfq = new DoFileQueue();
			dfq.setId(set.getLong("id"));
			dfq.setResId(set.getString("resId"));
			dfq.setFileId(set.getString("fileId"));
			dfq.setSrcPath(set.getString("srcPath"));
			dfq.setFileFormat(set.getString("file_format"));
			dfq.setPendingType(set.getString("pending_type"));
			dfq.setStatusConvered(set.getInt("status_convered"));
			dfq.setResultConveredfilePath(set.getString("result_converedfile_path"));
			dfq.setActionConveredfileUrl(set.getString("action_converedfile_url"));
			dfq.setActionTxtUrl(set.getString("action_txt_url"));
			dfq.setActionImageUrl(set.getString("action_image_url"));
			dfq.setActionMatedataUrl(set.getString("action_matedata_url"));
			dfq.setRetryNum(set.getInt("retryNum"));
			dfq.setPriority(set.getInt("priority"));
			dfq.setTimestamp(set.getString("timesflag"));
			queue.add(dfq);
		}
		return queue;
	}
	
	public static void addDoFileQueue(DoFileQueue doFileQueue) throws SQLException{
		String createTime = new Date().toString();
		String sql = "insert into dofile_queue values (null,'"
				+ doFileQueue.getResId() + "','" + doFileQueue.getFileId() + "','"
				+ doFileQueue.getSrcPath() + "','" + doFileQueue.getFileFormat() + "',"
				+ doFileQueue.getPendingType() + ",0,0,0,0,'"
				+ doFileQueue.getResultConveredfilePath() + "',"
				+ "null,null,null,null,null,null,null,0,0,0,0,0,null,'new',1,0,'"
				+ createTime + "',null,null,null,null,null,null,null,null,null,null)";
		System.out.println(sql);
		executeSQL(sql);
	}
	
	public static void writeFailTask(String result, DoFileQueue doFileQueue){
		int statusConvered = 0;
		int stautsExtractImage = 0;
		int stautsExtractTxt = 0;
		int stautsExtractMatedata = 0;
		
		if(doFileQueue.getPendingType().contains("0")&&result.indexOf("0")==1)
			statusConvered = 1;
		if(doFileQueue.getPendingType().contains("1")&&result.indexOf("1")==1)
			stautsExtractImage = 1;
		if(doFileQueue.getPendingType().contains("2")&&result.indexOf("2")==1)
			stautsExtractTxt = 1;
		if(doFileQueue.getPendingType().contains("3")&&result.indexOf("3")==1)
			stautsExtractMatedata = 1;
		
		String sql = "update dofile_queue set status_convered = "
				+ statusConvered + ", stauts_extract_image = "
				+ stautsExtractImage + ", stauts_extract_txt = "
				+ stautsExtractTxt + ", stauts_extract_matedata = "
				+ stautsExtractMatedata +" where id = "
				+ doFileQueue.getId();
		executeSQL(sql);
	}
	
	public static void writeTaskHistory(String result,DoFileQueue doFileQueue){
//		int statusConvered = doFileQueue.getPendingType().indexOf("0")==-1?0:1;
//		int stautsExtractImage = doFileQueue.getPendingType().indexOf("1")==-1?0:1;
//		int stautsExtractTxt = doFileQueue.getPendingType().indexOf("2")==-1?0:1;
//		int stautsExtractMatedata = doFileQueue.getPendingType().indexOf("3")==-1?0:1;
		int statusConvered = getStatusConvered(result,doFileQueue.getPendingType());
		int stautsExtractImage = getStautsExtractImage(result,doFileQueue.getPendingType());
		int stautsExtractTxt = getStautsExtractTxt(result,doFileQueue.getPendingType());
		int stautsExtractMatedata = getStautsExtractMatedata(result,doFileQueue.getPendingType());
		String timestap = System.currentTimeMillis() + "";
		String time = new Date().toString();
		
		String sql = "insert into dofile_history values(null,'"
				+ doFileQueue.getResId() + "','" + doFileQueue.getFileId()
				+ "','" + doFileQueue.getSrcPath() + "','" + doFileQueue.getFileFormat()
				+ "','" + doFileQueue.getPendingType() + "'," + stautsExtractMatedata
				+ "," + statusConvered + "," + stautsExtractImage
				+ "," + stautsExtractTxt + ",'" + doFileQueue.getResultConveredfilePath()
				+ "','" + doFileQueue.getResultTxt() + "','" + doFileQueue.getResultMatedata()
				+ "','" + doFileQueue.getResultImagePath() + "','" + doFileQueue.getActionConveredfileUrl()
				+ "','" + doFileQueue.getActionTxtUrl() + "','" + doFileQueue.getActionMatedataUrl()
				+ "','" + doFileQueue.getActionImageUrl() + "'," + doFileQueue.getSyncStautsMatedata()
				+ "," + doFileQueue.getSyncStautsConvered() + "," + doFileQueue.getSyncStautsImage()
				+ "," + doFileQueue.getSyncStautsTxt() + "," + doFileQueue.getRetryNum()
				+ ",'" + doFileQueue.getDescribes() + "','" + timestap
				+ "',1,1,'" + time + "','" + time + "','" + doFileQueue.getImageWH()
				+ "','" + doFileQueue.getTxtName() + "','" + doFileQueue.getFontSize()
				+ "','" + doFileQueue.getFontName() + "','" + doFileQueue.getPosition()
				+ "','" + doFileQueue.getColor() + "','" + doFileQueue.getIsBold()
				+ "','" + doFileQueue.getAlpha() + "','" + doFileQueue.getWmImage()
				+ "')";
		executeSQL(sql);		
	}
	
	private static int getStatusConvered(String result, String type){
		if(type.indexOf("0")!=-1&&result.indexOf("0")==-1)
			return 1;
		else
			return 0;
	}
	
	private static int getStautsExtractImage(String result, String type){
		if(type.indexOf("1")!=-1&&result.indexOf("1")==-1)
			return 1;
		else
			return 0;
	}	
	
	private static int getStautsExtractTxt(String result, String type){
		if(type.indexOf("2")!=-1&&result.indexOf("2")==-1)
			return 1;
		else
			return 0;
	}
	
	private static int getStautsExtractMatedata(String result, String type){
		if(type.indexOf("3")!=-1&&result.indexOf("3")==-1)
			return 1;
		else
			return 0;
	}
	
	public static void deleteTask(DoFileQueue doFileQueue){
		Map<String, String> resultMap = getFileConverStatus(doFileQueue);
		Collection<String> coll = resultMap.values();
		String result = coll.toString();
		String sql = "";
		if(result.contains("0")){
			Set<String> keySet = resultMap.keySet();
			Iterator<String> it = keySet.iterator();
			String param = "";
			while(it.hasNext()){
				String key = it.next();
				String value = resultMap.get(key);
				if("conver".equals(key)){
					param = "status_convered=" + Integer.valueOf(value);
				}else if("image".equals(key)){
					param = "stauts_extract_image=" + Integer.valueOf(value);
				}else if("txt".equals(key)){
					param = "stauts_extract_txt=" + Integer.valueOf(value);
				}else if("matedata".equals(key)){
					param = "stauts_extract_matedata=" + Integer.valueOf(value);
				}
			}
			sql = "update dofile_queue set " + param + " where id =" + doFileQueue.getId();
		}else{
			sql = "delete from dofile_queue where id =" + doFileQueue.getId();
		}
		
		if (StringUtils.isNotBlank(sql)) {
			logger.info("SQLiteDBUtil deleteTask() 打印sql语句:"+sql);
			executeSQL(sql);
		}
		
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
	
	public static void changeFileQueueStatus(DoFileQueue doFileQueue) throws SQLException{
		long timestap = System.currentTimeMillis();
		String sql = "update dofile_queue set timesflag = '"
				+ timestap + "' where id =" + doFileQueue.getId();
		executeSQL(sql);
	}
	
	public static void main(String[] args) {
		Connection conn = null;
		Statement stat = null;
		try {
			ApplicationContext context = new ClassPathXmlApplicationContext("applicationContext.xml");
			conn = SQLiteDBUtil.getConnection(); 
			stat = conn.createStatement();

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			closeStatement(stat);
			closeConnection(conn);			
		}
	}
	
	public static void executeSQL(String sql){
		Connection conn = null;
		Statement stat = null;
		try {
			conn = SQLiteDBUtil.getConnection();
			stat = conn.createStatement();
			stat.executeUpdate(sql);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			closeStatement(stat);
			closeConnection(conn);			
		}		
	}
}
