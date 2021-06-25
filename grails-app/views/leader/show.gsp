<!DOCTYPE html>
<html lang="en">

<head>
    <meta charset="utf-8" />
    <title>领导观摩系统</title>
    <meta content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=no" name="viewport" />
    <meta content="" name="description" />
    <meta content="" name="author" />

    <!-- ================== BEGIN BASE CSS STYLE ================== -->
    <g:render template="/layouts/base_head"/>
    <!-- ================== END BASE CSS STYLE ================== -->

    <!-- ================== BEGIN PAGE LEVEL STYLE ================== -->
    <asset:stylesheet href="bootstrap-sweetalert/sweetalert.css"/>
    <asset:stylesheet href="fileinput/fileinput.min.css"/>
    <asset:stylesheet href="bootstrap-select/bootstrap-select.min.css"/>
    <!-- ================== END PAGE LEVEL STYLE ================== -->
</head>

<body>
<!-- begin #sidebar -->
<g:render template="/leader/leader_sidebar" model="[active: 3010]"/>
<!-- end #sidebar -->
<!-- begin #content -->
<div id="content" class="content" style="padding-top: 50px;margin-left: 0px;">
    <!-- begin breadcrumb -->
    <ol class="breadcrumb pull-right">
        <li class="breadcrumb-item" style="font-size: 14px">
            <g:link controller="message" action="index">首页</g:link>
        </li>
    </ol>
    <!-- end breadcrumb -->
    <!-- begin page-header -->
    <h1 class="page-header">庭审信息 <small>案件详情</small></h1>
    <!-- end page-header -->

    <!-- begin row -->
    <div class="row plan-info" trialId="${data.trialId}" planId="${data.planId}" allowPlay="${data.allowPlay}">
        <div class="col-lg-8">
            <div class="panel panel-inverse panel-with-tabs">
                <div class="panel-heading p-0">
                    <div class="tab-overflow">
                        <ul class="nav nav-tabs nav-tabs-inverse plan-show-chn">
                            <g:each in="${chnList}" var="chn" status="i">
                                <li class="nav-item"><a href="#" data-toggle="tab" data="${chn.number}" class="nav-link plan-tab-chn <g:if test="${i == 0}">active</g:if>">${chn.name}</a></li>
                            </g:each>
                        <!-- 实际页面要从后台获取有几路图像 -->
                        </ul>
                    </div>
                </div>
                <div class="plan-show-info" style="height: 700px;">
                    <div class="plan-show-video" style="height: 100%;">
                        <div id="video" style="height: 100%;">
                            <div class="plan-show-video-notfound" >
                                <span style="font-size:30px;">未开庭暂无视频</span>
                            </div>
                        </div>
                        <div class="tab-content plan-show-video-tab-content">
                        </div>
                    </div>
                </div>
            </div>
        </div>
        <div class="col-lg-4">
            <div class="panel panel-inverse panel-with-tabs">
                <div class="panel-heading p-0">
                    <div class="tab-overflow">
                        <ul class="nav nav-tabs nav-tabs-inverse ">
                            <li class="nav-item"><a href="#nav-tab-7" data-toggle="tab" class="nav-link active">案件信息</a></li>
                            <li id="note" class="nav-item"><a href="#nav-tab-8" data-toggle="tab" class="nav-link">笔录信息</a></li>
                        </ul>
                    </div>
                </div>

                <div class="plan-show-info" style="height: 700px;">
                    <div class="tab-content plan-show-info-tab-content" style="margin: 0px">
                        <!-- begin tab-pane -->
                        <div class="tab-pane fade active show" id="nav-tab-7" style="height: 670px">
                            <!-- 携带初始化数据 -->
                            <div id="mainCaseDiv" style="margin-left: 5px;">
                                %{--                                <h3>${data.caseName}</h3>--}%
                                <dl class="dl-horizontal">
                                    <dt class="text-inverse">案号</dt>
                                    <dd>${data.caseArchives}</dd>
                                    <dt class="text-inverse">案件名称</dt>
                                    <dd>${data.caseName?data.caseName:"null"}</dd>
                                    <dt class="text-inverse">案件类型</dt>
                                    <dd>${data.caseType}</dd>
                                    <dt class="text-inverse">开庭地点</dt>
                                    <dd>${data.courtroom}</dd>
                                    <dt class="text-inverse">开庭时间</dt>
                                    <dd>${data.startDate}</dd>
                                    <dt class="text-inverse">当事人</dt>
                                    <dd>原告:${data.accuser}-被告:${data.accused}</dd>
                                    <dt class="text-inverse">主审法官</dt>
                                    <dd>${data.judge}</dd>
                                    <dt class="text-inverse">书记员</dt>
                                    <dd>${data.secretary}</dd>
                                </dl>
                            </div>
                        </div>
                        <!-- end tab-pane -->

                        <!-- begin tab-pane -->
                        <div class="tab-pane fade" style="min-height: 600px" id="nav-tab-8">
                            <div class="penhoder" id="penhoder" style="height: 580px" data-scrollbar="true">
                                <pre>

                                </pre>
                            </div>
                        </div>
                        <!-- end tab-pane -->
                        <!-- begin tab-pane -->
                        %{--                        <div class="tab-pane fade" id="nav-tab-10" style="height: 670px">--}%
                        %{--                            <g:each in="${}" var="chn" status="i">--}%
                        %{--                                <li class="nav-item"><a href="#" data-toggle="tab" data="${chn.number}" class="nav-link plan-tab-chn <g:if test="${i == 0}">active</g:if>">${chn.name}</a></li>--}%
                        %{--                            </g:each>--}%
                        %{--                        </div>--}%
                        <!-- end tab-pane -->
                    </div>
                </div>
            </div>
        </div>
    </div>
    <!-- end row -->
</div>
<!-- end #content -->
<g:render template="/layouts/base_bottom"/>

<!-- ================== BEGIN PAGE LEVEL JS ================== -->
<asset:javascript src="flvjs/flv.min.js"/>
<asset:javascript src="hls/hls.min.js"/>
<asset:javascript src="DPlayer/DPlayer.min.js"/>
<asset:javascript src="bootstrap-sweetalert/sweetalert.js"/>
<asset:javascript src="fileinput/fileinput.min.js"/>
<asset:javascript src="fileinput/zh.js"/>
<asset:javascript src="sockjs/sockjs.min.js"/>
<asset:javascript src="stomp/stomp.min.js"/>
<asset:javascript src="slimscroll/jquery.slimscroll.min.js"/>
<asset:javascript src="bootstrap-select/bootstrap-select.min.js"/>
<asset:javascript src="select2/dist/js/select2.min.js"/>
<asset:javascript src="leader/show.js"/>
<script>
    $(document).ready(function () {
    });
</script>
<!-- ================== END PAGE LEVEL JS ================== -->
</body>

</html>