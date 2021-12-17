<div id="top-menu" class="top-menu">
    <!-- begin top-menu nav -->
    <ul class="nav">
        <sec:ifAnyGranted roles='ROLE_SUPER,ROLE_ADMIN,ROLE_/'>
            <li class="has-sub <g:if test="${active >= 0 && active < 10000}">active</g:if>">
                <g:link controller="index">
                    <i class="fa fa-th-large"></i>
                    <span>控制台</span>
                </g:link>
            </li>
        </sec:ifAnyGranted>
        <sec:ifAnyGranted roles='ROLE_SUPER,ROLE_ADMIN,ROLE_/plan/list'>
            <li class="has-sub <g:if test="${active >= 10000 && active < 20000}">active</g:if>">
                <g:link controller="device" action="list">
                    <i class="fas fa-desktop"></i>
                    <span>主机管理</span>
                </g:link>
            </li>
        </sec:ifAnyGranted>
        <sec:ifAnyGranted roles='ROLE_SUPER,ROLE_ADMIN,ROLE_/plan/list'>
            <li class="has-sub <g:if test="${active >= 20000 && active < 30000}">active</g:if>">
                <g:link controller="app" action="list">
                    <i class="fas fa-pencil-alt fa-fw"></i>
                    <span>应用管理</span>
                </g:link>
            </li>
        </sec:ifAnyGranted>
        <sec:ifAnyGranted roles='ROLE_SUPER,ROLE_ADMIN'>
            <li class="has-sub <g:if test="${active >= 30000 && active < 40000}">active</g:if>">
                <g:link controller="task" action="list">
                    <i class="fas fa-bookmark"></i>
                    <span>任务中心</span>
                </g:link>
            </li>
        </sec:ifAnyGranted>
        <sec:ifAnyGranted roles='ROLE_SUPER'>
            <li class="has-sub <g:if test="${active >= 50000 && active < 60000}">active</g:if>">
                <a href="javascript:void(0);">
                    <b class="caret"></b>
                    <i class="fa fa-cog"></i>
                    <span>配置中心</span>
                </a>
                <ul class="sub-menu">
                    <sec:ifAnyGranted roles='ROLE_SUPER'>
                        <li class="has-sub <g:if test="${active >= 50000 && active <= 51000}">active</g:if>">
                            <a href="javascript:void(0);"><b class="caret pull-right"></b>权限管理</a>
                            <ul class="sub-menu bg-blue-transparent-2"
                                <g:if test="${active >= 50000 && active <= 51000}">style = "display: block;"</g:if>>
                                <sec:ifAnyGranted roles='ROLE_SUPER'>
                                    <li class="<g:if test="${active >= 50001 && active <= 50100}">active</g:if>"><g:link
                                            controller="user" action="list">用户管理</g:link></li>
                                </sec:ifAnyGranted>
                                <sec:ifAnyGranted roles='ROLE_SUPER'>
                                    <li class="<g:if test="${active >= 50101 && active <= 50200}">active</g:if>"><g:link
                                            controller="role" action="list">角色管理</g:link></li>
                                </sec:ifAnyGranted>
                                <sec:ifAnyGranted roles='ROLE_SUPER'>
                                    <li class="<g:if test="${active >= 50201 && active <= 50300}">active</g:if>"><g:link
                                            controller="menu" action="list">菜单管理</g:link></li>
                                </sec:ifAnyGranted>
                            </ul>
                        </li>
                    </sec:ifAnyGranted>
                </ul>
            </li>
        </sec:ifAnyGranted>
        <sec:ifAnyGranted roles='ROLE_SUPER'>
            <li class="has-sub <g:if test="${active >= 60000 && active < 70000}">active</g:if>">
                <g:link controller="toolBox" action="about">
                    <i class="fas fa-info"></i>
                    <span>关于</span>
                </g:link>
            </li>
        </sec:ifAnyGranted>
        <li class="menu-control menu-control-left">
            <a href="javascript:void(0);" data-click="prev-menu"><i class="fa fa-angle-left"></i></a>
        </li>
        <li class="menu-control menu-control-right">
            <a href="javascript:void(0);" data-click="next-menu"><i class="fa fa-angle-right"></i></a>
        </li>
    </ul>
</div>
