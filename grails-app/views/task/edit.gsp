<!DOCTYPE html>
<!--[if IE 8]> <html class="ie8"> <![endif]-->
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
    <asset:stylesheet href="parsley/src/parsley.css"/>
    <asset:stylesheet href="bootstrap-sweetalert/sweetalert.css"/>
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
    <g:render template="/layouts/base_sidebar" model="[active: 30002]"/>
    <!-- end #sidebar -->

    <!-- begin #content -->
    <div id="content" class="content">
        <!-- begin breadcrumb -->
        <ol class="breadcrumb pull-right">
            <li class="breadcrumb-item"><g:link controller="index">首页</g:link></li>
            <li class="breadcrumb-item"><g:link controller="task" action="list">首页</g:link></li>
            <li class="breadcrumb-item active">
                编辑任务
            </li>
        </ol>
        <!-- end breadcrumb -->
        <!-- begin page-header -->
        <h1 class="page-header">
            任务中心
            <small>编辑任务</small>
        </h1>
        <!-- end page-header -->
        <div class="panel panel-inverse">
            <div class="panel-heading">
                <div class="panel-heading-btn">
                    <a href="javascript:void(0);" class="btn btn-xs btn-icon btn-circle btn-default"
                       data-click="panel-expand"><i class="fa fa-expand"></i></a>
                </div>
                <h4 class="panel-title">修改法庭信息</h4>
            </div>

            <div class="panel-body">
                <g:form controller="task" action="editSave" id="${task.id}" method="POST"
                        data-parsley-validate="true">
                    <input id="task" type="hidden" name="id" value="${task.id}"/>

                    <div class="row">
                        <div class="col-md-7 b-r-1">
                            <!-- begin form-group row -->
                            <div class="form-group row m-b-10">
                                <label class="col-md-2 text-md-right col-form-label">任务名称<span
                                        class="text-danger">*</span></label>

                                <div class="col-md-10">
                                    <input type="text" class="form-control m-b-5" name="name" value="${task.name}"
                                           data-parsley-required="true" data-parsley-required-message="此项不能为空"/>
                                </div>
                            </div>
                            <!-- end form-group row -->


                            <!-- begin form-group row -->
                            <div class="form-group row m-b-10">
                                <label class="col-md-2 text-md-right col-form-label">绑定主机<span
                                        class="text-danger">&nbsp;&nbsp;&nbsp;</span></label>

                                <div class="col-md-10">
                                    <g:each in="${devices}" var="device">
                                        <div class="form-check form-check-inline m-t-2">
                                            <input class="form-check-input" type="checkbox" id="device${device.id}"
                                                   name="devices" value="${device.id}"
                                                <g:each in="${hasDevice}" var="has">
                                                    <g:if test="${has.id == device.id}">checked</g:if>
                                                </g:each>/>
                                            <label class="form-check-label"
                                                   for="device${device.id}">${device.name}</label>
                                        </div>
                                    </g:each>
                                </div>
                            </div>
                            <!-- end form-group row -->
                            <!-- begin form-group row -->
                            <div class="form-group row m-b-10">
                                <label class="col-md-2 text-md-right col-form-label">任务指令<span
                                        class="text-danger">*</span></label>

                                <div class="col-md-10">
                                    <input type="text" class="form-control m-b-5" name="content"
                                           value="${task.content[0]}"
                                           data-parsley-required="true" data-parsley-required-message="此项不能为空"/>
                                </div>
                            </div>
                        <!-- end form-group row -->
                            <g:each in="${task.content}" var="content" status="i">
                                <g:if test="${i != 0}">
                                    <div class="form-group row m-b-10">
                                        <label class="col-md-2 text-md-right col-form-label"></label>

                                        <div class="col-md-10">
                                            <input type="text" class="form-control m-b-5" name="content"
                                                   value="${content}"/>
                                        </div>
                                    </div>
                                </g:if>
                            </g:each>
                            <div class="form-group row m-b-10">
                                <label class="col-md-2 text-md-right col-form-label"></label>

                                <div class="col-md-10">
                                    <input type="text" class="form-control m-b-5" name="content"/>
                                </div>
                            </div>

                            <div class="form-group row m-b-10" id="commit">
                                <label class="col-md-2 text-md-right col-form-label"></label>

                                <div class="col-md-10">
                                    <button type="submit" class="btn btn-sm btn-primary m-r-5">提交</button>
                                    <button type="button" class="btn btn-sm btn-primary m-r-5"
                                            id="commandAdd">添加指令框</button>
                                </div>
                            </div>
                        </div>

                        <div class="col-md-5 p-l-40 b-l-1">
                            <h5>点击按钮自动生成并复制上传应用文件指令<span class="text-red">(请用根据实际情况替换`自定义上传路径`)</span></h5>

                            <div>
                                <g:each in="${appList}" var="app">
                                    <div class="text-left m-b-10">${app.itemName}</div>
                                    <g:each in="${app.versions}" var="version">
                                        <g:if test="${version.del == null || version.del == ""}">
                                            <div class="btn btn-sm btn-primary m-b-5 version">${version.number}</div>
                                            <input type="hidden" value="scp from ${version.path} to `自定义上传路径`">
                                        </g:if>
                                    </g:each>
                                </g:each>
                            </div>
                        </div>
                    </div>
                </g:form>
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
<asset:javascript src="jquery/jquery-migrate-1.1.0.min.js"/>
<asset:javascript src="parsley/dist/parsley.js"/>
<asset:javascript src="jquery-form/jquery.form.js"/>
<asset:javascript src="bootstrap-sweetalert/sweetalert.js"/>
<asset:javascript src="bootstrap-daterangepicker/moment.min.js"/>
<asset:javascript src="bootstrap-select/bootstrap-select.min.js"/>
<asset:javascript src="bootstrap-eonasdan-datetimepicker/build/js/bootstrap-datetimepicker.min.js"/>
<asset:javascript src="jquery-tag-it/js/tag-it.min.js"/>
<asset:javascript src="select2/dist/js/select2.min.js"/>
<asset:javascript src="task/edit.js"/>
<script>
    $(document).ready(function () {
    });
</script>
<!-- ================== END PAGE LEVEL JS ================== -->
</body>
</html>
