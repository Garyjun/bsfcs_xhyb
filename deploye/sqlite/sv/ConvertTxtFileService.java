package com.brainsoon.fileService.service.impl;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;

import com.brainsoon.common.util.HttpClientUtil;
import com.brainsoon.common.util.dofile.code.Epub2Html;
import com.brainsoon.common.util.dofile.content.PdfUtil;
import com.brainsoon.common.util.dofile.conver.OfficeToPdfUtils;
import com.brainsoon.common.util.dofile.util.ConstantsDef;
import com.brainsoon.common.util.dofile.util.DoFileUtils;
import com.brainsoon.common.util.dofile.util.PropertiesReader;
import com.brainsoon.fileService.service.IConvertTxtFileService;
import com.brainsoon.fileService.thread.ProcessFileC;

public class ConvertTxtFileService implements IConvertTxtFileService{

	private static final String txtSrcPath = PropertiesReader.getInstance().getProperty("txtFileSrcPath");
	private static final String txtTargetPath = PropertiesReader.getInstance().getProperty("txtFileTargetPath");
	private static final Logger logger = Logger.getLogger(ProcessFileC.class);
	
	/**
	 * 抽取文件文本方法，成功后调用url更新状态
	 */
	public void convertTxt(String successUrl) {
		File srcDir = new File(txtSrcPath);
		try {
			if (srcDir.listFiles().length > 0) {
				for (File resDir : srcDir.listFiles()) {
					logger.info("_______________"+resDir.getAbsolutePath());
					int status = createTxtFile(resDir);
					//回调方法
					HttpClientUtil http = new HttpClientUtil();
					logger.info("回调URL: " + successUrl);
					http.executeGet(successUrl);
				}
			} else {
				Thread.sleep(10000);
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("文件转换出错！");
		}
	}

	private int createTxtFile(File resDir){
		int status = 0;
		try {
			File targetFile = new File(txtTargetPath);
			if(!targetFile.exists()){
				targetFile.mkdirs();
			}
			String txtFile = txtTargetPath + File.separator + resDir.getName() + ".txt.bak";
			File txt = new File(txtFile);
			String middleFile = "";
			for(File file : resDir.listFiles()){
				String result = "";
				if(ConstantsDef.isOfficeFile(DoFileUtils.getExtensionName(file.getName()))){
					middleFile = txtTargetPath + File.separator + resDir.getName() + ".pdf";
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
			txt.renameTo(new File(txtTargetPath + File.separator + resDir.getName() + ".txt"));
			
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
	
	public static void main(String [] args){
		ConvertTxtFileService cfs = new ConvertTxtFileService();
		try {
			cfs.createTxtFile(new File("E:\\test"));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
