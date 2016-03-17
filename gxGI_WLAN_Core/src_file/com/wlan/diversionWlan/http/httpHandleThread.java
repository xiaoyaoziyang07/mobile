package com.wlan.diversionWlan.http;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

import com.wlan.comm.gzipUtil;
import com.wlan.comm.operationUtil;
import com.wlan.comm.publicLoadConf;
import com.wlan.comm.timeUtil;

public class httpHandleThread {
	//最终输出文件的分隔符
	private final String splitString = ",";
	//每个字段前后的符号
	private final String columnString = "'";
	
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
	
	
	public httpHandleThread(String s,String d, String m){
		srcpath = s;
		dstpath = d;
		msgno = m;
		this._14bittime = operationUtil.DateTimeFunctionString();
	}
	
	public httpHandleThread(){
		
	}
	
	public void run() {
		
		System.out.println("--HTTP,Handle");
		
        java.io.File file; 
        File filefrom, flittle;
        long lcurrent = System.currentTimeMillis();
		
        this.fileNo = 0;
        
        file = new File(this.srcpath);
	    if(file.exists()){
	    	String[] contents = file.list();
	    	java.util.Arrays.sort(contents);
	        
	    	if(contents.length>1) {
	    		this.buildFile();
	    	}else{
	    		return;
	    	}
	    	
	    	for(int j=0;j<contents.length;j++){
	    		
	    		flittle = new File(this.srcpath+contents[j]);
	    		
	    		if(lcurrent - flittle.lastModified() > publicLoadConf.fileDuration){
	        		  
	    			try{
		    			//文件为WLANLOG_[14bittime].dat
		    			fileReader(this.srcpath + contents[j]);
		    			
		    			filefrom = new File(this.srcpath + contents[j]);
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
	private void fileReader(String filename){
        File f = new File(filename);
        String line = "";
        if(!f.exists()){
        	
          System.out.println("--- file don't exists!"+filename);
        
        }else{
            //文件存在,读取文件
        	InputStreamReader read = null;
        	BufferedReader reader = null;
            try {
              read = new InputStreamReader(new FileInputStream(f), "GBK");
              reader = new BufferedReader(read);
              int i=0;
              StringBuffer sb = new StringBuffer();
              while ((line = reader.readLine()) != null) {
            	  i++;
            	  this.takestr(line);
              }

              System.out.println("[totalJC]"+filename+" JC:"+i);
              
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
			
			tmpArray = line.split("\\|\\$\\|");
			
			tmpLine=new StringBuffer();
			//起止时间,暂时一致
			tmpLine.append(this.columnString+timeUtil.returnDate1970to14Supper(tmpArray[0])+this.columnString);
			tmpLine.append(this.splitString);
			tmpLine.append(this.columnString+tmpArray[1]+this.columnString);
			tmpLine.append(this.splitString);
			tmpLine.append(this.columnString+tmpArray[2]+this.columnString);
			tmpLine.append(this.splitString);
			tmpLine.append(this.columnString+tmpArray[3]+this.columnString);
			tmpLine.append(this.splitString);
			tmpLine.append(this.columnString+tmpArray[4]+this.columnString);
			tmpLine.append(this.splitString);
			tmpLine.append(this.columnString+tmpArray[5]+this.columnString);
			tmpLine.append(this.splitString);
			tmpLine.append(this.columnString+"http"+this.columnString);
			tmpLine.append(this.splitString);
			tmpLine.append(this.columnString+"3"+this.columnString);
			
			
			this.outs.write(tmpLine.toString());
			this.outs.write("\r\n");
			
			if(publicLoadConf.httpConf.getType()==1) {
				//按照字节数
				this.currentStore = this.currentStore + tmpLine.toString().length();
			}else{
				//按照记录数
				this.currentStore = this.currentStore+1;
			}
			
			//超过阈值，构建新的文件
			if(this.currentStore > publicLoadConf.httpConf.getSize() && publicLoadConf.httpConf.getSize()>0){
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
			this.fileName = "AHTTPP"+this.msgno+"01D"+this._14bittime+"E"+this.supplyNo(this.fileNo)+".txt";
			fos = null;
			outs = null;
	        fos = new FileOutputStream(this.dstpath+this.fileName);   
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
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	//从文件名中拆出对应的14位时间
	private String splitTimeStr(String oldstr) {
		return oldstr.substring(8,22);
	}
	
	private String supplyNo(int no){
		String ret = String.valueOf(no);
		
		for(int i=ret.length();i<3;i++) {
			ret = "0"+ret;
		}
		
		return ret;
	}
	
	public static void main(String[] args){
		httpHandleThread h = new httpHandleThread(publicLoadConf.httpConfList.get(0).getSrcDirect(),publicLoadConf.httpConfList.get(0).getDstDirect(),publicLoadConf.httpConfList.get(0).getMsgno());
		h.run();
	}
	
}
