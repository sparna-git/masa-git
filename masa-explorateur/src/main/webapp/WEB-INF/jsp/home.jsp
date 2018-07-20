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

<link rel="icon" type="image/png" href="resources/favicon.png" />
<!-- Bootstrap core CSS -->
<link href="resources/bootstrap/css/bootstrap.css" rel="stylesheet" />
<link
	href='http://cdn.jsdelivr.net/g/yasqe@2.2(yasqe.min.css),yasr@2.4(yasr.min.css)'
	rel='stylesheet' type='text/css' />
<link href="resources/css/style.css" rel="stylesheet" />
<script src="resources/js/jquery-1.11.3.js"></script>

<script src="resources/bootstrap/js/bootstrap.js"></script>
<script defer
	src="https://use.fontawesome.com/releases/v5.0.6/js/all.js"></script>

</head>
<body class="with-background">
<jsp:include page="header.jsp"></jsp:include>
<br><br>
	<form method="post" action="expand" style="margin:auto;">
	 <div class="form-group" style="width: 80%; margin:auto;">
	  <label for="sparql">SPARQL :</label>
	  <textarea class="form-control" rows="10" name="query" id="sparql">${query!=null?query:'PREFIX edm: <http://www.europeana.eu/schemas/edm/>
PREFIX ore: <http://www.openarchives.org/ore/terms/>
PREFIX skos: <http://www.w3.org/2004/02/skos/core#>
PREFIX dc: <http://purl.org/dc/elements/1.1/>
PREFIX dcterms: <http://purl.org/dc/terms/>

SELECT  ?this  
WHERE  {
?this a <http://exemple.com/type/Thing>.
}'}</textarea><br>
	   <button type="submit" class="btn btn-primary btn-lg">Run</button>
	</div> 
	 
	</form>
	<br>
	
	<c:if test="${queryExpand!=null}">
		
		<div class="form-group" style="width: 80%; margin:auto;">
		  <label for="collapseExample">REQUÊTE ETENDUE :</label>
		  <textarea class="form-control " rows="15"   readonly="readonly" name="query" id="collapseExample">${queryExpand}</textarea>
				<br>
			<label for="yasr">RESULT SPARQL :</label>
		<div id="yasr"></div>
	</div> 
	</c:if>
	<script src='http://cdn.jsdelivr.net/yasr/2.4/yasr.bundled.min.js'></script>
	<script src='http://cdn.jsdelivr.net/yasqe/2.2/yasqe.bundled.min.js'></script>
	<script type="text/javascript">
		
	 $( document ).ready(function() {
		
		 var yasr = YASR(document.getElementById("yasr"));
		 if('${getResult}'){
			 var query= '${queryExpandReplace}';
			 $.ajax({
		         url : 'result',
		         type : 'POST', 
		         data : 'query=' + query , 
		         success: function(response) {
		            yasr.setResponse(response);
		          }
		      });
		 }
		 yasr.draw();
	 });
	  
	</script>
</body>
</html>