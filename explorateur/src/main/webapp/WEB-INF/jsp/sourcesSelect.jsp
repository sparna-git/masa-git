<!DOCTYPE html>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<html>
<head>
<title>OpenArchaeo Explorateur | Choix des sources</title>
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

<!-- favicon, if any -->
<link rel="icon" type="image/png" href="resources/favicon.png" />

</head>

<body>

	<jsp:include page="navbar.jsp">
		<jsp:param name="active" value="explorer"/>
	</jsp:include>

	<div class="container">
		<div class="row">
			<div class="col-sm">			
				<div class="card" id="sourcesSelectCard">
				  <div class="card-body">
				    <h1 class="card-title"><i class="fal fa-database"></i>&nbsp;&nbsp;Choisir les sources à interroger</h1>
				    <div class="card-text">
				    	<form action="explorer" method="get" style="margin:auto;" name="formsource">
				    		<div id="sources"></div>
							<input type="hidden" name="sources" id="sources" />
							<button class="btn btn-default" id="submitSources">Valider</button>
						</form>
				    </div>
				  </div>
				</div>
			
			</div>
		</div>
	</div>
	
	
	<script src="<c:url value="/resources/MDB-Free/js/jquery-3.1.1.min.js" />"></script>
	<script src="<c:url value="/resources/MDB-Free/js/bootstrap.min.js" />"></script>	
	<script type="text/javascript">
	
	var sourcesDefinition = ${sourcesDefinition};
	
	$( document ).ready(function() {
		
		var html = '<ul>';
		for(var i=0;i<sourcesDefinition.length;i++){
		   var source = sourcesDefinition[i];
 		   var label="";
 		   for(key in source.labels){
 			   label+=source.labels[key]+ ' <em>@'+key+'</em> ';
 		   }
 		   label=label.substring(0, label.length-1);
 		   html += '<li><input type="checkbox" class="aSource"  value="'+source.sourceString+'"> &nbsp;'+label+'&nbsp;(<em class="source">'+source.sourceString+'</em>)</li>';     
 	   }
		html += '</ul>';
		$('#sources').append(html);
		
		$( "#submitSources" ).click(function() {
			var sources="";

			$(".aSource:checked").each(function( index ) {
				sources += $( this ).val();
				sources += " ";
			});
			// on enleve le dernier caractere
			sources = sources.substr(0, sources.length-1);
			// set and submit
			console.log(sources)
			document.formsource.sources.value = sources;
			document.formsource.submit();
		 });
		 
	});
	</script>
	
</body>
</html>