package com.brainsoon.common.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Context;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.springframework.beans.factory.annotation.Autowired;

import com.brainsoon.common.util.CommonUtil;
import com.brainsoon.common.util.ParametersKeyValue;
import com.channelsoft.appframe.dao.IBaseDao;
import com.channelsoft.appframe.dao.query.PageableInfo;
import com.channelsoft.appframe.dao.query.PageableResult;
import com.channelsoft.appframe.dao.query.QueryConditionList;
import com.channelsoft.appframe.exception.ServiceException;
import com.channelsoft.appframe.service.IBaseOperateService;
import com.channelsoft.appframe.service.impl.BaseServiceObject;
import com.channelsoft.appframe.utils.BeanFactoryUtil;


/**
 * 
 * @ClassName: AbstactCRUDService 
 * @Description:  SCA CRUD 抽象类
 * @author tanghui 
 * @date 2013-4-10 下午2:10:49 
 *
 */
@SuppressWarnings("rawtypes")
public class BaseService extends BaseServiceObject{
	protected static  Log logger = LogFactory.getLog(BaseService.class.getClass());
	@Context
	public HttpServletRequest request;
	private Map map = null;
	private PageableInfo pageAbleInfo = new PageableInfo();
	@JsonIgnore
	public String msg;
	@JsonIgnore
	public String status = "0";
	@Autowired
    protected IBaseDao baseDao;
	
	/**
	 * 获取请求中的参数
	 * @return Map
	 */
	@JsonIgnore
	public Map getRequestMap() {
		if(null == map){
			setMap(ParametersKeyValue.getKeyValues(request));
		}
		return getMap();
	}
	
	/**
	 * 获取请求参数
	 * @param arg0
	 * @return String
	 */
	@JsonIgnore
	public String getParameter(String arg0){
		return request.getParameter(arg0);
	}
	
	/**
	 * 
	 * @Title: queryObj 
	 * @Description: 分页查询
	 * @param   origCls 要查询的数据库-vo对象
	 * @param   destClass 要JSON化的-vo对象（可为空，默认就返回数据库对应的vo对象）
	 * @return PageableInfo 
	 * @throws
	 */
	public PageableInfo queryObj(Class origCls,Class destClass,QueryConditionList conditionList) throws Exception {
		if (logger.isDebugEnabled()) {
			logger.debug("进入 SCACRUDService.queryObj()");
		}
		if(conditionList != null && conditionList.getConditionList() != null){
			try {
				if (StringUtils.isNotBlank(getPageAbleInfo().getSort())) {
					conditionList.setSortProperty(getPageAbleInfo().getSort());
					if (StringUtils.equals(getPageAbleInfo().getDir(), "asc")) {
						conditionList.setSortMode(QueryConditionList.SORT_MODE_ASC);
					} else {
						conditionList.setSortMode(QueryConditionList.SORT_MODE_DESC);
					}
				}
				conditionList.setStartIndex(getPageAbleInfo().getStartIndex());
				conditionList.setPageSize(getPageAbleInfo().getPageSize());
				PageableResult pageableResult = query(conditionList,origCls);
				getPageAbleInfo().setTotalRecords(pageableResult.getMaxRowCount());
				List origList = pageableResult.getCurrentPage();
				List destList = null;
				if(destClass != null){
				    destList = new ArrayList();
					for (int i = 0; i < origList.size(); i++) {
						Object destObj = destClass.newInstance();
						BeanUtils.copyProperties(destObj,origList.get(i));
						//destObj = copyProperties(destObj,list.get(i));
						if(destObj != null){
							destList.add(destObj);
						}
					}
				}else{
					destList = origList;
				}
				getPageAbleInfo().setRecords(destList);
			} catch (ServiceException e) {
				logger.debug(e.getMessage());
				e.printStackTrace();
			} catch (Exception exp) {
				logger.debug(exp.getMessage());
				exp.printStackTrace();
			}
			if (logger.isDebugEnabled()) {
				logger.debug("离开 SCACRUDService.queryObj()");
			}
		}
		

		
		return pageAbleInfo;
	}
	
	
	
	/**
	 * 
	 * @Title: queryBigDataObj 
	 * @Description: 分页大数据查询
	 * @param   origCls 要查询的数据库-vo对象
	 * @param   destClass 要JSON化的-vo对象（可为空，默认就返回数据库对应的vo对象）
	 * @return PageableInfo 
	 * @throws
	 */
	public PageableInfo queryBigDataObj(Class cls,Class tarClass,QueryConditionList conditionList) throws Exception {
		if (logger.isDebugEnabled()) {
			logger.debug("进入 SCACRUDService.queryObj()");
		}

		if(conditionList != null && conditionList.getConditionList() != null){
			try {
				if (StringUtils.isNotBlank(getPageAbleInfo().getSort())) {
					conditionList.setSortProperty(getPageAbleInfo().getSort());
					if (StringUtils.equals(getPageAbleInfo().getDir(), "asc")) {
						conditionList.setSortMode(QueryConditionList.SORT_MODE_ASC);
					} else {
						conditionList.setSortMode(QueryConditionList.SORT_MODE_DESC);
					}
				}
	
				conditionList.setStartIndex(getPageAbleInfo().getStartIndex());
				conditionList.setPageSize(getPageAbleInfo().getPageSize());
	
				PageableResult pageableResult = query(conditionList,cls);
				getPageAbleInfo().setTotalRecords(pageableResult.getMaxRowCount());
				List origList = pageableResult.getCurrentPage();
				List destList = null;
				if(tarClass != null){
				    destList = new ArrayList();
					for (int i = 0; i < origList.size(); i++) {
						Object destObj = tarClass.newInstance();
						BeanUtils.copyProperties(destObj,origList.get(i));
						if(destObj != null){
							destList.add(destObj);
						}
					}
				}else{
					destList = origList;
				}
				getPageAbleInfo().setRecords(destList);
			} catch (ServiceException e) {
				logger.debug(e.getMessage());
				e.printStackTrace();
			} catch (Exception exp) {
				logger.debug(exp.getMessage());
				exp.printStackTrace();
			}
	
			if (logger.isDebugEnabled()) {
				logger.debug("离开 SCACRUDService.queryObj()");
			}
		}
		return pageAbleInfo;
	}
	
	
	
	
	/**
	 * 
	 * @Title: setPageAbleInfo 
	 * @Description: 设置分页值
	 * @param   startIndex 开始页
	 * @param   pageSize 每页条数
	 * @param   sort 排序参数
	 * @return void 
	 * @throws
	 */
	public void setPageAbleInfo(String startIndex,String pageSize,String sort) {
		// 验证currentPageNo
		int startIndexNum=0;
		// 验证eachPageNum
		int pageSizeNum = 10;
		// 默认currentPageNo
		int deFaultStartIndexNum=0;
		// 默认eachPageNum
		int deFaultPageSizeNum = 10;
		try {
			startIndexNum = CommonUtil.validateInt("startIndex", false,startIndex, null);
			if(startIndexNum==-1){
				startIndexNum = deFaultStartIndexNum;
			}
			pageSizeNum = CommonUtil.validateInt("pageSize", false, pageSize,null);
			if(pageSizeNum==-1){
				pageSizeNum = deFaultPageSizeNum;
			}
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		getPageAbleInfo().setPageSize(pageSizeNum);
		getPageAbleInfo().setStartIndex(startIndexNum);
		if(StringUtils.isNotBlank(sort)){
			getPageAbleInfo().setSort(sort);
		}
	}
	
	
	
	
	protected void buildQueryCondition(QueryConditionList conditionList,Object obj) {
	}
	
	
	protected PageableResult query(QueryConditionList conditionList,Class cls) {
		return getBaseService().query(cls, conditionList);
	}
	
	
	@JsonIgnore
	protected IBaseOperateService getBaseService() {
		return (IBaseOperateService)BeanFactoryUtil.getBean("baseService");
	}
	
	
	protected PageableResult queryBigData(QueryConditionList conditionList,Class cls) {
		return getBaseService().queryBigData(cls, conditionList);
	}
	
	
	@JsonIgnore
	Map getMap() {
		return map;
	}
	
	
	void setMap(Map map) {
		this.map = map;
	}
	
	
	@JsonIgnore
	public PageableInfo getPageAbleInfo() {
		return pageAbleInfo;
	}
	
	
	public void setPageAbleInfo(PageableInfo pageAbleInfo) {
		this.pageAbleInfo = pageAbleInfo;
	}
	
	
	
	
}
