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
<script type="text/javascript">

function submit(){
	var choix="";
	var n = $( ".source_to_use:checked" ).length;
	var text=n+" sources à intérroger. Confirmez-vous ?"
    if (confirm(text) == true) {
		$(".source_to_use:checked").each(function( index ) {
			choix += $( this ).val();
			choix += "+";
		});
		// on enleve le dernier caractere
		choix = choix.substr(0, choix.length-1);
		document.formsource.source.value =choix;
		document.formsource.submit();
   }
}

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
    		   $('#nav').append('<input type="checkbox" class="source_to_use"  value="'+response[i].sourceString+'" style="margin:auto;"> &nbsp;'+label+'&nbsp;(<em class="source">'+response[i].sourceString+'</em>)<br><br>');     
    	   }
    	   
       }
      	
    });

</script>
</head>
<body class="with-background">
	<jsp:include page="header.jsp"></jsp:include>
	<br><br><br><br>
	<div class="flex-column " style="margin:auto; text-align:center;">
	 <span id="nav" ></span>
	<form action="sparql" method="post" style="margin:auto;" name="formsource" onsubmit="return false">
		<input type="hidden" name="source" id="source" />
		<button class="btn btn-default" onclick="submit()" style="margin:auto;" type="submit">Valider</button>
	</form>
	</div>
	

</body>
</html>