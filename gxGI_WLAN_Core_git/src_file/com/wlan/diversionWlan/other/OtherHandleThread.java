package com.wlan.diversionWlan.other;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.wlan.comm.gzipUtil;
import com.wlan.comm.publicLoadConf;
import com.wlan.comm.timeUtil;

public class OtherHandleThread implements Runnable {

	// 最终输出文件的分隔符
	private final String splitString = "|";

	private String srcpath = "";
	private String dstpath = "";
	private String msgno = "";
	private long size;

	
	//输出流容器
	private Map<String, MyFileOutputStream> outStreamMap = new HashMap<String, MyFileOutputStream>();
	private Map<String, Integer> numMap;
	
	private String fileName = "";
//	private long currentStore = 0;

	// 拆分部分临时变量群
	private StringBuffer tmpLine;
	private String[] tmpArray;

	private List<File> contents;

	public OtherHandleThread() {
		
		//构造函数初始化配置信息
		srcpath = publicLoadConf.otherConf.getSrcDirect();
		dstpath = publicLoadConf.otherConf.getDstDirect();
		msgno = publicLoadConf.otherConf.getMsgno();
		size = Long.parseLong(publicLoadConf.otherConf.getConfig().split("\\|")[1]);
	}

	private void initNumMap() {
		numMap = new HashMap<String, Integer>();
		numMap.put("HTTPS", 0);
		numMap.put("FTP", 0);
		numMap.put("SMTP", 0);
		numMap.put("IMAP", 0);
		numMap.put("POP3", 0);
		numMap.put("DNS", 0);
		numMap.put("SNMP", 0);
		numMap.put("OTHER", 0);
	}

	@Override
	public void run() {
		
		long time1 = System.currentTimeMillis();
		System.out.println("任务开始时间" + time1);
		System.out.println("--Other,Handle");
		
		//源目录下所有文件
		contents = fileWalker(srcpath);
		//按照最后修改时间排序
		Collections.sort(contents, new Comparator<File>() {

			@Override
			public int compare(File file1, File file2) {
				return file1.lastModified() - file2.lastModified()>0 ? 1 : -1;
//				return splitTimeStr(file1.getName()).compareTo(splitTimeStr(file2.getName()));
			}
		});
		
		if (contents.size() != 0) {
			try {
				
				String beforeTime = null;
				for (int j = 0; j < contents.size(); j++) {
					
					File flittle = contents.get(j);
					if(time1-flittle.lastModified() > publicLoadConf.fileDuration){
						
						//创建输入流读取源文件
						InputStreamReader read = new InputStreamReader(new FileInputStream(flittle), "UTF-8");
						BufferedReader reader = new BufferedReader(read);
						
						//获取最后修改时间
						String thisTime = formatModifyTime(flittle.lastModified());

						if(splitTimeStr(flittle.getName())!=beforeTime){
							beforeTime = splitTimeStr(flittle.getName());
							initNumMap();
						}
						
						String line;
						while ((line = reader.readLine()) != null) {
							
							//拼接字符串
							tmpArray = line.split(",");
							tmpLine = new StringBuffer();
							tmpLine.append(timeUtil.format2OtherTime(tmpArray[5]));
							tmpLine.append(splitString);
							tmpLine.append(timeUtil.format2OtherTime(tmpArray[5]));
							tmpLine.append(splitString);
							tmpLine.append(tmpArray[1]);
							tmpLine.append(splitString);
							tmpLine.append(tmpArray[2]);
							tmpLine.append(splitString);
							tmpLine.append(tmpArray[3]);
							tmpLine.append(splitString);
							tmpLine.append(tmpArray[4]);
							tmpLine.append(splitString);
							tmpLine.append(splitString);

							/*
							 * 端口和协议的对应字典
							 */
							String protocal;
							if(tmpArray[4].equals("443")){
								protocal = "HTTPS";
							}else if(tmpArray[4].equals("21")){
								protocal = "FTP";
							}else if(tmpArray[4].equals("25")){
								protocal = "SMTP";
							}else if(tmpArray[4].equals("143")){
								protocal = "IMAP";
							}else if(tmpArray[4].equals("110")){
								protocal = "POP3";
							}else if(tmpArray[4].equals("53")){
								protocal = "DNS";
							}else if(tmpArray[4].equals("161")||tmpArray[3].equals("162")){
								protocal = "SNMP";
							}else{
								protocal = "OTHER";
							}
							tmpLine.append(protocal);
							tmpLine.append(splitString);
							for (int i = 0; i < 8; i++) {
								tmpLine.append(splitString);
							}
							
							/*
							 * 遍历目标路径中文件数，得到num，统计协议名、最后修改时间一样，并且文件的大小大于size的文件个数
							 */
							
							/*int num = 0;
							File[] desFiles = new File(dstpath).listFiles();
							for(File f : desFiles){
								if(f.getName().startsWith("A" + protocal + "P" + msgno + "01D" + thisTime + "E") && f.length()>size){
									num++;
								}
							}*/
							
							int num = numMap.get(protocal);
							
							/*
							 * 创建文件对象
							 */
							fileName = "A" + protocal + "P" + msgno + "01D" + thisTime + "E" + supplyNo(num) + ".txt";
							File dstFile = new File(dstpath, fileName);

							//获取输出流
							OutputStream out = getWriter(dstFile,protocal);
							OutputStreamWriter writer = new OutputStreamWriter(out, "UTF-8");

							writer.write(tmpLine.toString());
							writer.write("\r\n");
							writer.flush();
							
							if(dstFile.length()>size){
								numMap.put(protocal, ++num);
							}
						}
						reader.close();
						// 删除
						flittle.delete();
					}
				}
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}finally{
				closeFile();
			}
		}
		long time2 = System.currentTimeMillis();
		System.out.println("任务结束时间：" + time2);
		System.out.println("任务用时：" + (time2-time1)/1000.0 + "s");
	}
	
//	private int getNumber(String protocal) {
//		int num = 0;
//		if(numMap.containsKey(protocal)){
//			num = numMap.get(protocal);
//		}
//		return num;
//	}

	/**
	 * 遍历目录，得到所有文件
	 * @param srcpath 要遍历的目录
	 * @return 目录下的文件
	 */
	private List<File> fileWalker(String srcpath) {
		List<File> files = new ArrayList<File>();
		String[] paths = srcpath.split("\\|");
		for (String p : paths) {
			File path = new File(p);
			if (path.exists()) {
				File[] contents = path.listFiles();
//				if (contents.length == 0) {
//					path.delete();
//				}
				for (File f : contents) {
					if (f.isFile()) {
						files.add(f);
					}
					if (f.isDirectory()) {
						files.addAll(fileWalker(f.getAbsolutePath()));
					}
				}
			}
		}
		return files;
	}
	
	/**
	 * 把时间格式化成yyyyMMddHHmmss的形式
	 * @param time
	 * @return
	 */
	private String formatModifyTime(long time){
		Date date = new Date(time);
		SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
		return format.format(date);
	}
	
	/**
	 * 得到输出流，如果输出流容器中存在，则返回容器中的；
	 * 如果没有，则创建一个，放到容器中，以方便下次使用；
	 * 如果容器中有，但是输出的目标文件不一样，就把原来的close掉，在创建新的，放到容器中供下次使用
	 * 
	 * @param dstFile 目标文件
	 * @param protocal 协议，Map中的key
	 * @return 输出流
	 * @throws IOException
	 */
	private MyFileOutputStream getWriter(File dstFile,String protocal) throws IOException{
		
		MyFileOutputStream stream = outStreamMap.get(protocal);
		if(stream == null){
			MyFileOutputStream o = new MyFileOutputStream(dstFile, true);
			outStreamMap.put(protocal, o);
			return o;
		}else{
			if(stream.getFile().equals(dstFile)){
				return stream;
			}else{
				stream.close();
				MyFileOutputStream o = new MyFileOutputStream(dstFile, true);
				outStreamMap.put(protocal, o);
				return o;
			}
		}
	}
	
	/**
	 * 关闭文件句柄 在文件轮换或读取完毕后
	 */

	private void closeFile() {
		try {
			for (Map.Entry<String, MyFileOutputStream> key : outStreamMap.entrySet()) {
				MyFileOutputStream stream = key.getValue();
				if(stream != null){
					stream.close();
				}
			}
			zipFile();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private void zipFile() {
		try {
			System.out.println(dstpath);
			gzipUtil.zipFile(dstpath + fileName);
		} catch (Exception e) {
			System.out.println("zipFile出错了");
			e.printStackTrace();
		}
	}

	// 从文件名中拆出对应的14位时间
	private String splitTimeStr(String oldstr) {
		return oldstr.substring(17, 29);
	}

	private String supplyNo(int no) {
		String ret = String.valueOf(no);

		for (int i = ret.length(); i < 3; i++) {
			ret = "0" + ret;
		}

		return ret;
	}
	
	/**
	 * 单独的文件读取,按照拆分规则进行拆分 处理步骤 1.文件读取 2.文件名称构建 3.按照大小或进行拆分 4.对另存后的文件进行压缩
	 * 
	 * @param filename
	 * @param _14bittime
	 */
	// private void fileReader(File f){
	// String line = "";
	// if(!f.exists()){
	//
	// System.out.println("--- file don't exists!"+f.getName());
	//
	// }else{
	// //文件存在,读取文件
	// InputStreamReader read = null;
	// BufferedReader reader = null;
	// try {
	// read = new InputStreamReader(new FileInputStream(f), "GBK");
	// reader = new BufferedReader(read);
	// int i=0;
	// // StringBuffer sb = new StringBuffer();
	// while ((line = reader.readLine()) != null) {
	// i++;
	// if(i>fileNo){
	// takestr(line,f);
	// }
	// }
	//
	// System.out.println("[totalJC]"+f.getName()+" JC:"+i);
	//
	// }catch(Exception e){
	// System.err.println("[Http.fileReader]"+e.toString());
	// }finally{
	// if(reader!=null){
	// try {
	// reader.close();
	// } catch (IOException e) {
	// }
	// }
	// if(read!=null){
	// try {
	// read.close();
	// } catch (IOException e) {
	// }
	// }
	// }
	// }
	// }

	/**
	 * 核心处理程序
	 * 
	 * @param line
	 * @param _14bitfile
	 */
	// private void takestr(String line, File f) {
	// try{
	// /**
	// * sample
	// * 1387954250.863987|$|117.140.249.237|$|37472|$|211.151.151.6
	// * |$|80|$|http://www.umeng.com/app_logs|$|
	// */
	//
	// tmpArray = line.split("\\|");
	// tmpLine=new StringBuffer();
	// //起止时间,暂时一致
	// tmpLine.append(timeUtil.format2JavaTime(tmpArray[5]));
	// tmpLine.append(splitString);
	// tmpLine.append(timeUtil.format2JavaTime(tmpArray[6]));
	// tmpLine.append(splitString);
	// tmpLine.append(tmpArray[0]);
	// tmpLine.append(splitString);
	// tmpLine.append(tmpArray[1]);
	// tmpLine.append(splitString);
	// tmpLine.append(tmpArray[2]);
	// tmpLine.append(splitString);
	// tmpLine.append(tmpArray[3]);
	// tmpLine.append(splitString);
	// tmpLine.append(tmpArray[7]);
	// tmpLine.append(splitString);
	// tmpLine.append(tmpArray[4]);
	// tmpLine.append(splitString);
	// for(int i=0;i<8;i++){
	// tmpLine.append(splitString);
	// }
	// fileName =
	// "AHTTPP"+msgno+"01D"+splitTimeStr(f.getName())+"E"+supplyNo(fileNo)+".txt";
	// fos = new FileOutputStream(dstpath+File.separator+fileName,true);
	// outs = new OutputStreamWriter(fos, "UTF-8");
	// outs.write(tmpLine.toString());
	// outs.write("\r\n");
	//
	// if(Integer.parseInt(publicLoadConf.httpConf.getConfig().split("\\|")[0])==1)
	// {
	// //按照字节数
	// currentStore = currentStore + tmpLine.toString().length();
	// }else{
	// //按照记录数
	// currentStore = currentStore+1;
	// }
	//
	// //超过阈值，构建新的文件
	// if(currentStore >
	// Integer.parseInt(publicLoadConf.httpConf.getConfig().split("\\|")[1]) &&
	// Integer.parseInt(publicLoadConf.httpConf.getConfig().split("\\|")[1])>0){
	// System.out.println(currentStore);
	// closeFile();
	// fileNo++;
	// // buildFile();
	// currentStore = 0;
	// }
	//
	// }catch(Exception e){
	// e.printStackTrace();
	// }
	// }

	/**
	 * 构建文件
	 * 
	 * @param fl
	 */
	// private void buildFile(){
	// try{
	// fileName =
	// "AHTTPP"+msgno+"01D"+getFileNames().getFirst()+"E"+supplyNo(fileNo)+".txt";
	// fos = new FileOutputStream(dstpath+File.separator+fileName,true);
	// outs = new OutputStreamWriter(fos, "UTF-8");
	// }catch(Exception e){
	// e.printStackTrace();
	// }
	//
	// }

	public static void main(String[] args) {
		OtherHandleThread hht = new OtherHandleThread();
		String fileName = hht.splitTimeStr("WLANLOG_PRO07_20140928170500.dat");
		System.out.println(fileName);
	}
}
