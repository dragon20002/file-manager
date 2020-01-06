package com.haruu.filemanager.model;

public class FileInfo {
	private String name;

	private long size;

	private String rootDirName;

	public FileInfo() {
	}

	public FileInfo(String name, long size, String rootDirName) {
		this.name = name;
		this.size = size;
		this.rootDirName = rootDirName;
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

}
