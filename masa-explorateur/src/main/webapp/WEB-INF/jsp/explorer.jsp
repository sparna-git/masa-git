<!DOCTYPE html>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<html>
<head>
<title>MASA Explorateur | Explorer</title>
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
<link rel="stylesheet" href="<c:url value="/resources/css/masa-explorateur.css" />" />

<!-- Vis.js -->
<link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/vis/4.21.0/vis.min.css">

<!-- Codemirror and SPARQL mode -->
<script src="<c:url value="/resources/codemirror/lib/codemirror.js" />"></script>
<link rel="stylesheet" href="<c:url value="/resources/codemirror/lib/codemirror.css" />">
<script src="<c:url value="/resources/codemirror/mode/sparql/sparql.js" />"></script>

<!-- favicon, if any -->
<link rel="icon" type="image/png" href="resources/favicon.png" />

<!-- YASR / YASQE -->
<link
	href='http://cdn.jsdelivr.net/g/yasqe@2.2(yasqe.min.css),yasr@2.4(yasr.min.css)'
	rel='stylesheet' type='text/css' />

</head>
<body>
	<jsp:include page="navbar.jsp">
		<jsp:param name="active" value="explorer"/>
	</jsp:include>

	<div class="container">
		<div class="row">
			<div class="col-sm">			
				<h1>Explorer</h1>
			
				<div class="form-group">
					<label for="sparql">SPARQL :</label>
					<textarea class="form-control" name="query" id="sparql">${query!=null?query:'
PREFIX skos: <http://www.w3.org/2004/02/skos/core#>
PREFIX dc: <http://purl.org/dc/elements/1.1/>
PREFIX dcterms: <http://purl.org/dc/terms/>

SELECT  ?this  
WHERE  {
  ?this a <http://exemple.com/type/Thing>.
}'}</textarea>
					<ul class="fa-ul">
			    		<c:forEach items="${queries}" var="query">
							<li><i class="fa-li fal fa-angle-right"></i><a onclick="setQuery('${query.sparqlQueryForJavascript}')" href="#">${query.title}</a></li>
						</c:forEach>
			    	</ul>
					<select class="form-control" style="width:20%;" id="view">
						<option value="select_Timeline">Timeline</option>
						<option value="select_gchart">Google Chart</option>
						<option value="select_rawResponse">Raw Response</option>
						<option value="select_pivot">Pivot Chart</option>
						<option value="select_table" selected>Table</option>
					</select>
					<input type="hidden" value="${sources}" name="sources" id="sources">
					<button type="button" id="run" class="btn btn-primary btn-lg">Run</button>
				</div>
				
				<div class="form-group" id="expand">
					<p><a href="#" id="label"><label for="expandedQuery">Masquer/afficher la requête étendue</label></a></p>
					<div id="expandedQueryContainer">
						<textarea class="form-control" rows="15" readonly="readonly" name="expandedQuery" id="expandedQuery"></textarea>
					</div>
				</div>
				<div class="form-group">
					<label for="yasr" id="labResult"></label>
					<div id="yasr"></div>
				</div>
			
			</div>
		</div>
	</div>

	<script src="<c:url value="/resources/MDB-Free/js/jquery-3.1.1.min.js" />"></script>
	<script src="<c:url value="/resources/MDB-Free/js/bootstrap.min.js" />"></script>
	<script src="https://cdn.jsdelivr.net/npm/yasgui-yasr@2.4.15/dist/yasr.bundled.min.js"></script>
	<script src="https://cdn.jsdelivr.net/npm/yasgui-yasqe@2.2.6/dist/yasqe.bundled.min.js"></script>		
	<script src="<c:url value="/resources/js/timeline.js" />"></script>

	<script type="text/javascript">
	
	 function changeView(val){
		 var sparql = document.getElementById("sparql").value;
		 
		 console.log(sparql);
		 
		 $.ajax({
	         url : 'expand',
	         type : 'POST', 
	         data : 'query=' + encodeURIComponent(sparql) , 
	         success: function(response) {
	        	$('#expand').show();
	        	$('#expandedQuery').text(response.expandQuery);
	        	console.log(response.expandQuery);
	        	
	        	$('.CodeMirror').remove();
	        	CodeMirror.fromTextArea(document.getElementById("expandedQuery"), {
	  			  lineNumbers: true,
	  			  mode: "sparql"
	  			});
	           
	   			var query= response.expandQuery;
	   			 $.ajax({
	   		         url : 'result',
	   		         type : 'POST', 
	   		         data : 'query=' + encodeURIComponent(query) , 
	   		         success: function(response) {
	   		        	$('#labResult').html('RESULTAT SPARQL :');
	   		        	$("#yasr").html('');
	   		        	var yasr = YASR(document.getElementById("yasr"));
	   		            yasr.setResponse(response);
	   		          }
	   		      });
	          }
	      });
		
		  // trigger click in YASR
		  $( "."+val ).trigger( "click" );
	 }
	
	
	 $( document ).ready(function() {
		 
		 /*
		 var editor = CodeMirror.fromTextArea(document.getElementById("expandedQuery"), {
			  lineNumbers: true,
			  mode: "sparql"
			});
		 */
		 
		 // hide query expansion
		 $('#expand').hide();
		 
		 $( "#run" ).click(function() {
			 changeView($( "#view option:selected" ).val());
		 });
		 
		 $( "#label" ).click(function() {
			 $('#expandedQueryContainer').toggle();
		 });
		 
		 YASR.registerOutput("Timeline",timelinePlugin);
		 
	 });
	 
	 function setQuery(query) {
		 $('#sparql').val(query);
	}

	  
	</script>
</body>
</html>