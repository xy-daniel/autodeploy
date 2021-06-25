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
            <li class="breadcrumb-item active">修改排期</li>
        </ol>
        <!-- end breadcrumb -->
        <!-- begin page-header -->
        <h1 class="page-header">
            <g:if test="${grailsApplication.config.pageVersion == 'v1'}"> 庭审管理</g:if>
            <g:if test="${grailsApplication.config.pageVersion == 'v2'}"> 庭审预告</g:if>
            <small>修改排期</small>
        </h1>
        <!-- end page-header -->
        <!-- begin wizard-form -->
        <g:form controller="plan" action="editSave" id="${planInfo.id}" name="form-wizard" class="form-control-with-bg">
            <!-- begin wizard -->
            <div id="wizard">
                <!-- begin wizard-step -->
                <ul>
                    <li class="col-md-4 col-sm-4 col-6">
                        <a href="#step-1">
                            <span class="number">1</span>
                            <span class="info text-ellipsis">
                                修改案件
                                <small class="text-ellipsis">修改案件信息</small>
                            </span>
                        </a>
                    </li>
                    <li class="col-md-4 col-sm-4 col-6">
                        <a href="#step-2">
                            <span class="number">2</span>
                            <span class="info text-ellipsis">
                                修改排期
                                <small class="text-ellipsis">修改排期信息</small>
                            </span>
                        </a>
                    </li>
                    <li class="col-md-4 col-sm-4 col-6">
                        <a href="#step-3">
                            <span class="number">3</span>
                            <span class="info text-ellipsis">
                                修改远程庭审
                                <small class="text-ellipsis">修改远程庭审信息</small>
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
                                    <legend class="no-border f-w-700 p-b-0 m-t-0 m-b-20 f-s-16 text-inverse">修改案件请填写以下内容</legend>
                                    <input type="hidden" id="archivesPo" value="${planInfo.caseInfo.archives}">
                                    <!-- begin form-group -->
                                    <div class="form-group row m-b-10">
                                        <label class="col-md-3 col-form-label text-md-right">案号 <span class="text-danger">*</span></label>
                                        <div class="col-md-6">
                                            <input type="text" name="archives" id="archives" class="form-control" value="${planInfo.caseInfo.archives}"
                                                   data-parsley-group="step-1"
                                                   data-parsley-remote data-parsley-remote-validator='checkarchives' data-parsley-remote-message="案号已存在"
                                                   data-parsley-required="true" data-parsley-required-message="此项不能为空"
                                                   data-parsley-maxlength="200"/>
                                        </div>
                                    </div>
                                    <!-- end form-group -->
                                    <!-- begin form-group -->
                                    <div class="form-group row m-b-10">
                                        <label class="col-md-3 col-form-label text-md-right">案件名称 <span class="text-danger">*</span></label>
                                        <div class="col-md-6">
                                            <input type="text" name="name" class="form-control" value="${planInfo.caseInfo.name}"
                                                   data-parsley-group="step-1"
                                                   data-parsley-required="true" data-parsley-required-message="此项不能为空"
                                                   data-parsley-maxlength="500"/>
                                        </div>
                                    </div>
                                    <!-- end form-group -->
                                    <!-- begin form-group -->
                                    <div class="form-group row m-b-10">
                                        <label class="col-md-3 col-form-label text-md-right">案件类型 <span class="text-danger">*</span></label>
                                        <div class="col-md-6">
                                            <select class="form-control selectpicker plan-add-case-type" title="请选择" data-size="10" data-live-search="true" data-style="btn-white" name="type" data-parsley-group="step-1" data-parsley-required="true" data-parsley-required-message="此项不能为空">
                                                <g:each in="${caseTypeList}" var="caseType" status="i">
                                                    <g:if test="${!caseType.shortName}">
                                                        <g:if test="${i > 1}">
                                                            </optgroup>
                                                        </g:if>
                                                        <optgroup label="${caseType.name}">
                                                    </g:if>
                                                    <g:else>
                                                        <option value="${caseType?.id}" <g:if test="${planInfo.caseInfo.type == caseType}">selected</g:if>>
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
                                        <div class="col-md-6">
                                            <ul id="tagIt_accuser" class="primary">
                                                <g:each in="${planInfo.caseInfo.accuser?.split(",")}" var="accuser" status="i">
                                                    <li>${accuser}</li>
                                                </g:each>
                                            </ul>
                                        </div>
                                    </div>
                                    <!-- end form-group -->
                                    <!-- begin form-group -->
                                    <div class="form-group row m-b-10">
                                        <label class="col-md-3 col-form-label text-md-right">原告律师</label>
                                        <div class="col-md-6">
                                            <input type="text" name="prosecutionCounsel" data-parsley-group="step-1" value="${planInfo.caseInfo.prosecutionCounsel}" class="form-control" data-parsley-maxlength="200"/>
                                        </div>
                                    </div>
                                    <!-- end form-group -->
                                    <!-- begin form-group -->
                                    <div class="form-group row m-b-10">
                                        <label class="col-md-3 col-form-label text-md-right">被告 <span class="text-danger">*</span></label>
                                        <div class="col-md-6">
                                            <ul id="tagIt_accused" class="primary">
                                                <g:each in="${planInfo.caseInfo.accused?.split(",")}" var="accused" status="i">
                                                    <li>${accused}</li>
                                                </g:each>
                                            </ul>
                                        </div>
                                    </div>
                                    <!-- end form-group -->
                                    <!-- begin form-group -->
                                    <div class="form-group row m-b-10">
                                        <label class="col-md-3 col-form-label text-md-right">被告律师</label>
                                        <div class="col-md-6">
                                            <input type="text" name="counselDefence" value="${planInfo.caseInfo.counselDefence}" data-parsley-group="step-1" class="form-control" data-parsley-maxlength="200"/>
                                        </div>
                                    </div>
                                    <!-- end form-group -->
                                    <!-- begin form-group -->
                                    <div class="form-group row m-b-10">
                                        <label class="col-md-3 col-form-label text-md-right">立案日期 <span class="text-danger">*</span></label>
                                        <div class="col-md-6">
                                            <div class="input-group date" id="datetimepicker1">
                                                <input type="text" class="form-control" id="t1" name="filingDate" value="${planInfo.caseInfo.filingDate?.format('yyyy/MM/dd HH:mm')}" data-parsley-group="step-1" data-parsley-required="true" data-parsley-required-message="此项不能为空"/>
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
                                            <textarea class="form-control" rows="10" name="summary" data-parsley-group="step-1" data-parsley-maxlength="2000">${planInfo.caseInfo.summary}</textarea>
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
                    <!-- end step-1 -->
                    <!-- begin step-2 -->
                    <div id="step-2">
                        <!-- begin fieldset -->
                        <fieldset>
                            <!-- begin row -->
                            <div class="row">
                                <!-- begin col-8 -->
                                <div class="col-md-8 offset-md-2">
                                    <legend class="no-border f-w-700 p-b-0 m-t-0 m-b-20 f-s-16 text-inverse">修改排期请填写以下内容</legend>
                                    <!-- begin form-group -->
                                    <div class="form-group row m-b-10">
                                        <label class="col-md-3 col-form-label text-md-right">法庭 <span class="text-danger">*</span></label>
                                        <div class="col-md-6">
                                            <select class="form-control selectpicker" data-size="10" data-live-search="true" data-style="btn-white" name="courtroom" data-parsley-group="step-2" data-parsley-required="true" data-parsley-required-message="此项不能为空">
                                                <option value="">请选择</option>
                                                <g:each in="${courtroomList}" var="courtroom" status="i">
                                                    <option value="${courtroom.id}"<g:if test="${planInfo.courtroom?.id == courtroom?.id}"> selected</g:if>>${courtroom.name}</option>
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
                                                <input type="text" class="form-control" name="startDate" value="${planInfo.startDate?.format('yyyy/MM/dd HH:mm')}" data-parsley-group="step-2" data-parsley-required="true" data-parsley-required-message="此项不能为空"/>
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
                                                <input type="text" class="form-control" name="endDate" value="${planInfo.startDate?.format('yyyy/MM/dd HH:mm')}" data-parsley-group="step-2" data-parsley-required="true" data-parsley-required-message="此项不能为空"/>
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
                                            <select class="form-control selectpicker" data-size="10" data-live-search="true" data-style="btn-white" name="judge" data-parsley-group="step-2" data-parsley-required="true" data-parsley-required-message="此项不能为空">
                                                <option value="">请选择</option>
                                                <g:each in="${judgeList}" var="judge" status="i">
                                                    <option value="${judge?.id}"<g:if test="${planInfo.judge?.id == judge?.id}"> selected</g:if>>${judge.name}${judge.dept?"(${judge.dept?.name})":""}</option>
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
                                                        <g:each in="${planInfo.collegial}" var="collegial" status="i">
                                                            <li>${collegial.info}</li>
                                                        </g:each>
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
                                            <select class="form-control selectpicker" data-size="10" data-live-search="true" data-style="btn-white" name="secretary"  data-parsley-group="step-2" data-parsley-required="true" data-parsley-required-message="此项不能为空">
                                                <option value="">请选择</option>
                                                <g:each in="${secretaryList}" var="secretary" status="i">
                                                    <option value="${secretary.id}"<g:if test="${planInfo.secretary?.id == secretary?.id}"> selected</g:if>>${secretary.name}${secretary.dept?"(${secretary.dept?.name})":""}</option>
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
                    <!-- end step-2 -->
                    <!-- begin step-3 -->
                    <div id="step-3">
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
                                                <input type="checkbox" name="distanceArraigned" class="js-switch js-check-change" id="switcher_checkbox" value="${planInfo.distanceArraigned}"/>
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
                                                <g:each in="${planInfo.distanceSignature?.split(",")}" var="distanceSignature" status="i">
                                                    <li>${distanceSignature}</li>
                                                </g:each>
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
                    <!-- end step-3 -->
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
                        <option value="${judge.id}">${judge.name}${judge.dept?"(${judge.dept?.name})":""}</option>
                    </g:each>
                </select>
            </div>
            <div class="modal-footer">
                <a href="javascript:;" class="btn btn-white" data-dismiss="modal">关闭</a>
                <a href="javascript:;" class="btn btn-success modal_collegial-submit">添加</a>
            </div>
        </div>
    </div>
</div>

<g:render template="/layouts/base_bottom"/>
<!-- ================== BEGIN PAGE LEVEL JS ================== -->
<asset:javascript src="jquery/jquery-migrate-1.1.0.min.js"/>
<asset:javascript src="bootstrap-daterangepicker/moment.min.js"/>
<asset:javascript src="bootstrap-select/bootstrap-select.min.js"/>
<asset:javascript src="bootstrap-eonasdan-datetimepicker/build/js/bootstrap-datetimepicker.min.js"/>
<asset:javascript src="jquery-tag-it/js/tag-it.min.js"/>
<asset:javascript src="select2/dist/js/select2.min.js"/>
<asset:javascript src="parsley/dist/parsley.js"/>
<asset:javascript src="jquery-smart-wizard/src/js/jquery.smartWizard.js"/>
<asset:javascript src="DataTables/media/js/jquery.dataTables.min.js"/>
<asset:javascript src="DataTables/media/js/dataTables.bootstrap.min.js"/>
<asset:javascript src="DataTables/extensions/Responsive/js/dataTables.responsive.min.js"/>
<asset:javascript src="bootstrap-sweetalert/sweetalert.js"/>
<asset:javascript src="plan/edit.js"/>
<script>
    $(document).ready(function () {
    });
</script>
<!-- ================== END PAGE LEVEL JS ================== -->
</body>
</html>
