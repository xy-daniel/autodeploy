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
    <g:render template="/layouts/base_sidebar" model="[active: 1000300]"/>
    <!-- end #sidebar -->

    <!-- begin #content -->
    <div id="content" class="content">
        <!-- begin page-header -->
        <h1 class="page-header">统计视频时长</h1>
        <!-- end page-header -->
        <!-- begin row -->
        <div class="row">
            <div class="col-lg-12">
                <div class="alert alert-warning fade show">
                    <span class="close" data-dismiss="alert">×</span>
                    <p><strong  class="h4">Warning!</strong>本功能可能导致数据不可挽回的损坏，使用前请先备份数据。</p>
                    <p class="h4">使用说明：</p>
                    <p>本功能只用于数据初始化时，或者视频时长丢失时补填视频时长。</p>
                    <p>脚本通过ffmpeg获取视频时长写入video_info 表中。脚本只会统计表中length字段为空的数据。</p>
                </div>
            </div>
            <div class="col-lg-12">
                <form class="form-inline" action="/" method="POST" id="import-form">
                    <div class="form-group m-r-10 m-b-15">
                        <input type="text" style="width: 500px" class="form-control" name="videoPath" placeholder="videoPath" value="/usr/local/movies/"/>
                    </div>
                    <button type="submit" href="javascript:;" class="btn btn-sm btn-primary m-r-5 m-b-15">开始</button>
                </form>
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
                <h4 class="modal-title">确认开始进行时长统计</h4>
                <button type="button" class="close" data-dismiss="modal" aria-hidden="true">×</button>
            </div>
            <div class="modal-body">
                <p>
                    请仔细阅读说明，充分了解本功能后点击确定。
                </p>
            </div>
            <div class="modal-footer">
                <a href="javascript:;" class="btn btn-white" data-dismiss="modal">我再想想</a>
                <a href="javascript:;" class="btn btn-success import-submit-btn">确认无误</a>
            </div>
        </div>
    </div>
</div>

<g:render template="/layouts/base_bottom"/>
<!-- ================== BEGIN PAGE LEVEL JS ================== -->
<asset:javascript src="sockjs/sockjs.min.js"/>
<asset:javascript src="stomp/stomp.min.js"/>
<asset:javascript src="toolBox/videoDuration.js"/>
<script>
    $(document).ready(function () {
    });
</script>
<!-- ================== END PAGE LEVEL JS ================== -->
</body>
</html>
