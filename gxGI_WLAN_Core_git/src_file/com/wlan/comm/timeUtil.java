package com.wlan.comm;

import java.text.*;
import java.util.*;

import com.wlan.comm.*;

/**
 * <p>
 * Title:
 * </p>
 * <p>
 * Copyright: Copyright (c) 2004
 * </p>
 * <p>
 * Company:
 * </p>
 * 
 * @author not attributable
 * @version 1.0
 */
public class timeUtil {
	private static  Random r = new Random();

	public timeUtil() {
	}


	/**
	 * 将java 从1970java记数的毫秒转化为14位的时间字符串 YYYYMMDDHHMMSS
	 * 
	 * @param longstr
	 *            long
	 * @return String
	 */
	public static String returnDate1970to14(long longstr) {
		String oldstr = String.valueOf(longstr);
		long theMinutes = 0;

		try {
			if (oldstr.length() == 13) {
				theMinutes = longstr / 1000;
			} else if (oldstr.length() == 10) {
				theMinutes = longstr;
			}
			// 增加了格林尼治时间的修正
			theMinutes = theMinutes + (8 * 60 * 60);
			theMinutes = retNextSecondsDate("19700101000000", theMinutes);

		} catch (Exception e) {
			System.out.println("e:" + e);
			e.printStackTrace();
		}
		return String.valueOf(theMinutes);
	}


	
	/**
	 * 返回一个14位日期字符串的1970记数法的值 单位:秒(毫秒已经去除)
	 * 
	 * @param longstr
	 *            long
	 * @return String
	 */
	public static long returnDate14to1970(String rq) {
		long theMinutes = 0;
		try {
			SimpleDateFormat simpledate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			rq = String.valueOf(rq).substring(0, 4) + "-" + String.valueOf(rq).substring(4, 6) + "-" + String.valueOf(rq).substring(6, 8) + " " + String.valueOf(rq).substring(8, 10) + ":"
					+ rq.substring(10, 12) + ":" + rq.substring(12, 14);
			theMinutes = simpledate.parse(rq).getTime();
			theMinutes = theMinutes / 1000;
		} catch (Exception e) {
			System.out.println("e:" + e);
			e.printStackTrace();
		}
		return theMinutes;
	}

	/**
	 * 返回该日期后多少秒的日期 返回日期格式YYYYMMDDHHMMSS
	 * 
	 * @param rq
	 *            String
	 * @param changs
	 *            int
	 * @return long
	 */
	public static long retNextSecondsDate(String rq, long changs) {
		SimpleDateFormat myFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String tmps = "";
		try {
			java.util.Date date = myFormatter.parse(String.valueOf(rq).substring(0, 4) + "-" + String.valueOf(rq).substring(4, 6) + "-" + String.valueOf(rq).substring(6, 8) + " "
					+ String.valueOf(rq).substring(8, 10) + ":" + rq.substring(10, 12) + ":" + rq.substring(12, 14));

			long Time = (date.getTime() / 1000) + changs;
			date.setTime(Time * 1000);

			tmps = operationUtil.DateTimeFunction(date);
			tmps = StringEncoder.replaceAll(tmps, "-", "");
			tmps = StringEncoder.replaceAll(tmps, " ", "");
			tmps = StringEncoder.replaceAll(tmps, ":", "");
			tmps = tmps.substring(0, 14);
		} catch (ParseException ex) {
		} finally {
			return Long.parseLong(tmps);
		}
	}

	/**
	 * 将14位的日期字符串转换为标准格式的日期字符串
	 * 
	 * @param s
	 *            String
	 * @return String
	 */
	public static String dateFormat14toDate(String s) {
		String tmps = s;
		if (s.length() >= 14) {
			tmps = s.substring(0, 4) + "-" + s.substring(4, 6) + "-" + s.substring(6, 8) + " " + s.substring(8, 10) + ":" + s.substring(10, 12) + ":" + s.substring(12, 14);
		}
		return tmps;
	}

	/**
	 * 将标准格式的日期字符串转换为14位的日期字符串
	 * 
	 * @param s
	 *            String
	 * @return String
	 */
	public static String dateFormatDateto14(String s) {
		String tmps = s.trim();
		tmps = tmps.replaceAll(":", "");
		tmps = tmps.replaceAll("-", "");
		tmps = tmps.replaceAll(" ", "");
		if (tmps.length() > 14) {
			tmps = tmps.substring(0, 14);
		}
		return tmps;
	}

	/**
	 * 返回当前月的下一个月
	 * 
	 * @param itmon
	 *            String
	 * @return String
	 */
	public static String retnextMonth(String itmon) {
		int y = Integer.parseInt(itmon.substring(0, 4));
		int m = Integer.parseInt(itmon.substring(4, 6));

		if (m == 12) {
			return String.valueOf(y + 1) + "01";
		} else {
			m++;
			return String.valueOf(y) + (m < 10 ? "0" + String.valueOf(m) : String.valueOf(m));
		}
	}

	/**
	 * 将java 从1970java 14位的时间字符串 YYYYMMDDHHMMSS
	 * 
	 * @param longstr
	 *            long
	 * @return String
	 */
	public static String returnDatenow17() {
		long longstr = System.currentTimeMillis();
		String oldstr = String.valueOf(longstr);
		long theMinutes = 0;

		try {
			// if (oldstr.length() == 13) {
			// theMinutes = longstr / 1000;
			// }
			// else if (oldstr.length() == 10) {
			// theMinutes = longstr;
			// }
			theMinutes = longstr;

			// 增加了格林尼治时间的修正
			theMinutes = theMinutes + (8 * 60 * 60 * 1000);

			// theMinutes = retNextSecondsDate("19700101000000", theMinutes);

			SimpleDateFormat myFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");

			java.util.Date date = myFormatter.parse("1970-01-01 00:00:00.000");
			long Time = (date.getTime()) + theMinutes;
			date.setTime(Time);
		} catch (Exception e) {
			System.out.println("e:" + e);
			e.printStackTrace();
		}
		return String.valueOf(theMinutes);
	}

	/**
	 * 
	 * @return String
	 */
	public static String returnNowTime() {
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
		String mDateTime = formatter.format(cal.getTime());
		return mDateTime;
	}

	/**
	 * 放回当前是周几, 1 - 周一 2 - 周二 ... 7 - 周日
	 * 
	 * @return int
	 */
	public static int currentWeek() {
		Calendar calls = Calendar.getInstance();
		java.util.Date date = new java.util.Date();
		calls.set(date.getYear(), date.getMonth() + 1, date.getDate());

		int i = calls.get(Calendar.DAY_OF_WEEK);
		i--;
		if (i == 0)
			i = 7;
		return i;
	}

	/**
	 * 返回下一个15分钟的时刻
	 * 
	 * @param times
	 *            long ---- 13位Java日期
	 * @return long --------- 13为Java日期
	 */
	public static long nextQuarterTime(long times) {
		return times + (900 - (times / 1000 % 900)) * 1000;
	}

	  /**
	   * 返回该日期后多少秒的日期
	   * 返回日期格式YYYYMMDDHHMMSS
	   * @param rq String
	   * @param changs int
	   * @return long
	   */
	  public static long SysMchanges(String rq, long changs) {
//	    System.out.println("rq:"+rq);
	    SimpleDateFormat myFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	    String tmps = "";
	    try {
	      java.util.Date date = myFormatter.parse(String.valueOf(rq).substring(0,4) + "-" +
	                                              String.valueOf(rq).substring(4, 6) + "-" +
	                                              String.valueOf(rq).substring(6, 8) + " " +
	                                              String.valueOf(rq).substring(8, 10) + ":" +
	                                              rq.substring(10, 12) + ":" +
	                                              rq.substring(12, 14)
	      );

	      long Time = (date.getTime() / 1000) + changs;
	      date.setTime(Time * 1000);

	      tmps = operationUtil.DateTimeFunction(date);
	      tmps = StringEncoder.replaceAll(tmps, "-", "");
	      tmps = StringEncoder.replaceAll(tmps, " ", "");
	      tmps = StringEncoder.replaceAll(tmps, ":", "");
	      tmps = tmps.substring(0, 14);
	    }
	    catch (ParseException ex) {
	    }
	    finally {
	      return Long.parseLong(tmps);
	    }
	}
	  	
	  /**
	   * 补0
	   * @param oldL
	   * @param len
	   * @return
	   */
	    private static String[] addZeroArray(long oldL,long newL,int len) {

	    	String[] retTimes = {"",""};

	    	long l1=0,l2=0,l3;
			
			l1 = oldL;
			l2 = newL;
			
//			if(l1>l2) {
//				l3=l2;
//				l2=l1;
//				l1=l3;
//			}
			
	    	retTimes[0] = addZore(l1,len)+"000";
	    	retTimes[1] = addZore(l2,len)+"000";
			
			return retTimes;
	    }
	    
	    private static String addZore(long ltmp,long len) {
	    	String sTemp = String.valueOf(ltmp);
	    	long iwhile = len - sTemp.length();
			for(int ix=0;ix<iwhile;ix++) {
				sTemp = "0"+sTemp;
			}
			return sTemp;
	    }
	  
		/**
		 * 加强版
		 * 1387954250.863987 -- 入参
		 * 2010-08-30 23:46:19.333846000 --- 返回值
		 * @param longstr
		 * @return
		 */
		public static String returnDate1970to14Supper(String longstr) {
			
			String[] oldstr = longstr.split("\\.");
			long theMinutes = 0;
			String endstr="";
			long l1=0,l2=0,l3;
			
			String[] xiaoshu = {"",""};
			
			try {
				theMinutes = Long.parseLong(oldstr[0]);

				l1 = Long.parseLong(oldstr[1]);
				l2 = r.nextInt(999999);
				
				xiaoshu = addZeroArray(l1,l2,6);
				
				endstr = String.valueOf(retNextSecondsDate("19700101000000",(Long.parseLong(oldstr[0]) + (System.currentTimeMillis() % 2)+1) + (8 * 60 * 60))); 
				// 增加了格林尼治时间的修正
				longstr = String.valueOf(retNextSecondsDate("19700101000000",(Long.parseLong(oldstr[0])) + (8 * 60 * 60))); 
				
				if(longstr.equals(endstr)){
					longstr =longstr.substring(0, 4) + "-" + 
							longstr.substring(4, 6) + "-" + 
							longstr.substring(6, 8) + " " + 
							longstr.substring(8, 10) + ":"
							+ longstr.substring(10, 12) + ":" + longstr.substring(12, 14);

					endstr = longstr;

				}else{
					// 计算结束时间
					longstr =longstr.substring(0, 4) + "-" + 
							longstr.substring(4, 6) + "-" + 
							longstr.substring(6, 8) + " " + 
							longstr.substring(8, 10) + ":"
							+ longstr.substring(10, 12) + ":" + longstr.substring(12, 14);

					endstr =endstr.substring(0, 4) + "-" + 
							endstr.substring(4, 6) + "-" + 
							endstr.substring(6, 8) + " " + 
							endstr.substring(8, 10) + ":"
							+ endstr.substring(10, 12) + ":" + endstr.substring(12, 14);
				}
				
			} catch (Exception e) {
				System.out.println("e:" + e);
				e.printStackTrace();
			}
			
			return longstr +"."+ xiaoshu[0]+"','"+endstr+"."+xiaoshu[1];
		}

		/**
		 * 加强版
		 * 1387954250.863987 -- 入参
		 * 2010-08-30 23:46:19.333846000 --- 返回值
		 * @param longstr
		 * @return
		 */
		public static String returnDate1970to20(long longstr) {
			
			String oldstr = String.valueOf(longstr);
			long theMinutes = 0;
			String returnStr = "";
			try {
				if (oldstr.length() == 13) {
					theMinutes = longstr / 1000;
				} else if (oldstr.length() == 10) {
					theMinutes = longstr;
				}
				// 增加了格林尼治时间的修正
				theMinutes = theMinutes + (8 * 60 * 60);
				theMinutes = retNextSecondsDate("19700101000000", theMinutes);
				
				returnStr = String.valueOf(theMinutes);
				
				returnStr =returnStr.substring(0, 4) + "-" + 
						returnStr.substring(4, 6) + "-" + 
						returnStr.substring(6, 8) + " " + 
						returnStr.substring(8, 10) + ":"
						+ returnStr.substring(10, 12) + ":" + returnStr.substring(12, 14);
				
			} catch (Exception e) {
				System.out.println("e:" + e);
				e.printStackTrace();
			}
			return returnStr;		

		}		
		
	/**
	 * 生成一个50-2000的随机数
	 * @return
	 */
    public static String genRandomNum(){
    	Random r = new Random();

    	int ix= Math.abs(r.nextInt(2000));  //生成的数最大为36-1
		if(ix<50)ix=50;
		return String.valueOf(ix);

    }
    
    public static String format2JavaTime(String str){
    	String[] strs = str.split("\\.");
//    	if(strs.length!=2){
//    		throw new IllegalArgumentException("错误的时间格式"+str);
//    	}
//    	if(strs[1].length()!=6){
//    		throw new IllegalArgumentException("错误的时间格式"+str);
//    	}
    	Date date;
//		try {
			date = new Date(Long.parseLong(strs[0])*1000);
//		} catch (NumberFormatException e) {
//			throw new IllegalArgumentException("错误的时间格式"+str);
//		}
    	SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    	String front = format.format(date);
    	return front + strs[1] + "000";
    }
    
    public static String format2OtherTime(String str){
		Date date = new Date(Long.parseLong(str)*1000);
    	SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    	String front = format.format(date);
    	return front + "000000000";
    }
    
	  
	public static void main(String[] args) throws InterruptedException, ParseException {
//		System.out.println(timeUtil.retNextSecondsDate("20121213091739",28800));
//		System.out.println(timeUtil.returnDate1970to14Supper("1387954250.863987"));
		System.out.println(timeUtil.returnDate14to1970("20141114150013"));
//		System.out.println(timeUtil.returnDate14to1970("20141114111500"));
//		select count(*) from pmwlan.radius_20141114 where time_stamp >=1415934000000 and time_stamp < 1415934900000 and acct_status_type<> 3;
		
//		long currentTimes = System.currentTimeMillis();
//		currentTimes = currentTimes - (currentTimes  % (15*60000));
//		System.out.println(timeUtil.returnDate1970to14(currentTimes));
		String ls = "132993";
		int iwhile = 6 - ls.length();
		for(int ix=0;ix<iwhile;ix++) {
			ls = ls+"0";
		}
		System.out.println(ls);
	}

}
