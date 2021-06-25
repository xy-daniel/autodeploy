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
    <asset:stylesheet href="jquery-smart-wizard/src/css/smart_wizard.css"/>
    <asset:stylesheet href="parsley/src/parsley.css"/>
    <asset:stylesheet href="bootstrap-eonasdan-datetimepicker/build/css/bootstrap-datetimepicker.min.css"/>
    <asset:stylesheet href="bootstrap-select/bootstrap-select.min.css"/>
    <asset:stylesheet href="jquery-tag-it/css/jquery.tagit.css"/>
    <asset:stylesheet href="select2/dist/css/select2.min.css"/>
    <asset:stylesheet href="DataTables/media/css/dataTables.bootstrap.min.css"/>
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
    <g:render template="/layouts/base_sidebar" model="[active: 20100]"/>
    <!-- end #sidebar -->

    <!-- begin #content -->
    <div id="content" class="content">
        <!-- begin breadcrumb -->
        <ol class="breadcrumb pull-right">
            <li class="breadcrumb-item"><g:link controller="index">首页</g:link></li>
            <li class="breadcrumb-item">
                <g:link controller="plan" action="list">
                    <g:if test="${grailsApplication.config.pageVersion == 'v1'}"> 庭审管理</g:if>
                    <g:if test="${grailsApplication.config.pageVersion == 'v2'}"> 庭审预告</g:if>
                </g:link>
            </li>
            <li class="breadcrumb-item active">创建新排期</li>
        </ol>
        <!-- end breadcrumb -->
        <!-- begin page-header -->
        <h1 class="page-header">
            <g:if test="${grailsApplication.config.pageVersion == 'v1'}"> 庭审管理</g:if>
            <g:if test="${grailsApplication.config.pageVersion == 'v2'}"> 庭审预告</g:if>
            <small>创建新排期</small>
        </h1>
        <!-- end page-header -->
        <!-- begin wizard-form -->
        <g:form controller="plan" action="addSave" name="form-wizard" class="form-control-with-bg">
            <!-- begin wizard -->
            <div id="wizard">
                <!-- begin wizard-step -->
                <ul>
                    <li class="col-md-3 col-sm-4 col-6">
                        <a href="#step-1">
                            <span class="number">1</span>
                            <span class="info text-ellipsis">
                                选择是否创建新案件
                                <small class="text-ellipsis">可以对一个案件进行多次排期</small>
                            </span>
                        </a>
                    </li>
                    <li class="col-md-3 col-sm-4 col-6">
                        <a href="#step-2">
                            <span class="number">2</span>
                            <span class="info text-ellipsis">
                                创建新案件
                                <small class="text-ellipsis">创建新的案件进行排期</small>
                            </span>
                        </a>
                    </li>
                    <li class="col-md-3 col-sm-4 col-6">
                        <a href="#step-3">
                            <span class="number">3</span>
                            <span class="info text-ellipsis">
                                创建新排期
                                <small class="text-ellipsis">对案件进行排期</small>
                            </span>
                        </a>
                    </li>
                    <li class="col-md-3 col-sm-4 col-6">
                        <a href="#step-4">
                            <span class="number">4</span>
                            <span class="info text-ellipsis">
                                远程庭审
                                <small class="text-ellipsis">远程庭审信息填写</small>
                            </span>
                        </a>
                    </li>
                </ul>
                <!-- end wizard-step -->
                <!-- begin wizard-content -->
                <div>
                    <!-- begin step-1 -->
                    <div id="step-1">
                        <!-- begin fieldset -->
                        <fieldset>
                            <!-- begin row -->
                            <div class="row">
                                <!-- begin col-8 -->
                                <div class="col-md-8 offset-md-2">
                                    <legend class="no-border f-w-700 p-b-0 m-t-0 m-b-20 f-s-16 text-inverse">请选择已有案件或者创建新案件</legend>
                                    <!-- begin form-group -->
                                    <div class="row">
                                        <div class="col-md-6 m-b-15 text-center">
                                            <a href="#modal_case" class="btn btn-lg btn-primary plan-wizard-btn" data-toggle="modal">
                                                <i class="fa fa-edit fa-2x pull-left m-r-10"></i>
                                                <b>已有案件排期</b>
                                            </a>
                                        </div>
                                        <div class="col-md-6 m-b-15 text-center">
                                            <a href="#" class="btn btn-lg btn-primary plan-wizard-btn plan-wizard-btn-add">
                                                <i class="fa fa-plus-square fa-2x pull-left m-r-10"></i>
                                                <b>新案件排期</b>
                                            </a>
                                        </div>
                                    </div>
                                    <!-- end form-group -->
                                </div>
                            </div>
                            <!-- end row -->
                        </fieldset>
                        <!-- end fieldset -->
                    </div>
                    <!-- end step-1 -->
                    <!-- begin step-2 -->
                    <div id="step-2">
                        <!-- begin fieldset -->
                        <fieldset>
                            <!-- begin row -->
                            <div class="row">
                                <!-- begin col-8 -->
                                <div class="col-md-8 offset-md-2">
                                    <legend class="no-border f-w-700 p-b-0 m-t-0 m-b-20 f-s-16 text-inverse">创建新案件请填写以下内容</legend>
                                    <input type="hidden" name="caseId"/>
                                    <!-- begin form-group -->
                                    <div class="form-group row m-b-10">
                                        <label class="col-md-3 col-form-label text-md-right">案号 <span class="text-danger">*</span></label>
                                        <div class="col-md-6">
                                            <input type="text" name="archives" id="archives" class="form-control"
                                                   data-parsley-group="step-2"
                                                   data-parsley-required="true" data-parsley-required-message="此项不能为空"
                                                   data-parsley-remote data-parsley-remote-validator='checkarchives' data-parsley-remote-message="案号已存在"
                                                   data-parsley-maxlength="200"/>
                                        </div>
                                    </div>
                                    <!-- end form-group -->
                                    <!-- begin form-group -->
                                    <div class="form-group row m-b-10">
                                        <label class="col-md-3 col-form-label text-md-right">案件名称 <span class="text-danger">*</span></label>
                                        <div class="col-md-6">
                                            <input type="text" name="name" class="form-control" data-parsley-group="step-2" data-parsley-required="true" data-parsley-required-message="此项不能为空" data-parsley-maxlength="500"/>
                                        </div>
                                    </div>
                                    <!-- end form-group -->
                                    <!-- begin form-group -->
                                    <div class="form-group row m-b-10">
                                        <label class="col-md-3 col-form-label text-md-right">案件类型 <span class="text-danger">*</span></label>
                                        <div class="col-md-6">
                                            <select class="form-control selectpicker plan-add-case-type" title="请选择"
                                                    data-size="10" data-live-search="true" data-style="btn-white" name="type"
                                                    data-parsley-group="step-2"
                                                    data-parsley-required="true" data-parsley-required-message="此项不能为空">
                                                <option value="">请选择</option>
                                                <g:each in="${caseTypeList}" var="caseType" status="i">
                                                    <g:if test="${!caseType.shortName}">
                                                        <g:if test="${i > 1}">
                                                            </optgroup>
                                                        </g:if>
                                                        <optgroup label="${caseType.name}">
                                                    </g:if>
                                                    <g:else>
                                                        <option value="${caseType?.id}">
                                                            ${caseType.name}${caseType.shortName?"-"+caseType.shortName:""}
                                                        </option>
                                                    </g:else>
                                                </g:each>
                                            </select>
                                        </div>
                                    </div>
                                    <!-- end form-group -->
                                    <!-- begin form-group -->
                                    <div class="form-group row m-b-10">
                                        <label class="col-md-3 col-form-label text-md-right">原告 <span class="text-danger">*</span></label>
%{--                                        <div class="col-md-6">--}%
%{--                                            <input type="text" name="accuser" class="form-control" data-parsley-group="step-2" data-parsley-required="true" data-parsley-required-message="此项不能为空" data-parsley-maxlength="2000"/>--}%
%{--                                        </div>--}%
                                        <div class="col-md-6">
                                            <ul id="tagIt_accuser" class="primary">
                                            </ul>
                                        </div>
                                    </div>
                                    <!-- end form-group -->
                                    <!-- begin form-group -->
                                    <div class="form-group row m-b-10">
                                        <label class="col-md-3 col-form-label text-md-right">原告律师</label>
                                        <div class="col-md-6">
                                            <input type="text" name="prosecutionCounsel" data-parsley-group="step-2" class="form-control" data-parsley-maxlength="200"/>
                                        </div>
                                    </div>
                                    <!-- end form-group -->
                                    <!-- begin form-group -->
                                    <div class="form-group row m-b-10">
                                        <label class="col-md-3 col-form-label text-md-right">被告 <span class="text-danger">*</span></label>
%{--                                        <div class="col-md-6">--}%
%{--                                            <input type="text" name="accused" class="form-control" data-parsley-group="step-2" data-parsley-required="true" data-parsley-required-message="此项不能为空" data-parsley-maxlength="2000"/>--}%
%{--                                        </div>--}%
                                        <div class="col-md-6">
                                            <ul id="tagIt_accused" class="primary"
                                                data-parsley-group="step-2" data-parsley-required="true"
                                                data-parsley-remote data-parsley-remote-validator='check_accused' data-parsley-remote-message="此项不能为空">
                                            </ul>
                                        </div>
                                    </div>
                                    <!-- end form-group -->
                                    <!-- begin form-group -->
                                    <div class="form-group row m-b-10">
                                        <label class="col-md-3 col-form-label text-md-right">被告律师</label>
                                        <div class="col-md-6">
                                            <input type="text" name="counselDefence" data-parsley-group="step-2" class="form-control" data-parsley-maxlength="200"/>
                                        </div>
                                    </div>
                                    <!-- end form-group -->
                                    <!-- begin form-group -->
                                    <div class="form-group row m-b-10">
                                        <label class="col-md-3 col-form-label text-md-right">立案日期 <span class="text-danger">*</span></label>
                                        <div class="col-md-6">
                                            <div class="input-group date" id="datetimepicker1">
                                                <input type="text" class="form-control" id="t1" name="filingDate"
                                                       data-parsley-group="step-2"
                                                       data-parsley-required="true" data-parsley-required-message="此项不能为空"/>
                                                <div class="input-group-addon">
                                                    <i class="fa fa-calendar"></i>
                                                </div>
                                            </div>
                                        </div>
                                    </div>
                                    <!-- end form-group -->
                                    <!-- begin form-group -->
                                    <div class="form-group row m-b-10">
                                        <label class="col-md-3 col-form-label text-md-right">案件概要</label>
                                        <div class="col-md-6">
                                            <textarea class="form-control" rows="10" data-parsley-group="step-2" name="summary" data-parsley-maxlength="2000"></textarea>
                                        </div>
                                    </div>
                                    <!-- end form-group -->

                                </div>
                                <!-- end col-8 -->
                            </div>
                            <!-- end row -->
                        </fieldset>
                        <!-- end fieldset -->
                    </div>
                    <!-- end step-2 -->
                    <!-- begin step-3 -->
                    <div id="step-3">
                        <!-- begin fieldset -->
                        <fieldset>
                            <!-- begin row -->
                            <div class="row">
                                <!-- begin col-8 -->
                                <div class="col-md-8 offset-md-2">
                                    <legend class="no-border f-w-700 p-b-0 m-t-0 m-b-20 f-s-16 text-inverse">创建新排期请填写以下内容</legend>
                                    <!-- begin form-group -->
                                    <div class="form-group row m-b-10">
                                        <label class="col-md-3 col-form-label text-md-right">法庭 <span class="text-danger">*</span></label>
                                        <div class="col-md-6">
                                            <select class="form-control selectpicker" data-size="10" data-live-search="true" data-style="btn-white" name="courtroom" data-parsley-group="step-3" data-parsley-required="true" data-parsley-required-message="此项不能为空">
                                                <option value="">请选择</option>
                                                <g:each in="${courtroomList}" var="courtroom" status="i">
                                                    <option value="${courtroom.id}">${courtroom.name}</option>
                                                </g:each>
                                            </select>
                                        </div>
                                    </div>
                                    <!-- end form-group -->
                                    <!-- begin form-group -->
                                    <div class="form-group row m-b-10">
                                        <label class="col-md-3 col-form-label text-md-right">计划开庭日期 <span class="text-danger">*</span></label>
                                        <div class="col-md-6">
                                            <div class="input-group date" id="datetimepicker2">
                                                <input type="text" class="form-control" name="startDate" data-parsley-group="step-3" data-parsley-required="true" data-parsley-required-message="此项不能为空"/>
                                                <div class="input-group-addon">
                                                    <i class="fa fa-calendar"></i>
                                                </div>
                                            </div>
                                        </div>
                                    </div>
                                    <!-- end form-group -->
                                    <!-- begin form-group -->
                                    <div class="form-group row m-b-10">
                                        <label class="col-md-3 col-form-label text-md-right">计划闭庭日期 <span class="text-danger">*</span></label>
                                        <div class="col-md-6">
                                            <div class="input-group date" id="datetimepicker3">
                                                <input type="text" class="form-control" name="endDate" data-parsley-group="step-3" data-parsley-required="true" data-parsley-required-message="此项不能为空"/>
                                                <div class="input-group-addon">
                                                    <i class="fa fa-calendar"></i>
                                                </div>
                                            </div>
                                        </div>
                                    </div>
                                    <!-- end form-group -->
                                    <!-- begin form-group -->
                                    <div class="form-group row m-b-10">
                                        <label class="col-md-3 col-form-label text-md-right">审判长 <span class="text-danger">*</span></label>
                                        <div class="col-md-6">
                                            <select class="form-control selectpicker" data-size="10" data-live-search="true" data-style="btn-white" name="judge" data-parsley-group="step-3" data-parsley-required="true" data-parsley-required-message="此项不能为空">
                                                <option value="">请选择</option>
                                                <g:each in="${judgeList}" var="judge" status="i">
                                                    <option value="${judge.id}">${judge.name}${judge.dept?"(${judge.dept.name})":""}</option>
                                                </g:each>
                                            </select>
                                        </div>
                                    </div>
                                    <!-- end form-group -->
                                    <!-- begin form-group -->
                                    <div class="form-group row m-b-10">
                                        <label class="col-md-3 col-form-label text-md-right">其他合议庭成员</label>
                                        <div class="col-md-6">
                                            <div class="row">
                                                <div class="col-md-3">
                                                    <a href="#modal_collegial" class="btn btn-primary btn-block" data-toggle="modal">添加成员</a>
                                                </div>
                                                <div class="col-md-9">
                                                    <ul id="tagIt_collegial" class="primary">
                                                    </ul>
                                                </div>
                                            </div>
                                            <small class="f-s-12 text-grey-darker">合议庭成员可以选择填入，如列表中没有也可直接输入后按回车添加。</small>
                                        </div>
                                    </div>
                                    <!-- end form-group -->
                                    <!-- begin form-group -->
                                    <div class="form-group row m-b-10">
                                        <label class="col-md-3 col-form-label text-md-right">书记员 <span class="text-danger">*</span></label>
                                        <div class="col-md-6">
                                            <select class="form-control selectpicker" data-size="10" data-live-search="true" data-style="btn-white" name="secretary"  data-parsley-group="step-3" data-parsley-required="true" data-parsley-required-message="此项不能为空">
                                                <option value="">请选择</option>
                                                <g:each in="${secretaryList}" var="secretary" status="i">
                                                    <option value="${secretary.id}">${secretary.name}${secretary.dept?"(${secretary.dept.name})":""}</option>
                                                </g:each>
                                            </select>
                                        </div>
                                    </div>
                                    <!-- end form-group -->
                                </div>
                                <!-- end col-8 -->
                            </div>
                            <!-- end row -->
                        </fieldset>
                        <!-- end fieldset -->
                    </div>
                    <!-- end step-3 -->
                    <!-- begin step-4 -->
                    <div id="step-4">
                        <!-- begin fieldset -->
                        <fieldset>
                            <!-- begin row -->
                            <div class="row">
                                <!-- begin col-8 -->
                                <div class="col-md-8 offset-md-2">
                                    <legend class="no-border f-w-700 p-b-0 m-t-0 m-b-20 f-s-16 text-inverse">此排期是否是远程庭审</legend>
                                    <!-- begin form-group -->
                                    <div class="form-group row m-b-10">
                                        <label class="col-md-3 col-form-label text-md-right">远程庭审</label>
                                        <div class="col-md-6">
                                            <div class="switcher switcher-success">
                                                <input type="checkbox" name="distanceArraigned" id="switcher_checkbox" value="1">
                                                <label for="switcher_checkbox"></label>
                                            </div>
                                        </div>
                                    </div>
                                    <!-- end form-group -->
                                    <!-- begin form-group -->
                                    <div class="form-group row m-b-10 distance-signature dis">
                                        <label class="col-md-3 col-form-label text-md-right">人员签名</label>
                                        <div class="col-md-6">
                                            <ul id="tagIt-distance_signature" class="primary">
                                            </ul>
                                            <small class="f-s-12 text-grey-darker">输入后按回车添加。</small>
                                        </div>
                                    </div>
                                    <!-- end form-group -->
                                </div>
                                <!-- end col-8 -->
                            </div>
                            <!-- end row -->
                        </fieldset>
                        <!-- end fieldset -->
                    </div>
                    <!-- end step-4 -->
                </div>
                <!-- end wizard-content -->
            </div>
            <!-- end wizard -->
        </g:form>
        <!-- end wizard-form -->
    </div>
    <!-- end #content -->

    <!-- begin scroll to top btn -->
    <g:render template="/layouts/base_topbtn"/>
    <!-- end scroll to top btn -->
</div>
<!-- end page container -->


<div class="modal modal-message fade" id="modal_case">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <h4 class="modal-title">选择案件</h4>
                <button type="button" class="close" data-dismiss="modal" aria-hidden="true">×</button>
            </div>
            <div class="modal-body">
                <p>
                    请选择要排期的案件。
                </p>
                <table id="data-table" class="table table-striped table-bordered display" style="width:100%">
                    <thead>
                    <tr>
                        <th class="text-nowrap">案号</th>
                        <th class="text-nowrap" style="max-width: 250px">案件名称</th>
                        <th class="text-nowrap" style="min-width: 44px; max-width: 250px">原告</th>
                        <th class="text-nowrap" style="min-width: 44px; max-width: 250px">被告</th>
                        <th class="text-nowrap" style="min-width: 100px">立案日期</th>
                        <th class="text-nowrap" style="min-width: 44px">操作</th>
                    </tr>
                    </thead>
                    <tbody>
                    </tbody>
                </table>
            </div>
            <div class="modal-footer">
                <a href="javascript:" class="btn btn-white" data-dismiss="modal">关闭</a>
            </div>
        </div>
    </div>
</div>

<div class="modal fade" id="modal_collegial">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <h4 class="modal-title">添加合议庭成员</h4>
                <button type="button" class="close" data-dismiss="modal" aria-hidden="true">×</button>
            </div>
            <div class="modal-body">
                <p>
                    请选择成员与成员类型。
                </p>
                <select class="form-control selectpicker m-b-15" data-size="10" data-live-search="true" data-style="btn-white" id="select_collegial_type">
                    <option value="">选择成员类型</option>
                    <option value="2">审判员</option>
                    <option value="4">人民陪审员</option>
                    <option value="255">其他</option>
                </select>
                <select class="form-control selectpicker" data-size="10" data-live-search="true" data-style="btn-white" id="select_collegial">
                    <option value="">选择成员</option>
                    <g:each in="${judgeList}" var="judge" status="i">
                        <option value="${judge.id}">${judge.name}${judge.dept?"(${judge.dept.name})":""}</option>
                    </g:each>
                </select>
            </div>
            <div class="modal-footer">
                <a href="javascript:" class="btn btn-white" data-dismiss="modal">关闭</a>
                <a href="javascript:" class="btn btn-success modal_collegial-submit">添加</a>
            </div>
        </div>
    </div>
</div>

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
<asset:javascript src="DataTables/extensions/Responsive/js/dataTables.responsive.min.js"/>
<asset:javascript src="plan/add.js"/>
<script>
    $(document).ready(function () {
    });
</script>
<!-- ================== END PAGE LEVEL JS ================== -->
</body>
</html>
