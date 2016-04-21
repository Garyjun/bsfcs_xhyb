package com.brainsoon.fileService.po;

import java.io.Serializable;
import java.util.Date;

import com.channelsoft.appframe.po.BaseHibernateObject;

/**
 * @ClassName: DoFileQueue
 * @Description: 待处理队列表
 * @author huagnjun
 * @date 2015年9月7日16:27:26
 * 建表语句：
 * 
	CREATE TABLE `dofile_queue` (
	  `id` INT(11) NOT NULL AUTO_INCREMENT COMMENT '主键id',
	  `resId` VARCHAR(100) DEFAULT NULL COMMENT '资源id',
	  `fileId` VARCHAR(100) DEFAULT NULL COMMENT '文件id',
	  `srcPath` VARCHAR(2000) DEFAULT NULL COMMENT '源文件路径（绝对）',
	  `file_format` VARCHAR(20) DEFAULT NULL COMMENT '文件格式',
	  `pending_type` VARCHAR(20) DEFAULT NULL COMMENT '待处理类型：0，文件转换 1，抽取图片，2抽取文本 3抽元数据',
	  `stauts_extract_matedata` INT(2) DEFAULT NULL COMMENT '抽取元数据状态 0：待抽取 1：抽取中 2：抽取成功 3：抽取失败',
	  `status_convered` INT(2) DEFAULT NULL COMMENT '转换文件状态 0：待转换 1：转换中 2：转换成功 3：转换失败',
	  `stauts_extract_image` INT(2) DEFAULT NULL COMMENT '抽取图片状态 0：待抽取 1：抽取中 2：抽取成功 3：抽取失败',
	  `stauts_extract_txt` INT(2) DEFAULT NULL COMMENT '抽取文本状态 0：待抽取 1：抽取中 2：抽取成功 3：抽取失败',
	  `result_converedfile_path` VARCHAR(2000) DEFAULT NULL COMMENT '转换后的文件路径（绝对）',
	  `result_txt` VARCHAR(2000) DEFAULT NULL COMMENT '抽取的文本内容',
	  `result_matedata` VARCHAR(2000) DEFAULT NULL COMMENT '抽取的元数据内容',
	  `result_image_path` VARCHAR(2000) DEFAULT NULL COMMENT '抽取的图片路径（绝对）',
	  `action_converedfile_url` VARCHAR(500) DEFAULT NULL COMMENT '转换后的文件传输路径（绝对）',
	  `action_txt_url` VARCHAR(500) DEFAULT NULL COMMENT '抽取的文本处理路径',
	  `action_matedata_url` VARCHAR(500) DEFAULT NULL COMMENT '抽取的元数据处理路径',
	  `action_image_url` VARCHAR(500) DEFAULT NULL COMMENT '抽取的图片处理路径（绝对）',
	  `sync_stauts_matedata` INT(2) DEFAULT NULL COMMENT '同步元数据状态 0：待同步 1：同步中 2：同步成功 3：同步失败',
	  `sync_stauts_convered` INT(2) DEFAULT NULL COMMENT '同步转换文件状态 0：待同步 1：同步中 2：同步成功 3：同步失败',
	  `sync_stauts_image` INT(2) DEFAULT NULL COMMENT '同步图片状态 0：待同步 1：同步中 2：同步成功 3：同步失败',
	  `sync_stauts_txt` INT(2) DEFAULT NULL COMMENT '同步文本状态 0：待同步 1：同步中 2：同步成功 3：同步失败',
	  `retryNum` INT(11) DEFAULT NULL COMMENT '重试次数（针对失败的记录）',
	  `describes` VARCHAR(2000) DEFAULT NULL COMMENT '处理过程描述',
	  `timestamp` VARCHAR(50) DEFAULT NULL COMMENT '时间戳',
	  `platformId` INT(11) DEFAULT NULL COMMENT '平台id',
	  `priority` INT(2) DEFAULT NULL COMMENT '处理的优先级 0：默认 1：立即开始 2：低',
	  `createTime` varchar(50) DEFAULT NULL COMMENT '文件创建时间',
	  `updateTime` varchar(50) DEFAULT NULL COMMENT '文件修改时间',
	  `imageWH` VARCHAR(20) DEFAULT NULL COMMENT '抽取的图片宽高',
	  `txtName` VARCHAR(100) DEFAULT NULL COMMENT '水印文字',
	  `fontSize` VARCHAR(20) DEFAULT NULL COMMENT '水印文字大小',
	  `fontName` VARCHAR(20) DEFAULT NULL COMMENT '水印文字字体',
	  `position` VARCHAR(20) DEFAULT NULL COMMENT '水印位置',
	  `color` VARCHAR(20) DEFAULT NULL COMMENT '字体颜色',
	  `isBold` VARCHAR(20) DEFAULT NULL COMMENT '字体是否加粗',
	  `alpha` VARCHAR(20) DEFAULT NULL COMMENT '透明度',
	  `wmImage` VARCHAR(500) DEFAULT NULL COMMENT '水印图片路径（绝对）',
	  PRIMARY KEY (`id`)
	) ENGINE=MYISAM DEFAULT CHARSET=utf8;
 
 *
 */
public class DoFileQueue extends BaseHibernateObject implements Serializable {

	private static final long serialVersionUID = -8692195341305982526L;

	private Long id; // 主键id
	private String resId; // 资源id
	private String fileId; // 文件id
	private String objectId; // 文件objectId
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
	private String createTime; // 文件创建时间
	private String updateTime; // 文件修改时间
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

	public String getObjectId() {
		return objectId;
	}

	public void setObjectId(String objectId) {
		this.objectId = objectId;
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

	
	public String getCreateTime() {
		return createTime;
	}

	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}

	public String getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(String updateTime) {
		this.updateTime = updateTime;
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

	@Override
	public Serializable getObjectID() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getObjectDescription() {
		// TODO Auto-generated method stub
		return toString();
	}

	@Override
	public String toString() {
		return "DoFileQueue [id=" + id + ", resId=" + resId + ", fileId="
				+ fileId + ", srcPath=" + srcPath + ", fileFormat="
				+ fileFormat + ", pendingType=" + pendingType
				+ ", stautsExtractMatedata=" + stautsExtractMatedata
				+ ", statusConvered=" + statusConvered
				+ ", stautsExtractImage=" + stautsExtractImage
				+ ", stautsExtractTxt=" + stautsExtractTxt
				+ ", resultConveredfilePath=" + resultConveredfilePath
				+ ", resultTxt=" + resultTxt + ", resultMatedata="
				+ resultMatedata + ", resultImagePath=" + resultImagePath
				+ ", actionConveredfileUrl=" + actionConveredfileUrl
				+ ", actionTxtUrl=" + actionTxtUrl + ", actionMatedataUrl="
				+ actionMatedataUrl + ", actionImageUrl=" + actionImageUrl
				+ ", syncStautsMatedata=" + syncStautsMatedata
				+ ", syncStautsConvered=" + syncStautsConvered
				+ ", syncStautsImage=" + syncStautsImage + ", syncStautsTxt="
				+ syncStautsTxt + ", retryNum=" + retryNum + ", describes="
				+ describes + ", timestamp=" + timestamp + ", platformId="
				+ platformId + ", priority=" + priority + ", createTime="
				+ createTime + ", updateTime=" + updateTime + ", imageWH="
				+ imageWH + ", txtName=" + txtName + ", fontSize=" + fontSize
				+ ", fontName=" + fontName + ", position=" + position
				+ ", color=" + color + ", isBold=" + isBold + ", alpha="
				+ alpha + ", wmImage=" + wmImage + "]";
	}

	@Override
	public String getEntityDescription() {
		// TODO Auto-generated method stub
		return null;
	}
}
