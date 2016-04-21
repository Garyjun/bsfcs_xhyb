package com.brainsoon.common.util;

import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.Map.Entry;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.mulgara.util.StringUtil;

import com.channelsoft.appframe.dao.query.Operator;
import com.google.gson.Gson;


/**
 * 
 * @ClassName: CommonUtil 
 * @Description:  通用工具类
 * @author tanghui 
 * @date 2013-3-27 下午5:02:51 
 *
 */
public class CommonUtil {
	
	protected static final Logger logger = Logger.getLogger(CommonUtil.class);
	/**
	 * msgMap存储结果码   key=status value=message
	 */
	public static Map<String,String> msgMap=new HashMap<String,String>();
	/**
	 * SUCCESS代表 操作成功 的status状态
	 */
	public static String SUCCESS="0";
	/**
	 * EXCEPTION代表 执行异常 的status状态
	 */
	public static String EXCEPTION="-1";
	/**
	 * PARAMERROR代表 参数不合法 的status状态
	 */
	public static String PARAMERROR="100";
	/**
	 * IDENTIFYERROR代表 认证失败 的status状态
	 */
	public static String IDENTIFYERROR="200";	
	static {
		msgMap.put(SUCCESS, "操作成功");
		msgMap.put(EXCEPTION, "执行异常:");
		msgMap.put(PARAMERROR, "参数不合法:");
		msgMap.put(IDENTIFYERROR, "认证失败");
	}
	
	//错误代码
	private static int ERRORINT=-1;
	
	/**
	 * SUCCESS代表 操作成功 的status状态-有数据
	 */
	public static String  HavaData =" 成功获取到数据.";
	
	
	/**
	 * SUCCESS代表 操作成功 的status状态-无数据
	 */
	public static String  NoData =" 未获取到数据.";
	
	/**
	 * 
	 * @Title: isEqualMapValue 
	 * @Description: 判断map的value和value是否相等
	 * @param   
	 * @return boolean 
	 * @throws
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static boolean isEqualMapValue(Map map,String value){
		boolean boo = false; //默认false
		if(map != null && map.size() > 0){
			if(StringUtils.isNotEmpty(value)){
				Iterator<Entry<String, String>> it = map.entrySet().iterator();  
		        while (it.hasNext()) {  
		            Map.Entry e = (Map.Entry) it.next();  
		            if(e.getValue().equals(value)){
		            	boo = true;
		            	break;
		            }else{
		            	boo = false;
		            }
		        }
			}else{
				logger.error("要比较的值为空.无法比较.");
			}
		}else{
			logger.error("要比较的MAP对象为空.无法比较.");
		}
		return boo;
	}
	
	/**
	 * 
	 * @Title: getKeyByValue 
	 * @Description: 通过Map的value取得key
	 * @param   
	 * @return List 
	 * @throws
	 */
	@SuppressWarnings("rawtypes")
	public static String getKeyByValue(Map<String,String> map, Object value) {
		String key = "";
		Iterator it = map.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry entry = (Entry) it.next();
			Object obj = entry.getValue();
			if (obj != null && String.valueOf(obj).toLowerCase().equals(String.valueOf(value).toLowerCase())) {
				key = (String) entry.getKey();
				break;
			   }
			 }
	      return key;
		}
	
	
	/**
	 * 
	 * @Title: getKeyByValue 
	 * @Description: 通过Map的key取得value
	 * @param   
	 * @return List 
	 * @throws
	 */
	@SuppressWarnings("rawtypes")
	public static String getValueByKey(Map<String,String> map, Object key) {
		String value = "";
		Iterator it = map.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry entry = (Entry) it.next();
			if (String.valueOf(entry.getKey()).toLowerCase().equals(String.valueOf(key).toLowerCase())) {
				value = (String) entry.getValue();
				break;
			   }
			 }
	      return value;
		}
	
	
	/**
	 * 
	 * @Title: appHqlStr 
	 * @Description: 拼接hql语句
	 * @param   hqlstr hql语句
	 * @param   prefix 前缀
	 * @param   filed 字段
	 * @param   filedValue  字段值
	 * @param   operator 操作符
	 * @return StringBuffer 
	 * @throws
	 */
	public static StringBuffer appHqlStr(StringBuffer hqlstr,String  prefix,String filed,String filedValue,String operator){
		if(hqlstr != null){
			//拼接
			if(StringUtils.isNotEmpty(filedValue) && StringUtils.isNotEmpty(filed)){
				if(StringUtils.isNotEmpty(prefix)){
					filed = prefix + "." + filed;
				}
				hqlstr = hqlstr.append(pjOperToFiled(filed, filedValue, operator));
			}
		}else{
			logger.error("StringBuffer对象为空.");
		}
		return hqlstr;
	}
	
	
	//拼接
	public static String pjOperToFiled(String filed,String filedValue,String i){
		String hsqlStr = "";
		if(StringUtils.isNotEmpty(filed)){
			if(StringUtils.isNotEmpty(i)){
				if(i.equals("1")){// = 
					hsqlStr = " " + filed + " = '" + filedValue + "' ";
				}else if(i.equals("2")){// like % 
					hsqlStr = " " + filed + " like '" + filedValue + "%' ";
				}else if(i.equals("3")){// like% 
					hsqlStr = " " + filed + " like '%" + filedValue + "' ";
				}else if(i.equals("4")){// like%% 
					hsqlStr = " " + filed + " like '%" + filedValue + "%' ";
				}else if(i.equals("5")){// > 
					hsqlStr = " " + filed + " >= " + filedValue + " ";
				}else if(i.equals("6")){// < 
					hsqlStr = " " + filed + " <= " + filedValue + " ";
				}else if(i.equals("7")){// between --> filedValue 形如：1,6
					if(filedValue.indexOf(",") != -1){
						String[] strs = filedValue.split(",");
						String str1 = strs[0];
						String str2 = strs[1];
						hsqlStr = " " + filed + " between " + str1 + " and " + str2 + " ";
					}else{
						logger.error("传入的between值格式不正确,必须形如：【x,y】.");
					}
				}else if(i.equals("8")){// filedValue 形如：1,6
					if(filedValue.indexOf(",") != -1){
						String[] strs = filedValue.split(",");
						String str1 = strs[0];
						String str2 = strs[1];
						hsqlStr = " " + filed + " >= " + str1 + " and "+ filed + " <= " + str2 + " ";
					}else{
						logger.error("传入的between值格式不正确,必须形如：【x,y】.");
					}
				}
			}else{
				hsqlStr = " " + filed + " = " + filedValue + " ";
			}
		}
		return hsqlStr;
	}
	
	
	
	   //拼接
	   public static Operator operator(String operVlaue){
			Operator operator = Operator.EQUAL;
			if(StringUtils.isNotEmpty(operVlaue)){
				if(operVlaue.equals("1")){// = 
					operator = Operator.EQUAL;
				}else if(operVlaue.equals("2")){// like % 
					operator = Operator.LIKE;
				}else if(operVlaue.equals("3")){// like% 
					operator = Operator.LIKE;
				}else if(operVlaue.equals("4")){// like%% 
					operator = Operator.LIKEANYWHERE;
				}else if(operVlaue.equals("5")){// >= 
					operator = Operator.GE;
				}else if(operVlaue.equals("6")){// <= 
					operator = Operator.LE;
				}else if(operVlaue.equals("7")){// between --> operVlaue 形如：1,6
					operator = Operator.IN;
				}else if(operVlaue.equals("8")){// operVlaue 形如：1,6
					operator = Operator.NOTIN;
				}
			}
			return operator;
		}
	   
	   
	   
	
	/**
	 * 根据提供的文件名称加载properties文件
	 * @param fileName
	 * @return
	 * @throws Exception 
	 */
	public static Properties loadProperties(String filePath) throws Exception{
		Properties pt=null;
		if(!StringUtil.isEmpty(filePath)){
			pt = new Properties();
        	InputStream in =pt.getClass().getResourceAsStream(filePath);
    		try {
    			pt.load(in);
    		} catch (IOException e) {
    			pt=null;
    			e.printStackTrace();
    		}finally{
    			try {
    				if(in!=null){
    					in.close();
    				}else{
    					throw new Exception(filePath+"文件加载错误");
    				}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
    		}
		}
		return pt;
	}
	
	
		
		
		/**
		 * 将返回JsonDataObject<?>类型的对象转成json串  
		 * @param obj
		 * @return 如果obj=null return null
		 */
		public static  String generateJsonString(Object obj){
			if(obj!=null){
				Gson gson = new Gson();
				return gson.toJson(obj);
			}
			return null;		
		}

		
		/**
		 * 验证String类型的参数可以转换为日期
		 * @param paramName 参数名称
		 * @param canBeNull 是否可以为空  true可为空  false不能为空
		 * @param value 参数值
		 * @param limitValues 限制条件 参数值取值范围 limitValues=null则不加限制
		 * @throws Exception
		 */
		public static  Date validateDate(String paramName,boolean canBeNull,String value,String[] limitValues) throws Exception{
			Date date=null;
			if(paramOk(paramName,canBeNull,value,limitValues)){
				if(!StringUtil.isEmpty(paramName)&&!StringUtil.isEmpty(value)){
					try{
						 SimpleDateFormat sdf = new SimpleDateFormat( "yyyy-MM-dd" );
						 date=sdf.parse(value);
					}catch(ParseException e){
						logger.error("参数 "+paramName+" 必须为yyyy-MM-dd类型");
					}
					
				}
			}	
			return date;
		}
		
		/**
		 * 验证String类型的参数可以转换为整数  paramName不能为空
		 * @param paramName NOT NULL参数名称
		 * @param canBeNull 是否可以为空  true可为空  false不能为空
		 * @param value 参数的值
		 * @param set 参数值的取值范围 用字符串表示的存放在Set中  如果没有限制 则set为null
		 * @throws Exception
		 */
		public static int validateInt(String paramName,boolean canBeNull,String value,String[] limitValues) throws Exception{
			int number=ERRORINT;
			if(paramOk(paramName,canBeNull,value,limitValues)){
				if(!StringUtil.isEmpty(paramName)&&!StringUtil.isEmpty(value)){
					try{
						number=Integer.parseInt(value);
					}catch(NumberFormatException e){
						logger.error("参数 "+paramName+" 必须为整型数字类型");
						//throw new Exception();
					}				
				}
			}	
			return number;
		}
		
		/////////////////////
		/**
		 * 验证String类型的参数可以转换为long类型
		 * @param paramName
		 * @param canBeNull 是否可以为空 true可以为空  false不能为空
		 * @param value 参数值
		 * @param limitValues 参数取值范围    如果limitValues=null则不加限制
		 * @return
		 * @throws Exception 
		 */
		public static long validateLong(String paramName, boolean canBeNull, String value,String[] limitValues) throws Exception {
			long number=ERRORINT;
			if(paramOk(paramName,canBeNull,value,limitValues)){
				if(!StringUtil.isEmpty(paramName)&&!StringUtil.isEmpty(value)){
					try{
						number=Long.parseLong(value);
					}catch(NumberFormatException e){
						logger.error("参数 "+paramName+" 转换成长整型long失败!!!!!!!");
					}			
				}
			}	
			return number;
		}
		/**
		 * 判断参数是否为空的合法性以及参数是否符合限制条件的合法性 
		 * @param paramName
		 * @param canBeNull
		 * @param value
		 * @param limitValues 限制条件
		 * @return true表示合法  异常则不合法网上抛异常
		 * @throws Exception
		 */
		private static boolean paramOk(String paramName, boolean canBeNull, String value,String[] limitValues) throws Exception{
			if(!canBeNull){
				//paramName参数不能为空
				if(StringUtil.isEmpty(value)){
					logger.error("参数 "+paramName+" 不能为空!!");
					
				}
			}
			if(!StringUtil.isEmpty(paramName)&&!StringUtil.isEmpty(value)){
				if(limitValues!=null){
					//如果对参数值有约束
					if(!valueInLimitValues(value,limitValues)){
						//如果参数值不在约束范围内 抛出异常
						logger.error("参数 "+paramName+" 只能取 "+Arrays.deepToString(limitValues)+"中的一个!!!");
					}				
				}
			}
			
			return true;
		}
		
		/**
		 * 判断value值是否存在于数组limitValues中 尽量保证 两个参数不为空 如果俩参数都为空 返回false
		 * @param value
		 * @param limitValues
		 * @return 存在返回true  不存在返回false
		 */
		private static boolean valueInLimitValues(String value,String[] limitValues){
			if(!StringUtil.isEmpty(value) && limitValues!=null){
				for(int i=0;i<limitValues.length;i++){
					if(value.equals(limitValues[i])){
						return true;
					}
				}
			}	
			return false;
		}
		
		
}
