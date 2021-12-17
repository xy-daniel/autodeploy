<!DOCTYPE html>
<!--[if IE 8]> <html class="ie8" lang="zh"> <![endif]-->
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
    <!-- ================== END PAGE LEVEL STYLE ================== -->
</head>

<body>

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
    <g:render template="/layouts/base_sidebar" model="[active: 0]"/>
    <!-- end #sidebar -->
    <div id="content" class="content">
        <div>
            操作系统型号：${os}
        </div>

        <div class="row">
            <div class="col-lg-3 col-md-6">
                <div class="widget widget-stats bg-black-lighter">
                    <div class="stats-icon"><i class="fa fa-desktop"></i></div>

                    <div class="stats-info">
                        <h4>主机数量</h4>

                        <p>${device}</p>
                    </div>

                    <div class="stats-link">
                        <a href="device/list">详情 <i class="fa fa-arrow-alt-circle-right"></i></a>
                    </div>
                </div>
            </div>

            <div class="col-lg-3 col-md-6">
                <div class="widget widget-stats bg-black-lighter">
                    <div class="stats-icon"><i class="fa fa-desktop"></i></div>

                    <div class="stats-info">
                        <h4>数据表数量</h4>

                        <p>${dataTable}</p>
                    </div>

                    <div class="stats-link">
                        <a href="dataTable/list">详情 <i class="fa fa-arrow-alt-circle-right"></i></a>
                    </div>
                </div>
            </div>

            <div class="col-lg-3 col-md-6">
                <div class="widget widget-stats bg-black-lighter">
                    <div class="stats-icon"><i class="fa fa-desktop"></i></div>

                    <div class="stats-info">
                        <h4>应用数量</h4>

                        <p>${app}</p>
                    </div>

                    <div class="stats-link">
                        <a href="app/list">详情 <i class="fa fa-arrow-alt-circle-right"></i></a>
                    </div>
                </div>
            </div>

            <div class="col-lg-3 col-md-6">
                <div class="widget widget-stats bg-black-lighter">
                    <div class="stats-icon"><i class="fa fa-desktop"></i></div>

                    <div class="stats-info">
                        <h4>任务数量</h4>

                        <p>${task}</p>
                    </div>

                    <div class="stats-link">
                        <a href="task/list">详情 <i class="fa fa-arrow-alt-circle-right"></i></a>
                    </div>
                </div>
            </div>
        </div>

        <div class="row">
            <div class="panel panel-inverse width-full m-l-10 m-r-10" data-sortable-id="index-1">
                <div class="panel-heading">
                    <div class="panel-heading-btn">
                        <a href="javascript:void(0);" class="btn btn-xs btn-icon btn-circle btn-default"
                           data-click="panel-expand">
                            <i class="fa fa-expand"></i>
                        </a>
                        <a href="javascript:void(0);" class="btn btn-xs btn-icon btn-circle btn-success"
                           data-click="panel-reload">
                            <i class="fa fa-redo"></i>
                        </a>
                        <a href="javascript:void(0);" class="btn btn-xs btn-icon btn-circle btn-warning"
                           data-click="panel-collapse">
                            <i class="fa fa-minus"></i>
                        </a>
                        <a href="javascript:void(0);" class="btn btn-xs btn-icon btn-circle btn-danger"
                           data-click="panel-remove">
                            <i class="fa fa-times"></i>
                        </a>
                    </div>
                    <h4 class="panel-title">监测分析(使用率/分钟)</h4>
                </div>

                <div class="panel-body">
                    <div id="infoChart" class="height-sm"></div>
                </div>
            </div>
        </div>
    </div>
</div>
<!-- end page container -->

<g:render template="/layouts/base_bottom"/>
<!-- ================== BEGIN PAGE LEVEL JS ================== -->
<asset:javascript src="flot/jquery.flot.min.js"/>
<asset:javascript src="flot/jquery.flot.time.min.js"/>
<asset:javascript src="flot/jquery.flot.resize.min.js"/>
<asset:javascript src="flot/jquery.flot.pie.min.js"/>
<asset:javascript src="index/index.js"/>

<script>
</script>
</body>
</html>
