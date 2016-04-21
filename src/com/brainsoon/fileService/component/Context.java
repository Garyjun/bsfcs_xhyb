package com.brainsoon.fileService.component;

import com.brainsoon.fileService.po.DoFileQueue;

/**
 * @ClassName: Context
 * @Description: TODO
 * @author xiehewei
 * @date 2015年5月18日 下午4:25:09
 *
 */
public class Context {

	private IFileProcess fileProcess;

    /**
     * 构造函数，传入一个具体策略对象
     * @param strategy    具体策略对象
     */
    public Context(IFileProcess fileProcess){
        this.fileProcess = fileProcess;
    }
    /**
     * 策略方法
     */
    public boolean contextInterface(DoFileQueue task){
    	return fileProcess.strategyInterface(task);
    }
	
}
