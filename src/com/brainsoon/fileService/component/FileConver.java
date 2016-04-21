package com.brainsoon.fileService.component;

import java.io.File;
import java.util.List;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;

import com.brainsoon.common.exception.ServiceException;
import com.brainsoon.common.util.dofile.content.PdfUtil;
import com.brainsoon.common.util.dofile.conver.ConverUtils;
import com.brainsoon.common.util.dofile.conver.OfficeToPdfUtils;
import com.brainsoon.common.util.dofile.conver.PdfToSwfUtil;
import com.brainsoon.common.util.dofile.conver.TxtToPdfUtils;
import com.brainsoon.common.util.dofile.util.ConstantsDef;
import com.brainsoon.common.util.dofile.util.DoFileException;
import com.brainsoon.common.util.dofile.util.DoFileUtils;
import com.brainsoon.common.util.dofile.util.FileToolkit;
import com.brainsoon.common.util.dofile.util.PropertiesReader;
import com.brainsoon.common.util.dofile.view.CatalogDTO;
import com.brainsoon.common.util.dofile.view.CreateNcxUtil;
import com.brainsoon.common.util.dofile.view.XmlFileUtil;
import com.brainsoon.fileService.po.DoFileQueue;

/**
 * @ClassName: FileConver
 * @Description: 文件转换
 * @author xiehewei
 * @date 2015年5月18日 上午9:10:52
 *
 */
public class FileConver implements IFileProcess {

	private static final Logger logger = Logger.getLogger(FileConver.class);
	
	private static final String PICTFORMAT = PropertiesReader.getInstance().getProperty(ConstantsDef.pictureFormat);//图片
	private static final String VIDEOFORMAT = PropertiesReader.getInstance().getProperty(ConstantsDef.videoFormat);//视频
	private static final String DOCUMENTFORMAT = PropertiesReader.getInstance().getProperty(ConstantsDef. documentFormat);//文档
	private static final String ANIMAFORMAT = PropertiesReader.getInstance().getProperty(ConstantsDef.animaFormat);//动画
	private static final String AUDIOFORMAT = PropertiesReader.getInstance().getProperty(ConstantsDef.audioFormat);//音频
	
	@Override
	public boolean strategyInterface(DoFileQueue task){
		boolean flag = true;
		try{
			String sufix = task.getSrcPath().substring(task.getSrcPath().lastIndexOf(".")+1);
				if(VIDEOFORMAT.contains(sufix)){
					//视频转换
					convertVideo(task);
				}else if(DOCUMENTFORMAT.contains(sufix)){
					//文档转换
					convertDocument(task);
				}else if(AUDIOFORMAT.contains(sufix)){
					//音频转换
					convertAudio(task);
				}else{
					flag = false;
				}
		}catch(Exception e){
			logger.error("文件转换出错......");
			flag = false;
		}
		return flag;
	}
	
	private void convertVideo(DoFileQueue task){
		String src = task.getSrcPath();
		String target = task.getResultConveredfilePath();
		String extentName = src.substring(src.lastIndexOf(".")+1);
		String targetPath = target.substring(0, target.lastIndexOf(File.separator));
		if(!new File(targetPath).exists()){
			new File(targetPath).mkdirs();
		}
		if(!"flv".equals(extentName.toLowerCase())&&
				!"swf".equals(extentName.toLowerCase())){
			ConverUtils.processFfmpegToFLV(src,target);
		}
	}
	
	private void convertAudio(DoFileQueue task){
		String src = task.getSrcPath();
		String target = task.getResultConveredfilePath();
		String targetPath = target.substring(0, target.lastIndexOf(File.separator));
		if(!new File(targetPath).exists()){
			new File(targetPath).mkdirs();
		}		
		ConverUtils.processFfmpegToMp3(src,target);
	}
	
	private void convertDocument(DoFileQueue task) throws Exception {
		String src = task.getSrcPath();
		String target = task.getResultConveredfilePath();
		String fileFormat = StringUtils.isBlank(task.getFileFormat())?"sswf":task.getFileFormat();
		String extentName = DoFileUtils.getExtensionName(src); //文件扩展名
		String fileName = DoFileUtils.getFileNameNoEx(src); //获取不带扩展名的文件名
		String middleFileName = getMiddleFileName(src,target);
		
		if(ConstantsDef.isOfficeFile(extentName)){
			//office转pdf
			OfficeToPdfUtils.convertToPdf(src, middleFileName);
		}else if(ConstantsDef.isTxtFile(extentName)){
			//txt转pdf
			logger.info("//txt转pdf  src "+src+" \n middleFileName "+middleFileName);
			TxtToPdfUtils.convertToPdf(src, middleFileName);
		}
		
		
		String midFile = extentName.equals("pdf")?src:middleFileName;

		String tarParentPath = "";
		if (target.endsWith(".swf")) {
			tarParentPath = target.substring(0, target.lastIndexOf(File.separator));
		} else if (!target.endsWith(File.separator)) {
			tarParentPath = target + File.separator;
		}	
		
		File pdfFile = new File(midFile);
	    if(pdfFile.exists()){
	    	try {
		    	// 获取pdf总页数
				int maxPages = PdfUtil.getPdfAllPageTotalNum(midFile);
				if(maxPages == 0){
					logger.error("{失败}-文档获取PDF总页数失败.");
				}
					//=======生成  1.ncx文件 2.带node节点的xml文件 3.所有的swf文件 4.生成整个pdf的swf文件===========
					// NCX文件临时路径
					String tempNcxPath = DoFileUtils.connectFilePath(tarParentPath, "book.ncx");
					if(new File(tempNcxPath).exists()){
						FileToolkit.deleteFile(tempNcxPath);
					}
					// 带node节点的xml文件临时路径
					String tempNodeXmlPath = DoFileUtils.connectFilePath(tarParentPath, "book.xml");
					if(new File(tempNodeXmlPath).exists()){
						FileToolkit.deleteFile(tempNodeXmlPath);
					}
					// 生成NCX文本内容
					String content = CreateNcxUtil.loadNcx(midFile, null);
					// 如果提取的内容不为空，则继续生成含node节点的导航文件，否则不生成
					if(StringUtils.isNotBlank(content)){
						// 将NCX文本转成DOCUMENT对象
						Document doc = DocumentHelper.parseText(content);
						// 生成临时带node的NCX文件
						XmlFileUtil.createXMLFile(doc, tempNcxPath);
						// 读取node节点文档---根据ncx内容生成符合在线预览的带node节点的xml
						List<CatalogDTO> catalogList = XmlFileUtil.NcxCvtXml(tempNcxPath,
								tempNodeXmlPath, maxPages, "");
						if(catalogList == null || catalogList.size() == 0){
							String xmlContent = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n <book>\n  <totalpage>" + maxPages + "</totalpage>\n</book>";
							XmlFileUtil.createXMLFile(xmlContent.trim(), tempNodeXmlPath);
						}
					}else{//生成一个只带总页数的导航文件
						String xmlContent = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n <book>\n  <totalpage>" + maxPages + "</totalpage>\n</book>";
						XmlFileUtil.createXMLFile(xmlContent.trim(), tempNodeXmlPath);
					}
					if(!new File(tempNodeXmlPath).exists() || new File(tempNodeXmlPath).length() <= 0){
						logger.error("{失败}-pdf生成导航XML失败。\n");
					}
					logger.info("开始处理文档的SWF逻辑开始...");  
					
					// 转换SWF文件
					PdfToSwfUtil.pdf2Swf(1,midFile,tarParentPath,null,maxPages,fileFormat,false);
					//判断是否存在swf文件，如果没有则说明未转换成功。
					Pattern p = Pattern.compile(".+\\.(swf)$");
					List<File> files = DoFileUtils.filePattern(new File(tarParentPath),p,false);
					if(fileFormat.equals("sswf")){
						maxPages = 1;
					}
					//通过正则表达式判断是否生成的swf个数是：总页数+整个单swf 的总和
					if(files == null || files.size() <= 0 || files.size() != maxPages){
						logger.error("{失败}-命令执行成功，但生成的SWF个数不对。文档转SWF文件完全未成功.\n");
					}
					
					if(!src.equals(midFile)){
						new File(midFile).delete();
					}
					logger.info("处理文档的SWF逻辑结束。");  
			 } catch (DoFileException e) {
				 logger.error("{失败}-" + e.getMessage() + "\n");
				 e.printStackTrace();
				 throw new ServiceException("文件转换出错！");
			}
		}else{
			logger.error("{失败}-PDF文件不存在\n");
		}
//		String midFile = extentName.equals("pdf")?src:middleFileName;
//		int num = PdfUtil.getPdfAllPageTotalNum(midFile);
//		String result = "";
//		if(target.endsWith(".swf")){
//			result = target.substring(0, target.lastIndexOf(File.separator));
//		}else if(!target.endsWith(File.separator)){
//			result = target + File.separator;
//		}
//		boolean converSwfType = true;
//		if("mswf".equals(fileFormat)){
//			converSwfType = false;
//		}
//		logger.info("pdf转swf========源文件路径" + midFile);
//		logger.info("pdf转swf========目标文件路径" + result);
//		logger.info("pdf转swf========转换方式" + converSwfType);
//		PdfToSwfUtil.pdfConvertToSwf(midFile, result, "0-"+num, converSwfType);
//		if(!src.equals(midFile))
//			new File(midFile).delete();
	}
	
	private static String getMiddleFileName(String src,String target){
		String result = "";
		String srcFileName = DoFileUtils.getFileNameNoEx(src);
		if(target.indexOf(".swf")!=-1){
			String targetExtentName = target.substring(target.lastIndexOf("."));
			result = target.replaceAll(targetExtentName, ".pdf");
		}else{
			if(target.endsWith(File.separator)){
				result = target + srcFileName + ".pdf";
			}else{
				result = target + File.separator + srcFileName + ".pdf";
			}
		}
		return result;
	}
	
	
	/**
	 * 
	* @Title: convertPdf
	* @Description: TODO(这里用一句话描述这个方法的作用)
	* @param pdfPath	pdf路径	C:\temp\xml\002\9787502040260\dpdf\9787502040260H.pdf
	* @param convertPath	转换后的文件需要存的路径，需在该路径下新建swf和txt目录，分别存放处理后的文件	C:\convert\9787502040260\swf\
	* @param swfFormat	sswf代表一个pdf生成一个swf文件，mswf代表一个pdf生成多个swf文件
	* @param hasFileName    swf是否包含原文件名
	* @throws Exception    参数
	* @return void    返回类型
	* @throws
	 */
	public static void convertPdf(String pdfPath, String convertPath, String swfFormat,  Boolean hasFileName) throws Exception {
		String fileName = DoFileUtils.getFileNameNoEx(pdfPath); //获取不带扩展名的文件名	9787502040260H
		pdfPath = pdfPath.replaceAll("\\\\", "/");
		convertPath = convertPath.replaceAll("\\\\", "/");
		if (!convertPath.endsWith("/")) {
			convertPath = convertPath + "/";
		}	
		
		File pdfFile = new File(pdfPath);
	    if(pdfFile.exists()){
	    	try {
		    	// 获取pdf总页数
				int maxPages = PdfUtil.getPdfAllPageTotalNum(pdfPath);
				if(maxPages == 0){
					logger.error("{失败}-文档获取PDF总页数失败.");
				}
				//=======生成  1.ncx文件 2.带node节点的xml文件 3.所有的swf文件 4.生成整个pdf的swf文件===========
				// NCX文件临时路径
				String tempNcxPath = DoFileUtils.connectFilePath(convertPath.replace("swf/", ""), "book.ncx");
				if(new File(tempNcxPath).exists()){
					FileToolkit.deleteFile(tempNcxPath);
				}
				// 带node节点的xml文件临时路径
				String tempNodeXmlPath = DoFileUtils.connectFilePath(convertPath.replace("swf/", ""), "book.xml");
				if(new File(tempNodeXmlPath).exists()){
					FileToolkit.deleteFile(tempNodeXmlPath);
				}
				// 生成NCX文本内容
				String content = CreateNcxUtil.loadNcx(pdfPath, null);
				// 如果提取的内容不为空，则继续生成含node节点的导航文件，否则不生成
				if(StringUtils.isNotBlank(content)){
					// 将NCX文本转成DOCUMENT对象
					Document doc = DocumentHelper.parseText(content);
					// 生成临时带node的NCX文件
					XmlFileUtil.createXMLFile(doc, tempNcxPath);
					// 读取node节点文档---根据ncx内容生成符合在线预览的带node节点的xml
					List<CatalogDTO> catalogList = XmlFileUtil.NcxCvtXml(tempNcxPath,tempNodeXmlPath, maxPages, "");
					if(catalogList == null || catalogList.size() == 0){
						String xmlContent = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n <book>\n  <totalpage>" + maxPages + "</totalpage>\n</book>";
						XmlFileUtil.createXMLFile(xmlContent.trim(), tempNodeXmlPath);
					}
				}else{//生成一个只带总页数的导航文件
					String xmlContent = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n <book>\n  <totalpage>" + maxPages + "</totalpage>\n</book>";
					XmlFileUtil.createXMLFile(xmlContent.trim(), tempNodeXmlPath);
				}
				if(!new File(tempNodeXmlPath).exists() || new File(tempNodeXmlPath).length() <= 0){
					logger.error("{失败}-pdf生成导航XML失败。\n");
				}
				logger.info("开始处理文档的SWF逻辑开始...");  
				
				// 转换SWF文件
				PdfToSwfUtil.pdf2Swf(1,pdfPath,convertPath,null,maxPages,swfFormat,hasFileName);
				//判断是否存在swf文件，如果没有则说明未转换成功。
				Pattern p = Pattern.compile(".+\\.(swf)$");
				List<File> files = DoFileUtils.filePattern(new File(convertPath),p,false);
				if(swfFormat.equals("sswf")){
					maxPages = 1;
				}
				//通过正则表达式判断是否生成的swf个数是：总页数+整个单swf 的总和
				if(files == null || files.size() <= 0 || files.size() != maxPages){
					logger.error("{失败}-命令执行成功，但生成的SWF个数不对。文档转SWF文件完全未成功.\n");
				}
				logger.info("处理文档的SWF逻辑结束。");  
			 } catch (DoFileException e) {
				 logger.error("{失败}-" + e.getMessage() + "\n");
				 e.printStackTrace();
				 throw new ServiceException("文件转换出错！");
			}
		}else{
			logger.error("{失败}-PDF文件不存在\n");
		}
	}
	
	public static void main(String[] args) throws Exception{
		/*DoFileQueue task = new DoFileQueue();
		task.setSrcPath("E:\\test\\market.txt");
		task.setResultConveredfilePath("E:\\123");
		task.setFileFormat("mswf");
		new FileConver().convertDocument(task);*/
		String pdf = "C:/temp/xml/001/9787502039639/pdf/9787502039639（宣传）.pdf";
		String target = "C:/temp/convert/9787502039639";
		convertPdf(pdf,target,"mswf",false);
		
	}
}
