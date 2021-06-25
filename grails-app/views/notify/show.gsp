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
    <g:render template="/layouts/base_sidebar"/>
    <!-- end #sidebar -->

    <!-- begin #content -->
    <div id="content" class="content">
        <!-- begin breadcrumb -->
        <ol class="breadcrumb pull-right">
            <li class="breadcrumb-item"><g:link controller="index">首页</g:link></li>
            <li class="breadcrumb-item">系统通知</li>
            <li class="breadcrumb-item active">系统通知</li>
        </ol>
        <!-- end breadcrumb -->
        <!-- begin page-header -->
        <h1 class="page-header">系统通知 <small>系统通知</small></h1>
        <!-- end page-header -->
        <div class="panel panel-inverse">
            <div class="panel-heading">
                <div class="panel-heading-btn">
                    <a href="javascript:;" class="btn btn-xs btn-icon btn-circle btn-default" data-click="panel-expand"><i class="fa fa-expand"></i></a>
                </div>
                <h4 class="panel-title">系统通知查看详情</h4>
            </div>
            <div class="panel-body">
                <div class="col-md-8 offset-md-2">
                    <!-- begin form-group row -->
                    <div class="form-group row m-b-10">
                        <label class="col-md-3 text-md-right col-form-label">操作人</label>
                        <div class="col-md-6">
                            <input type="text" class="form-control m-b-5" value="${notify.operator}" disabled/>
                        </div>
                    </div>
                    <!-- end form-group row -->
                    <!-- begin form-group row -->
                    <div class="form-group row m-b-10">
                        <label class="col-md-3 text-md-right col-form-label">是否已读</label>
                        <div class="col-md-6">
                            <input type="text" class="form-control m-b-5" value="${com.hxht.autodeploy.NotifyStatus.getString(notify.is_read)}" disabled/>
                        </div>
                    </div>
                    <!-- end form-group row -->
                    <!-- begin form-group row -->
                    <div class="form-group row m-b-10">
                        <label class="col-md-3 text-md-right col-form-label">创建时间</label>
                        <div class="col-md-6">
                            <input type="text" class="form-control m-b-5" value="${notify.dateCreated.format("yyyy/MM/dd HH:mm:ss")}" disabled/>
                        </div>
                    </div>
                    <!-- end form-group row -->
                    <!-- begin form-group row -->
                    <div class="form-group row m-b-10">
                        <label class="col-md-3 text-md-right col-form-label">详细信息</label>
                        <div class="col-md-6">
                            <textarea class="form-control m-b-5" rows="10" readonly>${notify.remark}</textarea>
                        </div>
                    </div>
                    <!-- end form-group row -->
                </div>
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
<script>
    $(document).ready(function () {
    });
</script>
<!-- ================== END PAGE LEVEL JS ================== -->
</body>
</html>
