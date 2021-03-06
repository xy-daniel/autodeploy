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
    <asset:stylesheet href="ztree/css/metroStyle/metroStyle.css"/>
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
    <g:render template="/layouts/base_sidebar" model="[active: 30109]"/>
    <!-- end #sidebar -->

    <!-- begin #content -->
    <div id="content" class="content">

        <!-- begin breadcrumb -->
        <ol class="breadcrumb pull-right">
            <li class="breadcrumb-item"><g:link controller="index">首页</g:link></li>
            <li class="breadcrumb-item"><g:link controller="role" action="list">角色管理</g:link></li>
            <li class="breadcrumb-item active">修改角色信息</li>
        </ol>
        <!-- end breadcrumb -->

        <!-- begin page-header -->
        <h1 class="page-header">角色管理 <small>修改角色信息</small></h1>
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
                        <h4 class="panel-title">修改角色信息</h4>
                    </div>
                    <!-- end panel-heading -->
                    <!-- begin panel-body -->
                    <div class="panel-body">
                        <!-- begin wizard-form -->
                        <form id="form-roleEdit" name="form-roleEdit" class="form-control-with-bg form-horizontal"
                              data-parsley-validate="true">
                            <!-- begin row -->
                            <div class="row">
                                <!--角色id-->
                                <input type="hidden" name="roleId" value="${role.id}"/>
                                <input type="hidden" id="authorityPo" value="${role.authority}"/>
                                <input type="hidden" id="remarkPo" value="${role.remark}"/>
                                <!-- begin col-8 -->
                                <div class="col-md-8 offset-md-2">
                                    <legend class="no-border f-w-700 p-b-0 m-t-0 m-b-20 f-s-16 text-inverse">修改角色信息请修改以下内容</legend>
                                    <!-- begin form-group--角色排序 -->
                                    <div class="form-group row m-b-10">
                                        <label class="col-md-3 col-form-label text-md-right" for="sequence">排序 <span
                                                class="text-danger">*</span></label>

                                        <div class="col-md-6">
                                            <input type="text" name="sequence" id="sequence"
                                                   class="form-control" value="${role.sequence}"
                                                   data-parsley-required="true" data-parsley-required-message="此项不能为空"/>
                                        </div>
                                    </div>
                                    <!-- end form-group--角色排序 -->
                                    <!-- begin form-group--角色名称 -->
                                    <div class="form-group row m-b-10">
                                        <label class="col-md-3 col-form-label text-md-right" for="authority">角色名称 <span
                                                class="text-danger">*</span></label>

                                        <div class="col-md-6">
                                            <input type="text" name="authority" id="authority"
                                                   class="form-control" value="${role.authority}"
                                                   data-parsley-required="true" data-parsley-required-message="此项不能为空"
                                                   data-parsley-remote data-parsley-remote-validator='checkauthority'
                                                   data-parsley-remote-message="输入的角色名称已存在"/>
                                        </div>
                                    </div>
                                    <!-- end form-group--角色名称 -->
                                    <!-- begin form-group--描述 -->
                                    <div class="form-group row m-b-10">
                                        <label class="col-md-3 col-form-label text-md-right" for="remark">描述 <span
                                                class="text-danger">*</span></label>

                                        <div class="col-md-6">
                                            <input type="text" id="remark" name="remark"
                                                   class="form-control" value="${role.remark}"
                                                   data-parsley-required="true" data-parsley-required-message="此项不能为空"
                                                   data-parsley-remote data-parsley-remote-validator='checkremark'
                                                   data-parsley-remote-message="描述已存在"/>
                                        </div>
                                    </div>
                                    <!-- end form-group--描述 -->

                                    <!-- begin form-group--功能权限菜单 -->
                                    <div class="form-group row m-b-10">
                                        <label class="col-md-3 col-form-label text-md-right" for="remark">功能权限 <span
                                                class="text-danger">*</span></label>

                                        <div class="col-md-6">
                                            <ul id="menuTree" class="ztree"></ul>
                                        </div>
                                        <input type="hidden" name="ids" id="ids">
                                    </div>
                                    <!-- end form-group--功能权限菜单 -->

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
<asset:javascript src="ztree/jquery.ztree.all.min.js"/>
<asset:javascript src="role/edit.js"/>
<script>
    $(document).ready(function () {
    });
</script>
<!-- ================== END PAGE LEVEL JS ================== -->
</body>
</html>
