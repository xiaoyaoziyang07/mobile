package cn.amichina.liyang;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
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
}
