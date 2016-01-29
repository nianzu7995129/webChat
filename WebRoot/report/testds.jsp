<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@page import="javax.naming.Context"%>
<%@page import="javax.sql.DataSource"%>
<%@page import="java.sql.Connection"%>
<%@page import="javax.naming.InitialContext"%>

<%
	Context ctx = new InitialContext();
	DataSource ds = (DataSource) ctx.lookup("java:comp/env/sql2008"); // 查找数据源
	Connection con = ds.getConnection();
	out.println(ds+"||"+con);
%>
