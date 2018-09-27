<!DOCTYPE html>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<html>
<head>
<title>MASA Explorateur | Accueil</title>
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

<!-- favicon, if any -->
<link rel="icon" type="image/png" href="resources/favicon.png" />

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
</script>

</head>

<body>

	<jsp:include page="navbar.jsp">
		<jsp:param name="active" value="home"/>
	</jsp:include>

	<div class="container">
		<div class="row">
			<div class="col-sm">			
				<h1>Bienvenue dans l'explorateur MASA</h1>
			
			</div>
		</div>
	</div>
	
	
	<script src="<c:url value="/resources/MDB-Free/js/jquery-3.1.1.min.js" />"></script>
	<script src="<c:url value="/resources/MDB-Free/js/bootstrap.min.js" />"></script>	
	<script type="text/javascript">

	</script>
	
</body>
</html>