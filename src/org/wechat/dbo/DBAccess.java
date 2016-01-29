package org.wechat.dbo;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import org.wechat.dbo.vo.param.InParam;
import org.wechat.dbo.vo.param.OutParam;
import org.wechat.utils.ConnectionPool;

public class DBAccess {
	private String driver;
	private String url;
	private String username;
	private String password;
	private Connection conn;
	private boolean isDebugMode = false;

	private Connection getConnection() throws Exception {
		if (isDebugMode) {
			Class.forName(driver);
			conn = DriverManager.getConnection(url, username, password);
		} else {
			ConnectionPool cp = new ConnectionPool();
			conn = cp.getConnection();
		}

		return conn;
	}

	public DBAccess(String driver, String url, String username, String password) throws Exception {
		this.driver = driver;
		this.url = url;
		this.username = username;
		this.password = password;
		conn = getConnection();
	}

	public DBAccess() throws Exception {
		this.driver = "net.sourceforge.jtds.jdbc.Driver";
		this.url = DBConst.dburl_WewaySoftFZ2;
		this.username = "sa";
		this.password = "691107";
		conn = getConnection();
	}

	public DBAccess(boolean isDebug) throws Exception {
		isDebugMode = isDebug;
		if (isDebugMode) {
			this.driver = "net.sourceforge.jtds.jdbc.Driver";
			this.url = DBConst.dburl_WewaySoftFZ2;
			this.username = "sa";
			this.password = "691107";
		}
		conn = getConnection();
	}

	/** 按顺序设置输入参数，无输出参数 */
	public ResultSet executeQuery(String sql, List<Object> inParams) throws SQLException {
		ResultSet rs = null;
		if (conn != null) {
			PreparedStatement ps = conn.prepareStatement(sql);
			int index = 1;
			if (inParams != null) {
				for (Object tmpValue : inParams) {
					if (tmpValue instanceof Integer) {
						ps.setInt(index, ((Integer) tmpValue).intValue());
					} else if (tmpValue instanceof String) {
						ps.setString(index, (String) tmpValue);
					}
					index++;
				}
			}
			rs = ps.executeQuery();
		}
		return rs;
	}

	/** 设置输入参数，输出参数 ,参数对象中带有index */
	public List<Object> executeProcedure(String sql, List<InParam> inParams, List<OutParam> outParams, int resultIndex, boolean withResult) throws SQLException {
		List<Object> resultList = new java.util.ArrayList<Object>();
		ResultSet rs = null;
		if (conn != null) {
			// 创建存储过程的对象
			CallableStatement c = conn.prepareCall(sql);
			if (inParams != null) {
				for (InParam inParam : inParams) {
					int index = inParam.getIndex();
					Object value = inParam.getValue();
					if (value instanceof Integer) {
						c.setInt(index, ((Integer) value).intValue());
					} else if (value instanceof String) {
						c.setString(index, (String) value);
					}
				}
			}
			if (outParams != null) {
				for (OutParam outParam : outParams) {
					int index = outParam.getIndex();
					int dataType = outParam.getDataType();
					c.registerOutParameter(index, dataType);
				}
			}
			c.execute();
			if (withResult) {
				resultList.add(null);//先占位，第一个为结果集
				c.getMoreResults();
			}
			for (OutParam outParam : outParams) {
				int index = outParam.getIndex();
				int dataType = outParam.getDataType();
				if (index == resultIndex) {
					if (dataType == java.sql.Types.VARCHAR) {
						String result = c.getString(index);
						resultList.add(result);
					}else if (dataType == java.sql.Types.INTEGER) {
						int result = c.getInt(index);
						resultList.add(result);
					}
				}
			}
			if (withResult) {
				rs = c.executeQuery();
				resultList.set(0,rs);
			}
		}
		return resultList;
	}

	public int executeUpdate(String sql, List<InParam> inParams) throws SQLException {
		int result = 0;
		if (conn != null) {
			PreparedStatement ps = conn.prepareStatement(sql);
			if (inParams != null) {
				for (InParam inParam : inParams) {
					int index = inParam.getIndex();
					Object value = inParam.getValue();
					if (value instanceof Integer) {
						ps.setInt(index, ((Integer) value).intValue());
					} else if (value instanceof String) {
						ps.setString(index, (String) value);
					}
				}
			}
			result = ps.executeUpdate();
		}
		return result;
	}

	public PreparedStatement executeBatch(String sql, List<List<InParam>> inParamsList) throws SQLException {
		if (conn != null) {
			PreparedStatement ps = conn.prepareStatement(sql);
			if (inParamsList != null) {
				for (List<InParam> inParams : inParamsList) {
					if (inParams != null) {
						for (InParam inParam : inParams) {
							int index = inParam.getIndex();
							Object value = inParam.getValue();
							if (value instanceof Integer) {
								ps.setInt(index, ((Integer) value).intValue());
							} else if (value instanceof String) {
								ps.setString(index, (String) value);
							}
						}
						ps.addBatch();
					}
				}
				ps.executeBatch();
			}
			return ps;
		}
		return null;
	}

	public void startTransaction() throws SQLException {
		conn.setAutoCommit(false);
	}

	public void endTransaction() throws SQLException {
		conn.setAutoCommit(true);
	}

	public void commit() throws SQLException {
		conn.commit();
	}

	public void rollback() {
		try {
			if (!conn.getAutoCommit()) {
				conn.rollback();
			}
		} catch (SQLException se) {
			System.out.println("[DBAccess] - rollback - 回滚事务出错");
			se.printStackTrace();
		}
	}

	public void finalize() {
		if (this.conn != null) {
			try {
				if (!this.conn.isClosed()) {
					this.conn.close();
				}
			} catch (SQLException se) {
				System.out.println("[DBAccess] - 关闭Connection时发生异常！");
				se.printStackTrace();
			} finally {
				this.conn = null;
			}
		}
	}

	public void close() {
		if (this.conn != null) {
			try {
				if (!this.conn.isClosed()) {
					this.conn.close();
				}
			} catch (SQLException se) {
				System.out.println("[DBAccess] - 关闭Connection时发生异常！");
				se.printStackTrace();
			} finally {
				this.conn = null;
			}
		}
	}

	public static void main(String args[]) {
		String driverName = "net.sourceforge.jtds.jdbc.Driver"; // 加载JDBC驱动
		// 加载驱动
		int result = -1;
		try {
			DriverManager.registerDriver(new net.sourceforge.jtds.jdbc.Driver());
			// 获得连接
			Class.forName(driverName);
			// jdbc:jtds:sqlserver://115.28.43.22:1433/ceshi
			// jdbc:jtds:sqlserver://123.57.209.53:1433/WewaySoftFZ2
			Connection conn = DriverManager.getConnection("jdbc:jtds:sqlserver://115.28.43.22:1433/ceshi", "sa", "691107");
			conn.setAutoCommit(false);
			//正确的sql
			//String sql = "declare @p1 dbo.tvpdlyndxorder insert into @p1 values(0,N'1543140627735185292 ',N'2015-12-27 ',N'XSDD-2015-12-27-0005 ',8,N'0000200001 ',N'0000100001 ',N'',N'00003 ',N'00001 ',N'',N'00002 ',N'0',N'',0,N'',N'00002 ',N'2015-12-27 ',N'0',0,0,0,N'100',N'',N'0',0,N'摘要 ',N'附加说明 ',2,N'',N'',N'',N'',N'',0,N'0',N'',0,N'23 ',N'0',N'0',N'0',0,0,N'',N'0',N'运输方式 ',N'送货地址 ',N'联系人 ',N'联系电话 ','2015-12-27 18:00:00 ') declare @p2 dbo.tvpBakDlyOrder insert into @p2 values(0,0,N'1543140627735185292 ',8,N'0000100001 ',N'0000100001 ',N'0000200001 ',N'00003 ',N'00001 ',N'00002 ',N'00008 ',N'11 ',N'100.00',N'136.80 ',0,N'100',N'0',N'0.00',N'136.80 ',N'136.80 ',N'1504.80 ',N'2015-12-27 ',0,N'0.00',N'136.80 ',N'0.00',N'1504.80 ',N'0',0,0,N'0',0,15 ,0,N'0.00',N'1504.80 ',N'',N'',N'0',N'0',N'',0,0,N'',N'',N'',N'',N'',N'',1,N'1',0,1 ,N'0',N'0',N'136.80 ',N'0') declare @p3 dbo.tvpBakDlyOrderDetail insert into @p3 values(0,N'1543140627735185292 ',1 ,15 ,N'11 ',N'0',N'0.00',N'0',N'0.00',N'1504.80 ',N'1504.80 ',N'0.00',N'1504.80 ',1 ,N'0',N'0',N'0') exec SaveOrderBill @tvpDlyNdxOrder=@p1,@tvpBakdlyOrder=@p2,@tvpBakDlyOrderDetail=@p3,@Operator=N'00002 '";
			String sql = "declare @p1 dbo.tvpdlyndxorder insert into @p1 values(0,N'1543140627735185292 ',N'2015-12-27 ',N'XSDD-2015-12-27-0005 ',8,N'0000200001 ',N'0000100001 ',N'',N'00003 ',N'00001 ',N'',N'00002 ',N'0',N'',0,N'',N'00002 ',N'2015-12-27 ',N'0',0,0,0,N'100',N'',N'0',0,N'摘要 ',N'附加说明 ',2,N'',N'',N'',N'',N'',0,N'0',N'',0,N'23 ',N'0',N'0',N'0',0,0,N'',N'0',N'运输方式 ',N'送货地址 ',N'联系人 ',N'联系电话 ','2015-12-27 18:00:00 ') declare @p2 dbo.tvpBakDlyOrder insert into @p2 values(0,0,N'1543703579209219202 ',7,N'0000100001 ',N'0000100001 ',N'0000100001 ',N'00004 ',N'00001 ',N'00003 ',N'00008 ',N'11 ',N'100.00',N'12.00 ',0,N'100',N'0',N'0.00',N'136.80 ',N'12 ',N'132.00 ',N'2015-12-28 ',0,N'0.00',N'12.00 ',N'0.00',N'132.00 ',N'0',0,0,N'0',0,15 ,0,N'0.00',N'132.00 ',N'',N'',N'0',N'0',N'',0,0,N'',N'',N'',N'',N'',N'',4,N'1',0,1 ,N'0',N'0',N'12.00 ',N'0') declare @p3 dbo.tvpBakDlyOrderDetail insert into @p3 values(0,N'1543703579209219202 ',1 ,15 ,N'11 ',N'0',N'0.00',N'0',N'0.00',N'1504.80 ',N'1504.80 ',N'0.00',N'1504.80 ',1 ,N'0',N'0',N'0') exec SaveOrderBill @tvpDlyNdxOrder=@p1,@tvpBakdlyOrder=@p2,@tvpBakDlyOrderDetail=@p3,@Operator=N'00002 '";
			PreparedStatement psm = conn.prepareStatement(sql);
			 psm.executeUpdate();
			int updateCount = psm.getUpdateCount();
			boolean hasMoreResult = psm.getMoreResults();
			ResultSet rs = psm.getResultSet();
			//System.out.println("executeResult>>>"+executeResult);
			System.out.println("updateCount>>>"+updateCount);
			System.out.println("hasMoreResult>>>"+hasMoreResult);
			if(rs!=null && rs.next()){
				System.out.println("rs>>>"+rs.getString("ErrorType")+"||"+rs.getString("ErrorMessage"));
				conn.rollback();
				conn.setAutoCommit(true);
			}else{
				conn.commit();
				conn.setAutoCommit(true);
				conn.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("updateResult>>"+result);
	}

	public Connection getConn() {
		return conn;
	}

	public void setConn(Connection conn) {
		this.conn = conn;
	}
}
