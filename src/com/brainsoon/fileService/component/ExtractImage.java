package com.brainsoon.fileService.component;

import com.brainsoon.common.util.dofile.image.ImgCoverUtil;
import com.brainsoon.fileService.po.DoFileQueue;
import com.sun.xml.internal.ws.util.MetadataUtil;

/**
 * @ClassName: ExtractImage
 * @Description: 抽取封面
 * @author xiehewei
 * @date 2015年5月18日 上午10:14:54
 *
 */
public class ExtractImage implements IFileProcess {

	@Override
	public boolean strategyInterface(DoFileQueue task){
		//ImgCoverUtil.conver2Other(srcPath, destPath);
		int i = (int)(Math.random()*2);
		return i==1?true:false;
	}

}
