package com.wlan.db.users;

import java.util.*;

public interface PoolUsers {
	public LinkedList getnotUsedConnection();

	public HashSet getusedUsedConnection();

	public String getURL();

	public String getUserid();

	public String getPassword();
}
