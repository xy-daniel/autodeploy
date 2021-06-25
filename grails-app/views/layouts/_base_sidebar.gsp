%{--
    2021.04.26 >>> 添加羁押室语音模板设置和法庭报警音频设置 daniel
--}%
<div id="top-menu" class="top-menu">
<!-- 第一版页面 -->
    <g:if test="${grailsApplication.config.pageVersion == 'v1'}">
        <!-- begin top-menu nav -->
        <ul class="nav">
            <sec:ifAnyGranted roles='ROLE_SUPER,ROLE_ADMIN,ROLE_/'>
                <li class="has-sub <g:if test="${active >= 10000 && active < 20000}">active</g:if>">
                    <g:link controller="index">
                        <i class="fa fa-th-large"></i>
                        <span>今日庭审</span>
                    </g:link>
                </li>
            </sec:ifAnyGranted>
            <sec:ifAnyGranted roles='ROLE_SUPER,ROLE_ADMIN,ROLE_/plan/list'>
                <li class="has-sub <g:if test="${active >= 20000 && active < 30000}">active</g:if>">
                    <g:link controller="plan" action="list">
                        <i class="fa fa-list"></i>
                        <span>庭审列表</span>
                    </g:link>
                </li>
            </sec:ifAnyGranted>
            <sec:ifAnyGranted roles='ROLE_SUPER,ROLE_ADMIN,ROLE_/charts/index'>
                <li class="has-sub <g:if test="${active >= 50000 && active < 60000}">active</g:if>">
                    <g:link controller="charts">
                        <i class="fa fa-chart-pie"></i>
                        <span>统计分析</span>
                    </g:link>
                </li>
            </sec:ifAnyGranted>
            <sec:ifAnyGranted roles='ROLE_SUPER,ROLE_ADMIN,ROLE_CONFIG'>
                <li class="has-sub <g:if test="${active >= 30000 && active < 40000}">active</g:if>">
                    <a href="javascript:void(0);">
                        <b class="caret"></b>
                        <i class="fa fa-cog"></i>
                        <span>配置与管理</span>
                    </a>
                    <ul class="sub-menu">
                        <sec:ifAnyGranted roles='ROLE_SUPER,ROLE_ADMIN,ROLE_COURT'>
                            <li class="has-sub <g:if test="${active >= 30101 && active <= 30111}">active</g:if>">
                                <a href="javascript:void(0);"><b class="caret pull-right"></b>院信息管理</a>
                                <ul class="sub-menu bg-blue-transparent-2"
                                    <g:if test="${active >= 30101 && active <= 30111}">style = "display: block;"</g:if>>
                                    <sec:ifAnyGranted roles='ROLE_SUPER,ROLE_ADMIN,ROLE_/court/index'>
                                        <li class="<g:if test="${active == 30101}">active</g:if>"><g:link
                                                controller="court" action="index">法院管理</g:link></li>
                                    </sec:ifAnyGranted>
                                    <sec:ifAnyGranted roles='ROLE_SUPER,ROLE_ADMIN,ROLE_/courtroom/list'>
                                        <li class="<g:if test="${active == 30102}">active</g:if>"><g:link
                                                controller="courtroom" action="list">法庭管理</g:link></li>
                                    </sec:ifAnyGranted>
                                    <g:if test="${grailsApplication.config.getProperty("tc.function.voice") == 'true'}">
                                        <sec:ifAnyGranted roles='ROLE_SUPER,ROLE_ADMIN,ROLE_/detentionDevice/list'>
                                            <li class="<g:if test="${active == 30110}">active</g:if>"><g:link
                                                    controller="detentionDevice" action="list">羁押室语音传唤</g:link></li>
                                        </sec:ifAnyGranted>
                                    </g:if>
                                    <g:if test="${grailsApplication.config.getProperty("tc.function.warn") == 'true'}">
                                        <sec:ifAnyGranted roles='ROLE_SUPER,ROLE_ADMIN,ROLE_/system/warnEdit'>
                                            <li class="<g:if test="${active == 30111}">active</g:if>"><g:link
                                                    controller="system" action="warnEdit">法庭报警</g:link></li>
                                        </sec:ifAnyGranted>
                                    </g:if>
                                    <sec:ifAnyGranted roles='ROLE_SUPER,ROLE_ADMIN,ROLE_/caseType/list'>
                                        <li class="<g:if test="${active == 30103}">active</g:if>"><g:link
                                                controller="caseType" action="list">案件类型管理</g:link></li>
                                    </sec:ifAnyGranted>
                                    <sec:ifAnyGranted roles='ROLE_SUPER,ROLE_ADMIN,ROLE_/menu/list'>
                                        <li class="<g:if test="${active == 30108}">active</g:if>"><g:link
                                                controller="menu" action="list">系统功能管理</g:link></li>
                                    </sec:ifAnyGranted>
                                    <sec:ifAnyGranted roles='ROLE_SUPER,ROLE_ADMIN,ROLE_/role/list'>
                                        <li class="<g:if test="${active == 30109}">active</g:if>"><g:link
                                                controller="role" action="list">角色管理</g:link></li>
                                    </sec:ifAnyGranted>
                                    <sec:ifAnyGranted roles='ROLE_SUPER,ROLE_ADMIN,ROLE_/employee/list'>
                                        <li class="<g:if test="${active == 30104}">active</g:if>"><g:link
                                                controller="employee" action="list">人员管理</g:link></li>
                                    </sec:ifAnyGranted>
                                    <sec:ifAnyGranted roles='ROLE_SUPER,ROLE_ADMIN,ROLE_/department/list'>
                                        <li class="<g:if test="${active == 30106}">active</g:if>"><g:link
                                                controller="department" action="list">部门管理</g:link></li>
                                    </sec:ifAnyGranted>
                                    <sec:ifAnyGranted roles='ROLE_SUPER,ROLE_ADMIN,ROLE_/user/list'>
                                        <li class="<g:if test="${active == 30107}">active</g:if>"><g:link
                                                controller="user" action="list">用户管理</g:link></li>
                                    </sec:ifAnyGranted>
                                </ul>
                            </li>
                        </sec:ifAnyGranted>
                        <sec:ifAnyGranted roles='ROLE_SUPER,ROLE_ADMIN,ROLE_SYSLOG'>
                            <li class="has-sub <g:if test="${active >= 30501 && active <= 30502}">active</g:if>">
                                <a href="javascript:void(0);"><b class="caret pull-right"></b>日志</a>
                                <ul class="sub-menu bg-blue-transparent-2"
                                    <g:if test="${active >= 30501 && active <= 30503}">style = "display: block;"</g:if>>
                                    <sec:ifAnyGranted roles='ROLE_SUPER,ROLE_ADMIN,ROLE_/alarmInfo/list'>
                                        <li class="<g:if test="${active == 30503}">active</g:if>"><g:link
                                                url="${createLink(uri: '/')}alarmInfo/list">报警日志</g:link></li>
                                    </sec:ifAnyGranted>
                                    <sec:ifAnyGranted roles='ROLE_SUPER,ROLE_ADMIN,ROLE_/log/system/list'>
                                        <li class="<g:if test="${active == 30501}">active</g:if>"><g:link
                                                url="${createLink(uri: '/')}log/system/list">系统操作日志</g:link></li>
                                    </sec:ifAnyGranted>
                                    <sec:ifAnyGranted roles='ROLE_SUPER,ROLE_ADMIN,ROLE_/log/login/list'>
                                        <li class="<g:if test="${active == 30502}">active</g:if>"><g:link
                                                url="${createLink(uri: '/')}log/login/list">登陆记录</g:link></li>
                                    </sec:ifAnyGranted>
                                </ul>
                            </li>
                        </sec:ifAnyGranted>
                        <sec:ifAnyGranted roles='ROLE_SUPER,ROLE_ADMIN,ROLE_/videoRecord/list'>
                            <li class="has-sub <g:if test="${active == 30400}">active</g:if>">
                                <g:link controller="videoRecord" action="list">直播授权</g:link>
                            </li>
                        </sec:ifAnyGranted>
                        <sec:ifAnyGranted roles='ROLE_SUPER,ROLE_ADMIN,ROLE_/distance/index'>
                            <li class="has-sub <g:if test="${active == 30600}">active</g:if>">
                                <g:link controller="distanceCourt" action="list">远程提讯</g:link>
                            </li>
                        </sec:ifAnyGranted>
                        <sec:ifAnyGranted roles='ROLE_SUPER,ROLE_ADMIN'>
                            <li class="has-sub <g:if test="${active == 30700}">active</g:if>">
                                <g:link controller="mountDisk" action="list">挂载磁盘配置</g:link>
                            </li>
                        </sec:ifAnyGranted>
                        <sec:ifAnyGranted roles='ROLE_SUPER,ROLE_ADMIN'>
                            <li class="has-sub <g:if test="${active == 30900}">active</g:if>">
                                <g:link controller="osInfo" action="list">服务器磁盘详情</g:link>
                            </li>
                        </sec:ifAnyGranted>
                        <sec:ifAnyGranted roles='ROLE_SUPER,ROLE_ADMIN'>
                            <li class="has-sub <g:if test="${active == 31000}">active</g:if>">
                                <g:link controller="cdBurning" action="list">光盘刻录服务器配置</g:link>
                            </li>
                        </sec:ifAnyGranted>
                        <sec:ifAnyGranted roles='ROLE_SUPER,ROLE_ADMIN'>
                            <li class="has-sub <g:if test="${active == 32000}">active</g:if>">
                                <g:link controller="trials" action="list">光盘刻录</g:link>
                            </li>
                        </sec:ifAnyGranted>
                        <sec:ifAnyGranted roles='ROLE_SUPER,ROLE_ADMIN'>
                            <li class="has-sub <g:if test="${active == 32100}">active</g:if>">
                                <g:link controller="clerkClient" action="list">客户端软件更新</g:link>
                            </li>
                        </sec:ifAnyGranted>
                        <sec:ifAnyGranted roles='ROLE_SUPER,ROLE_ADMIN'>
                            <li class="has-sub <g:if test="${active == 30800}">active</g:if>">
                                <g:link controller="system" action="edit">系统设置</g:link>
                            </li>
                        </sec:ifAnyGranted>
                    </ul>
                </li>
            </sec:ifAnyGranted>

            <sec:ifAnyGranted roles='ROLE_SUPER'>
                <li class="has-sub <g:if test="${active >= 1000000 && active < 1100000}">active</g:if>">
                    <a href="javascript:void(0);">
                        <b class="caret"></b>
                        <i class="fa fa-terminal"></i>
                        <span>管理员工具</span>
                    </a>
                    <ul class="sub-menu">
                        <li class="<g:if test="${active == 1000100}">active</g:if>"><g:link controller="ToolBox"
                                                                                            action="migrate">旧数据迁移</g:link></li>
                        <li hidden class="<g:if test="${active == 1000200}">active</g:if>"><g:link controller="ToolBox"
                                                                                                   action="migrate">统计数据脚本</g:link></li>
                        <li class="<g:if test="${active == 1000300}">active</g:if>"><g:link controller="ToolBox"
                                                                                            action="videoDuration">统计视频时长</g:link></li>
                        <g:if test="${grailsApplication.config.getProperty("tc.deployPlace") == "shenzhen"}">
                            <li class="<g:if test="${active == 1000700}">active</g:if>"><g:link controller="ToolBox"
                                                                                                action="pushShowVideoPlatform">推送点播平台系统数据</g:link></li>
                        </g:if>
                        <li class="<g:if test="${active == 1000400}">active</g:if>"><g:link controller="ToolBox"
                                                                                            action="revideo">视频数据恢复</g:link></li>
                        <li class="<g:if test="${active == 1000500}">active</g:if>"><g:link controller="ToolBox"
                                                                                            action="deleteVideo">测试数据删除</g:link></li>
                        <li class="<g:if test="${active == 1000600}">active</g:if>"><g:link controller="ToolBox"
                                                                                            action="about">关于</g:link></li>
                    </ul>
                </li>
            </sec:ifAnyGranted>
            <li class="menu-control menu-control-left">
                <a href="javascript:void(0);" data-click="prev-menu"><i class="fa fa-angle-left"></i></a>
            </li>
            <li class="menu-control menu-control-right">
                <a href="javascript:void(0);" data-click="next-menu"><i class="fa fa-angle-right"></i></a>
            </li>
        </ul>
        <!-- end top-menu nav -->
    </g:if>



<!-- 第二版页面 -->
    <g:if test="${grailsApplication.config.pageVersion == 'v2'}">
        <!-- begin top-menu nav -->
        <ul class="nav">
            <sec:ifAnyGranted roles='ROLE_SUPER,ROLE_ADMIN,ROLE_/'>
                <li class="has-sub <g:if test="${active >= 10000 && active < 20000}">active</g:if>">
                    <g:link controller="index">
                        <i class="fa fa-th-large"></i>
                        <span>今日庭审</span>
                    </g:link>
                </li>
            </sec:ifAnyGranted>

            <sec:ifAnyGranted roles='ROLE_SUPER,ROLE_ADMIN,ROLE_/plan/list'>
                <li class="has-sub <g:if test="${active >= 20000 && active < 30000}">active</g:if>">
                    <g:link controller="plan" action="list">
                        <i class="fa fa-list"></i>
                        <span>庭审预告</span>
                    </g:link>
                </li>
            </sec:ifAnyGranted>

            <sec:ifAnyGranted roles='ROLE_SUPER,ROLE_ADMIN,ROLE_/trialLive/list'>
                <li class="has-sub <g:if test="${active >= 3000 && active < 4000}">active</g:if>">
                    <g:link controller="trialLive" action="list">
                        <i class="fa fa-video"></i>
                        <span>庭审直播</span>
                    </g:link>
                </li>
            </sec:ifAnyGranted>

            <sec:ifAnyGranted roles='ROLE_SUPER,ROLE_ADMIN,ROLE_/trialVideo/list'>
                <li class="has-sub <g:if test="${active >= 4000 && active < 5000}">active</g:if>">
                <g:link controller="trialVideo" action="list">
                    <i class="fa fa-play-circle"></i>
                    <span>庭审点播</span>
                </g:link>
            </sec:ifAnyGranted>

            <sec:ifAnyGranted roles='ROLE_SUPER,ROLE_ADMIN,ROLE_CONFIG'>
                <li class="has-sub <g:if test="${active >= 30000 && active < 40000}">active</g:if>">
                <a href="javascript:void(0);">
                    <b class="caret"></b>
                    <i class="fa fa-business-time"></i>
                    <span>业务管理</span>
                </a>
                <ul class="sub-menu">
                    <sec:ifAnyGranted roles='ROLE_SUPER,ROLE_ADMIN,ROLE_COURT'>
                        <li class="has-sub <g:if test="${active >= 30101 && active <= 30111}">active</g:if>">
                            <a href="javascript:void(0);"><b class="caret pull-right"></b>院信息管理</a>
                            <ul class="sub-menu bg-blue-transparent-2"
                                <g:if test="${active >= 30101 && active <= 30111}">style = "display: block;"</g:if>>
                                <sec:ifAnyGranted roles='ROLE_SUPER,ROLE_ADMIN,ROLE_/court/index'>
                                    <li class="<g:if test="${active == 30101}">active</g:if>"><g:link controller="court"
                                                                                                      action="index">法院管理</g:link></li>
                                </sec:ifAnyGranted>
                                <sec:ifAnyGranted roles='ROLE_SUPER,ROLE_ADMIN,ROLE_/caseType/list'>
                                    <li class="<g:if test="${active == 30103}">active</g:if>"><g:link
                                            controller="caseType" action="list">案件类型管理</g:link></li>
                                </sec:ifAnyGranted>
                                <g:if test="${grailsApplication.config.getProperty("tc.function.voice") == 'true'}">
                                    <sec:ifAnyGranted roles='ROLE_SUPER,ROLE_ADMIN,ROLE_/detentionDevice/list'>
                                        <li class="<g:if test="${active == 30110}">active</g:if>"><g:link
                                                controller="detentionDevice" action="list">羁押室语音传唤</g:link></li>
                                    </sec:ifAnyGranted>
                                </g:if>
                                <g:if test="${grailsApplication.config.getProperty("tc.function.warn") == 'true'}">
                                    <sec:ifAnyGranted roles='ROLE_SUPER,ROLE_ADMIN,ROLE_/system/warnEdit'>
                                        <li class="<g:if test="${active == 30111}">active</g:if>"><g:link
                                                controller="system" action="warnEdit">法庭报警</g:link></li>
                                    </sec:ifAnyGranted>
                                </g:if>
                                <sec:ifAnyGranted roles='ROLE_SUPER,ROLE_ADMIN,ROLE_/menu/list'>
                                    <li class="<g:if test="${active == 30108}">active</g:if>"><g:link controller="menu"
                                                                                                      action="list">系统功能管理</g:link></li>
                                </sec:ifAnyGranted>
                                <sec:ifAnyGranted roles='ROLE_SUPER,ROLE_ADMIN,ROLE_/role/list'>
                                    <li class="<g:if test="${active == 30109}">active</g:if>"><g:link controller="role"
                                                                                                      action="list">角色管理</g:link></li>
                                </sec:ifAnyGranted>
                                <sec:ifAnyGranted roles='ROLE_SUPER,ROLE_ADMIN,ROLE_/employee/list'>
                                    <li class="<g:if test="${active == 30104}">active</g:if>"><g:link
                                            controller="employee" action="list">人员管理</g:link></li>
                                </sec:ifAnyGranted>
                                <sec:ifAnyGranted roles='ROLE_SUPER,ROLE_ADMIN,ROLE_/department/list'>
                                    <li class="<g:if test="${active == 30106}">active</g:if>"><g:link
                                            controller="department" action="list">部门管理</g:link></li>
                                </sec:ifAnyGranted>
                                <sec:ifAnyGranted roles='ROLE_SUPER,ROLE_ADMIN,ROLE_/user/list'>
                                    <li class="<g:if test="${active == 30107}">active</g:if>"><g:link controller="user"
                                                                                                      action="list">用户管理</g:link></li>
                                </sec:ifAnyGranted>
                            </ul>
                        </li>
                    </sec:ifAnyGranted>

                    <sec:ifAnyGranted roles='ROLE_SUPER,ROLE_ADMIN,ROLE_SYSLOG'>
                        <li class="has-sub <g:if test="${active >= 30501 && active <= 30502}">active</g:if>">
                            <a href="javascript:void(0);"><b class="caret pull-right"></b>日志</a>
                            <ul class="sub-menu bg-blue-transparent-2"
                                <g:if test="${active >= 30501 && active <= 30503}">style = "display: block;"</g:if>>
                                <sec:ifAnyGranted roles='ROLE_SUPER,ROLE_ADMIN,ROLE_/alarmInfo/list'>
                                    <li class="<g:if test="${active == 30503}">active</g:if>"><g:link
                                            url="${createLink(uri: '/')}alarmInfo/list">报警日志</g:link></li>
                                </sec:ifAnyGranted>
                                <sec:ifAnyGranted roles='ROLE_SUPER,ROLE_ADMIN,ROLE_/log/system/list'>
                                    <li class="<g:if test="${active == 30501}">active</g:if>"><g:link
                                            url="${createLink(uri: '/')}log/system/list">系统操作日志</g:link></li>
                                </sec:ifAnyGranted>
                                <sec:ifAnyGranted roles='ROLE_SUPER,ROLE_ADMIN,ROLE_/log/login/list'>
                                    <li class="<g:if test="${active == 30502}">active</g:if>"><g:link
                                            url="${createLink(uri: '/')}log/login/list">登陆记录</g:link></li>
                                </sec:ifAnyGranted>
                            </ul>
                        </li>
                    </sec:ifAnyGranted>
                    <sec:ifAnyGranted roles='ROLE_SUPER,ROLE_ADMIN,ROLE_/videoRecord/list'>
                        <li class="has-sub <g:if test="${active == 30400}">active</g:if>">
                            <g:link controller="videoRecord" action="list">直播授权</g:link>
                        </li>
                    </sec:ifAnyGranted>
                    <sec:ifAnyGranted roles='ROLE_SUPER,ROLE_ADMIN,ROLE_/charts/index'>
                        <li class="has-sub <g:if test="${active == 30900}">active</g:if>">
                            <g:link controller="charts"><span>统计分析</span></g:link>
                        </li>
                    </sec:ifAnyGranted>
                </ul>
            </sec:ifAnyGranted>


            <sec:ifAnyGranted roles='ROLE_SUPER,ROLE_ADMIN,ROLE_SYSCONFIG'>
                <li class="has-sub <g:if test="${active >= 5000 && active < 6000}">active</g:if>">
                <a href="javascript:void(0);">
                    <b class="caret"></b>
                    <i class="fa fa-cog"></i>
                    <span>系统配置</span>
                </a>
                <ul class="sub-menu">
                    <sec:ifAnyGranted roles='ROLE_SUPER,ROLE_ADMIN,ROLE_/distance/index'>
                        <li class="has-sub <g:if test="${active == 5100}">active</g:if>">
                            <g:link controller="distanceCourt" action="list">远程提讯</g:link>
                        </li>
                    </sec:ifAnyGranted>
                    <sec:ifAnyGranted roles='ROLE_SUPER,ROLE_ADMIN,ROLE_/mountDisk/list'>
                        <li class="has-sub <g:if test="${active == 5200}">active</g:if>">
                            <g:link controller="mountDisk" action="list">挂载磁盘配置</g:link>
                        </li>
                    </sec:ifAnyGranted>
                    <sec:ifAnyGranted roles='ROLE_SUPER,ROLE_ADMIN,ROLE_/system/edit'>
                        <li class="has-sub <g:if test="${active == 5300}">active</g:if>">
                            <g:link controller="system" action="edit">系统设置</g:link>
                        </li>
                    </sec:ifAnyGranted>
                    <sec:ifAnyGranted roles='ROLE_SUPER,ROLE_ADMIN,ROLE_/osInfo/list'>
                        <li class="has-sub <g:if test="${active == 5400}">active</g:if>">
                            <g:link controller="osInfo" action="list">服务器磁盘详情</g:link>
                        </li>
                    </sec:ifAnyGranted>
                    <sec:ifAnyGranted roles='ROLE_SUPER,ROLE_ADMIN,ROLE_/cdBurning/list'>
                        <li class="has-sub <g:if test="${active == 5500}">active</g:if>">
                            <g:link controller="cdBurning" action="list">光盘刻录服务器配置</g:link>
                        </li>
                    </sec:ifAnyGranted>
                    <sec:ifAnyGranted roles='ROLE_SUPER,ROLE_ADMIN,ROLE_/trial/list'>
                        <li class="has-sub <g:if test="${active == 5600}">active</g:if>">
                            <g:link controller="trials" action="list">光盘刻录</g:link>
                        </li>
                    </sec:ifAnyGranted>
                    <sec:ifAnyGranted roles='ROLE_SUPER,ROLE_ADMIN,ROLE_/clerkClient/list'>
                        <li class="has-sub <g:if test="${active == 5700}">active</g:if>">
                            <g:link controller="clerkClient" action="list">客户端软件更新</g:link>
                        </li>
                    </sec:ifAnyGranted>
                </ul>
            </sec:ifAnyGranted>


            <sec:ifAnyGranted roles='ROLE_SUPER,ROLE_ADMIN,ROLE_MONITOR'>
                <li class="has-sub <g:if test="${active >= 7000 && active < 8000}">active</g:if>">
                <g:link controller="courtroom" action="list">
                    <i class="fa fa-desktop"></i>
                    <span>监控中心</span>
                </g:link>
            </sec:ifAnyGranted>

            <sec:ifAnyGranted roles='ROLE_SUPER'>
                <li class="has-sub <g:if test="${active >= 1000000 && active < 1100000}">active</g:if>">
                    <a href="javascript:void(0);">
                        <b class="caret"></b>
                        <i class="fa fa-terminal"></i>
                        <span>管理员工具</span>
                    </a>
                    <ul class="sub-menu">
                        <li class="<g:if test="${active == 1000100}">active</g:if>"><g:link controller="ToolBox"
                                                                                            action="migrate">旧数据迁移</g:link></li>
                        <li hidden class="<g:if test="${active == 1000200}">active</g:if>"><g:link controller="ToolBox"
                                                                                                   action="migrate">统计数据脚本</g:link></li>
                        <li class="<g:if test="${active == 1000300}">active</g:if>"><g:link controller="ToolBox"
                                                                                            action="videoDuration">统计视频时长</g:link></li>
                        <li class="<g:if test="${active == 1000400}">active</g:if>"><g:link controller="ToolBox"
                                                                                            action="revideo">视频数据恢复</g:link></li>
                        <li class="<g:if test="${active == 1000500}">active</g:if>"><g:link controller="ToolBox"
                                                                                            action="deleteVideo">测试数据删除</g:link></li>
                        <li class="<g:if test="${active == 1000600}">active</g:if>"><g:link controller="ToolBox"
                                                                                            action="about">关于</g:link></li>
                    </ul>
                </li>
            </sec:ifAnyGranted>

            <li class="menu-control menu-control-left">
                <a href="javascript:void(0);" data-click="prev-menu"><i class="fa fa-angle-left"></i></a>
            </li>
            <li class="menu-control menu-control-right">
                <a href="javascript:void(0);" data-click="next-menu"><i class="fa fa-angle-right"></i></a>
            </li>
        </ul>
    </g:if>
</div>
