<!DOCTYPE html>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<c:set var="data" value="${requestScope['fr.humanum.masa.openarchaeo.federation.admin.AdminData']}" />

<html>
<head>
<title>openArchaeo Federation | Administration</title>

<meta charset="utf-8">
<meta name="viewport" content="width=device-width, initial-scale=1">

<!-- Font Awesome -->
<link rel="stylesheet" href="<c:url value="/resources/fa/css/all.min.css" />">

<!-- Bootstrap + Material Design Bootstrap -->
<link rel="stylesheet" href="<c:url value="/resources/MDB-Free/css/bootstrap.min.css" />" />
<link rel="stylesheet" href="<c:url value="/resources/MDB-Free/css/mdb.min.css" />">

<!-- easyautocomplete CSS -->
<link rel="stylesheet" href="<c:url value="/resources/css/easy-autocomplete.min.css" />"> 

<!-- App-specific CSS -->
<link rel="stylesheet" href="<c:url value="/resources/css/masa-federation.css" />" />

<!-- favicon, if any -->
<link rel="icon" type="image/png" href="resources/favicon.png" />




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
	<jsp:include page="navbar.jsp">
		<jsp:param name="active" value="admin"/>
	</jsp:include>

	<div  class="container-fluid" id="main-container">

		<div class="row justify-content-center">
			
			<c:if test="${not empty data.message}">
		      	<div class="col-sm-7">
			      	<div class="alert alert-${data.message.level} alert-dismissible fade show" role="alert">
					  ${data.message.text}
					  <button type="button" class="close" data-dismiss="alert" aria-label="Close">
					    <span aria-hidden="true">&times;</span>
					  </button>
					</div>
				</div>
			</c:if>
			
			<div class="col-sm-7">
			
				<h1>Administration</h1>
				<br />
				<div class="card adminCard">
				  <div class="card-body">
				    <h3 class="card-title">Les Sources</h3>
				    <div class="card-text">
				    	<ul class="fa-ul">
				    		<c:forEach items="${data.federationSources}" var="source">
								<li><i class="fa-li fal fa-angle-right"></i>${source.labels["fr"]}&nbsp;|&nbsp;<a href="<c:url value="/admin/sources/reIndex?source=${source.sourceIri}" />">Réindexer la source</a></li>
							</c:forEach>
				    	</ul>
				    </div>
				  </div>
				</div>
				
				<div class="card adminCard">
				  <div class="card-body">
				    <h3 class="card-title">Les Index</h3>
				    <div class="card-text">
				    	<select id="index" name="index">
				    		<c:forEach items="${data.indexIds}" var="idx">
								<option value="${idx}">${idx}</value>
							</c:forEach>
				    	</select>
				    	<input name="autocompleteTest" id="autocompleteTest" placeholder="Cherchez ici..." />
				    </div>
				  </div>
				</div>
				
				
			</div>
		
		</div><!-- /.row -->
		
	</div>
	 
	<script src="<c:url value="/resources/MDB-Free/js/jquery-3.1.1.min.js" />"></script>
	<script src="<c:url value="/resources/MDB-Free/js/bootstrap.min.js" />"></script>
	<script src="<c:url value="/resources/js/jquery.easy-autocomplete.min.js" />"></script>
	
	<script type="text/javascript">

		$( document ).ready(function() {
			 
			var options = {
				url: function(phrase) {
					return "<c:url value="/api/autocomplete" />?index=" + $( "#index option:selected" ).val() + "&key=" + phrase ;
				},

				getValue: "label",
				requestDelay: 400
			};
			
			$("#autocompleteTest").easyAutocomplete(options);
			 
		 });
		
	</script>
</body>	
</html>