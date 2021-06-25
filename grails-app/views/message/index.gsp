<%@ page import="com.hxht.autodeploy.PlanStatus" %>
<!DOCTYPE html>
<!--[if IE 8]> <html class="ie8"> <![endif]-->
<!--[if !IE]><!-->
<html>
<!--<![endif]-->
<head>
    <meta charset="utf-8" />
    <title>领导观摩系统</title>
    <meta content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=no" name="viewport" />
    <meta content="" name="description" />
    <meta content="" name="author" />

    <!-- ================== BEGIN BASE CSS STYLE ================== -->
    <g:render template="/layouts/base_head"/>
    <!-- ================== END BASE CSS STYLE ================== -->

    <!-- ================== BEGIN PAGE LEVEL STYLE ================== -->
    <asset:stylesheet href="bootstrap-calendar/css/bootstrap_calendar.css" rel="stylesheet"/>
    <asset:stylesheet href="index/index.css" rel="stylesheet"/>
    <asset:stylesheet href="bootstrap-treeview/bootstrap-treeview.min.css" rel="stylesheet"/>
    <!-- ================== END PAGE LEVEL STYLE ================== -->
    <style>
    .page-with-top-menu{
        padding-top: 40px;
    }

    </style>
</head>
<body>

<!-- begin #page-container -->
<div id="page-container" class="page-container fade page-without-sidebar page-header-fixed page-with-top-menu"style ="padding: 0px">
%{--    <!-- begin #header -->--}%
%{--    <g:render template="/layouts/base_navigation"--}%
%{--              model="[--}%
%{--                      picture: true,--}%
%{--                      arrow  : true--}%
%{--              ]"/>--}%
%{--    <!-- end #header -->--}%

    <!-- begin #sidebar -->
    <g:render template="/leader/leader_sidebar" model="[active: 10100]"/>
    <!-- end #sidebar -->
    <div id="content" class="content" style="padding-top:50px">
%{--        <div class="panel panel-inverse" style="height: 100%;margin-bottom:10px">--}%
%{--            <div class="panel-heading">--}%
%{--                 <h4 class="panel-title" style="color: white;font-size: 16px">今日庭审</h4>--}%
%{--            </div>--}%
%{--        </div>--}%
        <div class="row">
            <div class="col-md-2">
                <div class="col-sm-12 court-height">
                    <p class="form-group form-bottom">
                        <input type="input" class="form-control" id="input-search" placeholder="请输入要查询的法庭" value="">
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
                    <div class="index-plan-list-panel" data-scrollbar="true" style="border-top:0;">
                        <div class="col-md-12" style="padding-left: 0;padding-right: 0">
                            <div class="row index-show-video">
                                <div class="col-md-8" style="padding-right: 0">
                                    <div id="video">
                                        <div class="index-show-video-null">
                                            <span>未开庭暂无视频</span>
                                        </div>
                                    </div>
                                </div>2
                                <div class="col-md-4" style="padding-left: 0">
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
<asset:javascript src="DPlayer/DPlayer.min.js"/>l
<asset:javascript src="hls/hls.min.js"/>
<asset:javascript src="bootstrap-treeview/bootstrap-treeview.min.js"/>
<asset:javascript src="leader/message.js"/>
<!-- ================== END BASE JS ================== -->
</body>
</html>
