%{--
    2021.04.19 >>> 增加远程提讯开关 daniel
    2021.04.26 >>> 远程提讯开关修改为被远程提讯 daniel
--}%
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
    </g:if>
    <!-- end #sidebar -->

    <!-- begin #content -->
    <div id="content" class="content">
        <!-- begin breadcrumb -->
        <ol class="breadcrumb pull-right">
            <li class="breadcrumb-item"><g:link controller="index">首页</g:link></li>
            <li class="breadcrumb-item">
                <g:if test="${grailsApplication.config.pageVersion == 'v1'}">业务管理</g:if>
                <g:if test="${grailsApplication.config.pageVersion == 'v2'}"><g:link controller="courtroom"
                                                                                     action="list">监控中心</g:link></g:if>
            </li>
            <li class="breadcrumb-item active">
                <g:if test="${grailsApplication.config.pageVersion == 'v1'}">基础信息管理-法庭管理</g:if>
            </li>
        </ol>
        <!-- end breadcrumb -->
        <!-- begin page-header -->
        <h1 class="page-header">
            <g:if test="${grailsApplication.config.pageVersion == 'v1'}">配置与管理</g:if>
            <g:if test="${grailsApplication.config.pageVersion == 'v2'}">监控中心</g:if>
            <small>添加新法庭</small>
        </h1>
        <!-- end page-header -->
        <div class="panel panel-inverse">
            <div class="panel-heading">
                <div class="panel-heading-btn">
                    <a href="javascript:void(0);" class="btn btn-xs btn-icon btn-circle btn-default"
                       data-click="panel-expand"><i class="fa fa-expand"></i></a>
                </div>
                <h4 class="panel-title">添加新法庭</h4>
            </div>

            <div class="panel-body">
                <g:form controller="courtroom" action="addSave" method="POST" data-parsley-validate="true">
                    <div class="col-md-8 offset-md-2">
                        <!-- begin form-group row -->
                        <div class="form-group row m-b-10">
                            <label class="col-md-3 text-md-right col-form-label">法庭名称</label>

                            <div class="col-md-6">
                                <input type="text" class="form-control m-b-5" name="name" data-parsley-required="true"
                                       data-parsley-required-message="此项不能为空"/>
                            </div>
                        </div>
                        <!-- end form-group row -->
                        <!-- begin form-group row -->
                        <div class="form-group row m-b-10">
                            <label class="col-md-3 text-md-right col-form-label">直播服务地址</label>

                            <div class="col-md-6">
                                <input type="text" class="form-control m-b-5" name="liveIp" data-parsley-required="true"
                                       data-parsley-required-message="此项不能为空"/>
                            </div>
                        </div>
                        <!-- end form-group row -->
                        <!-- begin form-group row -->
                        <div class="form-group row m-b-10">
                            <label class="col-md-3 text-md-right col-form-label">直播服务端口</label>

                            <div class="col-md-6">
                                <input type="text" class="form-control m-b-5" name="livePort"
                                       data-parsley-required="true" data-parsley-required-message="此项不能为空"
                                       data-parsley-maxlength="8" data-parsley-maxlength-message="最大只允许八个字节"/>
                            </div>
                        </div>
                        <!-- end form-group row -->
                        <!-- begin form-group row -->
                        <div class="form-group row m-b-10">
                            <label class="col-md-3 text-md-right col-form-label">设备通信地址</label>

                            <div class="col-md-6">
                                <input type="text" class="form-control m-b-5" name="deviceIp"
                                       data-parsley-required="true" data-parsley-required-message="此项不能为空"/>
                            </div>
                        </div>
                        <!-- end form-group row -->
                        <!-- begin form-group row -->
                        <div class="form-group row m-b-10">
                            <label class="col-md-3 text-md-right col-form-label">设备通信类型</label>

                            <div class="col-md-6">
                                <select name="deviceType" class="form-control m-b-5" data-parsley-required="true"
                                        data-parsley-required-message="此项不能为空">
                                    <option value="TCP">TCP</option>
                                    <option value="UDP">UDP</option>
                                </select>
                            </div>
                        </div>
                        <!-- end form-group row -->
                        <!-- begin form-group row -->
                        <div class="form-group row m-b-10">
                            <label class="col-md-3 text-md-right col-form-label">设备通信端口</label>

                            <div class="col-md-6">
                                <input type="text" class="form-control m-b-5" name="devicePort"
                                       data-parsley-required="true" data-parsley-required-message="此项不能为空"
                                       data-parsley-maxlength="6" data-parsley-maxlength-message="最大只允许六个字节"/>
                            </div>
                        </div>
                        <!-- end form-group row -->
                        <!-- begin form-group row -->
                        <div class="form-group row m-b-10">
                            <label class="col-md-3 text-md-right col-form-label">存储地址</label>

                            <div class="col-md-6">
                                <input type="text" class="form-control m-b-5" name="storeIp"
                                       data-parsley-required="true" data-parsley-required-message="此项不能为空"/>
                            </div>
                        </div>
                        <!-- end form-group row -->
                        <!-- begin form-group row -->
                        <div class="form-group row m-b-10">
                            <label class="col-md-3 text-md-right col-form-label">送远程地址1</label>

                            <div class="col-md-6">
                                <input type="text" class="form-control m-b-5" placeholder="双方远程只使用此地址" name="rtsp"/>
                            </div>
                        </div>
                        <!-- end form-group row -->
                        <!-- begin form-group row -->
                        <div class="form-group row m-b-10">
                            <label class="col-md-3 text-md-right col-form-label">送远程地址2</label>

                            <div class="col-md-6">
                                <input type="text" class="form-control m-b-5" name="rtsp1"/>
                            </div>
                        </div>
                        <!-- end form-group row -->
                        <!-- begin form-group row -->
                        <div class="form-group row m-b-10">
                            <label class="col-md-3 text-md-right col-form-label">是否为公开庭</label>

                            <div class="col-md-6">
                                <select name="open" class="form-control m-b-5" data-parsley-required="false">
                                    <option value="1">是</option>
                                    <option value="0">否</option>
                                </select>
                            </div>
                        </div>
                        <!-- end form-group row -->
                        <!-- begin form-group row -->
                        <div class="form-group row m-b-10">
                            <label class="col-md-3 text-md-right col-form-label">被远程提讯</label>

                            <div class="col-md-6">
                                <select name="isCalled" class="form-control m-b-5" data-parsley-required="false">
                                    <option value="1">允许</option>
                                    <option value="0">禁止</option>
                                </select>
                            </div>
                        </div>
                        <!-- end form-group row -->
                        <!-- begin form-group row -->
                        <div class="form-group row m-b-10">
                            <label class="col-md-3 text-md-right col-form-label">法庭状态</label>

                            <div class="col-md-6">
                                <select name="status" class="form-control m-b-5" data-parsley-required="false">
                                    <option value="1">正常使用</option>
                                    <option value="0">停止使用</option>
                                    <option value="3">非正常状态</option>
                                </select>
                            </div>
                        </div>
                        <!-- end form-group row -->
                        <!-- begin form-group row -->
                        <div class="form-group row m-b-10">
                            <label class="col-md-3 text-md-right col-form-label">排序</label>

                            <div class="col-md-6">
                                <input type="text" class="form-control m-b-5" name="sequence"/>
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
<asset:javascript src="courtroom/add.js"/>
<script>
    $(document).ready(function () {
    });
</script>
<!-- ================== END PAGE LEVEL JS ================== -->
</body>
</html>
