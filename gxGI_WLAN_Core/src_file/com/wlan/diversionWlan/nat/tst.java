package com.wlan.diversionWlan.nat;

import java.io.FileOutputStream;

import com.wlan.comm.hexUtil;

public class tst {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		FileOutputStream out;
		try {
			out = new FileOutputStream("G:/AAA.dat");
			byte[] b1=new byte[62];
//type=session source_address=10.112.126.65 source_port=3890 destination_address=183.203.15.112 destination_port=80 nat_source_address=172.16.0.199 nat_source_port=3890 timestamp=20121105000100 elapsed_time=58
//String s = "010a707e410f32b7cb0f700050ac1000c70f32000000005096913c0000003a";
			StringBuffer s = new StringBuffer();
			
			s.append("01");
//			s.append(hexUtil.)
			b1 = hexUtil.hexStringToBytes(s.toString());
			out.write(b1);
			out.write(b1);
		    out.close(); 		
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
