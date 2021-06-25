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
    <asset:stylesheet href="bootstrap-eonasdan-datetimepicker/build/css/bootstrap-datetimepicker.min.css"/>
    <asset:stylesheet href="bootstrap-select/bootstrap-select.min.css"/>
    <asset:stylesheet href="jquery-tag-it/css/jquery.tagit.css"/>
    <asset:stylesheet href="select2/dist/css/select2.min.css"/>
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
    <g:render template="/layouts/base_sidebar" model="[active: 30110]"/>
    <!-- end #sidebar -->

    <!-- begin #content -->
    <div id="content" class="content">
        <!-- begin breadcrumb -->
        <ol class="breadcrumb pull-right">
            <li class="breadcrumb-item"><g:link controller="index">首页</g:link></li>
            <li class="breadcrumb-item">
                业务管理
            </li>
            <li class="breadcrumb-item active">
                基础信息管理-羁押室设备管理
            </li>
        </ol>
        <!-- end breadcrumb -->
        <!-- begin page-header -->
        <h1 class="page-header">
            配置与管理
            <small>修改羁押室设备信息</small>
        </h1>
        <!-- end page-header -->
        <div class="panel panel-inverse">
            <div class="panel-heading">
                <div class="panel-heading-btn">
                    <a href="javascript:void(0);" class="btn btn-xs btn-icon btn-circle btn-default" data-click="panel-expand"><i class="fa fa-expand"></i></a>
                </div>
                <h4 class="panel-title">修改羁押室设备信息</h4>
            </div>

            <div class="panel-body">
                <g:form controller="detentionDevice" action="editSave" id="${device.id}" method="POST" data-parsley-validate="true">
                    <input id="detentionDevice" type="hidden" name="id" value="${device.id}"/>
                    <div class="col-md-8 offset-md-2">
                        <!-- begin form-group row -->
                        <div class="form-group row m-b-10">
                            <label class="col-md-3 text-md-right col-form-label">设备名称</label>
                            <div class="col-md-6">
                                <input type="text" class="form-control m-b-5" name="deviceName" value="${device.deviceName}"/>
                            </div>
                        </div>
                        <!-- end form-group row -->
                        <!-- begin form-group row -->
                        <div class="form-group row m-b-10">
                            <label class="col-md-3 text-md-right col-form-label">设备地址</label>
                            <div class="col-md-6">
                                <input type="text" class="form-control m-b-5" name="ip" value="${device.ip}" data-parsley-required="true" data-parsley-required-message="此项不能为空"/>
                            </div>
                        </div>
                        <!-- end form-group row -->
                        <!-- begin form-group row -->
                        <div class="form-group row m-b-10">
                            <label class="col-md-3 text-md-right col-form-label">设备端口</label>
                            <div class="col-md-6">
                                <input type="text" class="form-control m-b-5" name="port" value="${device.port}" data-parsley-required="true" data-parsley-required-message="此项不能为空"/>
                            </div>
                        </div>
                        <!-- end form-group row -->
                        <!-- begin form-group row -->
                        <div class="form-group row m-b-10">
                            <label class="col-md-3 text-md-right col-form-label">软件版本</label>
                            <div class="col-md-6">
                                <input type="text" class="form-control m-b-5" name="ver" value="${device.ver}"/>
                            </div>
                        </div>
                        <!-- end form-group row -->
                        <!-- begin form-group row -->
                        <div class="form-group row m-b-10">
                            <label class="col-md-3 text-md-right col-form-label">设备状态</label>
                            <div class="col-md-6">
                                <select name="status" class="form-control m-b-5">
                                    <option value="0" <g:if test="${device.status == 0}">selected</g:if>>正常</option>
                                    <option value="1" <g:if test="${device.status == 1}">selected</g:if>>关闭</option>
                                    <option value="2" <g:if test="${device.status == 2}">selected</g:if>>损坏</option>
                                    <option value="3" <g:if test="${device.status == 3}">selected</g:if>>维护</option>
                                    <option value="4" <g:if test="${device.status == 4}">selected</g:if>>异常</option>
                                </select>
                            </div>
                        </div>
                        <!-- end form-group row -->
                        <!-- begin form-group row -->
                        <div class="form-group row m-b-10">
                            <label class="col-md-3 text-md-right col-form-label">设备状态</label>
                            <div class="col-md-6">
                                <select class="form-control selectpicker" data-size="10" data-live-search="true" data-style="btn-white" name="courtroom">
                                    <option value="">请选择</option>
                                    <g:each in="${courtroomList}" var="courtroom" status="i">
                                        <option value="${courtroom.id}"<g:if test="${device.courtroom?.id == courtroom.id}"> selected</g:if>>${courtroom.name}</option>
                                    </g:each>
                                </select>
                            </div>
                        </div>
                        <!-- end form-group row -->
                        <div class="form-group row m-b-10">
                            <label class="col-md-3 text-md-right col-form-label"></label>
                            <div class="col-md-6">
                                <button type="submit" class="btn btn-sm btn-primary m-r-5">提交</button>
                            </div>
                        </div>
                    </div>
                </g:form>
            </div>
        </div>
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
<asset:javascript src="parsley/dist/parsley.js"/>
<asset:javascript src="jquery-form/jquery.form.js"/>
<asset:javascript src="bootstrap-sweetalert/sweetalert.js"/>
<asset:javascript src="bootstrap-daterangepicker/moment.min.js"/>
<asset:javascript src="bootstrap-select/bootstrap-select.min.js"/>
<asset:javascript src="bootstrap-eonasdan-datetimepicker/build/js/bootstrap-datetimepicker.min.js"/>
<asset:javascript src="jquery-tag-it/js/tag-it.min.js"/>
<asset:javascript src="select2/dist/js/select2.min.js"/>
<asset:javascript src="detentionDevice/edit.js"/>
<script>
    $(document).ready(function () {
    });
</script>
<!-- ================== END PAGE LEVEL JS ================== -->
</body>
</html>
