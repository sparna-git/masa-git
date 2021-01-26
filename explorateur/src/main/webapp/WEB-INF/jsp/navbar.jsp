<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<!-- setup the locale for the messages based on the language in the session -->
<fmt:setLocale value="${sessionScope['fr.humanum.openarchaeo.SessionData'].userLocale.language}"/>
<c:set var="data" value="${sessionScope['fr.humanum.openarchaeo.SessionData']}" />
<fmt:setBundle basename="fr.humanum.openarchaeo.explorateur.i18n.OpenArchaeo"/>

<!--Navbar-->
<nav class="navbar navbar-expand-lg navbar-light bg-blue" id="globalnav">

    <!-- Navbar brand -->
    <a class="navbar-brand" href="<c:url value="/home" />"><fmt:message key="navbar.brand" /></a>

    <!-- Collapse button -->
    <button class="navbar-toggler" type="button" data-toggle="collapse" data-target="#navbarSupportedContent" aria-controls="navbarSupportedContent"
        aria-expanded="false" aria-label="Toggle navigation"><span class="navbar-toggler-icon"></span></button>

    <!-- Collapsible content -->
    <div class="collapse navbar-collapse" id="navbarSupportedContent">

        <!-- Links -->
        <ul class="navbar-nav mr-auto">
            <li class="nav-item ${param.active == 'home' ? 'active' : ''}">
                <a class="nav-link" href="<c:url value="/home" />"><fmt:message key="navbar.welcome" />${param.active == 'home' ? '<span class="sr-only">(current)</span>' : ''}</a>
            </li>
            <li class="nav-item ${param.active == 'explorer' ? 'active' : ''}">
                <a class="nav-link" href="<c:url value="/sourcesSelect" />"><fmt:message key="navbar.explore" />${param.active == 'explorer' ? '<span class="sr-only">(current)</span>' : ''}</a>
            </li>
        </ul>
        <!-- Links -->
        
        <ul class="navbar-nav ml-auto nav-flex-icons">
          <li class="nav-item dropdown">
            <a class="nav-link dropdown-toggle" id="langMenuLink" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">
              <span style="font-size:1.25rem;"><i class="fal fa-globe-africa"></i></span>&nbsp;&nbsp;${data.userLocale.language}
            </a>
            <div class="dropdown-menu dropdown-menu-right dropdown-default" aria-labelledby="langMenuLink">
              <a class="dropdown-item" href="<c:url value="/home?lang=fr" />">fr</a>
              <a class="dropdown-item" href="<c:url value="/home?lang=en" />">en</a>
            </div>
          </li>
        </ul>

    </div>
    <!-- Collapsible content -->

</nav>
<!--/.Navbar-->
                