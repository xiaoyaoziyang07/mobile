package com.wlan.diversionWlan.http;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

import com.sun.jmx.remote.internal.ArrayQueue;
import com.wlan.comm.gzipUtil;
import com.wlan.comm.operationUtil;
import com.wlan.comm.publicLoadConf;
import com.wlan.comm.timeUtil;

public class httpHandleThread implements Runnable{
	
	//最终输出文件的分隔符
	private final String splitString = "|";
	//每个字段前后的符号
//	private final String columnString = "'";
	
	private String srcpath = "";
	private String dstpath = "";
	private String msgno = "";
	
    private FileOutputStream fos = null;   
    private OutputStreamWriter outs = null;
    //记录当前文件的大小或行号
    private long currentStore = 0;
    //当前文件的行号
    private int fileNo = 0;
    
    private String fileName = "";
    
    //拆分部分临时变量群
    private StringBuffer tmpLine;
	private String[] tmpArray;
    private String _14bittime = "";
    
    private List<File> contents;
    private int deviceNum;
	
	
//	public httpHandleThread( String s, String d, String m){
//		srcpath = s;
//		dstpath = d;
//		msgno = m;
//		this._14bittime = operationUtil.DateTimeFunctionString();
//	}
	
	public httpHandleThread(){
		srcpath = publicLoadConf.httpConf.getSrcDirect();
		dstpath = publicLoadConf.httpConf.getDstDirect();
		msgno = publicLoadConf.httpConf.getMsgno();
		_14bittime = operationUtil.DateTimeFunctionString();
		deviceNum = srcpath.split("\\|").length;
	}
	
	@Override
	public void run() {
		
		System.out.println("--HTTP,Handle");
		
        File file; 
        File filefrom, flittle;
        long lcurrent = System.currentTimeMillis();
		
        this.fileNo = 0;
        
        file = new File(srcpath);
        System.out.println(file.getAbsolutePath());
	    if(file.exists()){
	    	
	    	contents = fileWalker(srcpath);
//	    	java.util.Arrays.sort(contents);
	    	Collections.sort(contents, new Comparator<File>() {

				@Override
				public int compare(File file1, File file2) {
					return splitTimeStr(file1.getName()).compareTo(splitTimeStr(file1.getName()));
				}
			});
	        
	    	if(contents.size()>1) {
	    		this.buildFile();
	    	}else{
	    		return;
	    	}
	    	
	    	for(int j=0;j<contents.size();j++){
	    		
	    		flittle = new File(contents.get(j).getAbsolutePath());
	    		
	    		if(lcurrent - flittle.lastModified() > publicLoadConf.fileDuration){
	        		  
	    			try{
		    			//文件为WLANLOG_[14bittime].dat
		    			fileReader(contents.get(j));
		    			
		    			filefrom = new File(contents.get(j).getAbsolutePath());
		    			//删除
		    			filefrom.delete();
	    			}catch(Exception e){
	    				e.printStackTrace();
	    			}
	    		}
	    	}
            this.closeFile();
	    	
	     }
	}
	
	private List<File> fileWalker(String srcpath) {
		List<File> files = new ArrayList<File>();
		String[] paths = srcpath.split("\\|");
		for(String p : paths){
			File path = new File(p);
			if(path.exists()){
				File[] contents = path.listFiles();
				for(File f : contents){
					if(f.isFile()){
						files.add(f);
					}
					if(f.isDirectory()){
						files.addAll(fileWalker(f.getAbsolutePath()));
					}
				}
			}
		}
		return files;
	}

	private LinkedList<String> getFileNames(){
		HashSet<String> set = new HashSet<String>();
		for(File file : contents){
			set.add(splitTimeStr(file.getName()));
		}
		LinkedList<String> fileNames = new LinkedList<String>();
		fileNames.addAll(set);
		return fileNames;
	}
	/**
	 * 单独的文件读取,按照拆分规则进行拆分
	 * 处理步骤
	 * 1.文件读取
	 * 2.文件名称构建
	 * 3.按照大小或进行拆分
	 * 4.对另存后的文件进行压缩
	 * @param filename
	 * @param _14bittime
	 */
	private void fileReader(File f){
        String line = "";
        if(!f.exists()){
        	
          System.out.println("--- file don't exists!"+f.getName());
        
        }else{
            //文件存在,读取文件
        	InputStreamReader read = null;
        	BufferedReader reader = null;
            try {
              read = new InputStreamReader(new FileInputStream(f), "GBK");
              reader = new BufferedReader(read);
              int i=0;
//              StringBuffer sb = new StringBuffer();
              while ((line = reader.readLine()) != null) {
            	  i++;
            	  if(i>fileNo){
            		  this.takestr(line);
            	  }
              }

              System.out.println("[totalJC]"+f.getName()+" JC:"+i);
              
            }catch(Exception e){
            	System.err.println("[Http.fileReader]"+e.toString());
            }finally{
            	if(reader!=null){
                    try {
						reader.close();
					} catch (IOException e) {
					}
            	}
            	if(read!=null){
                    try {
						read.close();
					} catch (IOException e) {
					}
            	}
            }
        }
	}
	
	/**
	 * 核心处理程序
	 * @param line
	 * @param _14bitfile
	 */
	private void takestr(String line) {
		try{
			/**
			 * sample
			 * 1387954250.863987|$|117.140.249.237|$|37472|$|211.151.151.6
			 * |$|80|$|http://www.umeng.com/app_logs|$|
			 */
			
			tmpArray = line.split("\\|");
			
			tmpLine=new StringBuffer();
			//起止时间,暂时一致
			tmpLine.append(timeUtil.format2JavaTime(tmpArray[5]));
			tmpLine.append(splitString);
			tmpLine.append(timeUtil.format2JavaTime(tmpArray[6]));
			tmpLine.append(splitString);
			tmpLine.append(tmpArray[0]);
			tmpLine.append(splitString);
			tmpLine.append(tmpArray[1]);
			tmpLine.append(splitString);
			tmpLine.append(tmpArray[2]);
			tmpLine.append(splitString);
			tmpLine.append(tmpArray[3]);
			tmpLine.append(splitString);
			tmpLine.append(tmpArray[7]);
			tmpLine.append(splitString);
			tmpLine.append(tmpArray[4]);
			tmpLine.append(splitString);
			for(int i=0;i<8;i++){
				tmpLine.append(splitString);
			}
			
			this.outs.write(tmpLine.toString());
			this.outs.write("\r\n");
			
			if(Integer.parseInt(publicLoadConf.httpConf.getConfig().split("\\|")[0])==1) {
				//按照字节数
				this.currentStore = this.currentStore + tmpLine.toString().length();
			}else{
				//按照记录数
				this.currentStore = this.currentStore+1;
			}
			
			//超过阈值，构建新的文件
			if(this.currentStore > Integer.parseInt(publicLoadConf.httpConf.getConfig().split("\\|")[1]) && Integer.parseInt(publicLoadConf.httpConf.getConfig().split("\\|")[1])>0){
				this.closeFile();
				this.fileNo++;
				this.buildFile();
				this.currentStore = 0;
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
			this.fileName = "AHTTPP"+msgno+"01D"+getFileNames().getFirst()+"E"+supplyNo(fileNo)+".txt";
	        fos = new FileOutputStream(dstpath+File.separator+fileName,true);
		    outs = new OutputStreamWriter(fos, "UTF-8");
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
			this.outs.close();
			this.fos.close();
			this.zipFile();
		}catch(Exception e){
			e.printStackTrace();
		}
		
	}
	
	private void zipFile(){
		try {
			gzipUtil.zipFile(this.dstpath+this.fileName);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	//从文件名中拆出对应的14位时间
	private String splitTimeStr(String oldstr) {
		return oldstr.substring(14,28);
	}
	
	private String supplyNo(int no){
		String ret = String.valueOf(no);
		
		for(int i=ret.length();i<3;i++) {
			ret = "0"+ret;
		}
		
		return ret;
	}
	
	public static void main(String[] args){
		httpHandleThread hht = new httpHandleThread();
		String fileName = hht.splitTimeStr("WLANLOG_PRO07_20140928170500.dat");
		System.out.println(fileName);
	}
}
