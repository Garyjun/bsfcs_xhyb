package com.brainsoon.fileService.component;

import com.brainsoon.fileService.po.DoFileQueue;

/**
 * @ClassName: ImplFileProcess
 * @Description: TODO
 * @author xiehewei
 * @date 2015年5月18日 上午11:16:02
 *
 */
public class ImplFileProcess implements IFileProcess {

	@Override
	public boolean strategyInterface(DoFileQueue task) {
		//return converFile(fileType, srcPath);
		return false;
	}

}
