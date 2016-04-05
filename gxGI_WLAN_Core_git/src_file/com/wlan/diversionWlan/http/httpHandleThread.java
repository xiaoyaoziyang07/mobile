package com.wlan.diversionWlan.http;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.wlan.comm.gzipUtil;
import com.wlan.comm.publicLoadConf;
import com.wlan.comm.timeUtil;

public class httpHandleThread implements Runnable {

	// 最终输出文件的分隔符
	private final String splitString = "|";
	// 每个字段前后的符号
	// private final String columnString = "'";

	private String srcpath = "";
	private String dstpath = "";
	private String msgno = "";
//	private int type;
	private long size;

	private FileOutputStream fos = null;
	private OutputStreamWriter outs = null;
	// 记录当前文件的大小或行号
	private long currentStore = 0;
	// 当前文件的行号
	private int fileNo = 0;

	private String fileName = "";

	// 拆分部分临时变量群
	private StringBuffer tmpLine;
	private String[] tmpArray;

	private List<File> contents;

	public httpHandleThread() {
		/*
		 * 构造函数初始化配置参数
		 */
		srcpath = publicLoadConf.httpConf.getSrcDirect();
		dstpath = publicLoadConf.httpConf.getDstDirect();
		msgno = publicLoadConf.httpConf.getMsgno();
		size = Long.parseLong(publicLoadConf.httpConf.getConfig().split("\\|")[1]);
	}

	@Override
	public void run() {
		
		long time1 = System.currentTimeMillis();
		System.out.println("任务开始时间" + time1);
		System.out.println("--HTTP,Handle");

		fileNo = 0;
		long t1 = System.currentTimeMillis();
		//源目录下所有文件
		contents = fileWalker(srcpath);
		long t2 = System.currentTimeMillis();
		System.out.println(t2-t1);
		//文件按照时间排序
		Collections.sort(contents, new Comparator<File>() {

			@Override
			public int compare(File file1, File file2) {
				return splitTimeStr(file1.getName()).compareTo(splitTimeStr(file2.getName()));
			}
		});
		System.out.println("The File Count:"+contents.size()+" In Path:"+dstpath);
		System.out.println("srcPath:"+srcpath);
		if (contents.size() > 0) {
			try {
				File currentFile = contents.get(0);
				String currentTime = splitTimeStr(currentFile.getName());
				File nextFile = null;
				String nextTime = null;
				
				for (int j = 0; j < contents.size(); j++) {
					
					System.out.println("Process File:"+currentFile.getAbsolutePath()+File.pathSeparator+currentFile.getName());
					if(time1-currentFile.lastModified()>publicLoadConf.fileDuration){
						System.out.println("Read File..");
						//创建输入流读取源文件
						InputStreamReader read = new InputStreamReader(new FileInputStream(currentFile), "UTF-8");
						BufferedReader reader = new BufferedReader(read);
						
						//根据要求创建文件对象
						fileName = "AHTTPP" + msgno + "01D" + currentTime + "E" + supplyNo(fileNo) + ".txt";
						File dstFile = new File(dstpath, fileName);
						//遍历目标路径，如果不包含该文件，就创建文件并初始化输出流
						List<File> dstFiles = fileWalker(dstpath);
						if(!dstFiles.contains(dstFile)){
							fos = new FileOutputStream(dstFile);
							outs = new OutputStreamWriter(fos, "UTF-8");
						}

						String line;
						while ((line = reader.readLine()) != null) {
							//拼接字符串
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
							outs.write(tmpLine.toString());
							outs.write("\r\n");
							currentStore = currentStore + tmpLine.toString().getBytes("GBK").length + 2;
//							outs.flush();
							System.out.println("Write File ");
							//判断是否超过配置的大小
							if(currentStore>size){
								System.out.println("dstFile Size Out "+size+" Create new File");
								closeWriter();
								fileNo++;
								fileName = "AHTTPP" + msgno + "01D" + splitTimeStr(currentFile.getName()) + "E" + supplyNo(fileNo) + ".txt";
								dstFile = new File(dstpath, fileName);
								fos = new FileOutputStream(dstFile);
								outs = new OutputStreamWriter(fos, "UTF-8");
							}
						}
						
						System.out.println("reader close");
						reader.close();
						// 删除原文件
						currentFile.delete();
						
						if(j!=contents.size()-1){
							nextFile = contents.get(j+1);
							nextTime = splitTimeStr(nextFile.getName());
							if(!currentTime.equals(nextTime)){
								closeWriter();
								fileNo = 0;
								zipFile(dstpath + "*.txt");
							}
							currentFile = nextFile;
							currentTime = nextTime;
						}
						
						if(j == contents.size()-1){
							closeWriter();
							zipFile(dstpath + "*.txt");
						}
						
						
					}
				}
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}finally{
				try {
					if(fos!=null){
						fos.close();
					}
					if(outs!=null){
						outs.flush();
						outs.close();
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		long time2 = System.currentTimeMillis();
		System.out.println("任务结束时间：" + time2);
		System.out.println("任务用时：" + (time2-time1)/1000.0 + "s");
	}

	
	/**
	 * 遍历目录，得到目录下的所有文件
	 * @param srcpath 目录
	 * @return 文件的集合
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
	 * 关闭文件句柄 在文件轮换或读取完毕后
	 */

	private void closeWriter() {
			try {
				fos.close();
				outs.flush();
				outs.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
	}

	private void zipFile(String cmd) {
		try {
			gzipUtil.zipFile(cmd);
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
}
