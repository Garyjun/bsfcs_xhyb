package com.brainsoon.fileService.po;

import java.io.Serializable;
import java.util.Date;

/**
 * @ClassName: DoFileQueue
 * @Description: 待处理队列表
 * @author xiehewei
 * @date 2015年5月15日 下午2:05:19
 *
 */
public class DoFileQueue implements Serializable {

	private static final long serialVersionUID = -8692195341305982526L;

	private Long id; // 主键id
	private String resId; // 资源id
	private String fileId; // 文件id
	private String srcPath; // 源文件路径（绝对）
	private String fileFormat; // 文件格式
	private String pendingType; // 待处理类型：0，文件转换 1，抽取图片，2抽取文本 3抽元数据
	private int stautsExtractMatedata; // 抽取元数据状态 0：待抽取 1：抽取中 2：抽取成功 3：抽取失败
	private int statusConvered; // 转换文件状态 0：待转换 1：转换中 2：转换成功 3：转换失败
	private int stautsExtractImage; // 抽取图片状态 0：待抽取 1：抽取中 2：抽取成功 3：抽取失败
	private int stautsExtractTxt; // 抽取文本状态 0：待抽取 1：抽取中 2：抽取成功 3：抽取失败
	private String resultConveredfilePath; // 转换后的文件路径（绝对）
	private String resultTxt; // 抽取的文本内容
	private String resultMatedata; // 抽取的元数据内容
	private String resultImagePath; // 抽取的图片路径（绝对）
	private String actionConveredfileUrl; // 转换后的文件传输路径（绝对）
	private String actionTxtUrl; // 抽取的文本处理路径
	private String actionMatedataUrl; // 抽取的元数据处理路径
	private String actionImageUrl; // 抽取的图片处理路径（绝对）
	private int syncStautsMatedata; // 同步元数据状态 0：待同步 1：同步中 2：同步成功 3：同步失败
	private int syncStautsConvered; // 同步转换文件状态 0：待同步 1：同步中 2：同步成功 3：同步失败
	private int syncStautsImage; // 同步图片状态 0：待同步 1：同步中 2：同步成功 3：同步失败
	private int syncStautsTxt; // 同步文本状态 0：待同步 1：同步中 2：同步成功 3：同步失败
	private int retryNum; // 重试次数（针对失败的记录）
	private String describes; // 处理过程描述
	private String timestamp; // 时间戳
	private int platformId; // 平台id
	private int priority; // 处理的优先级 0：默认 1：立即开始 2：低
	private Date createTime; // 文件创建时间
	private Date updateTime; // 文件修改时间
	private String imageWH;	//抽取的图片宽高
	private String txtName;	//水印文字
	private String fontSize;//水印文字大小
	private String fontName;//水印文字字体
	private String position;//水印位置
	private String color;	//字体颜色
	private String isBold;	//字体是否加粗
	private String alpha;	//透明度
	private String wmImage;	//水印图片路径（绝对）

	public void setId(Long id) {
		this.id = id;
	}

	public Long getId() {
		return id;
	}

	public void setResId(String resId) {
		this.resId = resId;
	}

	public String getResId() {
		return resId;
	}

	public void setFileId(String fileId) {
		this.fileId = fileId;
	}

	public String getFileId() {
		return fileId;
	}

	public void setSrcPath(String srcPath) {
		this.srcPath = srcPath;
	}

	public String getSrcPath() {
		return srcPath;
	}

	public String getFileFormat() {
		return fileFormat;
	}

	public void setFileFormat(String fileFormat) {
		this.fileFormat = fileFormat;
	}

	public String getPendingType() {
		return pendingType;
	}

	public void setPendingType(String pendingType) {
		this.pendingType = pendingType;
	}

	public int getStautsExtractMatedata() {
		return stautsExtractMatedata;
	}

	public void setStautsExtractMatedata(int stautsExtractMatedata) {
		this.stautsExtractMatedata = stautsExtractMatedata;
	}

	public int getStatusConvered() {
		return statusConvered;
	}

	public void setStatusConvered(int statusConvered) {
		this.statusConvered = statusConvered;
	}

	public int getStautsExtractImage() {
		return stautsExtractImage;
	}

	public void setStautsExtractImage(int stautsExtractImage) {
		this.stautsExtractImage = stautsExtractImage;
	}

	public int getStautsExtractTxt() {
		return stautsExtractTxt;
	}

	public void setStautsExtractTxt(int stautsExtractTxt) {
		this.stautsExtractTxt = stautsExtractTxt;
	}

	public String getResultConveredfilePath() {
		return resultConveredfilePath;
	}

	public void setResultConveredfilePath(String resultConveredfilePath) {
		this.resultConveredfilePath = resultConveredfilePath;
	}

	public String getResultTxt() {
		return resultTxt;
	}

	public void setResultTxt(String resultTxt) {
		this.resultTxt = resultTxt;
	}

	public String getResultMatedata() {
		return resultMatedata;
	}

	public void setResultMatedata(String resultMatedata) {
		this.resultMatedata = resultMatedata;
	}

	public String getResultImagePath() {
		return resultImagePath;
	}

	public void setResultImagePath(String resultImagePath) {
		this.resultImagePath = resultImagePath;
	}

	public String getActionConveredfileUrl() {
		return actionConveredfileUrl;
	}

	public void setActionConveredfileUrl(String actionConveredfileUrl) {
		this.actionConveredfileUrl = actionConveredfileUrl;
	}

	public String getActionTxtUrl() {
		return actionTxtUrl;
	}

	public void setActionTxtUrl(String actionTxtUrl) {
		this.actionTxtUrl = actionTxtUrl;
	}

	public String getActionMatedataUrl() {
		return actionMatedataUrl;
	}

	public void setActionMatedataUrl(String actionMatedataUrl) {
		this.actionMatedataUrl = actionMatedataUrl;
	}

	public String getActionImageUrl() {
		return actionImageUrl;
	}

	public void setActionImageUrl(String actionImageUrl) {
		this.actionImageUrl = actionImageUrl;
	}

	public int getSyncStautsMatedata() {
		return syncStautsMatedata;
	}

	public void setSyncStautsMatedata(int syncStautsMatedata) {
		this.syncStautsMatedata = syncStautsMatedata;
	}

	public int getSyncStautsConvered() {
		return syncStautsConvered;
	}

	public void setSyncStautsConvered(int syncStautsConvered) {
		this.syncStautsConvered = syncStautsConvered;
	}

	public int getSyncStautsImage() {
		return syncStautsImage;
	}

	public void setSyncStautsImage(int syncStautsImage) {
		this.syncStautsImage = syncStautsImage;
	}

	public int getSyncStautsTxt() {
		return syncStautsTxt;
	}

	public void setSyncStautsTxt(int syncStautsTxt) {
		this.syncStautsTxt = syncStautsTxt;
	}

	public void setRetryNum(int retryNum) {
		this.retryNum = retryNum;
	}

	public int getRetryNum() {
		return retryNum;
	}

	public void setDescribes(String describes) {
		this.describes = describes;
	}

	public String getDescribes() {
		return describes;
	}

	public void setTimestamp(String timestamp) {
		this.timestamp = timestamp;
	}

	public String getTimestamp() {
		return timestamp;
	}

	public void setPlatformId(int platformId) {
		this.platformId = platformId;
	}

	public int getPlatformId() {
		return platformId;
	}

	public void setPriority(int priority) {
		this.priority = priority;
	}

	public int getPriority() {
		return priority;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}

	public Date getUpdateTime() {
		return updateTime;
	}
	public String getImageWH() {
		return imageWH;
	}

	public void setImageWH(String imageWH) {
		this.imageWH = imageWH;
	}

	public String getTxtName() {
		return txtName;
	}

	public void setTxtName(String txtName) {
		this.txtName = txtName;
	}

	public String getFontSize() {
		return fontSize;
	}

	public void setFontSize(String fontSize) {
		this.fontSize = fontSize;
	}

	public String getFontName() {
		return fontName;
	}

	public void setFontName(String fontName) {
		this.fontName = fontName;
	}

	public String getPosition() {
		return position;
	}

	public void setPosition(String position) {
		this.position = position;
	}

	public String getColor() {
		return color;
	}

	public void setColor(String color) {
		this.color = color;
	}

	public String getIsBold() {
		return isBold;
	}

	public void setIsBold(String isBold) {
		this.isBold = isBold;
	}

	public String getAlpha() {
		return alpha;
	}

	public void setAlpha(String alpha) {
		this.alpha = alpha;
	}

	public String getWmImage() {
		return wmImage;
	}

	public void setWmImage(String wmImage) {
		this.wmImage = wmImage;
	}
}
