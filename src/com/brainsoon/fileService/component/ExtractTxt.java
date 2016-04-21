package com.brainsoon.fileService.component;

import com.brainsoon.fileService.po.DoFileQueue;

/**
 * @ClassName: ExtractTxt
 * @Description: 抽取文本
 * @author xiehewei
 * @date 2015年5月18日 上午10:31:03
 *
 */
public class ExtractTxt implements IFileProcess {

	@Override
	public boolean strategyInterface(DoFileQueue task) {
		int i = (int)(Math.random()*2);
		return i==1?true:false;
	}
}
