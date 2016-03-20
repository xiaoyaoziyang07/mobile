package com.wlan.diversionWlan.other;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import com.wlan.comm.gzipUtil;
import com.wlan.comm.publicLoadConf;
import com.wlan.comm.timeUtil;

public class OtherHandleThread implements Runnable {

	// 最终输出文件的分隔符
	private final String splitString = "|";
	// 每个字段前后的符号
	// private final String columnString = "'";

	private String srcpath = "";
	private String dstpath = "";
	private String msgno = "";
//	private int type;
	private long size;

	private FileOutputStream fos;
	private OutputStreamWriter outs;
//	private MyFileOutputStream[] fos = new MyFileOutputStream[8];
//	private OutputStreamWriter[] outs = new OutputStreamWriter[8];
	// 记录当前文件的大小或行号
//	private long currentStore = 0;
	// 当前文件的行号
	private int fileNo = 0;

	private String fileName = "";

	// 拆分部分临时变量群
	private StringBuffer tmpLine;
	private String[] tmpArray;

	private List<File> contents;

	public OtherHandleThread() {
		srcpath = publicLoadConf.otherConf.getSrcDirect();
		dstpath = publicLoadConf.otherConf.getDstDirect();
		msgno = publicLoadConf.otherConf.getMsgno();
//		type = Integer.parseInt(publicLoadConf.httpConf.getConfig().split("\\|")[0]);
		size = Long.parseLong(publicLoadConf.otherConf.getConfig().split("\\|")[1]);
	}

	@Override
	public void run() {
		
		long time1 = System.currentTimeMillis();
		System.out.println("任务开始时间" + time1);
		System.out.println("--Other,Handle");

		fileNo = 0;

		contents = fileWalker(srcpath);
		Collections.sort(contents, new Comparator<File>() {

			@Override
			public int compare(File file1, File file2) {
				return splitTimeStr(file1.getName()).compareTo(splitTimeStr(file2.getName()));
			}
		});
		if (contents.size() != 0) {
			try {
				String lastTime = "";
				for (int j = 0; j < contents.size(); j++) {
					
					File flittle = contents.get(j);
					InputStreamReader read = new InputStreamReader(new FileInputStream(flittle), "UTF-8");
					BufferedReader reader = new BufferedReader(read);
					
					String thisTime = formatModifyTime(flittle.lastModified());
					if(thisTime!=lastTime){
						fileNo = 0;
						lastTime = thisTime;
					}
					
					String line;
					while ((line = reader.readLine()) != null) {

						tmpArray = line.split("\\|");
						tmpLine = new StringBuffer();
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
						for (int i = 0; i < 8; i++) {
							tmpLine.append(splitString);
						}
						String protocal;
						if(tmpArray[3].equals("443")){
							protocal = "HTTPS";
						}else if(tmpArray[3].equals("21")){
							protocal = "FTP";
						}else if(tmpArray[3].equals("25")){
							protocal = "SMTP";
						}else if(tmpArray[3].equals("143")){
							protocal = "IMAP";
						}else if(tmpArray[3].equals("110")){
							protocal = "POP3";
						}else if(tmpArray[3].equals("53")){
							protocal = "DNS";
						}else if(tmpArray[3].equals("161")||tmpArray[3].equals("162")){
							protocal = "SNMP";
						}else{
							protocal = "OTHER";
						}
						
						fileName = "A" + protocal + "P" + msgno + "01D" + lastTime + "E" + supplyNo(fileNo) + ".txt";
						File dstFile = new File(dstpath, fileName);
//						List<File> dstFiles = fileWalker(dstpath);
						
//						if(!dstFiles.contains(dstFile)){
//							fos = new FileOutputStream(dstFile);
//							outs = new OutputStreamWriter(fos, "UTF-8");
//						}
						
						fos = new FileOutputStream(dstFile,true);
						outs = new OutputStreamWriter(fos, "UTF-8");
//						OutputStream write = getWriter(dstFile);
//						OutputStreamWriter writer = new OutputStreamWriter(write);
						
						outs.write(tmpLine.toString());
						outs.write("\r\n");
						outs.flush();
						
						fos.close();
						outs.close();
//						if (Integer.parseInt(publicLoadConf.httpConf.getConfig().split("\\|")[0]) == 1) {
//							// 按照字节数
//							currentStore = currentStore + tmpLine.toString().length();
//						} else {
//							// 按照记录数
//							currentStore = currentStore + 1;
//						}
						if(dstFile.length()>size){
							/*fos.close();
							outs.close();*/
							fileNo++;
							fileName = "A" + protocal + "P" + msgno + "01D" + lastTime + "E" + supplyNo(fileNo) + ".txt";
							dstFile = new File(dstpath, fileName);
							fos = new FileOutputStream(dstFile);
							outs = new OutputStreamWriter(fos, "UTF-8");
//							currentStore = 0;
						}
						// 超过阈值，构建新的文件
//						if (currentStore > Integer.parseInt(publicLoadConf.httpConf.getConfig().split("\\|")[1])&& Integer.parseInt(publicLoadConf.httpConf.getConfig().split("\\|")[1]) > 0) {
//							System.out.println(currentStore);
////							closeFile();
//							fileNo++;
//							// buildFile();
//							currentStore = 0;
//						}
					}
					reader.close();
					// 删除
					flittle.delete();
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
		System.out.println("任务用时：" + (time2-time1)/1000.0/60 + "s");
	}

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

	private String formatModifyTime(long time){
		Date date = new Date(time);
		SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
		return format.format(date);
	}
	
	/*private MyFileOutputStream getWriter(File dstFile) throws FileNotFoundException{
		
		for(int i = 0;i<fos.length;i++){
			if(fos[i]!=null&&fos[i].getFile()==dstFile){
				return fos[i];
			}
		}
		return new MyFileOutputStream(dstFile, true);
	}*/
	// private LinkedList<String> getFileNames(){
	// HashSet<String> set = new HashSet<String>();
	// for(File file : contents){
	// set.add(splitTimeStr(file.getName()));
	// }
	// LinkedList<String> fileNames = new LinkedList<String>();
	// fileNames.addAll(set);
	// return fileNames;
	// }
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

	/**
	 * 关闭文件句柄 在文件轮换或读取完毕后
	 */

	private void closeFile() {
		try {
			outs.close();
			fos.close();
			zipFile();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private void zipFile() {
		try {
			gzipUtil.zipFile(dstpath + fileName);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// 从文件名中拆出对应的14位时间
	private String splitTimeStr(String oldstr) {
		return oldstr.substring(14, 28);
	}

	private String supplyNo(int no) {
		String ret = String.valueOf(no);

		for (int i = ret.length(); i < 3; i++) {
			ret = "0" + ret;
		}

		return ret;
	}

	public static void main(String[] args) {
		OtherHandleThread hht = new OtherHandleThread();
		String fileName = hht.splitTimeStr("WLANLOG_PRO07_20140928170500.dat");
		System.out.println(fileName);
	}
}
