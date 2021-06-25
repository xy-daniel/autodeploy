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
    <!-- ================== END PAGE LEVEL STYLE ================== -->
    <style type="text/css">
        .quarter-div{
            font-weight:bold;
            font-size: 50px;
            color: black;
            min-height: 200px;
            max-height: 300px;
        }
        .green{
            background-color: #5CB85C;
        }
        .red{
            background-color: red;
        }
    </style>
</head>
<body>
<!-- begin #page-loader -->
%{--<g:render template="/layouts/base_loader"/>--}%
<!-- end #page-loader -->

<!-- begin #page-container -->
<div id="page-container" class="page-container fade page-without-sidebar page-header-fixed page-with-top-menu">
    <div id="header" class="header navbar-default">
        <!-- begin header-nav -->
        <ul class="navbar-nav navbar-right">
            <li class="dropdown navbar-user">
                <a href="javascript:void(0);" id="editConfig" class="dropdown-toggle" data-toggle="dropdown">
                    设置
                </a>
            </li>
        </ul>
        <!-- end header navigation right -->
    </div>
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
                            <div class="row" >
                                <!-- begin col-8 -->
                                <div class="col-md-12">
                                    <legend class="no-border f-w-700 p-b-0 m-t-0 m-b-20 f-s-16 text-inverse">声音控制-静音开启和关闭</legend>
                                    <!-- begin  -->
                                    <div class="row m-b-10">
                                        <div class="col-md-6">
                                            <input type="button" class="col-md-12 quarter-div green"  value="公诉人" id="gsr" name="gsr"/>
                                        </div>
                                        <div class="col-md-6">
                                            <input type="button" class="col-md-12 quarter-div green" value="律师" id="ls" name="ls"/>
                                        </div>
                                    </div>
                                    <!-- end  -->
                                    <!-- begin  -->
                                    <div class=" row m-b-10">
                                        <div class="col-md-6">
                                            <input type="button" class="col-md-12 quarter-div green"  value="现场嫌疑人" id="xcxyr" name="xcxyr"/>
                                        </div>
                                        <div class="col-md-6">
                                            <input type="button" class="col-md-12 quarter-div green" value="远程嫌疑人" id="ycxyr" name="ycxyr"/>
                                        </div>
                                    </div>
                                    <!-- end  -->
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
</div>
<!-- end page container -->
<g:render template="/layouts/base_bottom"/>
<asset:javascript src="bootstrap-sweetalert/sweetalert.js"/>
<!-- ================== BEGIN PAGE LEVEL JS ================== -->
<asset:javascript src="volumeControls/edit.js"/>
<script>
    $(document).ready(function () {
    });
</script>
<!-- ================== END PAGE LEVEL JS ================== -->
</body>
</html>