<!DOCTYPE html>
<!--[if IE 8]> <html class="ie8" lang="CN"> <![endif]-->
<!--[if !IE]><!-->
<html>
<!--<![endif]-->
<head>
    <meta charset="utf-8"/>
    <title>自动化运维平台</title>
    <meta content="width=device-width, initial-scale=1.0, maximum-scale=1.0, menu-scalable=no" name="viewport"/>
    <meta content="" name="description"/>
    <meta content="" name="author"/>

    <!-- ================== BEGIN BASE CSS STYLE ================== -->
    <g:render template="/layouts/base_head"/>
    <!-- ================== END BASE CSS STYLE ================== -->

    <!-- ================== BEGIN PAGE LEVEL STYLE ================== -->
    <asset:stylesheet href="parsley/src/parsley.css"/>
    <asset:stylesheet href="bootstrap-sweetalert/sweetalert.css"/>
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
    <g:render template="/layouts/base_sidebar" model="[active: 30108]"/>
    <!-- end #sidebar -->

    <!-- begin #content -->
    <div id="content" class="content">

        <!-- begin breadcrumb -->
        <ol class="breadcrumb pull-right">
            <li class="breadcrumb-item"><g:link controller="index">首页</g:link></li>
            <li class="breadcrumb-item"><g:link controller="menu" action="list">系统功能管理</g:link></li>
            <li class="breadcrumb-item active">修改系统功能信息</li>
        </ol>
        <!-- end breadcrumb -->

        <!-- begin page-header -->
        <h1 class="page-header">系统功能管理 <small>修改系统功能信息</small></h1>
        <!-- end page-header -->

        <!-- begin row -->
        <div class="row">
            <div class="col-lg-12">
                <!-- begin panel -->
                <div class="panel panel-inverse">
                    <!-- begin panel-heading -->
                    <div class="panel-heading">
                        <div class="panel-heading-btn">
                            <a href="javascript:void(0);" class="btn btn-xs btn-icon btn-circle btn-default"
                               data-click="panel-expand"><i class="fa fa-expand"></i></a>
                        </div>
                        <h4 class="panel-title">修改系统功能信息</h4>
                    </div>
                    <!-- end panel-heading -->
                    <!-- begin panel-body -->
                    <div class="panel-body">
                        <!-- begin wizard-form -->
                        <form id="" name="form-menuedit" class="form-control-with-bg form-horizontal"
                              data-parsley-validate="true">
                            <!-- begin row -->
                            <div class="row">
                                <input type="hidden" name="menuId" value="${menu.id}"/>
                                <!-- begin col-8 -->
                                <div class="col-md-8 offset-md-2">
                                    <legend class="no-border f-w-700 p-b-0 m-t-0 m-b-20 f-s-16 text-inverse">修改系统功能信息请修改以下内容</legend>

                                    <!-- begin form-group--功能名称 -->
                                    <div class="form-group row m-b-10">
                                        <label class="col-md-3 col-form-label text-md-right" for="name">名称<span
                                                class="text-danger">*</span></label>

                                        <div class="col-md-6">
                                            <input type="text" name="name" id="name" class="form-control"
                                                   value="${menu.name}" data-parsley-required="true"
                                                   data-parsley-required-message="此项不能为空"/>
                                        </div>
                                    </div>
                                    <!-- end form-group--功能名称 -->
                                    <!-- begin form-group--功能路径 -->
                                    <div class="form-group row m-b-10">
                                        <label class="col-md-3 col-form-label text-md-right"
                                               for="name">路径 &nbsp;&nbsp;</label>

                                        <div class="col-md-6">
                                            <input type="text" name="url" id="url" class="form-control"
                                                   value="${menu.url}"/>
                                        </div>
                                    </div>

                                    <!-- end form-group--功能路径 -->
                                    <!-- begin form-group--类型 -->
                                    <div class="form-group row m-b-10">
                                        <label class="col-md-3 col-form-label text-md-right">类型<span
                                                class="text-danger">*</span></label>

                                        <div class="col-md-6">
                                            <select class="form-control selectpicker" data-size="10"
                                                    data-style="btn-white" id="type" name="type"
                                                    data-parsley-required="true" data-parsley-required-message="此项不能为空">
                                                <option value="">请选择</option>
                                                <option value="0"
                                                        <g:if test="${menu.type == 0}">selected</g:if>>目录</option>
                                                <option value="1"
                                                        <g:if test="${menu.type == 1}">selected</g:if>>菜单</option>
                                                <option value="2"
                                                        <g:if test="${menu.type == 2}">selected</g:if>>按钮</option>
                                            </select>
                                        </div>
                                    </div>
                                    <!-- end form-group--类型 -->
                                    <!-- begin form-group -->
                                    <div class="form-group row m-b-10">
                                        <label class="col-md-3 col-form-label text-md-right">上级类型 &nbsp;&nbsp;</label>

                                        <div class="col-md-6">
                                            <select class="form-control selectpicker" data-size="10"
                                                    data-live-search="true" data-style="btn-white" id="parentId"
                                                    name="parentId">
                                                <option value="0">请选择</option>
                                                <g:each in="${menuList}" var="mu" status="i">
                                                    <option value="${mu.id}"
                                                            <g:if test="${mu.id == menu.parentId}">selected</g:if>>
                                                        ${mu.name}
                                                    </option>
                                                </g:each>
                                            </select>
                                        </div>
                                    </div>
                                    <!-- end form-group -->

                                    <!-- begin form-group--提交按钮 -->
                                    <div class="form-group row m-b-10">
                                        <label class="col-md-3 col-form-label">&nbsp;</label>

                                        <div class="col-md-6">
                                            <input type="submit" class="btn btn-primary" value="修改"/>
                                        </div>
                                    </div>
                                    <!--end form-group--提交按钮 -->
                                </div>
                                <!-- end col-8 -->
                            </div>
                            <!-- end row -->
                        </form>
                        <!-- end wizard-form -->
                    </div>
                </div>
                <!-- end panel -->
            </div>
        </div>
        <!--end row-->
    </div>
    <!-- end #content -->

    <!-- begin scroll to top btn -->
    <g:render template="/layouts/base_topbtn"/>
    <!-- end scroll to top btn -->
</div>
<!-- end page container -->
<g:render template="/layouts/base_bottom"/>
<!-- ================== BEGIN PAGE LEVEL JS ================== -->
<asset:javascript src="parsley/dist/parsley.js"/>
<asset:javascript src="jquery-form/jquery.form.js"/>
<asset:javascript src="bootstrap-sweetalert/sweetalert.js"/>
<asset:javascript src="jquery/jquery-migrate-1.1.0.min.js"/>
<asset:javascript src="bootstrap-daterangepicker/moment.min.js"/>
<asset:javascript src="bootstrap-select/bootstrap-select.min.js"/>
<asset:javascript src="jquery-tag-it/js/tag-it.min.js"/>
<asset:javascript src="select2/dist/js/select2.min.js"/>
<asset:javascript src="menu/edit.js"/>
<script>
    $(document).ready(function () {
    });
</script>
<!-- ================== END PAGE LEVEL JS ================== -->
</body>
</html>
