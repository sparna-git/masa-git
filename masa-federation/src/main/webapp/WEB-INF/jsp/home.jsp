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
<link href="resources/css/style.css" rel="stylesheet" />
<link href="resources/bootstrap/css/bootstrap.css" rel="stylesheet" />

<link href="resources/css/jasny-bootstrap.min.css" rel="stylesheet" />
<script src="resources/js/jquery-1.11.3.js"></script>
<script src="resources/bootstrap/js/popper.min.js"></script>
<script src="resources/bootstrap/js/bootstrap.js"></script>
<script src="resources/js/jasny-bootstrap.min.js"></script>
<script defer
	src="https://use.fontawesome.com/releases/v5.0.6/js/all.js"></script>
<link rel="icon" type="image/png" href="resources/favicon.png" />
<link
	href='http://cdn.jsdelivr.net/g/yasqe@2.2(yasqe.min.css),yasr@2.4(yasr.min.css)'
	rel='stylesheet' type='text/css' />
<style type="text/css">
.card {
	
}
</style>
</head>
<body class="with-background">
	<jsp:include page="header.jsp"></jsp:include>
	<br>
	<br><br>
	<br>
	<div class="form-group" style="width: 20%; position: fixed; float: left;">
		<nav>
			<div class="nav nav-tabs" id="nav-tab" role="tablist">
				<a class="nav-item nav-link " id="nav-source-tab" data-toggle="tab"
					href="#nav-source" role="tab" aria-controls="nav-source"
					aria-selected="true">Sources</a> <a
					class="nav-item nav-link active" id="nav-query-tab"
					data-toggle="tab" href="#nav-query" role="tab"
					aria-controls="nav-query" aria-selected="false">Queries</a>
			</div>
		</nav>
		<div class="tab-content" id="nav-tabContent">
			<div class="tab-pane fade show" id="nav-source" role="tabpanel"
				aria-labelledby="nav-source-tab">
				<c:forEach items="${data.federationSources}" var="source">
					
					<div class="card"
						style="width: 90%; margin-top: 10px; margin-bottom: 10px;">
						<div class="card-body">
							<a  href="#">${source.sourceIri}</a>
						</div>
					</div>


				</c:forEach>
				</div>
			<div class="tab-pane fade show active" id="nav-query" role="tabpanel"
				aria-labelledby="nav-query-tab">

				<c:forEach items="${data.queries}" var="query">
					
					<div class="card"
						style="width: 90%; margin-top: 10px; margin-bottom: 10px;">
						<div class="card-body">
							<a onclick="use('${query.sprqlQuery}')" href="#">${query.title}</a>
						</div>
					</div>


				</c:forEach>

			</div>

		</div>
	
	</div>
	<div class="form-group" style="width: 70%; margin-left:20%;">
		
		<textarea id="yasqe"></textarea>
		<br> <br>

		<div id="yasr"></div>
	</div>
	<script src='http://cdn.jsdelivr.net/yasr/2.4/yasr.bundled.min.js'></script>
	<script src='http://cdn.jsdelivr.net/yasqe/2.2/yasqe.bundled.min.js'></script>

	<script type="text/javascript">
		$("#query").click(function() {
			$('#collapseExample').toggle();
		});
		$("#nav-source-tab").click(function(){
			$("#nav-query-tab").removeClass("active");
		    $("#nav-source-tab").addClass("active");
		});
		
		$("#nav-query-tab").click(function(){
			$("#nav-source-tab").removeClass("active");
		    $("#nav-query-tab").addClass("active");
		});
		
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

		function use(query) {

			console.log(query);

			yasqe.setValue(query);
		}
	</script>
</body>
</html>