<%@ page import="com.hxht.autodeploy.court.manager.SystemController" %>
<!DOCTYPE html>
<!--[if IE 8]> <html class="ie8"> <![endif]-->
<!--[if !IE]><!-->
<html>
<!--<![endif]-->
<head>
    <meta charset="utf-8"/>
    <title>科技法庭管理系统</title>
    <meta content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=no" name="viewport"/>
    <meta content="" name="description"/>
    <meta content="" name="author"/>

    <!-- ================== BEGIN BASE CSS STYLE ================== -->
    <g:render template="/layouts/base_head"/>
    <!-- ================== END BASE CSS STYLE ================== -->

    <!-- ================== BEGIN PAGE LEVEL STYLE ================== -->
    <!-- ================== END PAGE LEVEL STYLE ================== -->
</head>

<body class="pace-top">
<!-- begin #page-loader -->
<g:render template="/layouts/base_loader"/>
<!-- end #page-loader -->
<div class="login-cover">
    <div class="login-cover-image"
         style="background-image: url(<asset:assetPath src="login-bg/login-bg-3.jpg" alt=""/>)"
         data-id="login-cover-image"></div>

    <div class="login-cover-bg"></div>
</div>
<!-- begin #page-container -->
<div id="page-container" class="fade">
    <!-- begin login -->
    <div class="login login-v2" data-pageload-addclass="animated fadeIn">
        <!-- begin brand -->
        <div class="login-header">
            <div class="brand">
                <b>${SystemController.currentCourt.ext5}</b>
                <small>请登陆</small>
            </div>

            <div class="icon">
                <i class="fa fa-lock"></i>
            </div>
        </div>
        <!-- end brand -->
        <!-- begin login-content -->
        <div class="login-content">
            <g:form url="${postUrl}" name="loginForm" id="loginForm" method="POST" class="margin-bottom-0">
                <div class="form-group m-b-20">
                    <input type="text" class="form-control form-control-lg" name="username" id="username"
                           placeholder="用户名" required/>
                </div>

                <div class="form-group m-b-20">
                    <input type="password" class="form-control form-control-lg" name="password" id="password"
                           placeholder="密码" required/>
                </div>

                <div class="checkbox checkbox-css m-b-20">
                    <input type="checkbox" id="remember_checkbox"/>
                    <label for="remember_checkbox">
                        记住我
                    </label>
                </div>
                <g:if test="${flash.message}">
                    <p class="text-center text-red f-s-20">
                        ${flash.message}
                    </p>
                </g:if>
                <div class="login-buttons">
                    <button type="submit" class="btn btn-success btn-block btn-lg">提交</button>
                </div>
            <div class="m-t-20">
                <p class="text-red f-s-18"> <strong>为了您更好的体验，请使用Chrome浏览器。</strong></p>
                <p class="text-red f-s-18">请选择下载：<a href="${contentPath}/login/download?file=chrome_x86.exe" class="text-red f-s-18">32位版 </a> <a href="${contentPath}/login/download?file=chrome_x64.exe" class="text-red f-s-18">64位版 </a></p>
                <p class="text-white">下载庭审公告Apk安装包。</p>
                <p class="text-white">请选择下载：<a href="${contentPath}/login/download?file=tn-b1.0.apk">tn-b1.0.apk </a></p>
            </div>
            </g:form>
        </div>
        <!-- end login-content -->
    </div>
    <!-- end login -->
    <!-- begin login-bg -->
    <div class="login-prompt clearfix">
    </div>
    <!-- end login-bg -->
</div>
<!-- end page container -->

<g:render template="/layouts/base_bottom"/>
<!-- ================== BEGIN PAGE LEVEL JS ================== -->
<asset:javascript src="login/login.js"/>
<script>
    $(document).ready(function () {
        var username = Cookies.get('username')
        if(username!=null && username !=""){
            $("#username").val(atob(username));
            $("input[type='checkbox']").attr("checked", true);
        }
    });
</script>
<!-- ================== END PAGE LEVEL JS ================== -->
</body>
</html>
