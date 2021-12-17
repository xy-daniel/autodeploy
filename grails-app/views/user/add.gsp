<!DOCTYPE html>
<!--[if IE 8]> <html class="ie8" lang="CN"> <![endif]-->
<!--[if !IE]><!-->
<html>
<!--<![endif]-->
<head>
    <meta charset="utf-8"/>
    <title>自动化运维平台</title>
    <meta content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=no" name="viewport"/>
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
    <g:render template="/layouts/base_sidebar" model="[active: 50002]"/>
    <!-- end #sidebar -->

    <!-- begin #content -->
    <div id="content" class="content">

        <!-- begin breadcrumb -->
        <ol class="breadcrumb pull-right">
            <li class="breadcrumb-item"><g:link controller="index">首页</g:link></li>
            <li class="breadcrumb-item"><g:link controller="user" action="list">用户管理</g:link></li>
            <li class="breadcrumb-item active">添加用户</li>
        </ol>
        <!-- end breadcrumb -->

        <!-- begin page-header -->
        <h1 class="page-header">用户管理 <small>添加用户</small></h1>
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
                        <h4 class="panel-title">添加用户</h4>
                    </div>
                    <!-- end panel-heading -->
                    <!-- begin panel-body -->
                    <div class="panel-body">
                        <!-- begin wizard-form -->
                        <form action="${createLink(uri: '/')}user/addSave" id="form-useradd" name="form-useradd"
                              class="form-control-with-bg form-horizontal" data-parsley-validate="true">
                            <!-- begin row -->
                            <div class="row">
                                <!-- begin col-8 -->
                                <div class="col-md-8 offset-md-2">
                                    <legend class="no-border f-w-700 p-b-0 m-t-0 m-b-20 f-s-16 text-inverse">添加用户请填写以下内容</legend>
                                    <!-- begin form-group--用户名称 -->
                                    <div class="form-group row m-b-10">
                                        <label class="col-md-3 col-form-label text-md-right" for="username">用户账号 <span
                                                class="text-danger">*</span></label>

                                        <div class="col-md-6">
                                            <input type="text" name="username" id="username" class="form-control"
                                                   data-parsley-remote data-parsley-remote-validator='checkusername'
                                                   data-parsley-remote-message="输入的账号已注册" data-parsley-required="true"
                                                   data-parsley-required-message="此项不能为空"/>
                                        </div>
                                    </div>
                                    <!-- end form-group--用户名称 -->
                                    <!-- begin form-group--用户密码 -->
                                    <div class="form-group row m-b-10">
                                        <label class="col-md-3 col-form-label text-md-right" for="pwd">用户密码 <span
                                                class="text-danger">*</span></label>

                                        <div class="col-md-6">
                                            <input type="text" name="pwd" id="pwd" class="form-control"
                                                   data-parsley-required="true" data-parsley-length="[6, 10]"
                                                   data-parsley-required-message="此项不能为空"
                                                   data-parsley-length-message="请输入6-10位密码"/>
                                        </div>
                                    </div>
                                    <!-- end form-group--用户密码 -->
                                    <!-- begin form-group--昵称 -->
                                    <div class="form-group row m-b-10">
                                        <label class="col-md-3 col-form-label text-md-right" for="realName">用户姓名 <span
                                                class="text-danger">*</span></label>

                                        <div class="col-md-6">
                                            <input type="text" name="realName" id="realName" class="form-control"
                                                   data-parsley-required="true" data-parsley-required-message="此项不能为空"/>
                                        </div>
                                    </div>
                                    <!-- end form-group--昵称 -->
                                    <!-- begin form-group--是否启用 -->
                                    <div class="form-group row m-b-10">

                                        <label class="col-md-3 col-form-label text-md-right">是否启用 <span
                                                class="text-danger">*</span></label>

                                        <div class="col-md-6">
                                            <div class="radio radio-css radio-inline">
                                                <input type="radio" name="enabled" id="true1" value="true" checked/>
                                                <label for="true1">是</label>
                                            </div>

                                            <div class="radio radio-css radio-inline">
                                                <input type="radio" name="enabled" id="false1" value="false"/>
                                                <label for="false1">否</label>
                                            </div>
                                        </div>
                                    </div>
                                    <!-- end form-group--是否启用 -->
                                    <!-- begin form-group--是否锁定 -->
                                    <div class="form-group row m-b-10">
                                        <label class="col-md-3 col-form-label text-md-right">是否锁定 <span
                                                class="text-danger">*</span></label>

                                        <div class="col-md-6">
                                            <div class="radio radio-css radio-inline">
                                                <input type="radio" name="accountLocked" id="true2" value="true"/>
                                                <label for="true2">是</label>
                                            </div>

                                            <div class="radio radio-css radio-inline">
                                                <input type="radio" name="accountLocked" id="false2" value="false"
                                                       checked/>
                                                <label for="false2">否</label>
                                            </div>
                                        </div>
                                    </div>
                                    <!-- end form-group--是否锁定 -->
                                    <!-- begin form-group--用户权限 -->
                                    <div class="form-group row m-b-10">
                                        <label class="col-md-3 col-form-label text-md-right">用户权限 <span
                                                class="text-danger">*</span></label>

                                        <div class="col-md-8">
                                            <g:each in="${roleList}" var="role" status="i">
                                                <g:if test="${role.authority != 'ROLE_SUPER'}">
                                                    <div class="form-check form-check-inline checkbox checkbox-css">
                                                        <input class="form-check-input" type="checkbox"
                                                               id="roleId_${role.id}" name="checkRole"
                                                               value="${role.id}">
                                                        <label class="form-check-label"
                                                               for="roleId_${role.id}">${role.remark}</label>
                                                    </div>
                                                </g:if>
                                            </g:each>
                                        </div>
                                    </div>
                                    <!-- end form-group--用户权限 -->
                                    <!-- begin form-group--提交按钮 -->
                                    <div class="form-group row m-b-10">
                                        <label class="col-md-3 col-form-label">&nbsp;</label>

                                        <div class="col-md-6">
                                            <input type="submit" class="btn btn-primary" value="提交"/>
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
<asset:javascript src="user/add.js"/>
<script>
    $(document).ready(function () {
    });
</script>
<!-- ================== END PAGE LEVEL JS ================== -->
</body>
</html>
