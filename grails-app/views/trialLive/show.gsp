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
<div id="page-container" class="page-container fade page-without-sidebar page-header-fixed page-with-top-menu">
    <!-- begin #header -->
    <g:render template="/layouts/base_navigation"
              model="[
                      picture: true,
                      arrow  : true
              ]"/>
    <!-- end #header -->

    <!-- begin #sidebar -->
    <g:render template="/layouts/base_sidebar" model="[active: 3010]"/>
    <!-- end #sidebar -->

    <!-- begin #content -->
    <div id="content" class="content">
        <!-- begin breadcrumb -->
        <ol class="breadcrumb pull-right">
            <li class="breadcrumb-item"><g:link controller="index">首页</g:link></li>
            <li class="breadcrumb-item">
                <g:link controller="trialLive" action="list">庭审直播</g:link>
            </li>
            <li class="breadcrumb-item active">案件详情</li>
        </ol>
        <!-- end breadcrumb -->
        <!-- begin page-header -->
        <h1 class="page-header">庭审直播 <small>案件详情</small></h1>
        <!-- end page-header -->
        <!-- begin row -->
        <div class="row plaan-info" trialId="${data.trialId}" planId="${data.planId}" allowPlay="${data.allowPlay}">
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
                                    <li><g:link controller="plan" action="show"
                                                params="[trial: t.id]">${PlanStatus.getString(t.status)}-${t.startDate}</g:link></li>
                                </g:each>
                            </ul>
                        </div>
                        </g:if>
                        <div class="tab-overflow">
                            <ul class="nav nav-tabs nav-tabs-inverse ">
                                <li class="nav-item"><a href="#nav-tab-7" data-toggle="tab" class="nav-link active">案件信息</a></li>
                                <li class="nav-item"><a href="#nav-tab-8" data-toggle="tab" class="nav-link">笔录</a></li>
                                <li class="nav-item"><a href="#nav-tab-10" data-toggle="tab"
                                                        class="nav-link connect">通讯</a></li>
                                <sec:ifAnyGranted roles='ROLE_SUPER,ROLE_ADMIN,ROLE_CONTROL'>
                                    <li class="nav-item"><a href="#nav-tab-9" data-toggle="tab" class="nav-link">庭审控制</a></li>
                                </sec:ifAnyGranted>
                            </ul>
                        </div>
                    </div>

                    <div class="plan-show-info">
                        <div class="tab-content plan-show-info-tab-content">
                            <!-- begin tab-pane -->
                            <div class="tab-pane fade active show" id="nav-tab-7">
                                <h3>${data.caseArchives} - ${caseList.size() != 0?PlanStatus.getString(data.status)+"(并案)":PlanStatus.getString(data.status)}</h3>
                                <g:if test="${caseList.size() != 0}">

                                    <div class="m-b-10 m-t-10">
                                        <h4>并案案件(<span class="text-red-darker">请选择并案案件切换案件信息</span>)</h4>
                                        <div class="width-300">
                                            <select class="form-control selectpicker" id="caselist" data-size="10" data-live-search="true" data-style="btn-white">
                                                <option value="0">${data.caseArchives}</option>
                                                <g:each in="${caseList}" var="caseinfo" status="i">
                                                    <option value="${caseinfo.id}">${caseinfo.archives}</option>
                                                </g:each>
                                            </select>
                                        </div>
                                    </div>
                                </g:if>
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

                            <!-- begin 即时通讯 -->
                            <input id="planId" type="hidden" value="${data.planId}">
                            <input id="userId" type="hidden" value="${data.userId}">
                            <input id="trialId" type="hidden" value="${data.trialId}">
                            <!-- begin panel -->
                            <div class="tab-pane fade" style="min-height: 550px" id="nav-tab-10">
                                <div class="chat" id="chat" style="height: 520px" data-scrollbar="true">
                                    <g:each in="${chatRecord}" var="cr" status="i">
                                        <g:if test="${userName == cr.userName}">
                                            <!-- 右侧 -->
                                            <div class="media media-sm message">
                                                <input type="hidden" value="${cr.uuid}">

                                                <div class="media-body">
                                                    <div class="row">
                                                        <div class="col-3">
                                                            <sec:ifAnyGranted roles='ROLE_SUPER,ROLE_ADMIN'>
                                                                <div class="checkbox checkbox-css">
                                                                    <input class="ck" type="checkbox"
                                                                           name="checkbox-select" value="${cr.uuid}"
                                                                           id="table_checkbox_${cr.uuid}"/>
                                                                    <label for="table_checkbox_${cr.uuid}"></label>
                                                                </div>
                                                            </sec:ifAnyGranted>
                                                        </div>

                                                        <div class="col-9">
                                                            <h4 class="media-heading"
                                                                style="text-align: right">${cr.userName}(${cr.time})</h4>
                                                        </div>
                                                    </div>

                                                    <div class="row">
                                                        <div class="col-3"></div>

                                                        <div class="col-9"><p
                                                                style="text-align: left;float: right;word-break: break-all">${cr.chatContext}</p>
                                                        </div>
                                                    </div>
                                                </div>
                                            </div>
                                        </g:if>
                                        <g:else>
                                            <div class="media media-sm message">
                                                <input type="hidden" value="${cr.uuid}">

                                                <div class="media-body">
                                                    <div class="row">
                                                        <div class="col-1" style="margin-right: -10px">
                                                            <sec:ifAnyGranted roles='ROLE_SUPER,ROLE_ADMIN'>
                                                                <div class="checkbox checkbox-css">
                                                                    <input class="ck" type="checkbox"
                                                                           name="checkbox-select" value="${cr.uuid}"
                                                                           id="table_checkbox_${cr.uuid}"/>
                                                                    <label for="table_checkbox_${cr.uuid}"></label>
                                                                </div>
                                                            </sec:ifAnyGranted>
                                                        </div>

                                                        <div class="col-11" style="padding-left: 0;padding-top: 3px">
                                                            <h4 class="media-heading">${cr.userName}(${cr.time})</h4>
                                                        </div>
                                                    </div>

                                                    <p style="width: 75%;word-break:break-word;padding-left: 30px">${cr.chatContext}</p>
                                                </div>
                                            </div>
                                        </g:else>
                                    </g:each>
                                </div>
                                <!-- 异步输入框 start -->
                                <div class="row">
                                    <sec:ifAnyGranted roles='ROLE_SUPER,ROLE_ADMIN'>
                                        <button id="delete" class="btn btn-sm btn-primary">删除</button>
                                    </sec:ifAnyGranted>
                                    <div class="col-md-auto"></div>
                                    <input type="text" class="form-control col-9" id="chatContext"/>

                                    <div class="col-md-auto"></div>
                                    <button class="btn btn-sm btn-primary fa-song">发送</button>
                                </div>
                                <!-- 输入框 end -->
                                <!-- 当前用户用户名 start -->
                                <input id="userName" type="hidden" value="${userName}">
                                <!-- 当前用户用户名 end -->
                            </div>
                            <!-- end 即时通讯 -->
                            <!-- begin tab-pane -->
                            <div class="tab-pane fade" style="min-height: 600px" id="nav-tab-8">
                                <div class="penhoder" id="penhoder" style="height: 580px" data-scrollbar="true">
                                    <pre>
                                    </pre>
                                </div>
                            </div>
                            <!-- end tab-pane -->
                            <!-- begin tab-pane -->
                            <div class="tab-pane fade" id="nav-tab-9">
                                <sec:ifAnyGranted roles='ROLE_SUPER,ROLE_ADMIN,ROLE_/plan/editConsoleOpen'>
                                    <a href="#" class="btn btn-success" id="begincourt">开庭</a>
                                </sec:ifAnyGranted>
                                <sec:ifAnyGranted roles='ROLE_SUPER,ROLE_ADMIN,ROLE_/plan/editConsoleAdjourn'>
                                    <a href="#" class="btn btn-warning" id="adjournment">休庭</a>
                                </sec:ifAnyGranted>
                                <sec:ifAnyGranted roles='ROLE_SUPER,ROLE_ADMIN,ROLE_/plan/editConsoleClose'>
                                    <a href="#" class="btn btn-danger" id="adjourned">闭庭</a>
                                </sec:ifAnyGranted>
                                <sec:ifAnyGranted roles='ROLE_SUPER,ROLE_ADMIN,ROLE_/plan/uploadConsoleNode'>
                                    <a href="#modal_upload" class="btn btn-info" id="uploadTrialNode"  data-toggle="modal">上传笔录</a>
                                </sec:ifAnyGranted>
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
<div class="modal fade" id="modal_upload">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <h4 class="modal-title">文件上传</h4>
                <button type="button" class="close" data-dismiss="modal" aria-hidden="true">×</button>
            </div>
            <div class="modal-body">
                <div class="htmleaf-container">
                    <div class="container kv-main">
                        <form enctype="multipart/form-data">
                            <input id="file-0a" class="file" type="file" data-min-file-count="1" data-bronse-on-zone-click = "true">
                        </form>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>

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
<asset:javascript src="plan/show.js"/>
<script>
    $(document).ready(function () {
    });
</script>
<!-- ================== END PAGE LEVEL JS ================== -->
</body>
</html>
