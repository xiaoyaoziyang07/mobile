package com.wlan.db.users;

import java.util.*;
import javax.naming.*;

import com.wlan.comm.publicLoadConf;

public class PMUSER implements PoolUsers {
	private static LinkedList m_notUsedConnection = new LinkedList();
	private static HashSet m_usedUsedConnection = new HashSet();
	private static String m_url = "";
	private static String m_user = "";
	private static String m_password = "";

	static {
		init();
	}

	public PMUSER() {
	}

	private static void init() {
		m_user = publicLoadConf.sysUSER;
		m_password = publicLoadConf.sysPWD;
	}

	public String getURL() {
		return this.m_url;
	}

	public String getUserid() {
		return this.m_user;
	}

	public String getPassword() {
		return this.m_password;
	}

	public LinkedList getnotUsedConnection() {
		return this.m_notUsedConnection;
	}

	public HashSet getusedUsedConnection() {
		return this.m_usedUsedConnection;
	}

}
