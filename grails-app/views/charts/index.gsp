<%@ page import="com.hxht.autodeploy.PlanStatus" %>
<%@ page import="com.hxht.autodeploy.court.manager.SystemController" %>
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
%{--    <asset:stylesheet href="bootstrap/css/bootstrap.min.css"/>--}%
    <asset:stylesheet href="bootstrap-eonasdan-datetimepicker/build/css/bootstrap-datetimepicker.min.css"/>
    <!-- ================== END PAGE LEVEL STYLE ================== -->
</head>
<body>
<!-- begin #page-loader -->
<g:render template="/layouts/base_loader"/>
<!-- end #page-loader -->

<!-- begin #page-container -->
<div id="page-container" class="page-container fade page-without-sidebar page-header-fixed page-with-top-menu page-content-full-height">
    <!-- begin #header -->
    <g:render template="/layouts/base_navigation"
              model="[
                      picture: true,
                      arrow  : true
              ]"/>
    <!-- end #header -->

    <!-- begin #sidebar -->
    <g:if test="${grailsApplication.config.pageVersion == 'v1'}">
        <g:render template="/layouts/base_sidebar" model="[active: 50100]"/>
    </g:if>
    <g:if test="${grailsApplication.config.pageVersion == 'v2'}">
        <g:render template="/layouts/base_sidebar" model="[active: 30900]"/>
    </g:if>
    <!-- end #sidebar -->

    <!-- begin #content -->
    <div id="content" class="content content-full-width inbox">
        <!-- begin vertical-box -->
        <div class="vertical-box with-grid">
            <!-- begin vertical-box-column -->
            <div class="vertical-box-column width-200 bg-silver hidden-xs">
                <!-- begin vertical-box -->
                <div class="vertical-box">
                    <!-- begin vertical-box-row -->
                    <div class="vertical-box-row">
                        <!-- begin vertical-box-cell -->
                        <div class="vertical-box-cell">
                            <!-- begin vertical-box-inner-cell -->
                            <div class="vertical-box-inner-cell">
                                <!-- begin scrollbar -->
                                <div data-scrollbar="true" data-height="100%">
                                    <!-- begin wrapper -->
                                    <div class="wrapper p-0">
                                        <div class="nav-title"><b>统计类别</b></div>
                                        <ul class="nav nav-inbox">
                                            <li class="active activeAnalyze" style="cursor: pointer">
                                                <a class="judgeAnalyze">法官统计</a>
                                            </li>
                                            <li style="cursor: pointer">
                                                <a class="courtAnalyze">法庭统计</a>
                                            </li>
                                            <li style="cursor: pointer">
                                                <a class="caseAnalyze">案件统计</a>
                                            </li>
                                            <li style="cursor: pointer">
                                                <a class="deptAnalyze">部门统计</a>
                                            </li>
                                            <g:if test="${SystemController.currentCourt.ext3?.matches("J30")}">
                                                <li style="cursor: pointer">
                                                    <a class="speechAnalyze">语音识别周统计</a>
                                                </li>
                                            </g:if>
                                        </ul>
                                    </div>
                                    <div class="wrapper p-0">
                                        <div class="nav-title"><b>年份选择</b></div>
                                        <div class="form-group m-b-10">
                                            <select id="year" class="form-control">
                                                <g:each in="${years}" var="year" status="i">
                                                    <g:if test="${year == current}">
                                                        <option selected id="currentYear">${year}</option>
                                                    </g:if>
                                                    <g:else>
                                                        <option>${year}</option>
                                                    </g:else>
                                                </g:each>
                                            </select>
                                        </div>
                                    </div>
                                    <!-- end wrapper -->
                                </div>
                                <!-- end scrollbar -->
                            </div>
                            <!-- end vertical-box-inner-cell -->
                        </div>
                        <!-- end vertical-box-cell -->
                    </div>
                    <!-- end vertical-box-row -->
                </div>
                <!-- end vertical-box -->
            </div>
            <!-- end vertical-box-column -->
            <!-- begin vertical-box-column -->
            <div class="vertical-box-column bg-white">
                <!-- begin vertical-box -->
                <div class="vertical-box">
                    <!-- begin wrapper -->
                    <div class="wrapper bg-silver-lighter clearfix">
                        <div class="pull-left">
                        </div>
                    </div>
                    <!-- end wrapper -->
                    <!-- begin vertical-box-row -->
                    <div class="vertical-box-row">
                        <!-- begin vertical-box-cell -->
                        <div class="vertical-box-cell">
                            <!-- begin vertical-box-inner-cell -->
                            <div class="vertical-box-inner-cell">
                                <!-- begin scrollbar -->
                                <div data-scrollbar="true" data-height="100%">
                                    <!-- begin wrapper -->
                                    <div class="mainDiv wrapper">
                                        <h3 id="tag" class="m-t-0 m-b-15 f-w-500">法官统计</h3>
                                        <div id="main" style="width: 100%;"></div>
                                    </div>
                                    <!-- end wrapper -->
                                </div>
                                <!-- end scrollbar -->
                            </div>
                            <!-- end vertical-box-inner-cell -->
                        </div>
                        <!-- end vertical-box-cell -->
                    </div>
                    <!-- end vertical-box-row -->
                    <!-- begin wrapper -->
                    <div class="wrapper text-center clearfix">
                    </div>
                    <!-- end wrapper -->
                </div>
                <!-- end vertical-box -->
            </div>
            <!-- end vertical-box-column -->
        </div>
        <!-- end vertical-box -->
    </div>
    <!-- end #content -->

    <!-- begin scroll to top btn -->
    <g:render template="/layouts/base_topbtn"/>
    <!-- end scroll to top btn -->
</div>
<!-- end page container -->

<g:render template="/layouts/base_bottom"/>
<!-- ================== BEGIN PAGE LEVEL JS ================== -->
<asset:javascript src="echarts-4.5.0/dist/echarts.min.js"/>
<asset:javascript src="bootstrap/js/bootstrap.min.js"/>
<asset:javascript src="bootstrap-table/bootstrap-table.min.js"/>
<asset:javascript src="bootstrap-table-export/xlsx.core.min.js"/>
<asset:javascript src="bootstrap-table-export/FileSaver.min.js"/>
<asset:javascript src="bootstrap-table/bootstrap-table-zh-CN.min.js"/>
<asset:javascript src="echarts-4.5.0/dist/echarts.min.js"/>
<asset:javascript src="bootstrap-daterangepicker/moment.min.js"/>
<asset:javascript src="bootstrap-eonasdan-datetimepicker/build/js/bootstrap-datetimepicker.min.js"/>
<asset:javascript src="bootstrap-table-export/tableExport.min.js"/>
<asset:javascript src="bootstrap-table-export/bootstrap-table-export.min.js"/>

<asset:javascript src="charts/index.js"/>
<script>
    $(document).ready(function () {
    });
</script>
<!-- ================== END PAGE LEVEL JS ================== -->
</body>
</html>
