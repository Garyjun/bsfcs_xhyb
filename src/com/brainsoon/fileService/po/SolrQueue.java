package com.brainsoon.fileService.po;

import java.io.Serializable;
import java.sql.Date;

import com.channelsoft.appframe.po.BaseHibernateObject;

/**
 * 
* @ClassName: SolrQueue
* @Description: 处理索引表solr_queue
* @author brainsoon
* @date 2015年12月14日
*
 */
public class SolrQueue extends BaseHibernateObject implements Serializable {
	
	private static final long serialVersionUID = -5756421881459958515L;
	
	//主键
	private Long id;
	 //资源标识
	private String resId;
	 //索引动作(0创建索引，1删除索引)
	private String actions;
	 //资源对象索引状态(0索引未创建,1索引已创建,2创建索引失败,5表示状态转换完毕)
	private String status;
	 //创建时间
	private Date createTime;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getResId() {
		return resId;
	}

	public void setResId(String resId) {
		this.resId = resId;
	}

	public String getActions() {
		return actions;
	}

	public void setActions(String actions) {
		this.actions = actions;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	@Override
	public Serializable getObjectID() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getObjectDescription() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getEntityDescription() {
		// TODO Auto-generated method stub
		return null;
	}
}
