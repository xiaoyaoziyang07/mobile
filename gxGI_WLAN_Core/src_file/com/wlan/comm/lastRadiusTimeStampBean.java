package com.wlan.comm;

import java.io.*;

public class lastRadiusTimeStampBean {
	
	public static String filepath = System.getProperty("user.dir").replace("\\", "/") + "/deploy/lastRadius.csv";
	
	public lastRadiusTimeStampBean() {
		
	}
	
	public static long readTimes() {
		long retNo = 1;
		try {
	        //读取文件,一遍读一遍处理
	        File f = new File(filepath);
	        String line = "";
	        if(!f.exists()){
	          System.out.println("--- file don't exists!");
	          long l13bittime = System.currentTimeMillis();
	          l13bittime = l13bittime/1000 - ((l13bittime / 1000) % (publicLoadConf.radiusConf.getType()*60)) - publicLoadConf.radiusConf.getType()*60;
	          retNo= l13bittime  * 1000;

	        }else{
	            //文件存在,读取文件
	            InputStreamReader read = new InputStreamReader(new FileInputStream(f), "GBK");
	            BufferedReader reader = new BufferedReader(read);
	              while ( (line = reader.readLine()) != null) {
	            	  retNo = Long.parseLong(line);
	              }
	         }
		}catch(Exception e) {
			e.printStackTrace();
//			如果没有该文件将最近一个一刻钟的开始作为起点
			long l13bittime = System.currentTimeMillis();
			l13bittime = l13bittime/1000 - ((l13bittime / 1000) % (publicLoadConf.radiusConf.getType()*60)) - publicLoadConf.radiusConf.getType()*60;
			retNo= l13bittime  * 1000;
			
		}finally{
			return retNo;
		}
		
	}
	
	/**
	 * 对其补位
	 * @param no
	 */
	public static void writeTimes(long no) {
		
		try {
			
			publicLoadConf.lastRadiusTime = no;
			
			FileWriter theFile = new FileWriter(filepath, false); //true追加文件false覆盖文件
			BufferedWriter outs = new BufferedWriter(theFile);
	
			outs.write(String.valueOf(no));
			outs.write("\n");
	
			outs.close();
			theFile.close();
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args){
		long l = 1415948413930l;
		System.out.println(l);
		System.out.println(l/1000);
		System.out.println(((l / 1000) % 900));
		l = l/1000 - ((l / 1000) % 900);
		System.out.println(l);
		System.out.println(timeUtil.returnDate1970to14(l));
	}
	
}
