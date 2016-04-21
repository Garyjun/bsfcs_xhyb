﻿package com.brainsoon.common.taglib;


public interface IConstantsRepository {
	
	public void register(Class cls, ConstantsMap constantsMap);
	
	public ConstantsMap getConstantsMap(String className, String typeName);
	
	public String getConstantsDesc(String className, String typeName, Object value);
	
}
