<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<!--Navbar-->
<nav class="navbar navbar-expand-lg navbar-light bg-blue" id="globalnav">

    <!-- Navbar brand -->
    <a class="navbar-brand" href="<c:url value="/home" />"><i class="fal fa-home"></i>&nbsp;OpenArchaeo Explorateur</a>

    <!-- Collapse button -->
    <button class="navbar-toggler" type="button" data-toggle="collapse" data-target="#navbarSupportedContent" aria-controls="navbarSupportedContent"
        aria-expanded="false" aria-label="Toggle navigation"><span class="navbar-toggler-icon"></span></button>

    <!-- Collapsible content -->
    <div class="collapse navbar-collapse" id="navbarSupportedContent">

        <!-- Links -->
        <ul class="navbar-nav mr-auto">
            <li class="nav-item ${param.active == 'home' ? 'active' : ''}">
                <a class="nav-link" href="<c:url value="/home" />">Accueil${param.active == 'home' ? '<span class="sr-only">(current)</span>' : ''}</a>
            </li>
            <li class="nav-item ${param.active == 'explorer' ? 'active' : ''}">
                <a class="nav-link" href="<c:url value="/sourcesSelect" />">Explorer${param.active == 'explorer' ? '<span class="sr-only">(current)</span>' : ''}</a>
            </li>
        </ul>
        <!-- Links -->

    </div>
    <!-- Collapsible content -->

</nav>
<!--/.Navbar-->
                