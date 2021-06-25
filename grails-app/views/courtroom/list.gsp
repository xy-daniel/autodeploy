%{--
    2021.04.19 >>> 增加远程提讯开关 daniel
--}%
<!DOCTYPE html>
<!--[if IE 8]> <html class="ie8" lang=""> <![endif]-->
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
    <asset:stylesheet href="DataTables/media/css/dataTables.bootstrap.min.css"/>
    <asset:stylesheet href="bootstrap-sweetalert/sweetalert.css"/>
    <asset:stylesheet href="DataTables/extensions/Responsive/css/responsive.bootstrap.min.css"/>
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
                <g:if test="${grailsApplication.config.pageVersion == 'v1'}"> 业务管理</g:if>
                <g:if test="${grailsApplication.config.pageVersion == 'v2'}"> 监控中心</g:if>
            </li>
            <li class="breadcrumb-item active">
                <g:if test="${grailsApplication.config.pageVersion == 'v1'}"> 基础信息管理-法庭管理</g:if>
            </li>
        </ol>
        <!-- end breadcrumb -->
        <!-- begin page-header -->
        <h1 class="page-header">
            <g:if test="${grailsApplication.config.pageVersion == 'v1'}"> 配置与管理</g:if>
            <g:if test="${grailsApplication.config.pageVersion == 'v2'}"> 监控中心</g:if>
            <small>法庭管理</small>
        </h1>
        <!-- end page-header -->
        <div class="panel panel-inverse">
            <div class="panel-heading">
                <div class="panel-heading-btn">
                    <a href="javascript:void(0);" class="btn btn-xs btn-icon btn-circle btn-default" data-click="panel-expand"><i class="fa fa-expand"></i></a>
                </div>
                <h4 class="panel-title">法庭列表</h4>
            </div>
            <div class="panel-body">
                <div class="table-btn-row m-b-15">
                    <sec:ifAnyGranted roles='ROLE_SUPER,ROLE_ADMIN,ROLE_/courtroom/add'>
                        <g:link controller="courtroom" action="add" class="btn btn-sm btn-inverse btn-add"><i class="fa fa-plus m-r-5"></i> 添加新法庭</g:link>
                    </sec:ifAnyGranted>
                    <sec:ifAnyGranted roles='ROLE_SUPER,ROLE_ADMIN,ROLE_/courtroom/del'>
                        <a href="javascript:void(0);" id="checkedBtn" class="btn btn-sm btn-inverse btn-del"><i class="fa fa-times m-r-5"></i> 删除所选法庭</a>
                    </sec:ifAnyGranted>
                    <sec:ifAnyGranted roles='ROLE_SUPER,ROLE_ADMIN,ROLE_/remote/index'>
                        <g:if test="${isAllow == "true"}">
                            <g:link controller="remote" action="index" class="btn btn-sm btn-inverse btn-add"><i class="fa fa-check m-r-5"></i> 上报远程法庭</g:link>
                        </g:if>
                    </sec:ifAnyGranted>
                </div>
                <table id="data-table" class="table table-striped table-bordered display" style="width:100%">
                    <thead>
                    <tr>
                        <th class="with-checkbox">
                            <div class="checkbox checkbox-css">
                                <input type="checkbox" value="" id="table_checkbox_all" />
                                <label for="table_checkbox_all">&nbsp;</label>
                            </div>
                        </th>
                        <th class="text-nowrap">法庭名称</th>
                        <th class="text-nowrap">直播服务地址</th>
                        <th class="text-nowrap">直播服务端口</th>
                        <th class="text-nowrap">庭审设备地址</th>
                        <th class="text-nowrap">设备在线状态</th>
                        <th class="text-nowrap">存储服务地址</th>
                        <th class="text-nowrap">远程提讯</th>
                        <th class="text-nowrap">法庭状态</th>
                        <th class="text-nowrap">操作</th>
                    </tr>
                    </thead>
                    <tbody>
                    </tbody>
                </table>
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
<asset:javascript src="DataTables/media/js/jquery.dataTables.min.js"/>
<asset:javascript src="bootstrap-sweetalert/sweetalert.js"/>
<asset:javascript src="DataTables/media/js/dataTables.bootstrap.min.js"/>
<asset:javascript src="DataTables/extensions/Responsive/js/dataTables.responsive.min.js"/>
<asset:javascript src="courtroom/courtroom.js"/>
<script>
    $(document).ready(function () {
    });
</script>
<!-- ================== END PAGE LEVEL JS ================== -->
</body>
</html>
