/**
 * Created by sakuri on 2019/8/13.
 * 2021.04.19 >>> 增加远程提讯开关 daniel
 */
;-function (window) {
    'use strict';
    var ready = {};
    var option = {};
    var init = function () {
        init_ready();//加载预设变量
        init_event();//初始化页面事件
    };

    function init_ready() {//初始化预设值
    }

    function init_event() {//初始化页面事件
        core.render_table();
        core.delCheckedBtn();
        core.video();
    }

    //内部核心属性
    var core = {
        /**
         * 查看该法庭视频
         */
        video:function(){

        },
        /**
         * 删除所选择的法庭
         */
        delCheckedBtn:function(){
            $("#checkedBtn").bind('click', function () {
                swal({
                    title:"确认删除?",
                    type:"warning",
                    showCancelButton:true,
                    cancelButtonText:'取 消',
                    cancelButtonColor:'#b9b9b9',
                    showConfirmButton:true,
                    confirmButtonText:'确 认',
                    confirmButtonColor:"#dd6b55",
                    closeOnConfirm:false,
                    closeOnCancel:true
                },function(){
                    var checkedBox = document.getElementsByName("checkbox-select");
                    var roomIds = "";
                    for (var i=0; i<checkedBox.length; i++){
                        if (checkedBox[i].checked){
                            if (roomIds==""){
                                roomIds = checkedBox[i].value;
                            }else{
                                roomIds = roomIds + "," + checkedBox[i].value;
                            }
                        }
                    }
                    $.get(
                        contextPath + 'courtroom/del',
                        {
                            roomIds:roomIds
                        },
                        function (result) {
                            if (result.code==0){
                                swal({
                                    title: '删除成功!',
                                    type: 'success',
                                    confirmButtonText:'确 认'
                                },function () {
                                    window.location.href = contextPath + 'courtroom/list';
                                });
                            }else if (result.code===410){
                                swal({
                                    title: '请选择数据',
                                    type: 'error',
                                    confirmButtonText:'确 认'
                                })
                            }else if (result.code==1){ // 法庭应该有排期
                                swal({
                                    title: '部分法庭正在使用已自动为您拒绝删除...',
                                    type: 'error',
                                    confirmButtonText:'确 认'
                                },function () {
                                    window.location.href = contextPath + 'courtroom/list';
                                })
                            }
                        },'json'
                    )
                })
            })
        },
        handleSelect: function () {
            $('#table_checkbox_all').bind('click', function (e) {
                var checkbox = $("input[type='checkbox']");
                if ($(this).is(':checked')) {
                    checkbox.prop("checked", true);
                } else {
                    checkbox.prop("checked", false);
                }
            });
            //每一次的小checkbox点击事件
            $(".ck").bind('click', function () {
                //获取所有的小的checkbox
                var checkboxs = $(".ck");
                for (var i=0; i<checkboxs.length; i++){
                    if (!checkboxs[i].checked){
                        $('#table_checkbox_all').prop("checked", false);
                        return;
                    }
                }
                $('#table_checkbox_all').prop("checked", true);
            })
        },
        render_table: function () {
            $('#data-table').DataTable({
                language: {
                    'sProcessing': '查询设备在线状态中,请稍候...',
                    'sLengthMenu': '显示 _MENU_ 项结果',
                    'sZeroRecords': '没有匹配结果',
                    'sInfo': '显示第 _START_ 至 _END_ 项结果，共 _TOTAL_ 项',
                    'sInfoEmpty': '显示第 0 至 0 项结果，共 0 项',
                    'sInfoFiltered': '(由 _MAX_ 项结果过滤)',
                    'sInfoPostFix': '',
                    'sSearch': '搜索：',
                    'sUrl': '',
                    'sEmptyTable': '表中数据为空',
                    'sLoadingRecords': '载入中...',
                    'sInfoThousands': ',',
                    'oPaginate': {
                        'sFirst': '首页',
                        'sPrevious': '上页',
                        'sNext': '下页',
                        'sLast': '末页'
                    },
                    'oAria': {
                        'sSortAscending': ': 以升序排列此列',
                        'sSortDescending': ': 以降序排列此列'
                    }
                },
                serverSide: true,
                ordering: false,
                bProcessing: true,
                ajax: {
                    url: contextPath + 'courtroom/list',
                    type: "POST"
                },
                aoColumns: [//初始化要显示的列
                    {
                        "mDataProp": "id",
                        "sClass": "table-select",
                        "mRender": function (data, type, full) {
                            return '<td class="with-checkbox">' +
                                '<div class="checkbox checkbox-css">' +
                                '<input class="ck" type="checkbox" name="checkbox-select" value="'+ data +'" id="table_checkbox_' + data + '" data-user="' + data + '" />' +
                                '<label for="table_checkbox_' + data + '">&nbsp;</label>' +
                                '</div>'
                        }
                    },
                    {
                        "mDataProp": "name",
                        "mRender": function (data, type, full) {
                            return data
                        }
                    },
                    {
                        "mDataProp": "liveIp",
                        "mRender": function (data, type, full) {
                            return data
                        }
                    },
                    {
                        "mDataProp": "livePort",
                        "mRender": function (data, type, full) {
                            return data
                        }
                    },
                    {
                        "mDataProp": "deviceIp",
                        "mRender": function (data, type, full) {
                            return data
                        }
                    },
                    {
                        "mDataProp": "isConnect",
                        "mRender": function (data, type, full) {
                            return data
                        }
                    },
                    {
                        "mDataProp": "storeIp",
                        "mRender": function (data, type, full) {
                            return data
                        }
                    },
                    {
                        "mDataProp": "isCalled",
                        "mRender": function (data, type, full) {
                            return data == 0 ? "禁止" : "允许"
                        }
                    },
                    {
                        "mDataProp": "status",
                        "mRender": function (data, type, full) {
                            return data
                        }
                    },
                    {
                        "mDataProp": "id",//获取列数据，跟服务器返回字段一致
                        "sClass": "center",//显示样式
                        "mRender": function (data, type, full) {//返回自定义的样式
                            var html =  '<div class="table-text">';
                            html +=  '<a href="'+contextPath+'courtroom/edit/' + data + '" class="btn btn-inverse btn-xs m-r-5 btn-enabled">编辑</a>';
                            html +=  '<a href="'+contextPath+'ctrl/index/' + data + '" class="btn btn-inverse btn-xs m-r-5 btn-enabled">控制</a>';
                            // html +=  '<a href="'+contextPath+'plan/courtroomlist/' + data + '" class="btn btn-inverse btn-xs m-r-5 btn-enabled">排期</a>';
                            html +=  '<a href="'+contextPath+'video/show/' + data + '" class="btn btn-inverse btn-xs m-r-5 btn-enabled">视频</a>';
                            html +=  '<a href="'+contextPath+'ctrl/show/' + data + '" class="btn btn-inverse btn-xs m-r-5 btn-enabled">设备控制台</a>';
                            html +=  '</div>';
                            return html
                        }
                    }
                ],
                fnDrawCallback: function () {
                    core.handleSelect();
                }
            });
        }
    };
    //对外公开的方法
    var page = {};
    init();
    window.p = page;
}(window);
