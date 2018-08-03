<!DOCTYPE html>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<c:set var="data"
	value="${requestScope['fr.humanum.masa.user.UserData']}" />

<html>
<head>
<meta charset="utf-8">
<meta name="viewport" content="width=device-width, initial-scale=1">
<link rel="stylesheet" href="resources/bootstrap/css/bootstrap.min.css">
<link rel="stylesheet" href="resources/css/style.css">
<link rel="stylesheet" href="resources/css/jquery-ui.min.css">
<title>Administration</title>

<style type="text/css">
	.msg {
	padding: 15px;
	margin-bottom: 20px;
	border: 1px solid transparent;
	border-radius: 4px;
	color: #31708f;
	background-color: #d9edf7;
	border-color: #bce8f1;
	width: 90%;
	margin: auto;
	margin-top: 50px;
}
</style>
</head>
<body>
	<jsp:include page="header.jsp">
		<jsp:param name="active" value="admin"/>
	</jsp:include>
	<br><br><br>
	<div class="container">
		<h5>Administration</h5>
		
		<c:if test="${data.msg!=null}">
			<div class="msg" style="text-align: center;">
			  <strong>${data.msg}</strong>
			</div>
		</c:if>
		<br>
		
		
	</div>
</body>
<script src="resources/js/jquery-1.11.3.js"></script>
<script src="resources/bootstrap/js/bootstrap.min.js"></script>
<script src="resources/js/jquery-ui.min.js"></script>
</html>