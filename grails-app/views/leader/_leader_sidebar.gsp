<style>

.top-menu{
    top:0px;
}
</style>
%{--<%@ page import="com.hxht.autodeploy.court.manager.SystemTitleController" %>--}%
<div id="top-menu" class="top-menu">
    <ul class="nav">
            <li class="has-sub <g:if test="${active >= 10000 && active < 20000}">active</g:if>">
                <g:link controller="message" action="index">
                    <i class="fa fa-th-large"></i>
                    <span>今日庭审</span>
                </g:link>
            </li>
            <li class="has-sub <g:if test="${active >= 20000 && active < 30000}">active</g:if>">
                <g:link controller="leader" action="query">
                    <i class="fa fa-list"></i>
                    <span>庭审预告</span>
                </g:link>
            </li>
            <li class="has-sub <g:if test="${active >= 30000 && active < 40000}">active</g:if>">
                <g:link controller="leader" action="trialList">
                    <i class="fa fa-video"></i>
                    <span>庭审直播</span>
                </g:link>
            </li>
            <li class="has-sub <g:if test="${active >= 40000 && active < 50000}">active</g:if>">
            <g:link controller="leader" action="trialVideoList">
                <i class="fa fa-camera-retro"></i>
                <span>庭审点播</span>
            </g:link>
            <li class="has-sub <g:if test="${active >= 70000 && active < 80000}">active</g:if>">
            <g:link controller="leader" action="courtroomStatus">
                <i class="fa fa-camera"></i>
                <span>监控中心</span>
            </g:link>

        <li class="menu-control menu-control-left">
            <a href="javascript:;" data-click="prev-menu"><i class="fa fa-angle-left"></i></a>
        </li>
        <li class="menu-control menu-control-right">
            <a href="javascript:;" data-click="next-menu"><i class="fa fa-angle-right"></i></a>
        </li>
    </ul>

</div>
