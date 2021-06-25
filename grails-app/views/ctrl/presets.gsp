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
        <h1 class="page-header">配置与管理 <small>修改摄像头预置位信息</small></h1>
        <!-- end page-header -->
        <!-- begin courtroom_id -->
        <input id="courtroom" type="hidden" value="${courtroom.id}"/>
        <!-- end courtroom_id -->
        <div class="panel panel-inverse">
            <div class="panel-heading">
                <div class="panel-heading-btn">
                    <a href="javascript:void(0);" class="btn btn-xs btn-icon btn-circle btn-default" data-click="panel-expand"><i class="fa fa-expand"></i></a>
                </div>
                <h4 class="panel-title">修改摄像头预置位信息</h4>
            </div>
            <div class="panel-body">
                <!-- begin tab-pane 摄像头控制-->
                <div class="tab-pane">
                    <div class="table-btn-row m-b-15">
                        <sec:ifAnyGranted roles='ROLE_SUPER,ROLE_ADMIN,ROLE_MONITOR'>
                            <g:link url="${createLink(uri: '/')}ctrl/persets/add/${courtroom.id}" class="btn btn-sm btn-inverse btn-add"><i class="fa fa-plus m-r-5"></i> 添加预置位</g:link>
                            <a href="javascript:void(0);" id="delPresets" class="btn btn-sm btn-inverse btn-del"><i class="fa fa-times m-r-5"></i> 删除预置位</a>
                            <g:link url="${createLink(uri: '/')}ctrl/camera/buttons/${courtroom.id}" class="btn btn-sm btn-inverse btn-add"><i class="fa fa-plus m-r-5"></i> 控制指令修改 </g:link>
                        </sec:ifAnyGranted>
                    </div>
                    <table class="table table-striped table-bordered display">
                        <thead>
                        <tr>
                            <th class="with-checkbox" width="15px">
                                <div class="checkbox checkbox-css">
                                    <input type="checkbox" class="cp_all" id="table_checkbox_all" />
                                    <label for="table_checkbox_all">&nbsp;</label>
                                </div>
                            </th>
                            <th class="text-nowrap">预置位名称</th>
                            <th class="text-nowrap">保存指令</th>
                            <th class="text-nowrap">调用指令</th>
                            <th class="text-nowrap">操作</th>
                        </tr>
                        </thead>
                        <tbody>
                        <g:each in="${camera?.presets}" var="cp" status="i">
                            <tr>
                                <td class="with-checkbox">
                                    <div class="checkbox checkbox-css">
                                        <input type="checkbox" class="cp_single" name="checkbox-select" value="${cp.uuid}" id="table_checkbox_${cp.uuid}" data-user="${cp.uuid}" />
                                        <label for="table_checkbox_${cp.uuid}">&nbsp;</label>
                                    </div>
                                </td>
                                <td>${cp.name}</td>
                                <td>${cp.save}</td>
                                <td>${cp.call}</td>
                                <td>
                                    <input type="hidden" value="${cp.uuid}" />
                                    <a href="javascript:void(0)" class="editCp btn btn-inverse btn-xs m-r-5 btn-enabled">编辑</a>
                                </td>
                            </tr>
                        </g:each>
                        </tbody>
                    </table>
                </div>
                <!-- end tab-pane -->
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
<asset:javascript src="bootstrap-sweetalert/sweetalert.js"/>
<asset:javascript src="bootstrap-daterangepicker/moment.min.js"/>
<asset:javascript src="bootstrap-select/bootstrap-select.min.js"/>
<asset:javascript src="bootstrap-eonasdan-datetimepicker/build/js/bootstrap-datetimepicker.min.js"/>
<asset:javascript src="jquery-tag-it/js/tag-it.min.js"/>
<asset:javascript src="select2/dist/js/select2.min.js"/>
<asset:javascript src="ctrl/presets.js"/>
<script>
    $(document).ready(function () {
    });
</script>
<!-- ================== END PAGE LEVEL JS ================== -->
</body>
</html>