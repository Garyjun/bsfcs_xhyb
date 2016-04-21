package com.brainsoon.fileService.component;

import com.brainsoon.fileService.po.DoFileQueue;

/**
 * @ClassName: IFileProcess
 * @Description: 文件处理接口
 * @author xiehewei
 * @date 2015年5月18日 上午9:11:52
 *
 */
public interface IFileProcess {

	public boolean strategyInterface(DoFileQueue task);
//	public boolean converFile(String fileType, String srcPath);
//	public boolean extractImage(String srcPath, String targetPath);
//	public boolean extractMetaData(String srcPath, String targetPath);
//	public boolean extractTxt(String srcPath, String targetPath);
}
