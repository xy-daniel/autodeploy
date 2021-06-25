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
    <asset:stylesheet href="DataTables/media/css/dataTables.bootstrap.min.css"/>
    <asset:stylesheet href="bootstrap-sweetalert/sweetalert.css"/>
    <asset:stylesheet href="DataTables/extensions/Responsive/css/responsive.bootstrap.min.css"/>
    <asset:stylesheet href="treegrid/jquery.treegrid.css"/>
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
            <li class="breadcrumb-item">系统功能管理</li>
        </ol>
        <!-- end breadcrumb -->
        <!-- begin page-header -->
        <h1 class="page-header">系统功能管理</h1>
        <!-- end page-header -->
        <!-- begin row -->
        <div class="row">
            <div class="col-lg-12">
                <!-- begin panel -->
                <div class="panel panel-inverse">
                    <!-- begin panel-heading -->
                    <div class="panel-heading">
                        <div class="panel-heading-btn">
                            <a href="javascript:;" class="btn btn-xs btn-icon btn-circle btn-default" data-click="panel-expand"><i class="fa fa-expand"></i></a>
                        </div>
                        <h4 class="panel-title">系统功能列表</h4>
                    </div>
                    <!-- end panel-heading -->
                    <!-- begin panel-body -->
                    <div class="panel-body">
                        <div class="table-btn-row m-b-15">
                            <sec:ifAnyGranted roles='ROLE_SUPER,ROLE_ADMIN,ROLE_/menu/add'>
                                <g:link controller="menu" action="add" class="btn btn-sm btn-inverse btn-add"><i class="fa fa-plus m-r-5"></i> 添加新功能</g:link>
                             </sec:ifAnyGranted>
                         %{--   <sec:ifAnyGranted roles='ROLE_SUPER'>
                                <a href="javascript:void(0);" id="checkedBtn" class="btn btn-sm btn-inverse btn-del"><i class="fa fa-times m-r-5"></i> 删除所选功能</a>
                            </sec:ifAnyGranted>--}%
                        </div>
                        <table id="menuTreeTable" data-mobile-responsive="true" data-click-to-select="true" style="width:100%">
                        </table>
                        </table>
                    </div>
                    <!-- end panel-body -->
                </div>
                <!-- end panel -->
            </div>
        </div>
        <!-- end row -->
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
<asset:javascript src="treegrid/jquery.treegrid.min.js"/>
<asset:javascript src="treegrid/jquery.treegrid.bootstrap3.js"/>
<asset:javascript src="treegrid/jquery.treegrid.extension.js"/>
<asset:javascript src="menu/list.js"/>
<script>
    $(document).ready(function () {
    });
</script>
<!-- ================== END PAGE LEVEL JS ================== -->
</body>
</html>
