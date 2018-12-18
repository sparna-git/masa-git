<!DOCTYPE html>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<!-- setup the locale for the messages based on the language in the session -->
<fmt:setLocale value="${sessionScope['fr.humanum.openarchaeo.SessionData'].userLocale.language}"/>
<fmt:setBundle basename="fr.humanum.openarchaeo.explorateur.i18n.OpenArchaeo"/>

<html>
<head>
<title><fmt:message key="window.app" /> | <fmt:message key="explore.window.title" /></title>
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
<link rel="stylesheet" href="<c:url value="/resources/css/openarchaeo-explorateur.css" />" />

<!-- Vis.js -->
<link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/vis/4.21.0/vis.min.css">

<!-- Codemirror and SPARQL mode -->
<script src="<c:url value="/resources/codemirror/lib/codemirror.js" />"></script>
<link rel="stylesheet" href="<c:url value="/resources/codemirror/lib/codemirror.css" />">
<script src="<c:url value="/resources/codemirror/mode/sparql/sparql.js" />"></script>

<!-- SimSemSearch -->
<link rel="stylesheet" href="<c:url value="/resources/css/nice-select.css" />">
<link rel="stylesheet" href="<c:url value="/resources/css/easy-autocomplete.min.css" />">
<link rel="stylesheet" href="<c:url value="/resources/css/simsemsearch.css" />">

<!-- favicon, if any -->
<link rel="icon" type="image/png" href="resources/favicon.png" />

<!-- YASR / YASQE -->
<!-- <link -->
<!-- 	href='http://cdn.jsdelivr.net/g/yasqe@2.2(yasqe.min.css),yasr@2.4(yasr.min.css)' -->
<!-- 	rel='stylesheet' type='text/css' /> -->
<link href='https://cdn.jsdelivr.net/npm/yasgui-yasr@2.12.19/dist/yasr.min.css' rel='stylesheet' type='text/css'/>


</head>
<body>
	<jsp:include page="navbar.jsp">
		<jsp:param name="active" value="explorer"/>
	</jsp:include>

	<div class="container-fluid">
		<div class="row justify-content-center">
			<div class="col-sm-10">			
				<h1><fmt:message key="explore.title" /></h1>
			
				<input type="hidden" value="${sources}" name="sources" id="sources">
				
				
				<div class="card" id="simsemsearch-container">
				  <div class="card-body">
				    <div class="card-text">				    	
						<div id="simsemsearch"></div>
						<div id="simsemsearch-control">
							<div class="row no-gutters justify-content-end">
								<div class="col-sm-6">
									<div class="float-right">
										<button type="button" id="run" class="btn btn-primary"><fmt:message key="explore.run" /></button>
										&nbsp;<fmt:message key="explore.andDisplayResultAs" />&nbsp;
									</div>
								</div>
								<div class="col-sm-2">
									<div class="align-middle">
										<select class="form-control form-control-lg" id="view">
											<option value="table" selected><fmt:message key="explore.output.table" /></option>
											<option value="leaflet"><fmt:message key="explore.output.leaflet" /></option>
											<option value="timeline"><fmt:message key="explore.output.timeline" /></option>
											<option value="gchart"><fmt:message key="explore.output.gchart" /></option>
											<option value="pivot"><fmt:message key="explore.output.pivot" /></option>
											<option value="rawResponse"><fmt:message key="explore.output.rawResponse" /></option>
										</select>
									</div>
								</div>
							</div>
						</div>
				    </div>
				  </div>
				</div>

				<input type="hidden" name="query" id="sparql" />
				<input type="hidden" name="rawExpandedSparql" id="rawExpandedSparql" />
				
				<div class="form-group">
					<label for="yasr" id="sparqlResultLabel"></label>					
					<div id="expandedQueryContainer">
						<textarea class="form-control" rows="15" readonly="readonly" name="expandedQuery" id="expandedQuery"></textarea>
					</div>
					<div id="yasr"></div>
				</div>
			
			</div>
		</div>
	</div>
	
	<jsp:include page="footer.jsp" />

	<!-- <script src="<c:url value="/resources/MDB-Free/js/jquery-3.1.1.min.js" />"></script> -->
	<script src="<c:url value="/resources/js/jquery-3.3.1.min.js" />"></script>
	<script src="https://cdn.jsdelivr.net/npm/yasgui-yasr@2.12.19/dist/yasr.bundled.min.js"></script>
	
	<script src="<c:url value="/resources/MDB-Free/js/popper.min.js" />"></script>
	<script src="<c:url value="/resources/MDB-Free/js/bootstrap.min.js" />"></script>
 	<script src="<c:url value="/resources/js/timeline.js" />"></script>
 	
 	<!-- simsemsearch -->
 	<script src="<c:url value="/resources/js/sparqljs-browser.js" />"></script>
 	<script src="<c:url value="/resources/js/jquery.nice-select.js" />"></script>
 	<script src="<c:url value="/resources/js/jquery.easy-autocomplete.min.js" />"></script>
 	<script src="<c:url value="/resources/js/simsemsearch.js" />"></script>

	<script type="text/javascript">
	
	 var yasr;
	
	 function execute(view){
		 var sparql = $('#sparql').val();
		 $('#sparqlResultLabel').html('');
		 $('#yasr').html('');
		 
		 $.ajax({
	         url : 'expand',
	         type : 'POST', 
	         data : 'query=' + encodeURIComponent(sparql) + '&sources=${sources}&view='+view , 
	         success: function(response) {
	        	document.getElementById("rawExpandedSparql").value = response.expandQuery;
	        	
	        	// remove old CodeMirror
	        	$('.CodeMirror').remove();
	        	// set the value of the textarea
	        	$('#expandedQuery').text(document.getElementById("rawExpandedSparql").value);
	        	// recreate CodeMirror
	        	var editor = CodeMirror.fromTextArea(document.getElementById("expandedQuery"), {
	  			  lineNumbers: true,
	  			   mode: "sparql"
	  			});
	        	
	           
	   			var query= response.expandQuery;
	   			 $.ajax({
	   		         // url : 'result',
	   		         url : 'sparql',
	   		         type : 'POST', 
	   		         data : 'query=' + encodeURIComponent(query) , 
	   		         success: function(response) {
	   		        	$('#sparqlResultLabel').html('<div class="alert alert-success" role="alert"><i class="fal fa-check"></i>&nbsp;<fmt:message key="explore.results.querySuccessful" /> - <small><a href="#" class="alert-link" id="toggleQueryButton"><fmt:message key="explore.results.viewSparql" /></a></small></div>');
	   		        	$( "#toggleQueryButton" ).click(function() {
		   					 $('#expandedQueryContainer').toggle();
		   					 // refresh CodeMirror after toggle for proper display
		   					 editor.refresh();
		   				});
	   		        	
	   		        	yasr = YASR(
	   		        			document.getElementById("yasr"),
	   		        			{ 
	   		        				"output": view,
	   		        				"drawOutputSelector": true,
									"persistency": { "prefix": false, "results": { "key": false }}
								},
								response
						);
	   			 	  },
	   		       	  error: function(response) {
	   		        	$('#sparqlResultLabel').html('<div class="alert alert-danger" role="alert"><i class="fal fa-exclamation-triangle"></i>&nbsp;<fmt:message key="explore.results.queryError" /></div>');
	   		          }
	   		      });
	          }
	      });
		
		  // trigger click in YASR
		  $( "."+view ).trigger( "click" );
	 }
	
	
	 $( document ).ready(function() {
		 
		 // hide query expansion
		 $('#expandedQueryContainer').hide();
		 
		 $( "#run" ).click(function() {
			 execute($( "#view option:selected" ).val());
		 });
		 
		 // YASR.plugins.table.defaults = { "mergeLabelsWithUris": true };
		 YASR.registerOutput("timeline",timelinePlugin);
// 		 yasr = YASR(document.getElementById("yasr"), 
// 			{
// 			 outputPlugins: ["error", "boolean", "rawResponse", "table", "pivot", "leaflet", "timeline"]
// 			}	 
// 		 );

		 $('#simsemsearch').SimSemSearchForm({
			pathSpecSearch: 'resources/config/spec-search.json',
			language: '${sessionScope['fr.humanum.openarchaeo.SessionData'].userLocale.language}',
			addDistinct: true,
			autocompleteUrl: function(domain, property, range, key) {
				// ici le code qui se charge de retourner les résultats de la recherche
				return '/federation/api/autocomplete?key='+key+'&domain='+encodeURIComponent(domain)+'&property='+encodeURIComponent(property)+'&range='+encodeURIComponent(range) ;
			},
			listUrl: function(domain, property, range) {
				// ici le code qui se charge de retourner les résultats de la recherche
				return '/federation/api/list?domain='+encodeURIComponent(domain)+'&property='+encodeURIComponent(property)+'&range='+encodeURIComponent(range) ;
			},
			datesUrl: function(domain, property, range, key) {
				// ici le code qui se charge de retourner les résultats de la recherche
				return '/federation/api/periods?lang=${sessionScope['fr.humanum.openarchaeo.SessionData'].userLocale.language}' ;
			},
			onQueryUpdated: function(queryString) {
				// ici on récupère la requete Sparql grace au premier parametre de la fonction
				console.log(queryString) ;
				// $('#sparql').val(queryString.replace(/&/g, "&amp;").replace(/</g, "&lt;").replace(/>/g, "&gt;"));
				$('#sparql').val(queryString);
			}
		  });
		 
	 });
	  
	</script>
</body>
</html>