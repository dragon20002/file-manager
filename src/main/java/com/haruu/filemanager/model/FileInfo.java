package com.haruu.filemanager.model;

public class FileInfo {

	private String name;

	private String encName;

	private long size;

	private boolean playable;

	public FileInfo() {
	}

	public FileInfo(String name, String encName, long size, boolean playable) {
		this.name = name;
		this.encName = encName;
		this.size = size;
		this.playable = playable;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getEncName() {
		return encName;
	}

	public void setEncName(String encName) {
		this.encName = encName;
	}

	public long getSize() {
		return size;
	}

	public void setSize(long size) {
		this.size = size;
	}

	public boolean isPlayable() {
		return playable;
	}

	public void setPlayable(boolean playable) {
		this.playable = playable;
	}

}
