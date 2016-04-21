package com.brainsoon.common.support;

import java.util.LinkedHashMap;
import java.util.List;
import org.apache.log4j.Logger;
import com.channelsoft.appframe.service.IBaseOperateService;
import com.channelsoft.appframe.utils.BeanFactoryUtil;



/**
 * 
 * @ClassName: OperDbUtils 
 * @Description: 操作数据库工具类
 * @author tanghui 
 * @date 2013-5-2 下午4:07:35 
 *
 */
public class OperDbUtils {
	private static final Logger logger = Logger.getLogger(OperDbUtils.class);
	private static IBaseOperateService baseQueryService = null;

	/**
	 * 根据key查询参数value
	 * @param indexTag
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static String queryParamValueByKey(String key){
		String paramValue = "";
	    try {
//			baseQueryService = (IBaseOperateService) BeanFactoryUtil.getBean("baseService");
//			String hql = "from SysParameter s where s.paraKey = '" + key + "' and s.paraStatus= 1";
//			List<SysParameter> sysParameters = baseQueryService.query(hql);
//			if(sysParameters != null && sysParameters.size() > 0){
//				paramValue = sysParameters.get(0).getParaValue();
//			}
		} catch (Exception e) {
			// tanghui Auto-generated catch block
			e.printStackTrace();
		}
		return paramValue;
	}
	
	
	/**
	 * 根据key查询参数value
	 * @param indexTag
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static String  queryNameByIndexAndKey(String index,String key){
		String value = "";
	    try {
//	    	IDictNameService dictNameService = (IDictNameService) BeanFactoryUtil.getBean("dictNameService");
//	    	value = dictNameService.getValueNameByIndex(key, index);
		} catch (Exception e) {
			// tanghui Auto-generated catch block
			e.printStackTrace();
		}
		return value;
	}
	/**
	 * 根据value查询参数key
	 * @param indexTag
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static String  queryKeyByIndexAndName(String index,String name){
		String value = "";
	    try {
//	    	IDictNameService dictNameService = (IDictNameService) BeanFactoryUtil.getBean("dictNameService");
//	    	value = dictNameService.getValueKeyByIndex(name, index);
		} catch (Exception e) {
			// tanghui Auto-generated catch block
			e.printStackTrace();
		}
		return value;
	}
	//test
	public static void main(String[] args){
//		BaseOperDbUtils m = new BaseOperDbUtils();
		System.out.println(OperDbUtils.queryParamValueByKey("prod_base_path"));
		
	}
	
	
}
