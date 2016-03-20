package com.wlan.comm;

import java.util.ArrayList;

import com.wlan.diversionWlan.Object.confObject;
import com.wlan.diversionWlan.Object.directObject;

/**
 *
 * <p>Title: </p>
 * <p>Description: Q3 DUI 4.0</p>
 * <p>Copyright: Copyright (c) 2008</p>
 * <p>Company: </p>
 * @author t.yang
 * @version 2.0
 */
public class publicLoadConf {
  
  //间隔扫描时间,单位为ms
  public static int timerJg = 10; 
  public static final boolean isdebug = true;

  //全局空目录的文件名
  public static String emptyFolder = "/usr/local/wlan/pro/emptyFolder/";
  
  //配置http和nat的目录结构
  public static ArrayList<directObject> natConfList = new ArrayList<directObject>();
  public static directObject httpConf;
  public static directObject otherConf;
  //读取http和nat的日志
//  public static confObject httpConf = new confObject();
//  public static confObject otherConf;
  public static confObject natConf;
  public static confObject radiusConf;
  /**
   * Nat msgNo Nat 文件也有的MsgNo,递增用
   */
  public static long natMsgNo = 0;
  
  /**
   * Radius msgNo Radius 文件也有的MsgNo,递增用
   */
  public static long radiusMsgNo = 0;

  
  //间隔读取的时间,单位ms
  public static long fileDuration = 0;
  
  /**
   * 与wlan数据库连接相关的部分，默认都是本地数据库
   * NAT 查询特有
   */
  public static String sysUSER = "";
  public static String sysDBDriver = "";
  public static String sysPWD = "";
  public static String sysURL = "";
  
  /**
   * 上次radius读取到的时间
   */
  public static long lastRadiusTime;
  
  /**
   * Radius目标路径
   */
  public static String RADIUSdstCataLog = "";
  
  /**
   * radius 省份代码
   */
  public static String RADIUSProCode = "";
  
  static {

   init();
  }

  private static void init() {
        String path = System.getProperty("user.dir").replace("\\", "/") + "/deploy/system.properties";

        System.out.println("[load system config, path]=" + path);

	    PropertyUtill c = null;
	    try {
	        c = new PropertyUtill(path);
	        
	        sysURL = c.getValue("sysURL");
	        sysDBDriver = c.getValue("sysDBDriver");
	        sysUSER = c.getValue("sysUSER");
	        sysPWD = c.getValue("sysPWD");
	        
	        System.out.println("[sysURL]"+sysURL);
	        System.out.println("[sysDBDriver]"+sysDBDriver);
	        System.out.println("[sysUSER]"+sysUSER);
	        System.out.println("[sysPWD]"+sysPWD);
	        
	        String tmpstr1,tmpstr2,tmpstr3,tmpstr4;
	        String[] tmpArray1,tmpArray2,tmpArray3;
	        directObject dmodel;

	        //NAT 目录部分
	        tmpstr1 = c.getValue("NATsrcCataLog");
	        tmpstr2 = c.getValue("NATdstCataLog");
	        tmpstr3 = c.getValue("NATMsgNo");
	        tmpArray1 = tmpstr1.split("\\|");
	        tmpArray2 = tmpstr2.split("\\|");
	        tmpArray3 = tmpstr3.split("\\|");
	        for(int i=0;i<tmpArray1.length;i++){
	        	dmodel = new directObject();
	        	dmodel.setSrcDirect(tmpArray1[i]);
	        	dmodel.setDstDirect(tmpArray2[i]);
	        	dmodel.setMsgno(tmpArray3[i]);
	        	System.out.print((i+1)+".[NAT.src]"+dmodel.getSrcDirect());
	        	System.out.println(" --> [NAT.dst]"+dmodel.getDstDirect()+" [Msg]"+dmodel.getMsgno());
	        	natConfList.add(dmodel);
	        }
	        System.out.println("{NAT.ConfList.Size}"+natConfList.size());
	        
	        //HTTP 目录部分
	        tmpstr1 = c.getValue("HTTPsrcCataLog");
	        tmpstr2 = c.getValue("HTTPdstCataLog");
	        tmpstr3 = c.getValue("HTTPMsgNo");
	        tmpstr4 = c.getValue("HttpConfigure");
	        if(tmpstr1 != ""){
	        	httpConf = new directObject();
	        	httpConf.setSrcDirect(tmpstr1);
	        	httpConf.setDstDirect(tmpstr2);
	        	httpConf.setMsgno(tmpstr3);
	        	httpConf.setConfig(tmpstr4);
	        }
	        
	      //Other 目录部分
	        tmpstr1 = c.getValue("OthersrcCataLog");
	        tmpstr2 = c.getValue("OtherdstCataLog");
	        tmpstr3 = c.getValue("OtherMsgNo");
	        tmpstr4 = c.getValue("OtherConfigure");
	        if(tmpstr1 != ""){
	        	otherConf = new directObject();
	        	otherConf.setSrcDirect(tmpstr1);
	        	otherConf.setDstDirect(tmpstr2);
	        	otherConf.setMsgno(tmpstr3);
	        	otherConf.setConfig(tmpstr4);
	        }
	        
	        //nat Configure
//	        tmpstr1 = c.getValue("NATConfigure");
//	        tmpArray1 = tmpstr1.split("\\|");
//	        natConf.setType(Integer.parseInt(tmpArray1[0]));
//	        natConf.setSize(Long.parseLong(tmpArray1[1]));
//	        System.out.println("{NAT.Configure}"+natConf.getType()+" "+natConf.getSize());
	        
	        //http Configure
//	        tmpstr2 = c.getValue("HttpConfigure");
//	        tmpArray2 = tmpstr2.split("\\|");
//	        httpConf.setType(Integer.parseInt(tmpArray2[0]));
//	        httpConf.setSize(Long.parseLong(tmpArray2[1]));
//	        System.out.println("{HTTP.Configure}"+httpConf.getType()+" "+httpConf.getSize());
	        
	        //radius Configure
//	        tmpstr2 = c.getValue("RADIUSConfigure");
//	        if(tmpstr2 != null){
//	        	radiusConf = new confObject();
//	        	tmpArray2 = tmpstr2.split("\\|");
//	        	radiusConf.setType(Integer.parseInt(tmpArray2[0]));
//	        	radiusConf.setSize(Long.parseLong(tmpArray2[1]));
//	        	System.out.println("{RADIUS.Configure}"+radiusConf.getType()+" "+radiusConf.getSize());
//	        	
//	        }
	        
	        fileDuration = Long.parseLong(c.getValue("fileDuration"));
	        
	        System.out.println("[fileDuration]"+fileDuration);
	        
//	        natMsgNo = natMsgNoConfBean.readNo();
//	        System.out.println("[natMsgNo]" + natMsgNo);
//
//	        radiusMsgNo = radiusMsgNoConfBean.readNo();
//	        System.out.println("[radiusMsgNo]" + radiusMsgNo);
//	        
//	        lastRadiusTime = lastRadiusTimeStampBean.readTimes();
//	        System.out.println("[lastRadiusTime]" + lastRadiusTime+"-->"+timeUtil.returnDate1970to14(lastRadiusTime));
//
//	        RADIUSdstCataLog = c.getValue("RADIUSdstCataLog");
//	        System.out.println("[RADIUSdstCataLog]"+RADIUSdstCataLog);
//
//	        RADIUSProCode = c.getValue("RADIUSProCode");
//	        System.out.println("[RADIUSProCode]"+RADIUSProCode);
	        
	        System.out.println("[load system config end ]");
	        
	        
	    } catch (Exception exx){
	    	System.err.println("Configure Error:"+exx.toString());
	        exx.printStackTrace();
	    }

  }
  public publicLoadConf() {
  }

  public static int getTimerJg() {
    return timerJg;
  }
}
