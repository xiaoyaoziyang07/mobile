package com.wlan.comm;

import java.io.*;

public class gzipUtil{
	
	/**
	 * 将单个文件压缩成为.gz格式文件
	 * @param inputFileNamePath 全路径文件格式
	 * @return
	 * @throws Exception
	 * 
	 * sample /root/1332/aaaa.dat --> /root/1332/aaa.dat.gz
	 */
	public static boolean zipFile(String inputFileNamePath) throws Exception
    {
		boolean isRun = false;
		String command = "gzip "+inputFileNamePath;
		
		try{
			runCommands(command);
			isRun=true;
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			return isRun;
		}
        
    }
	
	public static boolean runComputer(){
		
		boolean isRun = false;
		String command = "hadoop jar hadoop/hadoop-*-examples.jar terasort  terasort/input-wlan001 terasort/output-wlan001";
		
		try{
			runCommands(command);
			isRun=true;
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			return isRun;
		}

	}
	
	/**
	 * 解压文件
	 * @param zipFileNamePath
	 * @param outPutpath
	 * @return
	 * @throws Exception
	 */
    public static boolean unzip(String zipFileNamePath) throws Exception
    {
		boolean isRun = false;
		String command = "gzip -dv "+zipFileNamePath;
		
		try{
			runCommands(command);
			isRun=true;
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			return isRun;
		}
    }
    
	/**
	 * 执行外部指令的通用算法
	 */
	private static void runCommands(String commands){
		BufferedReader bufferedReader2 = null;
		Process process2 = null; 
		try{
			
			String[] cmd = {"/bin/sh", "-c", commands};
			String ls_2 = "";
			
			process2 = Runtime.getRuntime().exec(cmd);
			
			bufferedReader2 = new BufferedReader(new InputStreamReader(process2.getInputStream()));
			
			while ( (ls_2 = bufferedReader2.readLine()) != null) {
			
				System.out.println(ls_2);
			
			}
			process2.waitFor();
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			
			if(process2!=null){
				process2.destroy();
			}

			try {
				if(bufferedReader2!=null){
					bufferedReader2.close();
				}
			} catch (IOException e) {
				
			}

		}
	}    
}