package com.brainsoon.fileService.component;

import com.brainsoon.fileService.po.DoFileQueue;

/**
 * @ClassName: ExtractMetaData
 * @Description: 抽取元数据
 * @author xiehewei
 * @date 2015年5月18日 上午10:32:21
 *
 */
public class ExtractMetaData implements IFileProcess {

	@Override
	public boolean strategyInterface(DoFileQueue task){
		int i = (int)(Math.random()*2);
		return i==1?true:false;		
	}
}
