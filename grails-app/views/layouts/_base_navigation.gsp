<%@ page import="com.hxht.autodeploy.court.manager.SystemController" %>
<div id="header" class="header navbar-default">
    <!-- begin navbar-header -->
    <div class="navbar-header">
        <a href="${createLink(uri: '/')}" class="navbar-brand">${SystemController.currentCourt.ext5}</a>
%{--        <a href="${createLink(uri: '/')}" class="navbar-brand">科技法庭管理系统 </a>--}%
        <button type="button" class="navbar-toggle" data-click="top-menu-toggled">
            <span class="icon-bar"></span>
            <span class="icon-bar"></span>
            <span class="icon-bar"></span>
        </button>
    </div>
    <!-- end navbar-header -->

    <!-- begin header-nav -->
    <ul class="navbar-nav navbar-right">
        <li class="dropdown">
            <a href="#" data-toggle="dropdown" downclass="drop-toggle f-s-14">
                <i class="fa fa-bell"></i>
                <span class="label"></span>
            </a>
            <ul class="dropdown-menu media-list dropdown-menu-right">
                <li class="dropdown-header"></li>
            </ul>

        </li>
        <li class="dropdown navbar-user">
            <a href="#" class="dropdown-toggle" data-toggle="dropdown">
                <g:if test="${picture}">
                    <img src="/user/photo/<tc:photo/>" onerror="this.src='<asset:assetPath src="user.jpg" />'" alt=""/>
                </g:if >
                <span class="d-none d-md-inline">欢迎，<tc:realName/></span> <b class="caret"></b>
            </a>
            <g:if test="${arrow}">
                <g:render template="/layouts/navigation/arrow"/>
            </g:if >
        </li>
    </ul>
    <!-- end header navigation right -->
</div>
<asset:javascript src="notify/notify.js"/>