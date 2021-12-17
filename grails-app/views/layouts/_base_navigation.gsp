<div id="header" class="header navbar-default">
    <!-- begin navbar-header -->
    <div class="navbar-header">
        <a href="${createLink(uri: '/')}" class="navbar-brand">自动化运维平台</a>
        <button type="button" class="navbar-toggle" data-click="top-menu-toggled">
            <span class="icon-bar"></span>
            <span class="icon-bar"></span>
            <span class="icon-bar"></span>
        </button>
    </div>
    <!-- end navbar-header -->

    <!-- begin header-nav -->
    <ul class="navbar-nav navbar-right">
        <li class="dropdown navbar-user">
            <a href="#" class="dropdown-toggle" data-toggle="dropdown">
                <g:if test="${picture}">
                    <img src="/user/photo/<auto:photo/>" onerror="this.src='<asset:assetPath src="user.jpg" />'" alt=""/>
                </g:if >
                <span class="d-none d-md-inline">欢迎，<auto:realName/></span> <b class="caret"></b>
            </a>
            <g:if test="${arrow}">
                <g:render template="/layouts/navigation/arrow"/>
            </g:if >
        </li>
    </ul>
    <!-- end header navigation right -->
</div>