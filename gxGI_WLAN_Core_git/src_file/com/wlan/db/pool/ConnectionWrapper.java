package com.wlan.db.pool;

/**
 * <p>Title: 数据库连接池反射拦截</p>
 * <p>Description: 利用反射机制拦截数据库连接关闭函数并放回连接池</p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: CteSys</p>
 * @author  Yangtao Tao
 * @version 2.0
 */

import java.lang.reflect.*;
import java.sql.*;

public class ConnectionWrapper implements InvocationHandler {

	private final static String CLOSE_METHOD_NAME = "close";
	public Connection connection = null;
	private Connection m_originConnection = null;
	private String user;
	public long lastAccessTime = System.currentTimeMillis();
	Exception debugInfo = new Exception("Connection initial statement");

	ConnectionWrapper(Connection con, String loginUser) {
		Class[] interfaces = { java.sql.Connection.class };
		this.connection = (Connection) Proxy.newProxyInstance(con.getClass().getClassLoader(), interfaces, this);
		m_originConnection = con;
		user = loginUser;
	}

	/**
	 * 是否已经关闭或者无法连接
	 * 
	 * @return boolean true - 已经关闭 false - 没有关闭
	 * @throws SQLException
	 */
	public boolean isColsed() throws SQLException {
		return this.connection.isClosed();
	}

	public void close() throws SQLException {
		m_originConnection.close();
	}

	public Object invoke(Object object, Method method, Object[] objectArray) throws Exception {
		Object obj = null;
		if (CLOSE_METHOD_NAME.equals(method.getName())) {
			ConnectionPool.pushConnectionBackToPool(this, this.user);
		} else {
			obj = method.invoke(m_originConnection, objectArray);
		}
		lastAccessTime = System.currentTimeMillis();
		return obj;
	}
}
