package com.wlan.diversion;

import javax.servlet.http.*;

import com.wlan.comm.operationUtil;
import com.wlan.comm.publicLoadConf;
import com.wlan.comm.timeUtil;
import com.wlan.diversionWlan.Object.directObject;
import com.wlan.diversionWlan.Thread.wlanThreadClass;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: </p>
 * @author not attributable
 * @version 1.0
 */
public class TimerLoad extends HttpServlet {
  public TimerLoad() {
	  
	  directObject do1;
	  //首次启动时启动线程操作
	  
	  /**
	   * NAT 日志线程启动
	   */
	  if(publicLoadConf.natConfList!=null && publicLoadConf.natConfList.size()>0) {
		  try {
			  Thread[] t = new Thread[publicLoadConf.natConfList.size()];
			  for(int i=0;i<publicLoadConf.natConfList.size();i++){
				  do1 = new directObject();
				  do1 = publicLoadConf.natConfList.get(i);
				  
				  System.out.println("[Nat.Thread"+i+"].start ");
		    	  
				  t[i] = new wlanThreadClass(do1.getSrcDirect(),do1.getDstDirect(),do1.getMsgno(),2, "Nat_Thread_0"+i);
		    	  t[i].start();
		    	  
		    	  System.out.println("[Nat.Thread"+i+"].start over ");
			  }
		    	
		  }catch(Exception e){
			  e.printStackTrace();
		  }
	  }
	  
	  /**
	   * Http 日志线程启动
	   */
	  if(publicLoadConf.httpConfList!=null && publicLoadConf.httpConfList.size()>0) {
		  try {
			  Thread[] t1 = new Thread[publicLoadConf.httpConfList.size()];
			  for(int i=0;i<publicLoadConf.httpConfList.size();i++){
				  do1 = new directObject();
				  do1 = publicLoadConf.httpConfList.get(i);
				  
				  System.out.println("[Http.Thread"+i+"].start ");
		    	  
				  t1[i] = new wlanThreadClass(do1.getSrcDirect(),do1.getDstDirect(),do1.getMsgno(),1, "Http_Thread_0"+i);
		    	  t1[i].start();
		    	  
		    	  System.out.println("[Http.Thread"+i+"].start over ");
			  }
		  }catch(Exception e){
			  e.printStackTrace();
		  }
	  }

	  /**
	   * RADIUS 日志线程启动
	   */
	  if(publicLoadConf.radiusConf!=null) {
		  try {
			  	 System.out.println("[Radius.Thread].start ");
		    	  
			  	 Thread tRadius = new Thread();
			  	 tRadius = new wlanThreadClass("", publicLoadConf.RADIUSdstCataLog, publicLoadConf.RADIUSProCode, 3, "Radius_Thread");
			  	 tRadius.start();
			  	 
			  	 System.out.println("[Radius.Thread].start.over");
		    	
		  }catch(Exception e){
			  e.printStackTrace();
		  }
	  }
	  
	  //首次启动后500ms运行查询
	  RunTimer alarm = new RunTimer(500);
	  alarm.start();
	  
  }
}
