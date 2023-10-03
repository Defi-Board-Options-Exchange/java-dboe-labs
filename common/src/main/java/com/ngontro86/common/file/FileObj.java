package com.ngontro86.common.file;

import java.io.Serializable;

public class FileObj implements Serializable{
	private static final long serialVersionUID = 1L;
	private long localTimestamp;
	private Object data;
	
	public FileObj(long timestamp, Object obj) {
		this.localTimestamp = timestamp;
		this.data = obj;
	}

	public long getLocalTimestamp() { return localTimestamp; }

	public Object getData() { return data; }
}

