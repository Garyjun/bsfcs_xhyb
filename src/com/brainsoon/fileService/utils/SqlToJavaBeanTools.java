package com.brainsoon.fileService.utils;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

import org.apache.commons.dbcp.BasicDataSource;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * @ClassName: SqlToJavaBeanTools
 * @Description: 根据sql语句自动生成实体类对象工具类
 * @author xiehewei
 * @date 2015年5月15日 下午1:44:22
 *
 */
public class SqlToJavaBeanTools {

	private String tablename = "dofile_history";

	private String[] colnames; // 列名数组

	private String[] colTypes; // 列名类型数组

	private int[] colSizes; // 列名大小数组

	private boolean f_util = false; // 是否需要导入包java.util.*

	private boolean f_sql = false; // 是否需要导入包java.sql.*

	public void GenEntityTool() {
		// Connection conn = DBSession.getConnection(); // 得到数据库连接
//		ApplicationContext context = new ClassPathXmlApplicationContext(
//				"applicationContext.xml");
//		BasicDataSource dataSource = (BasicDataSource) context
//				.getBean("dataSource");
//		SQLiteDBUtil util = new SQLiteDBUtil();
//		util.setDataSource(dataSource);
//		Connection conn = util.getConnection();
//		String strsql = "select * from " + tablename;
//		try {
//			PreparedStatement pstmt = conn.prepareStatement(strsql);
//			ResultSetMetaData rsmd = pstmt.getMetaData();
//			int size = rsmd.getColumnCount(); // 共有多少列
//			colnames = new String[size];
//			colTypes = new String[size];
//			colSizes = new int[size];
//			for (int i = 0; i < rsmd.getColumnCount(); i++) {
//				colnames[i] = initcap(rsmd.getColumnName(i + 1));
//				String colFirst = colnames[i].substring(0, 1).toLowerCase();
//				colnames[i] = colFirst
//						+ colnames[i].substring(1, colnames[i].length());
//				colTypes[i] = rsmd.getColumnTypeName(i + 1);
//				if (colTypes[i].equalsIgnoreCase("datetime")) {
//					f_util = true;
//				}
//				if (colTypes[i].equalsIgnoreCase("image")
//						|| colTypes[i].equalsIgnoreCase("text")) {
//					f_sql = true;
//				}
//				colSizes[i] = rsmd.getColumnDisplaySize(i + 1);
//			}
//			String content = parse(colnames, colTypes, colSizes);
//			try {
//				FileWriter fw = new FileWriter(initcap(tablename) + ".java");
//				PrintWriter pw = new PrintWriter(fw);
//				pw.println(content);
//				pw.flush();
//				pw.close();
//			} catch (IOException e) {
//				e.printStackTrace();
//			}
//		} catch (SQLException e) {
//			e.printStackTrace();
//		} finally {
//			// DBSession.closeConnection(conn);
//			try {
//				conn.close();
//			} catch (SQLException e) {
//				e.printStackTrace();
//			}
//		}
	}

	/**
	 * 解析处理(生成实体类主体代码)
	 */
	private String parse(String[] colNames, String[] colTypes, int[] colSizes) {
		StringBuffer sb = new StringBuffer();
		if (f_util) {
			sb.append("import java.util.Date;\r\n");
		}
		if (f_sql) {
			sb.append("import java.sql.*;\r\n\r\n\r\n");
		}

		sb.append("import java.io.Serializable;\r\n\r\n\r\n");

		sb.append("public class " + initcap(tablename)
				+ " implements Serializable" + " {\r\n");
		sb.append("\r\n");
		processAllAttrs(sb);
		processAllMethod(sb);
		sb.append("}\r\n");
		System.out.println(sb.toString());
		return sb.toString();

	}

	/**
	 * 生成所有的方法
	 * 
	 * @param sb
	 */
	private void processAllMethod(StringBuffer sb) {
		for (int i = 0; i < colnames.length; i++) {
			// sb.append("\r\n");
			sb.append("\tpublic void set" + initcap(colnames[i]) + " ("
					+ sqlType2JavaType(colTypes[i]) + " " + colnames[i]
					+ ") {\r\n");
			sb.append("\t\tthis." + colnames[i] + " = " + colnames[i] + ";\r\n");
			sb.append("\t}\r\n");
			sb.append("\r\n");
			sb.append("\tpublic " + sqlType2JavaType(colTypes[i]) + " get"
					+ initcap(colnames[i]) + "(){\r\n");
			sb.append("\t\treturn " + colnames[i] + ";\r\n");
			sb.append("\t}\r\n");
			sb.append("\r\n");
		}
	}

	/**
	 * 解析输出属性
	 * 
	 * @return
	 */
	private void processAllAttrs(StringBuffer sb) {
		for (int i = 0; i < colnames.length; i++) {
			sb.append("\tprivate " + sqlType2JavaType(colTypes[i]) + " "
					+ colnames[i] + ";\r\n");
			sb.append("\r\n");

		}
	}

	/**
	 * 把输入字符串的首字母改成大写
	 * 
	 * @param str
	 * @return
	 */
	private String initcap(String str) {
		char[] ch = str.toCharArray();
		if (ch[0] >= 'a' && ch[0] <= 'z') {
			ch[0] = (char) (ch[0] - 32);
		}
		String[] arrs = new String(ch).split("_");
		String newStr = "";
		for (int i = 0; i < arrs.length; i++) {
			char[] chs = arrs[i].toCharArray();
			if (i > 0) {
				if (chs[0] >= 'a' && chs[0] <= 'z') {
					chs[0] = (char) (chs[0] - 32);
				}
			}
			newStr += new String(chs);
		}
		// System.out.println(newStr);
		return newStr;
	}

	private String sqlType2JavaType(String sqlType) {
		if (sqlType.equalsIgnoreCase("bit")) {
			return "bool";
		} else if (sqlType.equalsIgnoreCase("tinyint")) {
			return "byte";
		} else if (sqlType.equalsIgnoreCase("smallint")) {
			return "short";
		} else if (sqlType.equalsIgnoreCase("int")) {
			return "int";
		} else if (sqlType.equalsIgnoreCase("bigint")) {
			return "long";
		} else if (sqlType.equalsIgnoreCase("float")) {
			return "float";
		} else if (sqlType.equalsIgnoreCase("decimal")
				|| sqlType.equalsIgnoreCase("numeric")
				|| sqlType.equalsIgnoreCase("real")) {
			return "double";
		} else if (sqlType.equalsIgnoreCase("money")
				|| sqlType.equalsIgnoreCase("smallmoney")) {
			return "double";
		} else if (sqlType.equalsIgnoreCase("varchar")
				|| sqlType.equalsIgnoreCase("char")
				|| sqlType.equalsIgnoreCase("nvarchar")
				|| sqlType.equalsIgnoreCase("nchar")) {
			return "String";
		} else if (sqlType.equalsIgnoreCase("datetime")) {
			return "Date";
		}

		else if (sqlType.equalsIgnoreCase("image")) {
			return "Blob";
		} else if (sqlType.equalsIgnoreCase("text")) {
			return "Clob";
		} else if (sqlType.equalsIgnoreCase("Integer")) {
			return "Long";
		}
		return null;
	}

	public static void main(String[] args) {
		String dofile_history_sql = "create table if not exists dofile_history("
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
				+ "timestamp VARCHAR(50) NULL, "
				+ "platformId INT(11) NULL, "
				+ "priority TINYINT(2) NULL, "
				+ "createTime DATETIME NULL, "
				+ "updateTime DATETIME NULL);";
		// Vector vec = getDataObject(null, dofile_history_sql, null);
		// Iterator<T> it = vec.iterator();
		// while (it.hasNext()) {
		// System.out.println(it.next());
		// }
		new SqlToJavaBeanTools().GenEntityTool();
	}
}
