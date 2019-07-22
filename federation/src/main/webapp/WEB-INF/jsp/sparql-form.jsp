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
<title><fmt:message key="window.app" /> | <fmt:message key="sparql.window.title" /></title>
<meta charset="utf-8">
<meta name="viewport" content="width=device-width, initial-scale=1">
<meta name="description" content="">
<meta name="author" content="">

<!-- Font Awesome -->
<link rel="stylesheet" href="<c:url value="/resources/fa/css/all.min.css" />">

<!-- Bootstrap + Material Design Bootstrap -->
<link rel="stylesheet" href="<c:url value="/resources/MDB-Free/css/bootstrap.min.css" />" />
<link rel="stylesheet" href="<c:url value="/resources/MDB-Free/css/mdb.min.css" />">

<!-- App-specific CSS -->
<link rel="stylesheet" href="<c:url value="/resources/css/openarchaeo-federation.css" />" />

<!-- Vis.js -->
<link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/vis/4.21.0/vis.min.css">

<!-- favicon, if any -->
<link rel="icon" type="image/png" href="resources/favicon.png" />


<link href='http://cdn.jsdelivr.net/npm/yasgui-yasqe@2.11.22/dist/yasqe.min.css' rel='stylesheet' type='text/css'/>
<link href='http://cdn.jsdelivr.net/npm/yasgui-yasr@2.12.19/dist/yasr.min.css' rel='stylesheet' type='text/css'/>


<style>
  .yasqe .CodeMirror { height: 420px; }
</style>

</head>
<body class="with-background">

	<jsp:include page="navbar.jsp">
		<jsp:param name="active" value="sparql"/>
	</jsp:include>
	
	<div  class="container-fluid" id="main-container">

		<div class="row justify-content-center">

			<div class="col-sm-3">
			
				<div class="card left-pane-card">
				  <div class="card-body">
				    <h4 class="card-title"><i class="fal fa-caret-square-right"></i>&nbsp;<fmt:message key="sparql.queries" /></h4>
				    <div class="card-text">
				    	<ul class="fa-ul">
				    		<c:forEach items="${data.queries}" var="query">
								<li><i class="fa-li fal fa-angle-right"></i><a onclick="setQuery('${query.sparqlQueryForJavascript}')" href="#">${query.titles[sessionScope['fr.humanum.openarchaeo.SessionData'].userLocale.language]}</a></li>
							</c:forEach>
				    	</ul>
				    </div>
				  </div>
				</div>
				
				<div class="card left-pane-card">
				  <div class="card-body">
				    <h4 class="card-title"><i class="fal fa-database"></i>&nbsp;<fmt:message key="sparql.sources" /></h4>
				    <div class="card-text">
				    	<ul class="fa-ul">
				    		<c:forEach items="${data.federationSources}" var="source">
								<li><i class="fa-li fal fa-angle-right"></i>${source.getTitle("fr")}&nbsp;<small><em>(${source.sourceIri})</em></small></li>
								<ul class="fa-ul">
									<li><i class="fa-li fal fa-angle-right"></i><small>Endpoint : ${source.endpoint}</small></li>
									<c:if test="${ source.defaultGraph.isPresent() }"><li><i class="fa-li fal fa-angle-right"></i><small>Default graph : ${source.defaultGraph.get() }</small></li></c:if>
								</ul>
							</c:forEach>
				    	</ul>
				    </div>
				  </div>
				</div>
			</div><!-- / left column -->
			
			<div class="col-sm-7">
				<textarea id="yasqe"></textarea>
				<br>
				<br>
		
				<div id="yasr"></div>
			</div><!-- / column yasqe+yasr -->

		
		</div><!-- /.row -->
		
	</div>
	
	<jsp:include page="footer.jsp" />
	
	<script src="<c:url value="/resources/MDB-Free/js/jquery-3.1.1.min.js" />"></script>
	<script src="<c:url value="/resources/MDB-Free/js/popper.min.js" />"></script>
	<script src="<c:url value="/resources/MDB-Free/js/bootstrap.min.js" />"></script>
	
	<script src="<c:url value="/resources/js/moment.min.js" />"></script>
	<script src="https://cdnjs.cloudflare.com/ajax/libs/vis/4.21.0/vis.min.js"></script>

	<script src='https://cdn.jsdelivr.net/npm/yasgui-yasr@2.12.19/dist/yasr.bundled.min.js'></script>
	<script src='https://cdn.jsdelivr.net/npm/yasgui-yasqe@2.11.22/dist/yasqe.bundled.min.js'></script>
	<!--  defines timeline plugin -->
	<script src='<c:url value="/resources/js/timeline.js" />'></script>
	

	<script type="text/javascript">

		
	
		var yasqe = YASQE.fromTextArea(document.getElementById("yasqe"), {
			sparql : {
				showQueryButton : true,
				endpoint : "<c:url value="/sparql" />"
			}
		});
		
		YASR.registerOutput("timeline",timelinePlugin);
		var yasr = YASR(document.getElementById("yasr"), {
			//this way, the URLs in the results are prettified using the defined prefixes in the query
			getUsedPrefixes : yasqe.getPrefixesFromQuery,
			outputPlugins: ["error", "boolean", "rawResponse", "table", "pivot", "leaflet", "timeline"]
		}); 
		
		// link yasqe and yasr
		yasqe.options.sparql.callbacks.complete = yasr.setResponse;

		function setQuery(query) {
			yasqe.setValue(query);
		}
		
	</script>
</body>
</html>