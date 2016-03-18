package com.wlan.diversionWlan.nat;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

import com.wlan.comm.gzipUtil;
import com.wlan.comm.hexUtil;
import com.wlan.comm.natMsgNoConfBean;
import com.wlan.comm.operationUtil;
import com.wlan.comm.publicLoadConf;
import com.wlan.comm.timeUtil;
import com.wlan.diversionWlan.http.httpHandleThread;

public class natHandleThread {
	
	private String srcpath = "";
	private String dstpath = "";
	private String msgno = "";
	
    private FileOutputStream outs = null;
    //记录当前文件的大小或行号
    private long currentStore = 0;
    //当前文件的行号
    private int fileNo = 0;
    
    private String fileName = "";
    
    //拆分部分临时变量群
    private StringBuffer tmpLine;
	private String[] tmpArray;
    private String _14bittime = "";
	
	
	public natHandleThread(String s,String d, String m){
		srcpath = s;
		dstpath = d;
		msgno = m;
		this._14bittime = operationUtil.DateTimeFunctionString();
	}
	
	public natHandleThread(){
		
	}
	
	public void run() {

		System.out.println("--NAT,Handle");

        java.io.File file; 
        File filefrom, flittle;
        long lcurrent = System.currentTimeMillis();
		
        this.fileNo = 0;
        
        file = new File(this.srcpath);
	    if(file.exists()){
	    	String[] contents = file.list();
	    	java.util.Arrays.sort(contents);
	        
//	    	if(contents.length>1){
//	    		this.buildFile();
//	    	}else{
//	    		return;
//	    	}
	    	
	    	for(int j=0;j<contents.length;j++){
	    		
	    		flittle = new File(this.srcpath+contents[j]);
	    		
	    		if(lcurrent - flittle.lastModified() > publicLoadConf.fileDuration){
	        		  
	    			try{
		    			//文件为WLANLOG_[14bittime].dat
	    	    		this.buildFile();
		    			fileReader(this.srcpath + contents[j]);
		                this.closeFile();
		    			
		    			filefrom = new File(this.srcpath + contents[j]);
		    			//删除
		    			filefrom.delete();
	    			}catch(Exception e){
	    				e.printStackTrace();
	    			}
	    		}
	    	}
//            this.closeFile();
			natMsgNoConfBean.writeNo(publicLoadConf.natMsgNo);

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
            	System.err.println("[Nat.fileReader]"+e.toString());
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
			 * src sample
			 * 20130521140936,10.26.86.211,49842,117.140.246.211,60990,1369118771851,,PRO01
			 */
			tmpArray = line.split(",");
			
			tmpLine=new StringBuffer();

			tmpLine.append(hexUtil.con10to16("1",2));
			tmpLine.append(hexUtil.IpStringToHex((tmpArray[1])));
			tmpLine.append(hexUtil.con10to16(tmpArray[2],4));
			tmpLine.append(hexUtil.IpStringToHex((tmpArray[3])));
			tmpLine.append(hexUtil.con10to16(tmpArray[4],4));
			tmpLine.append(hexUtil.IpStringToHex((tmpArray[5])));
			tmpLine.append(hexUtil.con10to16(tmpArray[6],4));
			tmpLine.append(hexUtil.con10to16(String.valueOf(Long.parseLong(tmpArray[7])/1000),16));
			tmpLine.append(hexUtil.con10to16("0",8));
			
//			System.out.println(tmpLine.length());
			
			byte[] b1=new byte[62];
			b1 = hexUtil.hexStringToBytes(tmpLine.toString());
			outs.write(b1);
			
			if(publicLoadConf.natConf.getType()==1) {
				//按照字节数
				this.currentStore = this.currentStore + b1.length;
			}else{
				//按照记录数
				this.currentStore = this.currentStore+1;
			}
			
			if(this.currentStore > publicLoadConf.natConf.getSize() && publicLoadConf.natConf.getSize()>0){
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
			//文件Sample = NATLOG_SDJN09_20100601110100_0009.DAT.gz
			this.fileName = "NATLOG_"+this.msgno+this.natMsgNo()+"_"+this._14bittime+"_"+this.supplyNo(this.fileNo)+".DAT";
			outs = null;
	        outs = new FileOutputStream(this.dstpath+this.fileName);
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
	
	/**
	 * 返回一个+1的No
	 * @return
	 */
	
	private String natMsgNo() {
		String str = "";
		try {
			publicLoadConf.natMsgNo ++;
			if(publicLoadConf.natMsgNo == 9999999999l) {
				publicLoadConf.natMsgNo = 1;
			}
			
			str = String.valueOf(publicLoadConf.natMsgNo);
			
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
		long ll = System.currentTimeMillis();
		natHandleThread h = new natHandleThread(publicLoadConf.natConfList.get(0).getSrcDirect(),publicLoadConf.natConfList.get(0).getDstDirect(),publicLoadConf.natConfList.get(0).getMsgno());
		h.run();
		System.out.println(System.currentTimeMillis() - ll);
	}
	
}
