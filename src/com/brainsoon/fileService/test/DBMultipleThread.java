package com.brainsoon.fileService.test;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.commons.dbcp.BasicDataSource;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.brainsoon.common.service.BaseService;
import com.brainsoon.fileService.utils.SQLiteDBUtil;

/**
 * @ClassName: DBMultipleThread
 * @Description: TODO
 * @author xiehewei
 * @date 2015年5月15日 上午10:18:22
 *
 */
public class DBMultipleThread extends BaseService implements Runnable {

	@Override
	public void run() {
		//  创建一个表tbl1，录入数据
		baseDao.query("drop table if exists tbl1;");
		baseDao.query("create table if not exists tbl1("
				+ "name VARCHAR(20), salary INT, sex CHAR, age INT, favourite VARCHAR(50), subject VARCHAR(40));");// 创建一个表
		for(int i=0;i<100000;i++){
			if(i%2==0){
				String sql = "insert into tbl1 values(\'ZhangSan" + i + "\' ," + 100*i + ", \'M\', " + i + ", \'favourite" + i + "\', \'subject" +i+"\')";
				System.out.println(sql);
				baseDao.query(sql);
			}else{
				baseDao.query("insert into tbl1 values(\'LiSi" + i + "\' ," + 100*i + ", \'F\', " + i + ", \'favourite" + i + "\', \' subject" +i+"\')");
			}
		}
	}

}
