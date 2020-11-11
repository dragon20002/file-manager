package com.haruu.filemanager.model;

public class FileInfo {
	private String name;

	private long size;

	private String rootDirName;
	
	private String dirPath;
	
	private String encName;

	private boolean isDir;

	public FileInfo() {
	}

	public FileInfo(String name, long size, String rootDirName, String dirPath, String encName, boolean isDir) {
		this.name = name;
		this.size = size;
		this.rootDirName = rootDirName;
		this.dirPath = dirPath;
		this.encName = encName;
		this.isDir = isDir;
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

	public String getDirPath() {
		return dirPath;
	}

	public void setDirPath(String dirPath) {
		this.dirPath = dirPath;
	}

	public String getEncName() {
		return encName;
	}

	public void setEncName(String encName) {
		this.encName = encName;
	}

	public boolean isDir() {
		return isDir;
	}

	public void setDir(boolean isDir) {
		this.isDir = isDir;
	}

	@Override
	public String toString() {
		return "FileInfo [name=" + name + ", size=" + size + ", rootDirName=" + rootDirName + ", dirPath=" + dirPath
				+ ", encName=" + encName + ", isDir=" + isDir + "]";
	}

}
