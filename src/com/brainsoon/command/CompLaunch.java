package com.brainsoon.command;

import java.io.File;
import java.sql.SQLException;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.apache.tuscany.sca.node.Contribution;
import org.apache.tuscany.sca.node.ContributionLocationHelper;
import org.apache.tuscany.sca.node.Node;
import org.apache.tuscany.sca.node.NodeFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.support.FileSystemXmlApplicationContext;

import com.brainsoon.common.support.GlobalAppCacheMap;
import com.brainsoon.common.util.WebAppUtils;
import com.brainsoon.common.util.dofile.util.ConstantsDef;
import com.brainsoon.common.util.dofile.util.DoFileException;
import com.brainsoon.common.util.dofile.util.PropertiesReader;
import com.brainsoon.fileService.service.IConvertTxtFileService;
import com.brainsoon.fileService.service.IFileService;
import com.brainsoon.fileService.thread.ConvertTxtThread;
import com.brainsoon.fileService.thread.ProcessFileC;
import com.brainsoon.fileService.thread.ProcessFileP;
import com.brainsoon.fileService.thread.ProcessFileTask;
import com.channelsoft.appframe.utils.BeanFactoryUtil;

/**
 * <dl>
 * <dt>CompLaunch</dt>
 * <dd>Description:组件命令行启动</dd>
 * <dd>Company: 博云科技</dd>
 * <dd>CreateDate: 2013-3-7</dd>
 * </dl>
 *
 * @author 唐辉
 * @update tanghui 2013-04-11 将为了将配置文件打包在jar外，修改了加载配置文件的路径
 */
public class CompLaunch {

	private static Logger logger = Logger.getLogger(CompLaunch.class);
	//配置应用根路径
	private static String baseProjectPath = WebAppUtils.getProPath();
	private static final String SERVER = PropertiesReader.getInstance().getProperty(ConstantsDef.server);
	
	@SuppressWarnings("unused")
	public static void main(String[] args) throws Exception {
		logger.info("开始启动组件服务 .......");

		//加载日志
		initLog4jConfig();

		//初始化bean
		initBeanConfig();

		//配置加载的composite文件
        Node node = loadScaService("fcs.composite");
        
        //创建表dofile_queue，dofile_history
        isCreatTable();
        
    	//判断是否需要对状态为：加工中 的列表进行处理（解决由于重启服务等造成的数据停止转换，
  		//这样就会出现部分记录可能永远也无法被转换了，因为生产线程只处理：待转换和需要重试的记录）
  		//判断缓存中是否有该KEY，如果没有则说明是刚启动应用，否则不是
  		//ConverFilePStuas == 0  为需要对转换中的记录进行重新处理
          String openOfficePath = PropertiesReader.getInstance().getProperty(ConstantsDef.openOfficePath);
          if(!new File(openOfficePath).exists()){
          	throw new DoFileException("Office安装路径配置不正确，请检查。");
          }
          String openOfficePort = PropertiesReader.getInstance().getProperty(ConstantsDef.openOfficePort);
          if(StringUtils.isBlank(openOfficePort)){
          	throw new DoFileException("Office未配置端口，请检查。");
          }
          GlobalAppCacheMap.putKey("openOfficePath", openOfficePath);
          GlobalAppCacheMap.putKey("openOfficePort", openOfficePort);
  		  GlobalAppCacheMap.putKey("ConverFilePStuas", "0");

        if("1".equals(SERVER)){
        	logger.info("开始启动主机服务");
        	new Thread(new ProcessFileP()).start();
        }else if("2".equals(SERVER)){
        	logger.info("开始启动从机服务");
        	new Thread(new ProcessFileTask()).start();
        	new Thread(new ProcessFileC()).start();
        }else if("3".equals(SERVER)){
        	logger.info("开始启动主-从机服务");
        	new Thread(new ProcessFileP()).start();
        	new Thread(new ProcessFileC()).start();
        }else if("4".equals(SERVER)){
        	logger.info("开始启动资源文件转txt服务");
        	new Thread(new Runnable() {
				@Override
				public void run() {
					while(true){
						try {
							logger.info("正在进行资源文件转txt服务");
							Thread.sleep(100000);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}
			}).start();
        }
	}

	private static Node loadScaService(String compConfFile) {
		logger.info("初始化"+compConfFile+"服务 ...");
		String contribution = ContributionLocationHelper.getContributionLocation(CompLaunch.class);
        Node node = NodeFactory.newInstance().createNode(compConfFile, new Contribution("test", contribution));
        node.start();
        logger.info("服务 初始化完毕!!!");
		return node;
	}

	private static void initLog4jConfig() throws Exception {
		try {
			//String logConf = baseProjectPath + "log4j.xml";
		    String logConf = baseProjectPath + "log4j.properties";
			if(!new File(logConf).exists()){
				logger.error("加载日志配置文件异常：应用根目录不存在或者未配置. " + logConf);
			}
			logger.debug("加载日志配置文件 ...");
			//DOMConfigurator.configure(logConf);
			PropertyConfigurator.configure(logConf);
		} catch (Exception e) {
			logger.error("加载日志配置文件异常：", e);
			throw e;
		}
	}


	private static void initBeanConfig() throws BeansException {
		logger.info("开始加载spring bean ...");
		try {
			if(StringUtils.isNotBlank(baseProjectPath)){
				//加载其他 applicationContext_xxx.xml 文件,如果有多个,可以直接在以下的数组中添加
				String[] appConfig =
					   {
						"file:" + baseProjectPath + "applicationContext.xml",
						"file:" + baseProjectPath + "applicationContext-fcs.xml"
						};
				//加载spring applicationContext.xml 文件
				BeanFactoryUtil.setContext(new FileSystemXmlApplicationContext(appConfig));
			}else{
				logger.error("加载spring bean异常：应用根目录不存在或者未配置.");
			}
		} catch (BeansException e) {
			logger.error("加载spring bean异常：应用根目录不存在或者未配置.请确认["+ baseProjectPath + "applicationContext.xml]和["+ baseProjectPath + "applicationContext-fcs.xml]文件路径是否正确！");
			logger.error("加载spring bean异常：", e);
			throw e;
		}
		logger.info("加载spring bean完成!");
	}
	
	
	private static void isCreatTable() throws SQLException{
		logger.info("开始 创建表 dofile_queue，dofile_history  ...");
		IFileService fileService = (IFileService)BeanFactoryUtil.getBean("fileService");
		fileService.doCreatTable();
		
		logger.info("开始抽文本逻辑  开始..."); 
		//IConvertTxtFileService convertService = (IConvertTxtFileService)BeanFactoryUtil.getBean("convertTxtFileService");
		//convertService.doConvertTxt();
		new Thread(new ConvertTxtThread()).start();
	}
}
