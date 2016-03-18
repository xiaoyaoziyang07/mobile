package com.wlan.diversionWlan.Thread;

import com.wlan.comm.publicLoadConf;
import com.wlan.diversionWlan.http.httpHandleThread;
import com.wlan.diversionWlan.nat.natHandleThread;
import com.wlan.diversionWlan.other.OtherHandleThread;
import com.wlan.diversionWlan.radius.radiusHandleThread;


/**
 * 
 * 本进程反复读取目录下的文件进行处理
 * @author Administrator
 *
 */

public class wlanThreadClass extends Thread {
	
	//implements Runnable
	private String srcPath = "";
	private String dstPath = "";
	//1 - http 2 - nat
	private int type=0;
	private String threadName = "";
	private String msgno;
	
	public wlanThreadClass(String s, String d, String m, int t, String n){
		this.srcPath = s;
		this.dstPath = d;
		this.msgno = m;
		this.type = t;
		this.threadName = n;
	}
	
	//定时对所监视的目录进行读取
	public void run(){
		
		try {
		
			while (true) {
				
				try{
					long tims = System.currentTimeMillis();
					if(type == 1) {
						//http
						httpHandleThread hht = new httpHandleThread();
						hht.run();
						
						tims = System.currentTimeMillis() - tims;
						if(tims < publicLoadConf.fileDuration){
							Thread.sleep(publicLoadConf.fileDuration - tims);
						}
						
						
					}else if(type == 2){
						//nat
						natHandleThread nht = new natHandleThread(this.srcPath, this.dstPath, this.msgno);
						nht.run();

						tims = System.currentTimeMillis() - tims;
						if(tims < publicLoadConf.fileDuration){
							Thread.sleep(publicLoadConf.fileDuration - tims);
						}

					}else if(type == 3){
						//radius ------ 重新处理
						long currentTimes = System.currentTimeMillis();
						if(currentTimes > (publicLoadConf.lastRadiusTime + publicLoadConf.radiusConf.getType()*60000)) {
							//计算当前的15分钟时间
							currentTimes = currentTimes - (currentTimes  % (publicLoadConf.radiusConf.getType()*60000));
							radiusHandleThread rhd = new radiusHandleThread(this.dstPath, this.msgno,currentTimes);
							rhd.run();
						}

						tims = System.currentTimeMillis() - tims;
						if(tims < publicLoadConf.fileDuration){
							Thread.sleep(publicLoadConf.fileDuration - tims);
						}
					}else if(type == 4){
						//other
						OtherHandleThread oht = new OtherHandleThread(this.srcPath, this.dstPath,this.msgno);
						oht.run();
						
						tims = System.currentTimeMillis() - tims;
						if(tims < publicLoadConf.fileDuration){
							Thread.sleep(publicLoadConf.fileDuration - tims);
						}
					}

				}catch(Exception e){
					e.printStackTrace();
				}finally{
				}
				
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}

}
