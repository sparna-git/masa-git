<!DOCTYPE html>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<html>
<head>
<title></title>
<meta charset="utf-8">
<meta name="viewport" content="width=device-width, initial-scale=1">
<meta name="description" content="">
<meta name="author" content="">
<!-- Bootstrap core CSS -->
<link href="resources/bootstrap/css/bootstrap.css" rel="stylesheet" />
<link href="resources/css/masa-federation.css" rel="stylesheet" />
<script src="resources/js/jquery-1.11.3.js"></script>
<script src="resources/bootstrap/js/popper.min.js"></script>
<script src="resources/bootstrap/js/bootstrap.js"></script>
<script defer
	src="https://use.fontawesome.com/releases/v5.0.6/js/all.js"></script>
<link rel="icon" type="image/png" href="resources/favicon.png" />
<link
	href='http://cdn.jsdelivr.net/g/yasqe@2.2(yasqe.min.css),yasr@2.4(yasr.min.css)'
	rel='stylesheet' type='text/css' />
</head>
<body class="with-background">
<jsp:include page="header.jsp"></jsp:include>
	
	<div class="container-fluid" id="main-container">
		<textarea id="yasqe"></textarea>
	
		<div id="yasr"></div>
	</div>

	<script src='http://cdn.jsdelivr.net/yasr/2.4/yasr.bundled.min.js'></script>
	<script src='http://cdn.jsdelivr.net/yasqe/2.2/yasqe.bundled.min.js'></script>
	<script type="text/javascript">
		
	 $( document ).ready(function() {
	     
	var yasqe = YASQE.fromTextArea(document.getElementById("yasqe"), {
		sparql : {
			showQueryButton : true,
			endpoint : "sparql"
		}
	});
	
	
	var yasr = YASR(document.getElementById("yasr"), {
		//this way, the URLs in the results are prettified using the defined prefixes in the query
		getUsedPrefixes : yasqe.getPrefixesFromQuery
	});


	
	yasqe.options.sparql.callbacks.complete = yasr.setResponse;
  
	 });
	  
	</script>
</body>
</html>