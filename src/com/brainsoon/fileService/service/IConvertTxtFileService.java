package com.brainsoon.fileService.service;

import java.io.File;
import java.util.List;

import com.brainsoon.fileService.po.SolrQueue;

public interface IConvertTxtFileService {
	public void convertTxt(String successUrl);
	public void doConvertTxt();
	public List<SolrQueue> doQuery();
	public int doCreateTxtFile(File resDir);
	/**
	 * 	
	* @Title: pdfProcess
	* @Description: pdf处理	转换pdf为swf，并生成txt文件
	* @param objectId	pdf的objectId
	* @param pdfPath	pdf路径
	* @param convertPath	转换后的文件需要存的路径，需在该路径下新建swf和txt目录，分别存放处理后的文件
	* @param swfFormat	swf处理方式 sswf 分页 mswf 整个
	* @param hasFileName    swf是否包含原文件名
	* @return void    返回类型
	* @throws
	 */
	public String pdfProcess(String objectId,String pdfPath, String convertPath, String swfFormat,  Boolean hasFileName);
	
	/**
	 * 
	* @Title: doTIFAndPDF
	* @Description: 将tif转换为jpg AND 将pdf转换为swf
	* @param currentPath	C:/temp/xml/001/9787502039639 当前处理的目录
	* @return    参数
	* @return String    返回类型
	* @throws
	 */
	public String doTIFAndPDF(File currentPath);
	public String doPdf2Swf(File file);
	public String doTif2Min(File currentPath);
	public String doHtmlToXml(File xmlPath);
}
