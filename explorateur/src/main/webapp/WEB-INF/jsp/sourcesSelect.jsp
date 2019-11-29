<!DOCTYPE html>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<!-- setup the locale for the messages based on the language in the session -->
<fmt:setLocale value="${sessionScope['fr.humanum.openarchaeo.SessionData'].userLocale.language}"/>
<fmt:setBundle basename="fr.humanum.openarchaeo.explorateur.i18n.OpenArchaeo"/>

<c:set var="data" value="${requestScope['sourcesDefinition']}" />
<c:set var="lang" value="${sessionScope['fr.humanum.openarchaeo.SessionData'].userLocale.language}" />

<html>
<head>
<title><fmt:message key="window.app" /> | <fmt:message key="sources.window.title" /></title>
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
				    <h1 class="card-title"><i class="fal fa-database"></i>&nbsp;&nbsp;<fmt:message key="sources.title" /></h1>
				    <div class="card-text">				    	
				    		<div id="sourcesList">
								<div class="row">
					    			<c:forEach items="${data}" var="source" varStatus="i">
					    				<div class="col-6">
						    				<div class="card sourceCard">
						    				  <div class="card-header sourceCardHeader" id="heading${i.index}">
						    				  	<div class="row">
						    				  		<div class="col-sm-10">
							    				  		<h4 class="card-title"><input type="checkbox" class="selectSourceCheckbox" data-uri="${source.sourceString}" data-endpoint="${source.endpoint}" />&nbsp;${source.getTitle(lang)}</h4>
							    				  		<p><em>${source.getShortDesc(lang)}</em></p>
						    				  		</div>
						    				  	</div>
						    				  </div> <!-- / .card-header -->
											  <div class="card-body">
									    		<ul class="fa-ul">
					    				  			<li><i class="fa-li fal fa-angle-right"></i><fmt:message key="sources.desc.spatial" /> : ${source.displayValueList(source.getSpatial(lang))}</li>
					    				  			<li><i class="fa-li fal fa-angle-right"></i><fmt:message key="sources.desc.temporal" /> : <fmt:message key="sources.desc.temporal.from" /> ${source.getStartDate(lang)} <fmt:message key="sources.desc.temporal.to" /> ${source.getEndDate(lang)}</li>
					    				  			<li><i class="fa-li fal fa-angle-right"></i><fmt:message key="sources.desc.keywords" /> : ${source.displayValueList(source.getKeywords(lang))}</li>
					    				  		</ul>
					    				  		<hr />
										    	<ul class="fa-ul">
											    	<c:forEach var="entry" items="${source.getDescriptionInLang(lang)}">
											    	  	<li>
											    	  		<i class="fa-li fal fa-angle-right"></i><fmt:message key="sources.desc.${fn:replace(entry.key, ':', '_')}" />&nbsp;:&nbsp;
											    	  		${source.displayValueList(entry.value)}
											    	  	</li>
													</c:forEach>
												</ul>
											  </div> <!-- / .card-body -->
											</div> 
										</div>
									</c:forEach>
								</div>
				    		</div>
				    		
				    		<form action="explorer" method="get" style="margin:auto;" name="formsource" id="formsource">
				    			<div id="hiddenSources">
				    				<!-- here be inserted hidden inputs -->
				    			</div>
								<button class="btn btn-default" id="submitSources"><fmt:message key="sources.validate" /></button>
							</form>
				    </div>
				  </div>
				</div>
			
			</div>
		</div>
	</div>
	
	<jsp:include page="footer.jsp" />
	
	
	<script src="<c:url value="/resources/MDB-Free/js/jquery-3.1.1.min.js" />"></script>
	<script src="<c:url value="/resources/MDB-Free/js/popper.min.js" />"></script>
	<script src="<c:url value="/resources/MDB-Free/js/bootstrap.min.js" />"></script>	
	<script type="text/javascript">
		$( document ).ready(function() {
			
			$('#submitSources').attr('disabled', true);
    		
			$(".selectSourceCheckbox").click(function () {
				if($(this).prop('checked')) {
    				// set selected class on this one
    				$(this).closest(".sourceCardHeader").addClass("sourceCardHeader-selected");
				} else {
					$(this).closest(".sourceCardHeader").removeClass("sourceCardHeader-selected");
				}
				
				// recompute value of sources
				$("#hiddenSources").html("")
				$(".selectSourceCheckbox:checked").each(function( index ) { 
					$("#hiddenSources").append("<input type='hidden' name='source' value="+$(this).attr('data-uri')+" />");
				});
				
    			// enable submit button if needed
    			if($(".selectSourceCheckbox:checked").length > 0) {
	    			$('#submitSources').attr('disabled', false);
    			} else {
    				$('#submitSources').attr('disabled', true);
    			}
    			
    			// enable warning on federation if needed
    			var endpoints = $('.selectSourceCheckbox:checked').map(function() {
    				return $(this).attr('data-endpoint');
				}).get();
    			// keep unique values
    			endpoints = jQuery.uniqueSort( endpoints );
    			if(endpoints.length > 1) {
    				console.log("Warning on federation !");
    			}
    		});	
    		
    		$("#submitSources").click(function () {
    		    if($("#sources").val().length != 0) {
    		    	$("#formsource").submit();
    		    }
    		});
	 	});

	</script>
	
</body>
</html>