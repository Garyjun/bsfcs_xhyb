package com.brainsoon.fileService.thread;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.List;

import org.apache.log4j.Logger;

import com.brainsoon.common.util.HttpClientUtil;
import com.brainsoon.common.util.dofile.util.PropertiesReader;
import com.brainsoon.fileService.po.SolrQueue;
import com.brainsoon.fileService.service.IConvertTxtFileService;
import com.channelsoft.appframe.utils.BeanFactoryUtil;

/**
 * 
* @ClassName: ConvertTxtThread
* @Description: 抽文本方法进程
* @author huangjun
* @date 2015-12-23 14:57:25
*
 */
public class ConvertTxtThread implements Runnable {

	private static final Logger logger = Logger.getLogger(ConvertTxtThread.class);
	private static final String RES_NOCONVERT = PropertiesReader.getInstance().getProperty("txtFileSrcPath").replaceAll("\\\\", "/").replaceAll("//", "/");
	private static final String RES_CONVERTED = PropertiesReader.getInstance().getProperty("txtFileTargetPath").replaceAll("\\\\", "/").replaceAll("//", "/");
	/**
	 * 
	 * 消费进程(转换文件)
	 * 
	 */
	@Override
	public void run() {
		while (true) {
			try {
				logger.info("【doConvertTxt】---抽取文件文本方法------开始 ");
				List<SolrQueue> solrs = getConvertTxtFileService().doQuery();
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
							status = getConvertTxtFileService().doCreateTxtFile(resDirFile);
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
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	
	private IConvertTxtFileService getConvertTxtFileService(){
		IConvertTxtFileService convertTxtFileService = null;
		try {
			convertTxtFileService = (IConvertTxtFileService) BeanFactoryUtil.getBean("convertTxtFileService");
		} catch (Exception e) {
			logger.debug("bean['fileService']尚未装载到容器中！");
			e.printStackTrace();
		}
		return convertTxtFileService;
	}

}
