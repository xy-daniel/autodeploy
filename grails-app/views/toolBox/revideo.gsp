<%@ page import="com.hxht.autodeploy.court.manager.SystemController" %>
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
    <g:render template="/layouts/base_sidebar" model="[active: 1000400]"/>
    <!-- end #sidebar -->

    <!-- begin #content -->
    <div id="content" class="content">
        <!-- begin page-header -->
        <h1 class="page-header">视频数据恢复</h1>
        <!-- end page-header -->
        <!-- begin row -->
        <div class="row">
            <div class="col-lg-12">
                <div class="alert alert-warning fade show">
                    <span class="close" data-dismiss="alert">×</span>
                    <p><strong  class="h4">Warning!</strong>本功能可能导致数据不可挽回的损坏，使用前请先备份数据。</p>
                    <p class="h4">使用说明：</p>
                    <p>本功能只用于恢复因服务器存储已满导致的视频丢失问题。其他情况请勿使用！</p>
                    <p>运行过程中请勿中途关闭窗口，失败信息会打印到屏幕。</p>
                </div>
            </div>
            <div class="col-lg-12">
                <form class="form-inline" action="/" method="POST" id="import-form">
                    <div class="form-group m-r-10 m-b-15">
                        <input type="text" class="form-control" name="id" placeholder="排期主键"/>
                    </div>
                    <div class="form-group m-r-10 m-b-15">
                        <input type="text" class="form-control" name="uuid" placeholder="庭次UUID" style="width: 300px"/>
                    </div>
                    <div class="form-group m-r-10 m-b-15">
                        <input type="text" class="form-control" name="version" placeholder="主机版本（1/4）"/>
                    </div>
                    <div class="form-group m-r-10 m-b-15">
                        <input type="text" class="form-control" name="path" placeholder="默认为空，可以填写/mnt/HD0/" style="width: 300px"/>
                    </div>
                    <button type="submit" href="javascript:;" class="btn btn-sm btn-primary m-r-5 m-b-15">开始</button>
                </form>
                <g:if test="${SystemController.currentCourt.ext3?.matches("D97")}">
                    <form class="form-inline" action="/" method="POST" id="import-form2">
                        <button type="submit" href="javascript:;" class="btn btn-sm btn-primary m-r-5 m-b-15">开始拷贝txt中的视频文件</button>
                    </form>
                </g:if>
            </div>
            <div class="col-lg-12">
                <div class="court_content p-15" data-scrollbar="true">
                    <p class="text-muted">_></p>
                </div>
            </div>


        </div>
        <!-- end row -->
    </div>
    <!-- end #content -->

    <!-- begin scroll to top btn -->
    <g:render template="/layouts/base_topbtn"/>
    <!-- end scroll to top btn -->
</div>
<!-- end page container -->

<div class="modal fade" id="import-beforeSubmit">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <h4 class="modal-title">确认开始恢复视频数据</h4>
                <button type="button" class="close" data-dismiss="modal" aria-hidden="true">×</button>
            </div>
            <div class="modal-body">
                <p>
                    请仔细阅读说明，充分了解本功能后点击确定。
                </p>
            </div>
            <div class="modal-footer">
                <a href="javascript:;" class="btn btn-white" data-dismiss="modal">我还要修改</a>
                <a href="javascript:;" class="btn btn-success import-submit-btn">确认无误</a>
            </div>
        </div>
    </div>
</div>

<div class="modal fade" id="import-beforeSubmit2">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <h4 class="modal-title">确认开始恢复视频数据</h4>
                <button type="button" class="close" data-dismiss="modal" aria-hidden="true">×</button>
            </div>
            <div class="modal-body">
                <p>
                    请仔细阅读说明，充分了解本功能后点击确定。
                </p>
            </div>
            <div class="modal-footer">
                <a href="javascript:;" class="btn btn-white" data-dismiss="modal">我还要修改</a>
                <a href="javascript:;" class="btn btn-success import-submit-btn2">确认无误</a>
            </div>
        </div>
    </div>
</div>

<g:render template="/layouts/base_bottom"/>
<!-- ================== BEGIN PAGE LEVEL JS ================== -->
<asset:javascript src="sockjs/sockjs.min.js"/>
<asset:javascript src="stomp/stomp.min.js"/>
<asset:javascript src="toolBox/revideo.js"/>
<script>
    $(document).ready(function () {
    });
</script>
<!-- ================== END PAGE LEVEL JS ================== -->
</body>
</html>
