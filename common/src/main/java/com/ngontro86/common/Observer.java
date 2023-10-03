package com.ngontro86.common;

public interface Observer<TYPE, OBJ> {
	public void update(TYPE stype, Object newObj, Object oldObj);
}
