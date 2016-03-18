package cn.amichina.liyang;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

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
		String s = "1411894799.223045";
		System.out.println(timeUtil.returnDate1970to14Supper(s));
		double d = 1411894799.223045;
		Date date = new Date((long)d*1000);
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
}
