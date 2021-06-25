<%@ page import="com.hxht.autodeploy.PlanStatus" %>
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
            <li class="breadcrumb-item">业务管理</li>
            <li class="breadcrumb-item active">基础信息管理-法庭管理</li>
        </ol>
        <!-- end breadcrumb -->
        <!-- begin page-header -->
        <h1 class="page-header">配置与管理 <small>法庭管理</small></h1>
        <!-- end page-header -->
        <div class="panel panel-inverse">
            <div class="panel-heading">
                <div class="panel-heading-btn">
                    <a href="javascript:void(0);" class="btn btn-xs btn-icon btn-circle btn-default" data-click="panel-expand"><i class="fa fa-expand"></i></a>
                </div>
                <h4 class="panel-title">上报远程法庭（此功能为深圳专用，深圳法院向最高院上报法庭数据！）</h4>
            </div>
            <div class="panel-body">
                <div class="table-btn-row m-b-15">
                    <sec:ifAnyGranted roles='ROLE_SUPER,ROLE_ADMIN'>
                        <a href="javascript:void(0);" id="report-remote-court" class="btn btn-sm btn-inverse btn-del"><i class="fas fa-cloud-upload-alt m-r-5"></i> 上报所选法庭为远程庭</a>
                    </sec:ifAnyGranted>
                </div>
            <!-- begin row -->
            <div class="row">
                <g:each in="${courts}" var="c" status="i">
                    <div class="col-lg-3 col-md-6">
                        <g:if test="${c.remote == 1}">
                            <!-- 已上报法庭初始化颜色 -->
                            <div class="ch widget widget-stats bg-green-lighter">
                                <div class="stats-icon">
                                    <input type="hidden" value="${c.id}">
                                    <i class="fas fa-check"></i>
                                </div>
                                <div class="stats-info">
                                    <h4>远程庭</h4>
                                    <p>${c.name}</p>
                                </div>
                                <div class="stats-link">
                                    <a>
                                        <span>已上报</span>
                                        <i class="fa fa-arrow-alt-circle-right"></i>
                                    </a>
                                </div>
                            </div>
                        </g:if>
                        <g:else>
                            <!-- 未上报法庭初始化颜色 -->
                            <div class="ch widget widget-stats bg-grey-darker">
                                <div class="stats-icon">
                                    <input type="hidden" value="${c.id}">
                                    <i class="fas fa-plus"></i>
                                </div>
                                <div class="stats-info">
                                    <h4>本地庭</h4>
                                    <p>${c.name}</p>
                                </div>
                                <div class="stats-link">
                                    <a>
                                        <span>未上报</span>
                                        <i class="fa fa-arrow-alt-circle-right"></i>
                                    </a>
                                </div>
                            </div>
                        </g:else>

                    </div>
                </g:each>
            </div>
            <!-- end row -->
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
<asset:javascript src="remote/index.js"/>
<script>
    $(document).ready(function () {
    });
</script>
<!-- ================== END PAGE LEVEL JS ================== -->
</body>
</html>
