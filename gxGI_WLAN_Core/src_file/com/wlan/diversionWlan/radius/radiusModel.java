package com.wlan.diversionWlan.radius;

import com.wlan.comm.timeUtil;

public class radiusModel {
	public String username;
	public String frame_ip;
	public int type;
	public long time_stamp;
	public String time_stamp_str;
	
	public String toString() {
		return "<log account=\""+username+"\" " +
				"accountType=\"2\" loginType=\"3\" "+
				"priIpAddr=\""+frame_ip+"\" "+
				"pubIpAddr=\"\" "+
				"LineTimeType=\""+type+"\" "+
				"LineTime=\""+time_stamp_str+"\" "+
				"LAC=\"\" "+
				"SAC=\"\"/>";
	}
	
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getFrame_ip() {
		return frame_ip;
	}
	public void setFrame_ip(String frame_ip) {
		this.frame_ip = frame_ip;
	}
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	public long getTime_stamp() {
		return time_stamp;
	}
	public void setTime_stamp(long time_stamp) {
		this.time_stamp = time_stamp;
		this.time_stamp_str = timeUtil.returnDate1970to20(time_stamp);
	}
}
