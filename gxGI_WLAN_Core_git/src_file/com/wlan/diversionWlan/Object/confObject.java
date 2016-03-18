package com.wlan.diversionWlan.Object;

public class confObject {
	/**
	 * 如果为radius记录
	 * type=时间系数
	 * size=记录数
	 */
	/**
	 * 1.文件字节数
	 * 2.文件记录数
	 */
	private int type = 0;
	/**
	 * type==1 为字节数
	 * type==2 为记录数
	 */
	private long size = 0;
	
	
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	public long getSize() {
		return size;
	}
	public void setSize(long size) {
		this.size = size;
	}
}
