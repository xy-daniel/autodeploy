<!DOCTYPE html>
<!--[if IE 8]> <html class="ie8" lang="CN"> <![endif]-->
<!--[if !IE]><!-->
<html>
<!--<![endif]-->
<head>
    <meta charset="utf-8" />
    <title></title>
    <meta content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=no" name="viewport" />
    <meta content="" name="description" />
    <meta content="" name="author" />

    <!-- ================== BEGIN BASE CSS STYLE ================== -->
    <g:render template="/layouts/base_head"/>
    <!-- ================== END BASE CSS STYLE ================== -->

    <!-- ================== BEGIN PAGE LEVEL STYLE ================== -->
    <asset:stylesheet href="bootstrap-sweetalert/sweetalert.css"/>
    <asset:stylesheet href="bootstrap-select/bootstrap-select.min.css"/>
    <asset:stylesheet href="select2/dist/css/select2.min.css"/>
    <!-- ================== END PAGE LEVEL STYLE ================== -->
</head>
<body>
<!-- begin #page-loader -->
%{--<g:render template="/layouts/base_loader"/>--}%
<!-- end #page-loader -->

<!-- begin #page-container -->
<div id="page-container" class="page-container fade page-without-sidebar page-header-fixed page-with-top-menu">
    <!-- begin #header -->
    <!-- begin #content -->
    <div id="content" class="content">
        <!-- begin row -->
        <div class="row">
            <div class="col-lg-12">
                <div class="panel panel-inverse">
                    <!-- begin panel-body -->
                    <div class="panel-body">
                        <!-- begin fieldset -->
                        <fieldset>
                            <!-- begin row -->
                            <div class="row">
                                <!-- begin col-8 -->
                                <div class="col-md-8 offset-md-2">
                                    <legend class="no-border f-w-700 p-b-0 m-t-0 m-b-20 f-s-16 text-inverse">声音控制设置</legend>
                                    <!-- begin form-group -->
                                    <div class="row m-b-10">
                                        <label class="col-md-5 col-form-label text-md-right">选择法庭<span class="text-danger">*</span></label>
                                        <div class="col-md-6">
                                            <select class="form-control selectpicker" data-size="10" data-live-search="true" data-style="btn-white" name="courtroom" >
                                                <option value="" selected="selected">请选择</option>
                                                <g:each in="${courtroomList}" var="courtroom" status="i">
                                                    <option value="${courtroom.deviceIp}">${courtroom.name}</option>
                                                </g:each>
                                            </select>
                                        </div>
                                    </div>
                                    <!-- end form-group -->
                                    <!-- begin  -->
                                    <div class="row m-b-10">
                                        <label class="col-md-5 col-form-label text-md-right">公诉人 </label>
                                        <div class="col-md-6">
                                            <input type="text" class="form-control" value="4,5" id="gsr" name="gsr"/>
                                        </div>
                                    </div>
                                    <!-- end  -->
                                    <!-- begin  -->
                                    <div class=" row m-b-10">
                                        <label class="col-md-5 col-form-label text-md-right">律师 </label>
                                        <div class="col-md-6">
                                            <input type="text" class="form-control" value="6" id="ls" name="ls"/>
                                        </div>
                                    </div>
                                    <!-- end  -->
                                    <!-- begin  -->
                                    <div class=" row m-b-10">
                                        <label class="col-md-5 col-form-label text-md-right">现场嫌疑人 </label>
                                        <div class="col-md-6">
                                            <input type="text" class="form-control" value="7,8,9" id="xcxyr" name="xcxyr"/>
                                        </div>
                                    </div>
                                    <!-- end  -->
                                    <!-- begin  -->
                                    <div class=" row m-b-10">
                                        <label class="col-md-5 col-form-label text-md-right">远程嫌疑人 </label>
                                        <div class="col-md-6">
                                            <input type="text" class="form-control" value="14" id="ycxyr" name="ycxyr"/>
                                        </div>
                                    </div>
                                    <!-- end  -->
                                    <div class=" row m-b-10">
                                        <label class="col-md-5 col-form-label text-md-right"></label>
                                        <div class="col-md-6">
                                            <input type="button" class="btn btn-inverse" value="保存设置" id="saveCourt" name="saveCourt"/>
                                        </div>
                                    </div>

                                </div>
                                <!-- end col-8 -->
                            </div>
                            <!-- end row -->
                        </fieldset>
                        <!-- end fieldset -->
                    </div>
                </div>
                <!-- end panel -->
            </div>
        </div>
        <!--end row-->
    </div>
    <!-- end #content -->

    <!-- begin scroll to top btn -->
%{--    <g:render template="/layouts/base_topbtn"/>--}%
    <!-- end scroll to top btn -->
</div>
<!-- end page container -->
<g:render template="/layouts/base_bottom"/>
<asset:javascript src="bootstrap-sweetalert/sweetalert.js"/>
<asset:javascript src="bootstrap-select/bootstrap-select.min.js"/>
<asset:javascript src="select2/dist/js/select2.min.js"/>
<!-- ================== BEGIN PAGE LEVEL JS ================== -->
<asset:javascript src="volumeControls/editConfig.js"/>
<script>
    $(document).ready(function () {
    });
</script>
<!-- ================== END PAGE LEVEL JS ================== -->
</body>
</html>
