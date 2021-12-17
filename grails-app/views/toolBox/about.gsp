<!DOCTYPE html>
<html>
<head>
    <meta charset="utf-8"/>
    <title>自动化运维平台</title>
    <meta content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=no" name="viewport"/>
    <meta content="系统关于页面" name="description"/>
    <meta content="daniel" name="author"/>
    <g:render template="/layouts/base_head"/>
    <asset:stylesheet href="bootstrap-sweetalert/sweetalert.css"/>
</head>

<body>
<g:render template="/layouts/base_loader"/>
<div id="page-container" class="page-container fade page-without-sidebar page-header-fixed page-with-top-menu">
    <g:render template="/layouts/base_navigation"
              model="[
                      picture: true,
                      arrow  : true
              ]"/>
    <g:render template="/layouts/base_sidebar" model="[active: 60001]"/>
    <div id="content" class="content">
        <h1 class="page-header">系统版本</h1>

        <div class="alert alert-warning show">
            <dl>
                <dt class="f-s-16">系统版本号</dt>
                <dd class="p-l-10">---- autodeploy version 1.0.0 build 20210730 ----</dd>
            </dl>
        </div>
    </div>
    <g:render template="/layouts/base_topbtn"/>
</div>
<g:render template="/layouts/base_bottom"/>
</body>
</html>
<script>
    $(document).ready(function () {
    });
</script>
