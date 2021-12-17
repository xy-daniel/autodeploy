<!DOCTYPE html>
<!--[if IE 8]> <html class="ie8" lang="zh"> <![endif]-->
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
    <g:render template="/layouts/base_sidebar" model="[active: 20003]"/>
    <!-- end #sidebar -->

    <!-- begin #content -->
    <div id="content" class="content">
        <!-- begin breadcrumb -->
        <ol class="breadcrumb pull-right">
            <li class="breadcrumb-item"><g:link controller="index">首页</g:link></li>
            <li class="breadcrumb-item"><g:link controller="app" action="list">应用管理</g:link></li>
            <li class="breadcrumb-item">
                版本管理
            </li>
        </ol>
        <!-- end breadcrumb -->
        <!-- begin page-header -->
        <h1 class="page-header">
            应用管理
            <small>版本管理</small>
        </h1>
        <!-- end page-header -->
        <div class="panel panel-inverse">
            <div class="panel-heading">
                <div class="panel-heading-btn">
                    <a href="javascript:void(0);" class="btn btn-xs btn-icon btn-circle btn-default"
                       data-click="panel-expand"><i class="fa fa-expand"></i></a>
                </div>
                <h4 class="panel-title">应用:${app.itemName}-版本列表</h4>
            </div>

            <div class="panel-body">
                <div class="table-btn-row m-b-15">
                    <sec:ifAnyGranted roles='ROLE_SUPER,ROLE_ADMIN,ROLE_/courtroom/add'>
                        <g:link controller="version" action="add" id="${app.id}"
                                class="btn btn-sm btn-inverse btn-add"><i
                                class="fa fa-plus m-r-5"></i> 添加版本</g:link>
                    </sec:ifAnyGranted>
                </div>
                <input id="id" type="hidden" value="${app.id}">
                <table id="data-table" class="table table-striped table-bordered display width-full">
                    <thead>
                    <tr>
                        <th class="text-nowrap">版本号</th>
                        <th class="text-nowrap">存储路径</th>
                        <th class="text-nowrap">占用空间</th>
                        <th class="text-nowrap">最新发布时间</th>
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
<asset:javascript src="version/list.js"/>
<script>
    $(document).ready(function () {
    });
</script>
<!-- ================== END PAGE LEVEL JS ================== -->
</body>
</html>