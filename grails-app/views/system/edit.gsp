%{--
    2021.04.26 >>> 分离语音传唤和报警设置 daniel
    2021.06.16 >>> 互联网开庭统计 daniel
--}%
<!DOCTYPE html>
<!--[if IE 8]> <html class="ie8" lang="CN"> <![endif]-->
<!--[if !IE]><!-->
<html>
<!--<![endif]-->
<head>
    <meta charset="utf-8"/>
    <title>科技法庭管理系统</title>
    <meta content="width=device-width, initial-scale=1.0, maximum-scale=1.0, system-scalable=no" name="viewport"/>
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
    <g:if test="${grailsApplication.config.pageVersion == 'v1'}">
        <g:render template="/layouts/base_sidebar" model="[active: 30800]"/>
    </g:if>
    <g:if test="${grailsApplication.config.pageVersion == 'v2'}">
        <g:render template="/layouts/base_sidebar" model="[active: 5300]"/>
    </g:if>
    <!-- end #sidebar -->

    <!-- begin #content -->
    <div id="content" class="content">

        <!-- begin breadcrumb -->
        <ol class="breadcrumb pull-right">
            <li class="breadcrumb-item"><g:link controller="index">首页</g:link></li>
            <li class="breadcrumb-item"><g:link controller="system" action="edit">系统设置</g:link></li>
            <li class="breadcrumb-item active">修改系统设置</li>
        </ol>
        <!-- end breadcrumb -->

        <!-- begin page-header -->
        <h1 class="page-header">系统设置 <small>修改系统设置</small></h1>
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
                        <h4 class="panel-title">修改系统设置</h4>
                    </div>
                    <!-- end panel-heading -->
                    <!-- begin panel-body -->
                    <div class="panel-body">
                        <!-- begin wizard-form -->
                        <form id="form-systemTitleEditSave" name="form-systemTitleEditSave"
                              class="form-control-with-bg form-horizontal" data-parsley-validate="true">
                            <!-- begin row -->
                            <div class="row">
                                <!-- begin col-8 -->
                                <div class="col-md-8 offset-md-2">
                                    <legend class="no-border f-w-700 p-b-0 m-t-0 m-b-20 f-s-16 text-inverse">
                                        请填写以下内容
                                    </legend>
                                    <!-- begin form-group--系统标题 -->
                                    <div class="form-group row m-b-10">
                                        <label class="col-md-3 col-form-label text-md-right"
                                               for="systemTitle">系统标题 <span class="text-danger">*</span></label>

                                        <div class="col-md-6">
                                            <input type="text" name="systemTitle" id="systemTitle" class="form-control"
                                                   data-parsley-remote data-parsley-required="true"
                                                   data-parsley-required-message="此项不能为空" value="${court.ext5}"/>
                                        </div>
                                    </div>
                                    <!-- end form-group--系统标题 -->
                                    <!-- begin form-group--开庭模式 -->
%{--                                    <div class="form-group row m-b-10">--}%
%{--                                        <label class="col-md-3 col-form-label text-md-right">互联网开庭 <span--}%
%{--                                                class="text-danger">*</span></label>--}%
%{--                                        <div class="col-md-6">--}%
%{--                                            <select class="form-control selectpicker" name="allowInternet"--}%
%{--                                                    data-style="btn-white" data-parsley-required="false">--}%
%{--                                                <option value="0" <g:if test="${allowInternet == "0"}">selected</g:if>>--}%
%{--                                                    禁用</option>--}%
%{--                                                <option value="1" <g:if test="${allowInternet == "1"}">selected</g:if>>--}%
%{--                                                    允许</option>--}%
%{--                                            </select>--}%
%{--                                        </div>--}%
%{--                                    </div>--}%
                                    <!-- end form-group--开庭模式 -->
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
<asset:javascript src="system/parsley.js"/>
<asset:javascript src="system/edit.js"/>
<script>
    $(document).ready(function () {
    });
</script>
<!-- ================== END PAGE LEVEL JS ================== -->
</body>
</html>
