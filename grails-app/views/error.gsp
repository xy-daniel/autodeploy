<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="org.springframework.http.HttpStatus" %>
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

    <!-- ================== END PAGE LEVEL STYLE ================== -->
</head>
<body class="pace-top">
<!-- begin #page-loader -->
<div id="page-loader" class="fade show"><span class="spinner"></span></div>
<!-- end #page-loader -->

<!-- begin #page-container -->
<div id="page-container" class="fade">
    <!-- begin error -->
    <div class="error">
        <div class="error-code m-b-10">${(request.getAttribute('javax.servlet.error.status_code') as int)}</div>
        <div class="error-content jcm-error">
            <div class="error-message">${HttpStatus.valueOf(request.getAttribute('javax.servlet.error.status_code') as int).getReasonPhrase()}</div>
            <div>
                <g:link controller="index" class="btn btn-success p-l-20 p-r-20">Go Home</g:link>
            </div>
            <div class="error-desc m-b-30">
                <g:if test="${Throwable.isInstance(exception)}">
                    <tc:renderException exception="${exception}" />
                </g:if>
                <g:else test="${request.getAttribute('javax.servlet.error.exception')}">
                    <tc:renderException exception="${request.getAttribute('javax.servlet.error.exception')}" />
                </g:else>
            </div>
        </div>
    </div>
    <!-- end error -->


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
