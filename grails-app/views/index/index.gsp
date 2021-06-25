<%@ page import="com.hxht.autodeploy.PlanStatus" %>
<!DOCTYPE html>
<!--[if IE 8]> <html class="ie8"> <![endif]-->
<!--[if !IE]><!-->
<html>
<!--<![endif]-->
<head>
    <meta charset="utf-8"/>
    <title>科技法庭管理系统</title>
    <meta content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=no" name="viewport"/>
    <meta content="" name="description"/>
    <meta content="" name="author"/>

    <!-- ================== BEGIN BASE CSS STYLE ================== -->
    <g:render template="/layouts/base_head"/>
    <!-- ================== END BASE CSS STYLE ================== -->

    <!-- ================== BEGIN PAGE LEVEL STYLE ================== -->
    <asset:stylesheet href="bootstrap-calendar/css/bootstrap_calendar.css" rel="stylesheet"/>
    <asset:stylesheet href="index/index.css" rel="stylesheet"/>
    <asset:stylesheet href="bootstrap-treeview/bootstrap-treeview.min.css" rel="stylesheet"/>
    <!-- ================== END PAGE LEVEL STYLE ================== -->
</head>

<body>

<!-- begin #page-container -->
<div id="page-container" class="page-container fade page-without-sidebar page-header-fixed page-with-top-menu">
    <!-- begin #header -->
    <g:render template="/layouts/base_navigation"
              model="[
                      picture: true,
                      arrow  : true
              ]"/>
    <!-- end #header -->

    <!-- begin #sidebar -->
    <g:render template="/layouts/base_sidebar" model="[active: 10000]"/>
    <!-- end #sidebar -->
    <div id="content" class="content">

        <input id="pageVersion" value="${grailsApplication.config.pageVersion}" hidden="hidden"/>

        <div class="row">
            <div class="col-md-2">
                <div class="col-sm-12 court-height">
                    <p class="form-group form-bottom">
                        <input class="form-control" id="input-search" placeholder="请输入要查询的法庭" value="">
                    </p>

                    <div id="treeview2" class=""></div>
                </div>
            </div>

            <div class="col-md-4">
                <div class="panel panel-inverse">
                    <div class="panel-heading">
                        <div class="btn-group pull-right">
                            <button type="button" class="btn btn-success btn-xs">所选日期完整列表</button>
                        </div>
                        <input type="hidden" name="date" value="" id="datetime"/>
                        <h4 class="panel-title">排期分布</h4>
                    </div>

                    <div class="index-plan-list-panel" data-scrollbar="true">
                        <div id="schedule-calendar" class="bootstrap-calendar"></div>

                        <div class="list-group index-plan-list">
                            <div class="index-plan-list-null">
                                <span>今日期无排期数据</span>
                            </div>
                        </div>
                    </div>
                </div>
            </div>

            <div class="col-md-6">

                <div class="panel panel-inverse">
                    <div class="panel-heading">
                        <div class="btn-group pull-right">
                            <button type="button" class="btn btn-success btn-xs plan-show">案件详情</button>
                        </div>
                        <h4 class="panel-title court-plan">今日期无排期数据</h4>
                    </div>

                    <div class="index-plan-list-panel b-t-0" data-scrollbar="true">
                        <div class="col-md-12 p-l-0 p-r-0">
                            <div class="row index-show-video">
                                <div class="col-md-8">
                                    <sec:ifNotGranted roles='ROLE_SUPER,ROLE_ADMIN,ROLE_LIVE'>
                                        <div id="videoNotShow">
                                            <div class="index-show-video-null">
                                                <span>视频无直播权限</span>
                                            </div>
                                        </div>
                                    </sec:ifNotGranted>
                                    <sec:ifAnyGranted roles='ROLE_SUPER,ROLE_ADMIN,ROLE_LIVE'>
                                        <div id="video"></div>
                                    </sec:ifAnyGranted>
                                </div>

                                <div class="col-md-4 p-l-0">
                                    <div class="index-show-video-msg" data-scrollbar="true"></div>
                                </div>
                            </div>

                            <div class="row">
                                <div class="col-md-12">
                                    <div class="index-show-video-bigmsg" data-scrollbar="true"></div>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>
<!-- end page container -->

<g:render template="/layouts/base_bottom"/>
<!-- ================== BEGIN PAGE LEVEL JS ================== -->
<asset:javascript src="bootstrap-calendar/js/bootstrap_calendar.js"/>
<asset:javascript src="flvjs/flv.min.js"/>
<asset:javascript src="DPlayer/DPlayer.min.js"/>
<asset:javascript src="hls/hls.min.js"/>
<asset:javascript src="bootstrap-treeview/bootstrap-treeview.min.js"/>
<asset:javascript src="index/index.js"/>

<script>
</script>
</body>
</html>
