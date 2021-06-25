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

    <asset:stylesheet href="jquery-smart-wizard/src/css/smart_wizard.css"/>
    <asset:stylesheet href="parsley/src/parsley.css"/>
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
        <g:render template="/layouts/base_sidebar" model="[active: 32000]"/>
    </g:if>
    <g:if test="${grailsApplication.config.pageVersion == 'v2'}"> 
        <g:render template="/layouts/base_sidebar" model="[active: 5600]"/>
    </g:if>
    
    <!-- end #sidebar -->

    <!-- begin #content -->
    <div id="content" class="content">
        <!-- begin breadcrumb -->
        <ol class="breadcrumb pull-right">
            <li class="breadcrumb-item"><g:link controller="index">首页</g:link></li>
            <li class="breadcrumb-item">
                庭审信息管理
            </li>
        </ol>
        <!-- end breadcrumb -->
        <!-- begin page-header -->
        <h1 class="page-header">
            庭审信息管理
        </h1>
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
                        <h4 class="panel-title">庭审列表</h4>
                    </div>
                    <div class="panel-body">
                        <!-- 区分v2预告 -->
                        <g:if test="${grailsApplication.config.pageVersion == 'v2'}">
                            <input type="text" name="pageType"  class="form-control" id="pageType" value="trialNotice" hidden="hidden"/>
                        </g:if>

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
                                <select class="form-control selectpicker" data-size="10" data-live-search="true" data-style="btn-white" id="courtroom" name="courtroom">
                                    <option value="" selected="selected">请选择</option>
                                    <g:each in="${courtroomList}" var="courtroom" status="i">
                                        <option value="${courtroom.id}">${courtroom.name}</option>
                                    </g:each>
                                </select>
                            </div>
                            <!-- end form-group -->
                            <label class="col-md-1 text-md-right col-form-label">主审法官：</label>
                            <div class="col-md-2" >
                                <select class="form-control selectpicker" data-size="10" data-live-search="true" data-style="btn-white" id="judge" name="judge">
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
                                <select class="form-control selectpicker" data-size="10" data-live-search="true" data-style="btn-white" id="secretary" name="secretary">
                                    <option value="" selected="selected">请选择</option>
                                    <g:each in="${secretaryList}" var="secretarylist" status="i">
                                        <option value="${secretarylist.id}">${secretarylist.name}</option>
                                    </g:each>
                                </select>
                            </div>


                            <g:if test="${grailsApplication.config.pageVersion == 'v1'}">
                                <label class="col-md-1 text-md-right col-form-label">庭审状态：</label>
                                <div class="col-md-2">
                                    <select class="form-control selectpicker" data-size="10" data-live-search="true" data-style="btn-white" id="status" name="status">
                                        <option value="" selected="selected">请选择</option>
                                        <option value=2>休庭</option>
                                        <option value=3>闭庭</option>
                                        <option value=4>归档</option>
                                    </select>
                                </div>
                            </g:if>

                            <label class="col-md-1 col-form-label text-md-right">开庭时间范围：</label>
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
                        <div class="form-group row m-b-10">
                            <div class="col-md-3">
                                <a href="#cdBurn" class="btn btn-sm btn-inverse btn-add cdBurn" data-toggle="modal">集中刻录</a>
                            </div>
                        </div>
                        <table id="data-table" class="table table-striped table-bordered display" style="width:100%">
                            <thead>
                            <tr>
                                <th class="with-checkbox">
                                    %{--<div class="checkbox checkbox-css">
                                        <input type="checkbox" value="" id="table_checkbox_all" />
                                        <label for="table_checkbox_all">&nbsp;</label>
                                    </div>--}%
                                </th>
                                <th class="text-nowrap" style="width: 200px">案号</th>
                                <th class="text-nowrap" style="width: 200px">案件名称</th>
                                <th class="text-nowrap">排期法庭</th>
                                <th class="text-nowrap">主审法官</th>
                                <th class="text-nowrap">书记员</th>
                                <th class="text-nowrap">开庭时间</th>
                                <th class="text-nowrap">闭庭时间</th>
                                <th class="text-nowrap">庭审状态</th>
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
<form action="" id="cdBurnForm" name="cdBurn">
    <div class="modal fade" id="cdBurn">
        <div class="modal-dialog">
            <div class="modal-content">
                <div class="modal-header">
                    <h4 class="modal-title">集中刻录向导</h4>
                    <button type="button" class="close" data-dismiss="modal" aria-hidden="true">×</button>
                </div>
                <div class="modal-body">
                    <p>
                        请输入需要刻录的份数和选择刻录通道。
                    </p>
                    <div class="form-group row m-b-10">
                        <label class="col-md-3 col-form-label text-md-right">刻录份数 <span class="text-danger">*</span></label>
                        <div class="col-md-6">
                            <input type="text" placeholder="刻录份数" name="burnNum" id="burnNum" class="form-control" data-parsley-required="true" data-parsley-required-message="此项不能为空" />
                        </div>
                    </div>
                    <div class="form-group row m-b-10">
                        <label class="col-md-3 col-form-label text-md-right">刻录通道 <span class="text-danger">*</span></label>
                        <div class="col-md-6">
                            <select class="form-control cdChn" name="chnName" data-size="10" data-live-search="true" data-style="btn-white"
                                    id="select_cdChn" data-parsley-required="true" data-parsley-required-message="此项不能为空">
                            </select>
                        </div>
                        <input type="hidden" name="trialId" id="trialId">
                    </div>
                </div>
                <div class="modal-footer">
                    <a href="javascript:" class="btn btn-white" data-dismiss="modal">关闭</a>
                    <input type="submit" class="btn btn-primary modal_burn-submit" value="刻录"/>
                </div>
            </div>
        </div>
    </div>
</form>
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
<asset:javascript src="sockjs/sockjs.min.js"/>
<asset:javascript src="stomp/stomp.min.js"/>
<asset:javascript src="trial/list.js"/>
<script>
    $(document).ready(function () {
    });
</script>
<!-- ================== END PAGE LEVEL JS ================== -->
</body>
</html>
