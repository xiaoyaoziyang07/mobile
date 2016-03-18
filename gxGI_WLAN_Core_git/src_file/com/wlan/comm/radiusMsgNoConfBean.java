package com.wlan.comm;

import java.io.*;

public class radiusMsgNoConfBean {
	
	public static String filepath = System.getProperty("user.dir").replace("\\", "/") + "/deploy/radiusmsgno.csv";
	
	public radiusMsgNoConfBean() {
		
	}
	
	public static long readNo() {
		long retNo = 1;
		try {
	        //读取文件,一遍读一遍处理
	        File f = new File(filepath);
	        String line = "";
	        if(!f.exists()){
	          System.out.println("--- file don't exists!");
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
			retNo = 1;
		}finally{
			return retNo;
		}
		
	}
	
	/**
	 * 对其补位
	 * @param no
	 */
	public static void writeNo(long no) {
		
		try {
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
	
}
