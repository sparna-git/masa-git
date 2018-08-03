<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<nav class="navbar navbar-expand-lg navbar-primary bg-primary">
	<a class="navbar-brand" href="#"></a>
	<button class="navbar-toggler" type="button" data-toggle="collapse"
		data-target="#navbarSupportedContent"
		aria-controls="navbarSupportedContent" aria-expanded="false"
		aria-label="Toggle navigation">
		<span class="navbar-toggler-icon"></span>
	</button>

	<div class="collapse navbar-collapse " id="navbarSupportedContent">
		<ul class="navbar-nav mr-auto">
			<li class="nav-item ${param.active == 'home' ? 'active' : ''}"><a class="nav-link text-white"
				href="home">Accueil <span class="sr-only">(current)</span></a></li>
				
			<li class="nav-item ${param.active == 'admin' ? 'active' : ''}"><a class="nav-link text-white"
				href="admin">Administration <span class="sr-only">(current)</span></a></li>

		</ul>

	</div>
	<ul class="nav navbar-nav navbar-right">
		<c:if test="${sessionScope.USER_DETAILS.name!=null}">
			<li role="presentation"><a style="color:white;"><i
					class="glyphicon glyphicon-user"></i>&nbsp;${sessionScope.USER_DETAILS.name}</a>&nbsp;&nbsp;&nbsp;
			</li>

			<li role="presentation"><a href="login?logout" style="cursor:pointer; text-decoration: none; color:white;"><i class="fa fa-power-off"></i>&nbsp;Déconnexion</a></li>
		</c:if>
	</ul>
</nav>
