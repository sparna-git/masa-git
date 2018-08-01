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

<link href="resources/bootstrap/css/bootstrap.css" rel="stylesheet" />
<link href="resources/css/jasny-bootstrap.min.css" rel="stylesheet" />
<link href="resources/css/masa-federation.css" rel="stylesheet" />
<link href="https://cdnjs.cloudflare.com/ajax/libs/vis/4.21.0/vis.min.css" rel="stylesheet">
<script src="resources/js/jquery-1.11.3.js"></script>

<script src="https://cdnjs.cloudflare.com/ajax/libs/vis/4.21.0/vis.min.js"></script>
<script src="resources/bootstrap/js/popper.min.js"></script>
<script src="resources/bootstrap/js/bootstrap.js"></script>
<script src="resources/js/date.js"></script>
<script defer
	src="https://use.fontawesome.com/releases/v5.0.6/js/all.js"></script>
<link rel="icon" type="image/png" href="resources/favicon.png" />
<link
	href='http://cdn.jsdelivr.net/g/yasqe@2.2(yasqe.min.css),yasr@2.4(yasr.min.css)'
	rel='stylesheet' type='text/css' />


</head>
<body class="with-background">

	<jsp:include page="header.jsp"></jsp:include>
	
	<div class="form-group"
		style="width: 20%; position: fixed; float: left;">
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
							<c:forEach items="${source.labels}" var="label">
							<a href="#">${label.value} <em>(${label.key})</em></a><br>
							</c:forEach>
							<em>(${source.sourceIri})</em>
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
	
	<div  class="container-fluid" id="main-container" style="width: 70%; margin-left: 20%;">

		<textarea id="yasqe"></textarea>
		<br> <br>

		<div id="yasr"></div>
	</div>
	<script src='http://cdn.jsdelivr.net/yasr/2.4/yasr.bundled.min.js'></script>
	<script src='http://cdn.jsdelivr.net/yasqe/2.2/yasqe.bundled.min.js'></script>
	<script src='resources/js/timeline.js'></script>
	

	<script type="text/javascript">
		$("#query").click(function() {
			$('#collapseExample').toggle();
		});
		$("#nav-source-tab").click(function() {
			$("#nav-query-tab").removeClass("active");
			$("#nav-source-tab").addClass("active");
		});

		$("#nav-query-tab").click(function() {
			$("#nav-source-tab").removeClass("active");
			$("#nav-query-tab").addClass("active");
		});

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
			outputPlugins : ["Timeline"]
		}); 
		
		
		yasqe.options.sparql.callbacks.complete = yasr.setResponse;

		function use(query) {

			console.log(query);

			yasqe.setValue(query);
		}
		
		function add(yasr){
			var root = module.exports =function(yasr) {
				  var container = $("<div class='booleanResult'></div>");
				  var draw = function() {
				    container.empty().appendTo(yasr.resultsContainer);
				    var booleanVal = yasr.results.getBoolean();

				    var imgId = null;
				    var textVal = null;
				    if (booleanVal === true) {
				      imgId = "check";
				      textVal = "True";
				    } else if (booleanVal === false) {
				      imgId = "cross";
				      textVal = "False";
				    } else {
				      container.width("140");
				      textVal = "Could not find boolean value in response";
				    }

				    

				    $("<span></span>").text(textVal).appendTo(container);
				  };

				  var canHandleResults = function() {
				    return yasr.results.getBoolean && (yasr.results.getBoolean() === true || yasr.results.getBoolean() == false);
				  };

				  return {
				    name: null, //don't need to set this: we don't show it in the selection widget anyway, so don't need a human-friendly name
				    draw: draw,
				    hideFromSelection: true,
				    getPriority: 11,
				    canHandleResults: canHandleResults
				  };
				};

		}
		
	</script>
</body>
</html>