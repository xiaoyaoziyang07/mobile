package com.wlan.comm;

import java.io.*;
import java.sql.*;
import java.util.*;

import ch.ethz.ssh2.*;
import ch.ethz.ssh2.Connection;

/**
 * 用于scp传输操作的主体文件
 * @author andrew
 */
public class netSshComm {
	private List filelist = new ArrayList();
	private String hostname;
	private String username;
	private String password;
	private int port;
	private Connection conn;
	private String keyfile = "";
	// private Session sess;
	private SCPClient client;
	//freebsd
	private String limitstr = "	";
	//linux
//	private String limitstr = " ";
	//识别文件是WLAN文件的方法
	private String wlanfileIt = "WLL";//"WLL";

	public netSshComm(String hostname, String username, String password, String keyfile) {
		this.hostname = hostname;
		this.username = username;
		this.password = password;
		this.port = 22;
		this.keyfile = keyfile;
		this.conn = new Connection(this.hostname);
	}

	/**
	 * 构造函数,port为指定
	 * 
	 * @param hostname
	 *            String
	 * @param username
	 *            String
	 * @param password
	 *            String
	 * @param port
	 *            int
	 */
	public netSshComm(String hostname, String username, String password, int port, String keyfile) {
		this.hostname = hostname;
		this.username = username;
		this.password = password;
		this.port = port;
		this.keyfile = keyfile;
		this.conn = new Connection(this.hostname, this.port);
	}

	/**
	 * 连接,中间用filePushRemote的端口做了一下测试
	 * 
	 * @return boolean - true连接成功 - false连接失败
	 * @throws IOException
	 */
	public boolean login() throws IOException {
		boolean isAuthenticated = true;
		// 首先判断远程主机能否登陆
		try {
			conn.connect();
			if (this.keyfile.equals("")) {
				// 普通方式认证
				isAuthenticated = conn.authenticateWithPassword(this.username, this.password);
			} else {
				// key方式认证
				File filekey = new File(this.keyfile);
				isAuthenticated = conn.authenticateWithPublicKey(this.username, filekey, this.password);
			}
			// sess = conn.openSession();
			client = new SCPClient(this.conn);
		} catch (Exception e) {
			e.printStackTrace();
			isAuthenticated = false;
		}

		return isAuthenticated;
	}

	/**
	 * 发送远程命令
	 * 
	 * @param command
	 *            String
	 * @return String
	 * @throws IOException
	 */
	public String sendRemoteCommand(String command) throws IOException {
		String retLine = "";
		Session sess = conn.openSession();
		try {
			sess.execCommand(command);

			InputStream stdout = new StreamGobbler(sess.getStdout());
			BufferedReader br = new BufferedReader(new InputStreamReader(stdout));
			while (true) {
				String line = br.readLine();
				if (line == null) {
					break;
				} else {
					retLine = retLine + " " + line;
				}
			}
			stdout.close();
			br.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			sess.close();
			System.out.println("close!");
			sess.close();
		}
		return retLine;
	}

	/**
	 * 加入要传输的文件
	 * 
	 * @param fileAllPathName
	 *            String
	 */
	public void SetFile(String fileAllPathName) {
		filelist.add(fileAllPathName);
	}

	/**
	 * 传输文件
	 * 
	 * @param remotepath
	 *            String - 要传输的远程主机的路径
	 * @return boolean - true传输成功 false没有定义要传输的文件
	 * @throws IOException
	 */
	public boolean pushfile(String remotepath) throws IOException {
		if ((this.filelist == null) || (this.filelist.size() == 0)) {
			return false;
		} else {
			try {
				String[] newArr = new String[this.filelist.size()];
				// 传送文件
				for (int i = 0; i < newArr.length; i++) {
					newArr[i] = (String) this.filelist.get(i);
				}
				client.put(newArr, remotepath);
				//传后清空
				this.filelist = new ArrayList();
			} catch (Exception e) {
				e.printStackTrace();
			}
			return true;
		}
	}
	
	public void chmodFile() throws IOException {

		this.sendRemoteCommand("chmod 666 /home/nat_tmp/*");

//		String filename;
//		String[] newArr = new String[this.filelist.size()];
//		// 传送文件
//		for (int i = 0; i < newArr.length; i++) {
//			newArr[i] = (String) this.filelist.get(i);
//		}
//
//		for (int i = 0; i < newArr.length; i++) {
//			// 从newArr中返回文件名称
//			filename = this.retFileName(newArr[i]);
//		}
	}

	private String retFileName(String oldstring) {
		String[] s = oldstring.split("/");
		return s[s.length - 1];
	}
	
	/**
	 * 从服务器上下载文件
	 */
	public boolean getFileFromServer(String srcpath,String localpath){
		  boolean retbool = false;
		  try {
			  SCPClient client = new SCPClient(conn);
			  
		      client.get(srcpath, localpath);
		      retbool = true;
		  }catch(Exception e){
		      System.out.println("[getFileFromServer.e]" + e.toString());
		      e.printStackTrace();
		  }finally{
		      return retbool;
		  }
	}	

	/**
	 * 列出目录下的所有文件名
	 * @param path
	 * @return
	 * @throws IOException
	 */
    public ArrayList<String> exportfilename(String path) throws IOException {
    	
    	ArrayList<String> list = new ArrayList<String>();
    	
	    byte[] buffer = new byte[10240];
	    boolean returnOk = false;
	    try {
	      StringBuffer sb = new StringBuffer();
	      sb.append("AAAAAA\r");
	      sb.append("cd " + path + "\r");
	      sb.append("ls\r" );
	      sb.append("exit\r");

	      Session sess = conn.openSession();
	      sess.requestDumbPTY();
	      sess.startShell();

	      InputStream stdout = sess.getStdout();
	      InputStream stderr = sess.getStderr();

	      BufferedOutputStream stdin = new BufferedOutputStream(sess.getStdin());

	      stdin.write( (sb.toString()).getBytes());
	      stdin.flush();
	      stdin.close();

	      while (true) {
	        if ( (stdout.available() == 0) && (stderr.available() == 0)) {
	          int conditions = sess.waitForCondition(ChannelCondition.
	                                                 STDOUT_DATA | ChannelCondition.STDERR_DATA
	                                                 | ChannelCondition.EOF, 2000);
	          if ( (conditions & ChannelCondition.TIMEOUT) != 0) {
	            throw new IOException("Timeout while waiting for data from peer.");
	          }
	          if ( (conditions & ChannelCondition.EOF) != 0) {
	            if ( (conditions &
	                  (ChannelCondition.STDOUT_DATA |
	                   ChannelCondition.STDERR_DATA)) == 0) {
	              break;
	            }
	          }
	        }

	        if (stdout.available() > 0) {

	          int len = stdout.read(buffer);
	          if (len > 0) {
	            stdout = new StreamGobbler(sess.getStdout());
	            BufferedReader br = new BufferedReader(new InputStreamReader(stdout));
	            String line = "";
	            
	            boolean isRead = false;
	            ArrayList<String> list2;
	            while ( (line = br.readLine()) != null) {
	            	if(!isRead){
		            	if(line.toLowerCase().indexOf("aaaaaa: command not found")>-1){
		            		isRead = true;
		            		continue;
		            	}
	            	}else{
	            		list2 = new ArrayList<String>();
	            		list2 = this.handleLine(line);
	            		
	            		if(list2!=null && list2.size()>0){
	            			for(int j=0;j<list2.size();j++){
	            				if(list2.get(j).indexOf(this.wlanfileIt)==0){
	            					list.add(list2.get(j).trim());
	            				}
	            			}
	            		}
	            	}
	            }
	          }
	        }
	      }

	      sess.close();
	      returnOk = true;
	    } catch (Exception e) {
	      e.printStackTrace();
	    }
	    finally {
	      return list;
	    }
	}
	
    private ArrayList<String> handleLine(String line){
    	ArrayList<String> larr = new ArrayList<String>();
//System.out.println("------------");    	
    	try{
    		
//    		System.out.println(line);
    		while (line.indexOf("#")>-1){
    			line = line.substring(line.indexOf("#")+1);
    		}
//    		System.out.println(line);

    		while(line.length()>0){
    			if(line.indexOf(limitstr)>0){
    				larr.add(line.substring(0,line.indexOf(limitstr)).trim());
    				line = line.substring(line.indexOf(limitstr)).trim();
    			}else{
    				larr.add(line.trim());
    				line = "";
    			}
//    			System.out.println(line);
    		}
    		
    	}catch(Exception e){
    		e.printStackTrace();
    	}
    	
//		for(int j=0;j<larr.size();j++){
//			System.out.println(larr.get(j));
//		}

    	return larr;
    }
    
	/**
	 * 关闭连接
	 */
	public void disconnect() {
		// this.sess.close();
		this.conn.close();
	}

	public static void main(String[] args) throws IOException, InterruptedException {
		// 返回某个ID IP and
		netSshComm net = new netSshComm("192.168.0.188", "root", "amichina", 22, "");
		if (net.login()) {
			ArrayList<String> list = net.exportfilename("/usr/local");
			
			for(int i=0;i<list.size();i++){
				System.out.println(i+" "+list.get(i));
			}
		} else {
			System.out.println("login failure!");
		}
		net.disconnect();
	}
}
