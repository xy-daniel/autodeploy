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
    <asset:stylesheet href="parsley/src/parsley.css"/>
    <asset:stylesheet href="jquery-tag-it/css/jquery.tagit.css"/>
    <asset:stylesheet href="court/indexPlan.css" rel="stylesheet"/>
    <asset:stylesheet href="court/courtPlan.css"/>
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
    <g:render template="/layouts/base_sidebar" model="[active: 20100]"/>
    <!-- end #sidebar -->

    <!-- begin #content -->
    <div id="content" class="content">
        <!-- begin breadcrumb -->
        <ol class="breadcrumb pull-right">
            <li class="breadcrumb-item"><g:link controller="index">首页</g:link></li>
            <li class="breadcrumb-item">
                <g:link controller="plan" action="list">
                    <g:if test="${grailsApplication.config.pageVersion == 'v1'}">庭审管理</g:if>
                    <g:if test="${grailsApplication.config.pageVersion == 'v2'}">庭审预告</g:if>
                </g:link>
            </li>
            <li class="breadcrumb-item active">案件详情</li>
        </ol>
        <!-- end breadcrumb -->
        <!-- begin page-header -->
        <h1 class="page-header">
            <g:if test="${grailsApplication.config.pageVersion == 'v1'}">庭审管理</g:if>
            <g:if test="${grailsApplication.config.pageVersion == 'v2'}">庭审预告</g:if>
            <small>案件详情</small>
        </h1>
        <input id="courtroom" type="hidden" value="${courtroom.id}"/>
        <input id="courtroom-deviceIp" type="hidden" value="${courtroom.deviceIp}"/>
        <input id="flag" type="hidden" value="${flag}"/>
        <!-- end page-header -->
        <!-- begin row -->
        <div class="row plaan-info" trialId="${data.trialId}" planId="${data.planId}" allowPlay="${data.allowPlay}">
            <div class="col-lg-8">
                <ul class="nav nav-tabs nav-tabs-inverse plan-show-chn">
                    <g:each in="${chnList}" var="chn" status="i">
                        <li class="nav-item"><a href="#" data-toggle="tab" data="${chn.number}"
                                                class="nav-link plan-tab-chn <g:if
                                                        test="${i == 0}">active</g:if>">${chn.name}</a></li>
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
                            <a href="javascript:;" class="btn btn-xs btn-icon btn-circle btn-success"
                               data-click="panel-expand"><i class="fa fa-expand"></i></a>
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
                                <li class="nav-item"><a href="#nav-tab-7" data-toggle="tab"
                                                        class="nav-link active">案件信息</a></li>
                                <li class="nav-item"><a href="#nav-tab-8" data-toggle="tab" class="nav-link">笔录</a></li>
                                <li class="nav-item"><a href="#nav-tab-10" data-toggle="tab"
                                                        class="nav-link connect">通讯</a></li>
                                <sec:ifAnyGranted roles='ROLE_SUPER,ROLE_ADMIN,ROLE_CONTROL'>
                                    <li class="nav-item"><a href="#nav-tab-9" data-toggle="tab"
                                                            class="nav-link">庭审控制</a></li>
                                </sec:ifAnyGranted>
                                <sec:ifAnyGranted roles='ROLE_SUPER,ROLE_ADMIN'>
                                    <li class="nav-item"><a href="#nav-tab-11" data-toggle="tab"
                                                            class="nav-link">法庭设备控制</a></li>
                                </sec:ifAnyGranted>
                            </ul>
                        </div>
                    </div>

                    <div class="plan-show-info">
                        <div class="tab-content plan-show-info-tab-content">
                            <!-- begin tab-pane -->
                            <div class="tab-pane fade active show" id="nav-tab-7">
                                <h3>${data.caseArchives} - ${caseList.size() != 0 ? PlanStatus.getString(data.status) + "(并案)" : PlanStatus.getString(data.status)}</h3>
                                <g:if test="${caseList.size() != 0}">

                                    <div class="m-b-10 m-t-10">
                                        <h4>已并案案件(<span class="text-red-darker">请选择已并案案号查询案件详情</span>)</h4>

                                        <div class="width-300">
                                            <select class="form-control selectpicker" id="caselist" data-size="10"
                                                    data-live-search="true" data-style="btn-white">
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
                                        <dd>${data.secretary ? data.secretary.name : "无数据"}</dd>
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
                                    <a href="#modal_upload" class="btn btn-info" id="uploadTrialNode"
                                       data-toggle="modal">上传笔录</a>
                                </sec:ifAnyGranted>
                            </div>
                            <!-- end tab-pane -->
                            <!-- begin tab-pane -->
                            <div class="tab-pane fade" id="nav-tab-11">
                                <div class="col-lg-12">
                                    <!-- begin #accordion -->
                                    <div id="accordion" class="card-accordion">
                                        <!-- begin card -->
                                        <div class="card">
                                            <div class="card-header bg-black text-white pointer-cursor" data-toggle="collapse" data-target="#collapseOne">
                                                摄像头控制
                                            </div>
                                            <div id="collapseOne" class="collapse show" data-parent="#accordion">
                                                <div class="card-body">
                                                    <!-- begin nav-tabs -->
                                                    <ul class="nav nav-tabs camera-chn">
                                                        <g:each in="${camera.position}" var="cam" status="i">
                                                            <g:if test="${cam.visible == "1"}">
                                                                <li class="nav-items" >
                                                                    <a href="#" data-toggle="tab" data="${cam.codeDown}" class="nav-link <g:if test="${i == 0}">active</g:if>">
                                                                        <span class="d-sm-block d-none">${cam.name}</span>
                                                                    </a>
                                                                </li>
                                                            </g:if>
                                                        </g:each>
                                                    </ul>
                                                    <!-- end nav-tabs -->
                                                    <div class="tab-content">
                                                        <div class="control-wrapper col-md-col-6" style="">
                                                            <div class="control-btn control-top" data="${camera.buttons}">
                                                                <i class="fa fa-chevron-up"></i>
                                                                <div class="control-inner-btn control-inner"></div>
                                                            </div>
                                                            <div class="control-btn control-left">
                                                                <i class="fa fa-chevron-left"></i>
                                                                <div class="control-inner-btn control-inner"></div>
                                                            </div>
                                                            <div class="control-btn control-bottom">
                                                                <i class="fa fa-chevron-down"></i>
                                                                <div class="control-inner-btn control-inner"></div>
                                                            </div>
                                                            <div class="control-btn control-right">
                                                                <i class="fa fa-chevron-right"></i>
                                                                <div class="control-inner-btn control-inner"></div>
                                                            </div>
                                                            <div class="control-round">
                                                                <div class="control-round-inner">
                                                                    %{--                                    <i class="fa fa-pause-circle"></i>--}%
                                                                </div>
                                                            </div>
                                                        </div>
                                                        <div class="col-md-col-12 control-wrapper">
                                                            <div class="col-md-col-12 panel-body">
                                                                <a href="javascript:;" class="btn btn-warning btn-icon btn-circle btn-lg jj-minus"><i class="fa fa-minus"></i></a>
                                                                <b class="faPicture">焦距</b>
                                                                <a href="javascript:;" class="btn btn-warning btn-icon btn-circle btn-lg jj-plus"><i class="fa fa-plus"></i></a>
                                                            </div>
                                                            <div class="col-md-col-12 panel-body">
                                                                <a href="javascript:;" class="btn btn-warning btn-icon btn-circle btn-lg bb-minus"><i class="fa fa-minus"></i></a>
                                                                <b class="faPicture">变倍</b>
                                                                <a href="javascript:;" class="btn btn-warning btn-icon btn-circle btn-lg bb-plus"><i class="fa fa-plus"></i></a>
                                                            </div>
                                                            <div class="col-md-col-12 panel-body">
                                                                <a href="javascript:;" class="btn btn-warning btn-icon btn-circle btn-lg gq-minus"><i class="fa fa-minus"></i></a>
                                                                <b class="faPicture">光圈</b>
                                                                <a href="javascript:;" class="btn btn-warning btn-icon btn-circle btn-lg gq-plus"><i class="fa fa-plus"></i></a>
                                                            </div>
                                                        </div>
                                                        <div class="col-md-col-12 panel-body presents" data="${camera.presets}">
                                                            <p class="faPicture">预置位设置：
                                                            </p>
                                                            <select class="form-control selectpicker col-md-col-4" data-size="10" data-live-search="true" data-style="btn-white" id="cameraPre" name="camera">
                                                                <option value="" selected="selected">请选择</option>
                                                                <g:each in="${camera.presets}" var="pre" status="i">
                                                                    <option value="pre${pre.uuid}">${pre.name}</option>
                                                                </g:each>
                                                            </select>
                                                            <a href="javascript:;" class="btn btn-primary pre-button-commamd-save" style="margin-left: 10px;margin-right: 10px;">保存预置位</a>
                                                            <a href="javascript:;" class="btn btn-primary pre-button-commamd-call">恢复预置位</a>
                                                        </div>
                                                    </div>
                                                </div>
                                            </div>
                                        </div>
                                        <!-- end card -->
                                        <!-- begin card -->
                                        <div class="card">
                                            <div class="card-header bg-black text-white pointer-cursor collapsed" data-toggle="collapse" data-target="#collapseTwo">
                                                电源控制
                                            </div>
                                            <div id="collapseTwo" class="collapse" data-parent="#accordion">
                                                <div class="card-body">
                                                    <!-- begin nav-tabs -->
                                                    <ul class="nav nav-tabs powerNew-chn" style="display: none;">
                                                        <g:each in="${powerNew}" var="pow" status="i">
                                                            <g:if test="${pow.visible == "1"}">
                                                                <li class="nav-items" >
                                                                    <a href="#pow${pow.uuid.substring(0,16)}" data-toggle="tab" data="${pow.codeDown}" class="nav-link <g:if test="${i == 0}">active</g:if>">
                                                                        <span class="d-sm-block d-none">${pow.name}</span>
                                                                    </a>
                                                                </li>
                                                            </g:if>
                                                        </g:each>
                                                    </ul>
                                                    <!-- end nav-tabs -->
                                                    <!-- begin tab-content -->
                                                    <div class="tab-content">
                                                    <!-- begin tab-pane -->
                                                        <g:each in="${powerNew}" var="pow" status="i">
                                                            <g:if test="${pow.visible == "1"}">
                                                                <div class="tab-pane fade <g:if test="${i == 0}">active show</g:if> " id="pow${pow.uuid.substring(0,16)}">
                                                                    <p class="text-center m-b-0">
                                                                        <g:each in="${pow.buttons}" var="but" status="n">
                                                                            <a href="javascript:;" class="btn btn-primary powNew-button-commamd" data="${but.codeDown}">${but.name}</a>
                                                                        </g:each>
                                                                    </p>
                                                                </div>
                                                            </g:if>
                                                        </g:each>
                                                    <!-- end tab-pane -->
                                                    </div>
                                                    <!-- end tab-content -->
                                                </div>
                                            </div>
                                        </div>
                                        <!-- end card -->
                                        <!-- begin card -->
                                        <div class="card">
                                            <div class="card-header bg-black text-white pointer-cursor collapsed" data-toggle="collapse" data-target="#collapseThree">
                                                输出控制
                                            </div>
                                            <div id="collapseThree" class="collapse" data-parent="#accordion">
                                                <div class="card-body">
                                                    <!-- begin nav-tabs -->
                                                    <ul class="nav nav-tabs output-chn">
                                                        <g:each in="${outMatrix}" var="outMat" status="i">
                                                            <g:if test="${outMat.visible == "1"}">
                                                                <li class="nav-items" >
                                                                    <a href="#" data-toggle="tab" data="${outMat.codeDown}" class="nav-link <g:if test="${i == 0}">active</g:if>">
                                                                        <span class="d-sm-block d-none">${outMat.name}</span>
                                                                    </a>
                                                                </li>
                                                            </g:if>
                                                        </g:each>
                                                    </ul>
                                                    <!-- end nav-tabs -->
                                                    <!-- begin tab-content -->
                                                    <div class="tab-content row">
                                                        <g:each in="${videoMatrix}" var="video" status="i">
                                                            <g:if test="${video.visible == "1"}">
                                                                <div class="tab-pane fade active show">
                                                                    <p class="text-center m-b-0">
                                                                        <div class="form-check form-check-inline checkbox checkbox-css">
                                                                            <input class="form-check-input" type="radio" id="${video.codeDown}" name="checkRole" value="${video.codeDown}">
                                                                <label class="form-check-label" for="${video.codeDown}">${video.name}</label>
                                                                </div>
                                                            </p>
                                                        </div>
                                                            </g:if>
                                                        </g:each>
                                                        <g:each in="${vgaMatrix}" var="vga" status="i">
                                                            <g:if test="${vga.visible == "1"}">
                                                                <div class="tab-pane fade active show">
                                                                    <p class="text-center m-b-0">
                                                                        <div class="form-check form-check-inline checkbox checkbox-css">
                                                                            <input class="form-check-input" type="radio" id="${vga.codeDown}" name="checkRole" value="${vga.codeDown}">
                                                                <label class="form-check-label" for="${vga.codeDown}">${vga.name}</label>
                                                                </div>
                                                            </p>
                                                        </div>
                                                            </g:if>
                                                        </g:each>
                                                    </div>
                                                    <!-- end tab-content -->
                                                </div>
                                            </div>
                                        </div>
                                        <!-- end card -->
                                        <!-- begin card -->
                                        <div class="card">
                                            <div class="card-header bg-black text-white pointer-cursor collapsed" data-toggle="collapse" data-target="#collapseFour">
                                                设备控制
                                            </div>
                                            <div id="collapseFour" class="collapse" data-parent="#accordion">
                                                <div class="card-body">
                                                    <!-- begin nav-tabs -->
                                                    <ul class="nav nav-tabs equipment-chn">
                                                        <g:each in="${power}" var="pow" status="i">
                                                            <g:if test="${pow.visible == "1"}">
                                                                <li class="nav-items" >
                                                                    <a href="#pow${pow.uuid.substring(0,16)}" data-toggle="tab" data="${pow.codeDown}" class="nav-link <g:if test="${i == 0}">active</g:if>">
                                                                        <span class="d-sm-block d-none">${pow.name}</span>
                                                                    </a>
                                                                </li>
                                                            </g:if>
                                                        </g:each>
                                                    </ul>
                                                    <!-- end nav-tabs -->
                                                    <!-- begin tab-content -->
                                                    <div class="tab-content">
                                                    <!-- begin tab-pane -->
                                                        <g:each in="${power}" var="pow" status="i">
                                                            <g:if test="${pow.visible == "1"}">
                                                                <div class="tab-pane fade <g:if test="${i == 0}">active show</g:if> " id="pow${pow.uuid.substring(0,16)}">
                                                                    <p class="text-center m-b-0">
                                                                        <g:each in="${pow.buttons}" var="but" status="n">
                                                                            <a href="javascript:;" class="btn btn-primary pow-button-commamd" data="${but.codeDown}">${but.name}</a>
                                                                        </g:each>
                                                                    </p>
                                                                </div>
                                                            </g:if>
                                                        </g:each>
                                                    <!-- end tab-pane -->
                                                    </div>
                                                    <!-- end tab-content -->
                                                </div>
                                            </div>
                                        </div>
                                        <!-- end card -->
                                        <!-- begin card -->
                                        <div class="card">
                                            <div class="card-header bg-black text-white pointer-cursor collapsed" data-toggle="collapse" data-target="#collapseFive">
                                                综合控制
                                            </div>
                                            <div id="collapseFive" class="collapse" data-parent="#accordion">
                                                <div class="card-body">
                                                    <!-- begin tab-content -->
                                                    <div class="tab-content">
                                                        <g:each in="${total}" var="to" status="i">
                                                            <g:if test="${to.visible == "1"}">
                                                                <a href="javascript:;" class="btn btn-primary total-button-commamd" data="${to.codeDown}">${to.name}</a>
                                                            </g:if>
                                                        </g:each>
                                                    </div>
                                                    <!-- end tab-content -->
                                                </div>
                                            </div>
                                        </div>
                                        <!-- end card -->
                                        <!-- begin card -->
                                        <div class="card">
                                            <div class="card-header bg-black text-white pointer-cursor collapsed" data-toggle="collapse" data-target="#collapseSix">
                                                红外控制
                                            </div>
                                            <div id="collapseSix" class="collapse" data-parent="#accordion">
                                                <div class="card-body">
                                                    <!-- begin nav-tabs -->
                                                    <ul class="nav nav-tabs infrared-chn">
                                                        <g:each in="${irctrl}" var="irc" status="i">
                                                            <g:if test="${irc.visible == "1"}">
                                                                <li class="nav-items" >
                                                                    <a href="#irc${irc.uuid.substring(0,16)}" data-toggle="tab" data="${irc.codeDown}" class="nav-link <g:if test="${i == 0}">active</g:if>">
                                                                        <span class="d-sm-block d-none">${irc.name}</span>
                                                                    </a>
                                                                </li>
                                                            </g:if>
                                                        </g:each>
                                                    </ul>
                                                    <!-- end nav-tabs -->
                                                    <!-- begin tab-content -->
                                                    <div class="tab-content">
                                                        <g:each in="${irctrl}" var="irc" status="i">
                                                            <g:if test="${irc.visible == "1"}">
                                                                <div class="tab-pane fade <g:if test="${i == 0}">active show</g:if> " id="irc${irc.uuid.substring(0,16)}">
                                                                    <p class="text-center m-b-0">
                                                                        <g:each in="${irc.buttons}" var="but" status="n">
                                                                            <a href="javascript:;" class="btn btn-primary irc-button-commamd" data="${but.codeDown}">${but.name}</a>
                                                                        </g:each>
                                                                    </p>
                                                                </div>
                                                            </g:if>
                                                        </g:each>
                                                    </div>
                                                    <!-- end tab-content -->
                                                </div>
                                            </div>
                                        </div>
                                        <!-- end card -->
                                        <!-- begin card -->
                                        <div class="card">
                                            <div class="card-header bg-black text-white pointer-cursor collapsed" data-toggle="collapse" data-target="#collapseSeven">
                                                音量控制
                                            </div>
                                            <div id="collapseSeven" class="collapse" data-parent="#accordion">
                                                <div class="card-body">
                                                    <!-- begin panel-body -->
                                                    <div class="panel-body">
                                                        <!-- begin tab-content -->
                                                        <div class="tab-content">
                                                            <g:each in="${soundGroup}" var="group" status="i">
                                                                <p class="faPicture" style="margin-top: 20px;margin-bottom: 2px;margin-left: 0px">
                                                                    音量控制${group}组：
                                                                </p>
                                                                <g:each in="${soundMatrix}" var="sound" status="n">
                                                                    <g:if test="${sound.visible == "1" && group == sound?.group?.split("_")[0]}">
                                                                        <a href="javascript:;" class="btn btn-primary sound-button-commamd" data="${sound.codeDown}">${sound.name}</a>
                                                                    </g:if>
                                                                </g:each>
                                                            </g:each>
                                                        </div>
                                                        <!-- end tab-content -->
                                                    </div>
                                                    <!-- end panel-body -->
                                                </div>
                                            </div>
                                        </div>
                                        <!-- end card -->
                                    </div>
                                    <!-- end #accordion -->
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
                            <input id="file-0a" class="file" type="file" data-min-file-count="1"
                                   data-bronse-on-zone-click="true">
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
<asset:javascript src="jquery/jquery-migrate-1.1.0.min.js"/>
<asset:javascript src="/ctrl/show.js"/>
<script>
    $(document).ready(function () {
    });
</script>
<!-- ================== END PAGE LEVEL JS ================== -->
</body>
</html>
