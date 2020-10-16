<!DOCTYPE html>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<!-- setup the locale for the messages based on the language in the session -->
<fmt:setLocale value="${sessionScope['fr.humanum.openarchaeo.SessionData'].userLocale.language}"/>
<fmt:setBundle basename="fr.humanum.openarchaeo.federation.i18n.OpenArchaeo"/>

<c:set var="data" value="${requestScope['fr.humanum.openarchaeo.federation.FederationData']}" />

<html>
<head>
<title><fmt:message key="window.app" /> | <fmt:message key="api.window.title" /></title>
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
<link rel="stylesheet" href="<c:url value="/resources/css/openarchaeo-federation.css" />" />

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
				<h2>Sources API</h2>
				<ul class="fa-ul">
				<li><i class="fa-li fal fa-angle-right"></i><code>/api/sources</code></li>
					<li>Example : <i class="fa-li fal fa-angle-right"></i><a href="<c:url value="/api/sources" />">/api/sources</a></li>
		    	</ul>
				<h2>Autocomplete API</h2>
				<h3>domain / property / range variant</h3>
				<ul class="fa-ul">
					<li><i class="fa-li fal fa-angle-right"></i><code>/api/autocomplete?key=...&domain=...&property=...&range=...[&sources=...]</code></li>
					<li>Example : <i class="fa-li fal fa-angle-right"></i><a href="<c:url value="/api/autocomplete?key=tou&domain=http%3A%2F%2Fwww.openarchaeo.fr%2Fexplorateur%2Fonto%23Mobilier&property=http%3A%2F%2Fwww.openarchaeo.fr%2Fexplorateur%2Fonto%23trouve_dans&range=http%3A%2F%2Fwww.openarchaeo.fr%2Fexplorateur%2Fonto%23Site" />">/api/autocomplete?key=tou&domain=http%3A%2F%2Fwww.openarchaeo.fr%2Fexplorateur%2Fonto%23Mobilier&property=http%3A%2F%2Fwww.openarchaeo.fr%2Fexplorateur%2Fonto%23trouve_dans&range=http%3A%2F%2Fwww.openarchaeo.fr%2Fexplorateur%2Fonto%23Site</a></li>
		    	</ul>
				<h3>index variant</h3>
				<ul class="fa-ul">
					<li><i class="fa-li fal fa-angle-right"></i><code>/api/autocomplete?key=...&index=...[&sources=...]</code></li>
					<li>Example : <i class="fa-li fal fa-angle-right"></i><a href="<c:url value="/api/autocomplete?key=tou&index=httpwwwopenarchaeofrexplorateurontoMobilier_httpwwwopenarchaeofrexplorateurontotrouve_a_httpwwwopenarchaeofrexplorateurontoSite" />">/api/autocomple?key=tou&index=httpwwwopenarchaeofrexplorateurontoMobilier_httpwwwopenarchaeofrexplorateurontotrouve_a_httpwwwopenarchaeofrexplorateurontoSite</a></li>
		    	</ul>
		    	<h2>List API</h2>
		    	<h3>domain / property / range variant</h3>
				<ul class="fa-ul">
					<li><i class="fa-li fal fa-angle-right"></i><code>/api/list?domain=...&property=...&range=...&lang=...[&sources=...]</code></li>
					<li>Example : <i class="fa-li fal fa-angle-right"></i><a href="<c:url value="/api/list?domain=http%3A%2F%2Fwww.openarchaeo.fr%2Fexplorateur%2Fonto%23Mobilier&property=http%3A%2F%2Fwww.openarchaeo.fr%2Fexplorateur%2Fonto%23trouve_dans&range=http%3A%2F%2Fwww.openarchaeo.fr%2Fexplorateur%2Fonto%23Site&lang=fr" />">/api/list?domain=http%3A%2F%2Fwww.openarchaeo.fr%2Fexplorateur%2Fonto%23Mobilier&property=http%3A%2F%2Fwww.openarchaeo.fr%2Fexplorateur%2Fonto%23trouve_dans&range=http%3A%2F%2Fwww.openarchaeo.fr%2Fexplorateur%2Fonto%23Site&lang=fr</a></li>
		    	</ul>
				<h3>index variant</h3>
				<ul class="fa-ul">
					<li><i class="fa-li fal fa-angle-right"></i><code>/api/list?index=...&lang=...[&sources=...]</code></li>
					<li>Example : <i class="fa-li fal fa-angle-right"></i><a href="<c:url value="/api/list?index=httpwwwopenarchaeofrexplorateurontoMobilier_httpwwwopenarchaeofrexplorateurontotrouve_a_httpwwwopenarchaeofrexplorateurontoSite&lang=fr" />">/api/list?index=httpwwwopenarchaeofrexplorateurontoMobilier_httpwwwopenarchaeofrexplorateurontotrouve_a_httpwwwopenarchaeofrexplorateurontoSite&lang=fr</a></li>
					<li>Example on referential : <i class="fa-li fal fa-angle-right"></i><a href="<c:url value="/api/list?index=httpwwwopenarchaeofrexplorateurontoMobilier_httpwwwopenarchaeofrexplorateurontofait_en_httpwwwopenarchaeofrexplorateurontoType&lang=fr" />">/api/list?index=httpwwwopenarchaeofrexplorateurontoMobilier_httpwwwopenarchaeofrexplorateurontofait_en_httpwwwopenarchaeofrexplorateurontoType&lang=fr</a></li>
					<li>Example with sources : <i class="fa-li fal fa-angle-right"></i><a href="<c:url value="/api/list?index=httpwwwopenarchaeofrexplorateurontoMobilier_httpwwwopenarchaeofrexplorateurontotrouve_a_httpwwwopenarchaeofrexplorateurontoSite&sources=http%3A%2F%2Fopenarchaeo.huma-num.fr%2Ffederation%2Fsources%2Farsol" />">/api/list?index=httpwwwopenarchaeofrexplorateurontoMobilier_httpwwwopenarchaeofrexplorateurontotrouve_a_httpwwwopenarchaeofrexplorateurontoSite&sources=http%3A%2F%2Fopenarchaeo.huma-num.fr%2Ffederation%2Fsources%2Farsol&lang=fr</a></li>
		    	</ul>
		    	<h2>Periods API</h2>
				<ul class="fa-ul">
					<li><i class="fa-li fal fa-angle-right"></i><code>/api/periods?lang=...</code></li>
					<li>Example : <i class="fa-li fal fa-angle-right"></i><a href="<c:url value="/api/periods?lang=fr" />">/api/periods?lang=fr</a></li>
		    	</ul>
			</div>
		
		</div><!-- /.row -->
		
	</div>
	
	<jsp:include page="footer.jsp" />
	
	<script src="<c:url value="/resources/MDB-Free/js/jquery-3.1.1.min.js" />"></script>
	<script src="<c:url value="/resources/MDB-Free/js/popper.min.js" />"></script>
	<script src="<c:url value="/resources/MDB-Free/js/bootstrap.min.js" />"></script>
	

	<script type="text/javascript">

		
	</script>
</body>
</html>