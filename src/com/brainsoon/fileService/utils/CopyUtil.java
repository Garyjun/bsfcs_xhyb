package com.brainsoon.fileService.utils;
import com.brainsoon.fileService.po.DoFileHistory;
import com.brainsoon.fileService.po.DoFileQueue;

public class CopyUtil {
	public static void Copy(DoFileQueue queue, DoFileHistory history) throws Exception {
		history.setActionConveredfileUrl(queue.getActionConveredfileUrl());
		history.setActionImageUrl(queue.getActionImageUrl());
		history.setActionMatedataUrl(queue.getActionMatedataUrl());
		history.setActionTxtUrl(queue.getActionTxtUrl());
		history.setAlpha(queue.getAlpha());
		history.setColor(queue.getColor());
		history.setCreateTime(queue.getCreateTime());
		history.setFileFormat(queue.getFileFormat());
		history.setFileId(queue.getFileId());
		history.setFontName(queue.getFontName());
		history.setFontSize(queue.getFontSize());
		history.setImageWH(queue.getImageWH());
		history.setIsBold(queue.getIsBold());
		history.setPendingType(queue.getPendingType());
		history.setPlatformId(queue.getPlatformId());
		history.setPosition(queue.getPosition());
		history.setPriority(queue.getPriority());
		history.setResId(queue.getResId());
		history.setResultConveredfilePath(queue.getResultConveredfilePath());
		history.setResultImagePath(queue.getResultImagePath());
		history.setResultMatedata(queue.getResultMatedata());
		history.setResultTxt(queue.getResultTxt());
		history.setRetryNum(queue.getRetryNum());
		history.setSrcPath(queue.getSrcPath());
		history.setStatusConvered(queue.getStatusConvered());
		history.setStautsExtractImage(queue.getStautsExtractImage());
		history.setStautsExtractMatedata(queue.getStautsExtractMatedata());
		history.setStautsExtractTxt(queue.getStautsExtractTxt());
		history.setSyncStautsConvered(queue.getSyncStautsConvered());
		history.setSyncStautsImage(queue.getSyncStautsImage());
		history.setSyncStautsMatedata(queue.getSyncStautsMatedata());
		history.setSyncStautsTxt(queue.getSyncStautsTxt());
		history.setTimestamp(queue.getTimestamp());
		history.setTxtName(queue.getTxtName());
		history.setUpdateTime(queue.getUpdateTime());
		history.setWmImage(queue.getWmImage());
		history.setObjectId(queue.getObjectId());
	}
}