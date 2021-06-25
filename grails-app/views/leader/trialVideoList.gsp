<!DOCTYPE html>
<!--[if IE 8]> <html class="ie8"> <![endif]-->
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

    <asset:stylesheet href="jquery-smart-wizard/src/css/smart_wizard.css"/>
    <asset:stylesheet href="parsley/src/parsley.css"/>
    <asset:stylesheet href="bootstrap-eonasdan-datetimepicker/build/css/bootstrap-datetimepicker.min.css"/>
    <asset:stylesheet href="bootstrap-select/bootstrap-select.min.css"/>
    <asset:stylesheet href="jquery-tag-it/css/jquery.tagit.css"/>
    <asset:stylesheet href="select2/dist/css/select2.min.css"/>
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
    <g:render template="/leader/leader_sidebar" model="[active: 40100]"/>
    <!-- end #sidebar -->

    <!-- begin #content -->
    <div id="content" class="content">
        <!-- begin breadcrumb -->
        <ol class="breadcrumb pull-right">
            <li class="breadcrumb-item"><g:link controller="message" action="index">首页</g:link></li>
            <li class="breadcrumb-item">庭审点播</li>
        </ol>
        <!-- end breadcrumb -->
        <!-- begin page-header -->
        <h1 class="page-header">庭审点播</h1>
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
                        <h4 class="panel-title">点播列表</h4>
                    </div>
                    <div class="panel-body">
                        <!-- 区分v2点播 -->
                        <input type="text" name="pageType"  class="form-control" id="pageType" value="trialVideo" hidden="hidden"/>

                        <div class="form-group row m-b-10">
                            <label class="text-md-right col-form-label" style="width: 5%;">案号：</label>
                            <div class="col-md-2">
                                <input type="text" name="archives" placeholder="案号" class="form-control" id="archives"/>
                            </div>
                            <label class="col-md-1 text-md-right col-form-label">案件名称：</label>
                            <div class="col-md-2">
                                <input type="text" name="name" placeholder="案件名称" class="form-control" id="name"/>
                            </div>
                            <!-- begin form-group -->
                            <label class="col-md-1 col-form-label text-md-right">排期法庭：</label>
                            <div class="col-md-2">
                                <select class="form-control selectpicker" data-size="10" data-live-search="true" data-style="btn-white" id="courtroom" name="courtroom" data-parsley-group="step-3" data-parsley-required="true">
                                    <option value="" selected="selected">请选择</option>
                                    <g:each in="${courtroomList}" var="courtroom" status="i">
                                        <option value="${courtroom.id}">${courtroom.name}</option>
                                    </g:each>
                                </select>
                            </div>
                            <!-- end form-group -->
                            <label class="col-md-1 text-md-right col-form-label">主审法官：</label>
                            <div class="col-md-2" >
                                <select class="form-control selectpicker" data-size="10" data-live-search="true" data-style="btn-white" id="judge" name="judge" data-parsley-group="step-3" data-parsley-required="true">
                                    <option value="" selected="selected">请选择</option>
                                    <g:each in="${judgeList}" var="judgelist" status="i">
                                        <option value="${judgelist.id}">${judgelist.name}</option>
                                    </g:each>
                                </select>
                            </div>
                        </div>
                        <div class="form-group row m-b-10">

                            <label class="text-md-right col-form-label" style="width: 5%;">书记员：</label>
                            <div class="col-md-2">
                                <select class="form-control selectpicker" data-size="10" data-live-search="true" data-style="btn-white" id="secretary" name="secretary" data-parsley-group="step-3" data-parsley-required="true">
                                    <option value="" selected="selected">请选择</option>
                                    <g:each in="${secretaryList}" var="secretarylist" status="i">
                                        <option value="${secretarylist.id}">${secretarylist.name}</option>
                                    </g:each>
                                </select>
                            </div>
                            <label class="col-md-1 col-form-label text-md-right">排期开庭时间范围：</label>
                            <div class="col-md-3">
                                <div class="row">
                                    <div class="col-md-4">
                                        <div class="col-xs-6">
                                            <g:if test="${date != null && date != ''}">
                                                <input type="text" class="form-control"  id="startDate" placeholder="开始时间" value="${date}"/>
                                            </g:if>
                                            <g:if test="${date == null || date == ''}">
                                                <input type="text" class="form-control"  id="startDate" placeholder="开始时间"/>
                                            </g:if>
                                        </div>
                                    </div>
                                    <div class="col-md-4">
                                        <div class="col-xs-6">
                                            <g:if test="${date != null && date != ''}">
                                                <input type="text" class="form-control" id="endDate" placeholder="结束时间" value="${date}"/>
                                            </g:if>
                                            <g:if test="${date == null || date == ''}">
                                                <input type="text" class="form-control" id="endDate" placeholder="结束时间" />
                                            </g:if>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div>
                        <div class="form-group row m-b-10">
                            <div class="col-md-12 text-md-center">
                                <input type="button" class="btn btn-inverse" value="查询" id="query" name="query"/>
                                <label class="col-md-auto col-form-label">&nbsp;</label>
                                <input type="button" class="btn btn-inverse" value="重置" id="reset" name="reset"/>
                            </div>
                        </div>

                        <table id="data-table" class="table table-striped table-bordered display" style="width:100%;text-align: center">
                            <thead>
                            <tr>
                                <th class="text-nowrap" >序号</th>
                                <th class="text-nowrap" >案号</th>
                                <th class="text-nowrap" >案件名称</th>
                                <th class="text-nowrap">案件类型</th>
                                <th class="text-nowrap">排期开庭时间</th>
                                <th class="text-nowrap">排期法庭</th>
                                <th class="text-nowrap">主审法官</th>
                                <th class="text-nowrap">书记员</th>
                                <th class="text-nowrap">庭审状态</th>
                                <th class="text-nowrap">操作</th>
                            </tr>
                            </thead>
                            <tbody>
                            </tbody>
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
<asset:javascript src="parsley/dist/parsley.js"/>
<asset:javascript src="jquery-smart-wizard/src/js/jquery.smartWizard.js"/>
<asset:javascript src="jquery/jquery-migrate-1.1.0.min.js"/>
<asset:javascript src="bootstrap-daterangepicker/moment.min.js"/>
<asset:javascript src="bootstrap-select/bootstrap-select.min.js"/>
<asset:javascript src="bootstrap-eonasdan-datetimepicker/build/js/bootstrap-datetimepicker.min.js"/>
<asset:javascript src="jquery-tag-it/js/tag-it.min.js"/>
<asset:javascript src="select2/dist/js/select2.min.js"/>

<asset:javascript src="DataTables/media/js/jquery.dataTables.min.js"/>
<asset:javascript src="DataTables/media/js/dataTables.bootstrap.min.js"/>
<asset:javascript src="bootstrap-sweetalert/sweetalert.js"/>
<asset:javascript src="DataTables/extensions/Responsive/js/dataTables.responsive.min.js"/>
<asset:javascript src="leader/trialvideolist.js"/>
<script>
    $(document).ready(function () {
    });
</script>
<!-- ================== END PAGE LEVEL JS ================== -->
</body>
</html>
