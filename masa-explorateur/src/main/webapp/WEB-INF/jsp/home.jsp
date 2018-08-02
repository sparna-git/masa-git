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
<link href="resources/bootstrap/css/bootstrap.min.css" rel="stylesheet" />

<link href="resources/css/style.css" rel="stylesheet" />

<script defer
	src="https://use.fontawesome.com/releases/v5.0.6/js/all.js"></script>

</head>
<body class="with-background">
	<jsp:include page="header.jsp"></jsp:include>
	<br><br><br><br>
	<form action="sparql" method="post">
	
		<div class="flex-column " id="nav" style="margin:auto; text-align:center;">
		
		</div>
	
	</form>


	<script src="resources/js/jquery-1.11.3.js"></script>
	<script src="resources/bootstrap/js/bootstrap.min.js"></script>
	<script type="text/javascript">

	$.ajax({
       url : 'sources',
       type : 'GET', 
       success: function(response) {
    	   
    	   for(var i=0;i<response.length;i++){
    		   var label="";
    		   for(key in response[i].labels){
    			   label+=response[i].labels[key]+ ' <em>@'+key+'</em> ';
    			  
    		   }
    		   label=label.substring(0, label.length-1);
    		   $('#nav').append('<button class="btn btn-default" name="source" value="'+response[i].sourceString+'" style="width:20%; margin:auto;" type="submit">'+label+'<br>(<em class="source">'+response[i].sourceString+'</em>)</button><br><br>');     
    	   }    	   
       }      	
    });

	</script>
</body>
</html>