package cn.amichina.liyang;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import org.junit.Test;

import com.wlan.comm.operationUtil;
import com.wlan.comm.timeUtil;

public class UtilsTest {

	@Test
	public void test01(){
		System.out.println(operationUtil.DateTimeFunctionString());
	}
	@Test
	public void test02(){
//		String s = "1411894897.657849";
//		System.out.println(new Date(1411894897000L));
//		System.out.println(timeUtil.returnDate1970to14Supper(s));
//		double d = 1411894799.223045;
//		System.out.println(timeUtil.returnDate1970to14((long)d*1000));
		Date date = new Date(1411894897000L);
		System.out.println(date);
		SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
		System.out.println(format.format(date));
	}
	
	@Test
	public void test03() throws Exception{
		OutputStream ops = new FileOutputStream(new File("D:/1.txt"),true);
		ops.write("aaabbbbbbb".getBytes());
		ops.close();
	}
	
	@Test
	public void test04() throws Exception{
		Properties props = new Properties();
		System.out.println(props.get("sss"));
		
	}
	
	@Test
	public void test05() throws Exception{
//		1411895099.609750
		String s = "1411895099.609750";
    	String[] ss = s.split("\\.");
    	for(String sss : ss){
    		System.out.println(sss);
    	}
	}
	
	@Test
	public void test06() throws Exception{
		List<String> list = new ArrayList<String>();
		Collections.sort(list);
	}
	
	@Test
	public void test07() throws Exception{
		System.out.println(timeUtil.format2JavaTime("1411897019.969793"));
	}
	
	@Test
	public void test08() {
		File file = new File("D:/迅雷下载/blue.rar");
		System.out.println(file.length()/1024.0/1024/1024);
		System.out.println(file.getFreeSpace()/1024.0/1024/1024);
		System.out.println(file.getTotalSpace()/1024.0/1024/1024);
	}
	
	@Test
	public void test09() throws IOException, InterruptedException {
		File file = new File("D:/1.txt");
		file.createNewFile();
		file.setReadOnly();
		Thread.sleep(10000);
		file.delete();
	}
	
	@Test
	public void test10() {
		List<String> list = new ArrayList<String>();
		list.add("2014091509");
		list.add("2014091502");
		list.add("2014091504");
		list.add("2014091503");
		Collections.sort(list, new Comparator<String>() {

			@Override
			public int compare(String o1, String o2) {
				return Long.parseLong(o1)>Long.parseLong(o1)?-1:1;
			}
		});
		for (String string : list) {
			System.out.println(string);
		}
	}
}
