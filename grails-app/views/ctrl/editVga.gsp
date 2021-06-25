<%@ page import="com.hxht.autodeploy.PlanStatus" %>
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
    <g:if test="${grailsApplication.config.pageVersion == 'v1'}">
        <g:render template="/layouts/base_sidebar" model="[active: 30102]"/>
    </g:if>
    <g:if test="${grailsApplication.config.pageVersion == 'v2'}">
        <g:render template="/layouts/base_sidebar" model="[active: 7100]"/>
    </g:if>    <!-- end #sidebar -->

    <!-- begin #content -->
    <div id="content" class="content">
        <!-- begin breadcrumb -->
        <ol class="breadcrumb pull-right">
            <li class="breadcrumb-item"><g:link controller="index">首页</g:link></li>
            <li class="breadcrumb-item">业务管理</li>
            <li class="breadcrumb-item active">基础信息管理-法庭管理</li>
        </ol>
        <!-- end breadcrumb -->
        <!-- begin page-header -->
        <h1 class="page-header">配置与管理 <small>修改VGA矩阵</small></h1>
        <!-- end page-header -->
        <div class="panel panel-inverse">
            <div class="panel-heading">
                <div class="panel-heading-btn">
                    <a href="javascript:void(0);" class="btn btn-xs btn-icon btn-circle btn-default" data-click="panel-expand"><i class="fa fa-expand"></i></a>
                </div>
                <h4 class="panel-title">修改VGA矩阵</h4>
            </div>
    <input id="courtroom" type="hidden" value="${roomId}"/>
            <div class="panel-body">
                <g:form controller="ctrl" action="editSaveVga" id="${roomId}" method="POST">
                    <div class="col-md-8 offset-md-2">
                        <!-- begin uuid -->
                        <input type="hidden" name="uuid" value="${uuid}">
                        <!-- end uuid -->
                        <!-- begin form-group row -->
                        <div class="form-group row m-b-10">
                            <label class="col-md-3 text-md-right col-form-label">名称 </label>
                            <div class="col-md-6">
                                <input type="text" class="form-control m-b-5" name="name" value="${name}" data-parsley-required="true" data-parsley-required-message="此项不能为空"/>
                            </div>
                        </div>
                        <!-- end form-group row -->
                        <!-- begin form-group row -->
                        <div class="form-group row m-b-10">
                            <label class="col-md-3 text-md-right col-form-label">控制指令 </label>
                            <div class="col-md-6">
                                <input type="text" class="form-control m-b-5" name="codeDown" value="${codeDown}" data-parsley-required="true" data-parsley-required-message="此项不能为空"/>
                            </div>
                        </div>
                        <!-- end form-group row -->
                        <!-- begin form-group row -->
                        <div class="form-group row m-b-10">
                            <label class="col-md-3 text-md-right col-form-label">可见状态 </label>
                            <div class="col-md-6">
                                <div class="radio radio-css radio-inline">
                                    <input type="radio" name="visible" id="visible1" value="1" <g:if test="${visible == '1'}">checked</g:if> />
                                    <label for="visible1">可见</label>
                                </div>
                                <div class="radio radio-css radio-inline">
                                    <input type="radio" name="visible" id="visible2" value="0" <g:if test="${visible == '0'}">checked</g:if>/>
                                    <label for="visible2">隐藏</label>
                                </div>
                            </div>
                        </div>
                        <!-- end form-group row -->
                        <div class="form-group row m-b-10">
                            <label class="col-md-3 text-md-right col-form-label"></label>
                            <div class="col-md-6">
                                <button type="submit" class="btn btn-sm btn-primary m-r-5">修改</button>
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
<asset:javascript src="ctrl/parsley.js"/>
<asset:javascript src="ctrl/editVga.js"/>
<script>
    $(document).ready(function () {
    });
</script>
<!-- ================== END PAGE LEVEL JS ================== -->
</body>
</html>
