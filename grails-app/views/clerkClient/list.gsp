<!DOCTYPE html>
<!--[if IE 8]> <html class="ie8" lang="zh"> <![endif]-->
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
    <asset:stylesheet href="bootstrap-sweetalert/sweetalert.css"/>
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
        <g:render template="/layouts/base_sidebar" model="[active: 32100]"/>
    </g:if>
    <g:if test="${grailsApplication.config.pageVersion == 'v2'}">
        <g:render template="/layouts/base_sidebar" model="[active: 5700]"/>
    </g:if>
    <!-- end #sidebar -->

    <!-- begin #content -->
    <div id="content" class="content">
        <!-- begin page-header -->
        <h1 class="page-header">客户端软件版本</h1>

        <div class="alert alert-warning show">
            <dl>
                <dt class="f-s-16 m-t-20 row justify-content-between">
                    <div>
                        书记员软件版本号(最新版本号:${sjyLastVersion}),
                        法官控制系统版本号(最新版本号:${fgLastVersion}),
                        当事人软件版本号(最新版本号:${dsrLastVersion})
                    </div>
                    <a href="javascript:void(0)" class="btn btn-sm btn-inverse btn-add" id="uploadUpdatePackage">
                        上传软件更新包
                    </a>
                </dt>
                <dd class="p-l-10">
                    <table class="table table-hover">
                        <tr>
                            <th>所在法庭</th>
                            <th>书记员软件版本</th>
                            <th>法官控制系统版本</th>
                            <th>当事人软件版本</th>
                            <th>庭审主机软/硬版本</th>
                        </tr>
                        <g:each in="${secretaryVersion}" var="sv">
                            <tr>
                                <td>${sv.courtroom.name}</td>
                                <td>${sv.serviceVersion}</td>
                                <td>${sv.fgSoftVersion}</td>
                                <td>${sv.dsrSoftVersion}</td>
                                <td>${sv.deviceVersion}</td>
                            </tr>
                        </g:each>
                    </table>
                </dd>
            </dl>
        </div>

        <!-- end page-header -->
    </div>
    <!-- end #content -->

    <!-- begin scroll to top btn -->
    <g:render template="/layouts/base_topbtn"/>
    <!-- end scroll to top btn -->
</div>
<!-- end page container -->
<g:render template="/layouts/base_bottom"/>
<!-- ================== BEGIN PAGE LEVEL JS ================== -->
<asset:javascript src="bootstrap-sweetalert/sweetalert.js"/>
<asset:javascript src="clerkClient/list.js"/>
<script>
    $(document).ready(function () {
    });
</script>
<!-- ================== END PAGE LEVEL JS ================== -->
</body>
</html>
