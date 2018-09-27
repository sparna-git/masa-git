<!DOCTYPE html>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<c:set var="data" value="${requestScope['fr.humanum.masa.federation.FederationData']}" />

<html>
<head>
<title>MASA federation API</title>
<meta charset="utf-8">
<meta name="viewport" content="width=device-width, initial-scale=1">
<meta name="description" content="">
<meta name="author" content="">
<!-- Bootstrap core CSS -->

<!-- Font Awesome -->
<link rel="stylesheet" href="<c:url value="/resources/fa/css/all.min.css" />">

<!-- Bootstrap + Material Design Bootstrap -->
<link rel="stylesheet" href="<c:url value="/resources/MDB-Free/css/bootstrap.min.css" />" />
<link rel="stylesheet" href="<c:url value="/resources/MDB-Free/css/mdb.min.css" />">

<!-- App-specific CSS -->
<link rel="stylesheet" href="<c:url value="/resources/css/masa-federation.css" />" />

<!-- favicon, if any -->
<link rel="icon" type="image/png" href="resources/favicon.png" />


</head>
<body class="with-background">

	<jsp:include page="navbar.jsp">
		<jsp:param name="active" value="api"/>
	</jsp:include>
	
	<div  class="container-fluid" id="main-container">

		<div class="row justify-content-center">
			
			<div class="col-sm-7">
				<h1>API documentation</h1>
				<p>
				<ul class="fa-ul">
					<li><i class="fa-li fal fa-angle-right"></i><a href="<c:url value="/api/sources" />">/api/sources</a></li>
		    	</ul>
				</p>
			</div>
		
		</div><!-- /.row -->
		
	</div>
	
	<script src="<c:url value="/resources/MDB-Free/js/jquery-3.1.1.min.js" />"></script>
	<script src="<c:url value="/resources/MDB-Free/js/bootstrap.min.js" />"></script>
	

	<script type="text/javascript">

		
	</script>
</body>
</html>