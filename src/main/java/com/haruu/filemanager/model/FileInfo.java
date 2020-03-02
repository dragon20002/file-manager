package com.haruu.filemanager.model;

public class FileInfo {
	private String name;

	private long size;

	private String rootDirName;
	
	private String encName;

	public FileInfo() {
	}

	public FileInfo(String name, long size, String rootDirName, String encName) {
		this.name = name;
		this.size = size;
		this.rootDirName = rootDirName;
		this.encName = encName;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public long getSize() {
		return size;
	}

	public void setSize(long size) {
		this.size = size;
	}

	public String getRootDirName() {
		return rootDirName;
	}

	public void setRootDirName(String rootDirName) {
		this.rootDirName = rootDirName;
	}

	public String getEncName() {
		return encName;
	}

	public void setEncName(String encName) {
		this.encName = encName;
	}

	@Override
	public String toString() {
		return "FileInfo [name=" + name + ", size=" + size + ", rootDirName=" + rootDirName + ", encName=" + encName
				+ "]";
	}

}
