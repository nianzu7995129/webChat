package org.wechat.utils;

import java.sql.Connection;
import java.sql.SQLException;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sql.DataSource;

public class ConnectionPool {
	private DataSource ds = null; // 数据源
	private static final String datasourceName = "sql2008";

	public Connection getConnection() throws SQLException {
		Connection con = null;
		if (ds == null) {
			init(datasourceName);
		}
		if (ds != null) {
			con = ds.getConnection();
		}
		return con;
	}

	private void init(String dsName) {
		String jndiName = "java:comp/env/" + dsName;
		try {
			Context ctx = new InitialContext();
			ds = (DataSource) ctx.lookup(jndiName); // 查找数据源
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
