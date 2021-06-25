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

    <!-- ================== BEGIN PAGE LEVEL STYLE ================== -->
    <asset:stylesheet href="parsley/src/parsley.css"/>
    <asset:stylesheet href="bootstrap-sweetalert/sweetalert.css"/>
    <asset:stylesheet href="bootstrap-eonasdan-datetimepicker/build/css/bootstrap-datetimepicker.min.css"/>
    <asset:stylesheet href="bootstrap-select/bootstrap-select.min.css"/>
    <asset:stylesheet href="jquery-tag-it/css/jquery.tagit.css"/>
    <asset:stylesheet href="select2/dist/css/select2.min.css"/>
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
        <g:render template="/layouts/base_sidebar" model="[active: 30102]"/>
    </g:if>
    <g:if test="${grailsApplication.config.pageVersion == 'v2'}">
        <g:render template="/layouts/base_sidebar" model="[active: 7100]"/>
    </g:if>
    <!-- end #sidebar -->

    <!-- begin #content -->
    <div id="content" class="content">
        <!-- begin breadcrumb -->
        <ol class="breadcrumb pull-right">
            <li class="breadcrumb-item"><g:link controller="index">首页</g:link></li>
            <li class="breadcrumb-item">配置与管理</li>
            <li class="breadcrumb-item active">基础信息管理-法庭控制</li>
        </ol>
        <!-- end breadcrumb -->
        <!-- begin page-header -->
        <h1 class="page-header">配置与管理 <small>法庭控制</small></h1>
        <!-- begin courtroom_id -->
        <input id="courtroom" type="hidden" value="${courtroom.id}"/>
        <input id="flag" type="hidden" value="${flag}"/>
        <!-- end courtroom_id -->
        <!-- end page-header -->
        <div class="panel panel-inverse panel-with-tabs">
            <div class="panel-heading p-0">
                <div class="tab-overflow">
                    <ul class="nav nav-tabs nav-tabs-inverse ">
                        <li class="nav-item"><a href="#nav-tab-2" data-toggle="tab" id="encode" class="nav-link active">编码器</a></li>
%{--                        <li class="nav-item"><a href="#nav-tab-1" data-toggle="tab" id="ycEncode" class="nav-link">远程编码器</a></li>--}%
                        <li class="nav-item"><a href="#nav-tab-3" data-toggle="tab" id="decode" class="nav-link">解码器</a></li>
                        <li class="nav-item"><a href="#nav-tab-4" data-toggle="tab" id="video" class="nav-link">VIDEO矩阵</a></li>
                        <li class="nav-item"><a href="#nav-tab-5" data-toggle="tab" id="vga" class="nav-link">VGA矩阵</a></li>
                        <li class="nav-item"><a href="#nav-tab-6" data-toggle="tab" id="out" class="nav-link">输出控制</a></li>
                        <li class="nav-item"><a href="#nav-tab-7" data-toggle="tab" id="sound" class="nav-link">音量控制</a></li>
                        <li class="nav-item"><a href="#nav-tab-8" data-toggle="tab" id="total" class="nav-link">综合控制</a></li>
                        <li class="nav-item"><a href="#nav-tab-9" data-toggle="tab" id="power" class="nav-link">强电控制</a></li>
                        <li class="nav-item"><a href="#nav-tab-10" data-toggle="tab" id="ir" class="nav-link">红外控制</a></li>
                        <li class="nav-item"><a href="#nav-tab-11" data-toggle="tab" id="camera" class="nav-link">摄像头控制</a></li>
                        <li class="nav-item"><a href="#nav-tab-12" data-toggle="tab" id="powerNew" class="nav-link">电源控制</a></li>
                    </ul>
                </div>
            </div>

            <div class="tab-content plan-show-info-tab-content">
                <!-- begin tab-pane 编码器-->
                <div class="tab-pane fade active show" id="nav-tab-2">
                    <div class="table-btn-row m-b-15">
                        <sec:ifAnyGranted roles='ROLE_SUPER,ROLE_ADMIN,ROLE_MONITOR'>
                            <g:link url="${createLink(uri: '/')}ctrl/encode/add/${courtroom.id}" class="btn btn-sm btn-inverse btn-add"><i class="fa fa-plus m-r-5"></i> 添加编码器</g:link>
                            <a href="javascript:void(0);" id="delEncode" class="btn btn-sm btn-inverse btn-del"><i class="fa fa-times m-r-5"></i> 删除所选编码器</a>
                        </sec:ifAnyGranted>
                    </div>
                    <table class="table table-striped table-bordered display">
                        <thead>
                        <tr>
                            <th class="with-checkbox" width="15px">
                                <div class="checkbox checkbox-css">
                                    <input type="checkbox" class="encode_all" id="table_checkbox_all_2" />
                                    <label for="table_checkbox_all_2">&nbsp;</label>
                                </div>
                            </th>
                            <th class="text-nowrap">本地名称</th>
                            <th class="text-nowrap">编码器IP</th>
                            <th class="text-nowrap">通道名称</th>
                            <th class="text-nowrap">是否可以录制</th>
                            <th class="text-nowrap">排序</th>
                            <th class="text-nowrap">操作</th>
                        </tr>
                        </thead>
                        <tbody>
                        <g:each in="${encode}" var="e" status="i">
                            <tr>
                                <td class="with-checkbox">
                                    <div class="checkbox checkbox-css">
                                    <input type="checkbox" class="encode_single" name="checkbox-select" value="${e.uuid}" id="table_checkbox_${e.uuid}" data-user="${e.uuid}" />
                                    <label for="table_checkbox_${e.uuid}">&nbsp;</label>
                                    </div>
                                </td>
                                <td>${e.name}</td>
                                <td>${e.encodeip}</td>
                                <td>${e.number}</td>
                                <td>${e.record == "1"?"是":"否"}</td>
                                <td>${e.order}</td>
                                <td>
                                    <input type="hidden" value="${e.uuid}" />
                                    <a href="javascript:void(0)" class="editEncode btn btn-inverse btn-xs m-r-5 btn-enabled">编辑</a>
                                </td>
                            </tr>
                        </g:each>
                        </tbody>
                    </table>
                </div>
                <!-- end tab-pane -->
                <!-- begin tab-pane 远程编码器-->
                <div class="tab-pane fade" id="nav-tab-1">
                    <div class="table-btn-row m-b-15">
                        <sec:ifAnyGranted roles='ROLE_SUPER,ROLE_ADMIN,ROLE_MONITOR'>
                            <g:link url="${createLink(uri: '/')}ctrl/ycEncode/add/${courtroom.id}" class="btn btn-sm btn-inverse btn-add"><i class="fa fa-plus m-r-5"></i> 添加远程编码器</g:link>
                            <a href="javascript:void(0);" id="delYcEncode" class="btn btn-sm btn-inverse btn-del"><i class="fa fa-times m-r-5"></i> 删除所选编码器</a>
                        </sec:ifAnyGranted>
                    </div>
                    <table class="table table-striped table-bordered display">
                        <thead>
                        <tr>
                            <th class="with-checkbox" width="15px">
                                <div class="checkbox checkbox-css">
                                    <input type="checkbox" class="ycEncode_all" id="table_checkbox_all_1" />
                                    <label for="table_checkbox_all_1">&nbsp;</label>
                                </div>
                            </th>
                            <th class="text-nowrap">本地名称</th>
                            <th class="text-nowrap">编码器IP</th>
                            <th class="text-nowrap">通道名称</th>
                            <th class="text-nowrap">操作</th>
                        </tr>
                        </thead>
                        <tbody>
                        <g:each in="${ycEncode}" var="yc" status="i">
                            <tr>
                                <td class="with-checkbox">
                                    <div class="checkbox checkbox-css">
                                        <input type="checkbox" class="ycEncode_single" name="checkbox-select" value="${yc.uuid}" id="table_checkbox_${yc.uuid}" data-user="${yc.uuid}" />
                                        <label for="table_checkbox_${yc.uuid}">&nbsp;</label>
                                    </div>
                                </td>
                                <td>${yc.name}</td>
                                <td>${yc.encodeip}</td>
                                <td>${yc.number}</td>
                                <td>
                                    <input type="hidden" value="${yc.uuid}" />
                                    <a href="javascript:void(0)" class="editYcEncode btn btn-inverse btn-xs m-r-5 btn-enabled">编辑</a>
                                </td>
                            </tr>
                        </g:each>
                        </tbody>
                    </table>
                </div>
                <!-- end tab-pane -->
                <!-- begin tab-pane 解码器-->
                <div class="tab-pane fade" id="nav-tab-3">
                    <div class="table-btn-row m-b-15">
                        <sec:ifAnyGranted roles='ROLE_SUPER,ROLE_ADMIN,ROLE_MONITOR'>
                            <g:link url="${createLink(uri: '/')}ctrl/decode/add/${courtroom.id}" class="btn btn-sm btn-inverse btn-add"><i class="fa fa-plus m-r-5"></i> 添加解码器</g:link>
                            <a href="javascript:void(0);" id="delDecode" class="btn btn-sm btn-inverse btn-del"><i class="fa fa-times m-r-5"></i> 删除所选解码器</a>
                        </sec:ifAnyGranted>
                    </div>
                    <table class="table table-striped table-bordered display">
                        <thead>
                        <tr>
                            <th class="with-checkbox" width="15px">
                                <div class="checkbox checkbox-css">
                                    <input class="decode_all" type="checkbox" id="table_checkbox_all_3" />
                                    <label for="table_checkbox_all_3">&nbsp;</label>
                                </div>
                            </th>
                            <th class="text-nowrap">本地名称</th>
                            <th class="text-nowrap">解码器IP</th>
                            <th class="text-nowrap">通道名称</th>
                            <th class="text-nowrap">操作</th>
                        </tr>
                        </thead>
                        <tbody>
                        <g:each in="${decode}" var="d" status="i">
                            <tr>
                                <td class="with-checkbox">
                                    <div class="checkbox checkbox-css">
                                        <input type="checkbox" class="decode_single" name="checkbox-select" value="${d.uuid}" id="table_checkbox_${d.uuid}" data-user="${d.uuid}" />
                                        <label for="table_checkbox_${d.uuid}">&nbsp;</label>
                                    </div>
                                </td>
                                <td>${d.name}</td>
                                <td>${d.decodeip}</td>
                                <td>${d.number}</td>
                                <td>
                                    <input type="hidden" value="${d.uuid}" />
                                    <a href="javascript:void(0)" class="editDecode btn btn-inverse btn-xs m-r-5 btn-enabled">编辑</a>
                                </td>
                            </tr>
                        </g:each>
                        </tbody>
                    </table>
                </div>
                <!-- end tab-pane -->
                <!-- begin tab-pane VIDEO矩阵-->
                <div class="tab-pane fade" id="nav-tab-4">
                    <div class="table-btn-row m-b-15">
                        <sec:ifAnyGranted roles='ROLE_SUPER,ROLE_ADMIN,ROLE_MONITOR'>
                            <g:link url="${createLink(uri: '/')}ctrl/video/add/${courtroom.id}" class="btn btn-sm btn-inverse btn-add"><i class="fa fa-plus m-r-5"></i> 添加VIDEO矩阵</g:link>
                            <a href="javascript:void(0);" id="delVideo" class="btn btn-sm btn-inverse btn-del"><i class="fa fa-times m-r-5"></i> 删除所选VIDEO矩阵</a>
                        </sec:ifAnyGranted>
                    </div>
                    <table class="table table-striped table-bordered display">
                        <thead>
                        <tr>
                            <th class="with-checkbox" width="15px">
                                <div class="checkbox checkbox-css">
                                    <input class="video_all" type="checkbox" id="table_checkbox_all_4" />
                                    <label for="table_checkbox_all_4">&nbsp;</label>
                                </div>
                            </th>
                            <th class="text-nowrap">名称</th>
                            <th class="text-nowrap">控制指令</th>
                            <th class="text-nowrap">可见状态</th>
                            <th class="text-nowrap">操作</th>
                        </tr>
                        </thead>
                        <tbody>
                        <g:each in="${videoMatrix}" var="v" status="i">
                            <tr>
                                <td class="with-checkbox">
                                    <div class="checkbox checkbox-css">
                                        <input type="checkbox" class="video_single" name="checkbox-select" value="${v.uuid}" id="table_checkbox_${v.uuid}" data-user="${v.uuid}" />
                                        <label for="table_checkbox_${v.uuid}">&nbsp;</label>
                                    </div>
                                </td>
                                <td>${v.name}</td>
                                <td>${v.codeDown}</td>
                                <td>${v.visible == "1"?"可见":"隐藏"}</td>
                                <td>
                                    <input type="hidden" value="${v.uuid}" />
                                    <a href="javascript:void(0)" class="editVideo btn btn-inverse btn-xs m-r-5 btn-enabled">编辑</a>
                                </td>
                            </tr>
                        </g:each>
                        </tbody>
                    </table>
                </div>
                <!-- end tab-pane -->
                <!-- begin tab-pane VGA矩阵-->
                <div class="tab-pane fade" id="nav-tab-5">
                    <div class="table-btn-row m-b-15">
                        <sec:ifAnyGranted roles='ROLE_SUPER,ROLE_ADMIN,ROLE_MONITOR'>
                            <g:link url="${createLink(uri: '/')}ctrl/vga/add/${courtroom.id}" class="btn btn-sm btn-inverse btn-add"><i class="fa fa-plus m-r-5"></i> 添加VGA矩阵</g:link>
                            <a href="javascript:void(0);" id="delVag" class="btn btn-sm btn-inverse btn-del"><i class="fa fa-times m-r-5"></i> 删除所选VGA矩阵</a>
                        </sec:ifAnyGranted>
                    </div>
                    <table class="table table-striped table-bordered display">
                        <thead>
                        <tr>
                            <th class="with-checkbox" width="15px">
                                <div class="checkbox checkbox-css">
                                    <input class="vag_all" type="checkbox" id="table_checkbox_all_5" />
                                    <label for="table_checkbox_all_5">&nbsp;</label>
                                </div>
                            </th>
                            <th class="text-nowrap">名称</th>
                            <th class="text-nowrap">控制指令</th>
                            <th class="text-nowrap">可见状态</th>
                            <th class="text-nowrap">操作</th>
                        </tr>
                        </thead>
                        <tbody>
                        <g:each in="${vgaMatrix}" var="v" status="i">
                            <tr>
                                <td class="with-checkbox">
                                    <div class="checkbox checkbox-css">
                                        <input type="checkbox" class="vag_single" name="checkbox-select" value="${v.uuid}" id="table_checkbox_${v.uuid}" data-user="${v.uuid}" />
                                        <label for="table_checkbox_${v.uuid}">&nbsp;</label>
                                    </div>
                                </td>
                                <td>${v.name}</td>
                                <td>${v.codeDown}</td>
                                <td>${v.visible == "1"?"可见":"隐藏"}</td>
                                <td>
                                    <input type="hidden" value="${v.uuid}" />
                                    <a href="javascript:void(0)" class="editVga btn btn-inverse btn-xs m-r-5 btn-enabled">编辑</a>
                                </td>
                            </tr>
                        </g:each>
                        </tbody>
                    </table>
                </div>
                <!-- end tab-pane -->
                <!-- begin tab-pane 输出控制-->
                <div class="tab-pane fade" id="nav-tab-6">
                    <div class="table-btn-row m-b-15">
                        <sec:ifAnyGranted roles='ROLE_SUPER,ROLE_ADMIN,ROLE_MONITOR'>
                            <g:link url="${createLink(uri: '/')}ctrl/out/add/${courtroom.id}" class="btn btn-sm btn-inverse btn-add"><i class="fa fa-plus m-r-5"></i> 添加输出控制</g:link>
                            <a href="javascript:void(0);" id="delOut" class="btn btn-sm btn-inverse btn-del"><i class="fa fa-times m-r-5"></i> 删除所选输出控制</a>
                        </sec:ifAnyGranted>
                    </div>
                    <table class="table table-striped table-bordered display">
                        <thead>
                        <tr>
                            <th class="with-checkbox" width="15px">
                                <div class="checkbox checkbox-css">
                                    <input class="out_all" type="checkbox" id="table_checkbox_all_6" />
                                    <label for="table_checkbox_all_6">&nbsp;</label>
                                </div>
                            </th>
                            <th class="text-nowrap">名称</th>
                            <th class="text-nowrap">控制指令</th>
                            <th class="text-nowrap">可见状态</th>
                            <th class="text-nowrap">操作</th>
                        </tr>
                        </thead>
                        <tbody>
                        <g:each in="${outMatrix}" var="o" status="i">
                            <tr>
                                <td class="with-checkbox">
                                    <div class="checkbox checkbox-css">
                                        <input type="checkbox" class="out_single" name="checkbox-select" value="${o.uuid}" id="table_checkbox_${o.uuid}" data-user="${o.uuid}" />
                                        <label for="table_checkbox_${o.uuid}">&nbsp;</label>
                                    </div>
                                </td>
                                <td>${o.name}</td>
                                <td>${o.codeDown}</td>
                                <td>${o.visible == "1"?"可见":"隐藏"}</td>
                                <td>
                                    <input type="hidden" value="${o.uuid}" />
                                    <a href="javascript:void(0)" class="editOut btn btn-inverse btn-xs m-r-5 btn-enabled">编辑</a>
                                </td>
                            </tr>
                        </g:each>
                        </tbody>
                    </table>
                </div>
                <!-- end tab-pane -->
                <!-- begin tab-pane 音量控制  soundMatrix-->
                <div class="tab-pane fade" id="nav-tab-7">
                    <div class="table-btn-row m-b-15">
                        <sec:ifAnyGranted roles='ROLE_SUPER,ROLE_ADMIN,ROLE_MONITOR'>
                            <g:link url="${createLink(uri: '/')}ctrl/sound/add/${courtroom.id}" class="btn btn-sm btn-inverse btn-add"><i class="fa fa-plus m-r-5"></i> 添加音量控制</g:link>
                            <a href="javascript:void(0);" id="delSound"  class="btn btn-sm btn-inverse btn-del"><i class="fa fa-times m-r-5"></i> 删除所选音量控制</a>
                        </sec:ifAnyGranted>
                    </div>
                    <table class="table table-striped table-bordered display">
                        <thead>
                        <tr>
                            <th class="with-checkbox" width="15px">
                                <div class="checkbox checkbox-css">
                                    <input class="sound_all" type="checkbox" id="table_checkbox_all_7" />
                                    <label for="table_checkbox_all_7">&nbsp;</label>
                                </div>
                            </th>
                            <th class="text-nowrap">名称</th>
                            <th class="text-nowrap">控制指令</th>
                            <th class="text-nowrap">分组</th>
                            <th class="text-nowrap">可见状态</th>
                            <th class="text-nowrap">操作</th>
                        </tr>
                        </thead>
                        <tbody>
                        <g:each in="${soundMatrix}" var="s" status="i">
                            <tr>
                                <td class="with-checkbox">
                                    <div class="checkbox checkbox-css">
                                        <input type="checkbox" class="sound_single" name="checkbox-select" value="${s.uuid}" id="table_checkbox_${s.uuid}" data-user="${s.uuid}" />
                                        <label for="table_checkbox_${s.uuid}">&nbsp;</label>
                                    </div>
                                </td>
                                <td>${s.name}</td>
                                <td>${s.codeDown}</td>
                                <td>${s.group}</td>
                                <td>${s.visible == "1"?"可见":"隐藏"}</td>
                                <td>
                                    <input type="hidden" value="${s.uuid}" />
                                    <a href="javascript:void(0)" class="editSound btn btn-inverse btn-xs m-r-5 btn-enabled">编辑</a>
                                </td>
                            </tr>
                        </g:each>
                        </tbody>
                    </table>
                </div>
                <!-- end tab-pane -->
                <!-- begin tab-pane 综合控制-->
                <div class="tab-pane fade" id="nav-tab-8">
                    <div class="table-btn-row m-b-15">
                        <sec:ifAnyGranted roles='ROLE_SUPER,ROLE_ADMIN,ROLE_MONITOR'>
                            <g:link url="${createLink(uri: '/')}ctrl/total/add/${courtroom.id}" class="btn btn-sm btn-inverse btn-add"><i class="fa fa-plus m-r-5"></i> 添加综合控制</g:link>
                            <a href="javascript:void(0);" id="delTotal" class="btn btn-sm btn-inverse btn-del"><i class="fa fa-times m-r-5"></i> 删除所选综合控制</a>
                        </sec:ifAnyGranted>
                    </div>
                    <table class="table table-striped table-bordered display">
                        <thead>
                        <tr>
                            <th class="with-checkbox" width="15px">
                                <div class="checkbox checkbox-css">
                                    <input class="total_all" type="checkbox" id="table_checkbox_all_8" />
                                    <label for="table_checkbox_all_8">&nbsp;</label>
                                </div>
                            </th>
                            <th class="text-nowrap">名称</th>
                            <th class="text-nowrap">控制指令</th>
                            <th class="text-nowrap">发送时间</th>
                            <th class="text-nowrap">发送优先级</th>
                            <th class="text-nowrap">可见状态</th>
                            <th class="text-nowrap">操作</th>
                        </tr>
                        </thead>
                        <tbody>
                            <g:each in="${total}" var="t" status="i">
                                <tr>
                                    <td class="with-checkbox">
                                        <div class="checkbox checkbox-css">
                                            <input type="checkbox" class="total_single" name="checkbox-select" value="${t.uuid}" id="table_checkbox_${t.uuid}" data-user="${t.uuid}" />
                                            <label for="table_checkbox_${t.uuid}">&nbsp;</label>
                                        </div>
                                    </td>
                                    <td>${t.name}</td>
                                    <td>${t.codeDown}</td>
                                    <td>${t.sendTime}</td>
                                    <td>
                                        <g:if test="${t.sendPriority == '1'}">优 </g:if>
                                        <g:if test="${t.sendPriority == '2'}">良 </g:if>
                                        <g:if test="${t.sendPriority == '3'}">中 </g:if>
                                        <g:if test="${t.sendPriority == '4'}">差 </g:if>
                                    </td>
                                    <td>${t.visible == "1"?"可见":"隐藏"}</td>
                                    <td>
                                        <input type="hidden" value="${t.uuid}" />
                                        <a href="javascript:void(0)" class="editTotal btn btn-inverse btn-xs m-r-5 btn-enabled">编辑</a>
                                    </td>
                                </tr>
                            </g:each>
                        </tbody>
                    </table>
                </div>
                <!-- end tab-pane -->
                <!-- begin tab-pane 强电控制-->
                <div class="tab-pane fade" id="nav-tab-9">
                    <div class="table-btn-row m-b-15">
                        <sec:ifAnyGranted roles='ROLE_SUPER,ROLE_ADMIN,ROLE_MONITOR'>
                            <g:link url="${createLink(uri: '/')}ctrl/power/add/${courtroom.id}" class="btn btn-sm btn-inverse btn-add"><i class="fa fa-plus m-r-5"></i> 添加强电控制</g:link>
                            <a href="javascript:void(0);" id="delPower" class="btn btn-sm btn-inverse btn-del"><i class="fa fa-times m-r-5"></i> 删除所选强电控制</a>
                        </sec:ifAnyGranted>
                    </div>
                    <table class="table table-striped table-bordered display">
                        <thead>
                        <tr>
                            <th class="with-checkbox" width="15px">
                                <div class="checkbox checkbox-css">
                                    <input class="power_all" type="checkbox" id="table_checkbox_all_9" />
                                    <label for="table_checkbox_all_9">&nbsp;</label>
                                </div>
                            </th>
                            <th class="text-nowrap">名称</th>
                            <th class="text-nowrap">可见状态</th>
                            <th class="text-nowrap">操作</th>
                        </tr>
                        </thead>
                        <tbody>
                        <g:each in="${power}" var="p" status="i">
                            <tr>
                                <td class="with-checkbox">
                                    <div class="checkbox checkbox-css">
                                        <input type="checkbox" class="power_single" name="checkbox-select" value="${p.uuid}" id="table_checkbox_${p.uuid}" data-user="${p.uuid}" />
                                        <label for="table_checkbox_${p.uuid}">&nbsp;</label>
                                    </div>
                                </td>
                                <td>${p.name}</td>
                                <td>${p.visible == "1"?"可见":"隐藏"}</td>
                                <td>
                                    <input type="hidden" value="${p.uuid}" />
                                    <a href="javascript:void(0);" class="editPower btn btn-inverse btn-xs m-r-5 btn-enabled">编辑</a>
                                    <g:link url="${createLink(uri: '/')}ctrl/power/button/${courtroom.id}/${p.uuid}" class="btn btn-inverse btn-xs m-r-5 btn-enabled">按钮</g:link>
                                </td>
                            </tr>
                        </g:each>
                        </tbody>
                    </table>
                </div>
                <!-- end tab-pane -->
                <!-- begin tab-pane 红外控制  irctrl-->
                <div class="tab-pane fade" id="nav-tab-10">
                    <div class="table-btn-row m-b-15">
                        <sec:ifAnyGranted roles='ROLE_SUPER,ROLE_ADMIN,ROLE_MONITOR'>
                            <g:link url="${createLink(uri: '/')}ctrl/irctrl/add/${courtroom.id}" class="btn btn-sm btn-inverse btn-add"><i class="fa fa-plus m-r-5"></i> 添加红外控制</g:link>
                            <a href="javascript:void(0);" id="delIrctrl" class="btn btn-sm btn-inverse btn-del"><i class="fa fa-times m-r-5"></i> 删除所选红外控制</a>
                        </sec:ifAnyGranted>
                    </div>
                    <table class="table table-striped table-bordered display">
                        <thead>
                        <tr>
                            <th class="with-checkbox" width="15px">
                                <div class="checkbox checkbox-css">
                                    <input class="irctrl_all" type="checkbox" id="table_checkbox_all_10" />
                                    <label for="table_checkbox_all_10">&nbsp;</label>
                                </div>
                            </th>
                            <th class="text-nowrap">名称</th>
                            <th class="text-nowrap">可见状态</th>
                            <th class="text-nowrap">操作</th>
                        </tr>
                        </thead>
                        <tbody>
                        <g:each in="${irctrl}" var="ir" status="i">
                            <tr>
                                <td class="with-checkbox">
                                    <div class="checkbox checkbox-css">
                                        <input type="checkbox" class="irctrl_single" name="checkbox-select" value="${ir.uuid}" id="table_checkbox_${ir.uuid}" data-user="${ir.uuid}" />
                                        <label for="table_checkbox_${ir.uuid}">&nbsp;</label>
                                    </div>
                                </td>
                                <td>${ir.name}</td>
                                <td>${ir.visible == "1"?"可见":"隐藏"}</td>
                                <td>
                                    <input type="hidden" value="${ir.uuid}" />
                                    <a href="#" class="editIrctrl btn btn-inverse btn-xs m-r-5 btn-enabled">编辑</a>
                                    <g:link url="${createLink(uri: '/')}ctrl/irctrl/button/${courtroom.id}/${ir.uuid}" class="btn btn-inverse btn-xs m-r-5 btn-enabled">按钮</g:link>
                                </td>
                            </tr>
                        </g:each>
                        </tbody>
                    </table>
                </div>
                <!-- end tab-pane -->
                <!-- begin tab-pane 摄像头控制-->
                <div class="tab-pane fade" id="nav-tab-11">
                    <div class="table-btn-row m-b-15">
                        <sec:ifAnyGranted roles='ROLE_SUPER,ROLE_ADMIN,ROLE_MONITOR'>
                            <g:link url="${createLink(uri: '/')}ctrl/camera/add/${courtroom.id}" class="btn btn-sm btn-inverse btn-add"><i class="fa fa-plus m-r-5"></i> 添加摄像头控制 </g:link>
                            <a href="javascript:void(0);" id="delCamera" class="btn btn-sm btn-inverse btn-del"><i class="fa fa-times m-r-5"></i> 删除所选摄像头控制 </a>
                            <g:link url="${createLink(uri: '/')}ctrl/camera/buttons/${courtroom.id}" class="btn btn-sm btn-inverse btn-add"><i class="fa fa-plus m-r-5"></i> 控制指令修改 </g:link>
                            <g:link url="${createLink(uri: '/')}ctrl/camera/presets/${courtroom.id}" class="btn btn-sm btn-inverse btn-del"><i class="fa fa-edit m-r-5"></i> 预置位修改 </g:link>
                        </sec:ifAnyGranted>
                    </div>
                    <table class="table table-striped table-bordered display">
                        <thead>
                        <tr>
                            <th class="with-checkbox" width="15px">
                                <div class="checkbox checkbox-css">
                                    <input type="checkbox" class="camera_all" id="table_checkbox_all_11" />
                                    <label for="table_checkbox_all_11">&nbsp;</label>
                                </div>
                            </th>
                            <th class="text-nowrap">名称</th>
                            <th class="text-nowrap">控制指令</th>
                            <th class="text-nowrap">可见状态</th>
                            <th class="text-nowrap">操作</th>
                        </tr>
                        </thead>
                        <tbody>
                        <g:each in="${camera?.position}" var="cp" status="i">
                            <tr>
                                <td class="with-checkbox">
                                    <div class="checkbox checkbox-css">
                                        <input type="checkbox" class="camera_single" name="checkbox-select" value="${cp.uuid}" id="table_checkbox_${cp.uuid}" data-user="${cp.uuid}" />
                                        <label for="table_checkbox_${cp.uuid}">&nbsp;</label>
                                    </div>
                                </td>
                                <td>${cp.name}</td>
                                <td>${cp.codeDown}</td>
                                <td>${cp.visible == "1"?"可见":"隐藏"}</td>
                                <td>
                                    <input type="hidden" value="${cp.uuid}" />
                                    <a href="javascript:void(0)" class="editCamera btn btn-inverse btn-xs m-r-5 btn-enabled">编辑</a>
                                </td>
                            </tr>
                        </g:each>
                        </tbody>
                    </table>
                </div>
                <!-- end tab-pane -->
                <!-- begin tab-pane 新电源控制-->
                <div class="tab-pane fade" id="nav-tab-12">
                    <div class="table-btn-row m-b-15">
                        <sec:ifAnyGranted roles='ROLE_SUPER,ROLE_ADMIN,ROLE_MONITOR'>
                            <g:link url="${createLink(uri: '/')}ctrl/powerNew/add/${courtroom.id}" class="btn btn-sm btn-inverse btn-add"><i class="fa fa-plus m-r-5"></i> 添加电源控制</g:link>
                            <a href="javascript:void(0);" id="delPowerNew" class="btn btn-sm btn-inverse btn-del"><i class="fa fa-times m-r-5"></i> 删除所选电源控制</a>
                        </sec:ifAnyGranted>
                    </div>
                    <table class="table table-striped table-bordered display">
                        <thead>
                        <tr>
                            <th class="with-checkbox" width="15px">
                                <div class="checkbox checkbox-css">
                                    <input class="powerNew_all" type="checkbox" id="table_checkbox_all_12" />
                                    <label for="table_checkbox_all_12">&nbsp;</label>
                                </div>
                            </th>
                            <th class="text-nowrap">设备IP</th>
                            <th class="text-nowrap">可见状态</th>
                            <th class="text-nowrap">操作</th>
                        </tr>
                        </thead>
                        <tbody>
                        <g:each in="${powerNew}" var="p" status="i">
                            <tr>
                                <td class="with-checkbox">
                                    <div class="checkbox checkbox-css">
                                        <input type="checkbox" class="powerNew_single" name="checkbox-select" value="${p.uuid}" id="table_checkbox_${p.uuid}" data-user="${p.uuid}" />
                                        <label for="table_checkbox_${p.uuid}">&nbsp;</label>
                                    </div>
                                </td>
                                <td>${p.name}</td>
                                <td>${p.visible == "1"?"可见":"隐藏"}</td>
                                <td>
                                    <input type="hidden" value="${p.uuid}" />
                                    <a href="javascript:void(0);" class="editPowerNew btn btn-inverse btn-xs m-r-5 btn-enabled">编辑</a>
                                    <g:link url="${createLink(uri: '/')}ctrl/powerNew/button/${courtroom.id}/${p.uuid}" class="btn btn-inverse btn-xs m-r-5 btn-enabled">按钮</g:link>
                                </td>
                            </tr>
                        </g:each>
                        </tbody>
                    </table>
                </div>
                <!-- end tab-pane -->
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
<asset:javascript src="jquery/jquery-migrate-1.1.0.min.js"/>
<asset:javascript src="parsley/dist/parsley.js"/>
<asset:javascript src="bootstrap-sweetalert/sweetalert.js"/>
<asset:javascript src="bootstrap-daterangepicker/moment.min.js"/>
<asset:javascript src="bootstrap-select/bootstrap-select.min.js"/>
<asset:javascript src="bootstrap-eonasdan-datetimepicker/build/js/bootstrap-datetimepicker.min.js"/>
<asset:javascript src="jquery-tag-it/js/tag-it.min.js"/>
<asset:javascript src="select2/dist/js/select2.min.js"/>
<asset:javascript src="/ctrl/ctrl.js"/>
<script>
    $(document).ready(function () {
    });
</script>
<!-- ================== END PAGE LEVEL JS ================== -->
</body>
</html>
