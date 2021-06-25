<!DOCTYPE html>
<!--[if IE 8]> <html class="ie8"> <![endif]-->
<!--[if !IE]><!-->
<html>
<!--<![endif]-->
<head>
    <meta charset="utf-8" />
    <title>科技法庭管理系统</title>
    <meta content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=no" name="viewport" />
    <meta content="" name="description" />
    <meta content="" name="author" />

    <!-- ================== BEGIN BASE CSS STYLE ================== -->
    <g:render template="/layouts/base_head"/>
    <!-- ================== END BASE CSS STYLE ================== -->

    <!-- ================== BEGIN PAGE LEVEL STYLE ================== -->
    <asset:stylesheet href="parsley/src/parsley.css"/>
    <asset:stylesheet href="bootstrap-sweetalert/sweetalert.css"/>
    <asset:stylesheet href="jquery-tag-it/css/jquery.tagit.css"/>
    <asset:stylesheet href="index/index.css" rel="stylesheet"/>
    <asset:stylesheet href="court/court.css"/>
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
    <g:if test="${grailsApplication.config.pageVersion == 'v1'}">
        <g:render template="/layouts/base_sidebar" model="[active: 30102]"/>
    </g:if>
    <g:if test="${grailsApplication.config.pageVersion == 'v2'}">
        <g:render template="/layouts/base_sidebar" model="[active: 7100]"/>
    </g:if>
    <!-- end #sidebar -->

    <!-- begin #content -->
    <div id="content" class="content">
        <!-- begin breadcrumb -->
        <ol class="breadcrumb pull-right">
            <li class="breadcrumb-item"><g:link controller="index">首页</g:link></li>
            <li class="breadcrumb-item">配置与管理</li>
            <li class="breadcrumb-item active">法庭设备控制台</li>
        </ol>
        <!-- end breadcrumb -->
        <!-- begin page-header -->
        <h1 class="page-header">配置与管理 <small>法庭设备控制台</small></h1>
        <!-- begin courtroom_id -->
        <input id="courtroom" type="hidden" value="${courtroom.id}"/>
        <input id="courtroom-deviceIp" type="hidden" value="${courtroom.deviceIp}"/>
        <input id="flag" type="hidden" value="${flag}"/>
        <!-- end courtroom_id -->
        <!-- end page-header -->
       

        <!-- begin row -->
        <div class="row">
            <!-- begin col-6 -->
            <div class="col-lg-6">
                <!-- begin panel -->
                <div class="panel panel-inverse" data-sortable-id="ui-general-1">
                    <!-- begin panel-heading -->
                    <div class="panel-heading">
                        <div class="panel-heading-btn">
                            <a href="javascript:;" class="btn btn-xs btn-icon btn-circle btn-default" data-click="panel-expand"><i class="fa fa-expand"></i></a>
                        </div>
                        <h4 class="panel-title">摄像头控制</h4>
                    </div>
                    <!-- end panel-heading -->
                    
                    <!-- begin panel-body -->
                    <div class="panel-body">
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
                        <!-- begin tab-content -->
                        <div class="tab-content">
                            <div class="control-wrapper col-md-6" style="margin-left: 10%;width: 50%">
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
                            <div class="col-md-4" style="margin-left: 10%; transform: translateY(10%)">
                                <div class="col-md-12 panel-body" style="margin-left: 10%;">
                                    <a href="javascript:;" class="btn btn-warning btn-icon btn-circle btn-lg jj-minus"><i class="fa fa-minus"></i></a>
                                    <b class="faPicture">焦距</b>
                                    <a href="javascript:;" class="btn btn-warning btn-icon btn-circle btn-lg jj-plus"><i class="fa fa-plus"></i></a>
                                </div>
                                <div class="col-md-12 panel-body" style="margin-left: 10%;">
                                    <a href="javascript:;" class="btn btn-warning btn-icon btn-circle btn-lg bb-minus"><i class="fa fa-minus"></i></a>
                                    <b class="faPicture">变倍</b>
                                    <a href="javascript:;" class="btn btn-warning btn-icon btn-circle btn-lg bb-plus"><i class="fa fa-plus"></i></a>
                                </div>
                                <div class="col-md-12 panel-body" style="margin-left: 10%;">
                                    <a href="javascript:;" class="btn btn-warning btn-icon btn-circle btn-lg gq-minus"><i class="fa fa-minus"></i></a>
                                    <b class="faPicture">光圈</b>
                                    <a href="javascript:;" class="btn btn-warning btn-icon btn-circle btn-lg gq-plus"><i class="fa fa-plus"></i></a>
                                </div>
                            </div>
                            <div class="col-md-12 panel-body presents" data="${camera.presets}">
                                <p class="faPicture">预置位设置：
                                </p>
                                <select class="form-control selectpicker col-md-4" data-size="10" data-live-search="true" data-style="btn-white" id="cameraPre" name="camera">
                                    <option value="" selected="selected">请选择</option>
                                    <g:each in="${camera.presets}" var="pre" status="i">
                                        <option value="pre${pre.uuid}">${pre.name}</option>
                                    </g:each>
                                </select>
                                <a href="javascript:;" class="btn btn-primary pre-button-commamd-save" style="margin-left: 10px;margin-right: 10px;">保存预置位</a>
                                <a href="javascript:;" class="btn btn-primary pre-button-commamd-call">恢复预置位</a>
                               %{-- <p class="text-center m-b-0">

                                </p>
                                <p class="text-center m-b-0">

                                </p>--}%
                                <!-- end tab-pane -->
                            </div>
                        </div>
                        <!-- end tab-content -->
                    </div>
                </div>
                <!-- end panel -->
    
                <!-- begin panel -->
                <div class="panel panel-inverse" data-sortable-id="ui-general-3">
                    <!-- begin panel-heading -->
                    <div class="panel-heading">
                        <div class="panel-heading-btn">
                            <a href="javascript:;" class="btn btn-xs btn-icon btn-circle btn-default" data-click="panel-expand"><i class="fa fa-expand"></i></a>
                        </div>
                        <h4 class="panel-title">设备控制</h4>
                    </div>
                    <!-- end panel-heading -->
                    <!-- begin panel-body -->
                    <div class="panel-body">
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
                    <!-- end panel-body -->
                </div>
                <!-- end panel -->

                <!-- begin panel -->
                <div class="panel panel-inverse" data-sortable-id="ui-general-5">
                    <!-- begin panel-heading -->
                    <div class="panel-heading">
                        <div class="panel-heading-btn">
                            <a href="javascript:;" class="btn btn-xs btn-icon btn-circle btn-default" data-click="panel-expand"><i class="fa fa-expand"></i></a>
                        </div>
                        <h4 class="panel-title">综合控制</h4>
                    </div>
                    <!-- end panel-heading -->
                    <!-- begin panel-body -->
                    <div class="panel-body">
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
                    <!-- end panel-body -->
                </div>
                <!-- end panel -->
            </div>
            <!-- end col-6 -->
            <!-- begin col-6 -->
            <div class="col-lg-6">
                <!-- begin panel -->
                <div class="panel panel-inverse" data-sortable-id="ui-general-4">
                    <!-- begin panel-heading -->
                    <div class="panel-heading">
                        <div class="panel-heading-btn">
                            <a href="javascript:;" class="btn btn-xs btn-icon btn-circle btn-default" data-click="panel-expand"><i class="fa fa-expand"></i></a>
                        </div>
                        <h4 class="panel-title">输出控制</h4>
                    </div>
                    <!-- end panel-heading -->
                    <!-- begin panel-body -->
                    <div class="panel-body">
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
                    <!-- end panel-body -->
                </div>
                <!-- end panel -->
    
                <!-- begin panel -->
                <div class="panel panel-inverse" data-sortable-id="ui-general-5">
                    <!-- begin panel-heading -->
                    <div class="panel-heading">
                        <div class="panel-heading-btn">
                            <a href="javascript:;" class="btn btn-xs btn-icon btn-circle btn-default" data-click="panel-expand"><i class="fa fa-expand"></i></a>
                        </div>
                        <h4 class="panel-title">红外控制</h4>
                    </div>
                    <!-- end panel-heading -->
                    <!-- begin panel-body -->
                    <div class="panel-body">
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
                    <!-- end panel-body -->
                </div>
                <!-- end panel -->

                <!-- begin panel -->
                <div class="panel panel-inverse" data-sortable-id="ui-general-5">
                    <!-- begin panel-heading -->
                    <div class="panel-heading">
                        <div class="panel-heading-btn">
                            <a href="javascript:;" class="btn btn-xs btn-icon btn-circle btn-default" data-click="panel-expand"><i class="fa fa-expand"></i></a>
                        </div>
                        <h4 class="panel-title">音量控制</h4>
                    </div>
                    <!-- end panel-heading -->
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
                <!-- end panel -->

                <!-- begin panel -->
                <div class="panel panel-inverse" data-sortable-id="ui-general-7">
                    <!-- begin panel-heading -->
                    <div class="panel-heading">
                        <div class="panel-heading-btn">
                            <a href="javascript:;" class="btn btn-xs btn-icon btn-circle btn-default" data-click="panel-expand"><i class="fa fa-expand"></i></a>
                        </div>
                        <h4 class="panel-title">电源控制</h4>
                    </div>
                    <!-- end panel-heading -->
                    <!-- begin panel-body -->
                    <div class="panel-body">
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
                    <!-- end panel-body -->
                </div>
                <!-- end panel -->


        %{--<!-- begin panel -->
        <div class="panel panel-inverse" data-sortable-id="ui-general-5">
            <!-- begin panel-heading -->
            <div class="panel-heading">
                <div class="panel-heading-btn">
                    <a href="javascript:;" class="btn btn-xs btn-icon btn-circle btn-default" data-click="panel-expand"><i class="fa fa-expand"></i></a>
                </div>
                <h4 class="panel-title">综合控制</h4>
            </div>
            <!-- end panel-heading -->
            <!-- begin panel-body -->
            <div class="panel-body">
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
            <!-- end panel-body -->
        </div>
        <!-- end panel -->--}%
            </div>
            <!-- end col-6 -->
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
<asset:javascript src="jquery/jquery-migrate-1.1.0.min.js"/>
<asset:javascript src="bootstrap-sweetalert/sweetalert.js"/>
<asset:javascript src="/ctrl/show.js"/>
<script>
    $(document).ready(function () {
    });
</script>
<!-- ================== END PAGE LEVEL JS ================== -->
</body>
</html>
