<!DOCTYPE html>
<!--[if IE 8]> <html class="ie8" lang=""> <![endif]-->
<!--[if !IE]><!-->
<html>
<!--<![endif]-->
<head>
    <meta charset="utf-8" />
    <title>领导观摩系统</title>
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
    <style>
    .page-with-top-menu{
        padding-top: 40px;
    }

    </style>

</head>
<body>


<!-- begin #page-container -->
<div id="page-container" class="page-container fade page-without-sidebar page-header-fixed page-with-top-menu">
    <!-- begin #sidebar -->
    <g:render template="/leader/leader_sidebar" model="[active: 70100]"/>
    <!-- end #sidebar -->

    <!-- begin #content -->
    <div id="content" class="content">
        <!-- begin breadcrumb -->
        <ol class="breadcrumb pull-right">
            <li class="breadcrumb-item"><g:link controller="message" action="index">首页</g:link></li>
%{--            <li class="breadcrumb-item">--}%
%{--                <g:if test="${grailsApplication.config.pageVersion == 'v1'}"> 业务管理</g:if>--}%
%{--                <g:if test="${grailsApplication.config.pageVersion == 'v2'}"> 监控中心</g:if>--}%
%{--            </li>--}%
%{--            <li class="breadcrumb-item active">--}%
%{--                <g:if test="${grailsApplication.config.pageVersion == 'v1'}"> 基础信息管理-法庭管理</g:if>--}%
%{--            </li>--}%
        </ol>
        <!-- end breadcrumb -->
        <!-- begin page-header -->
        <h1 class="page-header">
             监控中心
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
                <table id="data-table" class="table table-striped table-bordered display" style="width:100%">
                    <thead>
                    <tr>
                        <th class="text-nowrap" style="width: 20px">序号</th>
                        <th class="text-nowrap">法庭名称</th>
                        <th class="text-nowrap">设备在线状态</th>
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
<asset:javascript src="leader/courtroom.js"/>
<script>
    $(document).ready(function () {
    });
</script>
<!-- ================== END PAGE LEVEL JS ================== -->
</body>
</html>
