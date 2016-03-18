package com.wlan.db.pool;

import java.io.*;
import java.sql.*;
import java.util.*;
import javax.naming.*;

import com.wlan.db.users.*;
import com.wlan.comm.publicLoadConf;
import com.wlan.db.users.PMUSER;

public class ConnectionPool {

	// private static LinkedList m_notUsedConnection = new LinkedList();
	// private static HashSet m_usedUsedConnection = new HashSet();
	private static HashMap m_usermap = new HashMap();
	private static HashMap m_pooluser = null; // PoolUsers
	private static String m_users = "";
	private static String m_user = "";
	private static String m_url = "";
	private static String m_password = "";
	static final boolean DEBUG = false;

	/**
	 * 2007年5月12日增加双层释放功能 CHECK_CLOSED_CONNECTION_TIME为高层释放的时间，现在设定为12小时
	 * 释放全部无用连接 测试间隔为60分钟
	 * */
	public static long CHECK_CLOSED_CONNECTION_TIME = 12 * 60 * 60 * 1000; // 检查未使用连接时间12
																			// *
																			// 60
																			// *
																			// 60
																			// *
																			// 1000(12小时)
	/**
	 * 高层上次释放的时间
	 * */
	static private long m_lastClearClosedConnection = System.currentTimeMillis();
	/**
	 * 5月12日增加双层释放功能 CHECK_CLOSED_CONNECTION_TIME为低层释放的时间，现在设定为每20分钟释放1/2无用连接
	 * 测试间隔为1分钟
	 * */
	public static long CHECK_PRE_CLOSED_CONNECTION_TIME = 60 * 60 * 1000; // 检查未使用连接时间1
																			// *
																			// 60
																			// *
																			// 60
																			// *
																			// 1000(1小时)
	/**
	 * 低层上次释放的时间
	 * */
	static private long m_pre_lastClearClosedConnection = System.currentTimeMillis();

	static {
		init();
	}

	public ConnectionPool() {
	}

	private static void init() {
		m_url = publicLoadConf.sysURL;
		m_users = publicLoadConf.sysUSER;
		String loginUser = "";
		String className = "";
		m_pooluser = new HashMap();
		m_usermap = new HashMap();
		try {
			Driver driver = null;

			driver = (Driver) Class.forName(publicLoadConf.sysDBDriver).newInstance();
			DriverManager.registerDriver(driver);

			className = m_users;

			PMUSER plmm = new PMUSER();
			m_pooluser.put(className, plmm);
			loginUser = "com.wlan.db.users." + className;
			m_usermap.put(className, (PoolUsers) Class.forName(loginUser).newInstance());

		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	private void initDBUsers(String userName) {
		try {
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static synchronized ConnectionWrapper getConnection() throws Exception {

		clearClosedConnection();

		String loginUser = m_users;

		if (((PoolUsers) m_pooluser.get(loginUser.toUpperCase())).getnotUsedConnection() != null) {
			while (((PoolUsers) m_pooluser.get(loginUser.toUpperCase())).getnotUsedConnection().size() > 0) {
				try {
					ConnectionWrapper wrapper = (ConnectionWrapper) ((PoolUsers) m_pooluser.get(loginUser.toUpperCase())).getnotUsedConnection().getFirst(); // .removeFirst();
					if (wrapper.connection.isClosed()) {
						((PoolUsers) m_pooluser.get(loginUser.toUpperCase())).getnotUsedConnection().removeFirst();
						continue;
					}
					((PoolUsers) m_pooluser.get(loginUser.toUpperCase())).getusedUsedConnection().add(wrapper);
					((PoolUsers) m_pooluser.get(loginUser.toUpperCase())).getnotUsedConnection().removeFirst();
					if (DEBUG) {
						wrapper.debugInfo = new Exception("Connection initial statement");
					}
					return wrapper;
				} catch (Exception e) {
					System.out.println("ConnectionPool.getConntion.Exception:" + e);
					e.printStackTrace();
				}
			}
		}

		// 如果程序运行到此说明没有成功建立连接
		int newCount = getIncreasingConnectionCount();
		LinkedList list = new LinkedList();
		ConnectionWrapper wrapper;
		for (int i = 0; i < newCount; i++) {
			wrapper = getNewConnection(loginUser);
			if (wrapper != null) {
				list.add(wrapper);
			}
		}
		if (list.size() == 0) {
			return null;
		}

		wrapper = (ConnectionWrapper) list.getFirst();
		((PoolUsers) m_pooluser.get(loginUser.toUpperCase())).getusedUsedConnection().add(wrapper);
		list.removeFirst();
		((PoolUsers) m_pooluser.get(loginUser.toUpperCase())).getnotUsedConnection().addAll(list);
		list.clear();
		return wrapper;
	}

	private static ConnectionWrapper getNewConnection(String loginUser) {
		ConnectionWrapper wrapper;
		try {
			m_user = ((PoolUsers) m_pooluser.get(loginUser.toUpperCase())).getUserid();
			m_password = ((PoolUsers) m_pooluser.get(loginUser.toUpperCase())).getPassword();

			Connection con = DriverManager.getConnection(m_url, m_user, m_password);
			wrapper = new ConnectionWrapper(con, loginUser);
			return wrapper;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static synchronized void pushConnectionBackToPool(ConnectionWrapper con, String userid) {
		boolean exist = ((PoolUsers) m_pooluser.get(userid.toUpperCase())).getusedUsedConnection().remove(con);
		if (exist) {
			((PoolUsers) m_pooluser.get(userid.toUpperCase())).getnotUsedConnection().add(con);
		}
	}

	/**
	 * 清理掉所有的无用连接
	 * 
	 * @return int
	 */
	public static int close() {
		int count1 = 0, count2 = 0;
		int size = 0;
		String stmp;
		StringTokenizer stok = new StringTokenizer(m_users, ",", false);
		while (stok.hasMoreTokens()) {
			stmp = stok.nextToken().trim().toUpperCase();

			Iterator iterator = ((PoolUsers) m_pooluser.get(stmp.toUpperCase())).getnotUsedConnection().iterator();
			while (iterator.hasNext()) {
				try {
					((ConnectionWrapper) iterator.next()).close();
					count1++;
				} catch (Exception e) {
				}
			}
			((PoolUsers) m_pooluser.get(stmp.toUpperCase())).getnotUsedConnection().clear();

			/**
			 * 2009.7.27 正在使用的连接不予释放
			 */
			/*
			 * iterator = ( (PoolUsers)
			 * m_pooluser.get(stmp.toUpperCase())).getusedUsedConnection
			 * ().iterator(); while (iterator.hasNext()) { try {
			 * ConnectionWrapper wrapper = (ConnectionWrapper) iterator.next();
			 * wrapper.close(); if (DEBUG) {
			 * wrapper.debugInfo.printStackTrace(); } count2++; } catch
			 * (Exception e) { } } ( (PoolUsers)
			 * m_pooluser.get(stmp.toUpperCase(
			 * ))).getusedUsedConnection().clear();
			 */

		}
		return count1 + count2;
	}

	/**
	 * 每个用户连接池释放掉1/2无用的连接
	 * 
	 * @return int
	 */
	public static int preclose() {
		int count1 = 0;
		int size = 0; // 需要释放的无用连接数量
		String stmp;
		ConnectionWrapper wrapper;

		StringTokenizer stok = new StringTokenizer(m_users, ",", false);

		while (stok.hasMoreTokens()) {
			stmp = stok.nextToken().trim().toUpperCase();
			// System.out.println("用户名："+stmp);
			count1 = 0;
			size = ((PoolUsers) m_pooluser.get(stmp.toUpperCase())).getnotUsedConnection().size() / 2;
			for (int i = 0; i < size; i++) {
				wrapper = (ConnectionWrapper) ((PoolUsers) m_pooluser.get(stmp.toUpperCase())).getnotUsedConnection().getFirst();
				try {
					wrapper.close();
				} catch (SQLException ex) {
				}
				((PoolUsers) m_pooluser.get(stmp.toUpperCase())).getnotUsedConnection().removeFirst();
			}
			count1 = count1 + size;
		}
		// System.out.println("清理未用连接："+ count1);
		return count1;
	}

	private static void clearClosedConnection() {

		int j = 0;
		long time = System.currentTimeMillis();

		// 首先判断是否到了高层的释放时间
		if (time - m_lastClearClosedConnection >= CHECK_CLOSED_CONNECTION_TIME) {
			// System.out.println("*************进行高端的释放****************");
			// System.out.println("---------清理前pool size Not Userd-----------:"
			// + getNotUsedConnectionCount());
			// System.out.println("---------清理前pool size Userd-----------:" +
			// getUsedConnectionCount());
			m_lastClearClosedConnection = time;
			m_pre_lastClearClosedConnection = time;
			// 复位上次的检查时间
			close();
			// System.out.println("---------清理后pool size Not Userd-----------:"
			// + getNotUsedConnectionCount());
			// System.out.println("---------清理后pool size Userd-----------:" +
			// getUsedConnectionCount());
		}
		// 首先判断是否到了低层的释放时间
		if (time - m_pre_lastClearClosedConnection >= CHECK_PRE_CLOSED_CONNECTION_TIME) {
			// System.out.println("*************进行低端的释放****************");
			// System.out.println("---------清理前pool size Not Userd-----------:"
			// + getNotUsedConnectionCount());
			// System.out.println("---------清理前pool size Userd-----------:" +
			// getUsedConnectionCount());
			m_pre_lastClearClosedConnection = time;
			preclose();
			// System.out.println("---------清理后pool size Not Userd-----------:"
			// + getNotUsedConnectionCount());
			// System.out.println("---------清理后pool size Userd-----------:" +
			// getUsedConnectionCount());
		}
	}

	/**
	 * 取得增加连接数，增加幅度为1/4
	 * 
	 * @return count
	 */
	public static int getIncreasingConnectionCount() {
		int count = 1;
		int current = getConnectionCount();
		count = current / 4;
		if (count < 1) {
			count = 1;
		}
		return count;
	}

	/**
	 * 取得减少连接数，减少幅度为1/3
	 * 
	 * @return current
	 */
	public static int getDecreasingConnectionCount() {
		int count = 0;
		int current = getConnectionCount();
		if (current < 10) {
			return 0;
		}
		return current / 3;
	}

	/**
	 * 输出调试信息
	 */
	public synchronized static void printDebugMsg() {
		printDebugMsg(System.out);
	}

	public synchronized static void printDebugMsg(PrintStream out) {
		if (DEBUG == false) {
			return;
		}
		StringBuffer msg = new StringBuffer();
		msg.append("debug message in " + ConnectionPool.class.getName());
		msg.append("\r\n");
		msg.append("total count is connection pool: " + getConnectionCount());
		msg.append("\r\n");
		msg.append("not used connection count: " + getNotUsedConnectionCount());
		msg.append("\r\n");
		msg.append("used connection, count: " + getUsedConnectionCount());
		out.println(msg);
		/*
		 * Iterator iterator = m_pooluser.getusedUsedConnection().iterator();
		 * while (iterator.hasNext()) { ConnectionWrapper wrapper =
		 * (ConnectionWrapper) iterator.next();
		 * wrapper.debugInfo.printStackTrace(out); } out.println();
		 */
	}

	public static int getNotUsedConnectionCount() {
		int size = 0;
		String stmp;
		StringTokenizer stok = new StringTokenizer(m_users, ",", false);
		while (stok.hasMoreTokens()) {
			stmp = stok.nextToken().trim().toUpperCase();
			size = size + ((PoolUsers) m_pooluser.get(stmp.toUpperCase())).getnotUsedConnection().size();
		}
		return size;
	}

	public static int getUsedConnectionCount() {
		int size = 0;
		String stmp;
		StringTokenizer stok = new StringTokenizer(m_users, ",", false);
		while (stok.hasMoreTokens()) {
			stmp = stok.nextToken().trim().toUpperCase();
			size = size + ((PoolUsers) m_pooluser.get(stmp.toUpperCase())).getusedUsedConnection().size();
		}
		return size;
	}

	public static int getConnectionCount() {
		int size = 0;
		String stmp;
		StringTokenizer stok = new StringTokenizer(m_users, ",", false);
		while (stok.hasMoreTokens()) {
			stmp = stok.nextToken().trim().toUpperCase();
			size = size + ((PoolUsers) m_pooluser.get(stmp.toUpperCase())).getusedUsedConnection().size();
			size = size + ((PoolUsers) m_pooluser.get(stmp.toUpperCase())).getnotUsedConnection().size();
		}
		return size;
	}

	public static String getUrl() {
		return m_url;
	}

	public static void setUrl(String url) {
		if (url == null) {
			return;
		}
		m_url = url.trim();
	}

	public static String getUser() {
		return m_user;
	}

	public static void setUser(String user) {
		if (user == null) {
			return;
		}
		m_user = user.trim();
	}

	public static String getPassword() {
		return m_password;
	}

	public static void setPassword(String password) {
		if (password == null) {
			return;
		}
		m_password = password.trim();
	}

}
