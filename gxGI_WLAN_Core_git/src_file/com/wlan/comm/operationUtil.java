package com.wlan.comm;

import java.io.*;
import java.lang.reflect.*;
import java.text.*;
import java.util.*;
import javax.naming.*;

import sun.misc.*;

/**
 * <p>
 * Title:
 * </p>
 * <p>
 * Description:
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

public class operationUtil {
	public operationUtil() {
	}

	public static Object getInitialParm(String sParmName) {
		Object obj = null;
		try {
			Context _ic = (Context) new InitialContext().lookup("java:comp/env");
			obj = _ic.lookup(sParmName);
		} catch (Exception e) {
			System.out.println("getInitialParm:" + e);
		}
		return obj;
	}

	public static String GBtoISO(String value) throws UnsupportedEncodingException {
		return new String(value.getBytes("ISO8859-1"));
	}

	/*
	 * public static String ISOtoGB(String value) throws
	 * UnsupportedEncodingException { return new String(value.getBytes(),
	 * "ISO8859-1"); }
	 */
	// 将空字符串进行转换成为“”
	public static String strTran(String oldString) {
		String newString = "";
		if (oldString == null) {
			newString = "";
		} else if (oldString.trim().equals("null")) {
			newString = "";
		} else {
			newString = oldString.trim();
		}
		return newString;
	}

	public static String tranChar(String oldString) {
		return oldString.replace((char) 12288, (char) 32);
	}

	public static float floatTran(String oldString) {
		float newString = 0;
		if (oldString == null) {
			newString = 0;
		} else if (oldString.trim().equals("null")) {
			newString = 0;
		} else {
			newString = Float.parseFloat(oldString);
		}
		return newString;
	}

	// 加入超连接
	public static String viewLink(String Content) {
		String ret = Content;
		if (Content.indexOf("http://") >= 0) {
			String fTemp = Content.substring(0, Content.indexOf("http://"));
			String lTemp = Content.substring(Content.indexOf("http://"), Content.length());
			int sp = lTemp.indexOf(32);
			int br = lTemp.indexOf("\n");
			if (sp > 0 || br > 0) {
				if (sp == -1) {
					ret = lTemp.substring(0, br);
				} else if (br == -1) {
					ret = lTemp.substring(0, sp);
				} else {
					ret = lTemp.substring(0, sp > br ? br : sp);
				}
				ret = fTemp + "<a href='" + ret + "' target='_blank'>" + ret + "</a>" + lTemp.substring(ret.length(), lTemp.length());
			} else {
				ret = fTemp + "<a href='" + lTemp + "' target='_blank'>" + lTemp + "</a>";
			}
		}

		// System.out.println("ret:"+ret);
		return ret;
	}

	// 将/n转换成为回车<br>
	public static String viewBr(String Content) {
		String makeContent = new String();
		operationUtil myself = new operationUtil();
		Content = myself.strTran(Content);
		StringTokenizer strToken = new StringTokenizer(Content, "\n");
		while (strToken.hasMoreTokens()) {
			makeContent = makeContent + "<br>" + strToken.nextToken();
		}
		if (makeContent.length() > 4) {
			if (makeContent.substring(0, 4).equals("<br>")) {
				makeContent = makeContent.substring(4, makeContent.length());
			}
		}
		return makeContent;
	}

	// 返回该年月的天数
	public static int RetDays(int years, int months) {
		int RetrunDays = 0;
		switch (months) {
		case 2:
			if (years % 4 == 0) {
				RetrunDays = 29;
			} else {
				RetrunDays = 28;
			}
			break;
		case 4:
		case 6:
		case 9:
		case 11:
			RetrunDays = 30;
			break;
		default:
			RetrunDays = 31;
			break;
		}

		return RetrunDays;
	}

	public static String DateFunction() {
		java.util.Date date = new java.util.Date();
		return DateFunction(date);
	}

	public static String DateFunction8bit() {
		java.util.Date date = new java.util.Date();
		
		return DateFunction(date).replaceAll("-", "");
	}

	// 返回当前日期
	public static String DateFunction(java.util.Date date) {

		java.lang.StringBuffer sb = new StringBuffer();
		sb.append(String.valueOf(date.getYear() + 1900) + "-");
		if (String.valueOf(date.getMonth() + 1).length() == 1) {
			sb.append("0");
		}
		sb.append(String.valueOf(date.getMonth() + 1) + "-");
		if (String.valueOf(date.getDate()).length() == 1) {
			sb.append("0");
		}
		sb.append(String.valueOf(date.getDate()));

		return sb.toString();
	}

	// 返回当前日期到分钟
	public static String DateTimeFunction() {
		java.util.Date date = new java.util.Date();
		return DateTimeFunction(date);
	}

	public static String DateTimeFunction(java.util.Date date) {
		java.lang.StringBuffer sb = new StringBuffer();
		sb.append(DateFunction(date) + " ");
		if (String.valueOf(date.getHours()).length() == 1) {
			sb.append("0");
		}
		sb.append(String.valueOf(date.getHours()) + ":");

		if (String.valueOf(date.getMinutes()).length() == 1) {
			sb.append("0");
		}
		sb.append(String.valueOf(date.getMinutes()) + ":");

		if (String.valueOf(date.getSeconds()).length() == 1) {
			sb.append("0");
		}
		sb.append(String.valueOf(date.getSeconds()));

		return sb.toString();
	}

	public static String buildcode(String oldstr) {
		BASE64Decoder decoder = new BASE64Decoder();
		byte[] b = null;
		try {
			b = decoder.decodeBuffer(oldstr + "=");
		} catch (IOException ex) {
		}
		String tmps = new String(b);
		return tmps;
	}

	/**
	 * 返回该表的字符串时间
	 * 
	 * @return String
	 */
	public static String DateTimeFunctionString() {
		java.util.Date date = new java.util.Date();
		String tmps = DateTimeFunction(date);
		tmps = tmps.replaceAll(":", "");
		tmps = tmps.replaceAll("-", "");
		tmps = tmps.replaceAll(" ", "");
		return tmps;
	}

	/**
	 * 将日期字符串转化为long字符串
	 * 
	 * @param oldstr
	 *            String
	 * @return String
	 */
	public static String dtbuildstring(String oldstr) {
		String tmps = oldstr;
		tmps = tmps.replaceAll(":", "");
		tmps = tmps.replaceAll("-", "");
		tmps = tmps.replaceAll(" ", "");
		if (tmps.length() > 14) {
			tmps = tmps.substring(0, 14);
		}
		return tmps;
	}

	// 返回日期型数据的字符串
	public static String DateString() {
		java.util.Date date = new java.util.Date();
		return DateString(date);
	}

	public static String DateString(java.util.Date date) {
		java.lang.StringBuffer sb = new StringBuffer();

		sb.append(String.valueOf(date.getYear() + 1900));
		if (String.valueOf(date.getMonth() + 1).length() == 1) {
			sb.append("0");
		}
		sb.append(String.valueOf(date.getMonth() + 1));
		if (String.valueOf(date.getDate()).length() == 1) {
			sb.append("0");
		}
		sb.append(String.valueOf(date.getDate()));

		if (String.valueOf(date.getHours()).length() == 1) {
			sb.append("0");
		}
		sb.append(String.valueOf(date.getHours()));
		if (String.valueOf(date.getMinutes()).length() == 1) {
			sb.append("0");
		}
		sb.append(String.valueOf(date.getMinutes()));
		if (String.valueOf(date.getSeconds()).length() == 1) {
			sb.append("0");
		}
		sb.append(String.valueOf(date.getSeconds()));

		return sb.toString();
	}

	// 返回当年剩下的日期
	public static int getLastDay(java.util.Date date) {
		Calendar c1 = Calendar.getInstance();
		int year = Integer.parseInt(date.toLocaleString().substring(0, 4));
		c1.set(year, 0, 1); // 设置日期为2000年1月1号，月以0开始的
		Calendar c2 = Calendar.getInstance();
		long t1 = c2.getTimeInMillis() - c1.getTimeInMillis();
		int days = (int) (t1 / (1000 * 60 * 60 * 24));
		GregorianCalendar gc = new GregorianCalendar();
		if (gc.isLeapYear(year)) {
			days = 366 - days;
		} else {
			days = 365 - days;
		}
		// System.out.println("days:" + days);
		return days;
	}

	// 返回今日应该完成百分比
	public static float getJrwc(String DJ) {
		String[] tempDJ = DJ.split("-");
		if (tempDJ[1].substring(0, 1) == "0") {
			tempDJ[1] = tempDJ[1].substring(1, 2);
		}
		if (tempDJ[2].substring(0, 1) == "0") {
			tempDJ[2] = tempDJ[2].substring(1, 2);
		}
		GregorianCalendar gc = new GregorianCalendar(Integer.parseInt(tempDJ[0]), Integer.parseInt(tempDJ[1]) - 1, Integer.parseInt(tempDJ[2]));
		float dq = Integer.parseInt(tempDJ[2]);
		float max = gc.getActualMaximum(gc.DAY_OF_MONTH); // 取当月最后一天
		return dq / max * 100;
	}

	/**
	 * 将一个字符的日期转换为java日期 日期字符串格式YYYY-MM-DD HH:MM:SS
	 * 
	 * @param oldRq
	 *            String
	 * @return Date
	 */
	public static java.util.Date getJavaDate(String oldRq) {
		int year = 0, month = 0, day = 0, hour = 0, min = 0, sec = 0;
		java.util.Date RetDate = new Date();
		try {
			String[] spaceFg = oldRq.split(" ");
			String[] dateFg = spaceFg[0].split("-");
			year = Integer.parseInt(dateFg[0]) - 1900;
			month = Integer.parseInt(dateFg[1]) - 1;
			day = Integer.parseInt(dateFg[2]);
			if (spaceFg.length > 1) {
				String[] timeFg = spaceFg[1].split(":");
				hour = Integer.parseInt(timeFg[0]);
				min = Integer.parseInt(timeFg[1]);
				if (spaceFg.length > 2) {
					sec = Integer.parseInt(timeFg[2]);
				}
			}

			RetDate = new Date(year, month, day, hour, min, sec);
		} catch (Exception e) {
			System.out.println("e-time exception:" + e);
		}
		return RetDate;
	}

	/**
	 * 将一个字符的日期转换为java日期 日期字符串格式YYYYMMDD
	 * 
	 * @param oldRq
	 *            String
	 * @return Date
	 */
	public static java.util.Date getJavaDate2(String oldRq) {
		int year = 0, month = 0, day = 0, hour = 0, min = 0, sec = 0;
		java.util.Date RetDate = new Date();
		try {
			RetDate = new Date(Integer.parseInt(oldRq.substring(0, 4)) - 1900, Integer.parseInt(oldRq.substring(5, 6)) - 1, Integer.parseInt(oldRq.substring(7, 8)), hour, min, sec);
		} catch (Exception e) {
			System.out.println("e-time exception:" + e);
		}
		return RetDate;
	}

	/**
	 * 为动态数组扩充大小 参数array - 任意类型的数组 newSize - 要达到的数组大小
	 * */
	public static Object ArrayExpander(Object array, int newSize) {
		if (array == null) {
			return null;
		}

		Class c = array.getClass();
		if (c.isArray()) {
			int len = Array.getLength(array);
			if (len >= newSize) {
				return array;
			} else {
				Class cc = c.getComponentType();
				Object newArray = Array.newInstance(cc, newSize);
				System.arraycopy(array, 0, newArray, 0, len);
				return newArray;
			}
		} else {
			throw new ClassCastException("need  array");
		}
	}

	/**
	 * split 函数的本地实例化 原因：jdk 1.4.02前没有该功能，Websphere5.1前的版本仍然仅支持jdk1.3.1以前的版本 参数
	 * theString - 要拆分的String pot - 分隔标志
	 */
	public static Object ArrayExpander1(Object array, int newSize) {
		if (array == null) {
			return null;
		}

		Class c = array.getClass();
		if (c.isArray()) {
			int len = Array.getLength(array);
			if (len >= newSize) {
				return array;
			} else {
				Class cc = c.getComponentType();
				Object newArray = Array.newInstance(cc, newSize);
				System.arraycopy(array, 0, newArray, 0, len);
				return newArray;
			}
		} else {
			throw new ClassCastException("need  array");
		}
	}

	public static String[] ArraySplit(String theString, String pot) {
		String[] s = {};
		int potNum = 0;
		String tmps = "";
		try {
			StringTokenizer strtok = new StringTokenizer(theString, pot, false);
			while (strtok.hasMoreTokens()) {
				potNum++;
				tmps = strtok.nextToken().trim();
				s = (String[]) ArrayExpander1(s, potNum);
				s[potNum - 1] = tmps;
			}

		} catch (Exception e) {
			// System.out.println("the ArraySplit Exception (CtesysUtil):"+e);
		} finally {
			return s;
		}

	}

	/**
	 * 从参数中解析出符合要求的值 参数需要解析的表达式 返回值String[] 0 ---- 键名 1 ---- 键值
	 */
	public static String[] AssayString(String incstring) {
		String[] retValue = { "", "" };
		int itmp;

		itmp = incstring.indexOf("=");
		if (itmp != -1) {
			retValue[0] = incstring.substring(0, itmp);
			retValue[1] = incstring.substring(itmp + 1);
		} else {
			retValue = null;
		}
		return retValue;
	}

	/**
	 * 返回指定日oldDate后Days天的日期,前?天使用负值
	 * 
	 * @param oldDate
	 *            String
	 * @param Days
	 *            int
	 * @return String
	 */
	public static String BeforeDates(String oldDate, int Days) {
		java.text.SimpleDateFormat formatter = new java.text.SimpleDateFormat("yyyy-MM-dd");
		// 定义日期字符串格式
		String itday[] = ArraySplit(oldDate, "-");
		// 拆分原有日期
		java.util.Date date = new java.util.Date(Integer.parseInt(itday[0]) - 1900, Integer.parseInt(itday[1]) - 1, Integer.parseInt(itday[2]));
		// 字符型日期转化为日期型日期
		date.setDate(date.getDate() + Days);
		// 求出n天前后的日期，其i为n
		String TempDate = formatter.format(date);
		// 重新将date日期转化为字符串按照上边定义的格式
		return TempDate;
	}

	/**
	 * 返回下一个符合条件的日期
	 * 
	 * @param oldDate
	 *            String 需要判断的日期
	 * @param type
	 *            int 0-即时日期 1-按照年计算 2-按照月计算
	 * @return String
	 */
	public static String RetNextAccordDate(String oldDate, int type) {
		String[] oldDateArrs = operationUtil.ArraySplit(oldDate, "-");
		java.util.Date date = new java.util.Date();

		switch (type) {
		case 0:
			oldDate = oldDate;
			break;
		case 1:

			// 每年提醒
			oldDate = String.valueOf(date.getYear() + 1900) + "-" + oldDateArrs[1] + "-" + oldDateArrs[2];
			if (date.before(operationUtil.getJavaDate(oldDate))) {
				oldDate = oldDate;
			} else {
				oldDate = String.valueOf(date.getYear() + 1901) + "-" + oldDateArrs[1] + "-" + oldDateArrs[2];
			}

			break;
		case 2:

			// 每月提醒
			oldDate = String.valueOf(date.getYear() + 1900) + "-" + String.valueOf(date.getMonth() + 1) + "-" + oldDateArrs[2];
			if (date.before(operationUtil.getJavaDate(oldDate))) {
				oldDate = oldDate;
			} else {
				if (date.getMonth() + 1 == 12) {
					oldDate = String.valueOf(date.getYear() + 1901) + "-01-" + oldDateArrs[2];
				} else {
					oldDate = String.valueOf(date.getYear() + 1900) + "-" + String.valueOf(date.getMonth() + 2) + "-" + oldDateArrs[2];
				}
			}
			break;
		}

		return oldDate;
	}

	/**
	 * 
	 * @param a
	 *            String
	 * @param b
	 *            String
	 * @return boolean
	 */
	public boolean isEquals(String a, String b) {

		if ((a == null || a.equals("")) && (b == null || b.equals(""))) {

			return false;
		} else if ((a == null || a.equals("")) && (b != null || !b.equals(""))) {

			return true;
		} else if ((a != null || !a.equals("")) && (b == null || b.equals(""))) {

			return true;
		} else if ((a != null || !a.equals("")) && (b != null || !b.equals("")) && (a.equals(b))) {

			return false;
		} else {

			return true;
		}
	}

	/**
	 * IP地址转换new
	 * 
	 * @param oldIp
	 *            String
	 * @return String
	 */
	public static String ipDiversion(String oldIp) {
		if (oldIp == null || oldIp.equals("0")) {
			return "0.0.0.0";
		}
		try {
			StringBuffer newIp = new StringBuffer();
			long dd1;
			String iptmp;
			dd1 = Long.parseLong(oldIp.trim()); // 将字符串IP转换为long形势IP
			// session 1
			iptmp = Long.toHexString(dd1).toUpperCase(); // 直接转换为16进制并且为大写
			// session 2
			int ll = iptmp.length();
			/**
			 * session 3 十六进制数位数如果小于八位，前边将0补齐8位 大于等于八位取后八位
			 */
			if (ll < 8) {
				for (int i = 1; i <= 8 - ll; i++) {
					iptmp = "0" + iptmp;
				}
			} else {
				iptmp = iptmp.substring(iptmp.length() - 8, iptmp.length());
			}
			/**
			 * session 4 将8位十六进制数据两两转换成为十进制数据,之间都用"."连接
			 */

			for (int i = 0; i < 4; i++) {
				if (i > 0) {
					newIp.append(".");
				}
				newIp.append(String.valueOf(Integer.parseInt(iptmp.substring(i * 2, i * 2 + 2), 16)));
			}
			return newIp.toString();
		} catch (Exception e) {
			return "0.0.0.0";
		}
	}

	/**
	 * 返回IP地址的前后两位
	 * 
	 * @param oldIp
	 *            String
	 * @param bits
	 *            int
	 * @return String[]
	 */
	public static String[] iptools(String oldIp, int bits) {
		// 前两位 IP的下限和上限(CHAR) 后两位(NUM)
		String[] ipbegin = { "", "", "", "" };

		String[] iptmps = operationUtil.ArraySplit(oldIp, ".");
		String tmps = "", tmpAll = "";
		int itmps = 0;
		for (int i = 0; i < iptmps.length; i++) {
			tmps = "";
			tmps = Integer.toBinaryString(Integer.parseInt(iptmps[i]));
			itmps = tmps.length();
			if (itmps < 8) {
				for (int j = 1; j <= 8 - itmps; j++) {
					tmps = "0" + tmps;
				}
			}
			tmpAll = tmpAll + tmps;
		}
		String ipMask1 = "", ipMask2 = "";
		for (int i = 0; i < 32; i++) {
			if (i > bits - 1) {
				ipMask1 = ipMask1 + "0";
				ipMask2 = ipMask2 + "1";
			} else {
				ipMask1 = ipMask1 + tmpAll.substring(i, i + 1);
				ipMask2 = ipMask1;
			}
		}

		for (int i = 0; i < 4; i++) {
			if (i > 0) {
				ipbegin[0] = ipbegin[0] + ".";
				ipbegin[1] = ipbegin[1] + ".";
			}
			ipbegin[0] = ipbegin[0] + String.valueOf(Integer.parseInt(ipMask1.substring(i * 8, i * 8 + 8), 2));
			ipbegin[1] = ipbegin[1] + String.valueOf(Integer.parseInt(ipMask2.substring(i * 8, i * 8 + 8), 2));
		}

		int iplength = 0;
		for (int i = 0; i < 2; i++) {
			ipbegin[i + 2] = ipAllLong(ipbegin[i]);
		}
		return ipbegin;
	}

	/**
	 * ip抵制补位并且转换 ep: 200.10.50.0 ->200010050000
	 * 
	 * @param oldIp
	 *            String
	 * @return String
	 */
	public static String ipAllLong(String oldIp) {
		String newIp = "";
		int iplength = 0;
		newIp = "";
		String[] ipnums = operationUtil.ArraySplit(oldIp, ".");
		for (int j = 0; j < 4; j++) {
			iplength = 0;
			iplength = ipnums[j].length();
			if (iplength < 3)
				for (int x = 0; x < 3 - iplength; x++) {
					ipnums[j] = "0" + ipnums[j];
				}

			newIp = newIp + ipnums[j];
		}
		return newIp;
	}

	/**
	 * 返回到ms的本地日期
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
	 * 将普通日期格式装换为 MM/dd/yyyy的日期
	 * 
	 * @param _13Javadate
	 *            long
	 * @return String
	 */
	public static String time14bittoForigenDate(long _14Javadate) {
		String _returnForDate = "";
		_returnForDate = String.valueOf(_14Javadate);

		int year = Integer.parseInt(_returnForDate.substring(0, 4));
		int mon = Integer.parseInt(_returnForDate.substring(4, 6));
		int day = Integer.parseInt(_returnForDate.substring(6, 8));
		Calendar calendar = Calendar.getInstance();
		calendar.set(year, mon - 1, day);
		java.text.SimpleDateFormat formatter = new java.text.SimpleDateFormat("MM/dd/yyyy");
		_returnForDate = formatter.format(calendar.getTime());
		return _returnForDate;
	}
	
	/**
	 * rsync 删除文件目录
	 */
	public static void rsyncDirect(String fileDirect){		
		try{
			String ts = "rsync --delete -r "+publicLoadConf.emptyFolder+" "+fileDirect;
			String[] cmd = {"/bin/sh", "-c", ts};
			String ls_2 = "";
			Process process2 = Runtime.getRuntime().exec(cmd);
			BufferedReader bufferedReader2 = new BufferedReader(new InputStreamReader(process2.getInputStream()));
			while ( (ls_2 = bufferedReader2.readLine()) != null) {
				System.out.println(ls_2);
			}
			process2.waitFor();
			bufferedReader2.close();
			process2.destroy();
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	

	
	
	public static void main(String[] args) throws ParseException, InterruptedException {
		// System.out.println(operationUtil.ipDiversion("181062199"));
//		System.out.println(operationUtil.ipDiversion("3232237058"));
		System.out.println(operationUtil.DateTimeFunctionString());
	}

}
