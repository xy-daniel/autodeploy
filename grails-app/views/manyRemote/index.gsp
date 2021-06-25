<!DOCTYPE html>
<!--[if IE 8]> <html class="ie8" lang="CN"> <![endif]-->
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
    <asset:stylesheet href="parsley/src/parsley.css"/>
    <asset:stylesheet href="bootstrap-sweetalert/sweetalert.css"/>
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
    <g:render template="/layouts/base_sidebar" model="[active: 40100]"/>
    <!-- end #sidebar -->

    <!-- begin #content -->
    <div id="content" class="content">

        <!-- begin breadcrumb -->
        <ol class="breadcrumb pull-right">
            <li class="breadcrumb-item"><g:link controller="index">首页</g:link></li>
            <li class="breadcrumb-item active"><g:link controller="plan" action="list">三方远程</g:link></li>
        </ol>
        <!-- end breadcrumb -->

        <!-- begin page-header -->
        <h1 class="page-header">三方远程</h1>
        <!-- end page-header -->

        <!-- begin row -->
        <div class="row">
            <div class="col-lg-12">
                <!-- begin panel -->
                <div class="panel panel-inverse">
                    <!-- begin panel-heading -->
                    <div class="panel-heading">
                        <div class="panel-heading-btn">
                            <a href="javascript:void(0);" class="btn btn-xs btn-icon btn-circle btn-default" data-click="panel-expand"><i class="fa fa-expand"></i></a>
                        </div>
                        <h4 class="panel-title">三方远程</h4>
                    </div>
                    <!-- end panel-heading -->
                    <!-- begin panel-body -->
                    <div class="panel-body">
                        <!-- begin wizard-form -->
                        <form controller="manyRemote" action="connect" id="form-useradd" name="form-connect" class="form-control-with-bg form-horizontal" data-parsley-validate="true">
                            <!-- begin row -->
                            <div class="row">
                                <!-- begin col-8 -->
                                <div class="col-md-8 offset-md-2">
                                    <legend class="no-border f-w-700 p-b-0 m-t-0 m-b-20 f-s-16 text-inverse">请选择远端法院</legend>
                                    <!-- begin form-group--省 -->
%{--                                    <div class="form-group row m-b-10">--}%
%{--                                        <label class="col-md-3 col-form-label text-md-right">省/市/区 </label>--}%
%{--                                        <!-- 精确到市就会好做很多 -->--}%
%{--                                        <div class="col-md-6" >--}%
%{--                                            <div id="distpicker" class="row row-space-6" data-toggle="distpicker">--}%
%{--                                                <div class="col-4">--}%
%{--                                                    <select data-province="---- 省 ----" class="form-control" id="province" name="province" data-parsley-required="false"></select>--}%
%{--                                                </div>--}%
%{--                                                <div class="col-4">--}%
%{--                                                    <select data-city="---- 市 ----" class="form-control" id="city" name="city" data-parsley-required="false"></select>--}%
%{--                                                </div>--}%
%{--                                                <div class="col-4">--}%
%{--                                                    <select data-district="---- 区 ----" class="form-control" id="district" name="district" data-parsley-required="false"></select>--}%
%{--                                                </div>--}%
%{--                                            </div>--}%
%{--                                        </div>--}%
%{--                                </div>--}%
                                <!-- begin form-group--高级人民法院 -->
                                <div class="form-group row m-b-10">
                                    <label class="col-md-3 col-form-label text-md-right">高级人民法院 </label>
                                    <div class="col-md-6">
                                        <select id="higherCourt" class="form-control" data-size="10" data-live-search="true" data-style="btn-white" data-parsley-required="">
                                            <option value="">----高级人民法院----</option>
                                            <g:each in="${higherCourt}" var="court">
                                                <option value="${court}">
                                                    ${court}
                                                </option>
                                            </g:each>
                                        </select>
                                    </div>
                                </div>
                                <!-- end form-group--高级人民法院 -->
                                <!-- begin form-group--中级人民法院 -->
                                <div class="form-group row m-b-10">
                                    <label class="col-md-3 col-form-label text-md-right">中级人民法院 </label>
                                    <div class="col-md-6">
                                        <select id="seniorCourt" class="form-control" data-size="10" data-live-search="true" data-style="btn-white" data-parsley-required="">
                                            <option value="">----中级人民法院----</option>
                                        </select>
                                    </div>
                                </div>
                                <!-- end form-group--中级人民法院 -->
                                <!-- begin form-group--初级人民法院 -->
                                <div class="form-group row m-b-10">
                                    <label class="col-md-3 col-form-label text-md-right">初级人民法院 </label>
                                    <div class="col-md-6">
                                        <select id="court" class="form-control" data-size="10" data-live-search="true" data-style="btn-white" data-parsley-required="">
                                            <option value="">----初级人民法院----</option>
                                        </select>
                                    </div>
                                </div>
                                <!-- end form-group--初级人民法院 -->
                                <!-- begin form-group--法庭 -->
                                <div class="form-group row m-b-10">
                                    <label class="col-md-3 col-form-label text-md-right">远程法庭 </label>
                                    <div class="col-md-6">
                                        <select class="form-control" data-size="10" data-live-search="true" data-style="btn-white" name="room" data-parsley-required="false">
                                            <option value="">----远程法庭----</option>
%{--                                            <g:each in="${courts}" var="court" status="i">--}%
%{--                                                <option value="${i}">--}%
%{--                                                    ${court}--}%
%{--                                                </option>--}%
%{--                                            </g:each>--}%
                                        </select>
                                    </div>
                                </div>
                                <!-- end form-group--法庭 -->
                                <!-- begin form-group--提交按钮 -->
                                <div class="form-group row m-b-10">
                                    <label class="col-md-3 col-form-label">&nbsp;</label>
                                    <div class="col-md-6">
                                        <input type="submit" class="btn btn-primary" value="连接"/>
                                    </div>
                                </div>
                                <!--end form-group--提交按钮 -->
                            </div>
                            <!-- end col-8 -->
                            </div>
                            <!-- end row -->
                        </form>
                        <!-- end wizard-form -->
                    </div>
                </div>
                <!-- end panel -->
            </div>
        </div>
        <!--end row-->
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
<asset:javascript src="jquery-form/jquery.form.js"/>
<asset:javascript src="bootstrap-sweetalert/sweetalert.js"/>
<asset:javascript src="jquery/jquery-migrate-1.1.0.min.js"/>
<asset:javascript src="bootstrap-daterangepicker/moment.min.js"/>
<asset:javascript src="bootstrap-select/bootstrap-select.min.js"/>
<asset:javascript src="jquery-tag-it/js/tag-it.min.js"/>
<asset:javascript src="select2/dist/js/select2.min.js"/>
<asset:javascript src="distpicker/distpicker.js"/>
<asset:javascript src="manyRemote/index.js"/>
<script>
    $(document).ready(function () {
    });
</script>
<!-- ================== END PAGE LEVEL JS ================== -->
</body>
</html>
