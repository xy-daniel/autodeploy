<%@ page import="com.hxht.autodeploy.PlanStatus" %>
<!DOCTYPE html>
<!--[if IE 8]> <html class="ie8"> <![endif]-->
<!--[if !IE]><!-->
<html>
<!--<![endif]-->
<head>
    <meta charset="utf-8"/>
    <title>领导观摩系统</title>
    <meta content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=no" name="viewport"/>
    <meta content="" name="description"/>
    <meta content="" name="author"/>

    <!-- ================== BEGIN BASE CSS STYLE ================== -->
    <g:render template="/layouts/base_head"/>
    <!-- ================== END BASE CSS STYLE ================== -->

    <!-- ================== BEGIN PAGE LEVEL STYLE ================== -->
    <!-- ================== END PAGE LEVEL STYLE ================== -->
    <style>
    .page-with-top-menu{
        padding-top: 40px;
    }

    </style>
</head>

<body>


<!-- begin #page-container -->
<div id="page-container" class="page-container fade page-without-sidebar page-header-fixed page-with-top-menu">

    <!-- begin #sidebar -->
    <g:render template="/leader/leader_sidebar" model="[active: 70100]"/>
    <!-- end #sidebar -->

    <!-- begin #content -->
    <div id="content" class="content">
        <!-- begin breadcrumb -->
        <ol class="breadcrumb pull-right">
            <li class="breadcrumb-item"><g:link controller="index">首页</g:link></li>
            <li class="breadcrumb-item"><g:link controller="courtroom" action="list">法庭管理</g:link></li>
            <li class="breadcrumb-item active">法庭视频监控</li>
        </ol>
        <!-- end breadcrumb -->
        <!-- begin page-header -->
        <h1 class="page-header">法庭管理 <small>法庭视频监控</small></h1>
        <!-- end page-header -->
        <!-- 数据 -->
        <input id="roomId" type="hidden" value="${courtroom.id}">
        <!-- 数据 -->
        <!-- begin row -->
        <div class="row">
            <div class="col-lg-8">
                <ul class="nav nav-tabs nav-tabs-inverse plan-show-chn">
                    <g:each in="${chnList}" var="chn" status="i">
                        <li class="nav-item"><a href="#" data-toggle="tab" data="${chn.number}"class="nav-link plan-tab-chn <g:if test="${i == 0}">active</g:if>">${chn.name}</a></li>
                    </g:each>
                </ul>
                <div class="plan-show-video">
                    <div id="video" style="width: 100%;">
                        <div class="plan-show-video-notfound">
                            <span>未开庭暂无视频</span>
                        </div>
                    </div>
                    <div class="tab-content plan-show-video-tab-content">
                    </div>
                </div>
            </div>
            <div class="col-lg-4">
                <div class="panel panel-inverse panel-with-tabs">
                    <div class="panel-heading p-0">
                        <div class="panel-heading-btn m-r-10 m-t-10">
                            <a href="javascript:void(0);" class="btn btn-xs btn-icon btn-circle btn-success" data-click="panel-expand"><i class="fa fa-expand"></i></a>
                        </div>
                        <div class="tab-overflow">
                            <ul class="nav nav-tabs nav-tabs-inverse ">
                                <li class="nav-item"><a href="#nav-tab-7" data-toggle="tab" class="nav-link active">案件信息</a></li>
                            </ul>
                        </div>
                    </div>

                    <div class="plan-show-info">
                        <div class="tab-content plan-show-info-tab-content">
                            <!-- begin tab-pane -->
                            <div class="tab-pane fade active show" id="nav-tab-7">
                                <g:if test="${data.planId != ""}">
                                    <h3>${data.caseArchives} - ${PlanStatus.getString(data.status)}</h3>
                                    <h4>${data.caseName}</h4>
                                    <dl class="dl-horizontal">
                                        <dt class="text-inverse">立案日期</dt>
                                        <dd>${data.filingDate}</dd>
                                        <dt class="text-inverse">所在法庭</dt>
                                        <dd>${data.courtroom} - ${data.caseType}</dd>
                                        <dt class="text-inverse">原告</dt>
                                        <dd>${data.accuser}</dd>
                                        <dt class="text-inverse">被告</dt>
                                        <dd>${data.accused}</dd>
                                        <dt class="text-inverse">庭审时间</dt>
                                        <dd>${data.startDate} - ${data.endDate}</dd>
                                        <dt class="text-inverse">合议庭成员</dt>
                                        <dd>${data.collegial*.info.join(",")}</dd>
                                        <dt class="text-inverse">书记员</dt>
                                        <dd>${data.secretary.name}</dd>
                                        <dt class="text-inverse">概要</dt>
                                        <dd>${data.summary}</dd>
                                        <dt class="text-inverse">详情</dt>
                                        <dd>${data.detail}</dd>
                                    </dl>
                                </g:if>
                                <g:else>
                                    <h2 style="padding-bottom: 500px">没有正在开庭的排期</h2>
                                </g:else>
                            </div>
                            <!-- end tab-pane -->
                        </div>
                    </div>
                </div>
            </div>
        </div>
        <!-- end row -->
    </div>
    <!-- end #content -->

    <!-- begin scroll to top btn -->
    <g:render template="/layouts/base_topbtn"/>
    <!-- end scroll to top btn -->
</div>
<!-- end page container -->

<g:render template="/layouts/base_bottom"/>
<!-- ================== BEGIN PAGE LEVEL JS ================== -->
<asset:javascript src="flvjs/flv.min.js"/>
<asset:javascript src="DPlayer/DPlayer.min.js"/>
<asset:javascript src="leader/videoshow.js"/>
<script>
    $(document).ready(function () {
    });
</script>
<!-- ================== END PAGE LEVEL JS ================== -->
</body>
</html>
