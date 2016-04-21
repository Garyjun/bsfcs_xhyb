package com.brainsoon.fileService.service.impl;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import com.brainsoon.common.service.BaseService;
import com.brainsoon.common.util.HttpClientUtil;
import com.brainsoon.common.util.dofile.code.Epub2Html;
import com.brainsoon.common.util.dofile.content.PdfUtil;
import com.brainsoon.common.util.dofile.conver.OfficeToPdfUtils;
import com.brainsoon.common.util.dofile.image.ImageUtils;
import com.brainsoon.common.util.dofile.image.ImgCoverUtil;
import com.brainsoon.common.util.dofile.util.ConstantsDef;
import com.brainsoon.common.util.dofile.util.DoFileUtils;
import com.brainsoon.common.util.dofile.util.PropertiesReader;
import com.brainsoon.fileService.component.FileConver;
import com.brainsoon.fileService.po.DoFileHistory;
import com.brainsoon.fileService.po.SolrQueue;
import com.brainsoon.fileService.service.IConvertTxtFileService;
import com.brainsoon.fileService.utils.MyFile;

public class ConvertTxtFileService extends BaseService implements IConvertTxtFileService{

	private static final String RES_NOCONVERT = PropertiesReader.getInstance().getProperty("txtFileSrcPath").replaceAll("\\\\", "/").replaceAll("//", "/");
	private static final String RES_CONVERTED = PropertiesReader.getInstance().getProperty("txtFileTargetPath").replaceAll("\\\\", "/").replaceAll("//", "/");
	private static final Logger logger = Logger.getLogger(ConvertTxtFileService.class);
	
	
	
	/**
	 * 抽取文件文本方法，成功后调用url更新状态
	 */
	public void doConvertTxt() {
		while (true) {
			logger.info("【doConvertTxt】---抽取文件文本方法------开始 ");
			StringBuffer selectHql = new StringBuffer();
			getBaseDao().closeSession();
			selectHql.append(" from SolrQueue where status = 0 ");
			List<SolrQueue> solrs = getBaseDao().find(selectHql.toString());
			if (solrs != null && solrs.size() > 0) {
				for (SolrQueue solrQueue : solrs) {
					String urnResId = solrQueue.getResId();
					String resId = urnResId.substring(urnResId.lastIndexOf(":")+1);
					File resDirFile = null;
					if (!RES_NOCONVERT.endsWith("/")) {
						resDirFile = new File(RES_NOCONVERT + "/"+resId);
					}else {
						resDirFile = new File(RES_NOCONVERT + resId);
					}
					
					int status = -1;
					try {
						status = doCreateTxtFile(resDirFile);
					} catch (Exception e) {
						e.printStackTrace();
					}
					
					if (status == 0) {
						//回调方法
						try {
							String successUrl = solrQueue.getActions();
							successUrl = URLDecoder.decode(successUrl,"UTF-8");
							HttpClientUtil http = new HttpClientUtil();
							logger.info("回调URL: " + successUrl);
							http.executeGet(successUrl);
							logger.info("【doConvertTxt】---抽取文件文本方法------处理完毕！---回调url： "+successUrl);
						} catch (UnsupportedEncodingException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}
			}
			
			try {
				logger.info("【doConvertTxt】---抽取文件文本方法--------没有要处理的数据---休息一分钟 ");
				Thread.sleep(60*1000);//姣忓垎閽熻琛ㄤ竴娆�		姣忓皬鏃惰琛ㄤ竴娆�60*60*1000
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
		}
	}
	
	
	
	/**
	 * 抽取文件文本方法，成功后调用url更新状态
	 */
	public void convertTxt(String successUrl) {
		String respath = successUrl.substring(successUrl.lastIndexOf(":")+1);
		File resDir = new File(RES_NOCONVERT+"/"+respath);
		try {
			
			if (resDir.exists()) {
				logger.info("_______________"+resDir.getAbsolutePath());
				int status = doCreateTxtFile(resDir);
				//回调方法
				HttpClientUtil http = new HttpClientUtil();
				logger.info("回调URL: " + successUrl);
				http.executeGet(successUrl);
			}
			
			/*if (srcDir.listFiles().length > 0) {
				for (File resDir : srcDir.listFiles()) {
					logger.info("_______________"+resDir.getAbsolutePath());
					int status = doCreateTxtFile(resDir);
					//回调方法
					HttpClientUtil http = new HttpClientUtil();
					logger.info("回调URL: " + successUrl);
					http.executeGet(successUrl);
				}
			} else {
				Thread.sleep(10000);
			}*/
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("文件转换出错！");
		}
	}

	public int doCreateTxtFile(File resDir){
		int status = -1;
		try {
			File targetFile = new File(RES_CONVERTED);
			if(!targetFile.exists()){
				targetFile.mkdirs();
			}
			String txtFile = RES_CONVERTED + File.separator + resDir.getName() + ".txt.bak";
			File txt = new File(txtFile);
			String middleFile = "";
			if(resDir.exists()){
				for(File file : resDir.listFiles()){
					String result = "";
					if(ConstantsDef.isOfficeFile(DoFileUtils.getExtensionName(file.getName()))){
						middleFile = RES_CONVERTED + File.separator + resDir.getName() + ".pdf";
						try {
							OfficeToPdfUtils.convertToPdf(file.getAbsolutePath(), middleFile);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}else{
						middleFile = file.getAbsolutePath();
					}
					if("pdf".equals(DoFileUtils.getExtensionName(middleFile))){
						try {
							int num = PdfUtil.getPdfAllPageTotalNum(middleFile);
							result =  PdfUtil.parsePdf(middleFile, 1, num);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
					if(ConstantsDef.isTxtFile(DoFileUtils.getExtensionName(file.getName()))){
						try {
							result = getTxtFileContent(file);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
					try {
						logger.info("===================="+file.getAbsolutePath());
						writeResultTxt(txt,result);
						new File(middleFile).delete();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				FileUtils.deleteDirectory(resDir); 
				txt.renameTo(new File(RES_CONVERTED + File.separator + resDir.getName() + ".txt"));
				
				status=0;//正常处理完毕返回状态
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			status = -1;
		}
		return status;
	}
	
	//获取文本文件file中的字符串内容
	private String getTxtFileContent(File file) throws IOException{
		String encode = Epub2Html.getFileCharsetByPath(file.getAbsolutePath());
		if (!"UTF-8".equalsIgnoreCase(encode)) {
			encode = "GBK";
		}
		String content = FileUtils.readFileToString(file,encode);
		return content;
	}
	
	//把result内容追加到文本文件txt中
	private void writeResultTxt(File txt, String result) throws IOException{
		RandomAccessFile randomFile = new RandomAccessFile(txt.getAbsolutePath(), "rw"); 
		long fileLength = randomFile.length(); 
		randomFile.seek(fileLength);
//		randomFile.write("\r\n".getBytes()); 
		randomFile.write(result.getBytes()); 
		randomFile.close(); 
	}
	
	/**
	 * 	
	* @Title: pdfProcess
	* @Description: pdf处理	转换pdf为swf，并生成txt文件
	* @param objectId	pdf的objectId
	* @param pdfPath	pdf路径
	* @param convertPath	转换后的文件需要存的路径，需在该路径下新建swf和txt目录，分别存放处理后的文件
	* @param swfFormat	sswf代表一个pdf生成一个swf文件，mswf代表一个pdf生成多个swf文件
	* @param hasFileName    swf是否包含原文件名
	* @return void    返回类型
	* @throws
	 */
	public String pdfProcess(String objectId,String pdfPath, String convertPath, String swfFormat,  Boolean hasFileName){
		String msg="SUCCESS";
		try {
			String fileEncode = System.getProperty("file.encoding");
			pdfPath = new String(pdfPath.getBytes("UTF-8"),fileEncode);
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
		}
		if (StringUtils.isBlank(pdfPath)) {
			msg="pdf路径错误！";
			return msg;
		}
		if (StringUtils.isBlank(convertPath)) {
			msg="转换文件存放路径错误！";
			return msg;
		}
		logger.info("【ConvertTxtFileService】pdfProcess() 开始	 pdf处理	转换pdf为swf，并生成txt文件");
		//----------第一步	创建swf和txt目录
		File convertBasePath = new File(convertPath);
		File swfPath = null;
		File pagesPath = null;
		if (convertBasePath.exists()) {
			try {
				swfPath = new File(convertPath + File.separator + "swf/");
				pagesPath = new File(convertPath + File.separator + "pages/");
				FileUtils.forceMkdir(swfPath);
				FileUtils.forceMkdir(pagesPath);
				logger.info("【ConvertTxtFileService】pdfProcess() 第一步	创建swf和txt目录 成功！");
			} catch (IOException e) {
				e.printStackTrace();
				msg="创建swf或txt目录错误！";
			}
		}else {
			msg="转换文件存放路径错误！";
			return msg;
		}
		
		//----------第二步	转换pdf为swf文件
		try {
			if (swfPath.exists()) {
				//从转换表中获取转换后的路径 直接拷贝到该目录下 ，如果没有转换记录 就自己转换
				boolean isConvert = true;//需不需要转换
				String resultPath = doGetPathById(objectId);
				if (StringUtils.isNotBlank(resultPath)) {
					File resultFile = new File(resultPath);
					if (resultFile.exists()) {
						File[] list = resultFile.listFiles();
						for (File file : list) {
							if (file.isFile()) {
								if ("book.ncx".equals(file.getName()) || "book.xml".equals(file.getName()) ) {
									FileUtils.copyFileToDirectory(file, swfPath.getParentFile());//拷贝到父目录
								}else {
									FileUtils.copyFileToDirectory(file, swfPath);
								}
								logger.info("【ConvertTxtFileService】pdfProcess() 第二步	拷贝文件："+file.getName());
							}
						}
						isConvert = false;//拷贝完了就不转换
					}
				}
				
				//执行转换
				if (isConvert) {
					FileConver.convertPdf(pdfPath, swfPath.getAbsolutePath(), swfFormat, hasFileName);
					logger.info("【ConvertTxtFileService】pdfProcess() 第二步	转换pdf为swf文件 成功！");
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			msg = "转换pdf为swf文件错误！";
		}
		
		//----------第三步	抽取PDF中的txt文件
		try {
			if (pagesPath.exists()) {
				PdfUtil.parsePdfEachPage2TxtFile(pdfPath,pagesPath.getAbsolutePath());
				logger.info("【ConvertTxtFileService】pdfProcess() 第三步	抽取PDF中的txt文件 成功！");
			}
		} catch (Exception e) {
			e.printStackTrace();
			msg = "抽取PDF中txt文件错误！";
		}  

		return msg;
	}
	
	public static void main(String [] args){
		ConvertTxtFileService ConvertTxtFileService = new ConvertTxtFileService();
		try {
			/*String pdf = "C:\\temp\\test\\123\\新华月报1994年10期总第600期.PDF";
			String swfPath = "C:\\temp\\test\\123\\swf";
			FileConver.convertPdf(pdf, swfPath, "nswf", false);*/
			
			ConvertTxtFileService.doTIFAndPDF(new File("C:\\temp\\xhyb"));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public List<SolrQueue> doQuery() {
		//getBaseDao().flush();
		StringBuffer selectHql = new StringBuffer();
		selectHql.append(" from SolrQueue where status = 0 ");
		List<SolrQueue> solrs = getBaseDao().query(selectHql.toString());
		return solrs;
	}
	
	/**
	 * 
	* @Title: doQueryById
	* @Description: 根据Id查询转换后路径
	* @return    参数
	* @return List<SolrQueue>    返回类型
	* @throws
	 */
	
	public String doGetPathById(String objectId) {
		String convertPath = "";
		StringBuffer selectHql = new StringBuffer();
		selectHql.append(" from DoFileHistory where objectId = '"+objectId+"'");
		List<DoFileHistory> histories = getBaseDao().query(selectHql.toString());
		if (histories.size()>0) {
			DoFileHistory doFileHistory = histories.get(0);
			convertPath = doFileHistory.getResultConveredfilePath(); 
		}
		return convertPath;
	}
	
	/**
	 * 
	* @Title: doTIFAndPDF
	* @Description: 将tif转换为jpg AND 将pdf转换为swf
	* @param currentPath	C:/temp/xml/001/9787502039639 当前处理的目录
	* @param basePath	C:/temp/xml/001/9787502039639 处理根目录
	* @return    参数
	* @return String    返回类型
	* @throws
	 */
	public String doTIFAndPDF(File currentPath){
		//String logFile = "C:/temp/xhyb/log.txt";
		String logFile = "/tmp/log.txt";
		try {
			MyFile.creatTxtFile(logFile);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		if (currentPath.exists()) {
			File[] list = currentPath.listFiles();
			for (File file : list) {
				if (file.isDirectory()) {
					doTIFAndPDF(file);
				}else if(file.isFile()){
					String fileName = file.getName();
					String fileNameOnly = fileName.substring(0, fileName.lastIndexOf("."));
					String fileType = fileName.substring(fileName.lastIndexOf(".")+1);
					File pdfParentFile = file.getParentFile();
					try {
						if ("tif".equals(fileType.toLowerCase())) {
							if ("插图".equals(pdfParentFile.getName())) {
								// 原绝对路径
								String srcPath = file.getAbsolutePath();
								// 转换后的文件路径
								String destPath = srcPath.substring(0, srcPath.lastIndexOf(".")+1)+"jpg"; 
								if (!new File(destPath).exists()) {
									ImgCoverUtil.conver2Other(srcPath, destPath);
									logger.info("【ConvertTxtFileService】doTIFAndPDF() 转换jpg tif文件原路径：" + srcPath);
								}

								srcPath = srcPath.replaceAll("\\\\", "/").replaceAll("//", "/");
								String tifName = srcPath.substring(srcPath.lastIndexOf("/")+1,srcPath.lastIndexOf("."));
								String minPath = destPath.replaceAll(tifName, tifName+"_min");
								if (new File(destPath).exists() && new File(destPath).length() > 10*1024) {
									ImageUtils.zoomImg(destPath, minPath, 60, 80);
									logger.info("【ConvertTxtFileService】doTIFAndPDF() 插图获取缩略图 插图原路径" + destPath);
								}else {
									SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式
									String date = df.format(new Date());
									MyFile.writeTxtFile(logFile, "[" +date+ "] doTIFAndPDF() 插图获取缩略图 插图原路径" + destPath);
									logger.info("【ConvertTxtFileService】doTIFAndPDF() 插图获取缩略图 插图原路径" + destPath);
								}
							}
						}else if("pdf".equals(fileType.toLowerCase())){
							if ("双层PDF".equals(pdfParentFile.getName())) {
								File swfPath = null;
								File pagesPath = null;
								//抽封面图片
								String coverPath = pdfParentFile.getAbsolutePath() + File.separator + "cover/";
								FileUtils.forceMkdir(new File(coverPath));
								PdfUtil.pdfToImage(file.getAbsolutePath(), coverPath+"cover.jpg", 1);
								if (new File(coverPath+"cover.jpg").exists() && new File(coverPath+"cover.jpg").length() > 10*1024) {
									ImageUtils.zoomImg(coverPath+"cover.jpg", coverPath+"cover_min.jpg", 60, 80);
									logger.info("【ConvertTxtFileService】doTIFAndPDF() 抽封面图片 pdf文件原路径" + file.getAbsolutePath());
								}else {
									SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式
									String date = df.format(new Date());
									MyFile.writeTxtFile(logFile, "[" +date+ "] doTIFAndPDF() 抽封面图片失败 pdf文件原路径" + file.getAbsolutePath());
									logger.info("【ConvertTxtFileService】doTIFAndPDF() 抽封面图片{失败} pdf文件原路径" + file.getAbsolutePath());
								}
								
								try {
									swfPath = new File(pdfParentFile.getAbsolutePath() + File.separator + "swf/");
									pagesPath = new File(pdfParentFile.getAbsolutePath() + File.separator + "pages/");
									FileUtils.forceMkdir(swfPath);
									FileUtils.forceMkdir(pagesPath);
									logger.info("【ConvertTxtFileService】doTIFAndPDF() 创建swf和txt目录 成功！");
								} catch (IOException e) {
									e.printStackTrace();
								}
								
								//执行转换
								FileConver.convertPdf(file.getAbsolutePath(), swfPath.getAbsolutePath(), "nswf", false);
								logger.info("【ConvertTxtFileService】doTIFAndPDF() 转换swf pdf文件原路径" + file.getAbsolutePath());
								
								//抽取PDF中的txt文件
								try {
									if (pagesPath.exists()) {
										PdfUtil.parsePdfEachPage2TxtFile(file.getAbsolutePath(),pagesPath.getAbsolutePath());
										logger.info("【ConvertTxtFileService】doTIFAndPDF() 抽取PDF中的txt文件 成功！");
									}
								} catch (Exception e) {
									e.printStackTrace();
								}  
							}
						}
					} catch (Exception e) {
						logger.error("【ConvertTxtFileService】doTIFAndPDF() 出错 请检查文件！ 文件路径：" + file.getAbsolutePath());
						e.printStackTrace();
					}
				}
			}
		}
		return "SUCCESS";
	}
	
	public String doPdf2Swf(File currentPath){
		String logFile = "/tmp/log2.txt";
		try {
			MyFile.creatTxtFile(logFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
		if (currentPath.exists()) {
			File[] list = currentPath.listFiles();
			for (File file : list) {
				if (file.isDirectory()) {
					doPdf2Swf(file);
				}else if(file.isFile()){
					String fileName = file.getName();
					String fileType = fileName.substring(fileName.lastIndexOf(".")+1);
					File pdfParentFile = file.getParentFile();
					try {
						if("pdf".equals(fileType.toLowerCase())){
							if ("双层PDF".equals(pdfParentFile.getName())) {
								File swfPath = null;
								
								try {
									swfPath = new File(pdfParentFile.getAbsolutePath() + File.separator + "swf/");
									FileUtils.forceMkdir(swfPath);
									logger.info("【ConvertTxtFileService】doPdf2Swf() 创建swf目录 成功！");
								} catch (IOException e) {
									e.printStackTrace();
								}
								
								//执行转换
								FileConver.convertPdf(file.getAbsolutePath(), swfPath.getAbsolutePath(), "mswf", false);
								logger.info("【ConvertTxtFileService】doPdf2Swf() 转换swf pdf文件原路径" + file.getAbsolutePath());
								
							}
						}
					} catch (Exception e) {
						logger.error("【ConvertTxtFileService】doPdf2Swf() 出错 请检查文件！ 文件路径：" + file.getAbsolutePath());
						e.printStackTrace();
						try {
							SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式
							String date = df.format(new Date());
							MyFile.writeTxtFile(logFile, "[" +date+ "] doPdf2Swf() 转换swf失败  pdf文件原路径" + file.getAbsolutePath());
						} catch (IOException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
					}
				}
			}
		}
		return "SUCCESS";
	}
	
	public String doTif2Min(File currentPath){
		String logFile = "/tmp/log3.txt";
		try {
			MyFile.creatTxtFile(logFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
		if (currentPath.exists()) {
			File[] list = currentPath.listFiles();
			for (File file : list) {
				if (file.isDirectory()) {
					doTif2Min(file);
				}else if(file.isFile()){
					String fileName = file.getName();
					String fileType = fileName.substring(fileName.lastIndexOf(".")+1);
					File pdfParentFile = file.getParentFile();
					try {
						if ("tif".equals(fileType.toLowerCase())) {
							if ("插图".equals(pdfParentFile.getName())) {
								// 原绝对路径
								String srcPath = file.getAbsolutePath();
								// 转换后的文件路径
								String destPath = srcPath.substring(0, srcPath.lastIndexOf(".")+1)+"jpg"; 
								if (!new File(destPath).exists()) {
									ImgCoverUtil.conver2Other(srcPath, destPath);
									logger.info("【ConvertTxtFileService】doTIFAndPDF() 转换jpg tif文件原路径：" + srcPath);
								}

								srcPath = srcPath.replaceAll("\\\\", "/").replaceAll("//", "/");
								String tifName = srcPath.substring(srcPath.lastIndexOf("/")+1,srcPath.lastIndexOf("."));
								String minPath = destPath.replaceAll(tifName, tifName+"_min");
								if (new File(destPath).exists() && new File(destPath).length() > 2*1024) {
									ImageUtils.zoomImg(destPath, minPath, 60, 80);
									logger.info("【ConvertTxtFileService】doTIFAndPDF() 插图获取缩略图 插图原路径" + destPath);
								}else {
									SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式
									String date = df.format(new Date());
									MyFile.writeTxtFile(logFile, "[" +date+ "] doTIFAndPDF() 插图获取缩略图 插图原路径" + destPath);
									logger.info("【ConvertTxtFileService】doTIFAndPDF() 插图获取缩略图 插图原路径" + destPath);
								}
							}
						}
					} catch (Exception e) {
						logger.error("【ConvertTxtFileService】doPdf2Swf() 出错 请检查文件！ 文件路径：" + file.getAbsolutePath());
						e.printStackTrace();
					}
				}
			}
		}
		return "SUCCESS";
	}
	
	 /**   
     * 追加文件：使用RandomAccessFile   
     *    
     * @param fileName 文件名   
     * @param content 追加的内容   
     */    
    public static void addToFile(String fileName, String content) {   
        RandomAccessFile randomFile = null;  
        try {     
            // 打开一个随机访问文件流，按读写方式     
            randomFile = new RandomAccessFile(fileName, "rw");     
            // 文件长度，字节数     
            long fileLength = randomFile.length();     
            // 将写文件指针移到文件尾。     
            randomFile.seek(fileLength);     
            randomFile.writeBytes(content);      
        } catch (IOException e) {     
            e.printStackTrace();     
        } finally{  
            if(randomFile != null){  
                try {  
                    randomFile.close();  
                } catch (IOException e) {  
                    e.printStackTrace();  
                }  
            }  
        }  
    }
    
    /**
     * 将文章的xml内容抽取html供预览使用
     */
    
	public String doHtmlToXml(File currentPath){
		//String logFile = "C:/temp/xhyb/log.txt";
		String logFile = "/tmp/articleLog.txt";
		try {
			MyFile.creatTxtFile(logFile);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		if (currentPath.exists()) {
			File[] list = currentPath.listFiles();
			for (File file : list) {
				if (file.isDirectory()) {
					doHtmlToXml(file);
				}else if(file.isFile()){
					String fileName = file.getName();
					String fileNameOnly = fileName.substring(0, fileName.lastIndexOf("."));
					File xmlParentFile = file.getParentFile();
					try {
						if ("正文XML".equals(xmlParentFile.getName())) {
							// xml原绝对路径
							String srcPath = file.getAbsolutePath();
							// 转换后的文件路径的绝对路径
							String htmlPath = xmlParentFile.getAbsolutePath()+"/article.html";
							if(new File(htmlPath).exists()){
								new File(htmlPath).delete();
							}
							//html模板路径
							String targetPath = htmlPath.substring(0, htmlPath.indexOf("fileRoot"))+"/fileRoot/fileArticle/column_detail.html";
							
							try {
								//读取xml文件
								java.io.File file2 = new java.io.File(srcPath);
								logger.info("xml原路径"+srcPath+"********************");
								DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
								DocumentBuilder builder = factory.newDocumentBuilder();
								Document doc = builder.parse(file2);
								NodeList nl = doc.getElementsByTagName("Article");
								
								//读取html模板
								logger.info("******************获取html模板路径"+targetPath+"*****************************");
								java.io.File htmlFile = new java.io.File(targetPath);
								logger.info("******************读取html模板完成*****************************");
								org.jsoup.nodes.Document doc1 = Jsoup.parse(htmlFile, "UTF-8", "http://example.com/");
								logger.info("******************xml文档行数"+nl.getLength()+"***********************");
								for(int i = 0; i < nl.getLength(); i++) {
									logger.info("***************************读取开始xml********************");
									String title1 = "";
									String author1 = "作者:";
									String time = "";
									String start = "<p>";
									String content = "";
									if(doc.getElementsByTagName("Title").item(i).getFirstChild() != null){
										title1 = doc.getElementsByTagName("Title").item(i).getFirstChild().getNodeValue();
									}
									if(doc.getElementsByTagName("Author").item(i).getFirstChild() != null){
										author1 = doc.getElementsByTagName("Author").item(i).getFirstChild().getNodeValue();
									}
									if(doc.getElementsByTagName("MagazineYear").item(i).getFirstChild() != null){
										time = doc.getElementsByTagName("MagazineYear").item(i).getFirstChild().getNodeValue();
									}
									if(doc.getElementsByTagName("Content").item(i).getFirstChild() != null){
										content = doc.getElementsByTagName("Content").item(i).getFirstChild().getNodeValue();
									}
									content = start+content;
									content = content.replaceAll("\n", "</p><p>");
									doc1.getElementById("title").text(title1);
									doc1.getElementById("author").text(author1);
									doc1.getElementById("time").text(time);
									doc1.getElementById("content").appendText(content);
									
									String html = doc1.html();
									html = html.replaceAll("&lt;", "<");
									html = html.replaceAll("&gt;", ">");
									logger.info("****************抽取的html页面"+htmlPath+"***************");
									FileOutputStream fos = new FileOutputStream(htmlPath, true);
									fos.write(html.getBytes());
									fos.close();
									logger.info("*****************************抽取完成****************************");
									logger.info("*****************************抽取html路径***************"+htmlPath+"*************");
								}
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					} catch (Exception e) {
						logger.error("【ConvertTxtFileService】doTIFAndPDF() 出错 请检查文件！ 文件路径：" + file.getAbsolutePath());
						e.printStackTrace();
					}
				}
			}
		}
		return "SUCCESS";
	}
    
}
