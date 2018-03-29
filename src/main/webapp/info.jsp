<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>SAP Adapter</title>
</head>
<body>
<%
java.util.Properties props = new java.util.Properties();
java.net.URL url = Thread.currentThread().getContextClassLoader().getResource("jco.properties");
props.load(url.openStream());
%>
<table>
<tr><td>jco.client.ashost</td><td><%=props.getProperty("jco.client.ashost")%></td></tr>
<tr><td>jco.client.client</td><td><%=props.getProperty("jco.client.client")%></td></tr>
<tr><td>jco.client.user</td><td><%=props.getProperty("jco.client.user")%></td></tr>
<tr><td>jco.client.lang</td><td><%=props.getProperty("jco.client.lang")%></td></tr>
<tr><td>jco.client.sysnr</td><td><%=props.getProperty("jco.client.sysnr")%></td></tr>
<tr><td>jco.destination.pool_capacity</td><td><%=props.getProperty("jco.destination.pool_capacity")%></td></tr>
<tr><td>jco.destination.peak_limit</td><td><%=props.getProperty("jco.destination.peak_limit")%></td></tr>
<tr><td>jco.server.gwhost</td><td><%=props.getProperty("jco.server.gwhost")%></td></tr>
<tr><td>jco.server.gwserv</td><td><%=props.getProperty("jco.server.gwserv")%></td></tr>
<tr><td>jco.server.progid</td><td><%=props.getProperty("jco.server.progid")%></td></tr>
<tr><td>jco.server.repository_destination</td><td><%=props.getProperty("jco.server.repository_destination")%></td></tr>
<tr><td>jco.server.connection_count</td><td><%=props.getProperty("jco.server.connection_count")%></td></tr>
</table>
</body>
</html>