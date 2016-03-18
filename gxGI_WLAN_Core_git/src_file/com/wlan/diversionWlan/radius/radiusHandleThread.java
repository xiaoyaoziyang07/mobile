package com.wlan.diversionWlan.radius;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.sql.SQLException;

import com.wlan.comm.gzipUtil;
import com.wlan.comm.hexUtil;
import com.wlan.comm.lastRadiusTimeStampBean;
import com.wlan.comm.natMsgNoConfBean;
import com.wlan.comm.operationUtil;
import com.wlan.comm.publicLoadConf;
import com.wlan.comm.radiusMsgNoConfBean;
import com.wlan.comm.timeUtil;
import com.wlan.db.DBConnection;
import com.wlan.diversionWlan.http.httpHandleThread;

public class radiusHandleThread {
	
	private String dstpath = "";
	private String msgno = "";
	
    private FileOutputStream fos = null;
    private OutputStreamWriter outs;

    
    private String fileName = "";
    
    //拆分部分临时变量群
    private StringBuffer tmpLine;
	private String[] tmpArray;
    private String _14bittime = "";
    private long endtime;
    private long InfaceEndtime;
    
    //本文件的记录数
    private long currentStore = 0;
    //总记录数，取出的
    private long totalStore = 0;
    //总记录数，计次的
    private long totalJc = 0;
    
    /**
     * 起止日期是否在同一天里边
     * isOneDays = false 在2天里
     * isOneDays = true  在1天里
     */
    private boolean isOneDays = false;
    
	/**
	 * 参数目标目录,msgNo,结束时间
	 * 目标取从上次到本次的时间
	 * @param d
	 * @param m
	 * @param e
	 */
	public radiusHandleThread(String d, String m, long e){
		dstpath = d;
		msgno = m;
		endtime = e;
		
		if(timeUtil.returnDate1970to14(publicLoadConf.lastRadiusTime).substring(0,8).equals(
				timeUtil.returnDate1970to14(this.endtime).substring(0, 8))) {
			isOneDays = true;
		}
		
		this._14bittime = operationUtil.DateTimeFunctionString();
	}
	
	public radiusHandleThread(){
		
	}
	
	/**
	 * 1.取出该时段的总记录数，跨天需要取2次
	 * 2.计算分页情况和每一文件的记录数
	 * 3.取出记录,逐条进行处理
	 */
	
	public void run() {

			System.out.println("--Radius,Handle");
			try{
				this.storeJc(publicLoadConf.lastRadiusTime, endtime);
				System.out.println("<total record>"+this.totalStore);
				if(this.totalStore>0) {
					
					this.buildFile();
					
					this.storeInvoke(publicLoadConf.lastRadiusTime, endtime);

					/**
					 * 最后一次有记录,就再关闭一次文件
					 */
					if(this.currentStore>0){
			            this.closeFile();
					}
					radiusMsgNoConfBean.writeNo(publicLoadConf.radiusMsgNo);
					lastRadiusTimeStampBean.writeTimes(endtime);
				}
		}catch(Exception e){
				e.printStackTrace();
			}
        

	     
	}
	
	/**
	 * 取出实际记录
	 * @param starttime
	 * @param endtime
	 */

	private void storeJc(long starttime,long endtime){
		DBConnection conn = new DBConnection();
		String sSQL;
		long tmpjc;
		try{
			
			conn.connect();
			if(this.isOneDays){
				//在1天里
				sSQL = "select count(*) from pmwlan.radius_"+timeUtil.returnDate1970to14(this.endtime).substring(0,8)+
						  " where time_stamp >= ? and time_stamp < ? and acct_status_type<>3";
				conn.preparedStatement = conn.connection.prepareStatement(sSQL);
				conn.preparedStatement.setLong(1, starttime);
				conn.preparedStatement.setLong(2, endtime);
				conn.resultSet = conn.preparedStatement.executeQuery();
				if (conn.resultSet.next()) {
					tmpjc = conn.resultSet.getLong(1);
					this.totalStore = tmpjc;
				}
			}else{
				//在2天里
				sSQL = "select count(*) from pmwlan.radius_"+timeUtil.returnDate1970to14(publicLoadConf.lastRadiusTime).substring(0,8)+
						  " where time_stamp >= ? and acct_status_type<>3";
				conn.preparedStatement = conn.connection.prepareStatement(sSQL);
				conn.preparedStatement.setLong(1, starttime);
				conn.resultSet = conn.preparedStatement.executeQuery();
				if (conn.resultSet.next()) {
					tmpjc = conn.resultSet.getLong(1);
					this.totalStore = tmpjc;
				}
				
				sSQL = "select count(*) from pmwlan.radius_"+timeUtil.returnDate1970to14(this.endtime).substring(0,8)+
						  " where time_stamp < ?  and acct_status_type<>3";
				conn.preparedStatement = conn.connection.prepareStatement(sSQL);
				conn.preparedStatement.setLong(1, endtime);
				conn.resultSet = conn.preparedStatement.executeQuery();
				if (conn.resultSet.next()) {
					tmpjc = conn.resultSet.getLong(1);
					this.totalStore = tmpjc+this.totalStore;
				}
			}
		}catch(Exception exx){
			exx.printStackTrace();
		
		}finally{
			try {
				conn.close(true);
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	
	/**
	 * 取出实际记录
	 * @param starttime
	 * @param endtime
	 */

	private void storeInvoke(long starttime,long endtime){
		DBConnection conn = new DBConnection();
		String sSQL;
		long tmpjc;
		try{
			radiusModel mm;
			conn.connect();
			if(this.isOneDays){
				//在1天里
				sSQL = "select username,frame_ip,acct_status_type,time_stamp from pmwlan.radius_"+timeUtil.returnDate1970to14(this.endtime).substring(0,8)+
						  " where time_stamp >= ? and time_stamp < ?  and acct_status_type<>3 order by time_stamp";
				conn.preparedStatement = conn.connection.prepareStatement(sSQL);
				conn.preparedStatement.setLong(1, starttime);
				conn.preparedStatement.setLong(2, endtime);
				conn.resultSet = conn.preparedStatement.executeQuery();
				while (conn.resultSet.next()) {
					mm = new radiusModel();
					mm.setUsername(conn.resultSet.getString(1));
					mm.setFrame_ip(conn.resultSet.getString(2));
					mm.setType(conn.resultSet.getInt(3));
					mm.setTime_stamp(conn.resultSet.getLong(4));
					this.takestr(mm);
				}
			}else{
				//在2天里
				sSQL = "select username,frame_ip,acct_status_type,time_stamp from pmwlan.radius_"+timeUtil.returnDate1970to14(publicLoadConf.lastRadiusTime).substring(0,8)+
						  " where time_stamp >= ?  and acct_status_type<>3 order by time_stamp";
				conn.preparedStatement = conn.connection.prepareStatement(sSQL);
				conn.preparedStatement.setLong(1, starttime);
				conn.resultSet = conn.preparedStatement.executeQuery();
				while (conn.resultSet.next()) {
					mm = new radiusModel();
					mm.setUsername(conn.resultSet.getString(1));
					mm.setFrame_ip(conn.resultSet.getString(2));
					mm.setType(conn.resultSet.getInt(3));
					mm.setTime_stamp(conn.resultSet.getLong(4));
					this.takestr(mm);

				}
				conn.close(false);
				
				sSQL = "select username,frame_ip,acct_status_type,time_stamp from pmwlan.radius_"+timeUtil.returnDate1970to14(this.endtime).substring(0,8)+
						  " where time_stamp < ?  and acct_status_type<>3 order by time_stamp";
				conn.preparedStatement = conn.connection.prepareStatement(sSQL);
				conn.preparedStatement.setLong(1, endtime);
				conn.resultSet = conn.preparedStatement.executeQuery();
				while (conn.resultSet.next()) {
					mm = new radiusModel();
					mm.setUsername(conn.resultSet.getString(1));
					mm.setFrame_ip(conn.resultSet.getString(2));
					mm.setType(conn.resultSet.getInt(3));
					mm.setTime_stamp(conn.resultSet.getLong(4));
					this.takestr(mm);

				}
			}
		}catch(Exception exx){
			exx.printStackTrace();
		
		}finally{
			try {
				conn.close(true);
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * 核心处理程序
	 * @param line
	 * @param _14bitfile
	 */
	private void takestr(radiusModel m) {
		try{
			
			outs.write(m.toString());
			this.outs.write("\r\n");
			InfaceEndtime = m.getTime_stamp();
			this.currentStore++;
			this.totalJc++;

			//当前只使用了记录数作为判断
			if(this.currentStore >= publicLoadConf.radiusConf.getSize()){
				this.closeFile();
				this.buildFile();
				this.currentStore=0;
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	
	/**
	 * 构建文件
	 * @param fl
	 */
	private void buildFile(){
		try{
			//文件Sample = NUU-770000000001-20141113124500.xml.gz
			this.fileName = "NUU-"+this.msgno+this.radiusMsgNo()+"-"+this._14bittime+".xml";
			fos = null;
			outs = null;
	        fos = new FileOutputStream(this.dstpath+this.fileName);
	        outs = new OutputStreamWriter(fos, "UTF-8"); 
	        //初始部分计算，临时用时间戳代替id
	        //数量计算
	        long jc;
	        if(this.totalStore - this.totalJc > publicLoadConf.radiusConf.getSize()) {
	        	jc = publicLoadConf.radiusConf.getSize();
	        }else{
	        	jc = this.totalStore - this.totalJc;
	        }
	        outs.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\r\n<info id=\""+System.currentTimeMillis()/1000+"\" type=\"net_login_info\" resultnum=\""+jc+"\">\r\n");
	        
		}catch(Exception e){
			e.printStackTrace();
		}
		
	}
	
	/**
	 * 关闭文件句柄
	 * 在文件轮换或读取完毕后
	 */
	
	private void closeFile(){
		try{
			this.outs.write("</info>");
			this.outs.close();
			this.fos.close();
			this.zipFile();
		}catch(Exception e){
			e.printStackTrace();
		}
		
	}
	
	/**
	 * 将文件进行压缩
	 */
	private void zipFile(){
		try {
			gzipUtil.zipFile(this.dstpath+this.fileName);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * 返回一个+1的No
	 * @return
	 */
	
	private String radiusMsgNo() {
		String str = "";
		try {
			publicLoadConf.radiusMsgNo ++;
			if(publicLoadConf.radiusMsgNo == 9999999999l) {
				publicLoadConf.radiusMsgNo = 1;
			}
			
			str = String.valueOf(publicLoadConf.radiusMsgNo);
			
			for(int i=str.length();i<10;i++) {
				str = "0"+str;
			}
		}catch(Exception e) {
			e.printStackTrace();
		}finally {
			return str;
		}
	}
	
	//从文件名中拆出对应的14位时间
	private String splitTimeStr(String oldstr) {
		return oldstr.substring(8,22);
	}
	
	private String supplyNo(int no){
		String ret = String.valueOf(no);
		
		for(int i=ret.length();i<4;i++) {
			ret = "0"+ret;
		}
		
		return ret;
	}
	
	public static void main(String[] args){

	}
	
}
