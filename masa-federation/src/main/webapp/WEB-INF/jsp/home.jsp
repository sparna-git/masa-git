<!DOCTYPE html>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<c:set var="data"
	value="${requestScope['fr.humanum.masa.federation.FederationData']}" />

<html>
<head>
<title></title>
<meta charset="utf-8">
<meta name="viewport" content="width=device-width, initial-scale=1">
<meta name="description" content="">
<meta name="author" content="">
<!-- Bootstrap core CSS -->

<link rel="stylesheet" href="resources/bootstrap/css/bootstrap.min.css" />
<link rel="stylesheet" href="resources/css/masa-federation.css" />
<link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/vis/4.21.0/vis.min.css">


<script defer
	src="https://use.fontawesome.com/releases/v5.0.6/js/all.js"></script>
<link rel="icon" type="image/png" href="resources/favicon.png" />

<link
	href='http://cdn.jsdelivr.net/g/yasqe@2.2(yasqe.min.css),yasr@2.4(yasr.min.css)'
	rel='stylesheet' type='text/css' />


</head>
<body class="with-background">

	<jsp:include page="header.jsp">
		<jsp:param name="active" value="home"/>
	</jsp:include>
	
	<div  class="container-fluid" id="main-container">

		<div class="row justify-content-center">

			<div class="col-sm-3">
				<div class="form-group">
					<nav>
						<div class="nav nav-tabs" id="nav-tab" role="tablist">
							<a
								class="nav-item nav-link active" id="nav-query-tab"
								data-toggle="tab" href="#nav-query" role="tab"
								aria-controls="nav-query" aria-selected="false">Queries</a>
							
							<a class="nav-item nav-link" id="nav-source-tab" data-toggle="tab"
								href="#nav-source" role="tab" aria-controls="nav-source"
								aria-selected="true">Sources</a>
						</div>
					</nav>
					<div class="tab-content" id="nav-tabContent">
						<div class="tab-pane fade show active" id="nav-query" role="tabpanel"
							aria-labelledby="nav-query-tab">
							<ul>
								<c:forEach items="${data.queries}" var="query">
									<li><a onclick="use('${query.sprqlQuery}')" href="#">${query.title}</a></li>
								</c:forEach>
							</ul>			
						</div><!-- / tab 1 : queries -->
						<div class="tab-pane fade" id="nav-source" role="tabpanel"
							aria-labelledby="nav-source-tab">
							<c:forEach items="${data.federationSources}" var="source">
								<div class="card"
									style="width: 90%; margin-top: 10px; margin-bottom: 10px;">
									<div class="card-body">
										<c:forEach items="${source.labels}" var="label">
											${label.value}@${label.key}<br>
										</c:forEach>
										<span class="sourceUri">${source.sourceIri}</span>
									</div>
								</div>
							</c:forEach>
						</div><!-- / tab 2 : sources -->
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
	
	<script src="resources/js/jquery-1.11.3.js"></script>
	<script src="resources/bootstrap/js/bootstrap.min.js"></script>
	
	<script src="https://cdnjs.cloudflare.com/ajax/libs/vis/4.21.0/vis.min.js"></script>

	<script src='https://cdn.jsdelivr.net/npm/yasgui-yasr@2.4.15/dist/yasr.bundled.min.js'></script>
	<script src='https://cdn.jsdelivr.net/npm/yasgui-yasqe@2.2.6/dist/yasqe.bundled.min.js'></script>
	<script src='resources/js/timeline.js'></script>
	

	<script type="text/javascript">

		var yasqe = YASQE.fromTextArea(document.getElementById("yasqe"), {
			sparql : {
				showQueryButton : true,
				endpoint : "http://fr.dbpedia.org/sparql"
			}
		});
		
		YASR.registerOutput("Timeline",timelinePlugin);
		
		 var yasr = YASR(document.getElementById("yasr"), {
			//this way, the URLs in the results are prettified using the defined prefixes in the query
			getUsedPrefixes : yasqe.getPrefixesFromQuery,
			outputPlugins : ["Timeline","error", "rawResponse", "table", "pivot", "leaflet"]
		}); 
		
		
		yasqe.options.sparql.callbacks.complete = yasr.setResponse;

		function use(query) {
			console.log(query);
			yasqe.setValue(query);
		}
		
	</script>
</body>
</html>