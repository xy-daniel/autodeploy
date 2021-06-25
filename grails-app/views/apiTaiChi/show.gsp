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
    <asset:stylesheet href="bootstrap-sweetalert/sweetalert.css"/>
    <asset:stylesheet href="fileinput/fileinput.min.css"/>
    <asset:stylesheet href="bootstrap-select/bootstrap-select.min.css"/>
    <!-- ================== END PAGE LEVEL STYLE ================== -->
</head>

<body>
<!-- begin #page-loader -->
<g:render template="/layouts/base_loader"/>
<!-- end #page-loader -->

<!-- begin #page-container -->
<div id="page-container" class="page-container fade page-without-sidebar">
    <!-- begin #content -->
    <div id="content" class="content">
        <!-- begin page-header -->
        <h1 class="page-header">庭审管理 <small>案件详情</small></h1>
        <!-- end page-header -->
        <!-- begin row -->
        <div class="row plaan-info" trialId="${data.trialId}" planId="${data.planId}">
            <div class="col-lg-8">
                <ul class="nav nav-tabs nav-tabs-inverse plan-show-chn">
                    <g:each in="${chnList}" var="chn" status="i">
                        <li class="nav-item"><a href="#" data-toggle="tab" data="${chn.number}" class="nav-link plan-tab-chn <g:if test="${i == 0}">active</g:if>">${chn.name}</a></li>
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
                            <a href="javascript:;" class="btn btn-xs btn-icon btn-circle btn-success" data-click="panel-expand"><i class="fa fa-expand"></i></a>
                        </div>
                        <g:if test="${data.status != PlanStatus.PLAN}">
                        <div class="btn-group pull-right m-r-10 m-t-10">
                            <button type="button" class="btn btn-success btn-xs dropdown-toggle"
                                    data-toggle="dropdown">选择排期庭次</button>
                            <ul class="dropdown-menu" role="menu">
                                <g:each in="${trialList}" var="t" status="i">
                                    <li><g:link controller="apiTaiChi" action="show"
                                                params="[trial: t.id]">${PlanStatus.getString(t.status)}-${t.startDate}</g:link></li>
                                </g:each>
                            </ul>
                        </div>
                        </g:if>
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
                                <!-- 携带初始化数据 -->
                                <div id="mainCaseDiv">
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
                                        <dt class="text-inverse">法官</dt>
                                        <dd>${data.judge}</dd>
                                        <dt class="text-inverse">合议庭成员</dt>
                                        <dd>
                                            <g:if test="${data.collegial == "无数据"}">
                                                无数据
                                            </g:if>
                                            <g:else>
                                                <g:each in="${data.collegial}" var="collegial" status="i">
                                                    <g:if test="${i == 0}">
                                                        ${collegial?.name}
                                                    </g:if>
                                                    <g:else>
                                                        ,${collegial?.name}
                                                    </g:else>
                                                </g:each>
                                            </g:else>
                                        </dd>
                                        <dt class="text-inverse">书记员</dt>
                                        <dd>${data.secretary?data.secretary.name:"无数据"}</dd>
                                        <dt class="text-inverse">概要</dt>
                                        <dd>${data.summary}</dd>
                                        <dt class="text-inverse">详情</dt>
                                        <dd>${data.detail}</dd>
                                    </dl>
                                </div>
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
<asset:javascript src="hls/hls.min.js"/>
<asset:javascript src="DPlayer/DPlayer.min.js"/>
<asset:javascript src="bootstrap-sweetalert/sweetalert.js"/>
<asset:javascript src="fileinput/fileinput.min.js"/>
<asset:javascript src="fileinput/zh.js"/>
<asset:javascript src="sockjs/sockjs.min.js"/>
<asset:javascript src="stomp/stomp.min.js"/>
<asset:javascript src="slimscroll/jquery.slimscroll.min.js"/>
<asset:javascript src="bootstrap-select/bootstrap-select.min.js"/>
<asset:javascript src="select2/dist/js/select2.min.js"/>
<asset:javascript src="taiChi/show.js"/>
<script>
    $(document).ready(function () {
    });
</script>
<!-- ================== END PAGE LEVEL JS ================== -->
</body>
</html>
