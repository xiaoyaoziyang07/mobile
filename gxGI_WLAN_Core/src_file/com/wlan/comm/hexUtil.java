package com.wlan.comm;

import java.math.BigInteger;
import java.nio.ByteBuffer;

public class hexUtil
{
    public hexUtil()
    {
    }

    /**
     * 将可识别的字符串转化为字节码
     *
     * @param s String
     * @return byte[]
     */
    public static byte[] pubStringtoBytes(String s)
    {
        byte[] b = hexUtil.hexStringToBytes(hexUtil.stringToHex2(s));
        ;
        return b;
    }

    /**
     * 直接将Byte[]字节流转换成可读取的String
     *
     * @param b byte[]
     * @return String
     */
    public static String pubBytestoString(byte[] b)
    {
        String rets = "";
        try
        {
            rets = HextoString(HextoString(b));
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        finally
        {
            return rets;
        }
    }

    /**
     * 将ASC字符流装换成十六进制的串
     *
     * @param paydata byte[]
     * @return String
     */
    public static String HextoString(byte[] paydata)
    {
        StringBuffer sb = new StringBuffer();
        String stmp = "";
        for (int ii = 0; ii < paydata.length; ii++)
        {
            stmp = Integer.toHexString(paydata[ii]);
            if (stmp.length() == 1)
            {
                sb.append("0" + stmp);
            }
            else if (stmp.length() > 2)
            {
                sb.append(stmp.substring(stmp.length() - 2, stmp.length()));
            }
            else
            {
                sb.append(stmp);
            }
        }

        return sb.toString();
    }

    /**
     * Convert hex string to byte[]
     *
     * @param hexString the hex string
     * @return byte[]
     */
    public static byte[] hexStringToBytes(String hexString)
    {
        if (hexString == null || hexString.equals(""))
        {
            return null;
        }
        hexString = hexString.toUpperCase();
        int length = hexString.length() / 2;
        char[] hexChars = hexString.toCharArray();
        byte[] d = new byte[length];
        for (int i = 0; i < length; i++)
        {
            int pos = i * 2;
            d[i] = (byte)(charToByte(hexChars[pos]) << 4 | charToByte(hexChars[pos + 1]));
        }
        return d;
    }

    /**
     * Convert char to byte
     *
     * @param c char
     * @return byte
     */
    public static byte charToByte(char c)
    {
        return (byte)"0123456789ABCDEF".indexOf(c);
    }

    /**
     * 将一个byte串解析成为对应的***.***.***.***格式的IP
     *
     * @param ip byte[]
     * @return String
     */
    public static String IpHextoString(byte[] ip)
    {
        StringBuffer sb = new StringBuffer();
        String stmp = "";
        for (int ii = 0; ii < ip.length; ii++)
        {
            stmp = Integer.toHexString(ip[ii]);
            if (stmp.length() == 1)
            {
                sb.append("0" + stmp);
            }
            else if (stmp.length() > 2)
            {
                sb.append(stmp.substring(stmp.length() - 2, stmp.length()));
            }
            else
            {
                sb.append(stmp);
            }
        }
        stmp = "";
        if (sb.toString().length() == 8)
        {
            for (int i = 0; i < 4; i++)
            {
                if (i > 0)
                {
                    stmp = stmp + ".";
                }
                stmp = stmp + String.valueOf(Integer.parseInt(sb.toString().substring(i * 2, i * 2 + 2), 16));
            }
        }
        return stmp;
    }

    /**
     * 8位16进制字符串转换成IP
     *
     * @param sb String
     * @return String
     */
    public static String IpHextoString(String sb)
    {
        String stmp = "";
        if (sb.length() == 8)
        {
            for (int i = 0; i < 4; i++)
            {
                if (i > 0)
                {
                    stmp = stmp + ".";
                }
                stmp = stmp + String.valueOf(Integer.parseInt(sb.substring(i * 2, i * 2 + 2), 16));
            }
        }
        return stmp;
    }
    
    /**
     * 将Ip转换为十六进制格式字符串
     * 10.112.126.65 --> 0a707e41
     * @param sb
     * @return
     */
    public static String IpStringToHex(String sb)
    {
        String sReturn = "",stmp;
        try{
	       String[] sip = sb.split("\\.");
	       for(int i=0;i<sip.length;i++) {
	    	   stmp = con10to16(sip[i]);
	    	   if(stmp.length()==1)stmp="0"+stmp;
	    	   sReturn = sReturn + stmp;
	       }
        }catch(Exception e){
        	e.printStackTrace();
        }
        return sReturn;
    }

    
    /**
     * 解析单个文本
     *
     * @param s String
     * @return String
     */
    public static String contentHex(String s)
    {
        char a;
        a = (char)Integer.parseInt(s, 16);
        return String.valueOf(a);
    }

    /**
     * 将十六进制数转换为十进制数
     *
     * @param s String
     * @return String
     */
    public static long con16to10(String s)
    {
    	return Long.parseLong(s, 16);
//        return Integer.parseInt(s, 16);
    }

    // 将2进制字符串转换为10进制串
    public static long con2to10(String s)
    {
        return Long.parseLong(s, 2);
    }

    // 将2进制数转换为十六进制数
    public static String con2to16(String s)
    {
        return Integer.toHexString(Integer.parseInt(s, 2));
    }

    // 将10进制数转换为十六进制数
    public static String con10to16(String s)
    {
        return Integer.toHexString(Integer.parseInt(s, 10));
    }

    // 将10进制数转换为十六进制数,有补位功能
    public static String con10to16(String s,int bit)
    {	
    	String sret = Integer.toHexString(Integer.parseInt(s, 10));
    	
    	for(int i=sret.length();i<bit;i++){
    		sret = "0"+sret;
    	}
    	
        return sret;
    }

    
    /**
     * 将字符串形式的ip地址转换为BigInteger
     *
     * @param ipInString 字符串形式的ip地址
     * @return 整数形式的ip地址
     */
    public static byte[] StringToBigInt(String ipInString)
    {
        ipInString = ipInString.replace(" ", "");
        byte[] bytes;
        if (ipInString.contains(":"))
            bytes = ipv6ToBytes(ipInString);
        else
            bytes = ipv4ToBytes(ipInString);
        return bytes;
    }

    /**
     * ipv4地址转有符号byte[5]
     *
     * @param ipv4 字符串的IPV4地址
     * @return big integer number
     */
    public static byte[] ipv4ToBytes(String ipv4)
    {
        byte[] ret = new byte[5];
        ret[0] = 0;
        // 先找到IP地址字符串中.的位置
        int position1 = ipv4.indexOf(".");
        int position2 = ipv4.indexOf(".", position1 + 1);
        int position3 = ipv4.indexOf(".", position2 + 1);
        // 将每个.之间的字符串转换成整型
        ret[1] = (byte)Integer.parseInt(ipv4.substring(0, position1));
        ret[2] = (byte)Integer.parseInt(ipv4.substring(position1 + 1, position2));
        ret[3] = (byte)Integer.parseInt(ipv4.substring(position2 + 1, position3));
        ret[4] = (byte)Integer.parseInt(ipv4.substring(position3 + 1));
        return ret;
    }


    /**
     * ipv6地址转有符号byte[17]
     *
     * @param ipv6 字符串形式的IP地址
     * @return big integer number
     */
    private static byte[] ipv6ToBytes(String ipv6)
    {
        byte[] ret = new byte[17];
        ret[0] = 0;
        int ib = 16;
        boolean comFlag = false;// ipv4混合模式标记
        if (ipv6.startsWith(":"))// 去掉开头的冒号
            ipv6 = ipv6.substring(1);
        String groups[] = ipv6.split(":");
        for (int ig = groups.length - 1; ig > -1; ig--)
        {// 反向扫描
            if (groups[ig].contains("."))
            {
                // 出现ipv4混合模式
                byte[] temp = ipv4ToBytes(groups[ig]);
                ret[ib--] = temp[4];
                ret[ib--] = temp[3];
                ret[ib--] = temp[2];
                ret[ib--] = temp[1];
                comFlag = true;
            }
            else if ("".equals(groups[ig]))
            {
                // 出现零长度压缩,计算缺少的组数
                int zlg = 9 - (groups.length + (comFlag ? 1 : 0));
                while (zlg-- > 0)
                {// 将这些组置0
                    ret[ib--] = 0;
                    ret[ib--] = 0;
                }
            }
            else
            {
                int temp = Integer.parseInt(groups[ig], 16);
                ret[ib--] = (byte)temp;
                ret[ib--] = (byte)(temp >> 8);
            }
        }
        return ret;
    }

    // 将二进制字符串格式化成全16位带空格的Binstr
    public static String BinstrToBinstr16(String input)
    {
        StringBuffer output = new StringBuffer();
        String[] tempStr = StrToStrArray(input);
        for (int i = 0; i < tempStr.length; i++)
        {
            for (int j = 16 - tempStr[i].length(); j > 0; j--)
                output.append('0');
            output.append(tempStr[i] + " ");
        }
        return output.toString();
    }

    // 将初始二进制字符串转换成字符串数组，以空格相隔
    public static String[] StrToStrArray(String str)
    {
        return str.split(" ");
    }

    /**
     * 用于将正常的字符串转换为十六进制字符串
     *
     * @param intString String
     * @return String
     */
    public static String stringToHex2(String intString)
    {
        byte[] bytes = intString.getBytes();
        return bytesToHex(bytes);
    }

    public static String bytesToHex(byte[] bytes)
    {
        StringBuilder hexString = new StringBuilder(2 * bytes.length);
        for (int i = 0; i < bytes.length; i++)
        {
            String hexStr = String.format("%2X", bytes[i]);
            hexString.append(hexStr);
        }
        return hexString.toString();
    }

    /**
     * 将十六进制串转成可读的字符串
     *
     * @param hex String
     * @return String
     */
    public static String HextoString(String hex)
    {
        StringBuffer sb = new StringBuffer();
        for (int ii = 0; ii < hex.length() / 2; ii++)
        {
            sb.append(hexUtil.contentHex(hex.substring((2 * ii), 2 + (2 * ii))));
        }
        return sb.toString();
    }

    /**
     * 注释：int到字节数组的转换！
     *
     * @param number
     * @return
     */
    public static byte[] intToByte(int number)
    {
        int temp = number;
        byte[] b = new byte[4];
        for (int i = 0; i < b.length; i++)
        {
            b[i] = new Integer(temp & 0xff).byteValue();//
            // 将最低位保存在最低位
            temp = temp >> 8;// 向右移8位
        }
        return b;
    }

    /**
     * 注释：字节数组到int的转换！
     *
     * @param b
     * @return
     */
    public static int byteToInt(byte[] b)
    {
        int s = 0;
        int s0 = b[0] & 0xff;// 最低位
        int s1 = b[1] & 0xff;
        int s2 = b[2] & 0xff;
        int s3 = b[3] & 0xff;
        s3 <<= 24;
        s2 <<= 16;
        s1 <<= 8;
        s = s0 | s1 | s2 | s3;
        return s;
    }

    /**
     * short到字节数组的转换！
     *
     * 采用网络字节传输 高位在上
     *
     * @param s
     * @return
     */
    public static byte[] shortToByte(short number)
    {
        int temp = number;
        byte[] b = new byte[2];
        for (int i = 0; i < b.length; i++)
        {
            b[i] = new Integer(temp & 0xff).byteValue();//
            // 将最低位保存在最低位
            temp = temp >> 8;// 向右移8位
        }
        return b;
    }

    /**
     * 注释：字节数组到short的转换！
     *
     * @param b
     * @return
     */
    public static short byteToShort(byte[] b)
    {
        short s = 0;
        short s0 = (short)(b[0] & 0xff);// 最低位
        short s1 = (short)(b[1] & 0xff);
        s1 <<= 8;
        s = (short)(s0 | s1);
        return s;
    }

    /**
     * long 转 byte 采用网络字节传输 高位在上
     *
     * @param number
     * @return
     * @see [类、类#方法、类#成员]
     */
    public static byte[] longToByte(long number)
    {
        long temp = number;
        byte[] b = new byte[8];
        for (int i = 0; i < b.length; i++)
        {
            b[i] = new Long(temp & 0xff).byteValue();//
            // 将最低位保存在最低位
            temp = temp >> 8;// 向右移8位
        }
        return b;
    }

    /**
     * byte to long
     *
     * 采用网络字节传输 高位在上
     *
     * @param b
     * @return
     * @see [类、类#方法、类#成员]
     */
    public static long byteToLong(byte[] b)
    {
        long s = 0;
        long s0 = b[0] & 0xff;// 最低位
        long s1 = b[1] & 0xff;
        long s2 = b[2] & 0xff;
        long s3 = b[3] & 0xff;
        long s4 = b[4] & 0xff;// 最低位
        long s5 = b[5] & 0xff;
        long s6 = b[6] & 0xff;
        long s7 = b[7] & 0xff;

        // s0不变
        s1 <<= 8;
        s2 <<= 16;
        s3 <<= 24;
        s4 <<= 8 * 4;
        s5 <<= 8 * 5;
        s6 <<= 8 * 6;
        s7 <<= 8 * 7;
        s = s0 | s1 | s2 | s3 | s4 | s5 | s6 | s7;
        return s;
    }

    /**
     * 补足位数，不足位数的补0
     *
     * @param input
     * @param lenth
     * @return
     * @see [类、类#方法、类#成员]
     */
    public static byte[] paddingZero(byte[] input, int lenth)
    {
        if (input.length < lenth)
        {
            ByteBuffer buffer = ByteBuffer.allocate(lenth);
            buffer = buffer.put(input);
            input = buffer.array();
        }

        return input;
    }

    public static void main(String[] args)
    {
        // 将字符串转换成字节流byte[]
        // System.out.println(hexUtil.HextoString("303d800101a138a036800b4c49435f30303030303031a1078405313233344c820312d687830731323334353637841041757468656e7469636174696f6e5f31"));
        // System.out.println(hexUtil.stringToHex2("ANDREW"));
        // byte[] bb = hexUtil.hexStringToBytes(hexUtil.stringToHex2("ANDREW"));
        //
        // System.out.println(bb.length);
        // String ss = hexUtil.HextoString(bb);
        // System.out.println(hexUtil.HextoString(ss));

//         String ssa = "20110905190909";
//         byte[] bt = hexUtil.hexStringToBytes(ssa);
//
//         for(int i=0;i< bt.length;i++){
//             System.out.println(hexUtil.con10to16(bt[i]+""));
//         }

//        String ip = "0a0b0a0a";
//        String ips = hexUtil.IpHextoString(ip);
//        
//        System.out.println("ips:"+ips);
        //
        // System.out.println(hexUtil.ipv4ToBytes(ips));

        // byte[] bb = hexUtil.pubStringtoBytes("00000000");

//        System.out.println(22);
         
         //ip --> ip byte
//         String ipss = "10.11.10.10";
//         System.out.println(hexUtil.IpStringToHex(ipss));
    	System.out.println(hexUtil.con10to16("1369118771"));
    }
}
