package com.wlan.db;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: </p>
 * @author not attributable
 * @version 1.0
 */

import java.sql.*;

import com.wlan.comm.*;
import com.wlan.db.pool.*;
import com.wlan.comm.publicLoadConf;

public class DBConnection {
	public Connection connection;
	public PreparedStatement preparedStatement;
	public Statement statement;
	public ResultSet resultSet;
	public ConnectionWrapper conn;
	private String userid;
	private String scedeviceid;

	public DBConnection() {
	}

	public void connect() throws SQLException {
		try {
			userid = publicLoadConf.sysUSER;

			this.conn = ConnectionPool.getConnection();
			this.connection = this.conn.connection;
			this.userid = userid;

			/*
			 * System.out.println(
			 * "===================================================");
			 * System.out.println("总的连接数:"+ConnectionPool.getConnectionCount());
			 * System
			 * .out.println("未使用连接:"+ConnectionPool.getNotUsedConnectionCount
			 * ());
			 * System.out.println("使用连接:"+ConnectionPool.getUsedConnectionCount
			 * ()); System.out.println(
			 * "===================================================");
			 */
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public boolean isClosed() throws SQLException {
		return this.conn.isColsed();
	}

	public void close(boolean bCloseConnection) throws SQLException {
		if (resultSet != null) {
			resultSet.close();
		}
		if (preparedStatement != null) {
			preparedStatement.close();
		}
		if (statement != null) {
			statement.close();
		}
		if (bCloseConnection) {
			if (connection != null) {
				connection.close();
				ConnectionPool.pushConnectionBackToPool(this.conn, this.userid);
			}
		}
	}

	protected void finalize() {
		try {
			this.close(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void setScedeviceid(String scedeviceid) {
		this.scedeviceid = scedeviceid;
	}

	public String getScedeviceid() {
		return scedeviceid;
	}
}
